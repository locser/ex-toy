# API Specifications - Toy Giveaway Campaign

## Tổng Quan

Tài liệu này mô tả chi tiết các API endpoints cho chức năng **Toy Giveaway Campaign** (Chiến dịch phát đồ chơi).

## Concept Overview

**Toy Giveaway Campaign** sử dụng lại entity `Toy` và `Event` hiện có:

- Admin tạo Event với type = GIVEAWAY
- Admin thêm Toys vào Event (toys có campaignId = eventId)
- Toys trong giveaway campaign có status = GIVEAWAY_AVAILABLE
- Users tham gia để nhận toys miễn phí
- Khi user nhận toy, toy status = GIVEAWAY_CLAIMED và userId được set

## Database Changes

### Event Entity - Thêm trường type

```sql
ALTER TABLE events ADD COLUMN type INT NOT NULL DEFAULT 1;
-- 1: EXCHANGE, 2: GIVEAWAY
```

### Toy Status - Thêm enum values

```java
// ToyStatus enum thêm:
GIVEAWAY_AVAILABLE(6),  // Toy available for giveaway
GIVEAWAY_CLAIMED(7);    // Toy claimed in giveaway
```

### ToyParticipation Entity - Mới

```sql
CREATE TABLE toy_participations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    toy_id BIGINT NOT NULL,
    campaign_id BIGINT NOT NULL,
    participation_date DATETIME NOT NULL,
    status INT NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_user_campaign (user_id, campaign_id),
    INDEX idx_user_id (user_id),
    INDEX idx_campaign_id (campaign_id)
);
```

## API Endpoints by Level

## Level 1: Basic Implementation

### 1. Tạo Giveaway Campaign

**Endpoint:** `POST /api/v1/admin/giveaway-campaigns`

**Request:**

```json
{
  "name": "Chiến dịch Tết 2024",
  "description": "Phát đồ chơi Tết cho các bé",
  "start_date": "01/02/2024 08:00",
  "end_date": "15/02/2024 23:59",
  "theme": "Tết Nguyên Đán",
  "rules": "Ai cũng có thể tham gia"
}
```

**Processing Notes:**

```
1. Validate dates (end_date > start_date)
2. Set event.type = GIVEAWAY (2)
3. Set event.status = UPCOMING (1)
4. Save to events table
5. Return event details
```

### 2. Thêm Toy vào Giveaway Campaign

**Endpoint:** `POST /api/v1/admin/giveaway-campaigns/{campaignId}/toys`

**Request:**

```json
{
  "toy_ids": [1, 2, 3, 4, 5]
}
```

**Processing Notes:**

```
1. Validate campaignId exists and type = GIVEAWAY
2. Validate all toy_ids exist and status = AVAILABLE
3. Update toys: campaignId = {campaignId}, status = GIVEAWAY_AVAILABLE
4. Return updated toy count
```

### 3. Danh sách Giveaway Campaigns

**Endpoint:** `GET /api/v1/giveaway-campaigns`

**Query Params:**

- `page=1`, `limit=10`, `status=2` (ACTIVE)

**Processing Notes:**

```
1. Query events where type = GIVEAWAY
2. Filter by status if provided
3. Join count available toys per campaign
4. Apply pagination
5. Return campaigns with toy counts
```

### 4. Chi tiết Giveaway Campaign

**Endpoint:** `GET /api/v1/giveaway-campaigns/{campaignId}`

**Processing Notes:**

```
1. Get event by id where type = GIVEAWAY
2. Count total toys in campaign
3. Count available toys (status = GIVEAWAY_AVAILABLE)
4. Check if current user participated
5. Return campaign details + participation status
```

### 5. Tham gia Giveaway Campaign (CORE FEATURE - 3 LEVELS)

**Endpoint:** `POST /api/v1/giveaway-campaigns/{campaignId}/participate`

**Headers:** `X-User-Id: 123`

**Query Parameters:**

- `level` (optional): Performance level (1=Basic, 2=Optimized, 3=Advanced). Default: 1
- `preferences` (optional): JSON string for user preferences (Level 3 only)

**Request Body (Level 3 only):**

```json
{
  "preferences": {
    "category": "Action Figures",
    "condition": [1, 2]
  }
}
```

**Processing Notes - Level 1 (Basic):**

```
1. Validate campaign exists, active, and not expired
2. Check user hasn't participated (toy_participations table)
3. Get random available toy (status = GIVEAWAY_AVAILABLE)
4. Atomic database transaction:
   - Update toy: userId = userId, status = GIVEAWAY_CLAIMED
   - Insert toy_participations record
5. Return claimed toy details
Performance target: < 500ms, 100 concurrent users
```

**Processing Notes - Level 2 (Optimized):**

```
1. Check Redis cache for campaign eligibility
2. Use Redis counter for available toy count validation
3. Implement optimistic locking for toy selection
4. Retry mechanism with exponential backoff (max 3 retries)
5. Atomic database transaction
6. Cache user participation status (TTL: 1 hour)
7. Invalidate relevant caches after successful participation
Performance target: < 300ms, 1000 concurrent users
```

**Processing Notes - Level 3 (Advanced):**

```
1. Acquire Redis distributed lock: "campaign:{campaignId}:participate"
2. Smart toy selection based on user preferences
3. Apply fairness algorithm to prevent gaming
4. Distributed transaction across multiple services
5. Real-time WebSocket notification to all clients
6. Advanced analytics tracking
7. Async event publishing for downstream services
Performance target: < 200ms, 10000+ concurrent users
```

**Response Success:**

```json
{
  "status": 200,
  "message": "Nhận đồ chơi thành công",
  "data": {
    "participation_id": 1,
    "toy": {
      "id": 15,
      "name": "Gấu bông Teddy",
      "description": "Gấu bông màu nâu",
      "category": "Stuffed Animals",
      "condition": 1
    },
    "campaign_name": "Chiến dịch Tết 2024",
    "participation_date": "02/02/2024 14:30"
  }
}
```

### 6. Lịch sử tham gia của User

**Endpoint:** `GET /api/v1/users/{userId}/giveaway-participations`

**Processing Notes:**

```
1. Query toy_participations by userId
2. Join with toys and events tables
3. Apply pagination
4. Return participation history with toy details
```

## Level 2: Performance Optimization

### 7. Tham gia với Caching

**Endpoint:** `POST /api/v1/giveaway-campaigns/{campaignId}/participate`

**Additional Processing Notes:**

```
1. Check Redis cache for campaign status
2. Use Redis counter for available toy count
3. Implement optimistic locking on toy selection
4. Cache user participation status
5. Invalidate relevant caches after successful participation
```

### 8. Real-time Campaign Statistics

**Endpoint:** `GET /api/v1/admin/giveaway-campaigns/{campaignId}/stats`

**Processing Notes:**

```
1. Get stats from Redis cache if available
2. Fallback to database aggregation
3. Cache results for 1 minute
4. Return real-time participation metrics
```

## Level 3: Advanced Features

### 9. Bulk Toy Assignment

**Endpoint:** `POST /api/v1/admin/giveaway-campaigns/{campaignId}/toys/bulk`

**Request:**

```json
{
  "toy_criteria": {
    "category": "Action Figures",
    "condition": [1, 2],
    "user_id": null,
    "limit": 50
  }
}
```

**Processing Notes:**

```
1. Query toys matching criteria
2. Batch update toys to campaign
3. Publish event for async processing
4. Return operation status
```

### 10. WebSocket Real-time Updates

**Endpoint:** `WS /api/v1/giveaway-campaigns/{campaignId}/live`

**Processing Notes:**

```
1. Establish WebSocket connection
2. Subscribe to campaign events
3. Broadcast toy claim events to all connected clients
4. Send remaining toy count updates
```

## Error Handling

### Common Error Responses

**Campaign Not Found:**

```json
{
  "status": 404,
  "message": "Không tìm thấy chiến dịch"
}
```

**Already Participated:**

```json
{
  "status": 409,
  "message": "Bạn đã tham gia chiến dịch này rồi"
}
```

**No Toys Available:**

```json
{
  "status": 410,
  "message": "Chiến dịch đã hết đồ chơi"
}
```

**Campaign Not Active:**

```json
{
  "status": 400,
  "message": "Chiến dịch chưa bắt đầu hoặc đã kết thúc"
}
```

## Status Codes

### Event Types

- `1`: EXCHANGE
- `2`: GIVEAWAY

### Event Status (reuse existing)

- `1`: UPCOMING
- `2`: ONGOING
- `3`: FINISHED
- `4`: DELETED

### Toy Status (extended)

- `0`: AVAILABLE
- `1`: PENDING_EXCHANGE
- `2`: IN_EXCHANGE
- `3`: EXCHANGED
- `4`: REMOVED
- `5`: AWAITING_APPROVAL
- `6`: GIVEAWAY_AVAILABLE
- `7`: GIVEAWAY_CLAIMED

### Participation Status

- `1`: CLAIMED
- `2`: DELIVERED
- `3`: CANCELLED

## Performance Considerations

### Level 1

- Database transactions for atomic toy claiming
- Basic indexing on campaignId and status

### Level 2

- Redis caching for campaign data
- Optimistic locking for concurrent toy claims
- Connection pooling optimization

### Level 3

- Distributed locking for high concurrency
- Event-driven architecture for real-time updates
- Database read replicas for query optimization

## Security Notes

### Rate Limiting

- Participation API: 1 request per minute per user
- List APIs: 60 requests per minute per user

### Validation

- All IDs must be positive integers
- User must be authenticated for participation
- Admin role required for campaign management

### Audit Trail

- Log all participation attempts
- Track toy ownership changes
- Monitor suspicious activity patterns
