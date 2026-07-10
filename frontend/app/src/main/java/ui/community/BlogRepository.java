package ui.community;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class BlogRepository {
    private static BlogRepository instance;
    private final MutableLiveData<List<BlogPost>> blogsData = new MutableLiveData<>();
    private final List<BlogPost> inMemoryBlogs = new ArrayList<>();
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private static final String PREF_NAME = "blog_data_prefs";
    private static final String KEY_BLOGS = "saved_blogs_list";

    private BlogRepository(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadFromStorage();
        if (inMemoryBlogs.isEmpty()) {
            initMockData();
        }
    }

    public static synchronized BlogRepository getInstance(Context context) {
        if (instance == null) {
            instance = new BlogRepository(context);
        }
        return instance;
    }

    private void loadFromStorage() {
        String json = prefs.getString(KEY_BLOGS, null);
        if (json != null) {
            List<BlogPost> saved = gson.fromJson(json, new TypeToken<List<BlogPost>>() {}.getType());
            if (saved != null) {
                inMemoryBlogs.clear();
                inMemoryBlogs.addAll(saved);
                blogsData.setValue(new ArrayList<>(inMemoryBlogs));
            }
        }
    }

    private void saveToStorage() {
        String json = gson.toJson(inMemoryBlogs);
        prefs.edit().putString(KEY_BLOGS, json).apply();
    }

    public LiveData<List<BlogPost>> getFeaturedBlogs() {
        return blogsData;
    }

    public BlogPost getBlogById(String id) {
        for (BlogPost b : inMemoryBlogs) {
            if (b.getId().equals(id)) return b;
        }
        return null;
    }

    public void toggleSaveBlog(String blogId, boolean isSaved) {
        for (BlogPost b : inMemoryBlogs) {
            if (b.getId().equals(blogId)) {
                b.setSaved(isSaved);
                blogsData.setValue(new ArrayList<>(inMemoryBlogs));
                saveToStorage();
                break;
            }
        }
    }

    public void toggleLikeBlog(String blogId, boolean isLiked) {
        for (BlogPost b : inMemoryBlogs) {
            if (b.getId().equals(blogId)) {
                b.setLiked(isLiked);
                b.setLikeCount(isLiked ? b.getLikeCount() + 1 : Math.max(0, b.getLikeCount() - 1));
                blogsData.setValue(new ArrayList<>(inMemoryBlogs));
                saveToStorage();
                break;
            }
        }
    }

    private void initMockData() {
        inMemoryBlogs.clear();
        // ... (rest of mock data same as before but adding to inMemoryBlogs)
        BlogPost b1 = new BlogPost("1", "Cách trang điểm phù hợp với mùa hè", "Khám phá các bước makeup nhẹ nhàng, bền màu và giữ da luôn tươi tắn trong những ngày nắng nóng.", R.drawable.img_blog1, "Kanila Official", true, "2 ngày trước", "Makeup Tips");
        b1.setLikeCount(1200); b1.setCommentCount(126); b1.setShareCount(89);
        b1.setContent("Mùa hè oi bức thường khiến lớp trang điểm dễ bị trôi và bết dính...");
        inMemoryBlogs.add(b1);

        BlogPost b2 = new BlogPost("2", "Các hãng sản phẩm mới ra mắt đang nổi bật", "Tổng hợp những sản phẩm makeup mới nhất được cộng đồng yêu thích trong tháng này.", R.drawable.img_blog2, "Kanila Official", true, "5 ngày trước", "Product Update");
        b2.setLikeCount(968); b2.setCommentCount(102); b2.setShareCount(72);
        b2.setContent("Tháng này chứng kiến sự bùng nổ của các dòng son kem lì...");
        inMemoryBlogs.add(b2);

        BlogPost b3 = new BlogPost("3", "Quy trình makeup cho người mới bắt đầu", "Hướng dẫn chi tiết từng bước giúp bạn dễ dàng tự tin với lớp makeup đầu tiên của mình.", R.drawable.img_blog3, "Kanila Beauty Team", true, "1 tuần trước", "Beginner Guide");
        b3.setLikeCount(1400); b3.setCommentCount(178); b3.setShareCount(94);
        b3.setContent("Nếu bạn mới tập tành trang điểm, đừng quá lo lắng...");
        inMemoryBlogs.add(b3);

        BlogPost b4 = new BlogPost("4", "Makeup nền mỏng nhẹ mỗi ngày", "Quy trình base makeup tự nhiên, mịn đẹp và không bí da.", R.drawable.img_blog4, "Kanila Beauty Team", true, "2 tuần trước", "Base Makeup");
        b4.setLikeCount(1100); b4.setCommentCount(95); b4.setShareCount(63);
        b4.setContent("Xu hướng trang điểm 'Clean Girl'...");
        inMemoryBlogs.add(b4);

        blogsData.setValue(new ArrayList<>(inMemoryBlogs));
        saveToStorage();
    }
}
