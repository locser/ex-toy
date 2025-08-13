// 1. CACHE STRATEGY - L1 (Local) + L2 (Redis) Cache
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        RedisCacheManager.Builder builder = RedisCacheManager
            .RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory())
            .cacheDefaults(cacheConfiguration());
        
        return builder.build();
    }
    
    @Bean
    public CaffeineCache campaignLocalCache() {
        return new CaffeineCache("campaigns", 
            Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .recordStats()
                .build());
    }
    
    @Bean
    public CaffeineCache giftLocalCache() {
        return new CaffeineCache("gifts", 
            Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .recordStats()
                .build());
    }
}

// 2. CAMPAIGN SERVICE - Cached với multi-level
@Service
public class CampaignService {
    
    private final LoadingCache<String, Campaign> campaignCache;
    private final RedisTemplate<String, String> redisTemplate;
    private final CampaignRepository campaignRepository;
    
    public CampaignService() {
        this.campaignCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build(this::loadCampaign);
    }
    
    public Campaign getCampaign(String campaignId) {
        try {
            // L1 Cache - Local Caffeine
            return campaignCache.get(campaignId);
        } catch (Exception e) {
            throw new CampaignNotFoundException("Campaign not found: " + campaignId);
        }
    }
    
    private Campaign loadCampaign(String campaignId) {
        // L2 Cache - Redis
        String cached = redisTemplate.opsForValue().get("campaign:" + campaignId);
        if (cached != null) {
            return JsonUtils.fromJson(cached, Campaign.class);
        }
        
        // L3 - Database
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new CampaignNotFoundException("Campaign not found"));
            
        // Cache in Redis for 5 minutes
        redisTemplate.opsForValue().set("campaign:" + campaignId, 
            JsonUtils.toJson(campaign), Duration.ofMinutes(5));
            
        return campaign;
    }
    
    public boolean isCampaignActive(String campaignId) {
        Campaign campaign = getCampaign(campaignId);
        long now = System.currentTimeMillis();
        return campaign.getStartTime() <= now && campaign.getEndTime() >= now;
    }
}

// 3. GIFT DISTRIBUTION SERVICE - Atomic Operations
@Service
public class GiftService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final LoadingCache<String, AtomicLong> giftCounterCache;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    // Script Lua để atomic check + decrement
    private final RedisScript<Long> claimGiftScript = RedisScript.of(
        "local key = KEYS[1] " +
        "local current = redis.call('GET', key) " +
        "if current and tonumber(current) > 0 then " +
        "  return redis.call('DECR', key) " +
        "else " +
        "  return -1 " +
        "end", Long.class);
    
    public GiftService() {
        this.giftCounterCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build(this::loadGiftCount);
    }
    
    @Async("giftExecutor")
    public CompletableFuture<GiftClaimResult> claimGift(String campaignId, String userId) {
        String giftKey = "gift_count:" + campaignId;
        
        // Atomic decrement with Lua script
        Long remaining = redisTemplate.execute(claimGiftScript, 
            Collections.singletonList(giftKey));
            
        if (remaining == -1) {
            return CompletableFuture.completedFuture(
                GiftClaimResult.failed("No gifts available"));
        }
        
        // Generate gift
        Gift gift = generateGift(campaignId, userId);
        
        // Async send to Kafka (fire and forget)
        sendParticipantEvent(campaignId, userId, gift);
        
        return CompletableFuture.completedFuture(
            GiftClaimResult.success(gift, remaining));
    }
    
    private Gift generateGift(String campaignId, String userId) {
        return Gift.builder()
            .id(generateGiftId())
            .campaignId(campaignId)
            .userId(userId)
            .giftCode(generateGiftCode())
            .claimedAt(Instant.now())
            .build();
    }
    
    private void sendParticipantEvent(String campaignId, String userId, Gift gift) {
        ParticipantEvent event = ParticipantEvent.builder()
            .campaignId(campaignId)
            .userId(userId)
            .gift(gift)
            .timestamp(Instant.now())
            .build();
            
        // Non-blocking send
        kafkaTemplate.send("giveaway-participants", userId, event);
    }
    
    // Fast ID generation
    private String generateGiftId() {
        return System.currentTimeMillis() + "_" + ThreadLocalRandom.current().nextInt(10000);
    }
    
    private String generateGiftCode() {
        return "GIFT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}

// 4. MAIN CONTROLLER - Optimized for Speed
@RestController
@RequestMapping("/api/v1/giveaway")
@Validated
public class GiveawayController {
    
    private final CampaignService campaignService;
    private final GiftService giftService;
    private final RateLimitService rateLimitService;
    
    @PostMapping("/claim")
    @Timed(name = "giveaway.claim.time")
    public ResponseEntity<GiftResponse> claimGift(
            @RequestBody @Valid ClaimRequest request,
            HttpServletRequest httpRequest) {
        
        String userId = request.getUserId();
        String campaignId = request.getCampaignId();
        String clientIp = getClientIp(httpRequest);
        
        // 1. Rate limiting - Redis based sliding window
        if (!rateLimitService.allowRequest(clientIp, userId)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(GiftResponse.rateLimited());
        }
        
        // 2. Check campaign status (cached)
        if (!campaignService.isCampaignActive(campaignId)) {
            return ResponseEntity.badRequest()
                .body(GiftResponse.campaignInactive());
        }
        
        // 3. Claim gift (async processing)
        try {
            CompletableFuture<GiftClaimResult> future = giftService.claimGift(campaignId, userId);
            GiftClaimResult result = future.get(100, TimeUnit.MILLISECONDS); // Fast timeout
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(GiftResponse.success(result.getGift()));
            } else {
                return ResponseEntity.badRequest()
                    .body(GiftResponse.failed(result.getMessage()));
            }
        } catch (TimeoutException e) {
            // Return immediately, processing continues in background
            return ResponseEntity.accepted()
                .body(GiftResponse.processing());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GiftResponse.error());
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

// 5. RATE LIMITING SERVICE - Sliding Window
@Service
public class RateLimitService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final int maxRequestsPerMinute = 10; // Per user per minute
    private final int maxRequestsPerIpPerMinute = 100; // Per IP per minute
    
    // Lua script for sliding window rate limiting
    private final RedisScript<Long> rateLimitScript = RedisScript.of(
        "local key = KEYS[1] " +
        "local window = tonumber(ARGV[1]) " +
        "local limit = tonumber(ARGV[2]) " +
        "local now = tonumber(ARGV[3]) " +
        "local clearBefore = now - window " +
        "redis.call('ZREMRANGEBYSCORE', key, 0, clearBefore) " +
        "local current = redis.call('ZCARD', key) " +
        "if current < limit then " +
        "  redis.call('ZADD', key, now, now) " +
        "  redis.call('EXPIRE', key, window) " +
        "  return limit - current - 1 " +
        "else " +
        "  return -1 " +
        "end", Long.class);
    
    public boolean allowRequest(String clientIp, String userId) {
        long now = System.currentTimeMillis();
        long window = 60000; // 1 minute
        
        // Check user rate limit
        String userKey = "rate_limit:user:" + userId;
        Long userRemaining = redisTemplate.execute(rateLimitScript,
            Collections.singletonList(userKey),
            String.valueOf(window),
            String.valueOf(maxRequestsPerMinute),
            String.valueOf(now));
            
        if (userRemaining == -1) {
            return false;
        }
        
        // Check IP rate limit
        String ipKey = "rate_limit:ip:" + clientIp;
        Long ipRemaining = redisTemplate.execute(rateLimitScript,
            Collections.singletonList(ipKey),
            String.valueOf(window),
            String.valueOf(maxRequestsPerIpPerMinute),
            String.valueOf(now));
            
        return ipRemaining != -1;
    }
}

// 6. ASYNC CONFIGURATION
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean("giftExecutor")
    public Executor giftExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(200);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("Gift-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
    
    @Bean("kafkaExecutor")
    public Executor kafkaExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(2000);
        executor.setThreadNamePrefix("Kafka-");
        executor.initialize();
        return executor;
    }
}

// 7. KAFKA PRODUCER OPTIMIZATION
@Configuration
public class KafkaProducerConfig {
    
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Performance optimizations
        props.put(ProducerConfig.ACKS_CONFIG, "1"); // Leader acknowledge only
        props.put(ProducerConfig.RETRIES_CONFIG, 0); // No retries for speed
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768); // 32KB batches
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5); // Wait 5ms to batch
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 67108864); // 64MB buffer
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // Fast compression
        
        return new DefaultKafkaProducerFactory<>(props);
    }
    
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(producerFactory());
        // Async send without callback for maximum speed
        return template;
    }
}

// 8. DATA MODELS
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Gift {
    private String id;
    private String campaignId;
    private String userId;
    private String giftCode;
    private Instant claimedAt;
}

@Data
@Builder
public class GiftClaimResult {
    private boolean success;
    private String message;
    private Gift gift;
    private Long remainingGifts;
    
    public static GiftClaimResult success(Gift gift, Long remaining) {
        return GiftClaimResult.builder()
            .success(true)
            .gift(gift)
            .remainingGifts(remaining)
            .build();
    }
    
    public static GiftClaimResult failed(String message) {
        return GiftClaimResult.builder()
            .success(false)
            .message(message)
            .build();
    }
}

@Data
@Validated
public class ClaimRequest {
    @NotBlank
    private String userId;
    
    @NotBlank
    private String campaignId;
}

@Data
@Builder
public class GiftResponse {
    private String status;
    private String message;
    private Gift gift;
    private Long remainingGifts;
    
    public static GiftResponse success(Gift gift) {
        return GiftResponse.builder()
            .status("SUCCESS")
            .gift(gift)
            .build();
    }
    
    public static GiftResponse failed(String message) {
        return GiftResponse.builder()
            .status("FAILED")
            .message(message)
            .build();
    }
    
    public static GiftResponse rateLimited() {
        return GiftResponse.builder()
            .status("RATE_LIMITED")
            .message("Too many requests")
            .build();
    }
    
    public static GiftResponse campaignInactive() {
        return GiftResponse.builder()
            .status("CAMPAIGN_INACTIVE")
            .message("Campaign is not active")
            .build();
    }
    
    public static GiftResponse processing() {
        return GiftResponse.builder()
            .status("PROCESSING")
            .message("Gift is being processed")
            .build();
    }
    
    public static GiftResponse error() {
        return GiftResponse.builder()
            .status("ERROR")
            .message("Internal server error")
            .build();
    }
}