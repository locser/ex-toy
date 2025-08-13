// ADVANCED OPTIMIZATIONS

// 1. CUSTOM FAST JSON SERIALIZER
@Component
public class FastJsonSerializer {
    
    private static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
        .registerModule(new JavaTimeModule());
    
    // Thread-safe và fast serialization
    public String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }
    
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialization failed", e);
        }
    }
}

// 2. CIRCUIT BREAKER cho External Dependencies
@Component
public class CircuitBreakerService {
    
    private final CircuitBreaker redisCircuitBreaker;
    private final CircuitBreaker kafkaCircuitBreaker;
    
    public CircuitBreakerService() {
        this.redisCircuitBreaker = CircuitBreaker.ofDefaults("redis");
        this.kafkaCircuitBreaker = CircuitBreaker.ofDefaults("kafka");
        
        // Configure Redis circuit breaker
        redisCircuitBreaker.getEventPublisher()
            .onStateTransition(event -> 
                log.warn("Redis Circuit Breaker state transition: {}", event));
    }
    
    public <T> T executeRedisOperation(Supplier<T> operation, T fallbackValue) {
        return redisCircuitBreaker.executeSupplier(operation);
    }
    
    public void executeKafkaOperation(Runnable operation) {
        kafkaCircuitBreaker.executeRunnable(operation);
    }
}

// 3. GIFT CODE GENERATOR - Ultra Fast
@Component
public class FastGiftCodeGenerator {
    
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();
    private static final AtomicLong counter = new AtomicLong(0);
    
    public String generateFastCode() {
        // Combine timestamp + counter + random for uniqueness
        long timestamp = System.currentTimeMillis();
        long count = counter.incrementAndGet();
        
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        long seed = timestamp + count;
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = (int) (seed % ALPHABET.length());
            code.append(ALPHABET.charAt(index));
            seed = seed / ALPHABET.length() + random.nextInt(100);
        }
        
        return code.toString();
    }
    
    // Pre-generate codes pool for ultra-fast access
    private final BlockingQueue<String> preGeneratedCodes = new LinkedBlockingQueue<>();
    
    @PostConstruct
    public void initializeCodePool() {
        // Pre-generate 10000 codes
        for (int i = 0; i < 10000; i++) {
            preGeneratedCodes.offer(generateFastCode());
        }
    }
    
    @Scheduled(fixedDelay = 1000)
    public void refillCodePool() {
        while (preGeneratedCodes.size() < 5000) {
            preGeneratedCodes.offer(generateFastCode());
        }
    }
    
    public String getPreGeneratedCode() {
        String code = preGeneratedCodes.poll();
        return code != null ? code : generateFastCode();
    }
}

// 4. MEMORY-EFFICIENT STATISTICS COLLECTOR
@Component
public class GiveawayStatsCollector {
    
    // Ring buffer để track metrics hiệu quả
    private final RingBuffer<RequestMetric> metricsBuffer;
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    
    // Sliding window cho rate calculation
    private final long[] requestTimestamps = new long[10000];
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    
    public GiveawayStatsCollector() {
        this.metricsBuffer = RingBuffer.createMultiProducer(
            RequestMetric::new, 8192); // 8K buffer size
    }
    
    public void recordRequest(boolean success, long responseTimeMs) {
        totalRequests.incrementAndGet();
        if (success) {
            successfulRequests.incrementAndGet();
        } else {
            failedRequests.incrementAndGet();
        }
        
        // Record timestamp for rate calculation
        long now = System.currentTimeMillis();
        int index = currentIndex.getAndIncrement() % requestTimestamps.length;
        requestTimestamps[index] = now;
        
        // Record detailed metric
        long sequence = metricsBuffer.next();
        try {
            RequestMetric metric = metricsBuffer.get(sequence);
            metric.timestamp = now;
            metric.success = success;
            metric.responseTimeMs = responseTimeMs;
        } finally {
            metricsBuffer.publish(sequence);
        }
    }
    
    public double getCurrentRPS() {
        long now = System.currentTimeMillis();
        long oneSecondAgo = now - 1000;
        
        return Arrays.stream(requestTimestamps)
            .filter(timestamp -> timestamp > oneSecondAgo)
            .count();
    }
    
    public GiveawayStats getStats() {
        long total = totalRequests.get();
        long successful = successfulRequests.get();
        long failed = failedRequests.get();
        
        return GiveawayStats.builder()
            .totalRequests(total)
            .successfulRequests(successful)
            .failedRequests(failed)
            .successRate(total > 0 ? (double) successful / total : 0.0)
            .currentRPS(getCurrentRPS())
            .build();
    }
    
    @Data
    @Builder
    public static class GiveawayStats {
        private long totalRequests;
        private long successfulRequests;
        private long failedRequests;
        private double successRate;
        private double currentRPS;
    }
    
    public static class RequestMetric {
        public long timestamp;
        public boolean success;
        public long responseTimeMs;
    }
}

// 5. OPTIMIZED DUPLICATE CHECK
@Component
public class DuplicateChecker {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final BloomFilter<String> bloomFilter;
    
    public DuplicateChecker(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        // Bloom filter cho fast pre-check
        this.bloomFilter = BloomFilter.create(
            Funnels.stringFunnel(Charset.defaultCharset()),
            1000000, // Expected insertions
            0.01     // False positive probability
        );
    }
    
    public boolean isDuplicate(String campaignId, String userId) {
        String key = campaignId + ":" + userId;
        
        // Fast pre-check với Bloom filter
        if (!bloomFilter.mightContain(key)) {
            return false; // Definitely not duplicate
        }
        
        // Redis check nếu Bloom filter says might exist
        Boolean exists = redisTemplate.hasKey("participant:" + key);
        return Boolean.TRUE.equals(exists);
    }
    
    public void markAsParticipant(String campaignId, String userId) {
        String key = campaignId + ":" + userId;
        
        // Add to Bloom filter
        bloomFilter.put(key);
        
        // Add to Redis with expiration
        redisTemplate.opsForValue().set(
            "participant:" + key, 
            "1", 
            Duration.ofHours(24)
        );
    }
}

// 6. WEBHOOK NOTIFIER cho Real-time Updates
@Component
public class WebhookNotifier {
    
    private final WebClient webClient;
    private final BlockingQueue<WebhookEvent> eventQueue = new LinkedBlockingQueue<>();
    
    public WebhookNotifier() {
        this.webClient = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                    .responseTimeout(Duration.ofMillis(2000))
                    .keepAlive(true)
            ))
            .build();
            
        // Start background processor
        startEventProcessor();
    }
    
    @Async("webhookExecutor")
    public void notifyGiftClaimed(String campaignId, String userId, String giftCode) {
        WebhookEvent event = WebhookEvent.builder()
            .type("GIFT_CLAIMED")
            .campaignId(campaignId)
            .userId(userId)
            .giftCode(giftCode)
            .timestamp(Instant.now())
            .build();
            
        eventQueue.offer(event);
    }
    
    private void startEventProcessor() {
        Thread processor = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    WebhookEvent event = eventQueue.take();
                    sendWebhook(event);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error processing webhook event", e);
                }
            }
        });
        processor.setDaemon(true);
        processor.start();
    }
    
    private void sendWebhook(WebhookEvent event) {
        try {
            webClient.post()
                .uri("https://webhook-endpoint.com/giveaway")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(event)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(2))
                .subscribe(
                    response -> log.debug("Webhook sent successfully"),
                    error -> log.warn("Webhook failed: {}", error.getMessage())
                );
        } catch (Exception e) {
            log.error("Failed to send webhook", e);
        }
    }
    
    @Data
    @Builder
    public static class WebhookEvent {
        private String type;
        private String campaignId;
        private String userId;
        private String giftCode;
        private Instant timestamp;
    }
}

// 7. HEALTH CHECK ENDPOINT - Optimized
@RestController
@RequestMapping("/health")
public class HealthController {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final DataSource dataSource;
    
    @GetMapping
    public ResponseEntity<HealthStatus> health() {
        HealthStatus status = HealthStatus.builder()
            .status("UP")
            .timestamp(Instant.now())
            .checks(performHealthChecks())
            .build();
            
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/ready")
    public ResponseEntity<String> ready() {
        // Fast readiness check
        try {
            redisTemplate.hasKey("health-check");
            return ResponseEntity.ok("READY");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("NOT_READY");
        }
    }
    
    private Map<String, Boolean> performHealthChecks() {
        Map<String, Boolean> checks = new HashMap<>();
        
        // Redis check
        checks.put("redis", checkRedis());
        
        // Kafka check
        checks.put("kafka", checkKafka());
        
        // Database check
        checks.put("database", checkDatabase());
        
        return checks;
    }
    
    private boolean checkRedis() {
        try {
            redisTemplate.opsForValue().set("health-check", "ok", Duration.ofSeconds(10));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean checkKafka() {
        try {
            // Simple producer check
            kafkaTemplate.send("health-check", "ping").get(100, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean checkDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(1);
        } catch (Exception e) {
            return false;
        }
    }
    
    @Data
    @Builder
    public static class HealthStatus {
        private String status;
        private Instant timestamp;
        private Map<String, Boolean> checks;
    }
}