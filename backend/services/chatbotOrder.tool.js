/**
 * chatbotOrder.tool.js
 * Secure order lookup for the Kanila chatbot.
 *
 * SECURITY CONTRACT:
 * - Never return another customer's order.
 * - Never expose: cost, margin, admin notes, internal payment tokens,
 *   raw payment intents, raw shipping provider credentials.
 * - Only return normalized, safe fields defined in SAFE_ORDER_FIELDS.
 *
 * Field names used here are verified from actual schema inspection:
 *   Order:              customer_id, order_number, order_status, payment_status,
 *                       fulfillment_status, placed_at, confirmed_at, created_at
 *   OrderTotal:         grand_total_amount, order_id
 *   OrderItem:          order_id, product_name_snapshot, quantity
 *   OrderStatusHistory: order_id, new_order_status, new_fulfillment_status, changed_at
 *   Shipment:           order_id, shipmentStatus, deliveredAt
 *   Customer:           account_id (→ _id = customer_id)
 */

const mongoose = require("mongoose");
const Order = require("../models/order.model");
const OrderTotal = require("../models/orderTotal.model");
const OrderItem = require("../models/orderItem.model");
const OrderStatusHistory = require("../models/orderStatusHistory.model");
const Shipment = require("../models/shipment.model");
const Customer = require("../models/customer.model");

// ─────────────────────────────────────────────────────────────────────────────
// Status label maps (using actual enum values verified from schema)
// ─────────────────────────────────────────────────────────────────────────────

const ORDER_STATUS_LABELS = {
  pending:    "Chờ xác nhận",
  confirmed:  "Đã xác nhận",
  processing: "Đang xử lý",
  completed:  "Hoàn tất",
  cancelled:  "Đã hủy",
  returned:   "Đã hoàn trả",
};

const PAYMENT_STATUS_LABELS = {
  unpaid:             "Chưa thanh toán",
  pending:            "Đang xử lý thanh toán",
  authorized:         "Đã ủy quyền thanh toán",
  paid:               "Đã thanh toán",
  failed:             "Thanh toán thất bại",
  partially_refunded: "Đã hoàn tiền một phần",
  refunded:           "Đã hoàn tiền",
};

const FULFILLMENT_STATUS_LABELS = {
  unfulfilled:        "Chưa xử lý",
  preparing:          "Đang chuẩn bị hàng",
  partially_shipped:  "Giao hàng một phần",
  shipped:            "Đã giao cho vận chuyển",
  in_transit:         "Đang giao hàng",
  delivered:          "Đã giao hàng",
  return_requested:   "Yêu cầu hoàn trả",
  return_approved:    "Duyệt hoàn trả",
  partially_returned: "Hoàn trả một phần",
  returned:           "Đã hoàn trả",
};

/** Map fulfillment → timeline step descriptions */
function fulfillmentDescription(status) {
  const desc = {
    unfulfilled:       "Kanila đã tiếp nhận đơn hàng.",
    preparing:         "Kanila đang chuẩn bị và đóng gói hàng.",
    partially_shipped: "Một phần đơn hàng đã được giao cho đơn vị vận chuyển.",
    shipped:           "Đơn hàng đã được bàn giao cho đơn vị vận chuyển.",
    in_transit:        "Đơn hàng đang trên đường giao đến bạn.",
    delivered:         "Đơn hàng đã được giao thành công.",
    return_requested:  "Yêu cầu hoàn trả đang được xem xét.",
    return_approved:   "Yêu cầu hoàn trả đã được duyệt.",
    partially_returned:"Một phần hàng đã được hoàn trả.",
    returned:          "Đơn hàng đã được hoàn trả đầy đủ.",
  };
  return desc[status] || "Trạng thái đang được cập nhật.";
}

// ─────────────────────────────────────────────────────────────────────────────
// Order code extraction from user message
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Try to extract an order number/code from a user message.
 * Handles patterns like: #DH001, KNL123456, ĐH-2026-001, KNL20260708-001, etc.
 *
 * Rules:
 * - Code must be ≥4 alphanumeric chars (prevents matching single Vietnamese chars).
 * - When preceded by a label keyword, the code must follow right after optional
 *   whitespace/#. No fallthrough to partial Vietnamese words.
 *
 * @param {string} message
 * @returns {string|null}
 */
function extractOrderCode(message) {
  // 1. Explicit hashtag followed by ≥3 alphanumeric/dash chars
  const hashPattern = /#([A-Z0-9][A-Z0-9\-]{2,})/i;

  // 2. KNL-prefixed codes (Kanila order numbers)
  const knlPattern = /\b(KNL[A-Z0-9\-]{4,})/i;

  // 3. Label keyword immediately before a code that starts with a letter/digit
  //    and is at least 4 chars — avoids matching Vietnamese word fragments
  const labelPattern = /(?:mã đơn|mã đơn hàng|đơn hàng|order)[:\s#]+([A-Z0-9][A-Z0-9\-]{3,})/i;

  const patterns = [knlPattern, hashPattern, labelPattern];

  for (const re of patterns) {
    const m = message.match(re);
    if (m && m[1] && m[1].length >= 4) {
      return m[1].toUpperCase();
    }
  }
  return null;
}

// ─────────────────────────────────────────────────────────────────────────────
// Normalize order to safe client-facing object
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Build normalized order object for chatbot response.
 * Never exposes internal fields.
 */
async function normalizeOrder(order) {
  const orderId = order._id;

  // Fetch supporting data in parallel (all non-fatal)
  const [total, items, history, shipment] = await Promise.all([
    OrderTotal.findOne({ order_id: orderId })
      .select("grand_total_amount")
      .lean()
      .catch(() => null),
    OrderItem.find({ order_id: orderId })
      .select("product_name_snapshot quantity")
      .lean()
      .catch(() => []),
    OrderStatusHistory.find({ order_id: orderId })
      .sort({ changed_at: 1 })
      .select("new_order_status new_fulfillment_status changed_at change_reason")
      .lean()
      .catch(() => []),
    Shipment.findOne({ order_id: orderId })
      .select("shipmentStatus deliveredAt shippedAt")
      .lean()
      .catch(() => null),
  ]);

  // Build timeline from status history
  const timelineSteps = [];

  // Always include "placed" as the first step
  timelineSteps.push({
    status: "pending",
    label: ORDER_STATUS_LABELS["pending"],
    time: order.placed_at || order.created_at,
    description: "Kanila đã tiếp nhận đơn hàng của bạn.",
  });

  // Add confirmed step if available
  if (order.confirmed_at) {
    timelineSteps.push({
      status: "confirmed",
      label: ORDER_STATUS_LABELS["confirmed"],
      time: order.confirmed_at,
      description: "Đơn hàng đã được xác nhận.",
    });
  }

  // Add fulfillment history steps (unique statuses only)
  const seenStatuses = new Set(["pending"]);
  if (order.confirmed_at) seenStatuses.add("confirmed");
  for (const h of history) {
    const fs = h.new_fulfillment_status;
    if (fs && !seenStatuses.has(fs)) {
      seenStatuses.add(fs);
      timelineSteps.push({
        status: fs,
        label: FULFILLMENT_STATUS_LABELS[fs] || fs,
        time: h.changed_at,
        description: fulfillmentDescription(fs),
      });
    }
  }

  // Add delivered step from shipment if not already in history
  if (shipment && shipment.deliveredAt && !seenStatuses.has("delivered")) {
    timelineSteps.push({
      status: "delivered",
      label: FULFILLMENT_STATUS_LABELS["delivered"],
      time: shipment.deliveredAt,
      description: "Đơn hàng đã được giao thành công.",
    });
  }

  // Derive next action hint
  let nextAction = "Bạn có thể liên hệ hỗ trợ nếu cần thêm thông tin.";
  if (order.fulfillment_status === "in_transit") {
    nextAction = "Đơn hàng đang trên đường đến. Vui lòng giữ liên lạc để nhận hàng.";
  } else if (order.fulfillment_status === "delivered") {
    nextAction = "Đơn hàng đã được giao. Hãy xem xét để lại đánh giá sản phẩm nhé!";
  } else if (order.order_status === "cancelled") {
    nextAction = "Đơn hàng đã bị hủy. Bạn cần hỗ trợ thêm không?";
  } else if (order.order_status === "pending" || order.fulfillment_status === "unfulfilled") {
    nextAction = "Đơn hàng đang được xử lý. Kanila sẽ sớm cập nhật trạng thái.";
  }

  return {
    order_id: orderId.toString(),
    order_code: order.order_number,
    status: order.order_status,
    status_label: ORDER_STATUS_LABELS[order.order_status] || order.order_status,
    fulfillment_status: order.fulfillment_status,
    fulfillment_status_label: FULFILLMENT_STATUS_LABELS[order.fulfillment_status] || order.fulfillment_status,
    payment_status: order.payment_status,
    payment_status_label: PAYMENT_STATUS_LABELS[order.payment_status] || order.payment_status,
    total_amount: total ? total.grand_total_amount : null,
    created_at: order.placed_at || order.created_at,
    items_count: items.length,
    items_preview: items.slice(0, 3).map((i) => ({
      name: i.product_name_snapshot,
      quantity: i.quantity,
    })),
    timeline: timelineSteps,
    next_action: nextAction,
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// Main lookup function
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Find order(s) for the authenticated user.
 * SECURITY: only queries orders for the current user's customer_id.
 *
 * @param {object} params
 * @param {string} params.accountId   — from req.user.account_id
 * @param {string|null} params.message — raw user message (to extract order code)
 * @returns {Promise<{ order: object|null, notFound: boolean, loginRequired: boolean }>}
 */
async function findOrderForUser({ accountId, message }) {
  // 1. Guest: cannot expose order data
  if (!accountId) {
    return { order: null, notFound: false, loginRequired: true };
  }

  // 2. Resolve customer_id from account_id
  let customer;
  try {
    customer = await Customer.findOne({ account_id: accountId })
      .select("_id")
      .lean();
  } catch (_) {
    return { order: null, notFound: false, loginRequired: false };
  }

  if (!customer) {
    return { order: null, notFound: false, loginRequired: true };
  }

  const customerId = customer._id;

  // 3. Try to extract order code from message
  const orderCode = message ? extractOrderCode(message) : null;

  let orderDoc;
  if (orderCode) {
    // Find specific order by code — only within current user's orders (security)
    orderDoc = await Order.findOne({
      customer_id: customerId,
      order_number: orderCode,
    })
      .select("-checkout_session_id -guest_session_id -guest_email -guest_phone -guest_full_name -currency_code")
      .lean()
      .catch(() => null);

    if (!orderDoc) {
      // Order code given but not found in this user's account
      return { order: null, notFound: true, loginRequired: false, orderCode };
    }
  } else {
    // No order code — return most recent order
    orderDoc = await Order.findOne({ customer_id: customerId })
      .sort({ placed_at: -1, created_at: -1 })
      .select("-checkout_session_id -guest_session_id -guest_email -guest_phone -guest_full_name -currency_code")
      .lean()
      .catch(() => null);

    if (!orderDoc) {
      return { order: null, notFound: true, loginRequired: false };
    }
  }

  // 4. Normalize to safe client object
  const normalizedOrder = await normalizeOrder(orderDoc);
  return { order: normalizedOrder, notFound: false, loginRequired: false };
}

module.exports = { findOrderForUser, extractOrderCode };
