package ui.community;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class CommunityRepository {
    private static CommunityRepository instance;
    private final MutableLiveData<List<Post>> feedPostsData = new MutableLiveData<>();
    private final List<Post> inMemoryPosts = new ArrayList<>();
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private static final String PREF_NAME = "community_data_prefs";
    private static final String KEY_POSTS = "saved_posts";

    private CommunityRepository(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadFromStorage();
        if (inMemoryPosts.isEmpty()) {
            initMockData();
        }
    }

    public static synchronized CommunityRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CommunityRepository(context);
        }
        return instance;
    }

    private void loadFromStorage() {
        String json = prefs.getString(KEY_POSTS, null);
        if (json != null) {
            List<Post> saved = gson.fromJson(json, new TypeToken<List<Post>>() {}.getType());
            if (saved != null) {
                inMemoryPosts.clear();
                inMemoryPosts.addAll(saved);
                feedPostsData.setValue(new ArrayList<>(inMemoryPosts));
            }
        }
    }

    private void saveToStorage() {
        String json = gson.toJson(inMemoryPosts);
        prefs.edit().putString(KEY_POSTS, json).apply();
    }

    private void initMockData() {
        String pkg = "com.example.frontend";
        
        List<String> images1 = new ArrayList<>();
        images1.add("android.resource://" + pkg + "/drawable/img_c1");
        images1.add("android.resource://" + pkg + "/drawable/img_c2");
        images1.add("android.resource://" + pkg + "/drawable/img_c3");
        inMemoryPosts.add(new Post("1", "Kim Trần", null, "2 giờ trước", "Serum Niacinamide 10% – sáng da, mờ thâm thấy rõ!", "Sau 3 tuần sử dụng The Ordinary Niacinamide 10% + Zinc 1% da mình sáng và cũng không nhờn rít.", images1, 1200, 137, 36, true, true));

        List<String> images2 = new ArrayList<>();
        images2.add("android.resource://" + pkg + "/drawable/img_c4");
        images2.add("android.resource://" + pkg + "/drawable/img_c5");
        images2.add("android.resource://" + pkg + "/drawable/img_c6");
        images2.add("android.resource://" + pkg + "/drawable/img_c7");
        inMemoryPosts.add(new Post("2", "Linh Nguyễn", null, "5 giờ trước", "Kem chống nắng for da dầu mụn", "Mình đã thử rất nhiều loại nhưng chân ái vẫn là La Roche-Posay Anthelios. Kiềm dầu cực tốt luôn.", images2, 850, 42, 12, false, true));

        List<String> images3 = new ArrayList<>();
        images3.add("android.resource://" + pkg + "/drawable/img_c8");
        images3.add("android.resource://" + pkg + "/drawable/img_c9");
        images3.add("android.resource://" + pkg + "/drawable/img_c10");
        inMemoryPosts.add(new Post("3", "Mai Anh", null, "1 ngày trước", "Tea Tree Oil - Cứu tinh cho nốt mụn sưng", "Mỗi khi có mụn sưng đỏ, mình chấm ngay tinh dầu tràm trà này. Mụn xẹp nhanh kinh khủng luôn mọi người ơi.", images3, 560, 28, 5, true, false));

        List<String> images4 = new ArrayList<>();
        images4.add("android.resource://" + pkg + "/drawable/img_c14");
        images4.add("android.resource://" + pkg + "/drawable/img_c15");
        images4.add("android.resource://" + pkg + "/drawable/img_c16");
        inMemoryPosts.add(new Post("4", "Minh Thư", null, "2 giờ trước", "Son bùn Fwee siêu mịn môi", "Chất son bùn xốp mịn, tán lên môi cực xinh luôn. Màu Rose Obsession là chân ái.", images4, 320, 45, 10, true, true));

        List<String> images5 = new ArrayList<>();
        images5.add("android.resource://" + pkg + "/drawable/img_c17");
        images5.add("android.resource://" + pkg + "/drawable/img_c18");
        images5.add("android.resource://" + pkg + "/drawable/img_c19");
        inMemoryPosts.add(new Post("5", "Hoàng Nam", null, "3 giờ trước", "Bảng mắt 3CE Multi Eye Color Palette", "Tone màu hồng đất rất dễ dùng cho đi làm hay đi chơi. Màu lên chuẩn và bám tốt.", images5, 210, 12, 4, false, false));

        List<String> images6 = new ArrayList<>();
        images6.add("android.resource://" + pkg + "/drawable/img_c20");
        images6.add("android.resource://" + pkg + "/drawable/img_c21");
        inMemoryPosts.add(new Post("6", "Thảo Vy", null, "6 giờ trước", "Mascara Maybelline Sky High - Mi cong dài", "Tách mi cực tốt, không bị vón cục và chống nước siêu đỉnh. Rất đáng mua.", images6, 450, 30, 8, true, false));

        List<String> images7 = new ArrayList<>();
        images7.add("android.resource://" + pkg + "/drawable/img_c23");
        images7.add("android.resource://" + pkg + "/drawable/img_c24");
        images7.add("android.resource://" + pkg + "/drawable/img_c25");
        inMemoryPosts.add(new Post("7", "Quốc Bảo", null, "10 giờ trước", "Phấn phủ bột Loose Powder kiềm dầu", "Hạt phấn siêu nhỏ mịn, giúp giữ lớp nền bền màu cả ngày dài mà không bị mốc.", images7, 180, 25, 2, false, true));

        List<String> images8 = new ArrayList<>();
        images8.add("android.resource://" + pkg + "/drawable/img_c26");
        images8.add("android.resource://" + pkg + "/drawable/img_c27");
        inMemoryPosts.add(new Post("8", "Hải Yến", null, "12 giờ trước", "Kẻ mắt nước sắc mảnh, lâu trôi", "Nét vẽ cực mảnh và sắc sảo. Không hề lem dù mình có mí mắt khá dầu.", images8, 600, 80, 15, true, true));

        List<String> images9 = new ArrayList<>();
        images9.add("android.resource://" + pkg + "/drawable/img_c28");
        images9.add("android.resource://" + pkg + "/drawable/img_c29");
        inMemoryPosts.add(new Post("9", "Đức Anh", null, "15 giờ trước", "Phấn highlight bắt sáng tự nhiên", "Giúp gương mặt bừng sáng và có chiều sâu hơn. Hiệu ứng glowy rất đẹp.", images9, 150, 10, 3, false, false));

        List<String> images10 = new ArrayList<>();
        images10.add("android.resource://" + pkg + "/drawable/img_c30");
        images10.add("android.resource://" + pkg + "/drawable/img_c31");
        inMemoryPosts.add(new Post("10", "Phương Linh", null, "18 giờ trước", "Kem nền Nars - Lớp nền hoàn hảo", "Che phủ tốt mà trông vẫn tự nhiên. Bám màu cả ngày không trôi.", images10, 380, 50, 12, true, true));

        List<String> images11 = new ArrayList<>();
        images11.add("android.resource://" + pkg + "/drawable/img_c32");
        images11.add("android.resource://" + pkg + "/drawable/img_c33");
        inMemoryPosts.add(new Post("11", "Tuấn Kiệt", null, "20 giờ trước", "Phấn má hồng Rare Beauty rạng rỡ", "Màu sắc lên chuẩn, chỉ cần một chấm nhỏ là đủ tươi tắn cả ngày.", images11, 290, 35, 7, false, true));

        List<String> images12 = new ArrayList<>();
        images12.add("android.resource://" + pkg + "/drawable/img_c24");
        images12.add("android.resource://" + pkg + "/drawable/img_c15");
        inMemoryPosts.add(new Post("12", "Gia Hân", null, "1 ngày trước", "Xịt khoá nền giữ lớp trang điểm lâu trôi", "Xịt xong cảm giác lớp makeup tiệp hẳn vào da, không lo bị trôi hay xuống tone.", images12, 750, 95, 20, true, false));

        List<String> images13 = new ArrayList<>();
        images13.add("android.resource://" + pkg + "/drawable/img_c27");
        images13.add("android.resource://" + pkg + "/drawable/img_c13");
        images13.add("android.resource://" + pkg + "/drawable/img_c4");
        inMemoryPosts.add(new Post("13", "Anh Tuấn", null, "2 ngày trước", "Son tint bóng Romand căng mọng", "Chất son tint nhẹ môi, lớp bóng đẹp như kẹo hồ lô. Màu sắc trendy rất trẻ trung.", images13, 120, 15, 5, false, true));

        feedPostsData.setValue(new ArrayList<>(inMemoryPosts));
    }

    public LiveData<List<Post>> getFeedPosts() {
        return feedPostsData;
    }

    public void addPost(Post post) {
        inMemoryPosts.add(0, post);
        feedPostsData.setValue(new ArrayList<>(inMemoryPosts));
        saveToStorage();
    }

    public void updatePost(Post updatedPost) {
        for (int i = 0; i < inMemoryPosts.size(); i++) {
            if (inMemoryPosts.get(i).getId().equals(updatedPost.getId())) {
                inMemoryPosts.set(i, updatedPost);
                feedPostsData.setValue(new ArrayList<>(inMemoryPosts));
                saveToStorage();
                break;
            }
        }
    }

    public void deletePost(String postId) {
        for (int i = 0; i < inMemoryPosts.size(); i++) {
            if (inMemoryPosts.get(i).getId().equals(postId)) {
                inMemoryPosts.remove(i);
                feedPostsData.setValue(new ArrayList<>(inMemoryPosts));
                saveToStorage();
                break;
            }
        }
    }

    public void addComment(String postId, Comment comment) {
        for (Post p : inMemoryPosts) {
            if (p.getId().equals(postId)) {
                p.getComments().add(comment);
                p.setCommentCount(p.getComments().size());
                feedPostsData.setValue(new ArrayList<>(inMemoryPosts));
                saveToStorage();
                break;
            }
        }
    }

    public void toggleSave(String postId, boolean isSaved) {
        for (Post p : inMemoryPosts) {
            if (p.getId().equals(postId)) {
                p.setSaved(isSaved);
                feedPostsData.setValue(new ArrayList<>(inMemoryPosts));
                saveToStorage();
                break;
            }
        }
    }
}
