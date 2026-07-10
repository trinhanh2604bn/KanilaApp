/**
 * ingredientCompatibility.service.js
 * In-memory dictionary of core skincare ingredients and their combination rules.
 */

const INGREDIENTS_DB = {
  "niacinamide": {
    name: "Niacinamide (Vitamin B3)",
    benefits: ["Thu nhỏ lỗ chân lông", "Kiểm soát dầu", "Làm sáng da", "Giảm thâm mụn", "Củng cố hàng rào bảo vệ da"],
    suitable_skin_types: ["oily", "combination", "normal", "dry", "sensitive"],
    concerns: ["acne", "dark_spot", "oil_control", "pore"],
    warnings: ["Tránh dùng chung với Vitamin C L-AA tinh khiết ở nồng độ cao nếu da quá nhạy cảm."]
  },
  "retinol": {
    name: "Retinol (Vitamin A)",
    benefits: ["Chống lão hóa", "Mờ nếp nhăn", "Hỗ trợ trị mụn", "Tăng sinh collagen", "Làm mới bề mặt da"],
    suitable_skin_types: ["oily", "combination", "normal", "dry"],
    concerns: ["anti_aging", "acne", "dark_spot", "dullness"],
    warnings: ["Dễ gây kích ứng, bong tróc thời gian đầu.", "Phải dùng kem chống nắng.", "Không dùng cho phụ nữ có thai/cho con bú."]
  },
  "aha": {
    name: "AHA (Alpha Hydroxy Acid)",
    benefits: ["Tẩy tế bào chết bề mặt", "Làm sáng da", "Giảm thâm sạm", "Kích thích tái tạo tế bào"],
    suitable_skin_types: ["dry", "normal", "combination"],
    concerns: ["dullness", "dark_spot", "anti_aging"],
    warnings: ["Làm da nhạy cảm hơn với ánh nắng, bắt buộc dùng kem chống nắng.", "Có thể gây châm chích nhẹ."]
  },
  "bha": {
    name: "BHA (Salicylic Acid)",
    benefits: ["Làm sạch sâu lỗ chân lông", "Tẩy tế bào chết", "Kháng viêm", "Hỗ trợ giảm mụn ẩn, mụn đầu đen"],
    suitable_skin_types: ["oily", "combination"],
    concerns: ["acne", "oil_control", "pore"],
    warnings: ["Dễ làm khô da.", "Nhạy cảm với ánh nắng.", "Không nên dùng quá nhiều lần trong tuần nếu mới bắt đầu."]
  },
  "vitamin c": {
    name: "Vitamin C",
    benefits: ["Chống oxy hóa", "Làm sáng da", "Mờ thâm nám", "Tăng sinh collagen", "Bảo vệ da khỏi gốc tự do"],
    suitable_skin_types: ["normal", "dry", "combination", "oily"],
    concerns: ["dullness", "dark_spot", "anti_aging"],
    warnings: ["Dễ bị oxy hóa, cần bảo quản kỹ.", "Vitamin C dạng L-AA dễ gây kích ứng cho da mụn viêm hoặc quá nhạy cảm."]
  },
  "b5": {
    name: "Panthenol (Vitamin B5)",
    benefits: ["Phục hồi da", "Làm dịu kích ứng", "Cấp ẩm", "Củng cố màng bảo vệ da"],
    suitable_skin_types: ["sensitive", "dry", "normal", "combination", "oily"],
    concerns: ["sensitive", "dryness"],
    warnings: []
  },
  "hyaluronic acid": {
    name: "Hyaluronic Acid (HA)",
    benefits: ["Cấp nước vượt trội", "Giữ ẩm cho da", "Giúp da căng mọng"],
    suitable_skin_types: ["dry", "normal", "combination", "oily", "sensitive"],
    concerns: ["dryness", "anti_aging"],
    warnings: ["Nên bôi trên nền da ẩm để tránh hút ẩm ngược."]
  },
  "centella": {
    name: "Centella Asiatica (Rau má)",
    benefits: ["Làm dịu da", "Kháng viêm", "Phục hồi tổn thương", "Hỗ trợ trị mụn"],
    suitable_skin_types: ["sensitive", "oily", "combination", "normal", "dry"],
    concerns: ["sensitive", "acne"],
    warnings: []
  }
};

const COMBINATION_RULES = [
  {
    ingredients: ["aha", "retinol"],
    level: "avoid",
    reason: "Cả hai đều là thành phần hoạt động mạnh, làm tăng quá trình sừng hóa và dễ gây kích ứng, khô, đỏ da trầm trọng nếu dùng chung trong cùng một buổi. Có thể dùng luân phiên sáng/tối hoặc cách ngày."
  },
  {
    ingredients: ["bha", "retinol"],
    level: "avoid",
    reason: "BHA và Retinol dùng chung dễ gây quá tải cho da, làm khô và phá vỡ hàng rào bảo vệ da. Nên dùng cách ngày hoặc BHA sáng, Retinol tối."
  },
  {
    ingredients: ["aha", "bha"],
    level: "warning",
    reason: "Dùng chung dễ gây kích ứng mạnh. Tuy nhiên một số sản phẩm đã được hãng pha chế sẵn theo tỷ lệ an toàn. Nếu dùng tách rời, nên chú ý tần suất."
  },
  {
    ingredients: ["vitamin c", "retinol"],
    level: "warning",
    reason: "Dễ gây kích ứng và thay đổi độ pH khiến chúng giảm tác dụng. Lời khuyên tốt nhất là dùng Vitamin C vào buổi sáng (để tăng cường chống nắng) và Retinol vào buổi tối."
  },
  {
    ingredients: ["niacinamide", "vitamin c"],
    level: "safe",
    reason: "Kết hợp an toàn và mang lại hiệu quả mờ thâm, sáng da rất tốt. Chỉ lưu ý nếu dùng Niacinamide và Vitamin C (L-AA) nồng độ cao cùng lúc có thể gây châm chích nhẹ với da quá nhạy cảm."
  },
  {
    ingredients: ["niacinamide", "retinol"],
    level: "safe",
    reason: "Sự kết hợp hoàn hảo. Niacinamide giúp làm dịu, phục hồi hàng rào bảo vệ da, giảm các tác dụng phụ gây kích ứng, đỏ da của Retinol."
  },
  {
    ingredients: ["niacinamide", "bha"],
    level: "safe",
    reason: "BHA làm sạch sâu lỗ chân lông, tạo điều kiện cho Niacinamide thẩm thấu tốt hơn để kiểm soát dầu và thu nhỏ lỗ chân lông."
  },
  {
    ingredients: ["b5", "retinol"],
    level: "safe",
    reason: "B5 giúp phục hồi và cấp ẩm cực tốt, là 'cứu tinh' hoàn hảo để làm dịu các kích ứng do Retinol gây ra."
  },
  {
    ingredients: ["b5", "aha"],
    level: "safe",
    reason: "B5 làm dịu da và cấp ẩm, giúp giảm cảm giác châm chích sau khi tẩy tế bào chết hóa học bằng AHA."
  },
  {
    ingredients: ["b5", "bha"],
    level: "safe",
    reason: "B5 cung cấp độ ẩm cần thiết sau khi BHA làm sạch sâu, giúp da không bị khô căng."
  },
  {
    ingredients: ["hyaluronic acid", "retinol"],
    level: "safe",
    reason: "HA cấp nước làm giảm tình trạng khô tróc do Retinol. Rất khuyến khích kết hợp."
  }
];

function normalizeName(name) {
  const lower = name.toLowerCase();
  if (lower.includes("niacinamide") || lower.includes("b3")) return "niacinamide";
  if (lower.includes("retinol") || lower.includes("tretinoin")) return "retinol";
  if (lower.includes("aha") || lower.includes("glycolic") || lower.includes("lactic")) return "aha";
  if (lower.includes("bha") || lower.includes("salicylic")) return "bha";
  if (lower.includes("vitamin c") || lower.includes("ascorbic")) return "vitamin c";
  if (lower.includes("b5") || lower.includes("panthenol")) return "b5";
  if (lower.includes("hyaluronic") || lower.includes("ha")) return "hyaluronic acid";
  if (lower.includes("centella") || lower.includes("rau má")) return "centella";
  return name.toLowerCase().trim();
}

/**
 * Check compatibility between two ingredients
 */
function checkCompatibility(ing1, ing2) {
  const n1 = normalizeName(ing1);
  const n2 = normalizeName(ing2);
  
  if (n1 === n2) {
    return { level: "warning", reason: "Dùng chung nhiều sản phẩm có cùng thành phần mạnh có thể gây quá tải cho da." };
  }

  for (const rule of COMBINATION_RULES) {
    if ((rule.ingredients.includes(n1) && rule.ingredients.includes(n2))) {
      return { level: rule.level, reason: rule.reason };
    }
  }

  // Default if both found in DB but no rule
  if (INGREDIENTS_DB[n1] && INGREDIENTS_DB[n2]) {
    return { level: "safe", reason: "Hai thành phần này có thể dùng chung an toàn. Tuy nhiên, luôn lắng nghe phản ứng của da." };
  }

  return { level: "unknown", reason: "Không có đủ dữ liệu để đánh giá chính xác độ tương thích của hai thành phần này." };
}

/**
 * Get ingredient details
 */
function getIngredientData(name) {
  const n = normalizeName(name);
  return INGREDIENTS_DB[n] || null;
}

module.exports = {
  INGREDIENTS_DB,
  checkCompatibility,
  getIngredientData,
  normalizeName
};
