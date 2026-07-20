const Product = require("../models/product.model");
const ReviewSummary = require("../models/reviewSummary.model");
const { getCustomerContext } = require("./chatbotCustomerContext.service");
const { generateProductComparisonReply } = require("./gemini.provider");

/**
 * Resolve customerId directly using mongoose to avoid circular dependency
 * with chatbot.service.js
 */
async function getCustomerId(user) {
  if (user && user.customer_id) return user.customer_id;
  if (user && user.account_id) {
    const Customer = require("../models/customer.model");
    const c = await Customer.findOne({ account_id: user.account_id }).select("_id").lean();
    return c ? c._id : null;
  }
  return null;
}

/**
 * Find products to compare.
 */
async function findProductsForComparison(message, productIds = []) {
  let products = [];
  
  if (productIds && productIds.length > 0) {
    products = await Product.find({ _id: { $in: productIds }, isActive: true, productStatus: "active" })
      .populate("brandId", "brandName")
      .populate("categoryId", "categoryName")
      .lean();
  } else {
    // Simple text matching fallback: Extract words > 2 chars, ignore stop words
    const stopWords = ["so", "sánh", "và", "hay", "với", "tốt", "hơn", "loại", "nào", "khác", "nhau", "nên", "mua"];
    const parts = message.toLowerCase().split(/(?:\s+và\s+|\s+hay\s+|\s+với\s+|,)/);
    
    for (let part of parts) {
      part = part.replace(/[^\w\sđ]/g, "").trim();
      const words = part.split(/\s+/).filter(w => w.length >= 2 && !stopWords.includes(w));
      if (words.length === 0) continue;
      
      const searchRegex = new RegExp(words.join(".*"), "i");
      
      const match = await Product.findOne({
        isActive: true,
        productStatus: "active",
        productName: { $regex: searchRegex }
      })
      .populate("brandId", "brandName")
      .populate("categoryId", "categoryName")
      .lean();
      
      if (match && !products.some(p => p._id.toString() === match._id.toString())) {
        products.push(match);
        if (products.length >= 3) break; // compare at most 3 products
      }
    }
  }

  // Attach reviews
  for (const p of products) {
    const rs = await ReviewSummary.findOne({ productId: p._id }).lean();
    p.reviewCount = rs ? rs.reviewCount : 0;
  }
  
  return products.slice(0, 3);
}

/**
 * Compare products structurally
 */
function compareProducts(products, customerProfile) {
  const comparison = {
    products: [],
    differences: {},
    pros_cons: {},
    recommendation: "" // Will be explained by Gemini, but we can set structure here
  };
  
  for (const p of products) {
    comparison.products.push({
      id: p._id,
      name: p.productName,
      brand: p.brandId?.brandName || "Không rõ",
      category: p.categoryId?.categoryName || "Không rõ",
      price: p.price,
      image: p.imageUrl,
      rating: p.averageRating,
      reviews: p.reviewCount,
      skin_types: p.skin_types_supported || [],
      concerns: p.concerns_targeted || [],
      sensitive_friendly: p.is_sensitive_friendly || false,
      stock: p.stock
    });
  }
  
  if (products.length >= 2) {
    const p1 = products[0];
    const p2 = products[1];
    
    // Price difference
    const diff = Math.abs(p1.price - p2.price);
    if (diff > 0) {
      const cheaper = p1.price < p2.price ? p1.productName : p2.productName;
      comparison.differences["price"] = `Sản phẩm ${cheaper} rẻ hơn ${diff.toLocaleString('vi-VN')}đ`;
    } else {
      comparison.differences["price"] = `Hai sản phẩm có cùng mức giá (${p1.price.toLocaleString('vi-VN')}đ)`;
    }
    
    // Category difference
    if (p1.categoryId?._id?.toString() !== p2.categoryId?._id?.toString()) {
      comparison.differences["category"] = `Chúng phục vụ mục đích khác nhau: ${p1.productName} là ${p1.categoryId?.categoryName}, còn ${p2.productName} là ${p2.categoryId?.categoryName}.`;
    }
    
    // Skin match
    if (customerProfile && customerProfile.skin_type) {
      const p1Match = p1.skin_types_supported?.includes(customerProfile.skin_type);
      const p2Match = p2.skin_types_supported?.includes(customerProfile.skin_type);
      
      let desc = "";
      if (p1Match && p2Match) {
        desc = `Cả hai đều phù hợp với da ${customerProfile.skin_type} của bạn.`;
      } else if (p1Match) {
        desc = `${p1.productName} thiết kế phù hợp hơn cho da ${customerProfile.skin_type}.`;
      } else if (p2Match) {
        desc = `${p2.productName} thiết kế phù hợp hơn cho da ${customerProfile.skin_type}.`;
      } else {
        desc = `Cả hai không đặc biệt dành cho da ${customerProfile.skin_type}.`;
      }
      comparison.differences["skin_match"] = desc;
    }
    
    // Pros & Cons
    for (const p of products) {
      const pros = [];
      const cons = [];
      if (p.is_sensitive_friendly) pros.push("An toàn cho da nhạy cảm");
      if (p.concerns_targeted?.length > 0) pros.push(`Hỗ trợ: ${p.concerns_targeted.slice(0, 3).join(", ")}`);
      
      if (p.stock === 0) cons.push("Đang hết hàng");
      else if (p.stock < 10) cons.push("Sắp hết hàng");
      
      if (customerProfile && customerProfile.skin_type && !p.skin_types_supported?.includes(customerProfile.skin_type)) {
        cons.push(`Có thể không tối ưu cho da ${customerProfile.skin_type}`);
      }
      
      comparison.pros_cons[p.productName] = { pros, cons };
    }
  }
  
  return comparison;
}

/**
 * Handle Product Comparison Phase 6A
 */
async function handleProductComparison(message, user, productIds, history) {
  const customerId = await getCustomerId(user);
  let customerProfile = null;
  if (customerId) {
    const context = await getCustomerContext(customerId);
    customerProfile = context.customer_profile;
  }
  
  const products = await findProductsForComparison(message, productIds);
  
  if (products.length === 0) {
    return { 
      botText: "Bạn muốn so sánh sản phẩm nào? Hãy gửi tên sản phẩm nhé.", 
      replyType: "text", 
      quickReplies: ["Tư vấn sản phẩm", "Gặp nhân viên hỗ trợ"] 
    };
  } else if (products.length === 1) {
    return { 
      botText: `Bạn muốn so sánh ${products[0].productName} với sản phẩm nào?`, 
      replyType: "text", 
      quickReplies: ["Tư vấn sản phẩm", "Gặp nhân viên hỗ trợ"] 
    };
  }
  
  const comparison = compareProducts(products, customerProfile);
  
  // Need to call gemini provider
  let botReply;
  try {
    botReply = await generateProductComparisonReply(message, comparison, customerProfile, history);
  } catch (e) {
    botReply = `Mình đã phân tích ${comparison.products.length} sản phẩm. Bạn có thể xem chi tiết sự khác biệt và ưu nhược điểm của từng loại dưới đây.`;
  }
  
  return {
    botText: botReply,
    comparison: comparison,
    replyType: "product_comparison",
    quickReplies: ["So sánh sản phẩm khác", "Thêm vào giỏ hàng", "Tư vấn thêm", "Gặp nhân viên hỗ trợ"]
  };
}

module.exports = {
  findProductsForComparison,
  compareProducts,
  handleProductComparison
};
