// stress-test-giveaway.js
import { check } from "k6";
import http from "k6/http";

export let options = {
  stages: [
    { duration: "30s", target: 9000 }, // Extreme stress
    { duration: "30s", target: 10000 }, // Extreme stress
    // { duration: "30s", target: 15000 }, // Extreme stress
  ],
  thresholds: {
    // http_req_duration: ["p(95)<1000"], // nghĩa là 95% số request phải hoàn thành trong dưới 1000ms (1 giây).
    http_req_duration: ["p(95)<30000"], // nghĩa là 95% số request phải hoàn thành trong dưới 30000ms (30 giây).
    http_req_failed: ["rate<0.01"], // Allow 10% error rate under extreme load
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
    "status is 400": (r) => r.status >= 400,
  });

  // sleep(0.1); // Very short sleep for spike simulation
}

//# Stress test
// k6 run stress-test-giveaway.js
