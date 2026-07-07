package com.example.frontend.feature.community.reels.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Mock data for Kanila Reels MVP.
 *
 * Usage:
 * 1. Put this file in:
 *    app/src/main/java/com/example/frontend/feature/community/reels/mock/MockReelsDataSource.java
 * 2. If your app package is not com.example.frontend, change the package line above.
 * 3. Replace only VIDEO_URL_01..VIDEO_URL_05 with your real mp4 URLs or local raw resource URIs.
 * 4. Keep this data layer isolated so you can replace it with API data later.
 *
 * Example remote video URL:
 *    https://your-domain.com/videos/reel_01.mp4
 *
 * Example local raw resource URI:
 *    android.resource://com.example.frontend/raw/reel_demo_1
 */
public final class MockReelsDataSource {

    private MockReelsDataSource() {
        // Utility class
    }

    // Replace these URLs only. Use direct .mp4 URLs or local raw resource URIs.
    // NOTE: Normal YouTube links will NOT work.
    public static final String VIDEO_URL_01 = "android.resource://com.example.frontend/raw/reel_demo_1";
    public static final String VIDEO_URL_02 = "android.resource://com.example.frontend/raw/reel_demo_2";
    public static final String VIDEO_URL_03 = "android.resource://com.example.frontend/raw/reel_demo_3";
    public static final String VIDEO_URL_04 = "android.resource://com.example.frontend/raw/reel_demo_4";
    public static final String VIDEO_URL_05 = "android.resource://com.example.frontend/raw/reel_demo_5";

    /**
     * NOTE TO DEVELOPER:
     * Please add short .mp4 files into app/src/main/res/raw/
     * Named: reel_demo_1.mp4, reel_demo_2.mp4, etc.
     * If these files are missing, the player will show the thumbnail/placeholder.
     */

    /**
     * Optional image URLs. You can leave them empty and let the UI use a default placeholder.
     * These are separated from video URLs so the MVP can still run even if images are not ready.
     */
    private static final String DEFAULT_AVATAR_URL = "";
    private static final String DEFAULT_PRODUCT_IMAGE_URL = "";
    private static final String DEFAULT_THUMBNAIL_URL = "";

    public static List<MockReel> getDemoReels() {
        List<MockReel> reels = new ArrayList<>();

        reels.add(new MockReel(
                "reel_001",
                VIDEO_URL_01,
                DEFAULT_THUMBNAIL_URL,
                "Kanila Beauty",
                "@kanila.official",
                DEFAULT_AVATAR_URL,
                "Son tint mới lên màu căng bóng, hợp makeup nhẹ mỗi ngày. Bấm giỏ hàng để xem sản phẩm trong video nha.",
                Arrays.asList("sonmoi", "glowtint", "makeupdaily"),
                "Original sound - Kanila",
                "12.8K",
                "326",
                "1.1K",
                Arrays.asList(
                        new MockReelProduct(
                                "demo_product_001",
                                "demo_variant_001",
                                "Kanila",
                                "Kanila Glow Tint Màu 03 Rosy Nude",
                                "Son tint bóng nhẹ, màu hồng đất dễ dùng hằng ngày",
                                DEFAULT_PRODUCT_IMAGE_URL,
                                "189.000đ",
                                "249.000đ",
                                "4.8",
                                "1.2K đánh giá",
                                "in_stock",
                                true
                        ),
                        new MockReelProduct(
                                "demo_product_002",
                                "demo_variant_002",
                                "Kanila",
                                "Kanila Lip Care Balm",
                                "Dưỡng môi trước khi thoa son, giúp môi mềm và ít lộ vân",
                                DEFAULT_PRODUCT_IMAGE_URL,
                                "129.000đ",
                                "159.000đ",
                                "4.7",
                                "845 đánh giá",
                                "in_stock",
                                false
                        )
                )
        ));

        reels.add(new MockReel(
                "reel_002",
                VIDEO_URL_02,
                DEFAULT_THUMBNAIL_URL,
                "Minh Anh Review",
                "@minhanh.beauty",
                DEFAULT_AVATAR_URL,
                "Routine skincare buổi sáng cho da dầu: làm sạch dịu nhẹ, cấp ẩm mỏng, chống nắng đủ lượng.",
                Arrays.asList("skincareroutine", "dadau", "buoisang"),
                "Morning routine - Kanila",
                "25.4K",
                "618",
                "2.4K",
                Arrays.asList(
                        new MockReelProduct(
                                "demo_product_003",
                                "demo_variant_003",
                                "Kanila",
                                "Kanila Gentle Gel Cleanser",
                                "Sữa rửa mặt gel dịu nhẹ cho da dầu và da hỗn hợp",
                                DEFAULT_PRODUCT_IMAGE_URL,
                                "169.000đ",
                                "199.000đ",
                                "4.9",
                                "2.1K đánh giá",
                                "in_stock",
                                true
                        ),
                        new MockReelProduct(
                                "demo_product_004",
                                "demo_variant_004",
                                "Kanila",
                                "Kanila Oil-Free Moisturizer",
                                "Kem dưỡng mỏng nhẹ, không gây bí da",
                                DEFAULT_PRODUCT_IMAGE_URL,
                                "229.000đ",
                                "279.000đ",
                                "4.8",
                                "1.7K đánh giá",
                                "in_stock",
                                false
                        ),
                        new MockReelProduct(
                                "demo_product_005",
                                "demo_variant_005",
                                "Kanila",
                                "Kanila Daily UV Sunscreen SPF50+",
                                "Kem chống nắng hằng ngày, ráo nhẹ, phù hợp đi học/đi làm",
                                DEFAULT_PRODUCT_IMAGE_URL,
                                "259.000đ",
                                "319.000đ",
                                "4.8",
                                "3.4K đánh giá",
                                "in_stock",
                                false
                        )
                )
        ));

        reels.add(new MockReel(
                "reel_003",
                VIDEO_URL_03,
                DEFAULT_THUMBNAIL_URL,
                "Kanila Unbox",
                "@kanila.unbox",
                DEFAULT_AVATAR_URL,
                "Unbox set chăm da cơ bản cho người mới bắt đầu. Ít bước nhưng vẫn đủ sạch - ẩm - chống nắng.",
                Arrays.asList("unbox", "skincarebasic", "newbie"),
                "Unboxing sound",
                "9.6K",
                "142",
                "784",
                Arrays.asList(
                        new MockReelProduct(
                                "demo_product_006",
                                "demo_variant_006",
                                "Kanila",
                                "Kanila Starter Skincare Set",
                                "Combo làm sạch, dưỡng ẩm và chống nắng cho người mới skincare",
                                DEFAULT_PRODUCT_IMAGE_URL,
                                "599.000đ",
                                "756.000đ",
                                "4.9",
                                "986 đánh giá",
                                "in_stock",
                                true
                        )
                )
        ));

        reels.add(new MockReel(
                "reel_004",
                VIDEO_URL_04,
                DEFAULT_THUMBNAIL_URL,
                "Hà Linh Skincare",
                "@halinh.skin",
                DEFAULT_AVATAR_URL,
                "Test texture kem chống nắng: chất kem mỏng, dễ tán, không quá bóng sau 5 phút.",
                Arrays.asList("kemchongnang", "texturetest", "spf50"),
                "Texture test - original",
                "31.2K",
                "904",
                "3.8K",
                Arrays.asList(
                        new MockReelProduct(
                                "demo_product_005",
                                "demo_variant_005",
                                "Kanila",
                                "Kanila Daily UV Sunscreen SPF50+",
                                "Kem chống nắng hằng ngày, ráo nhẹ, phù hợp đi học/đi làm",
                                DEFAULT_PRODUCT_IMAGE_URL,
                                "259.000đ",
                                "319.000đ",
                                "4.8",
                                "3.4K đánh giá",
                                "in_stock",
                                true
                        ),
                        new MockReelProduct(
                                "demo_product_007",
                                "demo_variant_007",
                                "Kanila",
                                "Kanila Sun Cushion Refill",
                                "Dặm lại chống nắng nhanh trong ngày",
                                DEFAULT_PRODUCT_IMAGE_URL,
                                "199.000đ",
                                "249.000đ",
                                "4.6",
                                "512 đánh giá",
                                "out_of_stock",
                                false
                        )
                )
        ));

        reels.add(new MockReel(
                "reel_005",
                VIDEO_URL_05,
                DEFAULT_THUMBNAIL_URL,
                "Linh Makeup",
                "@linh.makeup",
                DEFAULT_AVATAR_URL,
                "Makeup nhanh 5 phút: nền mỏng, má hồng nhẹ, son tint bóng. Phù hợp đi học và đi làm.",
                Arrays.asList("makeup5phut", "dailylook", "kanilamakeup"),
                "Daily makeup beat",
                "18.7K",
                "477",
                "1.9K",
                Arrays.asList(
                        new MockReelProduct(
                                "demo_product_008",
                                "demo_variant_008",
                                "Kanila",
                                "Kanila Skin Veil Cushion 21N",
                                "Cushion nền mỏng nhẹ, finish tự nhiên",
                                DEFAULT_PRODUCT_IMAGE_URL,
                                "329.000đ",
                                "399.000đ",
                                "4.7",
                                "1.5K đánh giá",
                                "in_stock",
                                true
                        ),
                        new MockReelProduct(
                                "demo_product_009",
                                "demo_variant_009",
                                "Kanila",
                                "Kanila Cloud Blush 02 Peach Milk",
                                "Má hồng kem tán mịn, màu đào sữa tự nhiên",
                                DEFAULT_PRODUCT_IMAGE_URL,
                                "179.000đ",
                                "229.000đ",
                                "4.8",
                                "934 đánh giá",
                                "in_stock",
                                false
                        ),
                        new MockReelProduct(
                                "demo_product_001",
                                "demo_variant_001",
                                "Kanila",
                                "Kanila Glow Tint Màu 03 Rosy Nude",
                                "Son tint bóng nhẹ, màu hồng đất dễ dùng hằng ngày",
                                DEFAULT_PRODUCT_IMAGE_URL,
                                "189.000đ",
                                "249.000đ",
                                "4.8",
                                "1.2K đánh giá",
                                "in_stock",
                                false
                        )
                )
        ));

        return Collections.unmodifiableList(reels);
    }

    public static MockReel findReelById(String reelId) {
        if (reelId == null) return null;
        for (MockReel reel : getDemoReels()) {
            if (reelId.equals(reel.getId())) {
                return reel;
            }
        }
        return null;
    }

    public static List<MockReelProduct> getProductsByReelId(String reelId) {
        MockReel reel = findReelById(reelId);
        if (reel == null) return Collections.emptyList();
        return reel.getProducts();
    }

    public static final class MockReel {
        private final String id;
        private final String videoUrl;
        private final String thumbnailUrl;
        private final String creatorName;
        private final String creatorUsername;
        private final String creatorAvatarUrl;
        private final String caption;
        private final List<String> hashtags;
        private final String audioName;
        private final String likeCountText;
        private final String commentCountText;
        private final String saveCountText;
        private final List<MockReelProduct> products;
        private boolean liked;
        private boolean saved;

        public MockReel(
                String id,
                String videoUrl,
                String thumbnailUrl,
                String creatorName,
                String creatorUsername,
                String creatorAvatarUrl,
                String caption,
                List<String> hashtags,
                String audioName,
                String likeCountText,
                String commentCountText,
                String saveCountText,
                List<MockReelProduct> products
        ) {
            this.id = id;
            this.videoUrl = videoUrl;
            this.thumbnailUrl = thumbnailUrl;
            this.creatorName = creatorName;
            this.creatorUsername = creatorUsername;
            this.creatorAvatarUrl = creatorAvatarUrl;
            this.caption = caption;
            this.hashtags = hashtags == null ? Collections.<String>emptyList() : Collections.unmodifiableList(hashtags);
            this.audioName = audioName;
            this.likeCountText = likeCountText;
            this.commentCountText = commentCountText;
            this.saveCountText = saveCountText;
            this.products = products == null ? Collections.<MockReelProduct>emptyList() : Collections.unmodifiableList(products);
            this.liked = false;
            this.saved = false;
        }

        public String getId() {
            return id;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public String getCreatorName() {
            return creatorName;
        }

        public String getCreatorUsername() {
            return creatorUsername;
        }

        public String getCreatorAvatarUrl() {
            return creatorAvatarUrl;
        }

        public String getCaption() {
            return caption;
        }

        public List<String> getHashtags() {
            return hashtags;
        }

        public String getHashtagText() {
            if (hashtags == null || hashtags.isEmpty()) return "";
            StringBuilder builder = new StringBuilder();
            for (String hashtag : hashtags) {
                if (hashtag == null || hashtag.trim().isEmpty()) continue;
                if (builder.length() > 0) builder.append(' ');
                builder.append('#').append(hashtag.trim());
            }
            return builder.toString();
        }

        public String getAudioName() {
            return audioName;
        }

        public String getLikeCountText() {
            return likeCountText;
        }

        public String getCommentCountText() {
            return commentCountText;
        }

        public String getSaveCountText() {
            return saveCountText;
        }

        public List<MockReelProduct> getProducts() {
            return products;
        }

        public int getProductCount() {
            return products == null ? 0 : products.size();
        }

        public String getProductPillText() {
            int count = getProductCount();
            if (count <= 0) return "";
            return count + " sản phẩm";
        }

        public boolean hasProducts() {
            return getProductCount() > 0;
        }

        public boolean isLiked() {
            return liked;
        }

        public void setLiked(boolean liked) {
            this.liked = liked;
        }

        public void toggleLiked() {
            this.liked = !this.liked;
        }

        public boolean isSaved() {
            return saved;
        }

        public void setSaved(boolean saved) {
            this.saved = saved;
        }

        public void toggleSaved() {
            this.saved = !this.saved;
        }
    }

    public static final class MockReelProduct {
        private final String productId;
        private final String variantId;
        private final String brandName;
        private final String productName;
        private final String shortDescription;
        private final String imageUrl;
        private final String priceText;
        private final String oldPriceText;
        private final String ratingText;
        private final String reviewCountText;
        private final String stockStatus;
        private final boolean primaryProduct;

        public MockReelProduct(
                String productId,
                String variantId,
                String brandName,
                String productName,
                String shortDescription,
                String imageUrl,
                String priceText,
                String oldPriceText,
                String ratingText,
                String reviewCountText,
                String stockStatus,
                boolean primaryProduct
        ) {
            this.productId = productId;
            this.variantId = variantId;
            this.brandName = brandName;
            this.productName = productName;
            this.shortDescription = shortDescription;
            this.imageUrl = imageUrl;
            this.priceText = priceText;
            this.oldPriceText = oldPriceText;
            this.ratingText = ratingText;
            this.reviewCountText = reviewCountText;
            this.stockStatus = stockStatus;
            this.primaryProduct = primaryProduct;
        }

        public String getProductId() {
            return productId;
        }

        public String getVariantId() {
            return variantId;
        }

        public String getBrandName() {
            return brandName;
        }

        public String getProductName() {
            return productName;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getPriceText() {
            return priceText;
        }

        public String getOldPriceText() {
            return oldPriceText;
        }

        public String getRatingText() {
            return ratingText;
        }

        public String getReviewCountText() {
            return reviewCountText;
        }

        public String getStockStatus() {
            return stockStatus;
        }

        public boolean isPrimaryProduct() {
            return primaryProduct;
        }

        public boolean isInStock() {
            return "in_stock".equalsIgnoreCase(stockStatus);
        }

        public String getStockText() {
            if (isInStock()) return "Còn hàng";
            return "Hết hàng";
        }
    }
}
