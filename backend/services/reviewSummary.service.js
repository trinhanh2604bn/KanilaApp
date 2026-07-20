const Review = require("../models/review.model");
const ReviewMedia = require("../models/reviewMedia.model");
const ReviewSummary = require("../models/reviewSummary.model");
const Product = require("../models/product.model");

const DICTIONARY = {
  "Mùi dễ chịu": ["mui de chiu", "mui thom", "thom", "khong hac", "de chiu"],
  "Mờ thâm": ["mo tham", "giam tham", "tham mun", "vet tham", "tham"],
  "Lên màu đẹp": ["len mau", "mau dep", "len mau chuan", "mau xinh"],
  "Bền màu": ["ben mau", "lau troi", "giu mau", "khong troi"],
  "Mềm mịn": ["mem min", "chat mem", "chat min", "min"],
  "Dễ tán": ["de tan", "tan deu", "de blend", "khong bi moc"],
  "Che phủ tốt": ["che phu", "che khuyet diem", "phu tot"],
  "Kiềm dầu": ["kiem dau", "khong bong dau", "bot dau"],
  "Dưỡng ẩm": ["duong am", "am da", "khong kho", "mem moi"],
  "Không kích ứng": ["khong kich ung", "lanh tinh", "khong gay mun", "khong bi ngua"],
  "Giá tốt": ["gia tot", "dang tien", "hop gia", "gia on"],
  "Đóng gói đẹp": ["dong goi", "bao bi", "dep", "chac chan"],
};

const STOPWORDS = ["la", "rat", "qua", "thi", "ma", "minh", "toi", "ban", "nay", "do", "co", "khong", "duoc", "voi", "cho", "san", "pham", "sp", "hang", "luon", "ay", "nhe", "nha", "cung", "nhung", "vi", "da", "moi", "mot", "lan", "khi", "dung", "su", "tot", "that"];

function normalizeText(text) {
  if (!text) return "";
  return text
    .toString()
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^\w\s]/g, "")
    .trim();
}

function generateKeywordsFromReviews(reviews) {
  const keywordCounts = {};

  reviews.forEach(review => {
    const combinedText = [
      review.reviewTitle,
      review.reviewContent,
      ...(review.reviewTags || []),
      ...(review.skinTypes || [])
    ].join(" ");

    const normalized = normalizeText(combinedText);

    // Dictionary matching
    Object.entries(DICTIONARY).forEach(([label, phrases]) => {
      phrases.forEach(phrase => {
        if (normalized.includes(phrase)) {
          keywordCounts[label] = (keywordCounts[label] || 0) + 1;
        }
      });
    });
  });

  let results = Object.entries(keywordCounts)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 8);

  if (results.length < 3) {
      // Fallback to token frequency
      const tokenCounts = {};
      reviews.forEach(review => {
          const combinedText = [review.reviewTitle, review.reviewContent].join(" ");
          const normalized = normalizeText(combinedText);
          const tokens = normalized.split(/\s+/);
          tokens.forEach(token => {
              if (token.length >= 3 && !STOPWORDS.includes(token)) {
                  tokenCounts[token] = (tokenCounts[token] || 0) + 1;
              }
          });
      });
      const topTokens = Object.entries(tokenCounts)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 8 - results.length);

      const newResults = topTokens.map(t => [t[0], t[1]]);
      results = [...results, ...newResults];
  }

  return {
    keywords: results.map(r => r[0]),
    keywordStats: results.map(r => ({ label: r[0], count: r[1] }))
  };
}

function generateAiSummaryFromReviews(reviews, averageRating, keywords) {
  const reviewCount = reviews.length;
  if (reviewCount === 0) return null;

  const keywordText = keywords.length > 0 ? keywords.join(", ") : "chất lượng, trải nghiệm sử dụng và mức độ phù hợp";

  if (reviewCount < 3) {
    return `Sản phẩm hiện có ít đánh giá. Các phản hồi hiện tại chủ yếu ghi nhận trải nghiệm về ${keywordText}.`;
  }

  if (averageRating >= 4.5) {
    return `Đa số khách hàng đánh giá rất tích cực về sản phẩm. Các điểm được nhắc đến nhiều gồm ${keywordText}. Nhìn chung, sản phẩm mang lại trải nghiệm tốt và phù hợp với nhu cầu sử dụng thường xuyên.`;
  }

  if (averageRating >= 4.0) {
    return `Phần lớn đánh giá dành cho sản phẩm là tích cực. Người dùng thường nhắc đến ${keywordText}. Trải nghiệm tổng thể khá tốt, tuy mức độ phù hợp có thể khác nhau tùy nhu cầu mỗi khách hàng.`;
  }

  if (averageRating >= 3.0) {
    return `Đánh giá về sản phẩm ở mức trung bình khá. Người dùng ghi nhận một số điểm như ${keywordText}, nhưng trải nghiệm chưa thật sự đồng đều giữa các khách hàng.`;
  }

  return `Sản phẩm nhận được nhiều phản hồi chưa thật sự tích cực. Người dùng nên đọc kỹ các đánh giá chi tiết để cân nhắc mức độ phù hợp trước khi mua.`;
}

async function getAggregateReviewSummary(productId) {
  console.log("[ReviewSummary] productId =", productId);

  const reviews = await Review.find({ productId, reviewStatus: "visible" }).lean();
  console.log("[ReviewSummary] visible reviews =", reviews.length);

  const reviewCount = reviews.length;
  let averageRating = 0;
  let ratingCounts = { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 };

  if (reviewCount > 0) {
    let sumRating = 0;
    reviews.forEach(r => {
      sumRating += r.rating;
      if (ratingCounts[r.rating] !== undefined) {
        ratingCounts[r.rating]++;
      }
    });
    averageRating = Number((sumRating / reviewCount).toFixed(1));
  }

  console.log("[ReviewSummary] averageRating =", averageRating);
  console.log("[ReviewSummary] rating counts =", ratingCounts[1], ratingCounts[2], ratingCounts[3], ratingCounts[4], ratingCounts[5]);

  const { keywords, keywordStats } = generateKeywordsFromReviews(reviews);
  console.log("[ReviewSummary] keywords =", keywords);

  const aiSummary = generateAiSummaryFromReviews(reviews, averageRating, keywords);

  // Media preview
  const reviewIds = reviews.map(r => r._id);
  const reviewMediaPreview = await ReviewMedia.find({
    reviewId: { $in: reviewIds }
  })
  .sort({ createdAt: -1, sortOrder: 1 })
  .limit(8)
  .lean();

  console.log("[ReviewSummary] media preview size =", reviewMediaPreview.length);
  reviewMediaPreview.forEach(m => {
      if (m.mediaUrl && m.mediaUrl.startsWith("content://")) {
          console.warn("[ReviewSummary] review media uses content:// URI; should be uploaded to server storage", m.mediaUrl);
      }
  });

  const summaryData = {
    productId,
    averageRating,
    reviewCount,
    rating1Count: ratingCounts[1],
    rating2Count: ratingCounts[2],
    rating3Count: ratingCounts[3],
    rating4Count: ratingCounts[4],
    rating5Count: ratingCounts[5],
    ratingDistribution: ratingCounts,
    aiSummary,
    keywords,
    keywordStats,
    reviewMediaPreview
  };

  // Optional cache update
  try {
    await ReviewSummary.findOneAndUpdate(
      { productId },
      {
        productId,
        averageRating,
        reviewCount,
        rating1Count: ratingCounts[1],
        rating2Count: ratingCounts[2],
        rating3Count: ratingCounts[3],
        rating4Count: ratingCounts[4],
        rating5Count: ratingCounts[5],
        aiSummary,
        keywords,
        keywordStats,
        updatedAt: new Date()
      },
      { upsert: true }
    );
  } catch (err) {
    console.error("[ReviewSummary] Cache update failed:", err.message);
  }

  // Update product average rating
  try {
      await Product.findByIdAndUpdate(productId, {
          averageRating
      });
  } catch (err) {
      console.error("[ReviewSummary] Product rating update failed:", err.message);
  }

  return summaryData;
}

module.exports = {
  getAggregateReviewSummary,
  generateKeywordsFromReviews,
  generateAiSummaryFromReviews
};
