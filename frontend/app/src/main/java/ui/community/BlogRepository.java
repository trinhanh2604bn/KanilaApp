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
//        loadFromStorage();
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

    public void addComment(String blogId, Comment comment) {
        for (BlogPost b : inMemoryBlogs) {
            if (b.getId().equals(blogId)) {
                b.getComments().add(comment);
                b.setCommentCount(b.getComments().size());
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
        b1.setContent("Cách trang điểm phù hợp với mùa hè\n" +
                "Bước 1: Chuẩn bị làn da thật kỹ trước khi trang điểm\n" +
                "\n" +
                "Vào mùa hè, nhiệt độ cao khiến da tiết nhiều dầu và mồ hôi hơn bình thường, vì vậy việc chuẩn bị da đúng cách sẽ quyết định độ bền của lớp trang điểm. Trước tiên, hãy làm sạch da bằng sữa rửa mặt dịu nhẹ để loại bỏ bụi bẩn và bã nhờn tích tụ. Sau đó, sử dụng toner để cân bằng độ pH và giúp da hấp thụ dưỡng chất tốt hơn. Tiếp theo, thoa một lớp kem dưỡng ẩm có kết cấu mỏng nhẹ, ưu tiên dạng gel hoặc lotion để cấp nước mà không gây bí da. Khi làn da đủ ẩm, lớp nền sẽ bám tốt hơn, hạn chế tình trạng bong tróc hoặc xuống tông trong thời tiết nóng.\n" +
                "\n" +
                "Bước 2: Không bỏ qua kem chống nắng\n" +
                "\n" +
                "Kem chống nắng là sản phẩm không thể thiếu khi trang điểm vào mùa hè. Tia UV kết hợp với nhiệt độ cao có thể khiến da nhanh lão hóa, xuất hiện nám, tàn nhang và mất độ đàn hồi. Hãy lựa chọn kem chống nắng có chỉ số SPF từ 30 đến 50, PA+++ hoặc PA++++ và có khả năng chống nước nếu thường xuyên hoạt động ngoài trời. Sau khi thoa kem chống nắng, nên chờ khoảng 5–10 phút để sản phẩm ổn định trên da trước khi bắt đầu các bước trang điểm tiếp theo.\n" +
                "\n" +
                "Bước 3: Sử dụng kem lót kiềm dầu\n" +
                "\n" +
                "Do tuyến bã nhờn hoạt động mạnh vào mùa hè, kem lót kiềm dầu sẽ giúp hạn chế tình trạng bóng nhờn và kéo dài thời gian bền màu của lớp nền. Nên tập trung thoa kem lót ở vùng chữ T, hai bên cánh mũi và cằm – những khu vực thường tiết nhiều dầu nhất. Nếu sở hữu làn da khô hoặc da hỗn hợp thiên khô, bạn có thể lựa chọn kem lót cấp ẩm để vừa giữ lớp nền mịn màng vừa hạn chế hiện tượng mốc nền.\n" +
                "\n" +
                "Bước 4: Lựa chọn lớp nền mỏng nhẹ\n" +
                "\n" +
                "Trang điểm mùa hè nên ưu tiên sự tự nhiên thay vì sử dụng lớp nền quá dày. Các sản phẩm như cushion, BB Cream, CC Cream hoặc kem nền có kết cấu mỏng nhẹ sẽ giúp da thông thoáng hơn nhưng vẫn đủ khả năng che phủ các khuyết điểm cơ bản. Khi đánh nền, hãy sử dụng mút trang điểm đã làm ẩm để dặm từng lớp mỏng thay vì thoa quá nhiều sản phẩm cùng lúc. Cách này giúp lớp nền bám chắc hơn, hạn chế tình trạng chảy nền khi gặp mồ hôi.\n" +
                "\n" +
                "Bước 5: Che khuyết điểm vừa đủ\n" +
                "\n" +
                "Sau khi hoàn thiện lớp nền, chỉ nên sử dụng kem che khuyết điểm ở những vị trí thật sự cần thiết như quầng thâm mắt, vết mụn hoặc vùng da không đều màu. Không nên phủ kín toàn bộ khuôn mặt bằng kem che khuyết điểm vì điều này sẽ khiến lớp trang điểm trở nên dày và dễ bị cakey trong điều kiện thời tiết nóng. Hãy dùng mút hoặc đầu ngón tay dặm nhẹ để kem hòa vào lớp nền một cách tự nhiên.\n" +
                "\n" +
                "Bước 6: Phủ phấn để cố định lớp nền\n" +
                "\n" +
                "Phấn phủ là trợ thủ đắc lực giúp kiểm soát dầu và giữ cho lớp trang điểm lâu trôi trong mùa hè. Nên sử dụng phấn phủ dạng bột với kết cấu mịn để tạo hiệu ứng nhẹ mặt và thông thoáng hơn so với các loại phấn nén dày. Chỉ cần phủ một lớp mỏng lên toàn bộ khuôn mặt, tập trung nhiều hơn ở vùng chữ T và cánh mũi để hạn chế bóng dầu trong suốt cả ngày.\n" +
                "\n" +
                "Bước 7: Ưu tiên tông màu mắt nhẹ nhàng\n" +
                "\n" +
                "Mùa hè rất phù hợp với những gam màu mắt tươi sáng và tự nhiên như cam đào, hồng nhạt, be, nude hoặc nâu sáng. Những màu sắc này không chỉ tạo cảm giác trẻ trung mà còn giúp khuôn mặt trở nên tươi tắn dưới ánh nắng. Khi trang điểm mắt, chỉ cần nhấn nhẹ phần đuôi mắt để tạo chiều sâu, tránh sử dụng quá nhiều màu hoặc nhũ đậm vì dễ khiến tổng thể trở nên nặng nề.\n" +
                "\n" +
                "Bước 8: Kẻ eyeliner và chuốt mascara chống nước\n" +
                "\n" +
                "Mồ hôi và độ ẩm cao có thể khiến eyeliner và mascara bị lem nếu sử dụng sản phẩm thông thường. Vì vậy, nên ưu tiên các dòng chống nước (waterproof) để giữ đôi mắt luôn sắc nét. Chỉ cần kẻ một đường eyeliner mảnh sát chân mi và chuốt một lớp mascara vừa đủ để tạo hiệu ứng mắt to hơn mà vẫn tự nhiên. Cuối ngày, nhớ sử dụng sản phẩm tẩy trang chuyên dụng dành cho mỹ phẩm chống nước để làm sạch hoàn toàn.\n" +
                "\n" +
                "Bước 9: Chọn má hồng dạng kem hoặc tint\n" +
                "\n" +
                "Các sản phẩm má hồng dạng kem hoặc dạng tint thường bám tốt hơn trên da vào mùa hè so với má hồng dạng phấn. Những gam màu như hồng đào, cam san hô hoặc hồng tự nhiên sẽ mang lại vẻ ngoài rạng rỡ, khỏe khoắn. Khi đánh má, hãy tán nhẹ từ gò má hướng lên thái dương để tạo hiệu ứng khuôn mặt thon gọn và trẻ trung.\n" +
                "\n" +
                "Bước 10: Lựa chọn son môi lâu trôi\n" +
                "\n" +
                "Đối với thời tiết nóng, son tint hoặc son lì có độ bám cao sẽ là lựa chọn phù hợp vì ít bị trôi khi ăn uống hoặc đổ mồ hôi. Những tông màu như đỏ cam, cam đất, hồng đất hoặc đỏ gạch đều rất phù hợp với không khí mùa hè và dễ phối với nhiều phong cách trang điểm khác nhau. Trước khi thoa son, nên dưỡng môi bằng một lớp son dưỡng mỏng để hạn chế tình trạng khô và bong tróc.\n" +
                "\n" +
                "Bước 11: Xịt khóa lớp trang điểm\n" +
                "\n" +
                "Sau khi hoàn thành các bước trang điểm, hãy sử dụng xịt khóa makeup để tăng khả năng bám của mỹ phẩm và giúp lớp nền trông tự nhiên hơn. Giữ chai xịt cách mặt khoảng 20–30 cm rồi xịt đều theo hình chữ X và chữ T. Đợi sản phẩm khô hoàn toàn trước khi ra ngoài để lớp trang điểm được cố định tốt nhất.");
        inMemoryBlogs.add(b1);

        BlogPost b2 = new BlogPost("2", "Các hãng sản phẩm mới ra mắt đang nổi bật", "Tổng hợp những sản phẩm makeup mới nhất được cộng đồng yêu thích trong tháng này.", R.drawable.img_blog2, "Kanila Official", true, "5 ngày trước", "Product Update");
        b2.setLikeCount(968); b2.setCommentCount(102); b2.setShareCount(72);
        b2.setContent("1. Charlotte Tilbury\n" +
                "\n" +
                "Charlotte Tilbury tiếp tục là một trong những thương hiệu cao cấp dẫn đầu xu hướng với nhiều sản phẩm mới tập trung vào hiệu ứng da căng bóng tự nhiên. Nổi bật nhất là Glazed Goddess Unreal Highlighter và Exagger-Eyes Eyeshadow Sticks, được sử dụng trong nhiều layout trang điểm của người nổi tiếng như Zendaya. Các sản phẩm này hướng đến lớp nền glowy, dễ sử dụng và phù hợp với xu hướng makeup mùa hè.\n" +
                "\n" +
                "2. MAC Cosmetics\n" +
                "\n" +
                "MAC vẫn duy trì sức hút bằng các bộ sưu tập hợp tác cùng influencer và nghệ sĩ nổi tiếng. Gần đây, thương hiệu giới thiệu MAC x Quen Blackwell Lip Kit, bao gồm chì kẻ môi, son lì và son bóng, hướng đến phong cách môi nude hiện đại đang rất được yêu thích trên TikTok.\n" +
                "\n" +
                "3. Marc Jacobs Beauty\n" +
                "\n" +
                "Sau thời gian vắng bóng, Marc Jacobs Beauty đã chính thức trở lại với bộ sưu tập makeup hoàn toàn mới. Thương hiệu tập trung vào thiết kế hiện đại, bảng màu nổi bật cùng các sản phẩm dành cho môi, mắt và nền, nhanh chóng thu hút sự quan tâm của giới làm đẹp.\n" +
                "\n" +
                "4. Prada Beauty\n" +
                "\n" +
                "Prada Beauty đang ngày càng phổ biến nhờ các dòng son môi, phấn mắt và kem nền cao cấp. Các sản phẩm của hãng thường xuyên xuất hiện trong những layout makeup của người nổi tiếng và các tuần lễ thời trang quốc tế, đặc biệt là phong cách trang điểm tự nhiên nhưng sang trọng.\n" +
                "\n" +
                "5. Kosas\n" +
                "\n" +
                "Kosas tiếp tục mở rộng dòng mỹ phẩm kết hợp chăm sóc da với các sản phẩm nền mỏng nhẹ, che phủ tự nhiên và chứa nhiều thành phần dưỡng da. Đây là thương hiệu được đánh giá cao trong xu hướng \"skin-first makeup\", rất phù hợp với người yêu thích phong cách trang điểm tối giản.\n" +
                "\n" +
                "6. Ogee\n" +
                "\n" +
                "Ogee gây chú ý với dòng Crystal Contour Collection, một bộ sản phẩm dạng thỏi đa năng có thể sử dụng làm kem nền, tạo khối và che khuyết điểm. Công thức chứa tỷ lệ lớn thành phần dưỡng da, phù hợp với xu hướng \"makeup kết hợp skincare\".\n" +
                "\n" +
                "7. Violette_FR\n" +
                "\n" +
                "Thương hiệu của chuyên gia trang điểm người Pháp Violette Serrat vừa ra mắt dòng tinted lip balm sau nhiều năm phát triển. Bộ sưu tập hướng đến phong cách trang điểm Pháp với màu sắc tự nhiên, dưỡng môi tốt và tạo hiệu ứng môi mềm mại.\n" +
                "\n" +
                "8. Real Techniques\n" +
                "\n" +
                "Mặc dù nổi tiếng với cọ và mút trang điểm hơn là mỹ phẩm, Real Techniques vừa hợp tác với chuyên gia makeup Kelli Anne Sewell để ra mắt bộ cọ trang điểm chuyên nghiệp có mức giá dễ tiếp cận, được nhiều beauty editor đánh giá cao.\n" +
                "\n" +
                "Những xu hướng makeup nổi bật gắn liền với các sản phẩm mới\n" +
                "\n" +
                "Bên cạnh các sản phẩm mới, nhiều thương hiệu cũng đang tập trung phát triển theo các xu hướng nổi bật của năm 2026 như:\n" +
                "\n" +
                "Lớp nền mỏng nhẹ, căng bóng tự nhiên (Skin Tint, Serum Foundation).\n" +
                "Má hồng dạng kem và dạng tint cho hiệu ứng trong trẻo.\n" +
                "Son dưỡng có màu (Tinted Lip Balm) thay thế son lì truyền thống.\n" +
                "Highlight hiệu ứng \"glass skin\" và \"glazed skin\".\n" +
                "Phấn mắt dạng thỏi giúp trang điểm nhanh.\n" +
                "Mỹ phẩm tích hợp thành phần chăm sóc da như Hyaluronic Acid, Niacinamide và Squalane");
        inMemoryBlogs.add(b2);

        BlogPost b3 = new BlogPost("3", "Quy trình makeup cho người mới bắt đầu", "Hướng dẫn chi tiết từng bước giúp bạn dễ dàng tự tin với lớp makeup đầu tiên của mình.", R.drawable.img_blog3, "Kanila Beauty Team", true, "1 tuần trước", "Beginner Guide");
        b3.setLikeCount(1400); b3.setCommentCount(178); b3.setShareCount(94);
        b3.setContent("Đối với người mới bắt đầu, makeup không cần quá cầu kỳ mà quan trọng nhất là thực hiện đúng quy trình và lựa chọn sản phẩm phù hợp với làn da. Một lớp trang điểm đẹp luôn bắt đầu từ nền da được chăm sóc kỹ lưỡng, sau đó lần lượt hoàn thiện qua các bước đánh nền, che khuyết điểm, phủ phấn, trang điểm mắt, má, môi và cuối cùng là khóa lớp makeup. Khi luyện tập thường xuyên và hiểu rõ đặc điểm khuôn mặt của mình, bạn sẽ dần cải thiện kỹ năng trang điểm, tạo nên những lớp makeup tự nhiên, hài hòa và phù hợp với nhiều hoàn cảnh khác nhau." +
                "Bước 1: Làm sạch da\n" +
                "\n" +
                "Trước khi bắt đầu trang điểm, việc làm sạch da là bước quan trọng giúp loại bỏ bụi bẩn, dầu thừa và cặn mỹ phẩm còn sót lại trên bề mặt da. Một làn da sạch sẽ giúp các sản phẩm trang điểm bám tốt hơn, hạn chế tình trạng lớp nền bị mốc, loang lổ hoặc nhanh xuống tông. Bạn nên sử dụng sữa rửa mặt phù hợp với loại da của mình, massage nhẹ nhàng khoảng 30–60 giây rồi rửa sạch bằng nước. Sau đó dùng khăn mềm thấm khô da thay vì chà xát mạnh để tránh gây tổn thương và kích ứng.\n" +
                "\n" +
                "Bước 2: Dưỡng ẩm\n" +
                "\n" +
                "Sau khi làm sạch, làn da cần được cấp ẩm đầy đủ để tạo bề mặt mềm mịn trước khi trang điểm. Kem dưỡng ẩm không chỉ giúp hạn chế tình trạng bong tróc mà còn giúp lớp nền tiệp vào da tự nhiên và bền màu hơn. Lấy một lượng kem vừa đủ, thoa đều lên toàn bộ khuôn mặt, tập trung vào những vùng da khô rồi massage nhẹ nhàng để dưỡng chất thẩm thấu. Nên chờ khoảng 3–5 phút trước khi chuyển sang bước tiếp theo để kem dưỡng phát huy hiệu quả.\n" +
                "\n" +
                "Bước 3: Thoa kem chống nắng\n" +
                "\n" +
                "Nếu trang điểm vào ban ngày, kem chống nắng là bước không thể bỏ qua. Sản phẩm này giúp bảo vệ da khỏi tác hại của tia UV, hạn chế tình trạng sạm nám, lão hóa và giảm nguy cơ tổn thương da do ánh nắng mặt trời. Nên ưu tiên các loại kem chống nắng có chỉ số SPF từ 30 trở lên, PA+++ hoặc cao hơn và có kết cấu mỏng nhẹ để không làm ảnh hưởng đến lớp nền. Sau khi thoa kem chống nắng, hãy đợi khoảng 5 phút trước khi tiếp tục các bước trang điểm.\n" +
                "\n" +
                "Bước 4: Thoa kem lót\n" +
                "\n" +
                "Kem lót đóng vai trò như một lớp đệm giữa da và lớp nền, giúp làm mịn bề mặt da, che bớt lỗ chân lông, kiểm soát dầu và tăng khả năng bám của kem nền. Chỉ cần lấy một lượng nhỏ bằng hạt đậu, tập trung thoa vào vùng chữ T, hai bên cánh mũi và những khu vực có lỗ chân lông to. Không nên sử dụng quá nhiều vì có thể khiến lớp nền bị dày và kém tự nhiên.\n" +
                "\n" +
                "Bước 5: Đánh kem nền\n" +
                "\n" +
                "Kem nền là bước quyết định đến vẻ ngoài của toàn bộ lớp trang điểm. Người mới nên lựa chọn màu kem nền gần với màu da thật hoặc sáng hơn khoảng nửa tông để tạo cảm giác tự nhiên. Chấm kem nền lên trán, mũi, cằm và hai má, sau đó dùng mút trang điểm đã làm ẩm hoặc cọ để tán đều theo hướng từ trong ra ngoài. Khi tán nền cần thao tác nhẹ nhàng bằng cách dặm thay vì miết để lớp nền bám chắc hơn. Đồng thời, đừng quên tán xuống vùng cổ và quai hàm để tránh tình trạng khuôn mặt và cổ bị lệch màu.\n" +
                "\n" +
                "Bước 6: Che khuyết điểm\n" +
                "\n" +
                "Sau khi hoàn thành lớp nền, hãy sử dụng kem che khuyết điểm để xử lý những vùng da chưa đều màu như quầng thâm mắt, vết mụn, thâm nám hoặc các vùng đỏ trên khuôn mặt. Chấm một lượng nhỏ kem lên vị trí cần che rồi dùng mút hoặc đầu ngón tay dặm nhẹ để sản phẩm hòa vào lớp nền. Không nên sử dụng quá nhiều kem che khuyết điểm vì có thể khiến lớp trang điểm trở nên dày và dễ bị đọng vào các nếp gấp trên da.\n" +
                "\n" +
                "Bước 7: Phủ phấn\n" +
                "\n" +
                "Phấn phủ giúp cố định lớp nền, giảm độ bóng dầu và tăng khả năng giữ lớp trang điểm trong nhiều giờ. Dùng cọ lớn hoặc bông phấn lấy một lượng phấn vừa phải rồi phủ nhẹ lên toàn bộ khuôn mặt, đặc biệt là vùng trán, mũi và cằm – những nơi thường tiết nhiều dầu. Đối với da khô, chỉ nên phủ một lớp mỏng để tránh làm da bị khô và mất đi độ tự nhiên.\n" +
                "\n" +
                "Bước 8: Kẻ chân mày\n" +
                "\n" +
                "Chân mày là yếu tố góp phần định hình gương mặt và tạo sự cân đối cho tổng thể lớp trang điểm. Người mới nên lựa chọn màu chì hoặc bột kẻ mày gần với màu tóc để tạo cảm giác hài hòa. Bắt đầu bằng việc xác định khuôn chân mày, sau đó vẽ khung nhẹ nhàng và tô đầy phần còn thiếu bằng những nét ngắn mô phỏng sợi lông mày. Cuối cùng, dùng chổi xoắn chải đều để màu sắc trở nên tự nhiên hơn.\n" +
                "\n" +
                "Bước 9: Trang điểm mắt\n" +
                "\n" +
                "Đối với người mới bắt đầu, nên lựa chọn những gam màu mắt nhẹ nhàng như be, nude, hồng nhạt hoặc nâu đất để dễ phối và phù hợp với nhiều hoàn cảnh. Trước tiên, đánh màu sáng lên toàn bộ bầu mắt, sau đó sử dụng màu đậm hơn ở phần đuôi mắt để tạo chiều sâu. Tiếp theo, kẻ một đường eyeliner mảnh sát chân mi nhằm giúp đôi mắt trông to và sắc nét hơn. Cuối cùng, bấm mi và chuốt mascara từ gốc đến ngọn theo chuyển động zíc zắc để hàng mi cong và dày tự nhiên.\n" +
                "\n" +
                "Bước 10: Đánh má hồng\n" +
                "\n" +
                "Má hồng giúp gương mặt trở nên tươi tắn và có sức sống hơn. Người mới nên lựa chọn các tông màu như hồng đào, cam đào hoặc hồng nude vì dễ sử dụng và phù hợp với nhiều màu da. Khi đánh má, hãy mỉm cười để xác định phần gò má, sau đó dùng cọ tán nhẹ theo hướng từ gò má lên thái dương để tạo hiệu ứng nâng cơ mặt và giúp khuôn mặt trông hài hòa hơn.\n" +
                "\n" +
                "Bước 11: Tạo khối và bắt sáng\n" +
                "\n" +
                "Mặc dù không bắt buộc đối với người mới, tạo khối và bắt sáng sẽ giúp khuôn mặt có chiều sâu và sắc nét hơn. Sử dụng sản phẩm tạo khối ở hai bên sống mũi, dưới xương gò má, viền hàm và trán để tạo hiệu ứng khuôn mặt thon gọn. Sau đó, dùng highlight ở những vị trí bắt sáng như sống mũi, đỉnh gò má, nhân trung và cằm để làn da trông căng bóng và rạng rỡ hơn. Điều quan trọng là phải tán đều để các đường chuyển màu mềm mại và tự nhiên.\n" +
                "\n" +
                "Bước 12: Đánh son môi\n" +
                "\n" +
                "Son môi là bước cuối cùng giúp hoàn thiện diện mạo. Trước khi thoa son, nên dưỡng môi hoặc tẩy tế bào chết định kỳ để môi luôn mềm mịn. Người mới có thể lựa chọn các tông màu dễ sử dụng như hồng đất, cam đất, đỏ gạch hoặc đỏ nâu. Có thể đánh lòng môi để tạo vẻ trẻ trung hoặc đánh full môi nếu muốn gương mặt trở nên nổi bật và sắc sảo hơn.\n" +
                "\n" +
                "Bước 13: Xịt khóa lớp trang điểm\n" +
                "\n" +
                "Sau khi hoàn thành toàn bộ các bước makeup, hãy sử dụng xịt khóa trang điểm để cố định lớp mỹ phẩm trên da. Giữ chai xịt cách mặt khoảng 20–30 cm rồi xịt đều theo hình chữ X và chữ T. Để sản phẩm khô tự nhiên mà không dùng tay chạm lên mặt. Bước này sẽ giúp lớp trang điểm bền màu hơn, hạn chế tình trạng xuống tông, đồng thời mang lại hiệu ứng căng bóng và tự nhiên cho làn da.");
        inMemoryBlogs.add(b3);

        BlogPost b4 = new BlogPost("4", "Makeup nền mỏng nhẹ mỗi ngày", "Quy trình base makeup tự nhiên, mịn đẹp và không bí da.", R.drawable.img_blog4, "Kanila Beauty Team", true, "2 tuần trước", "Base Makeup");
        b4.setLikeCount(1100); b4.setCommentCount(95); b4.setShareCount(63);
        b4.setContent("Bước 1: Chăm sóc da trước khi trang điểm\n" +
                "\n" +
                "Để có một lớp nền mỏng nhẹ và tự nhiên, việc chăm sóc da trước khi makeup là yếu tố quan trọng nhất. Hãy bắt đầu bằng cách làm sạch da với sữa rửa mặt dịu nhẹ để loại bỏ bụi bẩn và dầu thừa. Sau đó, sử dụng toner để cân bằng độ ẩm và thoa một lớp kem dưỡng mỏng nhẹ phù hợp với loại da. Làn da đủ ẩm sẽ giúp lớp nền tiệp vào da tốt hơn, hạn chế tình trạng mốc nền hoặc bong tróc trong suốt cả ngày. Nếu trang điểm vào ban ngày, đừng quên sử dụng kem chống nắng có chỉ số SPF từ 30 trở lên để bảo vệ da trước tác hại của tia UV.\n" +
                "\n" +
                "Bước 2: Sử dụng kem lót phù hợp\n" +
                "\n" +
                "Kem lót giúp tạo bề mặt da mịn màng và tăng độ bền cho lớp trang điểm. Với phong cách makeup hằng ngày, chỉ cần sử dụng một lượng nhỏ kem lót ở những vùng dễ tiết dầu hoặc có lỗ chân lông to như vùng chữ T, hai bên cánh mũi và cằm. Nếu da khô, hãy ưu tiên kem lót cấp ẩm; nếu da dầu, nên lựa chọn kem lót có khả năng kiềm dầu để lớp nền luôn khô ráo và tự nhiên.\n" +
                "\n" +
                "Bước 3: Lựa chọn lớp nền mỏng nhẹ\n" +
                "\n" +
                "Thay vì sử dụng kem nền có độ che phủ cao, hãy ưu tiên các sản phẩm như cushion, BB Cream, CC Cream hoặc kem nền dạng serum có kết cấu mỏng nhẹ. Những sản phẩm này vừa giúp làm đều màu da vừa tạo cảm giác tự nhiên như làn da thật. Chấm một lượng nhỏ lên trán, mũi, hai má và cằm, sau đó dùng mút trang điểm đã làm ẩm hoặc cọ để dặm nhẹ từ trong ra ngoài. Nếu cần che phủ nhiều hơn, hãy dặm thêm từng lớp mỏng thay vì đánh một lớp dày ngay từ đầu.\n" +
                "\n" +
                "Bước 4: Che khuyết điểm ở những vùng cần thiết\n" +
                "\n" +
                "Một lớp nền tự nhiên không có nghĩa là phải che phủ toàn bộ khuôn mặt. Hãy sử dụng kem che khuyết điểm chỉ ở những vị trí như quầng thâm mắt, vết mụn hoặc vùng da không đều màu. Dùng đầu ngón tay hoặc mút trang điểm dặm nhẹ để sản phẩm hòa vào lớp nền. Cách này giúp gương mặt vẫn giữ được vẻ tự nhiên nhưng tổng thể trông đều màu và tươi tắn hơn.\n" +
                "\n" +
                "Bước 5: Phủ phấn mỏng để cố định lớp nền\n" +
                "\n" +
                "Sau khi hoàn thiện lớp nền, phủ một lớp phấn thật mỏng để giúp lớp makeup bền hơn và hạn chế bóng dầu. Chỉ cần tập trung ở vùng chữ T hoặc những khu vực dễ tiết dầu, không nên phủ quá nhiều vì sẽ làm mất đi hiệu ứng căng bóng tự nhiên của làn da. Với những người có làn da khô, có thể chỉ phủ phấn ở vùng mũi và trán để giữ được độ ẩm cần thiết.\n" +
                "\n" +
                "Bước 6: Tô điểm nhẹ cho chân mày và đôi mắt\n" +
                "\n" +
                "Đối với phong cách trang điểm hằng ngày, chân mày chỉ cần được định hình gọn gàng bằng chì hoặc bột kẻ mày có màu gần với màu tóc. Sau đó, sử dụng một màu phấn mắt trung tính như be, nâu nhạt hoặc hồng đào để tạo chiều sâu nhẹ nhàng cho đôi mắt. Kẻ một đường eyeliner thật mảnh sát chân mi và chuốt một lớp mascara để hàng mi cong tự nhiên, giúp đôi mắt trông tươi tắn mà không quá sắc sảo.\n" +
                "\n" +
                "Bước 7: Đánh má hồng tự nhiên\n" +
                "\n" +
                "Má hồng là bước giúp gương mặt trở nên rạng rỡ hơn. Nên lựa chọn các tông màu nhẹ như hồng đào, cam đào hoặc hồng nude và ưu tiên má hồng dạng kem hoặc dạng tint để tạo hiệu ứng trong trẻo. Tán nhẹ từ phần gò má hướng lên thái dương sẽ giúp khuôn mặt trông tươi trẻ và có sức sống hơn.\n" +
                "\n" +
                "Bước 8: Thoa son môi tông tự nhiên\n" +
                "\n" +
                "Để hoàn thiện phong cách makeup nền mỏng nhẹ, hãy lựa chọn những màu son dịu như hồng đất, cam đất, đỏ đất hoặc hồng nude. Son tint, son dưỡng có màu hoặc son kem lì mỏng nhẹ đều là những lựa chọn phù hợp. Có thể đánh lòng môi rồi tán nhẹ ra viền hoặc đánh full môi với một lớp son mỏng để tạo cảm giác tự nhiên và tươi tắn.\n" +
                "\n" +
                "Bước 9: Khóa lớp trang điểm\n" +
                "\n" +
                "Cuối cùng, sử dụng xịt khóa makeup để giúp lớp trang điểm bền màu hơn trong suốt cả ngày. Giữ chai xịt cách mặt khoảng 20–30 cm rồi xịt đều theo hình chữ X và chữ T. Bước này không chỉ giúp cố định lớp makeup mà còn làm lớp nền trông mềm mại, tự nhiên hơn.");
        inMemoryBlogs.add(b4);

        blogsData.setValue(new ArrayList<>(inMemoryBlogs));
        saveToStorage();
    }
}
