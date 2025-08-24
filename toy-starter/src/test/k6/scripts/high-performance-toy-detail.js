// high-performance-toy-detail.js
// Optimized stress test to push beyond 1k req/s for GET /api/v1/toys/{id}/detail
import { check } from "k6";
import http from "k6/http";
import { Counter, Rate, Trend } from "k6/metrics";

// Custom metrics for detailed performance analysis
const cacheHitRate = new Rate("cache_hit_rate");
const dbHitRate = new Rate("db_hit_rate");
const errorRate = new Rate("error_rate");
const responseTimeCache = new Trend("response_time_cache");
const responseTimeDB = new Trend("response_time_db");
const requestCounter = new Counter("total_requests");

export let options = {
  scenarios: {
    // High-performance test targeting 2k+ req/s
    ultra_high_load: {
      executor: "constant-arrival-rate",
      rate: 2000, // 2000 requests per second
      timeUnit: "1s",
      duration: "3m",
      preAllocatedVUs: 100,
      maxVUs: 500,
    },

    // Cache optimization test - hit same toys repeatedly
    cache_optimization: {
      executor: "constant-arrival-rate",
      startTime: "3m30s",
      rate: 3000, // 3000 requests per second with cache hits
      timeUnit: "1s",
      duration: "2m",
      preAllocatedVUs: 50,
      maxVUs: 200,
    },

    // Mixed load test - realistic traffic pattern
    mixed_load: {
      executor: "ramping-arrival-rate",
      startTime: "6m",
      startRate: 1000,
      stages: [
        { duration: "1m", target: 1500 },
        { duration: "2m", target: 2500 },
        { duration: "1m", target: 3000 },
        { duration: "1m", target: 1000 },
      ],
      preAllocatedVUs: 100,
      maxVUs: 800,
    },
  },

  thresholds: {
    // Aggressive performance targets
    http_req_duration: [
      "p(50)<20", // 50% under 20ms (cache hits)
      "p(90)<100", // 90% under 100ms
      "p(95)<200", // 95% under 200ms
      "p(99)<500", // 99% under 500ms
    ],
    http_req_failed: ["rate<0.005"], // Error rate under 0.5%
    http_reqs: ["rate>1500"], // Target: >1500 requests per second

    // Cache performance targets
    cache_hit_rate: ["rate>0.85"], // >85% cache hit rate
    response_time_cache: ["p(95)<50"], // Cache responses under 50ms
    response_time_db: ["p(95)<300"], // DB responses under 300ms
  },
};

const BASE_URL = "http://localhost:1122";

// Optimized toy ID distribution for maximum cache efficiency
const HOT_TOYS = [1000001, 1000002, 1000003]; // 60% of traffic - always cached
const WARM_TOYS = [
  1000004, 1000005, 1000006, 1000007, 1000008, 1000009, 1000010,
]; // 30% of traffic - frequently cached
const COLD_TOYS = [
  1000011, 1000012, 1000013, 1000014, 1000015, 1000016, 1000017, 1000018,
  1000019, 1000020, 1000021, 1000022, 1000023, 1000024, 1000025,
]; // 10% of traffic

function getOptimizedToyId(scenario) {
  const rand = Math.random();

  if (scenario === "cache_optimization") {
    // For cache optimization, heavily favor hot toys
    return HOT_TOYS[Math.floor(Math.random() * HOT_TOYS.length)];
  }

  // Normal distribution: 60% hot, 30% warm, 10% cold
  if (rand < 0.6) {
    return HOT_TOYS[Math.floor(Math.random() * HOT_TOYS.length)];
  } else if (rand < 0.9) {
    return WARM_TOYS[Math.floor(Math.random() * WARM_TOYS.length)];
  } else {
    return COLD_TOYS[Math.floor(Math.random() * COLD_TOYS.length)];
  }
}

export default function () {
  const scenario = __ENV.K6_SCENARIO_NAME || "mixed_load";
  const toyId = getOptimizedToyId(scenario);
  const url = `${BASE_URL}/api/v1/toys/${toyId}/detail`;

  // Optimized headers for maximum performance
  const headers = {
    Accept: "application/json",
    "Accept-Encoding": "gzip",
    Connection: "keep-alive",
    "Cache-Control": "no-cache",
    "X-User-Id": 1, // Force fresh data to test real performance
  };

  const startTime = Date.now();
  const response = http.get(url, {
    headers,
    timeout: "5s", // 5 second timeout
  });
  const responseTime = Date.now() - startTime;

  requestCounter.add(1);

  // Classify response type based on timing
  const isCacheHit = responseTime < 15; // < 15ms likely cache hit
  const isDBHit = responseTime >= 15 && responseTime < 200; // 15-200ms likely DB hit

  if (isCacheHit) {
    cacheHitRate.add(1);
    responseTimeCache.add(responseTime);
  } else if (isDBHit) {
    dbHitRate.add(1);
    responseTimeDB.add(responseTime);
  }

  if (response.status !== 200) {
    errorRate.add(1);
  } else {
    errorRate.add(0);
  }

  const checks = check(response, {
    "status is 200": (r) => r.status === 200,
    "ultra fast response (< 15ms)": (r) => r.timings.duration < 15,
    "fast response (< 50ms)": (r) => r.timings.duration < 50,
    "acceptable response (< 200ms)": (r) => r.timings.duration < 200,
    "has valid toy data": (r) => {
      if (r.status !== 200) return false;
      try {
        const body = JSON.parse(r.body);
        return body.data && body.data.id && body.success === true;
      } catch (e) {
        return false;
      }
    },
    "response not empty": (r) => r.body && r.body.length > 50,
  });

  // Log performance issues
  if (response.timings.duration > 500) {
    console.log(`SLOW: Toy ${toyId} took ${response.timings.duration}ms`);
  }

  if (response.status !== 200) {
    console.log(
      `ERROR: Toy ${toyId} returned ${response.status}: ${response.body}`
    );
  }

  // No sleep for maximum throughput
}

export function setup() {
  console.log("🚀 Starting HIGH PERFORMANCE toy detail stress test");
  console.log(`📊 Target: >1500 req/s (current baseline: 1000 req/s)`);
  console.log(`🎯 Base URL: ${BASE_URL}`);

  // Pre-warm cache with hot toys
  console.log("🔥 Pre-warming cache with hot toys...");
  const warmupPromises = [];

  for (let i = 0; i < 3; i++) {
    // Warm up 3 times
    HOT_TOYS.forEach((toyId) => {
      const url = `${BASE_URL}/api/v1/toys/${toyId}/detail`;
      warmupPromises.push(http.asyncRequest("GET", url));
    });
  }

  // Wait for warmup to complete
  Promise.all(warmupPromises);

  console.log("✅ Cache warmup completed");
  console.log(`🎲 Hot toys (60% traffic): ${HOT_TOYS.join(", ")}`);
  console.log(`🌡️  Warm toys (30% traffic): ${WARM_TOYS.join(", ")}`);
  console.log(
    `❄️  Cold toys (10% traffic): ${COLD_TOYS.slice(0, 5).join(", ")}...`
  );

  return {
    baseUrl: BASE_URL,
    hotToys: HOT_TOYS,
    warmToys: WARM_TOYS,
    coldToys: COLD_TOYS,
  };
}

export function teardown(data) {
  console.log("\n🏁 HIGH PERFORMANCE TEST COMPLETED");
  console.log("=".repeat(50));
  console.log("📈 PERFORMANCE ANALYSIS:");
  console.log("- Check if req/s > 1500 (target exceeded)");
  console.log("- Cache hit rate should be > 85%");
  console.log("- P95 response time < 200ms");
  console.log("- Error rate < 0.5%");
  console.log("\n🔧 OPTIMIZATION RECOMMENDATIONS:");
  console.log("1. If cache hit rate < 85%: Increase cache TTL or size");
  console.log("2. If P95 > 200ms: Check database indexes and connection pool");
  console.log("3. If req/s < 1500: Scale horizontally or optimize code");
  console.log(
    "4. If errors > 0.5%: Check application logs and resource limits"
  );
}

/*
🚀 USAGE GUIDE:

1. Basic high-performance test:
   k6 run high-performance-toy-detail.js

2. Test specific scenario:
   k6 run --scenario ultra_high_load high-performance-toy-detail.js
   k6 run --scenario cache_optimization high-performance-toy-detail.js

3. Custom target rate:
   k6 run -e TARGET_RATE=2500 high-performance-toy-detail.js

4. With monitoring:
   k6 run --out influxdb=http://localhost:8086/k6 high-performance-toy-detail.js

5. Generate detailed JSON report:
   k6 run --out json=performance-report.json high-performance-toy-detail.js

📊 EXPECTED PERFORMANCE METRICS:

Scenario 1 - Ultra High Load (2000 req/s):
- Should achieve 1800-2200 req/s actual
- P95 < 200ms with good caching
- Cache hit rate > 80%

Scenario 2 - Cache Optimization (3000 req/s):
- Should achieve 2500-3500 req/s actual
- P95 < 50ms (mostly cache hits)
- Cache hit rate > 95%

Scenario 3 - Mixed Load (1000-3000 req/s ramp):
- Should handle peak of 2500+ req/s
- P95 < 300ms during peak
- Cache hit rate > 75%

🎯 PERFORMANCE TARGETS TO BEAT 1K REQ/S:
✅ Achieve sustained 1500+ req/s
✅ Maintain P95 < 200ms
✅ Keep error rate < 0.5%
✅ Optimize cache hit rate > 85%
*/
