/**
 * tests/unit/vertexGemini.service.test.js
 *
 * Unit tests for services/ai/vertexGemini.service.js
 * and config/vertex.config.js
 *
 * Rules:
 *   - Does NOT call Vertex AI network — all SDK calls are mocked
 *   - Does NOT require a real VERTEX_API_KEY
 *   - Uses fake/placeholder keys for test isolation
 */

"use strict";

// ─── Mock GoogleGenAI ─────────────────────────────────────────────────────────
// NOTE: Jest 30 requires mock factory to only reference allowed globals.
// We use a module-level jest.fn() that tests can configure via mockImplementation.

// This mock is hoisted before all require() calls by Jest.
// Variables prefixed with "mock" are allowed in jest.mock() factories.
let mockGenerateContentImpl = null;
let mockCapturedConstructorArgs = null;

jest.mock("@google/genai", () => {
  const mockGenAI = {
    GoogleGenAI: jest.fn().mockImplementation((args) => {
      // Store constructor args for inspection — accessed via module-scope var
      // (Jest allows 'jest' and module globals in factory)
      return {
        _constructorArgs: args,
        models: {
          generateContent: jest.fn(async (_payload) => {
            // Default response if no override set
            return {
              text: "default mock response",
              usageMetadata: { promptTokenCount: 5, candidatesTokenCount: 10 },
              candidates: [{ finishReason: "STOP" }],
            };
          }),
        },
        chats: {
          create: jest.fn().mockReturnValue({
            sendMessage: jest.fn(async () => ({ text: "chat mock" })),
          }),
        },
      };
    }),
  };
  return mockGenAI;
});

// ─── Helpers to get the mock and configure it ─────────────────────────────────

/**
 * Get the mocked GoogleGenAI constructor from the mocked module.
 * Called after jest.resetModules() + require('@google/genai').
 */
function getMockedGoogleGenAI() {
  return require("@google/genai").GoogleGenAI;
}

/**
 * Get the last-instantiated mock client.
 */
function getLastClient(MockedGoogleGenAI) {
  const calls = MockedGoogleGenAI.mock.instances;
  return calls[calls.length - 1];
}

// ─── Environment helpers ──────────────────────────────────────────────────────

const ORIG_ENV = { ...process.env };

function setEnv(overrides = {}) {
  delete process.env.VERTEX_API_KEY;
  delete process.env.VERTEX_GEMINI_MODEL;
  Object.assign(process.env, overrides);
}

function restoreEnv() {
  delete process.env.VERTEX_API_KEY;
  delete process.env.VERTEX_GEMINI_MODEL;
  delete process.env.GOOGLE_API_KEY;
  delete process.env.GOOGLE_GENAI_API_KEY;
  delete process.env.GOOGLE_APPLICATION_CREDENTIALS;
  Object.assign(process.env, ORIG_ENV);
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. config/vertex.config.js — validation
// ─────────────────────────────────────────────────────────────────────────────

describe("vertex.config — fail-fast validation", () => {
  afterEach(() => {
    restoreEnv();
    jest.resetModules();
  });

  // Test 1
  it("throws VERTEX_CONFIG_MISSING_API_KEY when VERTEX_API_KEY is not set", () => {
    setEnv({ VERTEX_GEMINI_MODEL: "gemini-2.5-flash" });
    const { _validateVertexConfig } = require("../../config/vertex.config");
    let thrown;
    try { _validateVertexConfig(); } catch (e) { thrown = e; }
    expect(thrown).toBeDefined();
    expect(thrown.code).toBe("VERTEX_CONFIG_MISSING_API_KEY");
  });

  // Test 2
  it("throws VERTEX_CONFIG_MISSING_API_KEY when VERTEX_API_KEY is only whitespace", () => {
    setEnv({ VERTEX_API_KEY: "   ", VERTEX_GEMINI_MODEL: "gemini-2.5-flash" });
    const { _validateVertexConfig } = require("../../config/vertex.config");
    let thrown;
    try { _validateVertexConfig(); } catch (e) { thrown = e; }
    expect(thrown).toBeDefined();
    expect(thrown.code).toBe("VERTEX_CONFIG_MISSING_API_KEY");
  });

  // Test 3
  it("throws VERTEX_CONFIG_MISSING_MODEL when VERTEX_GEMINI_MODEL is not set", () => {
    setEnv({ VERTEX_API_KEY: "fake-api-key-for-test" });
    const { _validateVertexConfig } = require("../../config/vertex.config");
    let thrown;
    try { _validateVertexConfig(); } catch (e) { thrown = e; }
    expect(thrown).toBeDefined();
    expect(thrown.code).toBe("VERTEX_CONFIG_MISSING_MODEL");
  });

  // Test 4
  it("throws VERTEX_CONFIG_MISSING_MODEL when VERTEX_GEMINI_MODEL is only whitespace", () => {
    setEnv({ VERTEX_API_KEY: "fake-api-key-for-test", VERTEX_GEMINI_MODEL: "   " });
    const { _validateVertexConfig } = require("../../config/vertex.config");
    let thrown;
    try { _validateVertexConfig(); } catch (e) { thrown = e; }
    expect(thrown).toBeDefined();
    expect(thrown.code).toBe("VERTEX_CONFIG_MISSING_MODEL");
  });

  // Test 5
  it("passes validation with both vars set and returns trimmed values", () => {
    setEnv({ VERTEX_API_KEY: "fake-api-key-for-test", VERTEX_GEMINI_MODEL: "gemini-2.5-flash" });
    const { _validateVertexConfig } = require("../../config/vertex.config");
    expect(() => _validateVertexConfig()).not.toThrow();
    const { apiKey, model } = _validateVertexConfig();
    expect(apiKey).toBe("fake-api-key-for-test");
    expect(model).toBe("gemini-2.5-flash");
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// 2. config/vertex.config.js — client initialisation
// ─────────────────────────────────────────────────────────────────────────────

describe("vertex.config — getVertexClient", () => {
  afterEach(() => {
    restoreEnv();
    jest.resetModules();
  });

  // Test 6: vertexai: true is passed to constructor
  it("passes vertexai: true to GoogleGenAI constructor", () => {
    setEnv({ VERTEX_API_KEY: "fake-api-key-for-test", VERTEX_GEMINI_MODEL: "gemini-2.5-flash" });
    const { getVertexClient, _resetVertexClientForTesting } = require("../../config/vertex.config");
    const MockedGoogleGenAI = getMockedGoogleGenAI();
    MockedGoogleGenAI.mockClear();
    _resetVertexClientForTesting();

    getVertexClient();

    expect(MockedGoogleGenAI).toHaveBeenCalledWith(
      expect.objectContaining({ vertexai: true })
    );
  });

  // Test 7: apiKey from VERTEX_API_KEY
  it("uses VERTEX_API_KEY for the apiKey parameter", () => {
    setEnv({ VERTEX_API_KEY: "test-vertex-key-xyz", VERTEX_GEMINI_MODEL: "gemini-2.5-flash" });
    const { getVertexClient, _resetVertexClientForTesting } = require("../../config/vertex.config");
    const MockedGoogleGenAI = getMockedGoogleGenAI();
    MockedGoogleGenAI.mockClear();
    _resetVertexClientForTesting();

    getVertexClient();

    expect(MockedGoogleGenAI).toHaveBeenCalledWith(
      expect.objectContaining({ apiKey: "test-vertex-key-xyz" })
    );
  });

  // Test 8: model from VERTEX_GEMINI_MODEL
  it("returns VERTEX_GEMINI_MODEL as the model name", () => {
    setEnv({ VERTEX_API_KEY: "fake-api-key-for-test", VERTEX_GEMINI_MODEL: "gemini-2.5-pro" });
    const { getVertexClient, _resetVertexClientForTesting } = require("../../config/vertex.config");
    _resetVertexClientForTesting();
    const { model } = getVertexClient();
    expect(model).toBe("gemini-2.5-pro");
  });

  // Test 9: model is always from env, not overridable
  it("returns the same model on repeated calls (no caller override)", () => {
    setEnv({ VERTEX_API_KEY: "fake-api-key-for-test", VERTEX_GEMINI_MODEL: "gemini-2.5-flash" });
    const { getVertexClient, _resetVertexClientForTesting } = require("../../config/vertex.config");
    _resetVertexClientForTesting();
    const { model: m1 } = getVertexClient();
    const { model: m2 } = getVertexClient();
    expect(m1).toBe("gemini-2.5-flash");
    expect(m2).toBe("gemini-2.5-flash");
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// 3. vertexGemini.service — input validation
// ─────────────────────────────────────────────────────────────────────────────

describe("vertexGemini.service — input validation", () => {
  let generateContent;

  beforeEach(() => {
    jest.resetModules();
    setEnv({ VERTEX_API_KEY: "fake-api-key-for-test", VERTEX_GEMINI_MODEL: "gemini-2.5-flash" });
    // Reset mock state
    const { GoogleGenAI } = require("@google/genai");
    GoogleGenAI.mockClear();
    // Set default mock to return a valid response
    GoogleGenAI.mockImplementation(() => ({
      models: {
        generateContent: jest.fn(async () => ({
          text: "Mock response text",
          usageMetadata: { promptTokenCount: 10 },
          candidates: [{ finishReason: "STOP" }],
        })),
      },
    }));
    require("../../config/vertex.config")._resetVertexClientForTesting();
    generateContent = require("../../services/ai/vertexGemini.service").generateContent;
  });

  afterEach(() => {
    restoreEnv();
    jest.resetModules();
  });

  // Test 10
  it("throws VERTEX_BAD_REQUEST for empty prompt string", async () => {
    await expect(generateContent({ prompt: "" })).rejects.toMatchObject({
      code: "VERTEX_BAD_REQUEST",
    });
  });

  // Test 11
  it("throws VERTEX_BAD_REQUEST for whitespace-only prompt", async () => {
    await expect(generateContent({ prompt: "   " })).rejects.toMatchObject({
      code: "VERTEX_BAD_REQUEST",
    });
  });

  // Test 12
  it("throws VERTEX_BAD_REQUEST when prompt is a number", async () => {
    await expect(generateContent({ prompt: 42 })).rejects.toMatchObject({
      code: "VERTEX_BAD_REQUEST",
    });
  });

  // Test 13
  it("throws VERTEX_BAD_REQUEST when prompt is null", async () => {
    await expect(generateContent({ prompt: null })).rejects.toMatchObject({
      code: "VERTEX_BAD_REQUEST",
    });
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// 4. vertexGemini.service — response normalisation
// ─────────────────────────────────────────────────────────────────────────────

describe("vertexGemini.service — response normalisation", () => {
  let generateContent;
  let mockGenerateContentFn;

  beforeEach(() => {
    jest.resetModules();
    setEnv({ VERTEX_API_KEY: "fake-api-key-for-test", VERTEX_GEMINI_MODEL: "gemini-2.5-flash" });
    const { GoogleGenAI } = require("@google/genai");
    GoogleGenAI.mockClear();
    mockGenerateContentFn = jest.fn();
    GoogleGenAI.mockImplementation(() => ({
      models: { generateContent: mockGenerateContentFn },
    }));
    require("../../config/vertex.config")._resetVertexClientForTesting();
    generateContent = require("../../services/ai/vertexGemini.service").generateContent;
  });

  afterEach(() => {
    restoreEnv();
    jest.resetModules();
  });

  // Test 14
  it("returns text, model and usageMetadata from response", async () => {
    const usage = { promptTokenCount: 5, candidatesTokenCount: 20 };
    mockGenerateContentFn.mockResolvedValueOnce({
      text: "Hello from Vertex AI",
      usageMetadata: usage,
      candidates: [{ finishReason: "STOP" }],
    });
    const result = await generateContent({ prompt: "Hello" });
    expect(result.text).toBe("Hello from Vertex AI");
    expect(result.model).toBe("gemini-2.5-flash");
    expect(result.usageMetadata).toEqual(usage);
  });

  // Test 15
  it("preserves usageMetadata when present", async () => {
    const usage = { promptTokenCount: 10, candidatesTokenCount: 30, totalTokenCount: 40 };
    mockGenerateContentFn.mockResolvedValueOnce({
      text: "Some response",
      usageMetadata: usage,
      candidates: [{ finishReason: "STOP" }],
    });
    const result = await generateContent({ prompt: "Test" });
    expect(result.usageMetadata).toEqual(usage);
  });

  // Test 16
  it("throws VERTEX_EMPTY_RESPONSE when model returns empty text", async () => {
    mockGenerateContentFn.mockResolvedValueOnce({
      text: "",
      usageMetadata: null,
      candidates: [],
    });
    await expect(generateContent({ prompt: "Test" })).rejects.toMatchObject({
      code: "VERTEX_EMPTY_RESPONSE",
    });
  });

  // Test 17
  it("throws VERTEX_EMPTY_RESPONSE when model returns only whitespace", async () => {
    mockGenerateContentFn.mockResolvedValueOnce({
      text: "   ",
      usageMetadata: null,
      candidates: [],
    });
    await expect(generateContent({ prompt: "Test" })).rejects.toMatchObject({
      code: "VERTEX_EMPTY_RESPONSE",
    });
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// 5. vertexGemini.service — error mapping
// ─────────────────────────────────────────────────────────────────────────────

describe("vertexGemini.service — error mapping (_mapError)", () => {
  let _mapError, _wrapError;

  beforeEach(() => {
    jest.resetModules();
    setEnv({ VERTEX_API_KEY: "fake-api-key-for-test", VERTEX_GEMINI_MODEL: "gemini-2.5-flash" });
    const svc = require("../../services/ai/vertexGemini.service");
    _mapError = svc._mapError;
    _wrapError = svc._wrapError;
  });

  afterEach(() => {
    restoreEnv();
    jest.resetModules();
  });

  // Test 18
  it("maps 401 status to VERTEX_AUTHENTICATION_FAILED", () => {
    const err = Object.assign(new Error("Unauthorized"), { status: 401 });
    expect(_mapError(err).code).toBe("VERTEX_AUTHENTICATION_FAILED");
  });

  // Test 19
  it("maps 403 status to VERTEX_PERMISSION_DENIED", () => {
    const err = Object.assign(new Error("Forbidden"), { status: 403 });
    expect(_mapError(err).code).toBe("VERTEX_PERMISSION_DENIED");
  });

  // Test 20
  it("maps 404 status to VERTEX_MODEL_NOT_FOUND", () => {
    const err = Object.assign(new Error("Not found"), { status: 404 });
    expect(_mapError(err).code).toBe("VERTEX_MODEL_NOT_FOUND");
  });

  // Test 21
  it("maps 429 status to VERTEX_RATE_LIMITED", () => {
    const err = Object.assign(new Error("Too many requests"), { status: 429 });
    expect(_mapError(err).code).toBe("VERTEX_RATE_LIMITED");
  });

  // Test 22
  it("maps 500 status to VERTEX_UPSTREAM_ERROR", () => {
    const err = Object.assign(new Error("Server error"), { status: 500 });
    expect(_mapError(err).code).toBe("VERTEX_UPSTREAM_ERROR");
  });

  // Test 23
  it("maps VERTEX_TIMEOUT code to VERTEX_TIMEOUT", () => {
    const err = Object.assign(new Error("timed out"), { code: "VERTEX_TIMEOUT" });
    expect(_mapError(err).code).toBe("VERTEX_TIMEOUT");
  });

  // Test 24
  it("does not expose raw upstream body in wrapped error message", () => {
    const rawErr = Object.assign(
      new Error('{"error":{"code":403,"message":"raw detail that should not leak"}}'),
      { status: 403 }
    );
    const wrapped = _wrapError(rawErr);
    expect(wrapped.message).not.toContain("raw detail that should not leak");
    expect(wrapped.code).toBe("VERTEX_PERMISSION_DENIED");
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// 6. vertexGemini.service — API key security
// ─────────────────────────────────────────────────────────────────────────────

describe("vertexGemini.service — security: API key not in error messages", () => {
  afterEach(() => {
    restoreEnv();
    jest.resetModules();
  });

  // Test 25
  it("API key value does not appear in any thrown error message", async () => {
    const fakeKey = "fake-secret-key-should-not-appear-in-errors";
    setEnv({ VERTEX_API_KEY: fakeKey, VERTEX_GEMINI_MODEL: "gemini-2.5-flash" });

    const { GoogleGenAI } = require("@google/genai");
    GoogleGenAI.mockClear();
    GoogleGenAI.mockImplementation(() => ({
      models: {
        generateContent: jest.fn(async () => {
          const e = Object.assign(new Error("quota exceeded"), { status: 429 });
          throw e;
        }),
      },
    }));
    require("../../config/vertex.config")._resetVertexClientForTesting();
    const { generateContent } = require("../../services/ai/vertexGemini.service");

    let thrown;
    try {
      await generateContent({ prompt: "hello" });
    } catch (e) {
      thrown = e;
    }

    expect(thrown).toBeDefined();
    expect(thrown.message).not.toContain(fakeKey);
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// 7. vertexReviewAi.provider — JSON output validation
// ─────────────────────────────────────────────────────────────────────────────

describe("vertexReviewAi.provider — _validateAndSanitizeOutput", () => {
  let _validateAndSanitizeOutput;

  beforeEach(() => {
    jest.resetModules();
    _validateAndSanitizeOutput =
      require("../../services/reviewAi/vertexReviewAi.provider")._validateAndSanitizeOutput;
  });

  afterEach(() => {
    jest.resetModules();
  });

  // Test 26
  it("accepts valid output structure", () => {
    const valid = {
      short_summary: "Tốt",
      positive_themes: [
        {
          code: "MOISTURIZING",
          title: "Dưỡng ẩm tốt",
          description: "Khách hàng hài lòng",
          supporting_review_refs: ["Review 1"],
        },
      ],
      negative_themes: [],
      common_experiences: ["Da mịn hơn"],
      usage_tips: ["Dùng buổi tối"],
      cautions: [],
    };
    expect(() => _validateAndSanitizeOutput(valid)).not.toThrow();
  });

  // Test 27
  it("throws VERTEX_INVALID_JSON_RESPONSE when short_summary is missing", () => {
    let thrown;
    try {
      _validateAndSanitizeOutput({
        positive_themes: [], negative_themes: [], common_experiences: [],
        usage_tips: [], cautions: [],
      });
    } catch (e) { thrown = e; }
    expect(thrown).toBeDefined();
    expect(thrown.code).toBe("VERTEX_INVALID_JSON_RESPONSE");
  });

  // Test 28
  it("throws VERTEX_INVALID_JSON_RESPONSE when short_summary is empty string", () => {
    let thrown;
    try {
      _validateAndSanitizeOutput({
        short_summary: "   ",
        positive_themes: [], negative_themes: [], common_experiences: [],
        usage_tips: [], cautions: [],
      });
    } catch (e) { thrown = e; }
    expect(thrown).toBeDefined();
    expect(thrown.code).toBe("VERTEX_INVALID_JSON_RESPONSE");
  });

  // Test 29
  it("throws VERTEX_INVALID_JSON_RESPONSE when positive_themes is not an array", () => {
    let thrown;
    try {
      _validateAndSanitizeOutput({
        short_summary: "OK",
        positive_themes: "not-an-array",
        negative_themes: [], common_experiences: [], usage_tips: [], cautions: [],
      });
    } catch (e) { thrown = e; }
    expect(thrown).toBeDefined();
    expect(thrown.code).toBe("VERTEX_INVALID_JSON_RESPONSE");
  });

  // Test 30
  it("sanitizes supporting_review_refs to strings only", () => {
    const output = _validateAndSanitizeOutput({
      short_summary: "OK",
      positive_themes: [
        {
          code: "GOOD",
          title: "Good",
          description: "Desc",
          supporting_review_refs: ["Review 1", 42, null, "Review 3"],
        },
      ],
      negative_themes: [],
      common_experiences: [],
      usage_tips: [],
      cautions: [],
    });
    expect(output.positive_themes[0].supporting_review_refs).toEqual(["Review 1", "Review 3"]);
  });

  // Test 31
  it("throws VERTEX_INVALID_JSON_RESPONSE for null input", () => {
    let thrown;
    try { _validateAndSanitizeOutput(null); } catch (e) { thrown = e; }
    expect(thrown).toBeDefined();
    expect(thrown.code).toBe("VERTEX_INVALID_JSON_RESPONSE");
  });

  // Test 32
  it("throws VERTEX_INVALID_JSON_RESPONSE when input is an array", () => {
    let thrown;
    try { _validateAndSanitizeOutput([]); } catch (e) { thrown = e; }
    expect(thrown).toBeDefined();
    expect(thrown.code).toBe("VERTEX_INVALID_JSON_RESPONSE");
  });
});
