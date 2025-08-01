// basic-giveaway-test.js
import { check } from "k6";
import http from "k6/http";

export let options = {
  stages: [
    { duration: "30s", target: 30 },
    // { duration: "30s", target: 2000 },
    // { duration: "3m", target: 50 },
    // { duration: "1m", target: 0 },
  ],
  thresholds: {
    http_req_duration: ["p(95)<200"], // 95% requests < 200ms
    http_req_failed: ["rate<0.01"], // Error rate < 1%
  },
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:1122";
const CAMPAIGN_ID = __ENV.CAMPAIGN_ID || "12";

export default function () {
  const userId = Math.floor(Math.random() * 100000) + 1;
  const sessionId = Math.random().toString(36).substring(2, 15);

  // Random preferences for level 3
  const preferences = JSON.stringify({
    category: ["Action Figures", "Stuffed Animals", "Educational"][
      Math.floor(Math.random() * 3)
    ],
    condition: [1, 2, 3][Math.floor(Math.random() * 3)],
    age_group: ["3-5", "6-8", "9-12"][Math.floor(Math.random() * 3)],
  });

  const headers = {
    "X-User-Id": userId.toString(),
    Cookie: `JSESSIONID=${sessionId}`,
    "Content-Type": "application/json",
  };

  const url = `${BASE_URL}/api/v1/giveaway-campaigns/${CAMPAIGN_ID}/participate?level=3&preferences=${encodeURIComponent(
    preferences
  )}`;

  const response = http.post(url, null, { headers });

  check(response, {
    "status is 200": (r) => r.status === 200,
    "advanced response time < 200ms": (r) => r.timings.duration < 200,
    "has smart toy selection": (r) => r.json("data.toy") !== undefined,
    "distributed lock working": (r) => r.timings.duration > 50, // Should have some processing time
  });

  //   sleep(0.2); // Very short sleep for maximum throughput
}
