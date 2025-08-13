# Spring Boot Threading & Sentinel - Interview Guide

## Tổng Quan
Tài liệu này tổng hợp các câu hỏi phỏng vấn và câu trả lời chi tiết về Threading, ThreadPool, Virtual Threads và Sentinel trong Spring Boot. Được thiết kế cho cả interviewer và candidate.

---

## PHẦN 1: THREAD FUNDAMENTALS

### Q1: Sự khác biệt giữa Thread và ThreadPool trong Java?

**Câu trả lời:**

**Thread:**
- Là đơn vị thực thi nhỏ nhất trong JVM
- Mỗi thread có stack riêng (thường 1MB)
- Tạo/hủy thread có cost cao (system calls)
- Không giới hạn số lượng → có thể gây OutOfMemoryError

**ThreadPool:**
- Tập hợp các thread được tạo sẵn và tái sử dụng
- Giới hạn số lượng thread → kiểm soát resource
- Sử dụng queue để quản lý tasks
- Giảm overhead tạo/hủy threads

```java
// Thread truyền thống - BAD
for (int i = 0; i < 1000; i++) {
    new Thread(() -> {
        // Do work
    }).start(); // Tạo 1000 threads!
}

// ThreadPool - GOOD
ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < 1000; i++) {
    executor.submit(() -> {
        // Do work
    }); // Chỉ sử dụng 10 threads
}
```

**Tại sao cần ThreadPool:**
- **Resource Management**: Kiểm soát memory và CPU usage
- **Performance**: Tránh overhead tạo/hủy threads
- **Scalability**: Handle nhiều requests với resource hạn chế
- **Stability**: Tránh thread starvation

---

### Q2: Spring Boot xử lý HTTP request như thế nào?

**Câu trả lời:**

**Threading Model:**
1. **Tomcat Connector** nhận request
2. **Acceptor Thread** accept connection
3. **Worker Thread** (từ thread pool) xử lý request
4. Thread được trả về pool sau khi hoàn thành

```yaml
# Default Tomcat Configuration
server:
  tomcat:
    threads:
      max: 200          # Max worker threads
      min-spare: 10     # Minimum idle threads
    max-connections: 8192  # Max concurrent connections
    accept-count: 100     # Queue size when all threads busy
```

**Request Lifecycle:**
```
Client Request → Acceptor → Thread Pool → Controller → Service → Repository → Response
                    ↓
                Worker Thread (từ pool)
```

**Key Points:**
- **One Thread Per Request**: Mỗi request được xử lý bởi 1 thread
- **Blocking I/O**: Thread bị block khi chờ database/external calls
- **Thread Pool Exhaustion**: Khi tất cả threads busy → requests queued

---

## PHẦN 2: SPRING BOOT THREADING MODEL

### Q3: Tomcat Threading Model và Configuration

**Câu trả lời:**

**Tomcat NIO Connector:**
- Sử dụng **NIO (Non-blocking I/O)** cho network operations
- **Acceptor threads**: Accept connections (default: 1)
- **Worker threads**: Process requests (configurable)

**Key Parameters:**
```yaml
server:
  tomcat:
    threads:
      max: 200              # Maximum worker threads
      min-spare: 10         # Minimum idle threads
    max-connections: 8192   # Max concurrent connections
    accept-count: 100       # Backlog queue size
    connection-timeout: 20000  # Connection timeout (ms)
    max-http-post-size: 2MB    # Max POST size
```

**Threading Behavior:**
```java
// Simplified Tomcat model
while (server.isRunning()) {
    Socket connection = acceptor.accept();  // Acceptor thread
    
    if (workerPool.hasAvailableThread()) {
        workerPool.submit(() -> {
            processRequest(connection);     // Worker thread
        });
    } else {
        // Queue request or reject
        requestQueue.offer(connection);
    }
}
```

**Monitoring:**
```java
@Component
public class ThreadPoolMonitor {
    
    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) 
            ((TomcatWebServer) ((ServletWebServerApplicationContext) 
            event.getApplicationContext()).getWebServer())
            .getTomcat().getConnector().getProtocolHandler().getExecutor();
            
        log.info("Core Pool Size: {}", executor.getCorePoolSize());
        log.info("Max Pool Size: {}", executor.getMaximumPoolSize());
    }
}
```

---

### Q4: Xử lý Long-Running Operations

**Câu trả lời:**

**Problem:**
```java
@RestController
public class SlowController {
    
    @GetMapping("/slow")
    public String slowEndpoint() {
        // BAD: Block worker thread for 5 seconds
        Thread.sleep(5000);  // External API call
        return "Done";
    }
}
```

**Consequences:**
- Worker thread bị block 5 giây
- Với 200 max threads → chỉ handle được 40 requests/second
- Thread pool exhaustion → 503 Service Unavailable

**Solutions:**

**1. Async Processing:**
```java
@RestController
public class AsyncController {
    
    @Async
    @GetMapping("/async")
    public CompletableFuture<String> asyncEndpoint() {
        return CompletableFuture.supplyAsync(() -> {
            // Long running task in separate thread pool
            callExternalAPI();
            return "Done";
        });
    }
}

@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        return executor;
    }
}
```

**2. Reactive Programming:**
```java
@RestController
public class ReactiveController {
    
    @GetMapping("/reactive")
    public Mono<String> reactiveEndpoint() {
        return WebClient.create()
            .get()
            .uri("https://external-api.com")
            .retrieve()
            .bodyToMono(String.class)
            .timeout(Duration.ofSeconds(5));
    }
}
```

**3. Message Queue:**
```java
@RestController
public class QueueController {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @PostMapping("/queue")
    public ResponseEntity<String> queueTask(@RequestBody TaskRequest request) {
        // Immediate response, process async
        rabbitTemplate.convertAndSend("task.queue", request);
        return ResponseEntity.accepted().body("Task queued");
    }
}
```

---

## PHẦN 3: VIRTUAL THREADS (JAVA 21)

### Q5: Virtual Threads là gì và tại sao quan trọng?

**Câu trả lời:**

**Virtual Threads:**
- Lightweight threads được quản lý bởi JVM (không phải OS)
- Có thể tạo hàng triệu virtual threads
- Được mount/unmount lên platform threads khi cần
- Giải quyết vấn đề thread-per-request scaling

**So sánh:**
```java
// Platform Thread (Traditional)
- Stack size: ~1MB
- OS managed
- Expensive creation
- Limited by OS (few thousands)

// Virtual Thread (Java 21+)
- Stack size: ~few KB
- JVM managed  
- Cheap creation
- Millions possible
```

**Key Benefits:**
- **Scalability**: Handle millions of concurrent requests
- **Simplicity**: Vẫn sử dụng blocking code style
- **Resource Efficiency**: Ít memory hơn platform threads
- **Better Throughput**: Đặc biệt cho I/O intensive applications

**When to use:**
- ✅ I/O heavy applications (web services, database calls)
- ✅ High concurrency requirements
- ✅ Blocking operations (JDBC, HTTP clients)

**When NOT to use:**
- ❌ CPU intensive tasks
- ❌ Applications với ít concurrent operations
- ❌ Code sử dụng ThreadLocal extensively

---

### Q6: Enable Virtual Threads trong Spring Boot

**Câu trả lời:**

**Configuration:**
```yaml
# application.yml
spring:
  threads:
    virtual:
      enabled: true
```

**Programmatic Configuration:**
```java
@Configuration
public class VirtualThreadConfig {
    
    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
        return protocolHandler -> {
            protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        };
    }
    
    @Bean
    @Primary
    public AsyncTaskExecutor applicationTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}
```

**Testing Virtual Threads:**
```java
@Test
public void testVirtualThreads() {
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        List<Future<String>> futures = new ArrayList<>();
        
        // Submit 1 million tasks
        for (int i = 0; i < 1_000_000; i++) {
            futures.add(executor.submit(() -> {
                Thread.sleep(1000); // Simulate I/O
                return Thread.currentThread().toString();
            }));
        }
        
        // All tasks complete without memory issues
        futures.forEach(future -> {
            try {
                String result = future.get();
                assertTrue(result.contains("VirtualThread"));
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });
    }
}
```

---

## PHẦN 4: THREADING PROBLEMS

### Q7: Memory Leak trong Threading Context

**Câu trả lời:**

**1. ThreadLocal Leaks:**
```java
// BAD - Memory leak
public class LeakyService {
    private static final ThreadLocal<List<String>> threadLocalData = 
        ThreadLocal.withInitial(ArrayList::new);
    
    public void processData(String data) {
        threadLocalData.get().add(data);
        // ThreadLocal never cleared!
    }
}

// GOOD - Proper cleanup
public class SafeService {
    private static final ThreadLocal<List<String>> threadLocalData = 
        ThreadLocal.withInitial(ArrayList::new);
    
    public void processData(String data) {
        try {
            threadLocalData.get().add(data);
        } finally {
            threadLocalData.remove(); // Always cleanup
        }
    }
}
```

**2. Static Collections:**
```java
// BAD - Growing cache without bounds
@Service
public class ProblematicService {
    private static final Map<String, String> cache = new ConcurrentHashMap<>();
    
    @Async
    public void processData(String data) {
        cache.put(UUID.randomUUID().toString(), data); // Memory leak!
    }
}

// GOOD - Bounded cache
@Service
public class SafeService {
    private final Cache<String, String> cache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build();
}
```

---

### Q8: Deadlock Prevention

**Câu trả lời:**

**Lock Ordering Strategy:**
```java
@Service
public class SafeService {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    
    private void acquireLocksInOrder(Object first, Object second, Runnable task) {
        Object firstLock = System.identityHashCode(first) < System.identityHashCode(second) 
            ? first : second;
        Object secondLock = firstLock == first ? second : first;
        
        synchronized (firstLock) {
            synchronized (secondLock) {
                task.run();
            }
        }
    }
}
```

---

## PHẦN 5: SENTINEL - CIRCUIT BREAKER

### Q12: Sentinel Overview

**Câu trả lời:**

**Sentinel là gì:**
- Open-source circuit breaker library của Alibaba
- Cung cấp flow control, circuit breaking, system protection
- Real-time monitoring và dynamic rule configuration
- High-performance và lightweight

**Core Features:**
- **Flow Control**: QPS và thread count limiting
- **Circuit Breaking**: Fault tolerance protection
- **System Adaptive Protection**: CPU, memory, load protection
- **Real-time Monitoring**: Dashboard và metrics

---

### Q13: Sentinel Implementation

**Câu trả lời:**

**Basic Setup:**
```java
@Component
public class UserService {
    
    @SentinelResource(
        value = "getUserById",
        blockHandler = "handleBlock",
        fallback = "handleFallback"
    )
    public User getUserById(Long id) {
        return externalUserService.getUser(id);
    }
    
    public User handleBlock(Long id, BlockException ex) {
        return new User(id, "Blocked User");
    }
    
    public User handleFallback(Long id, Throwable ex) {
        return new User(id, "Default User");
    }
}
```

**Flow Rules Configuration:**
```java
@Configuration
public class SentinelConfig {
    
    @PostConstruct
    public void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        
        FlowRule rule = new FlowRule();
        rule.setResource("getUserById");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(100); // 100 QPS limit
        
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
```

**Circuit Breaker Rules:**
```java
@PostConstruct
public void initDegradeRules() {
    List<DegradeRule> rules = new ArrayList<>();
    
    DegradeRule rule = new DegradeRule();
    rule.setResource("unreliableService");
    rule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
    rule.setCount(0.5); // 50% exception ratio
    rule.setTimeWindow(30); // Circuit open for 30 seconds
    
    rules.add(rule);
    DegradeRuleManager.loadRules(rules);
}
```

---

## PHẦN 6: PRODUCTION TROUBLESHOOTING

### Q19: High CPU & Thread Pool Exhaustion

**Câu trả lời:**

**Troubleshooting Steps:**

**1. Thread Dump Analysis:**
```bash
# Generate thread dump
jstack <pid> > threaddump.txt

# Or using JVisualVM
jvisualvm → Threads → Thread Dump
```

**2. Identify Bottlenecks:**
```java
@Component
public class ThreadPoolMonitor {
    
    @Scheduled(fixedRate = 30000)
    public void monitorThreadPools() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) 
            applicationTaskExecutor.getThreadPoolExecutor();
            
        log.info("Active threads: {}/{}", 
            executor.getActiveCount(), 
            executor.getMaximumPoolSize());
            
        log.info("Queue size: {}", executor.getQueue().size());
        
        if (executor.getActiveCount() > executor.getMaximumPoolSize() * 0.8) {
            log.warn("Thread pool utilization high!");
        }
    }
}
```

**3. Memory Analysis:**
```bash
# Heap dump
jmap -dump:format=b,file=heapdump.hprof <pid>

# GC analysis
jstat -gc <pid> 5s
```

**4. Application Metrics:**
```java
@Component
public class ApplicationMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter requestCounter;
    private final Timer responseTimer;
    
    public ApplicationMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.requestCounter = Counter.builder("http.requests.total").register(meterRegistry);
        this.responseTimer = Timer.builder("http.response.time").register(meterRegistry);
    }
    
    @EventListener
    public void handleRequest(RequestEvent event) {
        requestCounter.increment();
        responseTimer.record(event.getDuration(), TimeUnit.MILLISECONDS);
    }
}
```

---

## PHẦN 7: BEST PRACTICES SUMMARY

### Key Takeaways:

**Threading:**
1. **Use ThreadPools**: Tránh tạo threads manually
2. **Async Processing**: Cho long-running operations
3. **Virtual Threads**: Cho I/O intensive applications
4. **Monitor Resources**: Thread count, memory usage
5. **Proper Cleanup**: ThreadLocal, resources

**Sentinel:**
1. **Circuit Breaker**: Protect against cascade failures
2. **Flow Control**: Prevent system overload
3. **Real-time Monitoring**: Dashboard và alerts
4. **Dynamic Configuration**: Runtime rule updates
5. **Fallback Strategies**: Graceful degradation

**Production:**
1. **Monitoring**: Comprehensive metrics và logging
2. **Alerting**: Proactive issue detection
3. **Capacity Planning**: Load testing và sizing
4. **Incident Response**: Runbooks và procedures
5. **Continuous Improvement**: Performance optimization

---

*Tài liệu này cung cấp foundation knowledge cho Spring Boot threading và Sentinel. Trong thực tế, cần kết hợp với hands-on experience và specific use cases của từng project.*