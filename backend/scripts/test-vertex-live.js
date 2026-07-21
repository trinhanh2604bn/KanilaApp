#!/usr/bin/env node
/**
 * scripts/test-vertex-live.js
 *
 * Live smoke test for Vertex AI integration.
 * Makes exactly ONE generateContent request — no retries, no loop.
 *
 * Usage: node scripts/test-vertex-live.js
 *     or: npm run test:vertex:live
 *
 * Requirements:
 *   - VERTEX_API_KEY must be set in environment
 *   - VERTEX_GEMINI_MODEL must be set in environment
 *
 * Exit 0 = success
 * Exit 1 = failure
 *
 * SECURITY:
 *   - Never prints API key value
 *   - Never prints process.env
 *   - Never uses customer data
 *   - Never connects to MongoDB
 *   - Never modifies database
 */

"use strict";

require("dotenv").config();

const SMOKE_TIMEOUT_MS = 30_000;

async function runLiveSmokeTest() {
  // Guard: only run if both vars are present
  const apiKey = (process.env.VERTEX_API_KEY || "").trim();
  const model = (process.env.VERTEX_GEMINI_MODEL || "").trim();

  if (!apiKey) {
    console.error(
      "[SMOKE] SKIPPED: VERTEX_API_KEY is not set. " +
        "Set it in .env or environment before running live test."
    );
    process.exit(1);
  }
  if (!model) {
    console.error(
      "[SMOKE] SKIPPED: VERTEX_GEMINI_MODEL is not set. " +
        "Set it in .env or environment before running live test."
    );
    process.exit(1);
  }

  console.log("[SMOKE] Starting Vertex AI live smoke test...");
  console.log(`[SMOKE] Model configured: ${model}`);
  // API key is intentionally NOT printed

  const { GoogleGenAI } = require("@google/genai");

  // vertexai: true is mandatory
  const client = new GoogleGenAI({
    vertexai: true,
    apiKey,
  });

  const SMOKE_PROMPT =
    "Trả lời đúng một câu tiếng Việt: Kết nối Gemini trên Vertex AI của Kanila đã hoạt động.";

  let timeoutHandle;

  const timeoutPromise = new Promise((_, reject) => {
    timeoutHandle = setTimeout(() => {
      reject(new Error(`Smoke test timed out after ${SMOKE_TIMEOUT_MS}ms.`));
    }, SMOKE_TIMEOUT_MS);
  });

  const requestPromise = client.models.generateContent({
    model,
    contents: SMOKE_PROMPT,
    config: {
      temperature: 0.1,
      maxOutputTokens: 150,
    },
  });

  let response;
  try {
    response = await Promise.race([requestPromise, timeoutPromise]);
    clearTimeout(timeoutHandle);
  } catch (err) {
    clearTimeout(timeoutHandle);

    // Sanitized error reporting — no key values, no raw body dump
    const status =
      err.status || err.httpStatus || err.statusCode || null;
    const msg = err.message || String(err);

    if (status === 401 || msg.toLowerCase().includes("unauthorized") || msg.toLowerCase().includes("api key")) {
      console.error("[SMOKE] FAILED: VERTEX_AUTHENTICATION_FAILED");
      console.error(
        "[SMOKE] Check that VERTEX_API_KEY is valid and not expired/revoked."
      );
    } else if (status === 403 || msg.toLowerCase().includes("forbidden") || msg.toLowerCase().includes("permission denied")) {
      console.error("[SMOKE] FAILED: VERTEX_PERMISSION_DENIED");
      console.error(
        "[SMOKE] Check: Vertex AI API is enabled in Google Cloud Console, billing is active, API key restrictions allow Vertex AI."
      );
    } else if (status === 404 || msg.toLowerCase().includes("not found")) {
      console.error("[SMOKE] FAILED: VERTEX_MODEL_NOT_FOUND");
      console.error(
        `[SMOKE] Model not found. Check VERTEX_GEMINI_MODEL="${model}" in Google Cloud Console.`
      );
      console.error("[SMOKE] Do NOT change the model automatically. Contact project owner.");
    } else if (status === 429 || msg.toLowerCase().includes("quota") || msg.toLowerCase().includes("rate limit")) {
      console.error("[SMOKE] FAILED: VERTEX_RATE_LIMITED");
      console.error("[SMOKE] Check quota in Google Cloud Console. Do NOT retry immediately.");
    } else if (msg.toLowerCase().includes("timed out") || msg.toLowerCase().includes("timeout")) {
      console.error("[SMOKE] FAILED: VERTEX_TIMEOUT");
      console.error(`[SMOKE] Request timed out after ${SMOKE_TIMEOUT_MS}ms.`);
    } else {
      console.error("[SMOKE] FAILED: VERTEX_UPSTREAM_ERROR");
      console.error(`[SMOKE] HTTP Status: ${status || "unknown"}`);
      console.error(`[SMOKE] Error type: ${err.constructor?.name || "Error"}`);
    }

    process.exit(1);
  }

  // Validate response
  const text = response?.text || "";
  if (!text.trim()) {
    console.error("[SMOKE] FAILED: VERTEX_EMPTY_RESPONSE — Vertex AI returned empty text.");
    process.exit(1);
  }

  // Check for error markers in response text
  const lowerText = text.toLowerCase();
  if (lowerText.includes("api key") && lowerText.includes("invalid")) {
    console.error("[SMOKE] FAILED: Response contains API key error message.");
    process.exit(1);
  }

  // Log usage metadata (no PII, no key)
  const usage = response.usageMetadata || response.candidates?.[0]?.usageMetadata || null;
  const finishReason =
    response.candidates?.[0]?.finishReason ||
    response.finishReason ||
    "unknown";

  console.log("[SMOKE] PASSED: Vertex AI returned a non-empty response.");
  console.log(`[SMOKE] Finish reason: ${finishReason}`);
  if (usage) {
    console.log(
      `[SMOKE] Token usage — prompt: ${usage.promptTokenCount || "N/A"}, ` +
        `candidates: ${usage.candidatesTokenCount || "N/A"}, ` +
        `total: ${usage.totalTokenCount || "N/A"}`
    );
  }
  console.log("[SMOKE] Note: This test consumed a small amount of Vertex AI quota.");
  console.log("[SMOKE] Integration is operational. ✓");

  process.exit(0);
}

runLiveSmokeTest().catch((err) => {
  console.error("[SMOKE] Unexpected error:", err.message || err);
  process.exit(1);
});
