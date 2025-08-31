# Hướng dẫn Unit Testing trong Java - Từ cơ bản đến nâng cao

## Mục lục

1. [Giới thiệu về Unit Testing](#giới-thiệu-về-unit-testing)
2. [JUnit 5 - Framework chính](#junit-5---framework-chính)
3. [Mockito - Mocking Framework](#mockito---mocking-framework)
4. [TestContainers - Integration Testing](#testcontainers---integration-testing)
5. [Spring Boot Testing](#spring-boot-testing)
6. [Best Practices trong doanh nghiệp](#best-practices-trong-doanh-nghiệp)
7. [Các loại test thường viết](#các-loại-test-thường-viết)
8. [Câu hỏi phỏng vấn thường gặp](#câu-hỏi-phỏng-vấn-thường-gặp)

---

## Giới thiệu về Unit Testing

### Unit Test là gì?

Unit Test là việc kiểm thử từng đơn vị nhỏ nhất của code (thường là method hoặc class) một cách độc lập để đảm bảo chúng hoạt động đúng như mong đợi.

### Tại sao cần Unit Test?

- **Phát hiện bug sớm**: Tìm lỗi ngay khi code được viết
- **Refactoring an toàn**: Thay đổi code mà không lo phá vỡ tính năng
- **Documentation**: Test case là tài liệu sống của code
- **Thiết kế tốt hơn**: Buộc phải viết code dễ test = code tốt hơn
- **Confidence**: Tin tưởng khi deploy production

### Test Pyramid

```
    /\
   /  \
  / UI \     <- Ít test, chậm, đắt
 /______\
/        \
| Integration| <- Vừa phải
|____________|
|            |
|    Unit    | <- Nhiều test, nhanh, rẻ
|____________|
```

### 🎯 Tóm lại:

JUnit 5: khung chạy test.

Mockito: giả lập (mock) dependencies để cô lập test.

AssertJ: viết assertions đẹp, rõ ràng hơn.

## JUnit 5 - Framework chính

### Cài đặt

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

### Annotations cơ bản

#### @Test - Đánh dấu test method

```java
@Test
void shouldCalculateSum() {
    // Given
    Calculator calculator = new Calculator();

    // When
    int result = calculator.add(2, 3);

    // Then
    assertEquals(5, result);
}
```

#### @BeforeEach và @AfterEach

```java
class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @AfterEach
    void tearDown() {
        // Cleanup resources if needed
    }

    @Test
    void shouldCreateUser() {
        // Test implementation
    }
}
```

#### @BeforeAll và @AfterAll

```java
class DatabaseTest {

    private static Database database;

    @BeforeAll
    static void initDatabase() {
        database = new Database();
        database.connect();
    }

    @AfterAll
    static void closeDatabase() {
        database.disconnect();
    }
}
```

#### @DisplayName - Tên test dễ hiểu

```java
@Test
@DisplayName("Should throw exception when user email is invalid")
void shouldThrowExceptionWhenUserEmailIsInvalid() {
    // Test implementation
}
```

#### @ParameterizedTest - Test với nhiều tham số

```java
@ParameterizedTest
@ValueSource(strings = {"", " ", "invalid-email", "@domain.com"})
@DisplayName("Should reject invalid email formats")
void shouldRejectInvalidEmailFormats(String invalidEmail) {
    assertThrows(InvalidEmailException.class, () -> {
        emailValidator.validate(invalidEmail);
    });
}

@ParameterizedTest
@CsvSource({
    "1, 1, 2",
    "2, 3, 5",
    "10, 15, 25"
})
void shouldAddTwoNumbers(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}
```

#### @Nested - Nhóm test cases

```java
class UserServiceTest {

    @Nested
    @DisplayName("User Creation Tests")
    class UserCreationTests {

        @Test
        @DisplayName("Should create user with valid data")
        void shouldCreateUserWithValidData() {
            // Test implementation
        }

        @Test
        @DisplayName("Should throw exception with invalid email")
        void shouldThrowExceptionWithInvalidEmail() {
            // Test implementation
        }
    }

    @Nested
    @DisplayName("User Update Tests")
    class UserUpdateTests {

        @Test
        void shouldUpdateUserName() {
            // Test implementation
        }
    }
}
```

### Assertions mạnh mẽ

#### Basic Assertions

```java
@Test
void basicAssertions() {
    // Equality
    assertEquals(expected, actual);
    assertNotEquals(unexpected, actual);

    // Boolean
    assertTrue(condition);
    assertFalse(condition);

    // Null checks
    assertNull(object);
    assertNotNull(object);

    // Object identity
    assertSame(expected, actual);
    assertNotSame(unexpected, actual);
}
```

#### Exception Testing

```java
@Test
void shouldThrowException() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        userService.createUser(null);
    });

    assertEquals("User cannot be null", exception.getMessage());
}

@Test
void shouldNotThrowException() {
    assertDoesNotThrow(() -> {
        userService.createUser(validUser);
    });
}
```

#### Collection Assertions

```java
@Test
void collectionAssertions() {
    List<String> names = Arrays.asList("John", "Jane", "Bob");

    // Size
    assertEquals(3, names.size());

    // Contains
    assertTrue(names.contains("John"));
    assertFalse(names.contains("Alice"));

    // All elements
    assertAll(
        () -> assertTrue(names.contains("John")),
        () -> assertTrue(names.contains("Jane")),
        () -> assertTrue(names.contains("Bob"))
    );
}
```

#### Custom Assertions với AssertJ

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.24.2</version>
    <scope>test</scope>
</dependency>
```

```java
import static org.assertj.core.api.Assertions.*;

@Test
void assertJExamples() {
    // String assertions
    assertThat("Hello World")
        .isNotNull()
        .startsWith("Hello")
        .endsWith("World")
        .contains("lo Wo");

    // Collection assertions
    List<String> names = Arrays.asList("John", "Jane", "Bob");
    assertThat(names)
        .hasSize(3)
        .contains("John", "Jane")
        .doesNotContain("Alice")
        .allMatch(name -> name.length() >= 3);

    // Object assertions
    User user = new User("John", "john@email.com", 25);
    assertThat(user)
        .isNotNull()
        .extracting(User::getName, User::getEmail, User::getAge)
        .containsExactly("John", "john@email.com", 25);
}
```

## Mockito - Mocking Framework

### Tại sao cần Mock?

- **Isolation**: Test từng unit độc lập
- **Control**: Kiểm soát behavior của dependencies
- **Speed**: Không cần database, network calls thật
- **Reliability**: Không phụ thuộc vào external services

### Cài đặt

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.5.0</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.5.0</version>
    <scope>test</scope>
</dependency>
```

### Tạo Mock Objects

#### Cách 1: Sử dụng @Mock annotation

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {
        // Given
        User user = new User("John", "john@email.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = userService.createUser(user);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(user);
        verify(emailService).sendWelcomeEmail(user.getEmail());
    }
}
```

#### Cách 2: Sử dụng Mockito.mock()

```java
@Test
void manualMockCreation() {
    // Given
    UserRepository userRepository = mock(UserRepository.class);
    EmailService emailService = mock(EmailService.class);
    UserService userService = new UserService(userRepository, emailService);

    User user = new User("John", "john@email.com");
    when(userRepository.findByEmail("john@email.com")).thenReturn(user);

    // When
    User result = userService.findByEmail("john@email.com");

    // Then
    assertThat(result).isEqualTo(user);
}
```

### Stubbing - Định nghĩa behavior

#### When-Then Pattern

```java
@Test
void stubbingExamples() {
    // Return value
    when(userRepository.findById(1L)).thenReturn(user);

    // Return different values on consecutive calls
    when(userRepository.count())
        .thenReturn(1L)
        .thenReturn(2L)
        .thenReturn(3L);

    // Throw exception
    when(userRepository.findById(-1L))
        .thenThrow(new IllegalArgumentException("Invalid ID"));

    // Argument matchers
    when(userRepository.findByAge(anyInt())).thenReturn(users);
    when(userRepository.findByName(eq("John"))).thenReturn(johnUser);
    when(userRepository.findByEmail(contains("@gmail.com"))).thenReturn(gmailUsers);
}
```

#### Argument Matchers

```java
@Test
void argumentMatchersExamples() {
    // Any
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // Specific values
    when(userRepository.findById(eq(1L))).thenReturn(user);

    // String matchers
    when(userRepository.findByName(startsWith("John"))).thenReturn(users);
    when(userRepository.findByEmail(endsWith("@company.com"))).thenReturn(users);

    // Collection matchers
    when(userRepository.findByIds(anyList())).thenReturn(users);

    // Custom matchers
    when(userRepository.findByAge(argThat(age -> age >= 18))).thenReturn(adults);
}
```

### Verification - Kiểm tra interactions

#### Verify method calls

```java
@Test
void verificationExamples() {
    // Basic verification
    verify(userRepository).save(user);

    // Verify with argument matchers
    verify(emailService).sendEmail(eq("john@email.com"), anyString());

    // Verify number of interactions
    verify(userRepository, times(1)).save(user);
    verify(userRepository, times(2)).findAll();
    verify(userRepository, never()).delete(any(User.class));
    verify(userRepository, atLeast(1)).findById(anyLong());
    verify(userRepository, atMost(3)).save(any(User.class));

    // Verify no more interactions
    verifyNoMoreInteractions(userRepository);

    // Verify in order
    InOrder inOrder = inOrder(userRepository, emailService);
    inOrder.verify(userRepository).save(user);
    inOrder.verify(emailService).sendWelcomeEmail(user.getEmail());
}
```

### Spy Objects - Partial Mocking

```java
@Test
void spyExample() {
    // Create spy of real object
    List<String> spyList = spy(new ArrayList<>());

    // Use real methods
    spyList.add("one");
    spyList.add("two");

    // Verify real method was called
    verify(spyList).add("one");
    assertEquals(2, spyList.size());

    // Stub specific method
    when(spyList.size()).thenReturn(100);
    assertEquals(100, spyList.size());
}

@Test
void spyUserService() {
    UserService spyUserService = spy(new UserService(userRepository));

    // Stub specific method
    doReturn(true).when(spyUserService).isValidEmail(anyString());

    // Call real method that uses stubbed method
    User result = spyUserService.createUser(user);

    verify(spyUserService).isValidEmail(user.getEmail());
}
```

### Advanced Mockito Features

#### @Captor - Capture arguments

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void shouldCaptureUserArgument() {
        // Given
        UserService userService = new UserService(userRepository);
        User inputUser = new User("John", "john@email.com");

        // When
        userService.createUser(inputUser);

        // Then
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertThat(capturedUser.getName()).isEqualTo("John");
        assertThat(capturedUser.getEmail()).isEqualTo("john@email.com");
        assertThat(capturedUser.getCreatedAt()).isNotNull();
    }
}
```

#### Answer - Custom behavior

```java
@Test
void customAnswerExample() {
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
        User user = invocation.getArgument(0);
        user.setId(123L);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    });

    User result = userService.createUser(new User("John", "john@email.com"));

    assertThat(result.getId()).isEqualTo(123L);
    assertThat(result.getCreatedAt()).isNotNull();
}
```

## TestContainers - Integration Testing

### Cài đặt

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.19.0</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mysql</artifactId>
    <version>1.19.0</version>
    <scope>test</scope>
</dependency>
```

### Database Integration Test

```java
@Testcontainers
@SpringBootTest
class UserRepositoryIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Test
    void shouldSaveAndFindUser() {
        // Given
        User user = new User("John", "john@email.com");

        // When
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("John");
    }
}
```

## Spring Boot Testing

### @SpringBootTest - Full Integration Test

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateUser() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John", "john@email.com");

        // When
        ResponseEntity<UserResponse> response = restTemplate.postForEntity(
            "/api/users", request, UserResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getName()).isEqualTo("John");

        // Verify in database
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("John");
    }
}
```

### @WebMvcTest - Controller Layer Test

```java
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {
        // Given
        User user = new User("John", "john@email.com");
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(user);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "John",
                        "email": "john@email.com"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@email.com"));

        verify(userService).createUser(any(CreateUserRequest.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "John",
                        "email": "invalid-email"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }
}
```

### @DataJpaTest - Repository Layer Test

```java
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        // Given
        User user = new User("John", "john@email.com");
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByEmail("john@email.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John");
    }

    @Test
    void shouldReturnEmptyForNonExistentEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@email.com");

        // Then
        assertThat(found).isEmpty();
    }
}
```

### @JsonTest - JSON Serialization Test

```java
@JsonTest
class UserJsonTest {

    @Autowired
    private JacksonTester<User> json;

    @Test
    void shouldSerializeUser() throws Exception {
        // Given
        User user = new User("John", "john@email.com");
        user.setId(1L);

        // When & Then
        assertThat(json.write(user))
                .extractingJsonPathNumberValue("$.id").isEqualTo(1)
                .extractingJsonPathStringValue("$.name").isEqualTo("John")
                .extractingJsonPathStringValue("$.email").isEqualTo("john@email.com");
    }

    @Test
    void shouldDeserializeUser() throws Exception {
        // Given
        String content = """
            {
                "id": 1,
                "name": "John",
                "email": "john@email.com"
            }
            """;

        // When & Then
        assertThat(json.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(new User(1L, "John", "john@email.com"));
    }
}
```

## Best Practices trong doanh nghiệp

### 1. Test Naming Conventions

```java
// ❌ Bad
@Test
void test1() { }

@Test
void testUser() { }

// ✅ Good
@Test
void shouldCreateUserWhenValidDataProvided() { }

@Test
void shouldThrowExceptionWhenEmailIsInvalid() { }

@Test
void shouldReturnEmptyListWhenNoUsersExist() { }
```

### 2. AAA Pattern (Arrange-Act-Assert)

```java
@Test
void shouldCalculateDiscountForPremiumCustomer() {
    // Arrange (Given)
    Customer customer = new Customer("John", CustomerType.PREMIUM);
    Order order = new Order(1000.0);
    DiscountService discountService = new DiscountService();

    // Act (When)
    double discount = discountService.calculateDiscount(customer, order);

    // Assert (Then)
    assertThat(discount).isEqualTo(100.0);
}
```

### 3. Test Data Builders

```java
public class UserTestDataBuilder {
    private String name = "Default Name";
    private String email = "default@email.com";
    private int age = 25;
    private boolean active = true;

    public static UserTestDataBuilder aUser() {
        return new UserTestDataBuilder();
    }

    public UserTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public UserTestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestDataBuilder withAge(int age) {
        this.age = age;
        return this;
    }

    public UserTestDataBuilder inactive() {
        this.active = false;
        return this;
    }

    public User build() {
        return new User(name, email, age, active);
    }
}

// Usage
@Test
void shouldCreatePremiumUser() {
    // Given
    User user = aUser()
        .withName("John Premium")
        .withEmail("john@premium.com")
        .withAge(30)
        .build();

    // When & Then
    // Test logic here
}
```

### 4. Test Fixtures và @TestConfiguration

```java
@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public Clock testClock() {
        return Clock.fixed(
            LocalDateTime.of(2023, 1, 1, 12, 0).toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
        );
    }
}

@SpringBootTest
@Import(TestConfig.class)
class TimeBasedServiceTest {

    @Autowired
    private TimeBasedService timeBasedService;

    @Test
    void shouldReturnFixedTime() {
        LocalDateTime result = timeBasedService.getCurrentTime();
        assertThat(result).isEqualTo(LocalDateTime.of(2023, 1, 1, 12, 0));
    }
}
```

### 5. Custom Assertions

```java
public class UserAssertions {

    public static UserAssert assertThat(User actual) {
        return new UserAssert(actual);
    }

    public static class UserAssert extends AbstractAssert<UserAssert, User> {

        public UserAssert(User actual) {
            super(actual, UserAssert.class);
        }

        public UserAssert hasValidEmail() {
            isNotNull();
            if (!actual.getEmail().contains("@")) {
                failWithMessage("Expected user to have valid email but was <%s>", actual.getEmail());
            }
            return this;
        }

        public UserAssert isActive() {
            isNotNull();
            if (!actual.isActive()) {
                failWithMessage("Expected user to be active but was inactive");
            }
            return this;
        }

        public UserAssert hasAge(int expectedAge) {
            isNotNull();
            if (actual.getAge() != expectedAge) {
                failWithMessage("Expected user age to be <%d> but was <%d>", expectedAge, actual.getAge());
            }
            return this;
        }
    }
}

// Usage
@Test
void shouldCreateValidUser() {
    User user = userService.createUser("John", "john@email.com", 25);

    assertThat(user)
        .hasValidEmail()
        .isActive()
        .hasAge(25);
}
```

## Các loại test thường viết

### 1. Unit Tests - Service Layer

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private PaymentService paymentService;
    @Mock private EmailService emailService;
    @InjectMocks private OrderService orderService;

    @Test
    void shouldProcessOrderSuccessfully() {
        // Given
        Order order = new Order("product", 100.0);
        when(paymentService.processPayment(any())).thenReturn(true);
        when(orderRepository.save(any())).thenReturn(order);

        // When
        OrderResult result = orderService.processOrder(order);

        // Then
        assertThat(result.isSuccess()).isTrue();
        verify(paymentService).processPayment(order);
        verify(orderRepository).save(order);
        verify(emailService).sendConfirmation(order.getCustomerEmail());
    }

    @Test
    void shouldHandlePaymentFailure() {
        // Given
        Order order = new Order("product", 100.0);
        when(paymentService.processPayment(any())).thenReturn(false);

        // When
        OrderResult result = orderService.processOrder(order);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("Payment failed");
        verify(orderRepository, never()).save(any());
        verify(emailService, never()).sendConfirmation(any());
    }
}
```

### 2. Integration Tests - Repository Layer

```java
@DataJpaTest
@Sql("/test-data.sql")
class OrderRepositoryIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldFindOrdersByCustomerId() {
        // When
        List<Order> orders = orderRepository.findByCustomerId(1L);

        // Then
        assertThat(orders).hasSize(2);
        assertThat(orders).extracting(Order::getProductName)
                .containsExactly("Product A", "Product B");
    }

    @Test
    void shouldFindOrdersByDateRange() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        // When
        List<Order> orders = orderRepository.findByDateRange(startDate, endDate);

        // Then
        assertThat(orders).isNotEmpty();
        assertThat(orders).allMatch(order ->
            !order.getOrderDate().isBefore(startDate) &&
            !order.getOrderDate().isAfter(endDate)
        );
    }
}
```

### 3. Controller Tests

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private OrderService orderService;

    @Test
    void shouldCreateOrder() throws Exception {
        // Given
        Order order = new Order("product", 100.0);
        when(orderService.createOrder(any())).thenReturn(order);

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "productName": "product",
                        "amount": 100.0,
                        "customerEmail": "customer@email.com"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("product"))
                .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    void shouldReturnBadRequestForInvalidOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "productName": "",
                        "amount": -100.0
                    }
                    """))
                .andExpect(status().isBadRequest());
    }
}
```

### 4. Security Tests

```java
@SpringBootTest
@AutoConfigureTestDatabase
class SecurityIntegrationTest {

    @Autowired private TestRestTemplate restTemplate;

    @Test
    void shouldRequireAuthenticationForProtectedEndpoint() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity("/api/admin/users", String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldAllowAccessWithValidToken() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("valid-jwt-token");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/admin/users", HttpMethod.GET, entity, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

### 5. Performance Tests

```java
@Test
@Timeout(value = 2, unit = TimeUnit.SECONDS)
void shouldCompleteWithinTimeLimit() {
    // Test that must complete within 2 seconds
    List<User> users = userService.findAllUsers();
    assertThat(users).isNotNull();
}

@RepeatedTest(100)
void shouldHandleConcurrentRequests() {
    // Test that runs 100 times to check for race conditions
    User user = userService.createUser("Test User", "test@email.com");
    assertThat(user.getId()).isNotNull();
}
```

## Câu hỏi phỏng vấn thường gặp

### 1. **Câu hỏi cơ bản**

#### Q: Unit Test là gì? Tại sao cần viết Unit Test?

**A:** Unit Test là việc kiểm thử từng đơn vị nhỏ nhất của code (method/class) một cách độc lập. Cần viết vì:

- Phát hiện bug sớm, giảm cost fix bug
- Tự tin khi refactor code
- Documentation sống cho code
- Buộc phải thiết kế code tốt hơn (testable code)
- Regression testing tự động

#### Q: Sự khác biệt giữa Unit Test, Integration Test và E2E Test?

**A:**

- **Unit Test**: Test từng component độc lập, mock dependencies
- **Integration Test**: Test tương tác giữa các components
- **E2E Test**: Test toàn bộ workflow từ UI đến database

#### Q: Mock và Stub khác nhau như thế nào?

**A:**

- **Mock**: Verify interactions (method calls, parameters)
- **Stub**: Provide predefined responses, không verify interactions

```java
// Mock - verify interactions
verify(userRepository).save(user);

// Stub - provide responses
when(userRepository.findById(1L)).thenReturn(user);
```

### 2. **Câu hỏi về Mockito**

#### Q: @Mock, @Spy, @InjectMocks khác nhau như thế nào?

**A:**

- **@Mock**: Tạo fake object hoàn toàn
- **@Spy**: Wrap real object, có thể stub một số methods
- **@InjectMocks**: Inject mocks vào object thật

#### Q: Khi nào sử dụng ArgumentCaptor?

**A:** Khi cần verify giá trị cụ thể được pass vào method:

```java
@Captor
ArgumentCaptor<User> userCaptor;

verify(userRepository).save(userCaptor.capture());
User capturedUser = userCaptor.getValue();
assertThat(capturedUser.getEmail()).isEqualTo("expected@email.com");
```

#### Q: Làm thế nào để test static methods?

**A:** Sử dụng MockedStatic (Mockito 3.4+):

```java
@Test
void shouldMockStaticMethod() {
    try (MockedStatic<LocalDateTime> mockedTime = mockStatic(LocalDateTime.class)) {
        LocalDateTime fixedTime = LocalDateTime.of(2023, 1, 1, 12, 0);
        mockedTime.when(LocalDateTime::now).thenReturn(fixedTime);

        // Test code that uses LocalDateTime.now()
    }
}
```

### 3. **Câu hỏi về Spring Boot Testing**

#### Q: @SpringBootTest và @WebMvcTest khác nhau như thế nào?

**A:**

- **@SpringBootTest**: Load full application context, chậm hơn
- **@WebMvcTest**: Chỉ load web layer, nhanh hơn, cần @MockBean cho dependencies

#### Q: Khi nào sử dụng @DataJpaTest?

**A:** Khi test repository layer:

- Chỉ load JPA components
- Tự động configure TestEntityManager
- Rollback transactions sau mỗi test
- Sử dụng in-memory database

#### Q: TestContainers là gì và khi nào sử dụng?

**A:** TestContainers cho phép chạy real database/services trong Docker containers:

- Integration tests với real database
- Test với specific database versions
- Test với external services (Redis, Kafka, etc.)

### 4. **Câu hỏi về Best Practices**

#### Q: Test coverage bao nhiêu là đủ?

**A:**

- **Không có con số magic**, quan trọng là quality hơn quantity
- **80-90%** cho business logic critical
- **100%** cho utility functions
- **Ưu tiên**: Happy path → Edge cases → Error cases

#### Q: Làm thế nào để test private methods?

**A:**

- **Không nên test private methods trực tiếp**
- Test thông qua public methods
- Nếu private method quá phức tạp → extract thành separate class

#### Q: Cách handle flaky tests?

**A:**

- **Root cause analysis**: Tìm nguyên nhân (timing, external dependencies)
- **Isolation**: Đảm bảo tests độc lập
- **Deterministic**: Sử dụng fixed time, mock random values
- **Retry mechanism**: Chỉ cho infrastructure tests

### 5. **Câu hỏi thực tế trong doanh nghiệp**

#### Q: Chiến lược testing trong microservices?

**A:**

```
Service A Tests:
├── Unit Tests (70%)
│   ├── Service layer
│   ├── Repository layer
│   └── Utility classes
├── Integration Tests (20%)
│   ├── Database integration
│   ├── Message queue integration
│   └── External API integration (with WireMock)
└── Contract Tests (10%)
    ├── Consumer contracts
    └── Provider contracts
```

#### Q: Cách test async/concurrent code?

**A:**

```java
@Test
void shouldHandleAsyncProcessing() throws Exception {
    // Given
    CompletableFuture<String> future = asyncService.processAsync("input");

    // When & Then
    String result = future.get(5, TimeUnit.SECONDS);
    assertThat(result).isEqualTo("processed-input");
}

@Test
void shouldHandleConcurrentAccess() {
    // Given
    int threadCount = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    // When
    for (int i = 0; i < threadCount; i++) {
        executor.submit(() -> {
            try {
                service.processRequest();
            } finally {
                latch.countDown();
            }
        });
    }

    // Then
    assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
}
```

#### Q: Test data management strategy?

**A:**

- **Test Data Builders**: Flexible object creation
- **@Sql scripts**: Database setup
- **@DirtiesContext**: Clean context when needed
- **TestContainers**: Isolated database per test class

## Kinh nghiệm thực tế trong doanh nghiệp

### 1. **Test Strategy theo team size**

#### Startup/Small team (2-5 devs)

```
Focus: Speed to market
├── Unit Tests: Critical business logic only
├── Integration Tests: Happy path scenarios
├── E2E Tests: Core user journeys
└── Manual Testing: Edge cases
```

#### Medium team (5-20 devs)

```
Focus: Quality + Speed
├── Unit Tests: 70-80% coverage
├── Integration Tests: All major flows
├── Contract Tests: API boundaries
├── E2E Tests: Critical user journeys
└── Performance Tests: Load testing
```

#### Large team (20+ devs)

```
Focus: Reliability + Scalability
├── Unit Tests: 80-90% coverage
├── Integration Tests: All scenarios
├── Contract Tests: All service boundaries
├── E2E Tests: Full user journeys
├── Performance Tests: Stress testing
├── Security Tests: Penetration testing
└── Chaos Engineering: Resilience testing
```

### 2. **CI/CD Pipeline Integration**

```yaml
# .github/workflows/test.yml
name: Test Pipeline
on: [push, pull_request]

jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Setup JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: "17"
      - name: Run Unit Tests
        run: ./mvnw test
      - name: Generate Coverage Report
        run: ./mvnw jacoco:report
      - name: Upload Coverage to Codecov
        uses: codecov/codecov-action@v3

  integration-tests:
    runs-on: ubuntu-latest
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: root
        options: --health-cmd="mysqladmin ping" --health-interval=10s
    steps:
      - name: Run Integration Tests
        run: ./mvnw verify -P integration-tests
```

### 3. **Code Review Checklist cho Tests**

#### ✅ **Good Test Characteristics**

- [ ] Test name clearly describes what is being tested
- [ ] Follows AAA pattern (Arrange-Act-Assert)
- [ ] Tests one thing at a time
- [ ] Independent and can run in any order
- [ ] Fast execution (< 100ms for unit tests)
- [ ] Deterministic (same input = same output)
- [ ] Good assertions (specific, meaningful error messages)

#### ❌ **Red Flags**

- [ ] Tests with Thread.sleep()
- [ ] Tests depending on external services without mocks
- [ ] Tests with hardcoded dates/times
- [ ] Tests that test implementation details
- [ ] Tests with multiple assertions on unrelated things
- [ ] Tests that are commented out

### 4. **Metrics và Monitoring**

#### Test Metrics to Track

```java
// Test execution time monitoring
@TestExecutionListeners(TestExecutionTimeListener.class)
class PerformanceAwareTest {

    @Test
    @Timed(maxExecutionTime = 100, unit = TimeUnit.MILLISECONDS)
    void shouldExecuteQuickly() {
        // Test implementation
    }
}

// Custom test listener
public class TestExecutionTimeListener implements TestExecutionListener {

    @Override
    public void afterTestExecution(TestExtensionContext context) {
        long duration = context.getExecutionDuration().toMillis();
        if (duration > 1000) {
            System.out.println("SLOW TEST: " + context.getDisplayName() + " took " + duration + "ms");
        }
    }
}
```

#### Coverage Goals by Layer

```
Controller Layer: 90-95%
Service Layer: 85-95%
Repository Layer: 70-80%
Utility Classes: 95-100%
Configuration: 60-70%
```

### 5. **Common Pitfalls và Solutions**

#### Pitfall 1: Over-mocking

```java
// ❌ Bad - mocking everything
@Test
void badTest() {
    when(stringUtils.isEmpty(anyString())).thenReturn(false);
    when(mathUtils.add(anyInt(), anyInt())).thenReturn(5);
    // Testing mocks, not real behavior
}

// ✅ Good - mock only external dependencies
@Test
void goodTest() {
    // Use real objects for simple logic
    // Mock only database, external APIs, etc.
}
```

#### Pitfall 2: Testing implementation details

```java
// ❌ Bad - testing internal state
@Test
void badTest() {
    service.processUser(user);
    assertThat(service.getInternalCache().size()).isEqualTo(1);
}

// ✅ Good - testing behavior
@Test
void goodTest() {
    User result = service.processUser(user);
    assertThat(result.isProcessed()).isTrue();
}
```

## Kết luận

### **Roadmap học Unit Testing**

#### **Beginner (1-2 tháng)**

1. JUnit 5 basics (annotations, assertions)
2. Mockito fundamentals (mock, when, verify)
3. AAA pattern và test naming
4. Basic Spring Boot testing (@SpringBootTest)

#### **Intermediate (2-3 tháng)**

1. Advanced Mockito (ArgumentCaptor, Answer, Spy)
2. Spring testing annotations (@WebMvcTest, @DataJpaTest)
3. TestContainers cho integration tests
4. AssertJ cho fluent assertions

#### **Advanced (3-6 tháng)**

1. Test architecture và strategies
2. Performance testing
3. Contract testing (Pact)
4. Custom test frameworks và utilities

### **Tài liệu tham khảo**

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)
- [TestContainers Documentation](https://www.testcontainers.org/)
- [AssertJ Documentation](https://assertj.github.io/doc/)

---

_Tài liệu này cung cấp kiến thức toàn diện về Unit Testing trong Java, từ cơ bản đến nâng cao, chuẩn bị tốt cho phỏng vấn và công việc thực tế._
