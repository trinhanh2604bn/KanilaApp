/**
 * chatbotDemoConversations.js
 * Kanila AI Assistant — Multi-turn Demo Conversations.
 *
 * 10 complete multi-turn conversation flows showcasing the full
 * customer shopping journey across all chatbot phases.
 *
 * Each conversation simulates a realistic user interaction that
 * can be demonstrated live to stakeholders and investors.
 *
 * Usage:
 *   const { conversations } = require('./data/chatbotDemoConversations');
 *
 * Run all demos:
 *   node scripts/run-chatbot-demo.js
 */

"use strict";

const conversations = [

  // ─────────────────────────────────────────────────────────────────────────
  // FLOW 01: Customer buys full wedding makeup set
  // Phases: 8 (Makeup Commerce) → 5A (Add to Cart)
  // ─────────────────────────────────────────────────────────────────────────
  {
    conversation_id: "MAKEUP_FLOW_01",
    title: "Khách mua bộ makeup đám cưới",
    description: "Demonstrates: Occasion detection → Wedding makeup kit → Add to cart",
    phases_covered: ["Phase 8 Makeup Commerce", "Phase 5 Shopping Assistant"],
    messages: [
      {
        role: "user",
        text: "Mình sắp đi đám cưới của bạn cuối tuần này"
      },
      {
        role: "bot",
        expected: "Infer wedding occasion. Ask about makeup style preference (soft glam vs bold). Offer wedding makeup set quick reply.",
        expected_intent: "general_chat",
        expected_quick_replies: ["Makeup sang nhưng tự nhiên", "Makeup đậm nổi bật", "Cần tư vấn makeup set"]
      },
      {
        role: "user",
        text: "Muốn makeup sang nhưng tự nhiên, không quá đậm"
      },
      {
        role: "bot",
        expected: "Detect soft_glam style + wedding occasion. Recommend wedding makeup kit (cushion/foundation, blush, lipstick, mascara, setting spray). Include waterproof products. Explain longevity for all-day wear.",
        expected_intent: "event_makeup_look",
        expected_reply_type: "makeup_recommendation",
        expected_products_min: 3
      },
      {
        role: "user",
        text: "Mình muốn thêm toàn bộ vào giỏ"
      },
      {
        role: "bot",
        expected: "Add all recommended products to cart. Confirm with item count and total price. Offer checkout quick reply. Show cart summary.",
        expected_intent: "add_to_cart",
        expected_reply_type: "cart_action"
      },
      {
        role: "user",
        text: "Tổng tiền là bao nhiêu?"
      },
      {
        role: "bot",
        expected: "Show cart summary with subtotal, any discounts, and total. Offer Thanh toán ngay quick reply.",
        expected_intent: "cart_summary",
        expected_reply_type: "cart_summary"
      }
    ]
  },

  // ─────────────────────────────────────────────────────────────────────────
  // FLOW 02: Student finds affordable daily makeup
  // Phases: 4 (Personalization) → 8 (Makeup Commerce) → 6 (Comparison)
  // ─────────────────────────────────────────────────────────────────────────
  {
    conversation_id: "STUDENT_FLOW_02",
    title: "Sinh viên tìm makeup giá sinh viên",
    description: "Demonstrates: Budget constraint → Skin type personalization → Comparison",
    phases_covered: ["Phase 4 Personalization", "Phase 8 Makeup Commerce", "Phase 6 Comparison"],
    messages: [
      {
        role: "user",
        text: "Mình là sinh viên, ngân sách ít thôi, khoảng 300k cho cả bộ"
      },
      {
        role: "bot",
        expected: "Acknowledge budget 300k for full set. Ask about skin type (one question only). Offer skin type quick replies.",
        expected_intent: "general_chat",
        expected_quick_replies: ["Da dầu", "Da khô", "Da hỗn hợp", "Da thường"]
      },
      {
        role: "user",
        text: "Da mình dầu và hay đổ mồ hôi"
      },
      {
        role: "bot",
        expected: "Extract oily skin + sweating concern. Save preference. Recommend cushion/foundation suited for oily skin within 300k budget. Prioritize matte/oil-control products.",
        expected_intent: "cushion_foundation_recommendation",
        expected_reply_type: "makeup_recommendation",
        expected_context: { skinType: "oily", budget: { max: 300000 } }
      },
      {
        role: "user",
        text: "Cushion A và Cushion B cái nào tốt hơn cho da dầu?"
      },
      {
        role: "bot",
        expected: "Compare two cushion options. Build comparison table: price, finish (matte vs satin), oil control rating, coverage, longevity. Give recommendation for oily skin.",
        expected_intent: "cushion_foundation_recommendation",
        expected_reply_type: "product_recommendation"
      },
      {
        role: "user",
        text: "Ok mình chọn cái rẻ hơn, thêm vào giỏ giúp mình"
      },
      {
        role: "bot",
        expected: "Add selected product to cart. Confirm addition. Suggest complementary products (setting powder/spray) to complete the look. Show upsell.",
        expected_intent: "add_to_cart",
        expected_reply_type: "cart_action"
      }
    ]
  },

  // ─────────────────────────────────────────────────────────────────────────
  // FLOW 03: Order inquiry + support ticket flow
  // Phases: 3 (Order Tracking + Support)
  // ─────────────────────────────────────────────────────────────────────────
  {
    conversation_id: "ORDER_SUPPORT_FLOW_03",
    title: "Kiểm tra đơn và tạo yêu cầu đổi trả",
    description: "Demonstrates: Order lookup → Problem reporting → Support ticket creation",
    phases_covered: ["Phase 3 Order Tracking", "Phase 3 Customer Support"],
    messages: [
      {
        role: "user",
        text: "Mình muốn kiểm tra đơn hàng"
      },
      {
        role: "bot",
        expected: "Look up most recent order for authenticated user. Return order status (order_code, status_label, fulfillment_status, timeline, items_preview). Offer order detail quick replies.",
        expected_intent: "order_tracking",
        expected_reply_type: "order_tracking"
      },
      {
        role: "user",
        text: "Đơn hàng giao bao giờ vậy?"
      },
      {
        role: "bot",
        expected: "Use order context from previous message. Explain expected delivery based on fulfillment_status. If in_transit: show estimated delivery. Include timeline steps.",
        expected_intent: "order_tracking",
        expected_reply_type: "order_tracking"
      },
      {
        role: "user",
        text: "Mình nhận được rồi nhưng màu son không đúng, mình đặt đỏ nhưng nhận hồng"
      },
      {
        role: "bot",
        expected: "Detect wrong item complaint. Classify as wrong_or_missing_item (high priority). Create support ticket. Return ticket_code, status (open), estimated response time. Set handoff_required = true.",
        expected_intent: "support_ticket",
        expected_reply_type: "support_ticket",
        expected_context: { category: "wrong_or_missing_item", priority: "high" }
      },
      {
        role: "user",
        text: "Mình cần làm gì tiếp theo?"
      },
      {
        role: "bot",
        expected: "Explain next steps for wrong item: keep product, wait for team contact within 24-48h, or bring to store. Provide ticket code again. Offer human agent escalation.",
        expected_intent: "general_chat"
      }
    ]
  },

  // ─────────────────────────────────────────────────────────────────────────
  // FLOW 04: Ingredient safety check before purchase
  // Phases: 7 (Ingredient Analysis) → 8 (Makeup Commerce)
  // ─────────────────────────────────────────────────────────────────────────
  {
    conversation_id: "INGREDIENT_PURCHASE_FLOW_04",
    title: "Kiểm tra thành phần trước khi mua",
    description: "Demonstrates: Ingredient research → Safety confirmation → Product purchase",
    phases_covered: ["Phase 7 Ingredient Analysis", "Phase 8 Makeup Commerce"],
    messages: [
      {
        role: "user",
        text: "Da mình nhạy cảm, nên dùng kem nền có thành phần gì an toàn không?"
      },
      {
        role: "bot",
        expected: "Explain safe ingredients for sensitive skin (ceramide, centella, hyaluronic acid, mineral pigments). List ingredients to avoid (fragrance, alcohol, heavy silicones). Offer to find sensitive-skin-safe foundations.",
        expected_intent: "ingredient_check",
        expected_reply_type: "text"
      },
      {
        role: "user",
        text: "Niacinamide có an toàn cho da nhạy cảm không?"
      },
      {
        role: "bot",
        expected: "Explain niacinamide is generally safe for sensitive skin at 2-5% concentration. Note rare flushing at high concentrations. Recommend patch testing. Suggest starting with low concentration products.",
        expected_intent: "ingredient_check",
        expected_reply_type: "text"
      },
      {
        role: "user",
        text: "Ok vậy tìm kem nền có niacinamide phù hợp da nhạy cảm giúp mình"
      },
      {
        role: "bot",
        expected: "Search for foundations with sensitive-skin claims + niacinamide. Return products with is_sensitive_friendly = true. Explain why each is suitable. Add patch test reminder.",
        expected_intent: "cushion_foundation_recommendation",
        expected_reply_type: "makeup_recommendation",
        expected_context: { skin_type: "sensitive" }
      }
    ]
  },

  // ─────────────────────────────────────────────────────────────────────────
  // FLOW 05: Personalized recommendation based on saved profile
  // Phases: 4 (Personalization) → 9 (Advanced Engine)
  // ─────────────────────────────────────────────────────────────────────────
  {
    conversation_id: "PERSONALIZATION_FLOW_05",
    title: "Gợi ý cá nhân hóa theo profile đã lưu",
    description: "Demonstrates: Profile-based recommendation using saved preferences",
    phases_covered: ["Phase 4 Personalization", "Phase 9 Advanced Recommendation Engine"],
    messages: [
      {
        role: "user",
        text: "Da mình khô, hay bị bong tróc vào mùa đông"
      },
      {
        role: "bot",
        expected: "Save dry skin preference + seasonal concern. Acknowledge and confirm preference saved. Ask about makeup style preference (optional).",
        expected_intent: "product_recommendation"
      },
      {
        role: "user",
        text: "Mình thích màu nude, MLBB, không thích quá đậm"
      },
      {
        role: "bot",
        expected: "Save MLBB/nude shade preference. Confirm style preference. Offer product search based on combined profile.",
        expected_intent: "general_chat"
      },
      {
        role: "user",
        text: "Gợi ý son cho mình"
      },
      {
        role: "bot",
        expected: "Use saved profile (dry skin, MLBB/nude preference). Search lip products with moisturizing formula, MLBB/nude shades. Rank by profile compatibility. Explain personalization in response ('Dựa trên loại da khô và sở thích tông nude của bạn...').",
        expected_intent: "lipstick_recommendation",
        expected_reply_type: "product_recommendation",
        expected_context: {
          note: "customer_context_used = true",
          shade_preference: ["MLBB", "nude"]
        }
      },
      {
        role: "user",
        text: "Tìm cushion thêm nhé, loại dưỡng ẩm tốt"
      },
      {
        role: "bot",
        expected: "Use saved dry skin profile. Search for hydrating/dewy cushions. Return dewy/glowy finish cushions with HA or moisturizing formula. Explain suitability for dry skin.",
        expected_intent: "cushion_foundation_recommendation",
        expected_reply_type: "product_recommendation",
        expected_context: { skin_type: "dry", finish: "dewy" }
      }
    ]
  },

  // ─────────────────────────────────────────────────────────────────────────
  // FLOW 06: Quick office makeup routine builder
  // Phases: 9 (Advanced) → 5 (Shopping Assistant) → 5A (Cart)
  // ─────────────────────────────────────────────────────────────────────────
  {
    conversation_id: "OFFICE_ROUTINE_FLOW_06",
    title: "Xây dựng routine makeup công sở nhanh",
    description: "Demonstrates: Occasion inference → Routine building → Cart add",
    phases_covered: ["Phase 9 Advanced Recommendation Engine", "Phase 5 Shopping Assistant"],
    messages: [
      {
        role: "user",
        text: "Mình cần tư vấn makeup đi làm văn phòng, phải chuyên nghiệp nhưng không mất nhiều thời gian"
      },
      {
        role: "bot",
        expected: "Detect office occasion + time constraint. Suggest minimal, polished look. Recommend: primer (optional), lightweight foundation/cushion, brow product, neutral lipstick. Focus on quick application products.",
        expected_intent: "event_makeup_look",
        expected_context: { occasion: "office", style: "polished", requirements: ["natural", "long_wear"] }
      },
      {
        role: "user",
        text: "Ok nhưng mình hay đổ mồ hôi, cần makeup không trôi cả ngày"
      },
      {
        role: "bot",
        expected: "Extract sweat/oil control requirement. Add long-wear products (setting spray, oil-control primer, transfer-proof products). Update recommendations. Include setting spray suggestion.",
        expected_intent: "event_makeup_look",
        expected_context: { requirements: ["long_wear", "oil_control", "sweat_proof"] }
      },
      {
        role: "user",
        text: "Tạo combo đầy đủ cho mình và cho biết tổng tiền"
      },
      {
        role: "bot",
        expected: "Build complete office makeup combo (3-5 items). Calculate total price. Show combo with slot labels (primer, cushion, lipstick, setting spray). Include 'Thêm vào giỏ' quick reply.",
        expected_intent: "combo_recommendation",
        expected_reply_type: "combo_recommendation"
      },
      {
        role: "user",
        text: "Thêm hết vào giỏ"
      },
      {
        role: "bot",
        expected: "Add all combo products to cart. Confirm items added. Show new cart total. Offer Thanh toán ngay and Tiếp tục mua sắm options.",
        expected_intent: "add_to_cart",
        expected_reply_type: "cart_action"
      }
    ]
  },

  // ─────────────────────────────────────────────────────────────────────────
  // FLOW 07: Brand discovery and product comparison
  // Phases: 1 (General) → 6 (Comparison) → 8 (Makeup Commerce)
  // ─────────────────────────────────────────────────────────────────────────
  {
    conversation_id: "BRAND_DISCOVERY_FLOW_07",
    title: "Khám phá thương hiệu và so sánh sản phẩm",
    description: "Demonstrates: Brand education → Product comparison → Purchase",
    phases_covered: ["Phase 1 General AI", "Phase 6 Comparison", "Phase 8 Makeup Commerce"],
    messages: [
      {
        role: "user",
        text: "Mình mới dùng makeup, không biết thương hiệu nào tốt"
      },
      {
        role: "bot",
        expected: "Introduce popular brands across price ranges (Romand, Peripera for budget; MAC, NARS for premium; local Vietnamese brands). Ask about budget and preferences.",
        expected_intent: "general_chat"
      },
      {
        role: "user",
        text: "So sánh son của Romand và Peripera xem cái nào tốt hơn"
      },
      {
        role: "bot",
        expected: "Compare Romand vs Peripera lip products: price range, finish types, shade variety, staying power, packaging, availability. Give balanced comparison. Recommend based on different use cases.",
        expected_intent: "general_chat"
      },
      {
        role: "user",
        text: "Mình da ngăm, tông ấm, thì son nào trong hai hãng này hợp mình hơn?"
      },
      {
        role: "bot",
        expected: "Apply warm/olive skin tone filter. Recommend warm-toned shades from both brands (coral, brick red, warm berry). Compare specific products with price and shade names. Give clear recommendation for warm skin tone.",
        expected_intent: "lipstick_recommendation",
        expected_context: { tone: "warm", shade_preference: ["coral", "warm_red", "berry"] }
      },
      {
        role: "user",
        text: "Cho mình xem các sản phẩm Romand có tại Kanila"
      },
      {
        role: "bot",
        expected: "Filter products by Romand brand. Return available Romand products at Kanila. Show name, price, shade options, rating. Offer filter by product type.",
        expected_intent: "lipstick_recommendation",
        expected_reply_type: "product_recommendation"
      }
    ]
  },

  // ─────────────────────────────────────────────────────────────────────────
  // FLOW 08: Makeup bundle builder for special event
  // Phases: 9 (Advanced Engine) → 5 (Cart) → 3 (Order preview)
  // ─────────────────────────────────────────────────────────────────────────
  {
    conversation_id: "EVENT_BUNDLE_FLOW_08",
    title: "Mua bộ makeup cho sự kiện đặc biệt",
    description: "Demonstrates: Event detection → Full set recommendation → Cart management",
    phases_covered: ["Phase 9 Advanced Recommendation Engine", "Phase 5 Shopping Assistant", "Phase 8 Makeup Commerce"],
    messages: [
      {
        role: "user",
        text: "Mình chuẩn bị có buổi chụp ảnh kỷ yếu, cần makeup ăn ảnh"
      },
      {
        role: "bot",
        expected: "Detect photo shoot/graduation occasion. Explain camera-ready makeup (HD foundation, no-flash-back products). Recommend full set for photography. Focus on longevity and photogenic finish.",
        expected_intent: "event_makeup_look",
        expected_context: { occasion: "party", style: "glam" }
      },
      {
        role: "user",
        text: "Ngân sách khoảng 800k, mình da hơi vàng tông warm"
      },
      {
        role: "bot",
        expected: "Apply 800k budget and warm/yellow undertone. Build photo-ready kit within budget: foundation (warm tone match), highlighter, lipstick (warm shade), mascara. Show individual prices summing to ≤ 800k.",
        expected_intent: "makeup_set_builder",
        expected_context: { budget: { max: 800000 }, tone: "warm", bundle: true }
      },
      {
        role: "user",
        text: "Thêm combo này vào giỏ đi"
      },
      {
        role: "bot",
        expected: "Add all kit products to cart. Confirm successful addition. Show cart total. Suggest setting spray as upsell for photoshoot longevity.",
        expected_intent: "add_to_cart",
        expected_reply_type: "cart_action"
      },
      {
        role: "user",
        text: "Xem giỏ hàng của mình"
      },
      {
        role: "bot",
        expected: "Show full cart summary: item list with names and prices, subtotal, total. Offer Thanh toán ngay and Tiếp tục mua sắm.",
        expected_intent: "cart_summary",
        expected_reply_type: "cart_summary"
      }
    ]
  },

  // ─────────────────────────────────────────────────────────────────────────
  // FLOW 09: Korean natural makeup discovery
  // Phases: 1 (General) → 8 (Makeup Commerce) → 4 (Personalization)
  // ─────────────────────────────────────────────────────────────────────────
  {
    conversation_id: "KOREAN_LOOK_FLOW_09",
    title: "Tìm hiểu và mua makeup kiểu Hàn",
    description: "Demonstrates: Style preference capture → Korean makeup recommendations → Profile save",
    phases_covered: ["Phase 1 General AI", "Phase 8 Makeup Commerce", "Phase 4 Personalization"],
    messages: [
      {
        role: "user",
        text: "Mình muốn makeup phong cách Hàn Quốc, kiểu ulzzang tự nhiên"
      },
      {
        role: "bot",
        expected: "Explain Korean ulzzang makeup style (dewy skin, gradient lip, straight brow, natural look). List key products needed. Offer to find Korean-style products at Kanila.",
        expected_intent: "general_chat"
      },
      {
        role: "user",
        text: "Tìm cushion Hàn Quốc cho mình"
      },
      {
        role: "bot",
        expected: "Filter for Korean-brand cushions. Return dewy/natural finish cushions. Highlight Korean brands available at Kanila. Explain why these give the ulzzang dewy look.",
        expected_intent: "cushion_foundation_recommendation",
        expected_context: { style: "korean", finish: "dewy" }
      },
      {
        role: "user",
        text: "Son tint gradient kiểu Hàn thì dùng loại nào?"
      },
      {
        role: "bot",
        expected: "Explain gradient lip technique. Recommend sheer, blendable tints for gradient effect. Return 3-5 suitable tints. Include how-to-apply tip for gradient look.",
        expected_intent: "lipstick_recommendation",
        expected_context: { category: "lip_tint", style: "korean", finish: "tint" }
      },
      {
        role: "user",
        text: "Lưu sở thích này lại cho mình nhé, Korean makeup tự nhiên"
      },
      {
        role: "bot",
        expected: "Confirm saving Korean natural makeup style preference to profile. Acknowledge future recommendations will be personalized. Offer to find more Korean products.",
        expected_intent: "general_chat"
      }
    ]
  },

  // ─────────────────────────────────────────────────────────────────────────
  // FLOW 10: Full shopping journey — discovery to checkout
  // Phases: All phases in sequence
  // ─────────────────────────────────────────────────────────────────────────
  {
    conversation_id: "FULL_JOURNEY_FLOW_10",
    title: "Hành trình mua sắm đầy đủ từ đầu đến thanh toán",
    description: "Demonstrates: Full end-to-end customer journey across all phases",
    phases_covered: ["Phase 1", "Phase 4", "Phase 8", "Phase 9", "Phase 5A"],
    messages: [
      {
        role: "user",
        text: "Xin chào! Mình muốn makeup nhưng không biết bắt đầu từ đâu"
      },
      {
        role: "bot",
        expected: "Warm greeting. Ask one question: What skin type? OR what occasion? Provide quick reply options. Introduce Kanila chatbot capabilities briefly.",
        expected_intent: "general_chat"
      },
      {
        role: "user",
        text: "Da mình dầu, hay đi học và hay đi chơi cuối tuần"
      },
      {
        role: "bot",
        expected: "Save oily skin + school/casual occasions. Acknowledge. Ask about budget (one question). Provide budget range quick replies.",
        expected_intent: "product_recommendation"
      },
      {
        role: "user",
        text: "Ngân sách tầm 400-600k"
      },
      {
        role: "bot",
        expected: "Build profile: oily skin + 400-600k budget. Recommend starter makeup kit. Suggest versatile products working for both school and casual: oil-control cushion, tint, mascara. Show within budget.",
        expected_intent: "cushion_foundation_recommendation",
        expected_reply_type: "product_recommendation",
        expected_context: { skin_type: "oily", budget: { max: 600000 } }
      },
      {
        role: "user",
        text: "Cushion nào trong này kiềm dầu tốt nhất?"
      },
      {
        role: "bot",
        expected: "Focus on oil-control cushion options. Return top 3 ranked by oil-control rating. Compare their oil control performance, price, and finish. Give clear top pick for oily skin.",
        expected_intent: "cushion_foundation_recommendation",
        expected_reply_type: "product_recommendation"
      },
      {
        role: "user",
        text: "Tìm thêm son tint hợp với da ngăm không?"
      },
      {
        role: "bot",
        expected: "Detect darker skin tone request. Filter tints suitable for olive/dark skin (deeper berries, corals, warm reds that don't wash out). Return 3+ options. Explain why each suits darker complexions.",
        expected_intent: "lipstick_recommendation",
        expected_reply_type: "product_recommendation",
        expected_context: { tone: "olive" }
      },
      {
        role: "user",
        text: "Ok thêm cushion đó và 2 cái son tint vào giỏ"
      },
      {
        role: "bot",
        expected: "Add specified products to cart. If user not authenticated: prompt login. If authenticated: add 3 products, confirm. Show cart total. Suggest mascara upsell to complete the look.",
        expected_intent: "add_to_cart",
        expected_reply_type: "cart_action"
      },
      {
        role: "user",
        text: "Xem giỏ hàng mình có gì rồi"
      },
      {
        role: "bot",
        expected: "Show cart summary: 3 items (cushion + 2 tints), individual prices, subtotal, total. Offer Thanh toán ngay, Tiếp tục mua, Tư vấn thêm quick replies.",
        expected_intent: "cart_summary",
        expected_reply_type: "cart_summary"
      }
    ]
  }

];

module.exports = { conversations };
