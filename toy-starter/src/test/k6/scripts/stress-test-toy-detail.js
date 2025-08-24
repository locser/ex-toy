// stress-test-toy-detail.js
// Comprehensive stress test for GET /api/v1/toys/{id}/detail endpoint
import { check, sleep } from "k6";
import http from "k6/http";

export let options = {
  scenarios: {
    // Scenario 1: Gradual ramp-up to test current 1k req/s baseline
    baseline_test: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: [
        { duration: "30s", target: 500 }, // Warm up
        { duration: "1m", target: 2000 }, // Ramp to 200 VUs
        { duration: "2m", target: 5000 }, // Ramp to 500 VUs (should hit ~1k req/s)
        { duration: "2m", target: 10000 }, // Hold at 500 VUs
        { duration: "30s", target: 0 }, // Ramp down
      ],
    },

    // Scenario 2: Spike test to find breaking point
    spike_test: {
      executor: "ramping-vus",
      startTime: "6m",
      startVUs: 0,
      stages: [
        { duration: "10s", target: 10000 }, // Sudden spike
        { duration: "1m", target: 10000 }, // Hold spike
        { duration: "10s", target: 0 }, // Drop
      ],
    },

    // Scenario 3: Sustained high load test
    sustained_load: {
      executor: "constant-vus",
      startTime: "8m",
      vus: 800,
      duration: "3m",
    },
  },

  thresholds: {
    // Performance thresholds
    http_req_duration: [
      "p(50)<50", // 50% of requests under 50ms
      "p(90)<200", // 90% of requests under 200ms
      "p(95)<500", // 95% of requests under 500ms
      "p(99)<1000", // 99% of requests under 1s
    ],
    http_req_failed: ["rate<0.01"], // Error rate under 1%
    http_reqs: ["rate>1000"], // Target: >1000 requests per second

    // Cache hit rate tracking (custom metric)
    // cache_hit_rate: ["rate>0.8"], // Expect >80% cache hit rate
  },
};

const BASE_URL = "http://localhost:1122";

// Pool of toy IDs to test with (simulates real-world access patterns)
const TOY_IDS = [
  1000001, 1000002, 1000003, 1000004, 1000005, 1000006, 1000007, 1000008,
  1000009, 1000010, 1000011, 1000012, 1000013, 1000014, 1000015, 1000016,
  1000017, 1000018, 1000019, 1000020, 1000021, 1000022, 1000023, 1000024,
  1000025, 1000026, 1000027, 1000028, 1000029, 1000030,
  // Add more IDs based on your test data
];

// Weighted distribution - some toys accessed more frequently (80/20 rule)
const POPULAR_TOY_IDS = [1000001, 1000002, 1000003, 1000004, 1000005]; // Top 20% most accessed
const REGULAR_TOY_IDS = TOY_IDS.slice(5); // Remaining 80%

function getRandomToyId() {
  // 80% chance to access popular toys, 20% chance for regular toys
  if (Math.random() < 0.8) {
    return POPULAR_TOY_IDS[Math.floor(Math.random() * POPULAR_TOY_IDS.length)];
  } else {
    return REGULAR_TOY_IDS[Math.floor(Math.random() * REGULAR_TOY_IDS.length)];
  }
}

export default function () {
  const toyId = getRandomToyId();
  const url = `${BASE_URL}/api/v1/toys/${toyId}/detail`;

  // Add some realistic headers
  const headers = {
    Accept: "application/json",
    "User-Agent": "k6-stress-test/1.0",
    "Accept-Encoding": "gzip, deflate",
    Connection: "keep-alive",
  };

  const startTime = Date.now();
  const response = http.get(url, { headers });
  const endTime = Date.now();

  // Custom metrics for cache analysis
  const responseTime = endTime - startTime;
  const isCacheHit = responseTime < 10; // Assume <10ms indicates cache hit

  // Track cache hit rate
  // if (isCacheHit) {
  //   __VU.metrics.cache_hit_rate.add(1);
  // } else {
  //   __VU.metrics.cache_hit_rate.add(0);
  // }

  const checkResult = check(response, {
    "status is 200": (r) => r.status === 200,
    "response time < 100ms": (r) => r.timings.duration < 100,
    "response time < 500ms": (r) => r.timings.duration < 500,
    "has toy data": (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && body.data.id === toyId;
      } catch (e) {
        return false;
      }
    },
    "response size reasonable": (r) =>
      r.body.length > 100 && r.body.length < 10000,
    "cache hit (< 10ms)": (r) => r.timings.duration < 10,
    "database hit (10-100ms)": (r) =>
      r.timings.duration >= 10 && r.timings.duration < 100,
    "slow response (> 100ms)": (r) => r.timings.duration >= 100,
  });

  // Log slow responses for analysis
  if (response.timings.duration > 200) {
    console.log(
      `Slow response for toy ${toyId}: ${response.timings.duration}ms`
    );
  }

  // Log errors for debugging
  if (response.status !== 200) {
    console.log(
      `Error for toy ${toyId}: Status ${response.status}, Body: ${response.body}`
    );
  }

  // Very short sleep to maximize throughput while avoiding overwhelming
  sleep(0.001); // 1ms sleep
}

// Setup function to initialize test data if needed
export function setup() {
  console.log("Starting toy detail endpoint stress test...");
  console.log(`Base URL: ${BASE_URL}`);
  console.log(`Testing toy IDs: ${TOY_IDS.join(", ")}`);

  // Warm up the cache by hitting each toy once
  console.log("Warming up cache...");
  for (const toyId of POPULAR_TOY_IDS) {
    const url = `${BASE_URL}/api/v1/toys/${toyId}/detail`;
    http.get(url);
  }

  return { baseUrl: BASE_URL, toyIds: TOY_IDS };
}

// Teardown function for cleanup
export function teardown(data) {
  console.log("Stress test completed.");
  console.log("Check the results for:");
  console.log("- Request rate (target: >1000 req/s)");
  console.log("- Response times (p95 < 500ms)");
  console.log("- Error rate (< 1%)");
  console.log("- Cache hit rate (> 80%)");
}

/*
Usage examples:

1. Basic stress test:
   k6 run stress-test-toy-detail.js

2. With custom base URL:
   k6 run -e BASE_URL=http://your-server:8080 stress-test-toy-detail.js

3. With custom thresholds:
   k6 run --threshold http_req_duration=p(95)<200 stress-test-toy-detail.js

4. Generate detailed report:
   k6 run --out json=results.json stress-test-toy-detail.js

5. Run specific scenario only:
   k6 run --scenario baseline_test stress-test-toy-detail.js

Expected results for 1k+ req/s:
- VUs needed: ~500-800 (depending on response times)
- Cache hit rate: >80% for optimal performance
- P95 response time: <200ms with cache, <500ms without
- Error rate: <1%

Performance optimization tips:
1. Ensure Redis cache is properly configured and warmed up
2. Use connection pooling and keep-alive
3. Monitor database connection pool usage
4. Consider adding application-level caching
5. Optimize database queries and indexes
*/
