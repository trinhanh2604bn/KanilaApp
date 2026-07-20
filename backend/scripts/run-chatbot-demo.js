#!/usr/bin/env node
/**
 * run-chatbot-demo.js
 * Kanila AI Assistant Demo Runner
 *
 * Loads all demo scenarios and sends them to the chatbot API.
 * Prints formatted results and generates a JSON report.
 *
 * Usage:
 *   node scripts/run-chatbot-demo.js
 *   node scripts/run-chatbot-demo.js --phase 8
 *   node scripts/run-chatbot-demo.js --id DEMO_MAKEUP_001
 *   node scripts/run-chatbot-demo.js --conversations
 *   node scripts/run-chatbot-demo.js --dry-run
 *
 * Options:
 *   --phase <1-9>      Run only scenarios from specific phase
 *   --id <DEMO_XXX>    Run only one scenario by ID
 *   --conversations    Run multi-turn conversation flows
 *   --dry-run          Print scenarios without hitting the API
 *   --verbose          Show full API response body
 *   --url <url>        Override API base URL (default: http://localhost:5000)
 *   --token <jwt>      Bearer token for authenticated scenarios
 */

"use strict";

const http  = require("http");
const https = require("https");
const path  = require("path");
const fs    = require("fs");

// ─────────────────────────────────────────────────────────────────────────────
// Configuration
// ─────────────────────────────────────────────────────────────────────────────

const args = parseArgs(process.argv.slice(2));
const BASE_URL   = args["--url"]   || process.env.CHATBOT_DEMO_URL  || "http://localhost:5000";
const JWT_TOKEN  = args["--token"] || process.env.CHATBOT_DEMO_TOKEN || null;
const DRY_RUN    = "--dry-run"    in args;
const VERBOSE    = "--verbose"    in args;
const CONV_MODE  = "--conversations" in args;
const PHASE_FILTER = args["--phase"] || null;
const ID_FILTER    = args["--id"]    || null;

const CHATBOT_ENDPOINT = `${BASE_URL}/api/chatbot/message`;

// Report output path
const REPORT_DIR  = path.join(__dirname, "..", "data");
const REPORT_PATH = path.join(REPORT_DIR, "chatbot-demo-report.json");

// ─────────────────────────────────────────────────────────────────────────────
// Load scenario data
// ─────────────────────────────────────────────────────────────────────────────

const { scenarios } = require("../data/chatbotDemoScenarios");
const { conversations } = require("../data/chatbotDemoConversations");

// ─────────────────────────────────────────────────────────────────────────────
// Terminal colors
// ─────────────────────────────────────────────────────────────────────────────

const C = {
  reset:  "\x1b[0m",
  bold:   "\x1b[1m",
  dim:    "\x1b[2m",
  green:  "\x1b[32m",
  red:    "\x1b[31m",
  yellow: "\x1b[33m",
  cyan:   "\x1b[36m",
  blue:   "\x1b[34m",
  magenta:"\x1b[35m",
  white:  "\x1b[37m",
  bgGreen: "\x1b[42m",
  bgRed:   "\x1b[41m",
};

function col(color, text) {
  return `${C[color]}${text}${C.reset}`;
}

function bold(text) {
  return `${C.bold}${text}${C.reset}`;
}

function separator(char = "─", len = 70) {
  return col("dim", char.repeat(len));
}

// ─────────────────────────────────────────────────────────────────────────────
// Argument parser
// ─────────────────────────────────────────────────────────────────────────────

function parseArgs(argv) {
  const result = {};
  for (let i = 0; i < argv.length; i++) {
    if (argv[i].startsWith("--")) {
      result[argv[i]] = argv[i + 1] && !argv[i + 1].startsWith("--") ? argv[i + 1] : true;
      if (result[argv[i]] !== true) i++;
    }
  }
  return result;
}

// ─────────────────────────────────────────────────────────────────────────────
// HTTP helper
// ─────────────────────────────────────────────────────────────────────────────

function httpPost(url, body, token = null) {
  return new Promise((resolve, reject) => {
    const parsed = new URL(url);
    const lib = parsed.protocol === "https:" ? https : http;

    const data = JSON.stringify(body);
    const headers = {
      "Content-Type":   "application/json",
      "Content-Length": Buffer.byteLength(data),
    };
    if (token) headers["Authorization"] = `Bearer ${token}`;

    const options = {
      hostname: parsed.hostname,
      port:     parsed.port || (parsed.protocol === "https:" ? 443 : 80),
      path:     parsed.pathname + parsed.search,
      method:   "POST",
      headers,
    };

    const req = lib.request(options, (res) => {
      let body = "";
      res.on("data", (chunk) => (body += chunk));
      res.on("end", () => {
        try {
          resolve({ status: res.statusCode, data: JSON.parse(body) });
        } catch (e) {
          resolve({ status: res.statusCode, data: { raw: body } });
        }
      });
    });

    req.on("error", reject);
    req.write(data);
    req.end();
  });
}

// ─────────────────────────────────────────────────────────────────────────────
// Scenario evaluation helpers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Evaluate whether a chatbot API response meets scenario expectations.
 * Returns { pass: boolean, issues: string[] }
 */
function evaluateScenario(scenario, response) {
  const issues = [];
  const data   = response.data?.data || {};

  // Must succeed at HTTP level
  if (response.status !== 200) {
    issues.push(`HTTP ${response.status} — API returned non-200`);
  }

  if (!response.data?.success) {
    issues.push(`API success=false: ${response.data?.error?.details || "unknown error"}`);
  }

  // Must have a bot message
  if (!data.bot_message || data.bot_message.trim().length === 0) {
    issues.push("bot_message is empty");
  }

  // Check expected output type
  if (scenario.expected_output_type && scenario.expected_output_type !== "text") {
    const replyType = data.reply_type || "";
    if (scenario.expected_output_type === "product_recommendation") {
      if (!["product_recommendation", "makeup_recommendation", "combo_recommendation"].includes(replyType)) {
        if (!data.products || data.products.length === 0) {
          issues.push(`Expected product_recommendation but got reply_type=${replyType} with 0 products`);
        }
      }
    } else if (scenario.expected_output_type === "order_tracking") {
      if (replyType !== "order_tracking" && !data.order) {
        // Guest is expected to get login prompt
        if (data.bot_message && data.bot_message.toLowerCase().includes("đăng nhập")) {
          // OK — guest login prompt
        } else {
          issues.push(`Expected order_tracking response but got reply_type=${replyType}`);
        }
      }
    } else if (scenario.expected_output_type === "support_ticket") {
      if (replyType !== "support_ticket") {
        // Guest gets login prompt — acceptable
        if (data.bot_message && data.bot_message.toLowerCase().includes("đăng nhập")) {
          // OK
        } else {
          issues.push(`Expected support_ticket response but got reply_type=${replyType}`);
        }
      }
    } else if (scenario.expected_output_type === "cart_summary") {
      if (replyType !== "cart_summary") {
        if (data.bot_message && data.bot_message.toLowerCase().includes("đăng nhập")) {
          // OK — guest login prompt
        } else {
          issues.push(`Expected cart_summary response but got reply_type=${replyType}`);
        }
      }
    } else if (scenario.expected_output_type === "cart_action") {
      if (replyType !== "cart_action") {
        if (data.bot_message && data.bot_message.toLowerCase().includes("đăng nhập")) {
          // OK
        } else {
          issues.push(`Expected cart_action response but got reply_type=${replyType}`);
        }
      }
    } else if (scenario.expected_output_type === "combo_recommendation") {
      if (!["combo_recommendation", "product_recommendation"].includes(replyType)) {
        issues.push(`Expected combo_recommendation but got reply_type=${replyType}`);
      }
    }
  }

  // Check products returned for product scenarios
  if (
    ["product_recommendation", "makeup_recommendation"].includes(scenario.expected_output_type) &&
    data.products && data.products.length === 0 &&
    !scenario.expected_output_type.includes("text")
  ) {
    issues.push("Expected products in response but got empty products array");
  }

  return { pass: issues.length === 0, issues };
}

/**
 * Extract key metrics from API response for display.
 */
function extractMetrics(responseData) {
  const data = responseData?.data || {};
  return {
    reply_type:    data.reply_type || "—",
    products:      (data.products || []).length,
    has_order:     !!data.order,
    has_ticket:    !!data.ticket,
    has_cart:      !!data.cart_summary || !!data.cart_action,
    quick_replies: (data.quick_replies || []).length,
    handoff:       !!data.handoff_required,
    context_used:  !!data.customer_context_used,
    message_len:   (data.bot_message || "").length,
    message_preview: (data.bot_message || "").slice(0, 120),
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// Pretty print scenario result
// ─────────────────────────────────────────────────────────────────────────────

function printScenarioResult(scenario, result, index, total) {
  const status = result.pass
    ? `${C.bgGreen}${C.bold} PASS ${C.reset}`
    : `${C.bgRed}${C.bold} FAIL ${C.reset}`;

  console.log(`\n${separator()}`);
  console.log(
    `${bold(col("cyan", `[${index}/${total}]`))} ${status} ` +
    `${bold(scenario.id)} — ${col("yellow", scenario.title)}`
  );
  console.log(`  ${col("dim", "Phase")}  : ${scenario.phase}`);
  console.log(`  ${col("dim", "Input")}  : ${col("white", `"${scenario.customer_message}"`)}`);
  console.log(`  ${col("dim", "Intent")} : ${col("magenta", scenario.expected_intent)}`);

  if (result.metrics) {
    const m = result.metrics;
    console.log(
      `  ${col("dim", "Output")} : type=${col("blue", m.reply_type)} | ` +
      `products=${col("green", String(m.products))} | ` +
      `quick_replies=${m.quick_replies}`
    );
    if (m.context_used) console.log(`  ${col("cyan", "★ Personalized")} customer profile used`);
    if (m.handoff)      console.log(`  ${col("yellow", "→ Handoff required")} to human support`);
    if (m.message_preview) {
      console.log(`  ${col("dim", "Reply")}  : ${m.message_preview}${m.message_len > 120 ? "..." : ""}`);
    }
  }

  if (!result.pass && result.issues) {
    console.log(`  ${col("red", "✗ Issues")}:`);
    result.issues.forEach((issue) => {
      console.log(`    ${col("red", "—")} ${issue}`);
    });
  }

  if (!result.pass && result.error) {
    console.log(`  ${col("red", "✗ Error")} : ${result.error}`);
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Run single scenario
// ─────────────────────────────────────────────────────────────────────────────

async function runScenario(scenario, sessionId = null) {
  const requestBody = {
    message: scenario.customer_message,
    source_screen: "demo_runner",
  };
  if (sessionId) requestBody.session_id = sessionId;

  if (DRY_RUN) {
    return {
      pass: true,
      dryRun: true,
      scenario,
      requestBody,
    };
  }

  try {
    const start    = Date.now();
    const response = await httpPost(CHATBOT_ENDPOINT, requestBody, JWT_TOKEN);
    const elapsed  = Date.now() - start;

    const evaluation = evaluateScenario(scenario, response);
    const metrics    = extractMetrics(response.data);

    if (VERBOSE) {
      console.log("\n  Full response:");
      console.log(JSON.stringify(response.data, null, 4).split("\n").map(l => "    " + l).join("\n"));
    }

    return {
      pass:     evaluation.pass,
      issues:   evaluation.issues,
      metrics,
      elapsed,
      scenario,
      response: {
        status:     response.status,
        reply_type: metrics.reply_type,
        products:   metrics.products,
        message:    metrics.message_preview,
        session_id: response.data?.data?.session_id,
      },
    };
  } catch (err) {
    return {
      pass:  false,
      error: err.message,
      scenario,
    };
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Run multi-turn conversation
// ─────────────────────────────────────────────────────────────────────────────

async function runConversation(conversation, index, total) {
  console.log(`\n${"═".repeat(70)}`);
  console.log(bold(col("cyan", `[Conv ${index}/${total}] ${conversation.conversation_id}`)));
  console.log(`  ${conversation.title}`);
  console.log(`  ${col("dim", conversation.description)}`);
  console.log(`  Phases: ${col("yellow", conversation.phases_covered.join(", "))}`);
  console.log();

  let sessionId = null;
  let turnsPassed = 0;
  let turnsFailed = 0;
  const turnResults = [];

  for (const [i, msg] of conversation.messages.entries()) {
    if (msg.role === "user") {
      const turnNum = Math.ceil((i + 1) / 2);
      const scenario = {
        id: `${conversation.conversation_id}_T${turnNum}`,
        phase: conversation.phases_covered[0],
        title: `Turn ${turnNum}`,
        customer_message: msg.text,
        expected_intent: msg.expected_intent || "auto",
        expected_output_type: msg.expected_reply_type || "text",
      };

      if (!DRY_RUN) {
        const result = await runScenario(scenario, sessionId);
        if (result.response?.session_id) sessionId = result.response.session_id;

        const statusIcon = result.pass ? col("green", "✓") : col("red", "✗");
        const replyInfo  = result.metrics
          ? `[${result.metrics.reply_type}, ${result.metrics.products} products]`
          : result.error ? `[ERROR: ${result.error}]` : "[dry-run]";

        console.log(`  Turn ${turnNum} ${statusIcon} ${col("dim", `"${msg.text}"`)}`);
        console.log(`           ${col("blue", replyInfo)}`);
        if (result.metrics?.message_preview) {
          console.log(`           ${col("dim", result.metrics.message_preview.slice(0, 80))}${result.metrics.message_len > 80 ? "..." : ""}`);
        }

        if (result.pass) turnsPassed++;
        else {
          turnsFailed++;
          if (result.issues) result.issues.forEach(iss => console.log(`           ${col("red", "  ✗ " + iss)}`));
        }

        turnResults.push({ turn: turnNum, ...result });

        // Small delay between turns to avoid rate limiting
        await new Promise(r => setTimeout(r, 300));
      } else {
        console.log(`  Turn ${turnNum} ${col("dim", "[dry-run]")} ${col("dim", `"${msg.text}"`)}`);
        turnsPassed++;
      }
    } else {
      // Bot expected response — just log what we expect
      if (msg.expected) {
        console.log(`           ${col("dim", "↳ Bot should: " + msg.expected.slice(0, 80))}${msg.expected.length > 80 ? "..." : ""}`);
      }
    }
  }

  const convPass = turnsFailed === 0;
  console.log(
    `\n  Result: ${convPass ? col("green", "✓ PASS") : col("red", "✗ FAIL")} ` +
    `(${turnsPassed} passed, ${turnsFailed} failed)`
  );

  return { conversation_id: conversation.conversation_id, pass: convPass, turnsPassed, turnsFailed, turnResults };
}

// ─────────────────────────────────────────────────────────────────────────────
// Main runner
// ─────────────────────────────────────────────────────────────────────────────

async function main() {
  console.log("\n" + "═".repeat(70));
  console.log(bold(col("cyan", "  KANILA AI CHATBOT DEMO RUNNER")));
  console.log("  Kanila Beauty Shopping Assistant — Full Capability Demo");
  console.log("═".repeat(70));
  console.log(`  API Endpoint : ${col("yellow", CHATBOT_ENDPOINT)}`);
  console.log(`  Auth Token   : ${JWT_TOKEN ? col("green", "Provided") : col("yellow", "None (guest mode)")}`);
  console.log(`  Mode         : ${DRY_RUN ? col("yellow", "DRY RUN") : col("green", "LIVE")}`);
  if (PHASE_FILTER) console.log(`  Phase Filter : Phase ${PHASE_FILTER}`);
  if (ID_FILTER)    console.log(`  ID Filter    : ${ID_FILTER}`);
  console.log("═".repeat(70) + "\n");

  // ── Conversation mode ────────────────────────────────────────────────────
  if (CONV_MODE) {
    console.log(bold(col("cyan", "▶ Running Multi-Turn Conversation Flows\n")));
    const convResults = [];

    for (const [i, conv] of conversations.entries()) {
      const result = await runConversation(conv, i + 1, conversations.length);
      convResults.push(result);
    }

    const convPassed = convResults.filter(r => r.pass).length;
    const convFailed = convResults.length - convPassed;

    console.log("\n" + "═".repeat(70));
    console.log(bold(col("cyan", "  CONVERSATION FLOW SUMMARY")));
    console.log("═".repeat(70));
    console.log(`  Total Conversations : ${conversations.length}`);
    console.log(`  ${col("green", `Passed : ${convPassed}`)}`);
    console.log(`  ${col("red",   `Failed : ${convFailed}`)}`);
    console.log("═".repeat(70) + "\n");
    return;
  }

  // ── Scenario mode ────────────────────────────────────────────────────────
  let targetScenarios = scenarios;

  if (ID_FILTER) {
    targetScenarios = scenarios.filter(s => s.id === ID_FILTER);
    if (targetScenarios.length === 0) {
      console.error(col("red", `No scenario found with ID: ${ID_FILTER}`));
      process.exit(1);
    }
  } else if (PHASE_FILTER) {
    const phaseNum = String(PHASE_FILTER);
    targetScenarios = scenarios.filter(s => s.phase.includes(`Phase ${phaseNum}`));
    if (targetScenarios.length === 0) {
      console.error(col("red", `No scenarios found for phase: ${phaseNum}`));
      process.exit(1);
    }
  }

  console.log(bold(col("cyan", `▶ Running ${targetScenarios.length} Scenarios\n`)));

  const results = [];
  let passed = 0;
  let failed = 0;

  // Group scenarios by phase for display
  const phaseGroups = {};
  targetScenarios.forEach(s => {
    if (!phaseGroups[s.phase]) phaseGroups[s.phase] = [];
    phaseGroups[s.phase].push(s);
  });

  let globalIndex = 0;

  for (const [phase, phaseScenarios] of Object.entries(phaseGroups)) {
    console.log(`\n${separator("═")}`);
    console.log(bold(col("blue", `  ${phase.toUpperCase()}`)));
    console.log(separator("═"));

    for (const scenario of phaseScenarios) {
      globalIndex++;
      const result = await runScenario(scenario);
      results.push({ scenario, result });

      printScenarioResult(scenario, result, globalIndex, targetScenarios.length);

      if (result.pass) passed++;
      else failed++;

      // Small delay between API calls to avoid overwhelming the server
      if (!DRY_RUN) await new Promise(r => setTimeout(r, 200));
    }
  }

  // ── Phase summary ─────────────────────────────────────────────────────────
  console.log("\n\n" + "═".repeat(70));
  console.log(bold(col("cyan", "  DEMO RESULTS SUMMARY")));
  console.log("═".repeat(70));

  const phaseStats = {};
  results.forEach(({ scenario, result }) => {
    const p = scenario.phase;
    if (!phaseStats[p]) phaseStats[p] = { pass: 0, fail: 0 };
    result.pass ? phaseStats[p].pass++ : phaseStats[p].fail++;
  });

  for (const [phase, stats] of Object.entries(phaseStats)) {
    const icon = stats.fail === 0 ? col("green", "✓") : col("red", "✗");
    console.log(`  ${icon} ${phase.padEnd(45)} Pass: ${col("green", String(stats.pass).padStart(2))}  Fail: ${col("red", String(stats.fail))}`);
  }

  console.log(separator());
  const passColor = failed === 0 ? "green" : "yellow";
  console.log(`  Total  : ${targetScenarios.length}`);
  console.log(`  ${col("green", `Passed : ${passed}`)}`);
  console.log(`  ${col(failed > 0 ? "red" : "green", `Failed : ${failed}`)}`);
  console.log(`  Score  : ${col(passColor, `${Math.round((passed / targetScenarios.length) * 100)}%`)}`);
  console.log("═".repeat(70) + "\n");

  // ── Generate JSON report ──────────────────────────────────────────────────
  const report = {
    generated_at: new Date().toISOString(),
    api_endpoint: CHATBOT_ENDPOINT,
    auth_mode: JWT_TOKEN ? "authenticated" : "guest",
    dry_run: DRY_RUN,
    summary: {
      total: targetScenarios.length,
      passed,
      failed,
      score_percent: Math.round((passed / targetScenarios.length) * 100),
    },
    phase_breakdown: phaseStats,
    results: results.map(({ scenario, result }) => ({
      id:              scenario.id,
      phase:           scenario.phase,
      title:           scenario.title,
      input:           scenario.customer_message,
      expected_intent: scenario.expected_intent,
      expected_output: scenario.expected_output_type,
      status:          result.pass ? "PASS" : "FAIL",
      issues:          result.issues || [],
      error:           result.error || null,
      elapsed_ms:      result.elapsed || null,
      actual: result.metrics ? {
        reply_type:    result.metrics.reply_type,
        products_count: result.metrics.products,
        message_len:   result.metrics.message_len,
        message_preview: result.metrics.message_preview,
        context_used:  result.metrics.context_used,
        handoff:       result.metrics.handoff,
      } : null,
    })),
    failed_scenarios: results
      .filter(({ result }) => !result.pass)
      .map(({ scenario, result }) => ({
        id:     scenario.id,
        input:  scenario.customer_message,
        expected_output: scenario.expected_output_type,
        issues: result.issues || [],
        error:  result.error || null,
        reason: result.issues?.[0] || result.error || "Unknown",
      })),
  };

  try {
    if (!fs.existsSync(REPORT_DIR)) fs.mkdirSync(REPORT_DIR, { recursive: true });
    fs.writeFileSync(REPORT_PATH, JSON.stringify(report, null, 2), "utf8");
    console.log(`  ${col("green", "✓")} Report saved to: ${col("cyan", REPORT_PATH)}`);
  } catch (err) {
    console.error(col("red", `  ✗ Could not save report: ${err.message}`));
  }

  // Exit with code 1 if any scenarios failed (useful for CI)
  if (failed > 0 && !DRY_RUN) process.exit(1);
}

// ─────────────────────────────────────────────────────────────────────────────
// Entry point
// ─────────────────────────────────────────────────────────────────────────────

main().catch((err) => {
  console.error(col("red", `\n[FATAL] ${err.message}`));
  if (VERBOSE) console.error(err.stack);
  process.exit(1);
});
