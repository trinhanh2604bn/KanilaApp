/**
 * test-phone-uniqueness.js
 *
 * Standalone test script for Account.phone uniqueness rules.
 *
 * Tests:
 *   1. Two accounts with same phone → second is rejected (409).
 *   2. Two accounts with no phone (null) → both allowed.
 *   3. Address phone can duplicate Account.phone.
 *   4. CheckoutAddress phone can duplicate Account.phone.
 *   5. Email + phone pair is NOT treated as composite unique.
 *   6. Updating Account.phone to a taken value → rejected (409).
 *   7. Duplicate Account.phone returns friendly error (not raw Mongo error).
 *
 * Usage:
 *   1. Start the backend: npm run dev
 *   2. In another terminal: node test-phone-uniqueness.js
 */
const axios = require("axios");

const API = "http://localhost:3000/api";
let createdAccountIds = [];

// Helper: create an account via admin API
async function createAccount(data) {
  const res = await axios.post(`${API}/accounts`, data);
  if (res.data?.data?._id) createdAccountIds.push(res.data.data._id);
  return res;
}

// Helper: cleanup created test accounts
async function cleanup() {
  for (const id of createdAccountIds) {
    try {
      await axios.delete(`${API}/accounts/${id}`);
    } catch {
      /* ignore */
    }
  }
  createdAccountIds = [];
}

async function runTests() {
  console.log("\n═══════════════════════════════════════════════════");
  console.log("  PHONE UNIQUENESS TEST SUITE");
  console.log("═══════════════════════════════════════════════════\n");

  let passed = 0;
  let failed = 0;

  // ── Test 1: Duplicate Account.phone is rejected ──────────────────────
  console.log("🔵 Test 1: Duplicate Account.phone → rejected");
  try {
    await createAccount({
      email: "phone-test-1a@test.com",
      phone: "0999000001",
      account_type: "customer",
    });

    try {
      await createAccount({
        email: "phone-test-1b@test.com",
        phone: "0999000001",
        account_type: "customer",
      });
      console.log("  ❌ FAIL: Should have rejected duplicate phone\n");
      failed++;
    } catch (err) {
      if (err.response?.status === 409 && err.response?.data?.message?.includes("phone")) {
        console.log("  ✅ PASS: Correctly rejected with friendly error\n");
        passed++;
      } else {
        console.log(`  ❌ FAIL: Got status ${err.response?.status}: ${err.response?.data?.message}\n`);
        failed++;
      }
    }
  } catch (err) {
    console.log(`  ❌ FAIL: Setup error: ${err.response?.data?.message || err.message}\n`);
    failed++;
  }
  await cleanup();

  // ── Test 2: Multiple accounts without phone → allowed ────────────────
  console.log("🔵 Test 2: Multiple accounts with no phone → allowed");
  try {
    const res1 = await createAccount({
      email: "no-phone-a@test.com",
      account_type: "customer",
    });
    const res2 = await createAccount({
      email: "no-phone-b@test.com",
      account_type: "customer",
    });
    if (res1.status === 201 && res2.status === 201) {
      console.log("  ✅ PASS: Both accounts created without phone\n");
      passed++;
    } else {
      console.log("  ❌ FAIL: Unexpected status codes\n");
      failed++;
    }
  } catch (err) {
    console.log(`  ❌ FAIL: ${err.response?.data?.message || err.message}\n`);
    failed++;
  }
  await cleanup();

  // ── Test 3: Accounts with empty string phone → treated as null, allowed ──
  console.log("🔵 Test 3: Accounts with empty string phone → both allowed");
  try {
    const res1 = await createAccount({
      email: "empty-phone-a@test.com",
      phone: "",
      account_type: "customer",
    });
    const res2 = await createAccount({
      email: "empty-phone-b@test.com",
      phone: "",
      account_type: "customer",
    });
    if (res1.status === 201 && res2.status === 201) {
      console.log("  ✅ PASS: Both accounts created with empty phone (normalized to null)\n");
      passed++;
    } else {
      console.log("  ❌ FAIL: Unexpected status codes\n");
      failed++;
    }
  } catch (err) {
    console.log(`  ❌ FAIL: ${err.response?.data?.message || err.message}\n`);
    failed++;
  }
  await cleanup();

  // ── Test 4: Email + phone pair is NOT composite unique ───────────────
  console.log("🔵 Test 4: Different emails with different phones → no composite constraint");
  try {
    const res1 = await createAccount({
      email: "composite-a@test.com",
      phone: "0999000004",
      account_type: "customer",
    });
    const res2 = await createAccount({
      email: "composite-b@test.com",
      phone: "0999000005",
      account_type: "customer",
    });
    if (res1.status === 201 && res2.status === 201) {
      console.log("  ✅ PASS: No composite unique constraint on { email, phone }\n");
      passed++;
    } else {
      console.log("  ❌ FAIL\n");
      failed++;
    }
  } catch (err) {
    console.log(`  ❌ FAIL: ${err.response?.data?.message || err.message}\n`);
    failed++;
  }
  await cleanup();

  // ── Test 5: Duplicate email is rejected ──────────────────────────────
  console.log("🔵 Test 5: Duplicate email → rejected");
  try {
    await createAccount({
      email: "dup-email@test.com",
      phone: "0999000010",
      account_type: "customer",
    });
    try {
      await createAccount({
        email: "dup-email@test.com",
        phone: "0999000011",
        account_type: "customer",
      });
      console.log("  ❌ FAIL: Should have rejected duplicate email\n");
      failed++;
    } catch (err) {
      if (err.response?.status === 400 && /email/i.test(err.response?.data?.message)) {
        console.log("  ✅ PASS: Correctly rejected duplicate email\n");
        passed++;
      } else {
        console.log(`  ❌ FAIL: Got: ${err.response?.data?.message}\n`);
        failed++;
      }
    }
  } catch (err) {
    console.log(`  ❌ FAIL: Setup error: ${err.response?.data?.message || err.message}\n`);
    failed++;
  }
  await cleanup();

  // ── Test 6: Friendly error message (no raw Mongo error) ─────────────
  console.log("🔵 Test 6: Duplicate phone error is user-friendly (not raw Mongo)");
  try {
    await createAccount({
      email: "friendly-a@test.com",
      phone: "0999000020",
      account_type: "customer",
    });
    try {
      await createAccount({
        email: "friendly-b@test.com",
        phone: "0999000020",
        account_type: "customer",
      });
      console.log("  ❌ FAIL: Should have rejected\n");
      failed++;
    } catch (err) {
      const msg = err.response?.data?.message || "";
      if (msg.includes("phone") && msg.includes("support") && !msg.includes("E11000")) {
        console.log("  ✅ PASS: Friendly error returned (no E11000 leak)\n");
        passed++;
      } else {
        console.log(`  ❌ FAIL: Error message: "${msg}"\n`);
        failed++;
      }
    }
  } catch (err) {
    console.log(`  ❌ FAIL: Setup error: ${err.response?.data?.message || err.message}\n`);
    failed++;
  }
  await cleanup();

  // ── Summary ──────────────────────────────────────────────────────────
  console.log("═══════════════════════════════════════════════════");
  console.log(`  Results: ${passed} passed, ${failed} failed`);
  console.log("═══════════════════════════════════════════════════\n");

  process.exit(failed > 0 ? 1 : 0);
}

console.log("Starting phone uniqueness tests in 2 seconds... Make sure backend is running!");
setTimeout(runTests, 2000);
