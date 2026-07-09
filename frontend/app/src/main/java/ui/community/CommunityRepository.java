package ui.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;

public class CommunityRepository {
    private static CommunityRepository instance;

    private CommunityRepository() {}

    public static synchronized CommunityRepository getInstance() {
        if (instance == null) {
            instance = new CommunityRepository();
        }
        return instance;
    }

    public LiveData<List<Post>> getFeedPosts() {
        MutableLiveData<List<Post>> data = new MutableLiveData<>();
        List<Post> posts = new ArrayList<>();

        List<String> images1 = new ArrayList<>();
        images1.add("https://theordinary.com/dw/image/v2/BFNS_PRD/on/demandware.static/-/Sites-deciem-master/default/dw11b1f09c/Images/products/The%20Ordinary/rdn-niacinamide-10pct-zinc-1pct-30ml.png");
        posts.add(new Post("1", "Kim Trần", null, "2 giờ trước", "Serum Niacinamide 10% – sáng da, mờ thâm thấy rõ!", "Sau 3 tuần sử dụng The Ordinary Niacinamide 10% + Zinc 1% da mình sáng và cũng không nhờn rít.", images1, 1200, 137, 36, true, true));

        List<String> images2 = new ArrayList<>();
        images2.add("https://m.media-amazon.com/images/I/61y8B2s7SLL._SL1500_.jpg");
        posts.add(new Post("2", "Linh Nguyễn", null, "5 giờ trước", "Kem chống nắng for da dầu mụn", "Mình đã thử rất nhiều loại nhưng chân ái vẫn là La Roche-Posay Anthelios. Kiềm dầu cực tốt luôn.", images2, 850, 42, 12, false, true));

        List<String> images3 = new ArrayList<>();
        images3.add("https://thebodyshop.com.vn/media/catalog/product/t/e/tea_tree_oil_10ml_1.jpg");
        posts.add(new Post("3", "Mai Anh", null, "1 ngày trước", "Tea Tree Oil - Cứu tinh cho nốt mụn sưng", "Mỗi khi có mụn sưng đỏ, mình chấm ngay tinh dầu tràm trà này. Mụn xẹp nhanh kinh khủng luôn mọi người ơi.", images3, 560, 28, 5, true, false));

        List<String> images4 = new ArrayList<>();
        images4.add("https://cocoonvietnam.com/storage/products/gel-rua-mat-bi-dao-140ml-1.png");
        posts.add(new Post("4", "Hoàng Nam", null, "3 ngày trước", "Review Gel rửa mặt bí đao Cocoon", "Sản phẩm thuần chay Việt Nam cực ổn. Rửa xong da không bị khô căng, cảm giác rất sạch và dịu nhẹ.", images4, 320, 15, 2, false, true));

        List<String> images5 = new ArrayList<>();
        images5.add("https://vn-live-01.slatic.net/p/3e0986790a6e0984855734208f23788a.jpg");
        posts.add(new Post("5", "Thanh Hằng", null, "1 tuần trước", "Mẹo đánh nền không bị mốc (cakey)", "Bí quyết của mình là trộn một giọt dầu dưỡng vào kem nền. Lớp nền sẽ bóng khỏe và bám cực lâu cả ngày.", images5, 2100, 245, 89, true, true));

        data.setValue(posts);
        return data;
    }
}
