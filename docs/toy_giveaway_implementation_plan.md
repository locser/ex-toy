# Toy Giveaway Campaign - Implementation Plan

## Tổng Quan

Kế hoạch triển khai chức năng **Toy Giveaway Campaign** sử dụng lại entities hiện có (`Toy`, `Event`) thay vì tạo mới.

## Kiến Trúc Tổng Thể

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controller    │    │   Application   │    │     Domain      │
│     Layer       │───▶│     Layer       │───▶│     Layer       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Infrastructure  │    │     Cache       │    │   Database      │
│     Layer       │    │    (Redis)      │    │ (PostgreSQL)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Database Schema Changes

### 1. Event Table - Thêm type column
```sql
-- Migration: add_event_type.sql
ALTER TABLE events ADD COLUMN type INT NOT NULL DEFAULT 1;
CREATE INDEX idx_events_type ON events(type);

-- Update existing events to EXCHANGE type
UPDATE events SET type = 1 WHERE type IS NULL;
```

### 2. Toy Status - Extend enum
```java
// ToyStatus.java - thêm values
GIVEAWAY_AVAILABLE(6),  // Available for giveaway
GIVEAWAY_CLAIMED(7);    // Claimed in giveaway campaign
```

### 3. ToyParticipation Table - New
```sql
-- Migration: create_toy_participations.sql
CREATE TABLE toy_participations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    toy_id BIGINT NOT NULL,
    campaign_id BIGINT NOT NULL,
    participation_date DATETIME NOT NULL,
    status INT NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_user_campaign (user_id, campaign_id),
    INDEX idx_user_id (user_id),
    INDEX idx_toy_id (toy_id),
    INDEX idx_campaign_id (campaign_id),
    INDEX idx_participation_date (participation_date),
    
    FOREIGN KEY (toy_id) REFERENCES toys(id),
    FOREIGN KEY (campaign_id) REFERENCES events(id)
);
```

## Level 1: Basic Implementation

### Domain Layer Changes

#### 1. Enums
```java
// EventType.java - New enum
public enum EventType implements ValueEnum {
    EXCHANGE(1),
    GIVEAWAY(2);
    
    private final int value;
    // implementation...
}

// ToyStatus.java - Extend existing
public enum ToyStatus {
    AVAILABLE(0),
    PENDING_EXCHANGE(1),
    IN_EXCHANGE(2),
    EXCHANGED(3),
    REMOVED(4),
    AWAITING_APPROVAL(5),
    GIVEAWAY_AVAILABLE(6),  // NEW
    GIVEAWAY_CLAIMED(7);    // NEW
}

// ToyParticipationStatus.java - New enum
public enum ToyParticipationStatus implements ValueEnum {
    CLAIMED(1),
    DELIVERED(2),
    CANCELLED(3);
}
```

#### 2. Entities

**Event Entity - Thêm type field**
```java
@Entity
@Table(name = "events")
public class Event extends DateAudit {
    // existing fields...
    
    @Column(name = "type", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer type = EventType.EXCHANGE.getValue();
    
    // getters/setters...
}
```

**ToyParticipation Entity - New**
```java
@Entity
@Table(name = "toy_participations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ToyParticipation extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "toy_id", nullable = false)
    private Long toyId;
    
    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;
    
    @Column(name = "participation_date", nullable = false)
    private LocalDateTime participationDate;
    
    @Column(name = "status", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer status = ToyParticipationStatus.CLAIMED.getValue();
}
```

#### 3. Domain Services

**GiveawayCampaignDomainService - New**
```java
public interface GiveawayCampaignDomainService {
    Event createGiveawayCampaign(Event campaign);
    Event getGiveawayCampaignById(Long id);
    List<Toy> addToysToGiveawayCampaign(Long campaignId, List<Long> toyIds);
    ToyParticipation participateInGiveaway(Long userId, Long campaignId);
    boolean canParticipate(Long userId, Long campaignId);
    List<Toy> getAvailableToysInCampaign(Long campaignId);
    void updateCampaignStatus(Long campaignId, Integer status);
}
```

**Implementation Notes:**
```java
@Service
public class GiveawayCampaignDomainServiceImpl implements GiveawayCampaignDomainService {
    
    @Override
    @Transactional
    public ToyParticipation participateInGiveaway(Long userId, Long campaignId) {
        // 1. Validate campaign is active and type = GIVEAWAY
        Event campaign = getGiveawayCampaignById(campaignId);
        validateCampaignActive(campaign);
        
        // 2. Check user hasn't participated
        if (toyParticipationRepository.existsByUserIdAndCampaignId(userId, campaignId)) {
            throw new BadRequestException("User already participated");
        }
        
        // 3. Get random available toy
        List<Toy> availableToys = toyRepository.findByCampaignIdAndStatus(
            campaignId, ToyStatus.GIVEAWAY_AVAILABLE.getValue());
        
        if (availableToys.isEmpty()) {
            throw new BadRequestException("No toys available");
        }
        
        Toy selectedToy = availableToys.get(0); // or random selection
        
        // 4. Atomic update
        selectedToy.setUserId(userId);
        selectedToy.setStatus(ToyStatus.GIVEAWAY_CLAIMED.getValue());
        toyRepository.save(selectedToy);
        
        // 5. Create participation record
        ToyParticipation participation = new ToyParticipation();
        participation.setUserId(userId);
        participation.setToyId(selectedToy.getId());
        participation.setCampaignId(campaignId);
        participation.setParticipationDate(LocalDateTime.now());
        
        return toyParticipationRepository.save(participation);
    }
}
```

### Application Layer

#### DTOs
```java
// CreateGiveawayCampaignRequest.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGiveawayCampaignRequest {
    @NotBlank(message = "Tên chiến dịch không được để trống")
    private String name;
    
    private String description;
    
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;
    
    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;
    
    private String theme;
    private String rules;
}

// ToyParticipationDTO.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToyParticipationDTO {
    private Long id;
    private Long userId;
    private Long toyId;
    private Long campaignId;
    private String campaignName;
    private ToyDTO toy;
    private LocalDateTime participationDate;
    private Integer status;
    private String statusText;
}
```

#### Application Service
```java
@Service
public class GiveawayCampaignApplicationServiceImpl implements GiveawayCampaignApplicationService {
    
    @Override
    public EventDTO createGiveawayCampaign(CreateGiveawayCampaignRequest request) {
        Event campaign = new Event();
        campaign.setName(request.getName());
        campaign.setDescription(request.getDescription());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());
        campaign.setTheme(request.getTheme());
        campaign.setRules(request.getRules());
        campaign.setType(EventType.GIVEAWAY.getValue());
        
        Event savedCampaign = giveawayCampaignDomainService.createGiveawayCampaign(campaign);
        return mapToEventDTO(savedCampaign);
    }
    
    @Override
    public ToyParticipationDTO participateInGiveaway(Long userId, Long campaignId) {
        ToyParticipation participation = giveawayCampaignDomainService
            .participateInGiveaway(userId, campaignId);
        return mapToParticipationDTO(participation);
    }
}
```

### Infrastructure Layer

#### Repositories
```java
// ToyParticipationRepository.java
public interface ToyParticipationRepository {
    Optional<ToyParticipation> findOneById(Long id);
    ToyParticipation save(ToyParticipation participation);
    boolean existsByUserIdAndCampaignId(Long userId, Long campaignId);
    List<ToyParticipation> findByUserId(Long userId, int page, int limit);
    List<ToyParticipation> findByCampaignId(Long campaignId);
    long countByCampaignId(Long campaignId);
}

// EventRepository - Extend existing
public interface EventRepository {
    // existing methods...
    
    List<Event> findByType(Integer type, int page, int limit);
    Optional<Event> findByIdAndType(Long id, Integer type);
    long countByType(Integer type);
}

// ToyRepository - Extend existing  
public interface ToyRepository {
    // existing methods...
    
    List<Toy> findByCampaignIdAndStatus(Long campaignId, Integer status);
    long countByCampaignIdAndStatus(Long campaignId, Integer status);
    int updateStatusByCampaignId(Long campaignId, Integer oldStatus, Integer newStatus);
}
```

### Controller Layer

```java
@RestController
@RequestMapping("/api/v1")
public class GiveawayCampaignController {
    
    @PostMapping("/admin/giveaway-campaigns")
    public BaseResponse<EventResponseDTO> createGiveawayCampaign(
        @Valid @RequestBody CreateGiveawayCampaignRequestDTO request) {
        
        EventDTO campaign = giveawayCampaignService.createGiveawayCampaign(
            GiveawayCampaignDTOMapper.toCreateRequest(request));
        
        return BaseResponse.success(
            GiveawayCampaignDTOMapper.toEventResponseDTO(campaign),
            "Tạo chiến dịch thành công"
        );
    }
    
    @PostMapping("/admin/giveaway-campaigns/{campaignId}/toys")
    public BaseResponse<String> addToysToGiveaway(
        @PathVariable @ValidId Long campaignId,
        @Valid @RequestBody AddToysToGiveawayRequestDTO request) {
        
        giveawayCampaignService.addToysToGiveaway(campaignId, request.getToyIds());
        return BaseResponse.success("Thêm đồ chơi vào chiến dịch thành công");
    }
    
    @PostMapping("/giveaway-campaigns/{campaignId}/participate")
    public BaseResponse<ToyParticipationResponseDTO> participateInGiveaway(
        @PathVariable @ValidId Long campaignId,
        @RequestHeader("X-User-Id") Long userId) {
        
        ToyParticipationDTO participation = giveawayCampaignService
            .participateInGiveaway(userId, campaignId);
        
        return BaseResponse.success(
            GiveawayCampaignDTOMapper.toParticipationResponseDTO(participation),
            "Nhận đồ chơi thành công"
        );
    }
}
```

## Testing Strategy

### Unit Tests
```java
@ExtendWith(MockitoExtension.class)
class GiveawayCampaignDomainServiceImplTest {
    
    @Test
    void participateInGiveaway_Success() {
        // Given: Active campaign with available toys
        // When: User participates
        // Then: Toy is claimed and participation recorded
    }
    
    @Test
    void participateInGiveaway_AlreadyParticipated_ThrowsException() {
        // Given: User already participated
        // When: User tries to participate again
        // Then: BadRequestException is thrown
    }
    
    @Test
    void participateInGiveaway_NoToysAvailable_ThrowsException() {
        // Given: Campaign with no available toys
        // When: User tries to participate
        // Then: BadRequestException is thrown
    }
}
```

### Integration Tests
```java
@SpringBootTest
@Transactional
class GiveawayCampaignIntegrationTest {
    
    @Test
    void participateInGiveaway_ConcurrentUsers_OnlyOneSucceeds() {
        // Test concurrent participation with only 1 toy available
        // Only 1 user should succeed, others should get "no toys available"
    }
}
```

## Deployment Checklist

### Database Migration
- [ ] Run migration to add `type` column to events table
- [ ] Create `toy_participations` table
- [ ] Add necessary indexes
- [ ] Update existing events to have type = EXCHANGE

### Application Deployment
- [ ] Deploy new code with feature flag disabled
- [ ] Run database migrations
- [ ] Enable feature flag
- [ ] Monitor for errors

### Rollback Plan
- [ ] Disable feature flag
- [ ] Revert code deployment if needed
- [ ] Database rollback scripts ready

## Success Criteria Level 1

- ✅ Create giveaway campaigns
- ✅ Add toys to campaigns  
- ✅ Users can participate and claim toys
- ✅ No duplicate participation per user per campaign
- ✅ Atomic toy claiming (no race conditions)
- ✅ API response time < 500ms
- ✅ Support 100 concurrent users
- ✅ Test coverage > 80%
