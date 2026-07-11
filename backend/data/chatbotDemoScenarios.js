/**
 * chatbotDemoScenarios.js
 * Kanila AI Assistant Demo Test Scenarios — all 9 phases.
 *
 * Purpose: Generate realistic demo conversations that can be shown to
 * stakeholders, teachers, investors, testers, or customers.
 *
 * Usage:
 *   const { scenarios } = require('./data/chatbotDemoScenarios');
 *   const { conversations } = require('./data/chatbotDemoScenarios');
 *
 * Run all demos:
 *   node scripts/run-chatbot-demo.js
 */

"use strict";

// ─────────────────────────────────────────────────────────────────────────────
// PHASE 1 — General AI Assistant
// ─────────────────────────────────────────────────────────────────────────────

const PHASE_1_SCENARIOS = [
  {
    id: "DEMO_001",
    phase: "Phase 1 General AI Assistant",
    title: "Giới thiệu chatbot Kanila",
    customer_message: "Kanila có thể giúp mình những gì?",
    expected_intent: "general_chat",
    expected_context: {},
    expected_behavior: [
      "Greet user warmly",
      "List all chatbot capabilities",
      "Mention makeup recommendation",
      "Mention order tracking",
      "Mention beauty consultation",
      "Offer quick reply options"
    ],
    expected_output_type: "text",
    demo_value: "Shows chatbot introduction and capability overview"
  },
  {
    id: "DEMO_002",
    phase: "Phase 1 General AI Assistant",
    title: "Hướng dẫn makeup cho người mới bắt đầu",
    customer_message: "Mình mới bắt đầu makeup thì nên bắt đầu từ đâu?",
    expected_intent: "general_chat",
    expected_context: { experience_level: "beginner" },
    expected_behavior: [
      "Provide beginner makeup guidance",
      "Recommend basic products (cushion, lipstick, mascara)",
      "Suggest starting with simple looks",
      "Offer to recommend specific products"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI can guide makeup beginners"
  },
  {
    id: "DEMO_003",
    phase: "Phase 1 General AI Assistant",
    title: "Câu hỏi về thương hiệu makeup phổ biến",
    customer_message: "Thương hiệu makeup Hàn Quốc nào đang hot nhất hiện tại?",
    expected_intent: "general_chat",
    expected_context: { style: "korean" },
    expected_behavior: [
      "Mention popular Korean brands",
      "Highlight trending brands (Romand, Peripera, Innisfree, etc.)",
      "Offer to find products from these brands"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI knowledge about beauty brands and trends"
  },
  {
    id: "DEMO_004",
    phase: "Phase 1 General AI Assistant",
    title: "Hỏi về xu hướng makeup 2025",
    customer_message: "Xu hướng makeup 2025 là gì?",
    expected_intent: "general_chat",
    expected_context: {},
    expected_behavior: [
      "Mention current makeup trends",
      "Discuss glass skin, soft glam, no-makeup makeup",
      "Connect trends to product recommendations"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI awareness of beauty trends"
  },
  {
    id: "DEMO_005",
    phase: "Phase 1 General AI Assistant",
    title: "Câu hỏi chung về làm đẹp",
    customer_message: "Son tint và son thỏi khác nhau thế nào?",
    expected_intent: "general_chat",
    expected_context: {},
    expected_behavior: [
      "Explain difference between lip tint and lipstick",
      "Compare longevity, finish, application",
      "Suggest which type suits different occasions"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI beauty education capability"
  }
];

// ─────────────────────────────────────────────────────────────────────────────
// PHASE 2 — Product Recommendation
// ─────────────────────────────────────────────────────────────────────────────

const PHASE_2_SCENARIOS = [
  {
    id: "DEMO_006",
    phase: "Phase 2 Product Recommendation",
    title: "Tìm son tint theo ngân sách",
    customer_message: "Tìm son tint dưới 200k",
    expected_intent: "lipstick_recommendation",
    expected_context: {
      category: "lip_tint",
      budget: { max: 200000 }
    },
    expected_behavior: [
      "Detect lip tint category",
      "Apply budget filter ≤ 200,000 VND",
      "Search product database",
      "Return 3-5 lip tint products",
      "Show price and brand"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI can filter products by price"
  },
  {
    id: "DEMO_007",
    phase: "Phase 2 Product Recommendation",
    title: "Tìm cushion phù hợp sinh viên",
    customer_message: "Cushion nào phù hợp sinh viên?",
    expected_intent: "cushion_foundation_recommendation",
    expected_context: {
      category: "cushion",
      budget: { max: 300000 },
      occasion: "school"
    },
    expected_behavior: [
      "Detect cushion category",
      "Infer budget constraint (student = affordable)",
      "Infer school/daily occasion",
      "Return affordable cushion options"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI can infer context from customer description"
  },
  {
    id: "DEMO_008",
    phase: "Phase 2 Product Recommendation",
    title: "Tìm kem nền lâu trôi",
    customer_message: "Kem nền lâu trôi cho da mình",
    expected_intent: "cushion_foundation_recommendation",
    expected_context: {
      category: "foundation",
      requirements: ["long_wear"]
    },
    expected_behavior: [
      "Detect foundation category",
      "Extract long-wear requirement",
      "Search for long-lasting foundations",
      "Return products with long-wear attribute"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI understands product requirement keywords"
  },
  {
    id: "DEMO_009",
    phase: "Phase 2 Product Recommendation",
    title: "Tìm mascara chống lem",
    customer_message: "Mascara chống lem mắt cho mình với",
    expected_intent: "eye_makeup_recommendation",
    expected_context: {
      category: "mascara",
      requirements: ["waterproof"]
    },
    expected_behavior: [
      "Detect mascara category",
      "Extract waterproof requirement",
      "Filter waterproof mascaras",
      "Return top waterproof mascara options"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI extracts product feature requirements"
  },
  {
    id: "DEMO_010",
    phase: "Phase 2 Product Recommendation",
    title: "Tìm má hồng tông đào",
    customer_message: "Má hồng tone đào cho da mình",
    expected_intent: "blush_recommendation",
    expected_context: {
      category: "blush",
      shade_preference: ["peach"]
    },
    expected_behavior: [
      "Detect blush/má hồng category",
      "Extract peach shade preference",
      "Search blush with peach tones",
      "Return peach blush options"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI understands shade/color preferences"
  },
  {
    id: "DEMO_011",
    phase: "Phase 2 Product Recommendation",
    title: "Tìm phấn phủ kiềm dầu",
    customer_message: "Phấn phủ kiềm dầu tốt nhất hiện nay",
    expected_intent: "base_makeup_recommendation",
    expected_context: {
      category: "powder",
      finish: "matte",
      skin_type: "oily"
    },
    expected_behavior: [
      "Detect powder/phấn phủ category",
      "Extract oil-control requirement",
      "Return matte finish powders"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI understands finish types"
  },
  {
    id: "DEMO_012",
    phase: "Phase 2 Product Recommendation",
    title: "Tìm kem lót mịn màng",
    customer_message: "Gợi ý primer giúp mặt mịn và makeup bám lâu",
    expected_intent: "base_makeup_recommendation",
    expected_context: {
      category: "primer",
      requirements: ["long_wear", "smooth_finish"]
    },
    expected_behavior: [
      "Detect primer/kem lót category",
      "Extract long-wear and smooth finish requirements",
      "Return primers with those attributes"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI understands makeup prep product needs"
  }
];

// ─────────────────────────────────────────────────────────────────────────────
// PHASE 3 — Order Tracking & Customer Support
// ─────────────────────────────────────────────────────────────────────────────

const PHASE_3_SCENARIOS = [
  {
    id: "DEMO_013",
    phase: "Phase 3 Order Tracking",
    title: "Kiểm tra đơn hàng",
    customer_message: "Kiểm tra đơn hàng của mình",
    expected_intent: "order_tracking",
    expected_context: {},
    expected_behavior: [
      "Detect order tracking intent",
      "Check if user is authenticated",
      "If authenticated: look up latest order",
      "If guest: prompt login",
      "Return order status with timeline"
    ],
    expected_output_type: "order_tracking",
    demo_value: "Shows AI can retrieve and display order status"
  },
  {
    id: "DEMO_014",
    phase: "Phase 3 Order Tracking",
    title: "Hỏi ngày giao hàng",
    customer_message: "Đơn hàng của mình bao giờ giao đến?",
    expected_intent: "order_tracking",
    expected_context: {},
    expected_behavior: [
      "Detect shipping inquiry",
      "Look up most recent order",
      "Return estimated delivery info",
      "Show fulfillment status"
    ],
    expected_output_type: "order_tracking",
    demo_value: "Shows AI can answer shipping timeline questions"
  },
  {
    id: "DEMO_015",
    phase: "Phase 3 Order Tracking",
    title: "Tra cứu đơn hàng bằng mã",
    customer_message: "Kiểm tra đơn hàng #KNL20260708001",
    expected_intent: "order_tracking",
    expected_context: { order_code: "KNL20260708001" },
    expected_behavior: [
      "Extract order code from message",
      "Look up specific order by code",
      "Verify order belongs to current user",
      "Return order details"
    ],
    expected_output_type: "order_tracking",
    demo_value: "Shows AI can look up orders by code"
  },
  {
    id: "DEMO_016",
    phase: "Phase 3 Customer Support",
    title: "Báo cáo nhận sai sản phẩm",
    customer_message: "Mình nhận sai sản phẩm, mình đặt son đỏ nhưng nhận được son hồng",
    expected_intent: "support_ticket",
    expected_context: { category: "wrong_or_missing_item" },
    expected_behavior: [
      "Detect wrong item issue",
      "Classify as wrong_or_missing_item category",
      "Create high-priority support ticket",
      "Return ticket code and confirmation"
    ],
    expected_output_type: "support_ticket",
    demo_value: "Shows AI creates appropriate support ticket for wrong item"
  },
  {
    id: "DEMO_017",
    phase: "Phase 3 Customer Support",
    title: "Yêu cầu đổi trả sản phẩm",
    customer_message: "Mình muốn đổi trả sản phẩm không vừa ý",
    expected_intent: "support_ticket",
    expected_context: { category: "return_exchange" },
    expected_behavior: [
      "Detect return/exchange intent",
      "Classify as return_exchange category",
      "Create support ticket",
      "Guide user through return process"
    ],
    expected_output_type: "support_ticket",
    demo_value: "Shows AI handles return/exchange requests"
  },
  {
    id: "DEMO_018",
    phase: "Phase 3 Customer Support",
    title: "Yêu cầu hoàn tiền",
    customer_message: "Mình muốn hoàn tiền vì sản phẩm lỗi",
    expected_intent: "support_ticket",
    expected_context: { category: "refund" },
    expected_behavior: [
      "Detect refund intent",
      "Classify as refund + product_issue",
      "Create high-priority ticket",
      "Confirm ticket and next steps"
    ],
    expected_output_type: "support_ticket",
    demo_value: "Shows AI handles refund requests properly"
  },
  {
    id: "DEMO_019",
    phase: "Phase 3 Customer Support",
    title: "Gặp nhân viên hỗ trợ",
    customer_message: "Mình cần gặp nhân viên hỗ trợ trực tiếp",
    expected_intent: "support_ticket",
    expected_context: { category: "general_support" },
    expected_behavior: [
      "Detect human handoff request",
      "Set handoff_required = true",
      "Create consultation ticket",
      "Inform user about expected response time"
    ],
    expected_output_type: "support_ticket",
    demo_value: "Shows AI seamless handoff to human support"
  }
];

// ─────────────────────────────────────────────────────────────────────────────
// PHASE 4 — Personalized Recommendation
// ─────────────────────────────────────────────────────────────────────────────

const PHASE_4_SCENARIOS = [
  {
    id: "DEMO_020",
    phase: "Phase 4 Personalization",
    title: "Lưu thông tin loại da",
    customer_message: "Da mình là da dầu nhé",
    expected_intent: "product_recommendation",
    expected_context: { skin_type: "oily" },
    expected_behavior: [
      "Extract skin type from message",
      "Save oily skin preference to customer profile",
      "Acknowledge preference save",
      "Offer to find products for oily skin"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI saves user preferences for personalization"
  },
  {
    id: "DEMO_021",
    phase: "Phase 4 Personalization",
    title: "Dùng thông tin loại da để gợi ý",
    customer_message: "Tìm cushion giúp mình",
    expected_intent: "cushion_foundation_recommendation",
    expected_context: {
      category: "cushion",
      skin_type: "oily",
      note: "Uses saved oily skin preference"
    },
    expected_behavior: [
      "Load customer profile with saved oily skin preference",
      "Apply oily skin filter to search",
      "Rank products by oily skin compatibility",
      "Explain why products suit oily skin"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI uses saved preferences for personalized results"
  },
  {
    id: "DEMO_022",
    phase: "Phase 4 Personalization",
    title: "Lưu sở thích thương hiệu Hàn",
    customer_message: "Mình thích makeup Hàn Quốc, phong cách tự nhiên nhẹ nhàng",
    expected_intent: "general_chat",
    expected_context: { style: "korean", makeup_style: "natural" },
    expected_behavior: [
      "Extract Korean brand preference",
      "Extract natural makeup style",
      "Save preferences to profile",
      "Confirm preference saved"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI can capture style preferences"
  },
  {
    id: "DEMO_023",
    phase: "Phase 4 Personalization",
    title: "Gợi ý theo sở thích Hàn Quốc đã lưu",
    customer_message: "Tìm son cho mình",
    expected_intent: "lipstick_recommendation",
    expected_context: {
      category: "lipstick",
      style: "korean",
      note: "Uses saved Korean style preference"
    },
    expected_behavior: [
      "Load customer profile",
      "Apply Korean brand/style preference",
      "Return Korean-style lip products",
      "Personalized explanation with Korean style mention"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI uses style preferences for personalized lip recommendations"
  },
  {
    id: "DEMO_024",
    phase: "Phase 4 Personalization",
    title: "Hỏi thêm thông tin để cá nhân hóa",
    customer_message: "Gợi ý sản phẩm makeup cho mình",
    expected_intent: "product_recommendation",
    expected_context: { note: "No profile info — triggers progressive question" },
    expected_behavior: [
      "Detect incomplete profile",
      "Ask ONE targeted question (skin type)",
      "Provide quick reply options for skin types",
      "Do NOT query products yet"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI progressive profiling — asks exactly one question"
  }
];

// ─────────────────────────────────────────────────────────────────────────────
// PHASE 5 — Shopping Assistant & Cart Support
// ─────────────────────────────────────────────────────────────────────────────

const PHASE_5_SCENARIOS = [
  {
    id: "DEMO_025",
    phase: "Phase 5 Shopping Assistant",
    title: "Tư vấn bộ makeup đi tiệc",
    customer_message: "Mình muốn makeup đi tiệc, cần mua những gì?",
    expected_intent: "event_makeup_look",
    expected_context: {
      occasion: "party",
      requirements: ["long_wear", "bold_color"],
      style: "glam"
    },
    expected_behavior: [
      "Detect party occasion",
      "Build party makeup kit (foundation, blush, lipstick, setting spray)",
      "Recommend bold, long-wear products",
      "Suggest full makeup set"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI can build full occasion-specific makeup sets"
  },
  {
    id: "DEMO_026",
    phase: "Phase 5 Shopping Assistant",
    title: "Tạo combo skincare dưới 1 triệu",
    customer_message: "Tạo combo makeup dưới 1 triệu cho mình",
    expected_intent: "makeup_set_builder",
    expected_context: {
      bundle: true,
      budget: { max: 1000000 }
    },
    expected_behavior: [
      "Detect combo/bundle request",
      "Apply 1,000,000 VND total budget",
      "Build slot-based combo",
      "Show total combo price within budget"
    ],
    expected_output_type: "combo_recommendation",
    demo_value: "Shows AI can build budget-constrained makeup combos"
  },
  {
    id: "DEMO_027",
    phase: "Phase 5 Shopping Assistant",
    title: "Thêm sản phẩm vào giỏ hàng",
    customer_message: "Thêm bộ này vào giỏ hàng cho mình",
    expected_intent: "add_to_cart",
    expected_context: {},
    expected_behavior: [
      "Detect add_to_cart intent",
      "Verify user is authenticated",
      "Add previously recommended products to cart",
      "Confirm cart update with item count and total"
    ],
    expected_output_type: "cart_action",
    demo_value: "Shows AI can execute cart operations"
  },
  {
    id: "DEMO_028",
    phase: "Phase 5 Shopping Assistant",
    title: "Xem giỏ hàng hiện tại",
    customer_message: "Xem giỏ hàng của mình",
    expected_intent: "cart_summary",
    expected_context: {},
    expected_behavior: [
      "Detect cart summary intent",
      "Load user's active cart",
      "Return items, quantities, and total",
      "Offer checkout quick reply"
    ],
    expected_output_type: "cart_summary",
    demo_value: "Shows AI can display cart contents and totals"
  },
  {
    id: "DEMO_029",
    phase: "Phase 5 Shopping Assistant",
    title: "Gợi ý routine dưỡng da",
    customer_message: "Gợi ý routine dưỡng da cơ bản dưới 500k",
    expected_intent: "combo_recommendation",
    expected_context: {
      combo_type: "skincare_basic",
      budget: { max: 500000 }
    },
    expected_behavior: [
      "Detect routine/combo request",
      "Build skincare_basic combo (cleanser, serum, moisturizer)",
      "Apply 500k budget constraint",
      "Show step-by-step routine"
    ],
    expected_output_type: "combo_recommendation",
    demo_value: "Shows AI builds complete skincare routines within budget"
  },
  {
    id: "DEMO_030",
    phase: "Phase 5 Shopping Assistant",
    title: "Tư vấn làm đẹp chung",
    customer_message: "Da dầu mụn nên dùng gì để không bị bóng?",
    expected_intent: "beauty_consultation",
    expected_context: {
      skin_type: "oily",
      skin_concerns: ["acne", "oil_control"]
    },
    expected_behavior: [
      "Detect beauty consultation intent",
      "Extract oily + acne skin concerns",
      "Find suitable products for oily acne skin",
      "Explain why products suit this skin type"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI gives personalized beauty advisory"
  }
];

// ─────────────────────────────────────────────────────────────────────────────
// PHASE 6 — Product Comparison
// ─────────────────────────────────────────────────────────────────────────────

const PHASE_6_SCENARIOS = [
  {
    id: "DEMO_031",
    phase: "Phase 6 Product Comparison",
    title: "So sánh hai thương hiệu son",
    customer_message: "So sánh son Rom&nd và Peripera cho mình",
    expected_intent: "general_chat",
    expected_context: {
      comparison: {
        brands: ["Romand", "Peripera"],
        category: "lipstick"
      }
    },
    expected_behavior: [
      "Detect brand comparison request",
      "Retrieve products from both brands",
      "Compare: price, finish, shade range, longevity",
      "Give objective recommendation"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI can compare makeup brands objectively"
  },
  {
    id: "DEMO_032",
    phase: "Phase 6 Product Comparison",
    title: "So sánh hai sản phẩm cushion",
    customer_message: "Nên chọn cushion A hay cushion B?",
    expected_intent: "cushion_foundation_recommendation",
    expected_context: {
      comparison: {
        criteria: ["price", "finish", "coverage", "longevity"]
      }
    },
    expected_behavior: [
      "Detect product comparison request",
      "Build comparison table with price, finish, coverage, longevity",
      "Provide recommendation based on user's needs",
      "Return comparison_response type"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI can compare specific products with structured table"
  },
  {
    id: "DEMO_033",
    phase: "Phase 6 Product Comparison",
    title: "So sánh finish matte và dewy",
    customer_message: "Mình nên chọn kem nền matte hay dewy?",
    expected_intent: "cushion_foundation_recommendation",
    expected_context: {
      comparison: {
        finish_types: ["matte", "dewy"]
      }
    },
    expected_behavior: [
      "Detect finish comparison request",
      "Explain difference between matte and dewy",
      "Recommend based on skin type context",
      "Show products for each finish type"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI can explain cosmetic finish differences"
  },
  {
    id: "DEMO_034",
    phase: "Phase 6 Product Comparison",
    title: "So sánh giá theo thương hiệu",
    customer_message: "Son Hàn Quốc và son Việt Nam giá khác nhau thế nào?",
    expected_intent: "general_chat",
    expected_context: {
      comparison: { regions: ["korean", "vietnamese"] }
    },
    expected_behavior: [
      "Detect price comparison by origin",
      "Compare price points of Korean vs Vietnamese brands",
      "Show value proposition for each"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI understands brand origin and pricing"
  }
];

// ─────────────────────────────────────────────────────────────────────────────
// PHASE 7 — Ingredient Analysis
// ─────────────────────────────────────────────────────────────────────────────

const PHASE_7_SCENARIOS = [
  {
    id: "DEMO_035",
    phase: "Phase 7 Ingredient Analysis",
    title: "Hỏi về tác dụng Niacinamide",
    customer_message: "Niacinamide có tác dụng gì với da?",
    expected_intent: "ingredient_check",
    expected_context: { ingredient: "niacinamide" },
    expected_behavior: [
      "Detect ingredient analysis intent",
      "Look up niacinamide benefits",
      "Explain brightening, pore-minimizing, oil-control effects",
      "Mention suitable skin types"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI can explain ingredient benefits in accessible language"
  },
  {
    id: "DEMO_036",
    phase: "Phase 7 Ingredient Analysis",
    title: "Kiểm tra tương thích thành phần",
    customer_message: "Retinol có dùng chung với BHA được không?",
    expected_intent: "ingredient_check",
    expected_context: {
      compatibility_check: {
        ingredient1: "retinol",
        ingredient2: "BHA"
      }
    },
    expected_behavior: [
      "Detect compatibility check intent",
      "Analyze Retinol + BHA interaction",
      "Return caution level",
      "Advise on safe usage schedule"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI can analyze ingredient compatibility for safety"
  },
  {
    id: "DEMO_037",
    phase: "Phase 7 Ingredient Analysis",
    title: "Tra thành phần sản phẩm",
    customer_message: "Sản phẩm này có thành phần gì không gây kích ứng?",
    expected_intent: "ingredient_check",
    expected_context: { concern: "sensitivity" },
    expected_behavior: [
      "Detect ingredient lookup intent",
      "Search for sensitive-skin-safe products",
      "Return products with gentle ingredient list"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI can help users with sensitive skin avoid irritants"
  },
  {
    id: "DEMO_038",
    phase: "Phase 7 Ingredient Analysis",
    title: "Hỏi về Hyaluronic Acid",
    customer_message: "Hyaluronic acid trong kem nền có tác dụng gì?",
    expected_intent: "ingredient_check",
    expected_context: { ingredient: "hyaluronic_acid", product_type: "foundation" },
    expected_behavior: [
      "Explain hyaluronic acid hydration benefits",
      "Explain role in foundation (keeps skin plump)",
      "Recommend HA-infused foundations"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI connects ingredient knowledge to product recommendations"
  },
  {
    id: "DEMO_039",
    phase: "Phase 7 Ingredient Analysis",
    title: "Hỏi về SPF trong kem nền",
    customer_message: "Kem nền có SPF có đủ bảo vệ da khỏi nắng không?",
    expected_intent: "ingredient_check",
    expected_context: { ingredient: "SPF", product_type: "foundation" },
    expected_behavior: [
      "Explain SPF in foundation",
      "Clarify limitations of foundation SPF",
      "Recommend dedicated sunscreen for proper protection"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI gives accurate, safety-focused ingredient advice"
  }
];

// ─────────────────────────────────────────────────────────────────────────────
// PHASE 8 — Makeup Commerce Assistant
// ─────────────────────────────────────────────────────────────────────────────

const PHASE_8_SCENARIOS = [
  {
    id: "DEMO_MAKEUP_001",
    phase: "Phase 8 Makeup Commerce",
    title: "Tìm kem nền phù hợp",
    customer_message: "Mình muốn tìm kem nền",
    expected_intent: "cushion_foundation_recommendation",
    expected_context: {
      category: "foundation"
    },
    expected_behavior: [
      "Detect makeup shopping intent (kem nền = foundation)",
      "Map to cushion_foundation_recommendation intent",
      "Search foundation product database",
      "Return 3-5 foundation products with price, brand, rating",
      "Gemini explains top picks"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI immediately recommends products without asking unnecessary questions"
  },
  {
    id: "DEMO_MAKEUP_002",
    phase: "Phase 8 Makeup Commerce",
    title: "Tư vấn cushion cho da dầu",
    customer_message: "Da dầu nên dùng cushion nào?",
    expected_intent: "cushion_foundation_recommendation",
    expected_context: {
      category: "cushion",
      skinType: "oily",
      requirements: ["oil_control", "matte", "long_wear"]
    },
    expected_behavior: [
      "Detect cushion category",
      "Extract oily skin type",
      "Build requirements: oil_control, matte, long_wear",
      "Search database with skin type filter",
      "Return cushions suitable for oily skin"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI recommends makeup based on skin type"
  },
  {
    id: "DEMO_MAKEUP_003",
    phase: "Phase 8 Makeup Commerce",
    title: "Bộ makeup đám cưới",
    customer_message: "Makeup đi đám cưới cần mua gì?",
    expected_intent: "event_makeup_look",
    expected_context: {
      occasion: "wedding",
      requirements: ["long_wear", "soft_glam", "waterproof"],
      style: "soft_glam",
      bundle: true
    },
    expected_behavior: [
      "Detect wedding occasion",
      "Set style: soft_glam, waterproof",
      "Build wedding makeup kit (foundation, blush, lipstick, mascara, setting spray)",
      "Return full makeup set recommendation",
      "Explain suitability for wedding context"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI can create complete occasion-specific makeup kits"
  },
  {
    id: "DEMO_MAKEUP_004",
    phase: "Phase 8 Makeup Commerce",
    title: "Son phù hợp da ngăm",
    customer_message: "Son nào hợp da ngăm?",
    expected_intent: "lipstick_recommendation",
    expected_context: {
      category: "lipstick",
      tone: "olive",
      shade_preference: ["berry", "red", "coral"]
    },
    expected_behavior: [
      "Detect lipstick category",
      "Extract olive/dark skin tone",
      "Filter by tone_match_supported: olive",
      "Return bold colors (berry, red, coral) that flatter dark skin",
      "Explain why these shades suit the skin tone"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI can match makeup shades to skin tone"
  },
  {
    id: "DEMO_MAKEUP_005",
    phase: "Phase 8 Makeup Commerce",
    title: "Tìm makeup Hàn phong cách nhẹ nhàng",
    customer_message: "Mình muốn makeup Hàn Quốc nhẹ nhàng tự nhiên",
    expected_intent: "daily_makeup_look",
    expected_context: {
      style: "korean",
      makeup_style: "natural",
      requirements: ["lightweight", "natural"]
    },
    expected_behavior: [
      "Detect Korean/natural style preference",
      "Map to natural/ulzzang makeup look",
      "Find lightweight, natural-finish products",
      "Return products suitable for Korean natural look"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI understands makeup style preferences"
  },
  {
    id: "DEMO_MAKEUP_006",
    phase: "Phase 8 Makeup Commerce",
    title: "Tư vấn cushion cho da dầu, dưới 500k",
    customer_message: "Da mình dầu, muốn tìm cushion dưới 500k để đi học",
    expected_intent: "cushion_foundation_recommendation",
    expected_context: {
      category: "cushion",
      skinType: "oily",
      occasion: "school",
      budget: { max: 500000 }
    },
    expected_behavior: [
      "Detect makeup shopping intent",
      "Extract oily skin",
      "Extract budget ≤ 500,000 VND",
      "Extract school/daily occasion",
      "Search product database",
      "Return product cards",
      "Explain recommendation for school use"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI can recommend makeup based on skin type, budget, and occasion"
  },
  {
    id: "DEMO_MAKEUP_007",
    phase: "Phase 8 Makeup Commerce",
    title: "Tìm son tint cho look đi học",
    customer_message: "Son tint nào phù hợp makeup đi học nhẹ nhàng?",
    expected_intent: "lipstick_recommendation",
    expected_context: {
      category: "lip_tint",
      occasion: "school",
      style: "natural",
      requirements: ["lightweight", "natural"]
    },
    expected_behavior: [
      "Detect lip tint + school occasion",
      "Filter lightweight tint products",
      "Return natural-finish tints",
      "Suggest sheer tints suitable for daytime school look"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI matches product type to occasion and style"
  },
  {
    id: "DEMO_MAKEUP_008",
    phase: "Phase 8 Makeup Commerce",
    title: "Tìm sản phẩm đang sale",
    customer_message: "Có cushion nào đang giảm giá không?",
    expected_intent: "find_sale_product",
    expected_context: {
      category: "cushion",
      wants_sale: true
    },
    expected_behavior: [
      "Detect sale/discount intent",
      "Filter products with compareAtPrice",
      "Return products currently on promotion",
      "Show discount amount"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI can filter and show currently discounted products"
  },
  {
    id: "DEMO_MAKEUP_009",
    phase: "Phase 8 Makeup Commerce",
    title: "Hỏi về voucher và ưu đãi",
    customer_message: "Kanila có voucher makeup không?",
    expected_intent: "voucher_promotion_question",
    expected_context: {},
    expected_behavior: [
      "Detect voucher inquiry",
      "Guide user to Voucher/Ưu đãi section in app",
      "Do NOT fabricate specific coupon codes",
      "Show quick replies for further shopping"
    ],
    expected_output_type: "text",
    demo_value: "Shows AI handles voucher queries honestly without fabrication"
  },
  {
    id: "DEMO_MAKEUP_010",
    phase: "Phase 8 Makeup Commerce",
    title: "Hỏi kẻ mắt cho người mới bắt đầu",
    customer_message: "Kẻ mắt nào dễ dùng cho người mới bắt đầu?",
    expected_intent: "eye_makeup_recommendation",
    expected_context: {
      category: "eyeliner",
      experience_level: "beginner"
    },
    expected_behavior: [
      "Detect eyeliner category",
      "Infer beginner-friendly requirement",
      "Return easy-to-use eyeliner options (felt tip, automatic)",
      "Explain why each is beginner-friendly"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI tailors product recommendations to experience level"
  }
];

// ─────────────────────────────────────────────────────────────────────────────
// PHASE 9 — Advanced AI Recommendation Engine
// ─────────────────────────────────────────────────────────────────────────────

const PHASE_9_SCENARIOS = [
  {
    id: "DEMO_ADVANCED_001",
    phase: "Phase 9 Advanced Recommendation Engine",
    title: "Makeup nhanh trước khi đi học",
    customer_message: "Mình cần makeup nhanh 5 phút trước khi đi học",
    expected_intent: "daily_makeup_look",
    expected_context: {
      occasion: "school",
      style: "natural",
      requirements: ["quick", "lightweight"],
      time_constraint: "5 minutes"
    },
    expected_behavior: [
      "Detect quick makeup intent",
      "Extract school occasion",
      "Extract quick (5-minute) constraint",
      "Recommend 2-3 multi-use products (tinted moisturizer, brow pencil, tint)",
      "Order products by application speed"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI understands time constraints in makeup routine"
  },
  {
    id: "DEMO_ADVANCED_002",
    phase: "Phase 9 Advanced Recommendation Engine",
    title: "Makeup tiệc tối giữ 8 tiếng",
    customer_message: "Mình đi tiệc tối, cần makeup giữ được 8 tiếng",
    expected_intent: "event_makeup_look",
    expected_context: {
      occasion: "party",
      requirements: ["long_wear", "bold_color"],
      duration: "8_hours"
    },
    expected_behavior: [
      "Detect party/evening occasion",
      "Extract long-wear requirement (8 hours)",
      "Filter for highly durable products",
      "Recommend setting spray, transfer-proof products",
      "Include primer recommendation for longevity"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI understands duration/longevity requirements"
  },
  {
    id: "DEMO_ADVANCED_003",
    phase: "Phase 9 Advanced Recommendation Engine",
    title: "Full bộ makeup ngân sách 700k",
    customer_message: "Ngân sách 700k muốn mua full bộ makeup",
    expected_intent: "makeup_set_builder",
    expected_context: {
      bundle: true,
      budget: { max: 700000 }
    },
    expected_behavior: [
      "Detect full bundle request",
      "Apply 700,000 VND total budget",
      "Distribute budget across product slots",
      "Build optimized kit: foundation + lipstick + blush + mascara",
      "Ensure total stays within 700k"
    ],
    expected_output_type: "combo_recommendation",
    demo_value: "Shows AI can optimize a full makeup kit within a budget"
  },
  {
    id: "DEMO_ADVANCED_004",
    phase: "Phase 9 Advanced Recommendation Engine",
    title: "Makeup đi biển không trôi",
    customer_message: "Mình đi biển cần makeup không bị trôi khi tiếp xúc nước",
    expected_intent: "event_makeup_look",
    expected_context: {
      occasion: "outdoor",
      requirements: ["waterproof", "long_wear"],
      wants_waterproof: true
    },
    expected_behavior: [
      "Detect outdoor/water activity context",
      "Extract waterproof requirement",
      "Filter waterproof foundation, mascara, eyeliner",
      "Recommend waterproof full set"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI understands specific use-case requirements"
  },
  {
    id: "DEMO_ADVANCED_005",
    phase: "Phase 9 Advanced Recommendation Engine",
    title: "Gợi ý bán chạy nhất theo danh mục",
    customer_message: "Cushion bán chạy nhất Kanila là gì?",
    expected_intent: "cushion_foundation_recommendation",
    expected_context: {
      category: "cushion",
      wants_best_seller: true
    },
    expected_behavior: [
      "Detect best-seller inquiry",
      "Filter and sort by sales rank / averageRating",
      "Return top-rated cushions",
      "Highlight review counts and ratings"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI can surface popularity-ranked products"
  },
  {
    id: "DEMO_ADVANCED_006",
    phase: "Phase 9 Advanced Recommendation Engine",
    title: "Tìm sản phẩm nhạy cảm friendly",
    customer_message: "Da mình rất nhạy cảm dễ kích ứng, cần makeup an toàn",
    expected_intent: "cushion_foundation_recommendation",
    expected_context: {
      skin_type: "sensitive",
      skin_concerns: ["sensitive"],
      requirements: ["gentle", "fragrance_free", "non_comedogenic"]
    },
    expected_behavior: [
      "Detect sensitive skin concern",
      "Apply is_sensitive_friendly filter",
      "Return hypoallergenic, fragrance-free options",
      "Add disclaimer about patch testing"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI safely handles sensitive skin recommendations"
  },
  {
    id: "DEMO_ADVANCED_007",
    phase: "Phase 9 Advanced Recommendation Engine",
    title: "Tư vấn makeup tone lạnh",
    customer_message: "Da mình tông lạnh nên son màu gì đẹp?",
    expected_intent: "shade_tone_advice",
    expected_context: {
      tone: "cool",
      category: "lipstick"
    },
    expected_behavior: [
      "Detect cool skin tone",
      "Map cool tone to recommended shades (berry, plum, rose)",
      "Return lip products in complementary colors",
      "Explain why these shades suit cool undertones"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI understands skin undertones for makeup matching"
  },
  {
    id: "DEMO_ADVANCED_008",
    phase: "Phase 9 Advanced Recommendation Engine",
    title: "Concealer che quầng thâm",
    customer_message: "Mình bị quầng thâm mắt, dùng concealer nào tốt?",
    expected_intent: "concealer_recommendation",
    expected_context: {
      category: "concealer",
      skin_concerns: ["dark_spot"],
      use_case: "under_eye"
    },
    expected_behavior: [
      "Detect concealer category",
      "Extract dark circle concern",
      "Filter concealers for under-eye use",
      "Recommend peach/salmon undertone correctors"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI matches product solutions to specific skin concerns"
  },
  {
    id: "DEMO_ADVANCED_009",
    phase: "Phase 9 Advanced Recommendation Engine",
    title: "Gợi ý phối màu má hồng với son",
    customer_message: "Má hồng nào mix được với son đỏ?",
    expected_intent: "blush_recommendation",
    expected_context: {
      category: "blush",
      color_coordination: {
        with_lip: "red"
      }
    },
    expected_behavior: [
      "Detect blush category",
      "Identify coordination with red lip",
      "Recommend complementary blush shades (soft pink, peach, coral)",
      "Explain color theory for makeup coordination"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI understands makeup color coordination"
  },
  {
    id: "DEMO_ADVANCED_010",
    phase: "Phase 9 Advanced Recommendation Engine",
    title: "Makeup da khô dưỡng ẩm",
    customer_message: "Da khô, muốn makeup mà vẫn trông ẩm mướt cả ngày",
    expected_intent: "cushion_foundation_recommendation",
    expected_context: {
      skin_type: "dry",
      requirements: ["moisturizing", "dewy", "hydrating"],
      finish: "dewy"
    },
    expected_behavior: [
      "Detect dry skin type",
      "Build requirements: moisturizing, dewy, hydrating",
      "Filter products with dewy/glowy finish",
      "Prioritize skin-nourishing formulas"
    ],
    expected_output_type: "product_recommendation",
    demo_value: "Shows AI recommends makeup suited to dry skin needs"
  }
];

// ─────────────────────────────────────────────────────────────────────────────
// Combined export
// ─────────────────────────────────────────────────────────────────────────────

const scenarios = [
  ...PHASE_1_SCENARIOS,
  ...PHASE_2_SCENARIOS,
  ...PHASE_3_SCENARIOS,
  ...PHASE_4_SCENARIOS,
  ...PHASE_5_SCENARIOS,
  ...PHASE_6_SCENARIOS,
  ...PHASE_7_SCENARIOS,
  ...PHASE_8_SCENARIOS,
  ...PHASE_9_SCENARIOS
];

module.exports = {
  scenarios,
  PHASE_1_SCENARIOS,
  PHASE_2_SCENARIOS,
  PHASE_3_SCENARIOS,
  PHASE_4_SCENARIOS,
  PHASE_5_SCENARIOS,
  PHASE_6_SCENARIOS,
  PHASE_7_SCENARIOS,
  PHASE_8_SCENARIOS,
  PHASE_9_SCENARIOS
};
