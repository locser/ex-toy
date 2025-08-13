# MySQL và Spring Data JPA - Hướng Dẫn Tech Lead

## Tổng Quan

Tài liệu này hướng dẫn team về cách sử dụng MySQL với Spring Data JPA trong dự án X-Toy Platform. Được viết từ góc độ Tech Lead để giúp developers hiểu sâu về persistence layer và best practices.

## 1. Kiến Trúc Persistence Layer

### 1.1 Cấu Trúc Hiện Tại
```
toy-domain/
├── model/entity/          # JPA Entities
├── repository/           # Domain Repository Interfaces  
└── specifications/       # JPA Specifications

toy-infrastructure/
├── persistence/
│   ├── mapper/          # JPA Repository Implementations
│   └── repository/      # Infrastructure Repository Implementations
└── config/              # JPA Configuration
```

### 1.2 Tại Sao Chọn Spring Data JPA?

**Ưu điểm:**
- **Type Safety**: Compile-time checking với Criteria API
- **Convention over Configuration**: Tự động generate queries từ method names
- **Specification Pattern**: Dynamic queries mạnh mẽ
- **Auditing Support**: Tự động tracking created/modified dates
- **Caching Integration**: Seamless với Spring Cache

**So với MyBatis-Plus:**
- JPA: Object-oriented, domain-driven approach
- MyBatis-Plus: SQL-centric, more control over queries
- JPA: Better for complex object relationships
- MyBatis-Plus: Better for complex SQL optimizations

## 2. Entity Design Patterns

### 2.1 Base Entity Pattern
```java
@MappedSuperclass
public abstract class DateAudit {
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate  
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### 2.2 Entity Best Practices
```java
@Entity
@Table(name = "toys")
public class Toy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Sử dụng columnDefinition cho MySQL optimization
    @Column(name = "name", nullable = false, 
            columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String name = "";
    
    // Enum mapping với ValueEnum pattern
    @Column(name = "status", nullable = false)
    private Integer status = ToyStatus.AVAILABLE.getValue();
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

## 3. Repository Pattern Implementation

### 3.1 Domain Repository Interface
```java
// Domain layer - Business logic focused
public interface ToyRepository {
    Optional<Toy> findOneById(Long id);
    List<Toy> findByUserIdAndCampaignId(Long userId, Long campaignId);
    List<Toy> findAll(Specification<Toy> spec, Pageable pageable);
}
```

### 3.2 JPA Mapper Interface  
```java
// Infrastructure layer - JPA specific
@Repository
public interface ToyJPAMapper extends JpaRepository<Toy, Long>, 
                                     JpaSpecificationExecutor<Toy> {
    
    // Query methods - Spring Data JPA auto-generation
    List<Toy> findByUserIdAndStatus(Long userId, Integer status);
    
    // Custom queries với @Query
    @Query("SELECT t FROM Toy t WHERE t.campaignId = :campaignId " +
           "AND t.status = :status ORDER BY RAND() LIMIT 1")
    Toy findRandomAvailableToyInCampaign(@Param("campaignId") Long campaignId,
                                        @Param("status") Integer status);
    
    // Bulk operations
    @Modifying
    @Query("UPDATE Toy t SET t.status = :status, t.campaignId = :campaignId " +
           "WHERE t.id IN :ids")
    int updateStatusAndCampaignIdByIds(@Param("ids") List<Long> ids,
                                      @Param("campaignId") Long campaignId,
                                      @Param("status") Integer status);
}
```

### 3.3 Infrastructure Repository Implementation
```java
@Service
public class ToyInfrasRepositoryImpl implements ToyRepository {
    
    private final ToyJPAMapper toyJPAMapper;
    
    @Override
    public List<Toy> findAll(Specification<Toy> specification, Pageable pageable) {
        // Luôn sort by id desc để đảm bảo consistency
        Pageable sortedPageable = PageRequest.of(
            pageable.getPageNumber(), 
            pageable.getPageSize(),
            Sort.by(Sort.Direction.DESC, "id")
        );
        return toyJPAMapper.findAll(specification, sortedPageable).getContent();
    }
}
```

## 4. Dynamic Queries với Specification Pattern

### 4.1 Base Specification
```java
public class BaseSpecification {
    public static <T> Specification<T> hasId(Long id) {
        return (root, query, cb) -> 
            id == null ? null : cb.equal(root.get("id"), id);
    }
    
    public static <T> Specification<T> hasStatus(Integer status) {
        return (root, query, cb) -> 
            status == null ? null : cb.equal(root.get("status"), status);
    }
}
```

### 4.2 Toy Specification
```java
public class ToySpecification extends BaseSpecification {
    
    public static Specification<Toy> belongsToUser(Long userId) {
        return (root, query, cb) -> 
            userId == null ? null : cb.equal(root.get("userId"), userId);
    }
    
    public static Specification<Toy> inCampaign(Long campaignId) {
        return (root, query, cb) -> 
            campaignId == null ? null : cb.equal(root.get("campaignId"), campaignId);
    }
    
    public static Specification<Toy> nameContains(String keyword) {
        return (root, query, cb) -> 
            keyword == null ? null : 
            cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }
    
    // Combine specifications
    public static Specification<Toy> searchCriteria(Long userId, Long campaignId, 
                                                   String keyword, Integer status) {
        return Specification.where(belongsToUser(userId))
                           .and(inCampaign(campaignId))
                           .and(nameContains(keyword))
                           .and(hasStatus(status));
    }
}
```

## 5. MySQL Configuration & Optimization

### 5.1 Application Properties
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/java_demo?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: appuser
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  jpa:
    hibernate:
      ddl-auto: validate  # Production: validate, Development: update
    show-sql: false       # Set true for debugging
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
        use_sql_comments: true
        # Connection pool settings
        connection:
          provider_disables_autocommit: true
        # Query optimization
        jdbc:
          batch_size: 25
          order_inserts: true
          order_updates: true
        # Cache settings
        cache:
          use_second_level_cache: true
          region:
            factory_class: org.hibernate.cache.jcache.JCacheRegionFactory
```

### 5.2 MySQL Configuration (my.cnf) - Đã có sẵn
Dự án đã có file `docker/mysql/my.cnf` được tối ưu cho use case giveaway:
- Buffer pool 512M cho caching
- IO threads tối ưu cho write-heavy workload  
- Connection pool 200 connections
- UTF8MB4 charset support

## 6. Performance Best Practices

### 6.1 Query Optimization
```java
// ❌ N+1 Problem
public List<ToyDTO> getBadToys() {
    List<Toy> toys = toyRepository.findAll();
    return toys.stream()
              .map(toy -> {
                  User user = userRepository.findById(toy.getUserId()); // N+1!
                  return new ToyDTO(toy, user);
              })
              .collect(toList());
}

// ✅ Fetch Join hoặc Projection
@Query("SELECT t FROM Toy t JOIN FETCH t.user WHERE t.campaignId = :campaignId")
List<Toy> findToysWithUser(@Param("campaignId") Long campaignId);

// ✅ Hoặc sử dụng DTO Projection
@Query("SELECT new com.example.ToyUserDTO(t.id, t.name, u.name) " +
       "FROM Toy t JOIN User u ON t.userId = u.id " +
       "WHERE t.campaignId = :campaignId")
List<ToyUserDTO> findToyUserProjections(@Param("campaignId") Long campaignId);
```

### 6.2 Batch Operations
```java
// ✅ Bulk update thay vì multiple single updates
@Modifying
@Query("UPDATE Toy t SET t.status = :status WHERE t.id IN :ids")
int updateToysStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

// ✅ Batch insert với saveAll()
@Transactional
public void createMultipleToys(List<Toy> toys) {
    // Spring Data JPA tự động batch nếu hibernate.jdbc.batch_size > 0
    toyRepository.saveAll(toys);
}
```

### 6.3 Pagination Best Practices
```java
// ✅ Sử dụng Pageable với sort
public Page<Toy> getToys(int page, int size, String sortBy) {
    Pageable pageable = PageRequest.of(page, size, 
                                     Sort.by(Sort.Direction.DESC, sortBy));
    return toyRepository.findAll(pageable);
}

// ✅ Cursor-based pagination cho large datasets
public List<Toy> getToysAfter(Long lastId, int limit) {
    return toyRepository.findByIdGreaterThanOrderByIdAsc(lastId, 
                                                        PageRequest.of(0, limit));
}
```

## 7. Transaction Management

### 7.1 Declarative Transactions
```java
@Service
@Transactional(readOnly = true) // Default cho toàn class
public class ToyApplicationServiceImpl {
    
    @Transactional // Override cho write operations
    public Toy createToy(CreateToyRequest request) {
        // Business logic here
        return toyRepository.save(toy);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditToyCreation(Long toyId) {
        // Separate transaction cho audit log
    }
}
```

### 7.2 Transaction Best Practices
```java
// ✅ Keep transactions short
@Transactional
public void processGiveaway(Long campaignId) {
    List<Toy> toys = toyRepository.findByCampaignId(campaignId);
    
    // Process in batches để tránh long-running transaction
    for (int i = 0; i < toys.size(); i += BATCH_SIZE) {
        List<Toy> batch = toys.subList(i, Math.min(i + BATCH_SIZE, toys.size()));
        processBatch(batch);
    }
}

// ✅ Use read-only transactions cho queries
@Transactional(readOnly = true)
public List<Toy> searchToys(ToySearchCriteria criteria) {
    Specification<Toy> spec = ToySpecification.searchCriteria(
        criteria.getUserId(), criteria.getCampaignId(), 
        criteria.getKeyword(), criteria.getStatus()
    );
    return toyRepository.findAll(spec, criteria.getPageable());
}
```

## 8. Caching Strategy

### 8.1 JPA Second Level Cache
```java
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Toy {
    // Entity definition
}

// Query cache
@Query("SELECT t FROM Toy t WHERE t.status = :status")
@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
List<Toy> findCacheableToysByStatus(@Param("status") Integer status);
```

### 8.2 Spring Cache Integration
```java
@Service
public class ToyApplicationServiceImpl {
    
    @Cacheable(value = "toys", key = "#id")
    public ToyDTO getToyById(Long id) {
        return toyMapper.toDTO(toyRepository.findById(id));
    }
    
    @CacheEvict(value = "toys", key = "#toy.id")
    public Toy updateToy(Toy toy) {
        return toyRepository.save(toy);
    }
    
    @Cacheable(value = "campaignToys", key = "#campaignId + '_' + #pageable.pageNumber")
    public List<ToyDTO> getToysByCampaign(Long campaignId, Pageable pageable) {
        return toyRepository.findByCampaignId(campaignId, pageable)
                           .stream()
                           .map(toyMapper::toDTO)
                           .collect(toList());
    }
}
```

## 9. Common Pitfalls & Solutions

### 9.1 LazyInitializationException
```java
// ❌ Problem
@Transactional(readOnly = true)
public ToyDTO getToy(Long id) {
    Toy toy = toyRepository.findById(id);
    // Session closed here
    return new ToyDTO(toy.getName(), toy.getUser().getName()); // LazyInitializationException!
}

// ✅ Solution 1: Fetch Join
@Query("SELECT t FROM Toy t JOIN FETCH t.user WHERE t.id = :id")
Optional<Toy> findByIdWithUser(@Param("id") Long id);

// ✅ Solution 2: DTO Projection
@Query("SELECT new ToyUserDTO(t.name, u.name) FROM Toy t JOIN User u ON t.userId = u.id WHERE t.id = :id")
ToyUserDTO findToyUserDTO(@Param("id") Long id);
```

### 9.2 Cartesian Product Problem
```java
// ❌ Multiple fetch joins causing cartesian product
@Query("SELECT t FROM Toy t " +
       "JOIN FETCH t.user " +
       "JOIN FETCH t.photos " +
       "JOIN FETCH t.participations") // Cartesian product!

// ✅ Solution: Separate queries hoặc @EntityGraph
@EntityGraph(attributePaths = {"user", "photos"})
@Query("SELECT t FROM Toy t WHERE t.id = :id")
Optional<Toy> findByIdWithUserAndPhotos(@Param("id") Long id);
```

## 10. Kết Luận

### Key Takeaways:
1. **Repository Pattern**: Tách biệt domain logic và infrastructure concerns
2. **Specification Pattern**: Dynamic queries type-safe và maintainable  
3. **Performance**: Luôn monitor query performance và optimize
4. **Caching**: Sử dụng multi-level caching strategy
5. **Testing**: Test cả unit và integration với real database

### Next Steps:
- Implement query optimization monitoring
- Add more comprehensive caching strategy
- Consider read replicas cho scaling
- Evaluate query performance với production data volume

### So Sánh với MyBatis-Plus:

| Aspect | Spring Data JPA | MyBatis-Plus |
|--------|----------------|--------------|
| **Learning Curve** | Moderate (ORM concepts) | Easy (SQL familiar) |
| **Type Safety** | Strong (Criteria API) | Moderate (Code generation) |
| **Performance** | Good (with optimization) | Excellent (direct SQL) |
| **Maintenance** | High (automatic queries) | Medium (manual SQL) |
| **Complex Queries** | Challenging | Natural |
| **Domain Modeling** | Excellent | Limited |

### Khi Nào Dùng Gì:
- **Spring Data JPA**: Domain-rich applications, rapid development, team familiar với ORM
- **MyBatis-Plus**: Performance-critical, complex SQL requirements, legacy database schemas

---
*Tài liệu này sẽ được cập nhật thường xuyên khi có thêm best practices và lessons learned từ production.*