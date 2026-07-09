package ui.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;

public class BlogRepository {
    private static BlogRepository instance;

    private BlogRepository() {}

    public static synchronized BlogRepository getInstance() {
        if (instance == null) {
            instance = new BlogRepository();
        }
        return instance;
    }

    public LiveData<List<BlogPost>> getFeaturedBlogs() {
        MutableLiveData<List<BlogPost>> data = new MutableLiveData<>();
        List<BlogPost> blogs = new ArrayList<>();

        BlogPost b1 = new BlogPost("1", "5 bước skincare buổi sáng cho người bận rộn", "Tiết kiệm thời gian nhưng vẫn đảm bảo làn da luôn rạng rỡ suốt cả ngày dài...", null, "Kanila Official", true, "1 ngày trước", "Skincare Routine");
        b1.setContent("Nội dung chi tiết về 5 bước skincare buổi sáng...\n\nBước 1: Làm sạch nhẹ nhàng.\nBước 2: Toner cân bằng da.\nBước 3: Serum Vitamin C.\nBước 4: Kem dưỡng ẩm.\nBước 5: Kem chống nắng.");
        b1.setLikeCount(450);
        b1.setCommentCount(28);
        b1.setShareCount(15);
        blogs.add(b1);

        BlogPost b2 = new BlogPost("2", "Top 3 kem nền kiềm dầu tốt nhất mùa hè 2025", "Bạn đang tìm kiếm lớp nền mịn lì bất chấp nắng nóng? Đừng bỏ qua danh sách này...", null, "Kanila Beauty Team", true, "3 ngày trước", "Makeup Tips");
        b2.setContent("Danh sách 3 kem nền chân ái cho mùa hè này...\n1. Fenty Beauty Pro Filt'r\n2. Estée Lauder Double Wear\n3. Maybelline Fit Me");
        b2.setLikeCount(890);
        b2.setCommentCount(112);
        b2.setShareCount(56);
        blogs.add(b2);

        data.setValue(blogs);
        return data;
    }

    public BlogPost getBlogById(String id) {
        // In a real app, this would search the list or fetch from API
        if ("1".equals(id)) {
             BlogPost b = new BlogPost("1", "5 bước skincare buổi sáng cho người bận rộn", "Tiết kiệm thời gian nhưng vẫn đảm bảo làn da luôn rạng rỡ suốt cả ngày dài...", null, "Kanila Official", true, "1 ngày trước", "Skincare Routine");
             b.setContent("Nội dung chi tiết về 5 bước skincare buổi sáng...\n\nBước 1: Làm sạch nhẹ nhàng.\nBước 2: Toner cân bằng da.\nBước 3: Serum Vitamin C.\nBước 4: Kem dưỡng ẩm.\nBước 5: Kem chống nắng.");
             b.setLikeCount(450);
             b.setCommentCount(28);
             return b;
        } else if ("2".equals(id)) {
            BlogPost b = new BlogPost("2", "Top 3 kem nền kiềm dầu tốt nhất mùa hè 2025", "Bạn đang tìm kiếm lớp nền mịn lì bất chấp nắng nóng? Đừng bỏ qua danh sách này...", null, "Kanila Beauty Team", true, "3 ngày trước", "Makeup Tips");
            b.setContent("Danh sách 3 kem nền chân ái cho mùa hè này...\n1. Fenty Beauty Pro Filt'r\n2. Estée Lauder Double Wear\n3. Maybelline Fit Me");
            b.setLikeCount(890);
            b.setCommentCount(112);
            return b;
        }
        return null;
    }
}
