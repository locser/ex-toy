// stress-test-giveaway.js
import { check, sleep } from "k6";
import http from "k6/http";

export let options = {
  stages: [
    { duration: "30s", target: 4000 }, // Extreme stress
  ],
  thresholds: {
    http_req_duration: ["p(95)<1000"], // Allow higher latency under stress
    http_req_failed: ["rate<0.1"], // Allow 10% error rate under extreme load
  },
};

const BASE_URL = "http://localhost:1122";
const CAMPAIGN_ID = "12";

export default function () {
  const userId = 1;
  const sessionId = "1234567890";

  const headers = {
    "X-User-Id": userId.toString(),
    Cookie: `JSESSIONID=${sessionId}`,
    "Content-Type": "application/json",
  };

  // Test different levels randomly
  const level = 3;
  const url = `${BASE_URL}/api/v1/giveaway-campaigns/${CAMPAIGN_ID}/participate?level=${level}`;

  const response = http.post(url, null, { headers });

  check(response, {
    "status is 200": (r) => r.status === 200,
    "response received": (r) => r.status !== 0,
    "no timeout": (r) => r.timings.duration < 10000, // 10s timeout
    "status is 400": (r) => r.status >= 400 && r.status < 500,
  });

  sleep(0.1); // Very short sleep for spike simulation
}

//# Stress test
// k6 run stress-test-giveaway.js
