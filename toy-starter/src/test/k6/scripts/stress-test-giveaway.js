// stress-test-giveaway.js
import { check } from "k6";
import http from "k6/http";

export let options = {
  stages: [
    { duration: "30s", target: 4000 }, // Extreme stress
    { duration: "30s", target: 5000 }, // Extreme stress
    { duration: "30s", target: 6000 }, // Extreme stress
    { duration: "30s", target: 10000 }, // Extreme stress
    { duration: "30s", target: 15000 }, // Extreme stress
  ],
  thresholds: {
    // http_req_duration: ["p(95)<1000"], // nghĩa là 95% số request phải hoàn thành trong dưới 1000ms (1 giây).
    http_req_duration: ["p(95)<30000"], // nghĩa là 95% số request phải hoàn thành trong dưới 30000ms (30 giây).
    http_req_failed: ["rate<0.1"], // Allow 10% error rate under extreme load
  },
};

const BASE_URL = "http://localhost:1122/api/v1";
const CAMPAIGN_ID = "12";

export default function () {
  const userId = 1;
  const sessionId = "123123";

  const headers = {
    "X-User-Id": userId.toString(),
    Cookie: `JSESSIONID=${sessionId}`,
    "Content-Type": "application/json",
  };

  // Test different levels randomly /giveaway-campaigns/12/claim10k
  const level = 3;
  // const url = `${BASE_URL}/api/v1/giveaway-campaigns/${CAMPAIGN_ID}/participate?level=${level}`;
  // /giveaway-campaigns/12/claim10k
  const url = `${BASE_URL}/giveaway-campaigns/${CAMPAIGN_ID}/claim10k`;

  const response = http.post(url, null, { headers });

  check(response, {
    "status is 200": (r) => r.status === 200,
    "response received": (r) => r.status !== 0,
    "time < 10s": (r) => r.timings.duration < 10000, // 10s
    "time < 15s": (r) => r.timings.duration < 15000, // 15s
    "time < 20s": (r) => r.timings.duration < 20000, // 20s
    "time < 30s": (r) => r.timings.duration < 30000, // 30s
    "status is 400": (r) => r.status >= 400 && r.status < 500,
  });

  // sleep(0.1); // Very short sleep for spike simulation
}

//# Stress test
// k6 run stress-test-giveaway.js
