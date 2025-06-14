# Toy Giveaway Campaign - Endpoints Summary

## Tổng Quan

Tóm tắt các endpoints cho chức năng **Toy Giveaway Campaign** theo từng level với notes xử lý chi tiết.

---

## Level 1: Basic Implementation (1-100 users)

### 1. 🔧 **Admin: Tạo Giveaway Campaign**

```
POST /api/v1/admin/giveaway-campaigns
```

**Processing Notes:**

```
✅ Validate request data (dates, required fields)
✅ Set event.type = GIVEAWAY (2)
✅ Set event.status = UPCOMING (1)
✅ Save to events table
✅ Return campaign details
```

**Key Logic:**

- Validate `end_date > start_date`
- Auto-set type = GIVEAWAY
- Generate unique campaign ID

---

### 2. 🔧 **Admin: Thêm Toys vào Campaign**

```
POST /api/v1/admin/giveaway-campaigns/{campaignId}/toys
Body: {"toy_ids": [1,2,3,4,5]}
```

**Processing Notes:**

```
✅ Validate campaignId exists and type = GIVEAWAY
✅ Validate all toy_ids exist and status = AVAILABLE
✅ Batch update toys:
   - campaignId = {campaignId}
   - status = GIVEAWAY_AVAILABLE (6)
✅ Return updated toy count
```

**Key Logic:**

- Atomic batch update for all toys
- Validate toys are not already in other campaigns
- Update toy status to GIVEAWAY_AVAILABLE

---

### 3. 👥 **Public: Danh sách Giveaway Campaigns**

```
GET /api/v1/giveaway-campaigns?page=1&limit=10&status=2
```

**Processing Notes:**

```
✅ Query events where type = GIVEAWAY
✅ Filter by status if provided (ACTIVE = 2)
✅ Join count available toys per campaign
✅ Apply pagination (page, limit)
✅ Return campaigns with toy counts
```

**Key Logic:**

- Only show campaigns with type = GIVEAWAY
- Include available toy count for each campaign
- Support filtering by status

---

### 4. 👥 **Public: Chi tiết Giveaway Campaign**

```
GET /api/v1/giveaway-campaigns/{campaignId}
Header: X-User-Id: 123
```

**Processing Notes:**

```
✅ Get event by id where type = GIVEAWAY
✅ Count total toys in campaign
✅ Count available toys (status = GIVEAWAY_AVAILABLE)
✅ Check if current user participated
✅ Return campaign details + participation status
```

**Key Logic:**

- Show campaign details with real-time toy availability
- Include user's participation status
- Validate campaign is giveaway type

---

### 5. 🎯 **Core: Tham gia Giveaway Campaign (3 LEVELS)**

```
POST /api/v1/giveaway-campaigns/{campaignId}/participate?level={1|2|3}
Header: X-User-Id: 123
Query: level=1 (Basic), level=2 (Optimized), level=3 (Advanced)
```

**Level 1 - Basic (1-100 users):**

```
✅ Validate campaign exists, active, not expired
✅ Check user hasn't participated (toy_participations table)
✅ Get random available toy (status = GIVEAWAY_AVAILABLE)
✅ Atomic database transaction:
   - Update toy: userId = {userId}, status = GIVEAWAY_CLAIMED
   - Insert toy_participations record
✅ Return claimed toy details
✅ Performance target: < 500ms
```

**Level 2 - Optimized (100-1000 users):**

```
✅ Check Redis cache for campaign eligibility
✅ Use Redis counter for available toy count validation
✅ Implement optimistic locking for toy selection
✅ Retry mechanism with exponential backoff (max 3 retries)
✅ Atomic database transaction
✅ Cache user participation status (TTL: 1 hour)
✅ Invalidate relevant caches after successful participation
✅ Performance target: < 300ms
```

**Level 3 - Advanced (1000+ users):**

```
✅ Acquire Redis distributed lock: "campaign:{campaignId}:participate"
✅ Smart toy selection based on user preferences
✅ Apply fairness algorithm to prevent gaming
✅ Distributed transaction across multiple services
✅ Real-time WebSocket notification to all clients
✅ Advanced analytics tracking
✅ Async event publishing for downstream services
✅ Performance target: < 200ms
```

**Key Logic:**

- **CRITICAL**: Choose appropriate level based on load
- Graceful degradation between levels
- Comprehensive error handling for all scenarios
- Performance monitoring and alerting

---

### 6. 👤 **User: Lịch sử tham gia**

```
GET /api/v1/users/{userId}/giveaway-participations?page=1&limit=10
```

**Processing Notes:**

```
✅ Query toy_participations by userId
✅ Join with toys and events tables
✅ Apply pagination
✅ Return participation history with toy details
```

**Key Logic:**

- Show user's participation history across all campaigns
- Include toy and campaign details
- Support pagination for large histories

---

## Level 2: Performance Optimization (100-1000 users)

### 7. 🚀 **Enhanced: Tham gia với Caching**

```
POST /api/v1/giveaway-campaigns/{campaignId}/participate
Header: X-User-Id: 123
```

**Additional Processing Notes:**

```
✅ Check Redis cache for campaign status
✅ Use Redis counter for available toy count
✅ Implement optimistic locking on toy selection
✅ Cache user participation status (TTL: 1 hour)
✅ Invalidate relevant caches after successful participation
✅ Retry mechanism for failed attempts (max 3 retries)
```

**Key Optimizations:**

- Redis cache for campaign metadata
- Optimistic locking to handle concurrency
- Cache invalidation strategy
- Retry logic for transient failures

---

### 8. 📊 **Admin: Real-time Campaign Statistics**

```
GET /api/v1/admin/giveaway-campaigns/{campaignId}/stats
```

**Processing Notes:**

```
✅ Get stats from Redis cache if available (TTL: 1 min)
✅ Fallback to database aggregation if cache miss
✅ Calculate: total_toys, claimed_toys, participation_rate
✅ Cache results for 1 minute
✅ Return real-time participation metrics
```

**Key Logic:**

- Cache-first strategy for performance
- Real-time statistics calculation
- Automatic cache refresh

---

### 9. 🔧 **Admin: Bulk Toy Management**

```
POST /api/v1/admin/giveaway-campaigns/{campaignId}/toys/bulk
Body: {"action": "add", "toy_criteria": {...}}
```

**Processing Notes:**

```
✅ Query toys matching criteria (category, condition, etc.)
✅ Validate toys are eligible for giveaway
✅ Batch update toys to campaign (chunks of 100)
✅ Update Redis counters
✅ Return operation status and affected count
```

**Key Logic:**

- Bulk operations for efficiency
- Chunked processing for large datasets
- Cache updates for consistency

---

## Level 3: Advanced Features (1000+ users)

### 10. 🔒 **Enhanced: Distributed Locking Participation**

```
POST /api/v1/giveaway-campaigns/{campaignId}/participate
Header: X-User-Id: 123
```

**Additional Processing Notes:**

```
✅ Acquire Redis distributed lock: "campaign:{campaignId}:participate"
✅ Lock timeout: 5 seconds
✅ Check participation eligibility within lock
✅ Select and claim toy atomically
✅ Release lock after completion
✅ Handle lock acquisition failures gracefully
```

**Key Features:**

- Distributed locking for high concurrency
- Lock timeout and cleanup
- Graceful failure handling

---

### 11. 📡 **Real-time: WebSocket Updates**

```
WS /api/v1/giveaway-campaigns/{campaignId}/live
```

**Processing Notes:**

```
✅ Establish WebSocket connection
✅ Subscribe to campaign events (toy claimed, campaign status)
✅ Broadcast real-time updates to all connected clients
✅ Send remaining toy count updates
✅ Handle connection lifecycle (connect/disconnect)
```

**Key Features:**

- Real-time toy availability updates
- Live participation notifications
- Connection management

---

### 12. 🎯 **Advanced: Smart Toy Selection**

```
POST /api/v1/giveaway-campaigns/{campaignId}/participate
Header: X-User-Id: 123
Body: {"preferences": {"category": "Action Figures", "condition": [1,2]}}
```

**Processing Notes:**

```
✅ Parse user preferences
✅ Query available toys matching preferences
✅ Fallback to random selection if no matches
✅ Apply fairness algorithm (prevent same user getting best toys)
✅ Log selection criteria for analytics
```

**Key Features:**

- User preference-based toy selection
- Fairness algorithms
- Analytics and reporting

---

### 13. 📊 **Analytics: Campaign Performance**

```
GET /api/v1/admin/giveaway-campaigns/{campaignId}/analytics
```

**Processing Notes:**

```
✅ Aggregate participation data by time periods
✅ Calculate conversion rates and engagement metrics
✅ Generate toy category distribution reports
✅ Identify peak participation times
✅ Cache complex analytics queries
```

**Key Features:**

- Advanced analytics and reporting
- Performance metrics
- Business intelligence data

---

### 14. 🔔 **Notifications: Campaign Events**

```
POST /api/v1/giveaway-campaigns/{campaignId}/notify
Body: {"event": "campaign_started", "target": "all_users"}
```

**Processing Notes:**

```
✅ Validate notification event and target
✅ Queue notification messages to RabbitMQ
✅ Process notifications asynchronously
✅ Support multiple channels (email, push, in-app)
✅ Track notification delivery status
```

**Key Features:**

- Multi-channel notifications
- Async processing with queues
- Delivery tracking

---

## Error Handling Summary

### Common Errors Across All Levels

| Error Code | Message                   | HTTP Status |
| ---------- | ------------------------- | ----------- |
| 4001       | Campaign not found        | 404         |
| 4002       | Campaign not active       | 400         |
| 4003       | User already participated | 409         |
| 4004       | No toys available         | 410         |
| 4005       | Campaign not started yet  | 400         |
| 4006       | Campaign expired          | 400         |
| 4007       | Invalid campaign type     | 400         |

### Level-Specific Errors

**Level 2:**

- 4008: Cache unavailable, using fallback
- 4009: Optimistic lock failure, retry required

**Level 3:**

- 4010: Distributed lock acquisition failed
- 4011: WebSocket connection limit exceeded
- 4012: Notification delivery failed

---

## Performance Targets

### Level 1

- ✅ API response time < 500ms (95th percentile)
- ✅ Support 100 concurrent users
- ✅ Zero data inconsistency

### Level 2

- ✅ API response time < 300ms (95th percentile)
- ✅ Support 1000 concurrent users
- ✅ Cache hit rate > 80%

### Level 3

- ✅ API response time < 200ms (95th percentile)
- ✅ Support 10000+ concurrent users
- ✅ Real-time notification latency < 1s

---

## Implementation Priority

```
🔥 High Priority (Level 1):
   - Endpoints 1-6: Core functionality

⚡ Medium Priority (Level 2):
   - Endpoints 7-9: Performance optimization

🚀 Low Priority (Level 3):
   - Endpoints 10-14: Advanced features
```
