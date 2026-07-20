/**
 * config/vertex.config.js
 *
 * Vertex AI configuration and client factory for Kanila Backend.
 *
 * Authentication: Vertex AI Express API key (VERTEX_API_KEY)
 * Transport:      vertexai: true  — routes to Vertex AI, NOT Google AI Studio
 *
 * Forbidden:
 *   - Do NOT use GEMINI_API_KEY, GOOGLE_API_KEY, GOOGLE_GENAI_API_KEY
 *   - Do NOT use GOOGLE_APPLICATION_CREDENTIALS
 *   - Do NOT use ADC (Application Default Credentials)
 *   - Do NOT use service-account JSON
 *   - Do NOT hardcode API keys
 *   - Do NOT log API key values
 */

"use strict";

const { GoogleGenAI } = require("@google/genai");

// ─── Fail-fast validation ────────────────────────────────────────────────────

/**
 * Validate VERTEX_API_KEY and VERTEX_GEMINI_MODEL at load time.
 * Throws with a clear error code — never logs the key value.
 *
 * Called lazily on first client access so tests that don't need
 * a real client can stub or skip this check.
 */
function _validateVertexConfig() {
  const apiKey = (process.env.VERTEX_API_KEY || "").trim();
  if (!apiKey) {
    const err = new Error(
      "[Vertex] VERTEX_CONFIG_MISSING_API_KEY: VERTEX_API_KEY is not set or empty. " +
        "Set it in your .env file or environment."
    );
    err.code = "VERTEX_CONFIG_MISSING_API_KEY";
    throw err;
  }

  const model = (process.env.VERTEX_GEMINI_MODEL || "").trim();
  if (!model) {
    const err = new Error(
      "[Vertex] VERTEX_CONFIG_MISSING_MODEL: VERTEX_GEMINI_MODEL is not set or empty. " +
        "Set it in your .env file or environment."
    );
    err.code = "VERTEX_CONFIG_MISSING_MODEL";
    throw err;
  }

  return { apiKey, model };
}

// ─── Singleton client ────────────────────────────────────────────────────────

let _vertexClient = null;
let _validatedModel = null;

/**
 * Returns the shared GoogleGenAI client configured for Vertex AI.
 * Validates environment on first call.
 * @returns {{ client: GoogleGenAI, model: string }}
 */
function getVertexClient() {
  if (_vertexClient && _validatedModel) {
    return { client: _vertexClient, model: _validatedModel };
  }

  const { apiKey, model } = _validateVertexConfig();

  // vertexai: true — mandatory. This routes requests to Vertex AI, not Google AI Studio.
  _vertexClient = new GoogleGenAI({
    vertexai: true,
    apiKey,
  });

  _validatedModel = model;

  return { client: _vertexClient, model: _validatedModel };
}

/**
 * Returns only the configured model name.
 * Validates that VERTEX_GEMINI_MODEL is set.
 * @returns {string}
 */
function getVertexModel() {
  const model = (process.env.VERTEX_GEMINI_MODEL || "").trim();
  if (!model) {
    const err = new Error(
      "[Vertex] VERTEX_CONFIG_MISSING_MODEL: VERTEX_GEMINI_MODEL is not set or empty."
    );
    err.code = "VERTEX_CONFIG_MISSING_MODEL";
    throw err;
  }
  return model;
}

/**
 * Exposed for unit testing only — resets the singleton.
 * Do NOT call in production code.
 * @internal
 */
function _resetVertexClientForTesting() {
  _vertexClient = null;
  _validatedModel = null;
}

module.exports = {
  getVertexClient,
  getVertexModel,
  _validateVertexConfig,
  _resetVertexClientForTesting,
};
