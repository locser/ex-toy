// stress-test-toy-detail.js
// Comprehensive stress test for GET /api/v1/toys/{id}/detail endpoint

import { check, sleep } from "k6";
import http from "k6/http";

export let options = {
  scenarios: {
    // Scenario 1: Gradual ramp-up to test current 1k req/s baseline
    baseline_test: {
      executor: "ramping-vus",
      startVUs: 200,
      stages: [
        // { duration: "30s", target: 500 }, // Warm up
        { duration: "1m", target: 5000 }, // Ramp to 200 VUs
        // { duration: "1m", target: 5000 }, // Ramp to 500 VUs (should hit ~1k req/s)
        { duration: "3m", target: 10000 }, // Hold at 500 VUs
        // { duration: "30s", target: 0 }, // Ramp down
      ],
    },

    // Scenario 2: Spike test to find breaking point
    spike_test: {
      executor: "ramping-vus",
      startTime: "5s",
      startVUs: 50,
      stages: [
        { duration: "200s", target: 10000 }, // Sudden spike
        // { duration: "1m", target: 10000 }, // Hold spike
        { duration: "20s", target: 0 }, // Drop
      ],
    },

    // Scenario 3: Sustained high load test
    sustained_load: {
      executor: "constant-vus",
      startTime: "5s",
      vus: 800,
      duration: "300s", //"2m",
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

function getRandomToyId() {
  // 80% chance to access popular toys, 20% chance for regular toys
  return 1000000 + Math.floor(Math.random() * 1000);
}

export default function () {
  const toyId = getRandomToyId();
  const url = `http://localhost:1122/api/v1/toys/${toyId}/detail`;

  // Add some realistic headers
  const headers = {
    Accept: "application/json",
    "User-Agent": "k6-stress-test/1.0",
    "Accept-Encoding": "gzip, deflate",
    Connection: "keep-alive",
  };

  const response = http.get(url, { headers });

  // Check response status and basic validation
  check(response, {
    "status is 200": (r) => r.status === 200,
    "response time < 500ms": (r) => r.timings.duration < 500,
    "response time < 200ms": (r) => r.timings.duration < 200,
    "response has body": (r) => r.body && r.body.length > 0,
  });

  // // Log errors for debugging
  // if (response.status !== 200) {
  //   console.log(
  //     `Error for toy ${toyId}: Status ${response.status}, Body: ${response.body}`
  //   );
  // }

  // Small delay to simulate realistic user behavior
  sleep(0.1);
}

// Setup function to initialize test data if needed
export function setup() {
  console.log("Starting toy detail endpoint stress test...");
  console.log(`Base URL: ${BASE_URL}`);
  // console.log(`Testing toy IDs: ${TOY_IDS.join(", ")}`);

  // Warm up the cache by hitting each toy once
  console.log("Warming up cache...");
  // for (const toyId of TOY_IDS) {
  //   const url = `http://localhost:1122/api/v1/toys/${toyId}/detail`;
  //   http.get(url);
  // }

  return { baseUrl: BASE_URL };
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

4. Generate detailed report:
   k6 run --out json=results.json stress-test-toy-detail.js

5. Run specific scenario only:
  //  k6 run stress-test-toy-detail.js --scenarios baseline_test,spike_test,sustained_load

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
