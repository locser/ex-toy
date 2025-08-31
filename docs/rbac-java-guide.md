# Hướng dẫn RBAC (Role-Based Access Control) với Java

## Mục lục
1. [Giới thiệu về RBAC](#giới-thiệu-về-rbac)
2. [Các khái niệm cơ bản](#các-khái-niệm-cơ-bản)
3. [Thiết kế RBAC trong Java](#thiết-kế-rbac-trong-java)
4. [Triển khai với Spring Security](#triển-khai-với-spring-security)
5. [Ví dụ thực tế](#ví-dụ-thực-tế)
6. [Best Practices](#best-practices)

---

## Giới thiệu về RBAC

RBAC (Role-Based Access Control) là một mô hình bảo mật được sử dụng rộng rãi để quản lý quyền truy cập trong các hệ thống phần mềm. Thay vì gán quyền trực tiếp cho từng người dùng, RBAC sử dụng khái niệm "vai trò" (role) làm trung gian.

### Lợi ích của RBAC:
- **Đơn giản hóa quản lý quyền**: Dễ dàng gán/thu hồi quyền thông qua vai trò
- **Tăng tính bảo mật**: Nguyên tắc "least privilege" - chỉ cấp quyền tối thiểu cần thiết
- **Dễ bảo trì**: Thay đổi quyền của vai trò sẽ áp dụng cho tất cả người dùng có vai trò đó
- **Tuân thủ quy định**: Dễ dàng audit và kiểm soát quyền truy cập

## Các khái niệm cơ bản

### 1. User (Người dùng)
Đại diện cho một thực thể có thể đăng nhập và sử dụng hệ thống.

### 2. Role (Vai trò)
Tập hợp các quyền được nhóm lại theo chức năng công việc. Ví dụ: ADMIN, USER, MANAGER.

### 3. Permission (Quyền)
Quyền thực hiện một hành động cụ thể trên một tài nguyên. Ví dụ: READ_USER, WRITE_POST, DELETE_COMMENT.

### 4. Resource (Tài nguyên)
Đối tượng được bảo vệ trong hệ thống. Ví dụ: User, Post, Comment.

### 5. Mối quan hệ
- **User ↔ Role**: Nhiều-nhiều (một user có thể có nhiều role, một role có thể được gán cho nhiều user)
- **Role ↔ Permission**: Nhiều-nhiều (một role có thể có nhiều permission, một permission có thể thuộc nhiều role)

## Thiết kế RBAC trong Java

### Cấu trúc Entity không có JPA Relationships

#### 1. Entity User (đã có sẵn)
```java
// User entity đã tồn tại trong hệ thống
// Path: toy-domain/src/main/java/locser/toy/domain/model/entity/User.java
```

#### 2. Entity Role
```java
@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @Column(name = "is_system_role", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isSystemRole = false;

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
        this.isActive = true;
        this.isSystemRole = false;
    }
}
```

#### 3. Entity Permission
```java
@Entity
@Table(name = "permissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permission extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "resource", nullable = false, length = 50)
    private String resource;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @Column(name = "is_system_permission", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isSystemPermission = false;

    public Permission(String name, String description, String resource, String action) {
        this.name = name;
        this.description = description;
        this.resource = resource;
        this.action = action;
        this.isActive = true;
        this.isSystemPermission = false;
    }
}
```

#### 4. Entity UserRole (Bảng mapping User ↔ Role)
```java
@Entity
@Table(name = "user_roles", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRole extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "assigned_by", nullable = false)
    private Long assignedBy;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "assigned_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime assignedAt = LocalDateTime.now();

    public UserRole(Long userId, Long roleId, Long assignedBy) {
        this.userId = userId;
        this.roleId = roleId;
        this.assignedBy = assignedBy;
        this.isActive = true;
        this.assignedAt = LocalDateTime.now();
    }
}
```

#### 5. Entity RolePermission (Bảng mapping Role ↔ Permission)
```java
@Entity
@Table(name = "role_permissions", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"role_id", "permission_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolePermission extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Column(name = "assigned_by", nullable = false)
    private Long assignedBy;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @Column(name = "assigned_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime assignedAt = LocalDateTime.now();

    public RolePermission(Long roleId, Long permissionId, Long assignedBy) {
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.assignedBy = assignedBy;
        this.isActive = true;
        this.assignedAt = LocalDateTime.now();
    }
}
```

### Service Layer cho RBAC

```java
@Service
@Transactional
public class RBACService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    // Gán role cho user
    public void assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new RoleNotFoundException("Role not found"));
        
        user.getRoles().add(role);
        userRepository.save(user);
    }
    
    // Kiểm tra user có permission không
    public boolean hasPermission(String username, String permissionName) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .anyMatch(permission -> permission.getName().equals(permissionName));
    }
    
    // Lấy tất cả permissions của user
    public Set<String> getUserPermissions(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getName)
            .collect(Collectors.toSet());
    }
}
```

## Triển khai với Spring Security

### 1. Custom UserDetailsService

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return UserPrincipal.create(user);
    }
}

// Custom UserPrincipal
public class UserPrincipal implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    
    public UserPrincipal(Long id, String username, String password, 
                        Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }
    
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(permission -> new SimpleGrantedAuthority(permission.getName()))
            .collect(Collectors.toList());
        
        return new UserPrincipal(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            authorities
        );
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    // Implement other UserDetails methods...
}
```

### 2. Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/toys/**").hasAuthority("READ_TOY")
                .requestMatchers(HttpMethod.POST, "/api/toys/**").hasAuthority("CREATE_TOY")
                .requestMatchers(HttpMethod.PUT, "/api/toys/**").hasAuthority("UPDATE_TOY")
                .requestMatchers(HttpMethod.DELETE, "/api/toys/**").hasAuthority("DELETE_TOY")
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN_ACCESS")
                .anyRequest().authenticated()
            )
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        return http.build();
    }
}
```

### 3. Method Level Security

```java
@RestController
@RequestMapping("/api/toys")
public class ToyController {
    
    @Autowired
    private ToyService toyService;
    
    @GetMapping
    @PreAuthorize("hasAuthority('READ_TOY')")
    public ResponseEntity<List<Toy>> getAllToys() {
        return ResponseEntity.ok(toyService.getAllToys());
    }
    
    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_TOY')")
    public ResponseEntity<Toy> createToy(@RequestBody CreateToyRequest request) {
        return ResponseEntity.ok(toyService.createToy(request));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_TOY') or @toyService.isOwner(#id, authentication.name)")
    public ResponseEntity<Toy> updateToy(@PathVariable Long id, 
                                        @RequestBody UpdateToyRequest request) {
        return ResponseEntity.ok(toyService.updateToy(id, request));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_TOY') or hasAuthority('ADMIN_ACCESS')")
    public ResponseEntity<Void> deleteToy(@PathVariable Long id) {
        toyService.deleteToy(id);
        return ResponseEntity.noContent().build();
    }
}
```

## Ví dụ thực tế

### 1. Khởi tạo dữ liệu mẫu

```java
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializePermissions();
        initializeRoles();
        initializeUsers();
    }
    
    private void initializePermissions() {
        if (permissionRepository.count() == 0) {
            // Toy permissions
            permissionRepository.save(new Permission("READ_TOY", "Đọc thông tin đồ chơi", "TOY", "READ"));
            permissionRepository.save(new Permission("CREATE_TOY", "Tạo đồ chơi mới", "TOY", "CREATE"));
            permissionRepository.save(new Permission("UPDATE_TOY", "Cập nhật đồ chơi", "TOY", "UPDATE"));
            permissionRepository.save(new Permission("DELETE_TOY", "Xóa đồ chơi", "TOY", "DELETE"));
            
            // User permissions
            permissionRepository.save(new Permission("READ_USER", "Đọc thông tin người dùng", "USER", "READ"));
            permissionRepository.save(new Permission("UPDATE_USER", "Cập nhật người dùng", "USER", "UPDATE"));
            
            // Admin permissions
            permissionRepository.save(new Permission("ADMIN_ACCESS", "Truy cập admin", "SYSTEM", "ADMIN"));
        }
    }
    
    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            // USER role
            Role userRole = new Role("USER", "Người dùng thông thường");
            userRole.getPermissions().add(permissionRepository.findByName("READ_TOY").get());
            userRole.getPermissions().add(permissionRepository.findByName("READ_USER").get());
            roleRepository.save(userRole);
            
            // CREATOR role
            Role creatorRole = new Role("CREATOR", "Người tạo nội dung");
            creatorRole.getPermissions().add(permissionRepository.findByName("READ_TOY").get());
            creatorRole.getPermissions().add(permissionRepository.findByName("CREATE_TOY").get());
            creatorRole.getPermissions().add(permissionRepository.findByName("UPDATE_TOY").get());
            roleRepository.save(creatorRole);
            
            // ADMIN role
            Role adminRole = new Role("ADMIN", "Quản trị viên");
            adminRole.getPermissions().addAll(permissionRepository.findAll());
            roleRepository.save(adminRole);
        }
    }
    
    private void initializeUsers() {
        if (userRepository.count() == 0) {
            // Admin user
            User admin = new User("admin", passwordEncoder.encode("admin123"));
            admin.getRoles().add(roleRepository.findByName("ADMIN").get());
            userRepository.save(admin);
            
            // Regular user
            User user = new User("user", passwordEncoder.encode("user123"));
            user.getRoles().add(roleRepository.findByName("USER").get());
            userRepository.save(user);
            
            // Creator user
            User creator = new User("creator", passwordEncoder.encode("creator123"));
            creator.getRoles().add(roleRepository.findByName("CREATOR").get());
            userRepository.save(creator);
        }
    }
}
```

### 2. Custom Permission Evaluator

```java
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    
    @Autowired
    private RBACService rbacService;
    
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String username = authentication.getName();
        String permissionName = permission.toString();
        
        return rbacService.hasPermission(username, permissionName);
    }
    
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, 
                               String targetType, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String username = authentication.getName();
        String permissionName = permission.toString();
        
        if ("TOY".equals(targetType)) {
            return rbacService.hasPermission(username, permissionName) || 
                   rbacService.isOwnerOfToy(username, (Long) targetId);
        }
        
        return rbacService.hasPermission(username, permissionName);
    }
}
```

## Best Practices

### 1. Thiết kế Permission

#### Nguyên tắc đặt tên Permission
```java
// Format: ACTION_RESOURCE
// Ví dụ:
READ_TOY, CREATE_TOY, UPDATE_TOY, DELETE_TOY
READ_USER, UPDATE_USER, DELETE_USER
MANAGE_ROLES, ASSIGN_PERMISSIONS
VIEW_REPORTS, EXPORT_DATA
```

### 2. Caching cho Performance

```java
@Service
public class CachedRBACService {
    
    @Autowired
    private RBACService rbacService;
    
    @Cacheable(value = "userPermissions", key = "#username")
    public Set<String> getUserPermissions(String username) {
        return rbacService.getUserPermissions(username);
    }
    
    @CacheEvict(value = "userPermissions", key = "#username")
    public void evictUserPermissionsCache(String username) {
        // Cache sẽ được xóa khi user permissions thay đổi
    }
}
```

### 3. Testing RBAC

```java
@SpringBootTest
public class RBACIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void testAdminCanAccessAllEndpoints() {
        String adminToken = loginAndGetToken("admin", "admin123");
        
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/toys",
            HttpMethod.GET,
            createAuthenticatedRequest(adminToken),
            String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    public void testUserCanOnlyReadToys() {
        String userToken = loginAndGetToken("user", "user123");
        
        // User can read toys
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/toys",
            HttpMethod.GET,
            createAuthenticatedRequest(userToken),
            String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // User cannot create toys
        response = restTemplate.exchange(
            "/api/toys",
            HttpMethod.POST,
            createAuthenticatedRequest(userToken, createToyRequest()),
            String.class
        );
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
```

## Flow RBAC trong ToyController

### Luồng xử lý khi có request truy cập endpoint

#### 1. **Request Flow Diagram**
```
[Client Request] 
    ↓
[Spring MVC DispatcherServlet]
    ↓
[ToyController Method với @RequirePermission]
    ↓
[RBACSecurityAspect - @Around Advice]
    ↓
[Kiểm tra Authentication & Authorization]
    ↓
[Proceed hoặc Throw SecurityException]
    ↓
[ToyService Business Logic]
    ↓
[Response to Client]
```

#### 2. **Chi tiết từng bước**

##### **Bước 1: Client gửi request**
```http
GET /api/v1/toys/123/detail
Headers:
  X-User-Id: 456
  Content-Type: application/json
```

##### **Bước 2: Spring MVC routing**
- DispatcherServlet nhận request
- Route đến `ToyController.getToyByIdDetail(Long id)`
- Method có annotation `@RequirePermission("READ_TOY")`

##### **Bước 3: RBACSecurityAspect intercept**
```java
@Around("@annotation(requirePermission)")
public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) {
    // 3.1: Lấy userId từ request
    Long userId = getCurrentUserId(); // từ header X-User-Id hoặc param
    
    // 3.2: Kiểm tra authentication
    if (userId == null) {
        throw new SecurityException("User not authenticated");
    }
    
    // 3.3: Lấy required permissions từ annotation
    String[] requiredPermissions = requirePermission.value(); // ["READ_TOY"]
    
    // 3.4: Gọi RBACService để kiểm tra quyền
    boolean hasPermission = rbacService.hasAnyPermission(userId, requiredPermissions);
    
    // 3.5: Kiểm tra ownership nếu được phép
    if (!hasPermission && requirePermission.allowOwner()) {
        hasPermission = checkOwnership(joinPoint, requirePermission, userId);
    }
    
    // 3.6: Quyết định cho phép hoặc từ chối
    if (!hasPermission) {
        throw new SecurityException("Access denied. Required permissions: READ_TOY");
    }
    
    // 3.7: Cho phép thực thi method
    return joinPoint.proceed();
}
```

##### **Bước 4: RBACService kiểm tra quyền**
```java
@Override
public boolean hasPermission(Long userId, String permissionName) {
    // 4.1: Lấy active roles của user
    List<UserRole> userRoles = userRoleRepository.findActiveByUserId(userId);
    // SQL: SELECT * FROM user_roles WHERE user_id = ? AND is_active = true 
    //      AND (expires_at IS NULL OR expires_at > NOW())
    
    // 4.2: Lấy role IDs
    List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(toList());
    
    // 4.3: Lấy permissions cho các roles này
    List<RolePermission> rolePermissions = rolePermissionRepository.findActiveByRoleIds(roleIds);
    // SQL: SELECT * FROM role_permissions WHERE role_id IN (?, ?, ?) AND is_active = true
    
    // 4.4: Kiểm tra permission có tồn tại không
    return rolePermissions.stream().anyMatch(rp -> {
        return permissionRepository.findById(rp.getPermissionId())
                .map(Permission::getName)
                .filter(name -> name.equals(permissionName))
                .isPresent();
    });
}
```

##### **Bước 5: Database queries thực tế**
```sql
-- Query 1: Lấy roles của user
SELECT ur.*, r.name as role_name 
FROM user_roles ur 
JOIN roles r ON ur.role_id = r.id 
WHERE ur.user_id = 456 
  AND ur.is_active = true 
  AND r.is_active = true
  AND (ur.expires_at IS NULL OR ur.expires_at > NOW());

-- Query 2: Lấy permissions của roles
SELECT rp.*, p.name as permission_name 
FROM role_permissions rp 
JOIN permissions p ON rp.permission_id = p.id 
WHERE rp.role_id IN (1, 2, 3) 
  AND rp.is_active = true 
  AND p.is_active = true;

-- Query 3: Kiểm tra permission cụ thể
SELECT COUNT(*) FROM permissions p
JOIN role_permissions rp ON p.id = rp.permission_id
JOIN user_roles ur ON rp.role_id = ur.role_id
WHERE ur.user_id = 456 
  AND p.name = 'READ_TOY'
  AND ur.is_active = true 
  AND rp.is_active = true 
  AND p.is_active = true;
```

#### 3. **Các trường hợp cụ thể**

##### **Case 1: User có quyền READ_TOY**
```
Request: GET /api/v1/toys/123/detail
User ID: 456 (có role USER với permission READ_TOY)

Flow:
1. getCurrentUserId() → 456
2. hasAnyPermission(456, ["READ_TOY"]) → true
3. joinPoint.proceed() → ToyController.getToyByIdDetail(123)
4. toyService.getToyByIdDetail(123) → ToyResponseDTO
5. Return success response
```

##### **Case 2: User không có quyền**
```
Request: POST /api/v1/toys (tạo toy mới)
User ID: 456 (chỉ có role USER, không có CREATE_TOY)

Flow:
1. getCurrentUserId() → 456
2. hasAnyPermission(456, ["CREATE_TOY"]) → false
3. allowOwner = false → không check ownership
4. Throw SecurityException("Access denied. Required permissions: CREATE_TOY")
5. Return 403 Forbidden
```

##### **Case 3: User không có quyền nhưng là owner**
```
Request: PUT /api/v1/toys/123 (update toy)
User ID: 456 (không có UPDATE_TOY nhưng là owner của toy 123)
Annotation: @RequirePermission(value = "UPDATE_TOY", allowOwner = true, resourceType = "TOY", resourceIdParam = "id")

Flow:
1. getCurrentUserId() → 456
2. hasAnyPermission(456, ["UPDATE_TOY"]) → false
3. allowOwner = true → checkOwnership(456, "TOY", 123)
4. isResourceOwner(456, "TOY", 123) → true (toy.userId == 456)
5. joinPoint.proceed() → cho phép update
```

##### **Case 4: Admin có tất cả quyền**
```
Request: DELETE /api/v1/toys/123
User ID: 1 (có role ADMIN với tất cả permissions)

Flow:
1. getCurrentUserId() → 1
2. hasAnyPermission(1, ["DELETE_TOY", "MANAGE_TOY"]) → true (có ADMIN_ACCESS)
3. joinPoint.proceed() → cho phép delete
```

#### 4. **Performance Optimization**

##### **Caching Strategy**
```java
// Cache user permissions for 5 minutes
@Cacheable(value = "userPermissions", key = "#userId", unless = "#result.isEmpty()")
public Set<String> getUserPermissions(Long userId) {
    // Implementation
}

// Cache role assignments for 10 minutes  
@Cacheable(value = "userRoles", key = "#userId")
public List<String> getUserRoles(Long userId) {
    // Implementation
}
```

##### **Database Optimization**
```sql
-- Composite indexes for fast lookups
CREATE INDEX idx_user_roles_user_active ON user_roles(user_id, is_active, expires_at);
CREATE INDEX idx_role_permissions_role_active ON role_permissions(role_id, is_active);
CREATE INDEX idx_permissions_name_active ON permissions(name, is_active);

-- View for quick permission lookup
CREATE VIEW user_permissions_fast AS
SELECT ur.user_id, p.name as permission_name
FROM user_roles ur
JOIN role_permissions rp ON ur.role_id = rp.role_id  
JOIN permissions p ON rp.permission_id = p.id
WHERE ur.is_active = true 
  AND rp.is_active = true 
  AND p.is_active = true
  AND (ur.expires_at IS NULL OR ur.expires_at > NOW());
```

#### 5. **Error Handling & Logging**

```java
@Around("@annotation(requirePermission)")
public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) {
    String methodName = joinPoint.getSignature().getName();
    Long userId = getCurrentUserId();
    
    try {
        // Permission check logic...
        
        if (!hasPermission) {
            // Log security violation
            log.warn("SECURITY_VIOLATION: User {} denied access to {} - Required: {}", 
                    userId, methodName, Arrays.toString(requiredPermissions));
            throw new SecurityException("Access denied");
        }
        
        // Log successful access
        log.debug("SECURITY_GRANTED: User {} accessed {} with permissions {}", 
                userId, methodName, Arrays.toString(requiredPermissions));
        
        return joinPoint.proceed();
        
    } catch (SecurityException e) {
        // Log and re-throw
        log.error("SECURITY_ERROR: User {} - Method {} - Error: {}", 
                userId, methodName, e.getMessage());
        throw e;
    }
}
```

## Kết luận

RBAC là một mô hình bảo mật mạnh mẽ và linh hoạt cho các ứng dụng Java. Khi triển khai RBAC:

### Ưu điểm:
- **Quản lý tập trung**: Dễ dàng quản lý quyền thông qua roles
- **Bảo mật cao**: Nguyên tắc least privilege
- **Linh hoạt**: Có thể mở rộng và tùy chỉnh theo nhu cầu
- **Audit**: Dễ dàng theo dõi và kiểm soát quyền truy cập

### Lưu ý quan trọng:
- **Performance**: Sử dụng caching cho các truy vấn permission thường xuyên
- **Database Design**: Thiết kế index phù hợp cho các bảng user, role, permission
- **Security**: Luôn validate input và sử dụng parameterized queries
- **Testing**: Viết test cases đầy đủ cho tất cả scenarios bảo mật

### Tài liệu tham khảo:
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [NIST RBAC Standard](https://csrc.nist.gov/projects/role-based-access-control)
- [OWASP Access Control Guidelines](https://owasp.org/www-project-top-ten/)

---

*Tài liệu này cung cấp hướng dẫn toàn diện về RBAC với Java. Để biết thêm chi tiết hoặc có câu hỏi, vui lòng tham khảo documentation chính thức.*
