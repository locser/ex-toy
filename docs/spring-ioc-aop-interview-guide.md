# Spring IOC & AOP - Interview Guide

## Mục lục

1. [Spring IOC (Inversion of Control)](#spring-ioc-inversion-of-control)
2. [Spring AOP (Aspect-Oriented Programming)](#spring-aop-aspect-oriented-programming)
3. [Câu hỏi phỏng vấn thường gặp](#câu-hỏi-phỏng-vấn-thường-gặp)
4. [Ví dụ thực tế](#ví-dụ-thực-tế)
5. [Best Practices](#best-practices)

---

## Spring IOC (Inversion of Control)

### 1. IOC là gì?

**Inversion of Control** là một design principle trong đó control flow của chương trình được "đảo ngược" - thay vì code điều khiển flow, framework điều khiển flow và gọi code của bạn.

#### Trước khi có IOC (Traditional Approach):

```java
// ❌ BAD - Tight coupling
public class UserService {
    private UserRepository userRepository;

    public UserService() {
        // Service tự tạo dependency
        this.userRepository = new UserRepositoryImpl();
    }

    public User findUser(Long id) {
        return userRepository.findById(id);
    }
}

// Sử dụng
UserService userService = new UserService(); // Service tự quản lý dependencies
```

#### Sau khi có IOC (Spring Approach):

```java
// ✅ GOOD - Loose coupling
@Service
public class UserService {
    private final UserRepository userRepository;

    // Spring inject dependency
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUser(Long id) {
        return userRepository.findById(id);
    }
}

// Spring tự động inject
@Autowired
private UserService userService; // Spring quản lý dependencies
```

### 2. Dependency Injection (DI) - Cơ chế thực hiện IOC

#### 2.1 Constructor Injection (Recommended)

```java
@Service
public class OrderService {
    private final PaymentService paymentService;
    private final EmailService emailService;

    // Constructor injection - dependencies required
    public OrderService(PaymentService paymentService, EmailService emailService) {
        this.paymentService = paymentService;
        this.emailService = emailService;
    }

    public void processOrder(Order order) {
        // Process order logic
        paymentService.processPayment(order);
        emailService.sendConfirmation(order);
    }
}
```

#### 2.2 Setter Injection

```java
@Service
public class NotificationService {
    private EmailService emailService;
    private SMSService smsService;

    // Setter injection - dependencies optional
    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setSmsService(SMSService smsService) {
        this.smsService = smsService;
    }
}
```

#### 2.3 Field Injection (Not Recommended)

```java
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository; // Field injection

    @Autowired
    private CategoryService categoryService;

    // Khó test, không thể set final, không rõ dependencies
}
```

### 3. Spring Container và Bean Lifecycle

#### 3.1 Bean Lifecycle

```java
@Component
public class LifecycleBean implements InitializingBean, DisposableBean {

    @PostConstruct
    public void customInit() {
        System.out.println("1. @PostConstruct called");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("2. InitializingBean.afterPropertiesSet() called");
    }

    @PreDestroy
    public void customDestroy() {
        System.out.println("3. @PreDestroy called");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("4. DisposableBean.destroy() called");
    }
}
```

#### 3.2 Bean Scopes

```java
@Component
@Scope("singleton") // Default - one instance per container
public class SingletonBean {
    private int counter = 0;

    public int increment() {
        return ++counter;
    }
}

@Component
@Scope("prototype") // New instance each time
public class PrototypeBean {
    private int counter = 0;

    public int increment() {
        return ++counter;
    }
}

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionScopedBean {
    // One instance per HTTP session
}

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean {
    // One instance per HTTP request
}
```

### 4. Configuration Classes

#### 4.1 Java Configuration

```java
@Configuration
@EnableTransactionManagement
@EnableCaching
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
        config.setUsername("user");
        config.setPassword("password");
        config.setMaximumPoolSize(20);
        return new HikariDataSource(config);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.example.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.setProperty("hibernate.show_sql", "true");
        em.setJpaProperties(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }
}
```

#### 4.2 Conditional Beans

```java
@Configuration
public class ConditionalConfig {

    @Bean
    @ConditionalOnProperty(name = "app.feature.enabled", havingValue = "true")
    public FeatureService featureService() {
        return new FeatureServiceImpl();
    }

    @Bean
    @ConditionalOnClass(name = "com.example.ExternalService")
    public ExternalService externalService() {
        return new ExternalServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultService defaultService() {
        return new DefaultServiceImpl();
    }
}
```

---

## Spring AOP (Aspect-Oriented Programming)

### 1. AOP là gì?

**Aspect-Oriented Programming** là một programming paradigm cho phép tách biệt cross-cutting concerns (những vấn đề xuyên suốt ứng dụng) như logging, security, transaction management ra khỏi business logic.

#### Cross-cutting Concerns:

- Logging
- Security/Authentication
- Transaction Management
- Performance Monitoring
- Error Handling
- Caching

### 2. AOP Core Concepts

#### 2.1 Aspect

```java
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.service.*.*(..))")
    public void logBeforeMethodExecution(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        logger.info("Executing method: {}.{}", className, methodName);
    }

    @AfterThrowing(pointcut = "execution(* com.example.service.*.*(..))",
                   throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        String methodName = joinPoint.getSignature().getName();
        logger.error("Exception in method: {} - Error: {}", methodName, error.getMessage());
    }
}
```

#### 2.2 Pointcut

```java
@Aspect
@Component
public class PointcutExamples {

    // Method execution
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceLayer() {}

    // Method with specific annotation
    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalMethods() {}

    // Method with specific parameters
    @Pointcut("execution(* *.*(Long, String))")
    public void methodsWithLongAndString() {}

    // Method in specific package
    @Pointcut("within(com.example.controller..*)")
    public void controllerLayer() {}

    // Bean with specific name
    @Pointcut("bean(*Service)")
    public void serviceBeans() {}

    // Combined pointcuts
    @Pointcut("serviceLayer() && transactionalMethods()")
    public void transactionalServiceMethods() {}
}
```

#### 2.3 Advice Types

```java
@Aspect
@Component
public class AdviceExamples {

    // Before advice - executed before method execution
    @Before("execution(* com.example.service.*.*(..))")
    public void beforeAdvice(JoinPoint joinPoint) {
        System.out.println("Before executing: " + joinPoint.getSignature().getName());
    }

    // After advice - executed after method execution (success or failure)
    @After("execution(* com.example.service.*.*(..))")
    public void afterAdvice(JoinPoint joinPoint) {
        System.out.println("After executing: " + joinPoint.getSignature().getName());
    }

    // After returning advice - executed after successful method execution
    @AfterReturning(pointcut = "execution(* com.example.service.*.*(..))",
                    returning = "result")
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {
        System.out.println("Method returned: " + result);
    }

    // After throwing advice - executed after method throws exception
    @AfterThrowing(pointcut = "execution(* com.example.service.*.*(..))",
                   throwing = "error")
    public void afterThrowingAdvice(JoinPoint joinPoint, Throwable error) {
        System.out.println("Method threw exception: " + error.getMessage());
    }

    // Around advice - wraps method execution
    @Around("execution(* com.example.service.*.*(..))")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed(); // Execute method
            long endTime = System.currentTimeMillis();
            System.out.println("Method execution time: " + (endTime - startTime) + "ms");
            return result;
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            throw e;
        }
    }
}
```

### 3. Practical AOP Examples

#### 3.1 Performance Monitoring

```java
@Aspect
@Component
public class PerformanceMonitorAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitorAspect.class);

    @Around("@annotation(Monitored)")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            if (executionTime > 1000) { // Log slow methods
                logger.warn("SLOW METHOD: {} took {}ms", methodName, executionTime);
            } else {
                logger.debug("Method {} executed in {}ms", methodName, executionTime);
            }

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Method {} failed after {}ms", methodName, executionTime, e);
            throw e;
        }
    }
}

// Custom annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitored {}

// Usage
@Service
public class UserService {

    @Monitored
    public User findUser(Long id) {
        // Method will be monitored
        return userRepository.findById(id);
    }
}
```

#### 3.2 Security Aspect

```java
@Aspect
@Component
public class SecurityAspect {

    @Before("@annotation(secured)")
    public void checkSecurity(Secured secured) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("Authentication required");
        }

        String[] requiredRoles = secured.roles();
        boolean hasRole = Arrays.stream(requiredRoles)
            .anyMatch(role -> auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role)));

        if (!hasRole) {
            throw new SecurityException("Insufficient privileges");
        }
    }
}

// Custom security annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Secured {
    String[] roles() default {};
}

// Usage
@Service
public class AdminService {

    @Secured(roles = {"ADMIN"})
    public void deleteUser(Long userId) {
        // Only ADMIN role can access
        userRepository.deleteById(userId);
    }
}
```

#### 3.3 Caching Aspect

```java
@Aspect
@Component
public class CachingAspect {

    @Autowired
    private CacheManager cacheManager;

    @Around("@annotation(cacheable)")
    public Object cacheMethod(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
        String cacheName = cacheable.value();
        String key = generateCacheKey(joinPoint);

        Cache cache = cacheManager.getCache(cacheName);
        Cache.ValueWrapper cachedValue = cache.get(key);

        if (cachedValue != null) {
            return cachedValue.get();
        }

        Object result = joinPoint.proceed();
        cache.put(key, result);
        return result;
    }

    private String generateCacheKey(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        return methodName + "_" + Arrays.toString(args);
    }
}

// Custom caching annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
    String value();
}

// Usage
@Service
public class ProductService {

    @Cacheable("products")
    public Product findProduct(Long id) {
        // Result will be cached
        return productRepository.findById(id);
    }
}
```

### 4. AOP Configuration

#### 4.1 Enable AOP

```java
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }

    @Bean
    public PerformanceMonitorAspect performanceAspect() {
        return new PerformanceMonitorAspect();
    }
}
```

#### 4.2 XML Configuration (Legacy)

```xml
<!-- aop-config.xml -->
<aop:config>
    <aop:aspect id="loggingAspect" ref="loggingAspect">
        <aop:pointcut id="serviceMethods"
                      expression="execution(* com.example.service.*.*(..))"/>
        <aop:before pointcut-ref="serviceMethods"
                    method="logBeforeMethod"/>
        <aop:after pointcut-ref="serviceMethods"
                   method="logAfterMethod"/>
    </aop:aspect>
</aop:config>
```

---

## Câu hỏi phỏng vấn thường gặp

### 1. **Spring IOC Questions**

#### Q: IOC là gì và tại sao cần thiết?

**A:** IOC là design principle cho phép framework điều khiển flow của chương trình thay vì code tự điều khiển. Cần thiết vì:

- **Loose Coupling**: Giảm sự phụ thuộc giữa các components
- **Testability**: Dễ dàng mock dependencies cho testing
- **Maintainability**: Dễ thay đổi implementation mà không ảnh hưởng code
- **Reusability**: Components có thể tái sử dụng ở nhiều nơi

#### Q: Constructor Injection vs Setter Injection?

**A:**

- **Constructor Injection**:
  - ✅ Dependencies required, immutable, thread-safe
  - ❌ Không thể thay đổi dependencies sau khi tạo
- **Setter Injection**:
  - ✅ Có thể thay đổi dependencies, optional dependencies
  - ❌ Dependencies có thể null, không thread-safe

#### Q: Bean scopes trong Spring?

**A:**

- **Singleton** (default): Một instance cho toàn bộ container
- **Prototype**: Mỗi lần request tạo instance mới
- **Request**: Một instance cho mỗi HTTP request
- **Session**: Một instance cho mỗi HTTP session
- **Application**: Một instance cho mỗi ServletContext

### 2. **Spring AOP Questions**

#### Q: AOP giải quyết vấn đề gì?

**A:** AOP giải quyết cross-cutting concerns:

- **Separation of Concerns**: Tách business logic khỏi cross-cutting logic
- **Code Reuse**: Một aspect có thể áp dụng cho nhiều methods
- **Maintainability**: Dễ thay đổi cross-cutting logic ở một nơi
- **Clean Code**: Business logic sạch sẽ, dễ đọc

#### Q: Các loại Advice trong AOP?

**A:**

- **@Before**: Thực thi trước method execution
- **@After**: Thực thi sau method execution (success/failure)
- **@AfterReturning**: Thực thi sau successful execution
- **@AfterThrowing**: Thực thi sau exception
- **@Around**: Wrap method execution, có thể thay đổi behavior

#### Q: Pointcut expressions phổ biến?

**A:**

```java
// Method execution
execution(* com.example.service.*.*(..))

// Method với annotation
@annotation(org.springframework.transaction.annotation.Transactional)

// Method trong package
within(com.example.controller..*)

// Bean với tên cụ thể
bean(*Service)

// Method parameters
execution(* *.*(Long, String))
```

### 3. **Advanced Questions**

#### Q: Circular Dependencies trong Spring?

**A:** Spring giải quyết circular dependencies bằng:

- **Constructor Injection**: Không thể resolve, throw exception
- **Setter Injection**: Có thể resolve bằng lazy initialization
- **@Lazy**: Delay bean creation cho đến khi cần

```java
@Service
public class ServiceA {
    private ServiceB serviceB;

    @Autowired
    public void setServiceB(ServiceB serviceB) {
        this.serviceB = serviceB;
    }
}

@Service
public class ServiceB {
    private ServiceA serviceA;

    @Autowired
    public void setServiceA(ServiceA serviceA) {
        this.serviceA = serviceA;
    }
}
```

#### Q: AOP Proxy Types?

**A:**

- **JDK Dynamic Proxy**: Cho interfaces (default)
- **CGLIB Proxy**: Cho classes (khi không có interface)
- **Configuration**: `@EnableAspectJAutoProxy(proxyTargetClass = true)`

#### Q: Performance impact của AOP?

**A:**

- **Minimal**: AOP proxies có overhead nhỏ
- **Caching**: Sử dụng caching để giảm overhead
- **Pointcut Optimization**: Sử dụng pointcut expressions hiệu quả
- **Profiling**: Monitor performance impact trong production

---

## Ví dụ thực tế

### 1. **E-commerce Application**

#### User Service với IOC

```java
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuditService auditService;

    public UserService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      EmailService emailService,
                      AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.auditService = auditService;
    }

    public User createUser(CreateUserRequest request) {
        // Validate request
        validateUserRequest(request);

        // Create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());

        User savedUser = userRepository.save(user);

        // Send welcome email
        emailService.sendWelcomeEmail(savedUser.getEmail());

        // Audit
        auditService.logUserCreation(savedUser.getId());

        return savedUser;
    }
}
```

#### AOP cho Logging và Security

```java
@Aspect
@Component
public class EcommerceAspect {

    @Around("@annotation(Logged)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.info("Starting method: {}.{} with args: {}",
                className, methodName, Arrays.toString(joinPoint.getArgs()));

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.info("Method {}.{} completed in {}ms",
                    className, methodName, executionTime);
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Method {}.{} failed after {}ms with error: {}",
                     className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }

    @Before("@annotation(secured)")
    public void checkSecurity(Secured secured) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("Authentication required");
        }

        String[] requiredRoles = secured.roles();
        boolean hasRole = Arrays.stream(requiredRoles)
            .anyMatch(role -> auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role)));

        if (!hasRole) {
            throw new SecurityException("Insufficient privileges");
        }
    }
}

// Usage
@Service
public class OrderService {

    @Logged
    @Secured(roles = {"USER", "ADMIN"})
    public Order createOrder(CreateOrderRequest request) {
        // Method will be logged and secured
        return orderRepository.save(new Order(request));
    }
}
```

### 2. **Banking Application**

#### Transaction Management với AOP

```java
@Aspect
@Component
public class TransactionAspect {

    @Around("@annotation(transactional)")
    public Object manageTransaction(ProceedingJoinPoint joinPoint,
                                  Transactional transactional) throws Throwable {
        TransactionStatus status = null;
        try {
            // Begin transaction
            status = transactionManager.beginTransaction();

            // Execute method
            Object result = joinPoint.proceed();

            // Commit transaction
            transactionManager.commit(status);

            return result;
        } catch (Exception e) {
            // Rollback on exception
            if (status != null) {
                transactionManager.rollback(status);
            }
            throw e;
        }
    }
}

// Usage
@Service
public class AccountService {

    @Transactional
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // Method will be wrapped in transaction
        Account fromAccount = accountRepository.findById(fromAccountId);
        Account toAccount = accountRepository.findById(toAccountId);

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }
}
```

---

## Best Practices

### 1. **IOC Best Practices**

#### Constructor Injection

```java
// ✅ GOOD
@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
}

// ❌ BAD
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
}
```

#### Configuration Classes

```java
@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        // Configuration logic
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        // Configuration logic
    }
}
```

### 2. **AOP Best Practices**

#### Pointcut Reuse

```java
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceLayer() {}

    @Pointcut("@annotation(Logged)")
    public void loggedMethods() {}

    @Before("serviceLayer() && loggedMethods()")
    public void logServiceMethod(JoinPoint joinPoint) {
        // Logging logic
    }
}
```

#### Performance Optimization

```java
@Aspect
@Component
public class PerformanceAspect {

    private final Cache<String, Long> methodCache = Caffeine.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build();

    @Around("@annotation(Monitored)")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        // Check cache first
        Long cachedTime = methodCache.getIfPresent(methodName);
        if (cachedTime != null && cachedTime > 1000) {
            log.warn("Method {} is known to be slow", methodName);
        }

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            // Cache slow methods
            if (executionTime > 1000) {
                methodCache.put(methodName, executionTime);
            }

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Method {} failed after {}ms", methodName, executionTime);
            throw e;
        }
    }
}
```

### 3. **Testing Best Practices**

#### Unit Testing với Mock Dependencies

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequest request = new CreateUserRequest("test@email.com", "password", "Test User");
        User savedUser = new User(1L, "test@email.com", "encoded_password", "Test User");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.createUser(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@email.com");

        verify(emailService).sendWelcomeEmail("test@email.com");
        verify(auditService).logUserCreation(1L);
    }
}
```

#### Integration Testing với AOP

```java
@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void shouldLogMethodExecution() {
        // Given
        CreateUserRequest request = new CreateUserRequest("test@email.com", "password", "Test User");

        // When
        User result = userService.createUser(request);

        // Then
        // Verify logging aspect was applied
        assertThat(result).isNotNull();
        // Check logs for aspect execution
    }
}
```

---

## Kết luận

### **Key Takeaways:**

**Spring IOC:**

1. **Dependency Injection**: Constructor injection preferred
2. **Bean Lifecycle**: Understand initialization and destruction
3. **Scopes**: Choose appropriate scope for your use case
4. **Configuration**: Use Java configuration over XML

**Spring AOP:**

1. **Cross-cutting Concerns**: Separate business logic from cross-cutting logic
2. **Pointcuts**: Use reusable and efficient pointcut expressions
3. **Advice Types**: Choose appropriate advice for your needs
4. **Performance**: Monitor and optimize AOP overhead

**Best Practices:**

1. **Constructor Injection**: For required dependencies
2. **Pointcut Reuse**: Define reusable pointcuts
3. **Testing**: Mock dependencies and test aspects separately
4. **Performance**: Monitor AOP impact in production

### **Tài liệu tham khảo:**

- [Spring Framework Reference](https://docs.spring.io/spring-framework/reference/)
- [Spring AOP Reference](https://docs.spring.io/spring-framework/reference/core/aop.html)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)

---

_Tài liệu này cung cấp foundation knowledge cho Spring IOC và AOP. Trong thực tế, cần kết hợp với hands-on experience và specific use cases của từng project._
