# CPU Performance & Thread Management - Tech Architecture Guide

## 📊 Tổng Quan Kiến Trúc

Dựa trên phân tích codebase, hệ thống sử dụng **multi-layered thread management** với các kỹ thuật tối ưu hóa CPU hiện đại:

### 🏗️ Kiến Trúc Thread Pool Hierarchy

```
┌─────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER                        │
├─────────────────────────────────────────────────────────────┤
│  HTTP Threads (Tomcat)     │  Async Threads (Custom Pools)  │
│  • Default: 200 threads    │  • Gift Executor: 50-200       │
│  • Max Connections: 8192   │  • Kafka Executor: 20-100      │
├─────────────────────────────────────────────────────────────┤
│                    INFRASTRUCTURE LAYER                     │
├─────────────────────────────────────────────────────────────┤
│  DB Pool (HikariCP)        │  Redis Pool (Lettuce)          │
│  • Max Pool: 50            │  • Max Active: 50              │
│  • Min Idle: 10            │  • Max Idle: 20                │
├─────────────────────────────────────────────────────────────┤
│                    MONITORING LAYER                         │
├─────────────────────────────────────────────────────────────┤
│  Prometheus Metrics        │  Custom Aspects                │
│  • CPU Usage               │  • Query Time Monitoring       │
│  • Thread Pool Stats       │  • Slow Operation Detection    │
└─────────────────────────────────────────────────────────────┘
```

## 🎯 CPU Performance Optimization Strategies

### 1. **Thread Pool Configuration - Công Thức Tính Toán**

#### **CPU-Intensive Tasks:**
```java
// Công thức: Number of CPU cores
int corePoolSize = Runtime.getRuntime().availableProcessors();
int maxPoolSize = corePoolSize * 2;

@Bean("cpuIntensiveExecutor")
public Executor cpuIntensiveExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("CPU-");
    return executor;
}
```

#### **I/O-Intensive Tasks (như trong codebase):**
```java
// Công thức: CPU cores * (1 + Wait Time / Service Time)
// Ví dụ: 8 cores * (1 + 100ms / 10ms) = 8 * 11 = 88 threads

@Bean("giftExecutor")
public Executor giftExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(50);        // Base threads
    executor.setMaxPoolSize(200);        // Peak load capacity
    executor.setQueueCapacity(1000);     // Buffer for burst traffic
    executor.setThreadNamePrefix("Gift-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    return executor;
}
```

### 2. **CPU Utilization Thresholds - Ngưỡng Tối Ưu**

#### **Recommended CPU Usage Levels:**

| **Scenario** | **CPU Usage** | **Action** | **Reasoning** |
|--------------|---------------|------------|---------------|
| **Normal Operation** | 40-60% | Maintain | Optimal performance với headroom |
| **Peak Traffic** | 60-80% | Scale horizontally | Tránh bottleneck |
| **Critical Load** | 80-90% | **Alert + Auto-scale** | Nguy cơ performance degradation |
| **Danger Zone** | >90% | **Circuit breaker** | Protect system stability |

#### **Implementation trong Code:**

```java
@Component
public class CPUMonitor {
    
    private final OperatingSystemMXBean osBean = 
        ManagementFactory.getOperatingSystemMXBean();
    
    @Scheduled(fixedDelay = 5000) // Check every 5 seconds
    public void monitorCPU() {
        double cpuUsage = osBean.getProcessCpuLoad() * 100;
        
        if (cpuUsage > 90) {
            // CRITICAL: Activate circuit breaker
            circuitBreakerService.openCircuit("high-cpu");
            alertService.sendCriticalAlert("CPU usage: " + cpuUsage + "%");
            
        } else if (cpuUsage > 80) {
            // WARNING: Scale out
            autoScalingService.triggerScaleOut();
            
        } else if (cpuUsage > 60) {
            // INFO: Monitor closely
            metricsService.recordHighLoad(cpuUsage);
        }
    }
}
```

### 3. **Thread Pool Monitoring - Real-time Metrics**

#### **Key Metrics to Track:**

```java
@Component
public class ThreadPoolMonitor {
    
    @Autowired
    private ThreadPoolTaskExecutor giftExecutor;
    
    @EventListener
    @Scheduled(fixedDelay = 10000)
    public void monitorThreadPools() {
        ThreadPoolExecutor executor = giftExecutor.getThreadPoolExecutor();
        
        // Core metrics
        int activeThreads = executor.getActiveCount();
        int poolSize = executor.getPoolSize();
        int corePoolSize = executor.getCorePoolSize();
        int maxPoolSize = executor.getMaximumPoolSize();
        long completedTasks = executor.getCompletedTaskCount();
        int queueSize = executor.getQueue().size();
        
        // Calculate utilization percentages
        double threadUtilization = (double) activeThreads / poolSize * 100;
        double poolUtilization = (double) poolSize / maxPoolSize * 100;
        double queueUtilization = (double) queueSize / 1000 * 100; // Queue capacity = 1000
        
        // Log metrics
        log.info("Thread Pool Stats - Active: {}/{} ({}%), Pool: {}/{} ({}%), Queue: {} ({}%)",
            activeThreads, poolSize, String.format("%.1f", threadUtilization),
            poolSize, maxPoolSize, String.format("%.1f", poolUtilization),
            queueSize, String.format("%.1f", queueUtilization));
        
        // Alert conditions
        if (threadUtilization > 90) {
            alertService.sendAlert("High thread utilization: " + threadUtilization + "%");
        }
        
        if (queueUtilization > 80) {
            alertService.sendAlert("Queue nearly full: " + queueUtilization + "%");
        }
    }
}
```

## 🚀 Advanced Performance Techniques

### 1. **Virtual Threads (Java 21+) - Future Optimization**

```java
// Thay thế traditional thread pools cho I/O intensive tasks
@Configuration
public class VirtualThreadConfig {
    
    @Bean("virtualThreadExecutor")
    public Executor virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}

@Service
public class GiftService {
    
    @Async("virtualThreadExecutor")
    public CompletableFuture<Gift> processGiftAsync(String campaignId, String userId) {
        // I/O operations - perfect for virtual threads
        // Can handle millions of concurrent operations
        return CompletableFuture.completedFuture(processGift(campaignId, userId));
    }
}
```

### 2. **CPU Cache Optimization**

```java
// Locality of reference - group related data
@Entity
@Table(name = "toys")
public class Toy {
    // Hot fields first (frequently accessed)
    @Id private Long id;
    private String name;
    private ToyStatus status;
    
    // Cold fields last (rarely accessed)
    private String description;
    private LocalDateTime createdAt;
}

// CPU cache-friendly batch processing
@Service
public class BatchProcessor {
    
    public void processToysBatch(List<Long> toyIds) {
        // Process in chunks that fit in CPU cache
        int batchSize = 1000; // Tune based on object size
        
        for (int i = 0; i < toyIds.size(); i += batchSize) {
            List<Long> batch = toyIds.subList(i, 
                Math.min(i + batchSize, toyIds.size()));
            processBatch(batch);
        }
    }
}
```

### 3. **Lock-Free Programming**

```java
// Sử dụng atomic operations thay vì synchronized
@Component
public class CounterService {
    
    private final AtomicLong giftCounter = new AtomicLong(0);
    private final LongAdder participantCounter = new LongAdder(); // Better for high contention
    
    public long incrementGiftCount() {
        return giftCounter.incrementAndGet(); // Lock-free
    }
    
    public void incrementParticipant() {
        participantCounter.increment(); // Even better for concurrent writes
    }
}
```

## 📈 Performance Monitoring Dashboard

### **Key Performance Indicators (KPIs):**

#### **CPU Metrics:**
- **CPU Utilization**: Target 40-70%
- **Load Average**: Should be < number of CPU cores
- **Context Switches**: Monitor for excessive switching

#### **Thread Metrics:**
- **Active Threads**: Monitor utilization %
- **Thread Pool Queue**: Watch for backlog
- **Thread Creation Rate**: Minimize new thread creation

#### **Application Metrics:**
- **Response Time**: P95 < 200ms
- **Throughput**: Requests per second
- **Error Rate**: < 0.1%

### **Prometheus Queries:**

```promql
# CPU Usage
100 - (avg by (instance) (irate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)

# Thread Pool Utilization
(executor_active_threads / executor_pool_size) * 100

# Queue Depth
executor_queue_size

# Response Time P95
histogram_quantile(0.95, http_request_duration_seconds_bucket)
```

## 🛠️ Troubleshooting Guide

### **Common CPU Performance Issues:**

#### **1. High CPU với Low Throughput:**
```bash
# Check for excessive GC
jstat -gc <pid> 1s

# Thread dump analysis
jstack <pid> > thread_dump.txt

# Look for:
# - Thread contention (BLOCKED threads)
# - Excessive context switching
# - CPU-intensive loops
```

#### **2. Thread Pool Exhaustion:**
```java
// Symptoms: 503 errors, high response times
// Solution: Increase pool size or optimize tasks

// Monitor queue growth
if (executor.getQueue().size() > maxQueueSize * 0.8) {
    // Scale up or reject new requests
    scaleUpOrReject();
}
```

#### **3. Memory Leaks in Threading:**
```java
// ThreadLocal cleanup
public class SafeThreadLocalService {
    private static final ThreadLocal<UserContext> userContext = 
        ThreadLocal.withInitial(UserContext::new);
    
    public void processRequest() {
        try {
            // Use ThreadLocal
            userContext.get().setUserId("123");
            // ... process
        } finally {
            // CRITICAL: Always cleanup
            userContext.remove();
        }
    }
}
```

## 🎯 Best Practices Summary

### **DO's:**
1. **Monitor continuously**: CPU, threads, queues
2. **Use appropriate thread pools**: Different pools for different tasks
3. **Implement circuit breakers**: Protect against cascade failures
4. **Profile regularly**: Find bottlenecks before they become problems
5. **Test under load**: Simulate production traffic

### **DON'Ts:**
1. **Don't create threads manually**: Use thread pools
2. **Don't ignore queue sizes**: Monitor and alert
3. **Don't forget ThreadLocal cleanup**: Causes memory leaks
4. **Don't over-optimize prematurely**: Measure first
5. **Don't ignore GC impact**: Monitor garbage collection

---

*Tài liệu này cung cấp foundation cho CPU và Thread optimization. Trong production, cần fine-tune dựa trên specific workload patterns và hardware characteristics.*