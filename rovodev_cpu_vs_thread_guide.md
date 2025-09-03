# CPU vs Thread trong Java - Khi nào dùng gì?

## 🧠 Hiểu Cơ Bản: CPU vs Thread

### **CPU (Central Processing Unit)**
- **Là gì**: Hardware thực tế thực hiện tính toán
- **Số lượng**: Cố định (4, 8, 16 cores...)
- **Vai trò**: Thực thi instructions
- **Không thể tạo thêm**: Hardware limitation

### **Thread**
- **Là gì**: Software abstraction - đường dẫn thực thi
- **Số lượng**: Có thể tạo hàng nghìn (giới hạn bởi memory)
- **Vai trò**: Organize và schedule work
- **Có thể tạo thêm**: Programmatically

## 🎯 Relationship: CPU ↔ Thread

```
┌─────────────────────────────────────────────────────────┐
│                    PHYSICAL LAYER                       │
├─────────────────────────────────────────────────────────┤
│  CPU Core 1    │  CPU Core 2    │  CPU Core 3    │ ... │
├─────────────────────────────────────────────────────────┤
│                    LOGICAL LAYER                        │
├─────────────────────────────────────────────────────────┤
│  Thread 1      │  Thread 2      │  Thread 3      │ ... │
│  Thread 4      │  Thread 5      │  Thread 6      │     │
│  Thread 7      │  Thread 8      │  Thread 9      │     │
└─────────────────────────────────────────────────────────┘

Mapping: N Threads → M CPU Cores (N >> M)
```

## 🚀 Khi Nào Dùng CPU-Focused Approach?

### **1. CPU-Intensive Tasks (Compute-Heavy)**

#### **Characteristics:**
- Tính toán phức tạp
- Ít I/O operations
- Cần processing power

#### **Examples:**
```java
// ❌ BAD: Tạo quá nhiều threads cho CPU-intensive task
public class BadCpuIntensiveExample {
    public void calculatePrimes() {
        int threadCount = 1000; // TOO MANY!
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                // CPU-intensive calculation
                findPrimesUpTo(1_000_000);
            });
        }
    }
}

// ✅ GOOD: Số threads = số CPU cores
public class GoodCpuIntensiveExample {
    public void calculatePrimes() {
        int cpuCores = Runtime.getRuntime().availableProcessors(); // 8 cores
        ExecutorService executor = Executors.newFixedThreadPool(cpuCores);
        
        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                findPrimesUpTo(1_000_000);
            });
        }
    }
    
    // CPU-intensive method
    private List<Integer> findPrimesUpTo(int limit) {
        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= limit; i++) {
            if (isPrime(i)) {
                primes.add(i);
            }
        }
        return primes;
    }
}
```

#### **Rule: Threads ≈ CPU Cores**
```java
@Configuration
public class CpuIntensiveConfig {
    
    @Bean("cpuIntensiveExecutor")
    public Executor cpuIntensiveExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cores);           // 8 cores = 8 threads
        executor.setMaxPoolSize(cores);            // No more than CPU cores
        executor.setQueueCapacity(Integer.MAX_VALUE); // Large queue
        executor.setThreadNamePrefix("CPU-");
        return executor;
    }
}
```

### **2. Parallel Processing**

```java
// Sử dụng ForkJoinPool cho CPU-intensive parallel tasks
public class ParallelProcessingExample {
    
    public long calculateSumParallel(List<Integer> numbers) {
        // Automatically uses available CPU cores
        return numbers.parallelStream()
                     .mapToLong(Integer::longValue)
                     .sum();
    }
    
    // Custom ForkJoinPool với specific parallelism
    public long calculateSumCustom(List<Integer> numbers) {
        int parallelism = Runtime.getRuntime().availableProcessors();
        
        ForkJoinPool customThreadPool = new ForkJoinPool(parallelism);
        try {
            return customThreadPool.submit(() ->
                numbers.parallelStream()
                       .mapToLong(Integer::longValue)
                       .sum()
            ).get();
        } finally {
            customThreadPool.shutdown();
        }
    }
}
```

## 🌐 Khi Nào Dùng Thread-Focused Approach?

### **1. I/O-Intensive Tasks**

#### **Characteristics:**
- Waiting for network, database, file system
- CPU mostly idle
- Concurrency > Parallelism

#### **Examples:**
```java
// ✅ GOOD: Nhiều threads cho I/O operations
public class IoIntensiveExample {
    
    @Bean("ioIntensiveExecutor")
    public Executor ioIntensiveExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cores * 2);      // 8 cores * 2 = 16 threads
        executor.setMaxPoolSize(cores * 10);      // 8 cores * 10 = 80 threads
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("IO-");
        return executor;
    }
    
    @Async("ioIntensiveExecutor")
    public CompletableFuture<String> callExternalAPI(String url) {
        // I/O operation - thread will be blocked waiting
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        return CompletableFuture.completedFuture(response);
    }
}
```

#### **Rule: Threads >> CPU Cores**
```java
// Formula: Threads = CPU Cores * (1 + Wait Time / Service Time)
// Example: 8 cores * (1 + 100ms / 10ms) = 8 * 11 = 88 threads

public class ThreadCalculator {
    
    public int calculateOptimalThreads() {
        int cores = Runtime.getRuntime().availableProcessors();
        double waitTime = 100.0; // ms waiting for I/O
        double serviceTime = 10.0; // ms actual processing
        
        return (int) (cores * (1 + waitTime / serviceTime));
    }
}
```

### **2. Concurrent User Requests**

```java
// Web server handling multiple users
@RestController
public class UserController {
    
    // Mỗi request = 1 thread từ Tomcat thread pool
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        // I/O operation - database call
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }
    
    // Async processing để không block request thread
    @PostMapping("/users/{id}/process")
    public ResponseEntity<String> processUser(@PathVariable Long id) {
        // Delegate to background thread
        userProcessingService.processAsync(id);
        return ResponseEntity.accepted().body("Processing started");
    }
}

@Service
public class UserProcessingService {
    
    @Async("backgroundExecutor")
    public void processAsync(Long userId) {
        // Long-running task in separate thread
        performComplexProcessing(userId);
    }
}
```

## 🎯 Decision Matrix: CPU vs Thread

| **Task Type** | **CPU Usage** | **I/O Wait** | **Thread Strategy** | **Example** |
|---------------|---------------|--------------|---------------------|-------------|
| **Mathematical Calculation** | High | Low | Threads ≈ CPU Cores | Prime numbers, sorting |
| **File Processing** | Medium | Medium | Threads = 2-3x CPU Cores | Image processing |
| **Database Queries** | Low | High | Threads = 5-10x CPU Cores | CRUD operations |
| **HTTP API Calls** | Low | Very High | Threads = 10-20x CPU Cores | External integrations |
| **Real-time Streaming** | Medium | Medium | Reactive/Virtual Threads | WebSocket, SSE |

## 🔄 Hybrid Approach: Best of Both Worlds

### **Ví dụ từ Codebase Thực Tế:**

```java
@Configuration
@EnableAsync
public class OptimalThreadConfig {
    
    // CPU-intensive: Image processing, calculations
    @Bean("cpuExecutor")
    public Executor cpuExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cores);
        executor.setMaxPoolSize(cores);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("CPU-");
        return executor;
    }
    
    // I/O-intensive: Database, external APIs
    @Bean("ioExecutor")
    public Executor ioExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cores * 2);
        executor.setMaxPoolSize(cores * 10);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("IO-");
        return executor;
    }
    
    // Mixed workload: Gift processing (từ codebase)
    @Bean("giftExecutor")
    public Executor giftExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);        // Base for I/O
        executor.setMaxPoolSize(200);        // Scale for peak load
        executor.setQueueCapacity(1000);     // Buffer
        executor.setThreadNamePrefix("Gift-");
        return executor;
    }
}
```

### **Usage Examples:**

```java
@Service
public class TaskService {
    
    // CPU-intensive task
    @Async("cpuExecutor")
    public CompletableFuture<Integer> calculatePrimes(int limit) {
        // Heavy computation
        return CompletableFuture.completedFuture(findPrimesUpTo(limit));
    }
    
    // I/O-intensive task
    @Async("ioExecutor")
    public CompletableFuture<String> fetchUserData(Long userId) {
        // Database + external API calls
        return CompletableFuture.completedFuture(callExternalAPI(userId));
    }
    
    // Mixed workload
    @Async("giftExecutor")
    public CompletableFuture<Gift> processGift(String campaignId, String userId) {
        // Some computation + database + Redis + Kafka
        return CompletableFuture.completedFuture(createGift(campaignId, userId));
    }
}
```

## 🚨 Common Mistakes

### **❌ Mistake 1: Too Many Threads for CPU Tasks**
```java
// BAD: 1000 threads cho CPU-intensive task
ExecutorService executor = Executors.newFixedThreadPool(1000);
for (int i = 0; i < 1000; i++) {
    executor.submit(() -> heavyCalculation()); // Context switching overhead!
}
```

### **❌ Mistake 2: Too Few Threads for I/O Tasks**
```java
// BAD: Chỉ 4 threads cho I/O operations
ExecutorService executor = Executors.newFixedThreadPool(4);
for (int i = 0; i < 1000; i++) {
    executor.submit(() -> callSlowAPI()); // Underutilized!
}
```

### **❌ Mistake 3: Blocking I/O in CPU Pool**
```java
// BAD: I/O operation trong CPU-dedicated pool
@Async("cpuExecutor")
public void processData() {
    String data = restTemplate.getForObject(url, String.class); // BLOCKING I/O!
    heavyCalculation(data);
}
```

## 🎯 Summary: Decision Framework

### **Use CPU-Focused Approach When:**
1. **High CPU utilization** (>80% CPU usage)
2. **Minimal I/O operations**
3. **Mathematical/algorithmic processing**
4. **Threads ≈ CPU cores** is optimal

### **Use Thread-Focused Approach When:**
1. **Low CPU utilization** (<30% CPU usage)
2. **Frequent I/O operations** (DB, network, files)
3. **Concurrent user requests**
4. **Threads >> CPU cores** is beneficial

### **Monitor and Adjust:**
```java
// Always monitor and tune based on actual metrics
@Component
public class PerformanceMonitor {
    
    @Scheduled(fixedDelay = 30000)
    public void logPerformanceMetrics() {
        double cpuUsage = getCpuUsage();
        int activeThreads = getActiveThreads();
        int queueSize = getQueueSize();
        
        log.info("CPU: {}%, Active Threads: {}, Queue: {}", 
                 cpuUsage, activeThreads, queueSize);
        
        // Auto-tune based on metrics
        if (cpuUsage > 90 && activeThreads > getCpuCores() * 2) {
            log.warn("Too many threads for CPU-intensive workload!");
        }
    }
}
```

---

**Key Takeaway**: CPU là hardware resource cố định, Thread là software tool để organize work. Chọn strategy dựa trên workload characteristics, không phải số lượng tuyệt đối!