package com.example.frontend.feature.community.reels.mock;

import com.example.frontend.model.Product;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Mock data for Kanila Reels MVP using real Product model.
 */
public final class MockReelsDataSource {

    private MockReelsDataSource() {
        // Utility class
    }

    public static final String VIDEO_URL_01 = "android.resource://com.example.frontend/raw/reel_demo_1";
    public static final String VIDEO_URL_02 = "android.resource://com.example.frontend/raw/reel_demo_2";
    public static final String VIDEO_URL_03 = "android.resource://com.example.frontend/raw/reel_demo_3";
    public static final String VIDEO_URL_04 = "android.resource://com.example.frontend/raw/reel_demo_4";
    public static final String VIDEO_URL_05 = "android.resource://com.example.frontend/raw/reel_demo_5";

    private static final String DEFAULT_AVATAR_URL = "";
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
                        new Product("65f123456789012345678901", "Kanila", "Kanila Glow Tint Màu 03 Rosy Nude", "189000", "4.8", "1.2K", 0, "Best Seller", "Lipstick"),
                        new Product("65f123456789012345678902", "Kanila", "Kanila Lip Care Balm", "129000", "4.7", "845", 0, "", "Lipcare")
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
                        new Product("65f123456789012345678903", "Kanila", "Kanila Gentle Gel Cleanser", "169000", "4.9", "2.1K", 0, "", "Cleanser"),
                        new Product("65f123456789012345678904", "Kanila", "Kanila Oil-Free Moisturizer", "229000", "4.8", "1.7K", 0, "", "Moisturizer"),
                        new Product("65f123456789012345678905", "Kanila", "Kanila Daily UV Sunscreen SPF50+", "259000", "4.8", "3.4K", 0, "", "Sunscreen")
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
                        new Product("65f123456789012345678906", "Kanila", "Kanila Starter Skincare Set", "599000", "4.9", "986", 0, "New", "Set")
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
                        new Product("65f123456789012345678905", "Kanila", "Kanila Daily UV Sunscreen SPF50+", "259000", "4.8", "3.4K", 0, "", "Sunscreen"),
                        new Product("65f123456789012345678907", "Kanila", "Kanila Sun Cushion Refill", "199000", "4.6", "512", 0, "", "Sunscreen")
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
                        new Product("65f123456789012345678908", "Kanila", "Kanila Skin Veil Cushion 21N", "329000", "4.7", "1.5K", 0, "", "Cushion"),
                        new Product("65f123456789012345678909", "Kanila", "Kanila Cloud Blush 02 Peach Milk", "179000", "4.8", "934", 0, "", "Blush"),
                        new Product("65f123456789012345678901", "Kanila", "Kanila Glow Tint Màu 03 Rosy Nude", "189000", "4.8", "1.2K", 0, "Best Seller", "Lipstick")
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

    public static List<Product> getProductsByReelId(String reelId) {
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
        private final List<Product> products;
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
                List<Product> products
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
            this.products = products == null ? Collections.<Product>emptyList() : Collections.unmodifiableList(products);
            this.liked = false;
            this.saved = false;
        }

        public String getId() { return id; }
        public String getVideoUrl() { return videoUrl; }
        public String getThumbnailUrl() { return thumbnailUrl; }
        public String getCreatorName() { return creatorName; }
        public String getCreatorUsername() { return creatorUsername; }
        public String getCreatorAvatarUrl() { return creatorAvatarUrl; }
        public String getCaption() { return caption; }
        public List<String> getHashtags() { return hashtags; }
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
        public String getAudioName() { return audioName; }
        public String getLikeCountText() { return likeCountText; }
        public String getCommentCountText() { return commentCountText; }
        public String getSaveCountText() { return saveCountText; }
        public List<Product> getProducts() { return products; }
        public int getProductCount() { return products == null ? 0 : products.size(); }
        public String getProductPillText() {
            int count = getProductCount();
            if (count <= 0) return "";
            return count + " sản phẩm";
        }
        public boolean hasProducts() { return getProductCount() > 0; }
        public boolean isLiked() { return liked; }
        public void setLiked(boolean liked) { this.liked = liked; }
        public void toggleLiked() { this.liked = !this.liked; }
        public boolean isSaved() { return saved; }
        public void setSaved(boolean saved) { this.saved = saved; }
        public void toggleSaved() { this.saved = !this.saved; }
    }
}
