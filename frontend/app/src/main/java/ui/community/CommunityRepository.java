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
        List<String> images1 = new ArrayList<>();
        images1.add("https://theordinary.com/dw/image/v2/BFNS_PRD/on/demandware.static/-/Sites-deciem-master/default/dw11b1f09c/Images/products/The%20Ordinary/rdn-niacinamide-10pct-zinc-1pct-30ml.png");
        inMemoryPosts.add(new Post("1", "Kim Trần", null, "2 giờ trước", "Serum Niacinamide 10% – sáng da, mờ thâm thấy rõ!", "Sau 3 tuần sử dụng The Ordinary Niacinamide 10% + Zinc 1% da mình sáng và cũng không nhờn rít.", images1, 1200, 137, 36, true, true));

        List<String> images2 = new ArrayList<>();
        images2.add("https://m.media-amazon.com/images/I/61y8B2s7SLL._SL1500_.jpg");
        inMemoryPosts.add(new Post("2", "Linh Nguyễn", null, "5 giờ trước", "Kem chống nắng for da dầu mụn", "Mình đã thử rất nhiều loại nhưng chân ái vẫn là La Roche-Posay Anthelios. Kiềm dầu cực tốt luôn.", images2, 850, 42, 12, false, true));

        List<String> images3 = new ArrayList<>();
        images3.add("https://thebodyshop.com.vn/media/catalog/product/t/e/tea_tree_oil_10ml_1.jpg");
        inMemoryPosts.add(new Post("3", "Mai Anh", null, "1 ngày trước", "Tea Tree Oil - Cứu tinh cho nốt mụn sưng", "Mỗi khi có mụn sưng đỏ, mình chấm ngay tinh dầu tràm trà này. Mụn xẹp nhanh kinh khủng luôn mọi người ơi.", images3, 560, 28, 5, true, false));

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
                p.setCommentCount(p.getCommentCount() + 1);
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
