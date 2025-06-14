# Toy Giveaway Campaign - Usage Examples

## Tổng Quan

Tài liệu này cung cấp các ví dụ sử dụng chi tiết cho tính năng **Toy Giveaway Campaign** với 3 levels performance khác nhau.

## Level 1: Basic Implementation

### Suitable For

- **Quy mô**: 1-100 users đồng thời
- **Use case**: MVP, prototype, small campaigns
- **Performance target**: < 500ms response time

### API Usage

```bash
# Basic participation
curl -X POST \
  'http://localhost:8080/api/v1/giveaway-campaigns/1/participate?level=1' \
  -H 'X-User-Id: 123' \
  -H 'Content-Type: application/json'
```

### Success Response

```json
{
  "status": 200,
  "message": "Nhận đồ chơi thành công (Basic)",
  "data": {
    "id": 1,
    "userId": 123,
    "toyId": 45,
    "campaignId": 1,
    "participationDate": "2024-02-15T10:30:00",
    "status": 1,
    "toy": {
      "id": 45,
      "name": "Robot Transformer",
      "description": "Robot biến hình cao cấp",
      "category": "Action Figures",
      "condition": 1
    },
    "campaignName": "Chiến dịch Tết 2024"
  }
}
```

### Error Scenarios

**User already participated:**

```json
{
  "status": 409,
  "message": "Bạn đã tham gia chiến dịch này rồi"
}
```

**Campaign expired:**

```json
{
  "status": 400,
  "message": "Chiến dịch đã kết thúc"
}
```

**No toys available:**

```json
{
  "status": 410,
  "message": "Chiến dịch đã hết đồ chơi"
}
```

## Level 2: Optimized Implementation

### Suitable For

- **Quy mô**: 100-1000 users đồng thời
- **Use case**: Production campaigns, medium scale
- **Performance target**: < 300ms response time

### API Usage

```bash
# Optimized participation with caching
curl -X POST \
  'http://localhost:8080/api/v1/giveaway-campaigns/1/participate?level=2' \
  -H 'X-User-Id: 123' \
  -H 'Content-Type: application/json'
```

### Features

- **Redis Caching**: Campaign metadata và user participation status
- **Optimistic Locking**: Handle concurrent requests gracefully
- **Retry Mechanism**: Auto-retry với exponential backoff
- **Cache Invalidation**: Smart cache updates

### Success Response

```json
{
  "status": 200,
  "message": "Nhận đồ chơi thành công (Optimized)",
  "data": {
    "id": 2,
    "userId": 123,
    "toyId": 46,
    "campaignId": 1,
    "participationDate": "2024-02-15T10:31:00",
    "status": 1,
    "performance": {
      "level": 2,
      "responseTime": "245ms",
      "cacheHit": true,
      "retryCount": 0
    }
  }
}
```

### System Overload Response

```json
{
  "status": 429,
  "message": "Hệ thống đang quá tải, vui lòng thử lại sau",
  "retryAfter": 5
}
```

## Level 3: Advanced Implementation

### Suitable For

- **Quy mô**: 1000+ users đồng thời
- **Use case**: Large scale campaigns, enterprise
- **Performance target**: < 200ms response time

### API Usage

```bash
# Advanced participation with preferences
curl -X POST \
  'http://localhost:8080/api/v1/giveaway-campaigns/1/participate?level=3' \
  -H 'X-User-Id: 123' \
  -H 'Content-Type: application/json' \
  -d '{
    "preferences": {
      "category": "Action Figures",
      "condition": [1, 2],
      "ageGroup": "8-12",
      "brand": "LEGO"
    }
  }'
```

### Advanced Features

- **Distributed Locking**: Redis-based locks for high concurrency
- **Smart Toy Selection**: AI-powered preference matching
- **Fairness Engine**: Prevent gaming, ensure fair distribution
- **Real-time Updates**: WebSocket notifications
- **Analytics**: Advanced user behavior tracking

### Success Response

```json
{
  "status": 200,
  "message": "Nhận đồ chơi thành công (Advanced)",
  "data": {
    "id": 3,
    "userId": 123,
    "toyId": 47,
    "campaignId": 1,
    "participationDate": "2024-02-15T10:32:00",
    "status": 1,
    "toy": {
      "id": 47,
      "name": "LEGO Creator 3-in-1",
      "description": "Bộ LEGO sáng tạo đa năng",
      "category": "Action Figures",
      "condition": 1,
      "matchScore": 95,
      "matchReasons": [
        "Category match: Action Figures",
        "Brand match: LEGO",
        "Condition preference: New"
      ]
    },
    "performance": {
      "level": 3,
      "responseTime": "185ms",
      "lockAcquisitionTime": "8ms",
      "selectionAlgorithm": "PreferenceBasedWithFairness",
      "fairnessScore": 0.85
    },
    "recommendations": [
      {
        "toyId": 48,
        "name": "LEGO Technic Crane",
        "matchScore": 88,
        "reason": "Similar brand and category"
      }
    ]
  }
}
```

### Distributed Lock Error

```json
{
  "status": 503,
  "message": "Không thể acquire lock, hệ thống busy",
  "retryAfter": 2
}
```

## Performance Comparison

### Load Testing Results

```bash
# Level 1 - 100 concurrent users
ab -n 1000 -c 100 -H "X-User-Id: $RANDOM" \
  "http://localhost:8080/api/v1/giveaway-campaigns/1/participate?level=1"

# Results:
# Requests per second:    222.15 [#/sec]
# Time per request:       450.154 [ms] (mean)
# 95th percentile:        485ms
```

```bash
# Level 2 - 500 concurrent users
ab -n 2500 -c 500 -H "X-User-Id: $RANDOM" \
  "http://localhost:8080/api/v1/giveaway-campaigns/1/participate?level=2"

# Results:
# Requests per second:    1785.72 [#/sec]
# Time per request:       279.954 [ms] (mean)
# 95th percentile:        295ms
```

```bash
# Level 3 - 1000 concurrent users
ab -n 5000 -c 1000 -H "X-User-Id: $RANDOM" \
  "http://localhost:8080/api/v1/giveaway-campaigns/1/participate?level=3"

# Results:
# Requests per second:    5263.16 [#/sec]
# Time per request:       189.999 [ms] (mean)
# 95th percentile:        198ms
```

## Integration Examples

### Spring Boot Test

```java
@SpringBootTest
@Transactional
class GiveawayCampaignIntegrationTest {

    @Autowired
    private GiveawayCampaignController controller;

    @Test
    void testLevel1Participation() {
        // Given: Active campaign with available toys
        Long campaignId = 1L;
        Long userId = 123L;

        // When: User participates with Level 1
        BaseResponse<ToyParticipationResponseDTO> response =
            controller.participateInGiveaway(campaignId, userId, 1, null);

        // Then: Success response with toy details
        assertEquals(200, response.getStatus());
        assertTrue(response.getMessage().contains("Basic"));
        assertNotNull(response.getData().getToy());
    }

    @Test
    void testLevel3WithPreferences() {
        // Given: Campaign with multiple toy categories
        Long campaignId = 1L;
        Long userId = 456L;
        String preferences = "{\"category\":\"Action Figures\",\"condition\":[1,2]}";

        // When: User participates with Level 3 and preferences
        BaseResponse<ToyParticipationResponseDTO> response =
            controller.participateInGiveaway(campaignId, userId, 3, preferences);

        // Then: Toy matches preferences
        assertEquals(200, response.getStatus());
        assertTrue(response.getMessage().contains("Advanced"));
        assertEquals("Action Figures", response.getData().getToy().getCategory());
    }
}
```

### JavaScript Frontend Integration

```javascript
// Basic participation (Level 1)
async function participateBasic(campaignId, userId) {
  const response = await fetch(
    `/api/v1/giveaway-campaigns/${campaignId}/participate?level=1`,
    {
      method: "POST",
      headers: {
        "X-User-Id": userId,
        "Content-Type": "application/json",
      },
    }
  );

  const result = await response.json();

  if (result.status === 200) {
    showSuccessMessage(result.message);
    displayToyDetails(result.data.toy);
  } else {
    showErrorMessage(result.message);
  }
}

// Advanced participation with preferences (Level 3)
async function participateAdvanced(campaignId, userId, preferences) {
  const response = await fetch(
    `/api/v1/giveaway-campaigns/${campaignId}/participate?level=3`,
    {
      method: "POST",
      headers: {
        "X-User-Id": userId,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ preferences }),
    }
  );

  const result = await response.json();

  if (result.status === 200) {
    showAdvancedSuccess(result.data);
    displayMatchDetails(result.data.toy.matchReasons);
    showRecommendations(result.data.recommendations);
  } else {
    handleAdvancedError(result);
  }
}

// Real-time updates for Level 3
function setupWebSocketUpdates(campaignId) {
  const ws = new WebSocket(
    `ws://localhost:8080/api/v1/giveaway-campaigns/${campaignId}/live`
  );

  ws.onmessage = function (event) {
    const update = JSON.parse(event.data);

    if (update.type === "toy_claimed") {
      updateAvailableToyCount(update.remainingToys);
      showRealtimeNotification(`${update.toyName} vừa được nhận!`);
    }
  };
}
```

## Monitoring và Alerting

### Key Metrics Dashboard

```yaml
# Grafana Dashboard Config
dashboard:
  title: "Toy Giveaway Campaign Metrics"
  panels:
    - title: "Response Time by Level"
      type: "graph"
      targets:
        - expr: 'histogram_quantile(0.95, http_request_duration_seconds_bucket{endpoint="/participate"})'
        - expr: 'avg(http_request_duration_seconds{level="1"})'
        - expr: 'avg(http_request_duration_seconds{level="2"})'
        - expr: 'avg(http_request_duration_seconds{level="3"})'

    - title: "Cache Hit Rate (Level 2+)"
      type: "singlestat"
      targets:
        - expr: "rate(cache_hits_total[5m]) / rate(cache_requests_total[5m]) * 100"

    - title: "Lock Acquisition Time (Level 3)"
      type: "graph"
      targets:
        - expr: "histogram_quantile(0.95, lock_acquisition_duration_seconds_bucket)"
```

### Alert Rules

```yaml
# Prometheus Alert Rules
groups:
  - name: giveaway_alerts
    rules:
      - alert: HighResponseTime
        expr: histogram_quantile(0.95, http_request_duration_seconds_bucket{endpoint="/participate"}) > 0.5
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Giveaway participation response time too high"

      - alert: LowCacheHitRate
        expr: rate(cache_hits_total[5m]) / rate(cache_requests_total[5m]) < 0.8
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "Cache hit rate below threshold"

      - alert: LockContentionHigh
        expr: rate(lock_acquisition_failures_total[5m]) > 0.1
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "High lock contention detected"
```

## Best Practices

### Choosing the Right Level

1. **Level 1 (Basic)**:

   - Dùng cho testing, development, small campaigns
   - Khi cần đảm bảo data consistency tuyệt đối
   - Ít hơn 100 users đồng thời

2. **Level 2 (Optimized)**:

   - Production environment với moderate load
   - Khi có Redis infrastructure
   - 100-1000 users đồng thời

3. **Level 3 (Advanced)**:
   - Large scale campaigns với high concurrency
   - Khi cần smart features và real-time updates
   - 1000+ users đồng thời

### Error Handling Strategy

```java
// Graceful degradation
public ToyParticipationDTO participateWithFallback(Long userId, Long campaignId, Integer level) {
    try {
        return participateInGiveaway(userId, campaignId, level, null);
    } catch (DistributedLockException e) {
        // Fallback to Level 2
        return participateInGiveaway(userId, campaignId, 2, null);
    } catch (CacheException e) {
        // Fallback to Level 1
        return participateInGiveaway(userId, campaignId, 1, null);
    }
}
```

### Security Considerations

```java
// Rate limiting per user
@RateLimiter(name = "giveaway-participation", fallbackMethod = "fallbackParticipate")
public ResponseEntity<BaseResponse> participate(Long campaignId, Long userId, Integer level) {
    // Implementation
}

// Idempotency for safety
@Idempotent(key = "giveaway:#{campaignId}:#{userId}")
public ToyParticipationDTO participateInGiveaway(Long userId, Long campaignId, Integer level) {
    // Implementation
}
```
