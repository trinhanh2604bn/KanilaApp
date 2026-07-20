#!/usr/bin/env node
/**
 * scripts/verify-vertex-config.js
 *
 * Verifies Vertex AI configuration without making any network calls.
 * Use: node scripts/verify-vertex-config.js
 *    or: npm run verify:vertex
 *
 * Exit 0 = all checks pass
 * Exit 1 = one or more checks fail
 *
 * SECURITY: Never prints API key value. Never prints process.env dump.
 */

"use strict";

require("dotenv").config();

let failures = 0;

function pass(msg) {
  console.log(`[PASS] ${msg}`);
}

function fail(msg) {
  console.error(`[FAIL] ${msg}`);
  failures++;
}

// ─── 1. Check VERTEX_API_KEY ─────────────────────────────────────────────────
const apiKey = (process.env.VERTEX_API_KEY || "").trim();
if (!apiKey) {
  fail("VERTEX_API_KEY is not set or empty — set it in .env or environment.");
} else {
  pass("VERTEX_API_KEY is configured");
}

// ─── 2. Check VERTEX_GEMINI_MODEL ────────────────────────────────────────────
const model = (process.env.VERTEX_GEMINI_MODEL || "").trim();
if (!model) {
  fail("VERTEX_GEMINI_MODEL is not set or empty — set it in .env or environment.");
} else {
  pass(`VERTEX_GEMINI_MODEL is configured: ${model}`);
}

// ─── 3. Check @google/genai is importable ────────────────────────────────────
try {
  const pkg = require("@google/genai");
  if (!pkg.GoogleGenAI) {
    fail("@google/genai is available but GoogleGenAI export not found.");
  } else {
    pass("@google/genai is available and exports GoogleGenAI");
  }
} catch (err) {
  fail(`@google/genai could not be loaded: ${err.message}`);
}

// ─── 4. Check Vertex client can be initialised ───────────────────────────────
if (apiKey && model) {
  try {
    const { GoogleGenAI } = require("@google/genai");
    // Construct with vertexai: true — MANDATORY
    const client = new GoogleGenAI({
      vertexai: true,
      apiKey,
    });
    if (!client) {
      fail("GoogleGenAI client returned null/undefined.");
    } else {
      pass("Vertex client initialized (vertexai: true, apiKey: VERTEX_API_KEY)");
    }
  } catch (err) {
    fail(`Vertex client initialization failed: ${err.message}`);
  }
} else {
  fail("Vertex client initialization skipped — missing VERTEX_API_KEY or VERTEX_GEMINI_MODEL.");
}

// ─── 5. Check no forbidden Gemini env vars set ───────────────────────────────
const FORBIDDEN_VARS = [
  "GOOGLE_API_KEY",
  "GOOGLE_GENAI_API_KEY",
  "GOOGLE_APPLICATION_CREDENTIALS",
];

let hasForbidden = false;
for (const varName of FORBIDDEN_VARS) {
  const val = (process.env[varName] || "").trim();
  if (val) {
    fail(`Forbidden environment variable is set: ${varName} — remove it to avoid SDK routing to Google AI Studio.`);
    hasForbidden = true;
  }
}
if (!hasForbidden) {
  pass("No forbidden Gemini environment variables detected");
}

// ─── Final result ─────────────────────────────────────────────────────────────
console.log("");
if (failures === 0) {
  console.log("[OK] All Vertex AI configuration checks passed.");
  process.exit(0);
} else {
  console.error(`[ERROR] ${failures} check(s) failed. See above for details.`);
  process.exit(1);
}
