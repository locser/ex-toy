# ORM Frameworks Comparison: Prisma vs TypeORM vs Sequelize vs Hibernate

## 🎯 Tổng Quan 4 ORM Frameworks

| **Framework** | **Language** | **Type** | **Philosophy** | **Best For** |
|---------------|--------------|----------|----------------|--------------|
| **Prisma** | TypeScript/JavaScript | Modern ORM | Schema-first, Type-safe | Modern apps, GraphQL |
| **TypeORM** | TypeScript/JavaScript | Traditional ORM | Code-first, Decorator-based | Enterprise TypeScript |
| **Sequelize** | JavaScript/TypeScript | Traditional ORM | Model-based, Promise-driven | Node.js apps |
| **Hibernate** | Java | Mature ORM | Annotation-based, JPA standard | Enterprise Java |

---

## 🔍 Chi Tiết Từng Framework

### 1. **Hibernate (Java) - Hiện tại trong codebase**

#### **Characteristics:**
- **Mature**: 20+ years development
- **JPA Standard**: Java Persistence API compliance
- **Annotation-based**: Declarative mapping
- **Enterprise-grade**: Production-proven

#### **Code Example từ codebase:**
```java
@Entity
@Table(name = "toys")
@Getter
@Setter
public class Toy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

// Repository pattern
public interface ToyJPAMapper extends JpaRepository<Toy, Long>, JpaSpecificationExecutor<Toy> {
    List<Toy> findByUserId(Long userId);
    
    @Query("SELECT t FROM Toy t WHERE t.status = :status")
    List<Toy> findByStatus(@Param("status") Integer status);
}
```

#### **Pros:**
- ✅ **Mature ecosystem**: Extensive documentation, community
- ✅ **Performance**: Advanced caching (L1, L2), lazy loading
- ✅ **Enterprise features**: Transaction management, connection pooling
- ✅ **Type safety**: Compile-time checking
- ✅ **Standards compliance**: JPA standard

#### **Cons:**
- ❌ **Learning curve**: Complex configuration, annotations
- ❌ **Boilerplate**: Verbose entity definitions
- ❌ **Runtime errors**: Some issues only appear at runtime
- ❌ **N+1 problem**: Requires careful query optimization

---

### 2. **Prisma (TypeScript/JavaScript)**

#### **Characteristics:**
- **Schema-first**: Database schema drives code generation
- **Type-safe**: Auto-generated TypeScript types
- **Modern**: Built for modern development workflows
- **Developer experience**: Excellent tooling

#### **Code Example:**
```prisma
// schema.prisma
model Toy {
  id          Int      @id @default(autoincrement())
  userId      Int      @map("user_id")
  campaignId  Int      @map("campaign_id")
  name        String
  description String?
  category    String
  condition   Int      @default(0)
  status      Int      @default(0)
  createdAt   DateTime @default(now()) @map("created_at")
  updatedAt   DateTime @updatedAt @map("updated_at")
  
  user        User     @relation(fields: [userId], references: [id])
  
  @@map("toys")
}

model User {
  id       Int    @id @default(autoincrement())
  email    String @unique
  name     String
  toys     Toy[]
  
  @@map("users")
}
```

```typescript
// Auto-generated client usage
import { PrismaClient } from '@prisma/client'

const prisma = new PrismaClient()

// Type-safe queries
async function getToysByUser(userId: number) {
  return await prisma.toy.findMany({
    where: { userId },
    include: {
      user: true  // Auto-join with type safety
    }
  })
}

// Complex queries with type safety
async function getToysWithFilters(filters: {
  category?: string
  status?: number
  userId?: number
}) {
  return await prisma.toy.findMany({
    where: {
      category: filters.category,
      status: filters.status,
      userId: filters.userId
    },
    orderBy: { createdAt: 'desc' },
    take: 10
  })
}
```

#### **Pros:**
- ✅ **Type safety**: 100% type-safe at compile time
- ✅ **Developer experience**: Excellent tooling, auto-completion
- ✅ **Schema management**: Database migrations handled automatically
- ✅ **Performance**: Optimized queries, connection pooling
- ✅ **Modern**: Built for modern JavaScript/TypeScript ecosystem

#### **Cons:**
- ❌ **Vendor lock-in**: Prisma-specific syntax
- ❌ **Limited flexibility**: Less control over SQL generation
- ❌ **Newer ecosystem**: Smaller community compared to others
- ❌ **Migration complexity**: Complex schema changes can be challenging

---

### 3. **TypeORM (TypeScript)**

#### **Characteristics:**
- **Decorator-based**: Similar to Hibernate but for TypeScript
- **Active Record/Data Mapper**: Multiple patterns supported
- **Enterprise-ready**: Advanced features for large applications
- **TypeScript-first**: Built specifically for TypeScript

#### **Code Example:**
```typescript
// Entity definition
import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, OneToMany, CreateDateColumn, UpdateDateColumn } from 'typeorm'

@Entity('toys')
export class Toy {
  @PrimaryGeneratedColumn()
  id: number

  @Column({ name: 'user_id' })
  userId: number

  @Column({ name: 'campaign_id', default: 0 })
  campaignId: number

  @Column()
  name: string

  @Column({ type: 'text', nullable: true })
  description: string

  @Column({ default: 0 })
  condition: number

  @Column({ default: 0 })
  status: number

  @CreateDateColumn({ name: 'created_at' })
  createdAt: Date

  @UpdateDateColumn({ name: 'updated_at' })
  updatedAt: Date

  // Relations
  @ManyToOne(() => User, user => user.toys)
  user: User

  @OneToMany(() => ToyParticipation, participation => participation.toy)
  participations: ToyParticipation[]
}

// Repository usage
import { Repository } from 'typeorm'

export class ToyService {
  constructor(
    private toyRepository: Repository<Toy>
  ) {}

  async findByUserId(userId: number): Promise<Toy[]> {
    return await this.toyRepository.find({
      where: { userId },
      relations: ['user', 'participations']
    })
  }

  async findWithComplexQuery(): Promise<Toy[]> {
    return await this.toyRepository
      .createQueryBuilder('toy')
      .leftJoinAndSelect('toy.user', 'user')
      .where('toy.status = :status', { status: 1 })
      .andWhere('user.isAdmin = :isAdmin', { isAdmin: false })
      .orderBy('toy.createdAt', 'DESC')
      .take(10)
      .getMany()
  }
}
```

#### **Pros:**
- ✅ **TypeScript native**: Excellent TypeScript integration
- ✅ **Flexible**: Multiple patterns (Active Record, Data Mapper)
- ✅ **Advanced features**: Query builder, migrations, caching
- ✅ **Familiar**: Similar to Hibernate for Java developers
- ✅ **Database agnostic**: Supports multiple databases

#### **Cons:**
- ❌ **Complexity**: Can become complex for large applications
- ❌ **Performance**: Not as optimized as Prisma
- ❌ **Documentation**: Sometimes inconsistent
- ❌ **Breaking changes**: History of breaking changes between versions

---

### 4. **Sequelize (JavaScript/TypeScript)**

#### **Characteristics:**
- **Promise-based**: Async/await friendly
- **Model-based**: Traditional ORM approach
- **Mature**: Long-standing Node.js ORM
- **Feature-rich**: Comprehensive feature set

#### **Code Example:**
```javascript
// Model definition
const { DataTypes, Model } = require('sequelize')

class Toy extends Model {
  static init(sequelize) {
    return super.init({
      id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true
      },
      userId: {
        type: DataTypes.INTEGER,
        allowNull: false,
        field: 'user_id'
      },
      campaignId: {
        type: DataTypes.INTEGER,
        defaultValue: 0,
        field: 'campaign_id'
      },
      name: {
        type: DataTypes.STRING,
        allowNull: false
      },
      description: {
        type: DataTypes.TEXT
      },
      condition: {
        type: DataTypes.INTEGER,
        defaultValue: 0
      },
      status: {
        type: DataTypes.INTEGER,
        defaultValue: 0
      }
    }, {
      sequelize,
      tableName: 'toys',
      timestamps: true,
      createdAt: 'created_at',
      updatedAt: 'updated_at'
    })
  }

  static associate(models) {
    this.belongsTo(models.User, { foreignKey: 'userId', as: 'user' })
    this.hasMany(models.ToyParticipation, { foreignKey: 'toyId', as: 'participations' })
  }
}

// Usage
class ToyService {
  async findByUserId(userId) {
    return await Toy.findAll({
      where: { userId },
      include: [
        { model: User, as: 'user' },
        { model: ToyParticipation, as: 'participations' }
      ]
    })
  }

  async findWithComplexQuery() {
    return await Toy.findAll({
      include: [{
        model: User,
        as: 'user',
        where: { isAdmin: false }
      }],
      where: { status: 1 },
      order: [['created_at', 'DESC']],
      limit: 10
    })
  }
}
```

#### **Pros:**
- ✅ **Mature**: Battle-tested in production
- ✅ **Feature-rich**: Comprehensive ORM features
- ✅ **Database support**: Wide range of database support
- ✅ **Community**: Large community and ecosystem
- ✅ **Flexible**: Good balance of features and simplicity

#### **Cons:**
- ❌ **TypeScript support**: Not native, requires additional setup
- ❌ **Performance**: Can be slower than newer alternatives
- ❌ **API inconsistency**: Some APIs feel dated
- ❌ **Memory usage**: Can be memory-intensive for large datasets

---

## 📊 Performance Comparison

### **Query Performance:**
```
Prisma:    ████████████████████████ (Excellent - Optimized queries)
Hibernate: ███████████████████████  (Excellent - Mature optimizations)
TypeORM:   ████████████████████     (Good - Decent performance)
Sequelize: ██████████████████       (Good - Adequate performance)
```

### **Developer Experience:**
```
Prisma:    ████████████████████████ (Excellent - Modern tooling)
TypeORM:   ███████████████████████  (Excellent - TypeScript native)
Hibernate: ████████████████████     (Good - Mature but verbose)
Sequelize: ██████████████████       (Good - Traditional approach)
```

### **Type Safety:**
```
Prisma:    ████████████████████████ (Perfect - 100% type-safe)
TypeORM:   ███████████████████████  (Excellent - Native TypeScript)
Hibernate: ███████████████████████  (Excellent - Java type system)
Sequelize: ████████████             (Limited - Requires extra setup)
```

---

## 🎯 Migration Scenarios

### **From Hibernate to Prisma:**
```typescript
// Hibernate Entity
@Entity
@Table(name = "toys")
public class Toy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
}

// Equivalent Prisma Schema
model Toy {
  id     Int @id @default(autoincrement())
  userId Int @map("user_id")
  
  @@map("toys")
}
```

### **From Hibernate to TypeORM:**
```typescript
// Very similar syntax
@Entity('toys')
export class Toy {
  @PrimaryGeneratedColumn()
  id: number
  
  @Column({ name: 'user_id' })
  userId: number
}
```

---

## 🚀 Recommendations

### **Choose Prisma if:**
- ✅ Building new TypeScript/JavaScript applications
- ✅ Want maximum type safety and developer experience
- ✅ Prefer schema-first approach
- ✅ Working with GraphQL APIs
- ✅ Team values modern tooling

### **Choose TypeORM if:**
- ✅ Migrating from Java/Hibernate to TypeScript
- ✅ Need enterprise-grade features
- ✅ Want flexibility in query building
- ✅ Have complex domain models
- ✅ Team familiar with decorator patterns

### **Choose Sequelize if:**
- ✅ Working with legacy JavaScript applications
- ✅ Need wide database support
- ✅ Team familiar with traditional ORMs
- ✅ Gradual migration from JavaScript to TypeScript
- ✅ Budget constraints (free, open source)

### **Stick with Hibernate if:**
- ✅ Java ecosystem and team expertise
- ✅ Enterprise requirements and compliance
- ✅ Complex business logic and transactions
- ✅ Existing large codebase
- ✅ Performance-critical applications

---

## 🔄 Migration Strategy Example

### **Hibernate → Prisma Migration:**

#### **Step 1: Schema Analysis**
```sql
-- Current database schema (from Hibernate)
CREATE TABLE toys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    campaign_id BIGINT DEFAULT 0,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### **Step 2: Prisma Schema Generation**
```bash
# Generate Prisma schema from existing database
npx prisma db pull
npx prisma generate
```

#### **Step 3: Gradual Migration**
```typescript
// Phase 1: Read operations with Prisma
const toys = await prisma.toy.findMany({
  where: { userId: 123 }
})

// Phase 2: Write operations with Prisma
const newToy = await prisma.toy.create({
  data: {
    userId: 123,
    name: "New Toy",
    description: "Description"
  }
})

// Phase 3: Complete migration
// Remove Hibernate dependencies
```

---

## 📈 Performance Optimization Tips

### **Hibernate (Current):**
```java
// Enable query caching
@Cacheable
@Query("SELECT t FROM Toy t WHERE t.userId = :userId")
List<Toy> findByUserId(@Param("userId") Long userId);

// Batch fetching
@BatchSize(size = 10)
@OneToMany(mappedBy = "toy", fetch = FetchType.LAZY)
private List<ToyParticipation> participations;
```

### **Prisma:**
```typescript
// Connection pooling
const prisma = new PrismaClient({
  datasources: {
    db: {
      url: "mysql://user:pass@localhost:3306/db?connection_limit=20&pool_timeout=20"
    }
  }
})

// Query optimization
const toys = await prisma.toy.findMany({
  select: {
    id: true,
    name: true,
    user: {
      select: { name: true }
    }
  }
})
```

---

**Conclusion**: Mỗi ORM có strengths riêng. Hibernate excellent cho enterprise Java, Prisma tuyệt vời cho modern TypeScript, TypeORM good balance cho TypeScript enterprise, và Sequelize solid choice cho traditional Node.js applications.