package ui.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.R;
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
        data.setValue(getAllBlogs());
        return data;
    }

    public BlogPost getBlogById(String id) {
        for (BlogPost b : getAllBlogs()) {
            if (b.getId().equals(id)) return b;
        }
        return null;
    }

    private List<BlogPost> getAllBlogs() {
        List<BlogPost> blogs = new ArrayList<>();

        BlogPost b1 = new BlogPost("1", "Cách trang điểm phù hợp với mùa hè", "Khám phá các bước makeup nhẹ nhàng, bền màu và giữ da luôn tươi tắn trong những ngày nắng nóng.", R.drawable.img_blog1, "Kanila Official", true, "2 ngày trước", "Makeup Tips");
        b1.setLikeCount(1200);
        b1.setCommentCount(126);
        b1.setShareCount(89);
        b1.setContent("Mùa hè oi bức thường khiến lớp trang điểm dễ bị trôi và bết dính. Để giữ được vẻ ngoài tươi tắn suốt cả ngày, bạn cần tuân thủ quy trình trang điểm mỏng nhẹ. \n\nBước 1: Luôn bắt đầu với kem chống nắng.\nBước 2: Sử dụng kem lót kiềm dầu.\nBước 3: Chọn cushion hoặc kem nền mỏng nhẹ.\nBước 4: Phấn phủ bột để khóa lớp nền.\n\nHãy cùng Kanila khám phá chi tiết hơn trong bài viết này nhé!");
        blogs.add(b1);

        BlogPost b2 = new BlogPost("2", "Các hãng sản phẩm mới ra mắt đang nổi bật", "Tổng hợp những sản phẩm makeup mới nhất được cộng đồng yêu thích trong tháng này.", R.drawable.img_blog2, "Kanila Official", true, "5 ngày trước", "Product Update");
        b2.setLikeCount(968);
        b2.setCommentCount(102);
        b2.setShareCount(72);
        b2.setContent("Tháng này chứng kiến sự bùng nổ của các dòng son kem lì và phấn mắt tông cam đào. Nhiều thương hiệu lớn đã cho ra mắt những bộ sưu tập giới hạn đầy ấn tượng.\n\nĐiểm danh các cái tên hot nhất:\n- Romand Bare Water Cushion\n- 3CE New Take Face Blusher\n- Black Rouge Mud Glow... \n\nCùng Kanila điểm qua chi tiết các bảng màu đang làm mưa làm gió hiện nay!");
        blogs.add(b2);

        BlogPost b3 = new BlogPost("3", "Quy trình makeup cho người mới bắt đầu", "Hướng dẫn chi tiết từng bước giúp bạn dễ dàng tự tin với lớp makeup đầu tiên của mình.", R.drawable.img_blog3, "Kanila Beauty Team", true, "1 tuần trước", "Beginner Guide");
        b3.setLikeCount(1400);
        b3.setCommentCount(178);
        b3.setShareCount(94);
        b3.setContent("Nếu bạn mới tập tành trang điểm, đừng quá lo lắng về việc phải mua thật nhiều mỹ phẩm. Chỉ cần những món cơ bản nhất là bạn đã có thể thay đổi diện mạo rồi.\n\nCác bước tối giản cho người mới:\n1. Làm sạch và dưỡng ẩm.\n2. Kem nền hoặc BB Cream.\n3. Chì kẻ mày.\n4. Chuốt mascara.\n5. Son môi.\n\nHọc cách chọn tone màu phù hợp với làn da của bạn ngay tại đây.");
        blogs.add(b3);

        BlogPost b4 = new BlogPost("4", "Makeup nền mỏng nhẹ mỗi ngày", "Quy trình base makeup tự nhiên, mịn đẹp và không bí da.", R.drawable.img_blog4, "Kanila Beauty Team", true, "2 tuần trước", "Base Makeup");
        b4.setLikeCount(1100);
        b4.setCommentCount(95);
        b4.setShareCount(63);
        b4.setContent("Xu hướng trang điểm 'Clean Girl' hay 'No Makeup Makeup Look' vẫn chưa bao giờ hạ nhiệt. Bí quyết nằm ở lớp nền thật trong và tiệp hẳn vào da.\n\nLưu ý quan trọng:\n- Dưỡng da thật kỹ trước khi đánh nền.\n- Sử dụng mút trang điểm ẩm.\n- Chấm nền từng điểm nhỏ trên mặt.\n\nKết quả sẽ là một làn da căng bóng như sương, cực kỳ phù hợp để đi học hoặc đi làm hàng ngày.");
        blogs.add(b4);

        return blogs;
    }
}
