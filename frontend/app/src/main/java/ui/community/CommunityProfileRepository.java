package ui.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommunityProfileRepository {
    private static CommunityProfileRepository instance;

    private CommunityProfileRepository() {}

    public static synchronized CommunityProfileRepository getInstance() {
        if (instance == null) {
            instance = new CommunityProfileRepository();
        }
        return instance;
    }

    public LiveData<CommunityProfile> getMyProfile() {
        MutableLiveData<CommunityProfile> data = new MutableLiveData<>();
        CommunityProfile profile = new CommunityProfile(
                "user123",
                "Thanh Thanh",
                "thanhthanh_beauty",
                null,
                Arrays.asList("Da dầu", "Mụn ẩn"),
                1200,
                450,
                8500,
                2500
        );
        data.setValue(profile);
        return data;
    }

    public LiveData<List<Post>> getMyPosts() {
        MutableLiveData<List<Post>> data = new MutableLiveData<>();
        List<Post> posts = new ArrayList<>();
        
        List<String> images = new ArrayList<>();
        images.add("https://m.media-amazon.com/images/I/61y8B2s7SLL._SL1500_.jpg");
        posts.add(new Post("p101", "Thanh Thanh", null, "1 tuần trước", "Routine trị mụn của mình", "Chia sẻ với mọi người bộ sản phẩm mình đang dùng để trị mụn ẩn...", images, 150, 12, 5, true, true));
        
        data.setValue(posts);
        return data;
    }

    public LiveData<List<SavedContent>> getSavedContent() {
        MutableLiveData<List<SavedContent>> data = new MutableLiveData<>();
        List<SavedContent> saved = new ArrayList<>();
        
        saved.add(new SavedContent("1", SavedContent.TYPE_BLOG, "5 bước skincare buổi sáng cho người bận rộn", null, "Kanila Official", "2 ngày trước"));
        saved.add(new SavedContent("2", SavedContent.TYPE_FEED, "Serum Niacinamide 10% – sáng da, mờ thâm thấy rõ!", null, "Kim Trần", "3 ngày trước"));
        
        data.setValue(saved);
        return data;
    }
}
