/**
 * services/ai/vertexGemini.service.js
 *
 * Shared internal service for all Vertex AI (Gemini) calls in Kanila Backend.
 *
 * Architecture:
 *   Kanila Android → Kanila Backend → vertexGemini.service → Vertex AI
 *
 * Rules:
 *   - vertexai: true is enforced via config/vertex.config.js
 *   - Model is always VERTEX_GEMINI_MODEL — callers cannot override it
 *   - API key is never passed to callers, never logged
 *   - Worker handles retry — this service does NOT retry by default
 *   - One optional network retry for transient 5xx/429 only
 *
 * Error codes returned:
 *   VERTEX_BAD_REQUEST, VERTEX_AUTHENTICATION_FAILED, VERTEX_PERMISSION_DENIED,
 *   VERTEX_MODEL_NOT_FOUND, VERTEX_TIMEOUT, VERTEX_RATE_LIMITED,
 *   VERTEX_UPSTREAM_ERROR, VERTEX_EMPTY_RESPONSE, VERTEX_INVALID_JSON_RESPONSE
 */

"use strict";

const { getVertexClient, getVertexModel } = require("../../config/vertex.config");

// Default timeout: 90 seconds (Gemini 2.5 Flash can be slow on long prompts)
const DEFAULT_TIMEOUT_MS = 90_000;

// HTTP status codes that are safe to retry once
const RETRYABLE_STATUS_CODES = new Set([429, 500, 502, 503, 504]);

// ─── Error mapping ───────────────────────────────────────────────────────────

/**
 * Map an upstream error to a sanitized Vertex error code.
 * Never exposes raw API key or full upstream body.
 *
 * @param {Error} err
 * @returns {{ code: string, httpStatus: number|null, message: string }}
 */
function _mapError(err) {
  // Extract HTTP status from various SDK error shapes
  const status =
    err.status ||
    err.httpStatus ||
    err.statusCode ||
    (err.response && err.response.status) ||
    null;

  const msg = err.message || "";

  if (status === 400 || msg.includes("400")) {
    return {
      code: "VERTEX_BAD_REQUEST",
      httpStatus: 400,
      message: "Vertex AI rejected the request (400 Bad Request).",
    };
  }
  if (status === 401 || msg.includes("401") || msg.toLowerCase().includes("unauthorized") || msg.toLowerCase().includes("api key")) {
    return {
      code: "VERTEX_AUTHENTICATION_FAILED",
      httpStatus: 401,
      message: "Vertex AI authentication failed. Check VERTEX_API_KEY.",
    };
  }
  if (status === 403 || msg.includes("403") || msg.toLowerCase().includes("forbidden") || msg.toLowerCase().includes("permission denied")) {
    return {
      code: "VERTEX_PERMISSION_DENIED",
      httpStatus: 403,
      message: "Vertex AI permission denied. Check API restrictions and project billing.",
    };
  }
  if (status === 404 || msg.includes("404") || msg.toLowerCase().includes("not found")) {
    return {
      code: "VERTEX_MODEL_NOT_FOUND",
      httpStatus: 404,
      message: `Vertex AI model not found. Check VERTEX_GEMINI_MODEL in Google Cloud Console.`,
    };
  }
  if (err.code === "VERTEX_TIMEOUT" || msg.toLowerCase().includes("timed out") || msg.toLowerCase().includes("timeout")) {
    return {
      code: "VERTEX_TIMEOUT",
      httpStatus: 408,
      message: `Vertex AI request timed out after ${DEFAULT_TIMEOUT_MS}ms.`,
    };
  }
  if (status === 429 || msg.includes("429") || msg.toLowerCase().includes("quota") || msg.toLowerCase().includes("rate limit")) {
    return {
      code: "VERTEX_RATE_LIMITED",
      httpStatus: 429,
      message: "Vertex AI rate limited. Check quota in Google Cloud Console.",
    };
  }
  if (status >= 500 || (msg.match && msg.match(/5\d\d/))) {
    return {
      code: "VERTEX_UPSTREAM_ERROR",
      httpStatus: status || 500,
      message: `Vertex AI upstream error (${status || "5xx"}).`,
    };
  }

  return {
    code: "VERTEX_UPSTREAM_ERROR",
    httpStatus: status || null,
    message: "Vertex AI encountered an unexpected error.",
  };
}

/**
 * Wrap an upstream error with a sanitized Vertex error code.
 * The original error is attached as .cause for internal logging.
 * Raw upstream body is never re-thrown to callers.
 *
 * @param {Error} err
 * @returns {Error}
 */
function _wrapError(err) {
  if (err.code && err.code.startsWith("VERTEX_CONFIG_")) {
    // Re-throw config errors as-is — they don't contain secrets
    return err;
  }

  const { code, httpStatus, message } = _mapError(err);
  const wrapped = new Error(message);
  wrapped.code = code;
  wrapped.httpStatus = httpStatus;
  wrapped.cause = err; // internal only — never serialised to client responses
  return wrapped;
}

// ─── Core generateContent call ───────────────────────────────────────────────

/**
 * Execute a single generateContent call with timeout.
 * No retry — callers (workers) own retry logic.
 *
 * @param {object} params
 * @param {string} params.prompt                    — user/content prompt (required, non-empty)
 * @param {string} [params.systemInstruction]       — optional system instruction
 * @param {object} [params.generationConfig]        — temperature, topP, maxOutputTokens etc.
 * @param {string} [params.responseMimeType]        — e.g. "application/json"
 * @param {number} [params.timeoutMs]               — override default timeout
 *
 * @returns {Promise<{
 *   text: string,
 *   model: string,
 *   usageMetadata: object|null,
 *   finishReason: string|null
 * }>}
 */
async function generateContent({
  prompt,
  systemInstruction,
  generationConfig,
  responseMimeType,
  timeoutMs = DEFAULT_TIMEOUT_MS,
}) {
  // ── Input validation ────────────────────────────────────────────────────
  if (typeof prompt !== "string") {
    const err = new Error("VERTEX_BAD_REQUEST: prompt must be a string.");
    err.code = "VERTEX_BAD_REQUEST";
    throw err;
  }
  if (!prompt.trim()) {
    const err = new Error("VERTEX_BAD_REQUEST: prompt must not be empty.");
    err.code = "VERTEX_BAD_REQUEST";
    throw err;
  }

  // ── Build config ────────────────────────────────────────────────────────
  const { client, model } = getVertexClient();

  const requestConfig = {};
  if (systemInstruction) requestConfig.systemInstruction = systemInstruction;
  if (generationConfig) Object.assign(requestConfig, generationConfig);
  if (responseMimeType) requestConfig.responseMimeType = responseMimeType;

  // ── Timeout promise ─────────────────────────────────────────────────────
  let _timeoutHandle;
  const timeoutPromise = new Promise((_, reject) => {
    _timeoutHandle = setTimeout(() => {
      const err = new Error(`Vertex AI request timed out after ${timeoutMs}ms.`);
      err.code = "VERTEX_TIMEOUT";
      reject(err);
    }, timeoutMs);
  });

  // ── Request promise ─────────────────────────────────────────────────────
  const requestPromise = (async () => {
    const requestPayload = {
      model,
      contents: prompt,
    };
    if (Object.keys(requestConfig).length > 0) {
      requestPayload.config = requestConfig;
    }

    const response = await client.models.generateContent(requestPayload);
    return response;
  })();

  try {
    const response = await Promise.race([requestPromise, timeoutPromise]);
    clearTimeout(_timeoutHandle);

    // ── Normalise response ────────────────────────────────────────────────
    const text = response.text || "";
    if (!text.trim()) {
      const err = new Error("Vertex AI returned an empty response.");
      err.code = "VERTEX_EMPTY_RESPONSE";
      throw err;
    }

    const usageMetadata = response.usageMetadata || null;
    const finishReason =
      response.candidates?.[0]?.finishReason ||
      response.finishReason ||
      null;

    return {
      text,
      model,
      usageMetadata,
      finishReason,
    };
  } catch (err) {
    clearTimeout(_timeoutHandle);

    if (
      err.code === "VERTEX_EMPTY_RESPONSE" ||
      err.code === "VERTEX_TIMEOUT" ||
      (err.code && err.code.startsWith("VERTEX_CONFIG_"))
    ) {
      throw err; // already normalised
    }

    throw _wrapError(err);
  }
}

// ─── generateContent with one optional network retry ─────────────────────────

/**
 * generateContent with one optional retry for transient errors.
 * Only retries 429, 500, 502, 503, 504, and network resets.
 * Never retries 400, 401, 403, 404, validation errors.
 *
 * Use this when there is NO external retry mechanism (e.g. no worker).
 * For the AI Review Worker, prefer generateContent() directly.
 *
 * @param {object} params — same as generateContent
 * @param {number} [retryDelayMs=2000] — delay before single retry
 * @returns {Promise<object>}
 */
async function generateContentWithRetry(params, retryDelayMs = 2000) {
  try {
    return await generateContent(params);
  } catch (err) {
    const shouldRetry =
      RETRYABLE_STATUS_CODES.has(err.httpStatus) ||
      err.code === "VERTEX_RATE_LIMITED" ||
      err.code === "VERTEX_UPSTREAM_ERROR";

    if (!shouldRetry) throw err;

    // One retry after delay
    await new Promise((resolve) => setTimeout(resolve, retryDelayMs));
    return await generateContent(params);
  }
}

module.exports = {
  generateContent,
  generateContentWithRetry,
  // Exposed for unit testing
  _mapError,
  _wrapError,
  DEFAULT_TIMEOUT_MS,
};
