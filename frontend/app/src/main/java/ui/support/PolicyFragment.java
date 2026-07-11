package ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;
import ui.common.FragmentNavigationHelper;

public class PolicyFragment extends Fragment {

    private RecyclerView rvPolicies;
    private PolicyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_policies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        rvPolicies = view.findViewById(R.id.rvPolicies);
        rvPolicies.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PolicyAdapter();
        rvPolicies.setAdapter(adapter);

        loadPolicies();
    }

    private void loadPolicies() {
        List<PolicyItem> policies = new ArrayList<>();
        
        // --- CHÍNH SÁCH ---
        policies.add(new PolicyItem("HEADER", "CÁC CHÍNH SÁCH", 0));
        policies.add(new PolicyItem("Chính sách bảo mật", "Cam kết bảo vệ thông tin cá nhân khách hàng...", R.drawable.ic_shortcut_policy));
        policies.add(new PolicyItem("Chính sách đổi trả", "Quy trình đổi trả linh hoạt trong 7 ngày...", R.drawable.ic_shortcut_order));
        policies.add(new PolicyItem("Chính sách vận chuyển", "Thông tin về phí ship và thời gian giao nhận...", R.drawable.ic_shortcut_order));
        policies.add(new PolicyItem("Chính sách thanh toán", "Các phương thức thanh toán an toàn và bảo mật...", R.drawable.ic_shortcut_voucher));
        
        // --- ĐIỀU KHOẢN ---
        policies.add(new PolicyItem("HEADER", "ĐIỀU KHOẢN & QUY ĐỊNH", 0));
        policies.add(new PolicyItem("Điều khoản dịch vụ", "Quy định chung khi sử dụng ứng dụng Kanila...", R.drawable.ic_list));
        policies.add(new PolicyItem("Quy tắc cộng đồng", "Tiêu chuẩn hành vi tại Kanila Community...", R.drawable.ic_community));
        policies.add(new PolicyItem("Điều khoản Membership", "Quy định về tích điểm và nâng hạng thành viên...", R.drawable.ic_shortcut_royalty));
        
        adapter.setItems(policies);
    }

    private class PolicyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;
        private List<PolicyItem> items = new ArrayList<>();

        public void setItems(List<PolicyItem> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return "HEADER".equals(items.get(position).title) ? TYPE_HEADER : TYPE_ITEM;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                return new HeaderViewHolder(v);
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_policy, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            PolicyItem item = items.get(position);
            if (holder instanceof HeaderViewHolder) {
                HeaderViewHolder hvh = (HeaderViewHolder) holder;
                hvh.tvHeader.setText(item.preview);
                hvh.tvHeader.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary));
                hvh.tvHeader.setTextSize(13);
                hvh.tvHeader.setPadding(0, position == 0 ? 16 : 48, 0, 16);
            } else if (holder instanceof ViewHolder) {
                ViewHolder vh = (ViewHolder) holder;
                vh.tvTitle.setText(item.title);
                vh.ivIcon.setImageResource(item.iconRes);
                vh.itemView.setOnClickListener(v -> FragmentNavigationHelper.replaceFragment(requireActivity(), 
                        PolicyDetailFragment.newInstance(item.title, getFullPolicyContent(item.title))));
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder {
            TextView tvHeader;
            HeaderViewHolder(View view) {
                super(view);
                tvHeader = view.findViewById(android.R.id.text1);
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            ImageView ivIcon;
            ViewHolder(View view) {
                super(view);
                tvTitle = view.findViewById(R.id.tvPolicyTitle);
                ivIcon = view.findViewById(R.id.ivPolicyIcon);
            }
        }
    }

    private String getFullPolicyContent(String title) {
        if (title.contains("bảo mật")) {
            return "CHÍNH SÁCH BẢO MẬT KANILA BEAUTY\n\n" +
                   "Chào mừng bạn đến với Kanila. Chúng tôi coi trọng quyền riêng tư của bạn và cam kết bảo vệ dữ liệu cá nhân của bạn.\n\n" +
                   "1. THU THẬP THÔNG TIN\n" +
                   "Chúng tôi thu thập các thông tin sau khi bạn đăng ký tài khoản:\n" +
                   "• Họ tên, Số điện thoại, Email và Địa chỉ giao hàng.\n" +
                   "• Thông tin về làn da thông qua Beauty Profile (Loại da, tình trạng da...).\n" +
                   "• Lịch sử mua hàng và các sản phẩm yêu thích.\n\n" +
                   "2. MỤC ĐÍCH SỬ DỤNG\n" +
                   "Thông tin của bạn được sử dụng để:\n" +
                   "• Xử lý và giao các đơn hàng bạn đã đặt.\n" +
                   "• Gợi ý sản phẩm cá nhân hóa dựa trên hồ sơ làn da.\n" +
                   "• Gửi thông báo về các chương trình khuyến mãi và ưu đãi độc quyền.\n" +
                   "• Cải thiện chất lượng dịch vụ và trải nghiệm người dùng trên ứng dụng.\n\n" +
                   "3. CHIA SẺ THÔNG TIN\n" +
                   "Kanila cam kết không bán hoặc cho thuê thông tin cá nhân của bạn cho bên thứ ba. Chúng tôi chỉ chia sẻ dữ liệu với:\n" +
                   "• Các đơn vị vận chuyển (GHTK, GHN, Viettel Post...) để giao hàng.\n" +
                   "• Các đối tác thanh toán bảo mật (VNPay, MoMo...) để hoàn tất giao dịch.\n\n" +
                   "4. BẢO MẬT DỮ LIỆU\n" +
                   "Toàn bộ dữ liệu được mã hóa bằng giao thức SSL và lưu trữ tại hệ thống máy chủ bảo mật cao, tuân thủ tiêu chuẩn quốc tế PCI DSS.\n\n" +
                   "5. QUYỀN CỦA NGƯỜI DÙNG\n" +
                   "Bạn có quyền truy cập, chỉnh sửa thông tin cá nhân hoặc yêu cầu xóa tài khoản vĩnh viễn bất kỳ lúc nào thông qua phần Cài đặt trong ứng dụng.";
        } else if (title.contains("dịch vụ")) {
            return "ĐIỀU KHOẢN DỊCH VỤ CHUNG\n\n" +
                   "Chào mừng bạn đến với ứng dụng Kanila Beauty. Bằng việc cài đặt và sử dụng ứng dụng, bạn xác nhận đã đọc, hiểu và đồng ý với các điều khoản sau đây:\n\n" +
                   "1. QUY ĐỊNH VỀ TÀI KHOẢN\n" +
                   "Người dùng phải từ 13 tuổi trở lên để sử dụng đầy đủ các tính năng. Bạn chịu trách nhiệm về tính chính xác của thông tin cung cấp và bảo mật mật khẩu tài khoản của mình.\n\n" +
                   "2. QUYỀN SỞ HỮU TRÍ TUỆ\n" +
                   "Tất cả nội dung trên ứng dụng bao gồm thiết kế, đồ họa, văn bản, logo, hình ảnh, mã nguồn và phần mềm đều thuộc sở hữu của Kanila Beauty. Nghiêm cấm mọi hành vi sao chép, sửa đổi hoặc sử dụng lại mà không có sự đồng ý chính thức.\n\n" +
                   "3. HÀNH VI NGHIÊM CẤM\n" +
                   "• Sử dụng ứng dụng vào các mục đích phi pháp hoặc gây hại cho người khác.\n" +
                   "• Can thiệp trái phép vào hệ thống, cơ sở dữ liệu hoặc làm gián đoạn dịch vụ của Kanila.\n" +
                   "• Giả mạo danh tính hoặc cung cấp thông tin lừa đảo.\n\n" +
                   "4. THAY ĐỔI DỊCH VỤ\n" +
                   "Kanila bảo lưu quyền sửa đổi, tạm ngưng hoặc chấm dứt bất kỳ phần nào của dịch vụ vào bất kỳ lúc nào mà không cần thông báo trước.\n\n" +
                   "5. GIỚI HẠN TRÁCH NHIỆM\n" +
                   "Trong mọi trường hợp, Kanila không chịu trách nhiệm cho bất kỳ thiệt hại trực tiếp hoặc gián tiếp nào phát sinh từ việc sử dụng hoặc không thể sử dụng ứng dụng.";
        } else if (title.contains("Quy tắc cộng đồng")) {
            return "QUY TẮC CỘNG ĐỒNG KANILA (COMMUNITY GUIDELINES)\n\n" +
                   "Kanila Community là không gian văn minh để chia sẻ kinh nghiệm làm đẹp. Để duy trì môi trường tích cực, chúng tôi yêu cầu các thành viên tuân thủ các quy tắc sau:\n\n" +
                   "1. TÔN TRỌNG VÀ LỊCH SỰ\n" +
                   "Hãy luôn tôn trọng quan điểm của người khác. Tuyệt đối không sử dụng ngôn từ công kích, quấy rối, xúc phạm cá nhân hoặc phân biệt đối xử.\n\n" +
                   "2. NỘI DUNG CHÂN THỰC\n" +
                   "Khuyến khích các bài đánh giá, hình ảnh và video thực tế. Không đăng tải nội dung giả mạo, gây hiểu lầm về chất lượng sản phẩm hoặc quảng cáo sai sự thật.\n\n" +
                   "3. KHÔNG SPAM VÀ QUẢNG CÁO RÁC\n" +
                   "Không đăng các bình luận rác, chèo kéo bán hàng ngoài hệ thống Kanila hoặc chia sẻ các đường dẫn độc hại.\n\n" +
                   "4. BẢO VỆ SỞ HỮU TRÍ TUỆ\n" +
                   "Chỉ đăng tải những hình ảnh/nội dung mà bạn có quyền sở hữu. Ghi rõ nguồn nếu sử dụng nội dung tham khảo.\n\n" +
                   "5. CƠ CHẾ BÁO CÁO (REPORT)\n" +
                   "Người dùng được khuyến khích sử dụng tính năng 'Báo cáo' đối với các bài viết vi phạm. Đội ngũ quản trị của Kanila sẽ xử lý và có quyền xóa bài hoặc khóa tài khoản vi phạm mà không cần thông báo.";
        } else if (title.contains("đổi trả")) {
            return "CHÍNH SÁCH ĐỔI TRẢ & HOÀN TIỀN\n\n" +
                   "Kanila luôn mong muốn mang đến sự hài lòng tuyệt đối cho khách hàng với chính sách đổi trả linh hoạt.\n\n" +
                   "1. THỜI GIAN ĐỔI TRẢ\n" +
                   "Khách hàng có 07 ngày kể từ ngày nhận hàng thành công để gửi yêu cầu đổi trả.\n\n" +
                   "2. ĐIỀU KIỆN ĐỔI TRẢ\n" +
                   "Sản phẩm được chấp nhận đổi trả khi đáp ứng đủ các điều kiện:\n" +
                   "• Còn nguyên tem mác, màng co và chưa qua sử dụng.\n" +
                   "• Bao bì không bị móp méo, hư hỏng do lỗi bảo quản của khách hàng.\n" +
                   "• Có video quay cảnh khui hàng (unboxing) để làm bằng chứng.\n\n" +
                   "3. CÁC TRƯỜNG HỢP ĐƯỢC ĐỔI TRẢ MIỄN PHÍ\n" +
                   "• Sản phẩm bị hư hỏng, vỡ nát trong quá trình vận chuyển.\n" +
                   "• Giao sai sản phẩm, sai dung tích hoặc sai phân loại màu sắc.\n" +
                   "• Sản phẩm có lỗi từ nhà sản xuất (vòi xịt hỏng, chất kem biến màu...).\n\n" +
                   "4. QUY TRÌNH HOÀN TIỀN\n" +
                   "Sau khi nhận được hàng gửi về và kiểm tra đạt yêu cầu, Kanila sẽ hoàn tiền qua:\n" +
                   "• Ví điện tử Kanila: Hoàn tiền ngay trong vòng 24h.\n" +
                   "• Tài khoản ngân hàng: Từ 3 - 7 ngày làm việc tùy ngân hàng.";
        } else if (title.contains("vận chuyển")) {
            return "CHÍNH SÁCH VẬN CHUYỂN & GIAO NHẬN\n\n" +
                   "Kanila hợp tác cùng các đơn vị vận chuyển uy tín để đảm bảo hàng hóa đến tay khách hàng nhanh chóng và an toàn.\n\n" +
                   "1. PHÍ VẬN CHUYỂN\n" +
                   "• Miễn phí vận chuyển toàn quốc cho đơn hàng từ 500.000đ trở lên.\n" +
                   "• Đơn hàng dưới 500.000đ: Phí ship đồng giá 25.000đ (Nội thành) và 35.000đ (Liên tỉnh).\n\n" +
                   "2. THỜI GIAN GIAO HÀNG\n" +
                   "• Khu vực TP.HCM & Hà Nội: 1 - 2 ngày làm việc.\n" +
                   "• Khu vực tỉnh thành khác: 3 - 5 ngày làm việc.\n" +
                   "• Giao hàng hỏa tốc: Áp dụng trong vòng 2h tại TP.HCM (tùy khu vực).\n\n" +
                   "3. KIỂM TRA HÀNG HÓA\n" +
                   "Kanila khuyến khích khách hàng đồng kiểm (kiểm tra ngoại quan) cùng nhân viên giao hàng. Trường hợp không được đồng kiểm, quý khách vui lòng quay video khui hàng để được hỗ trợ khiếu nại nếu có sự cố.\n\n" +
                   "4. THEO DÕI ĐƠN HÀNG\n" +
                   "Bạn có thể tra cứu trạng thái đơn hàng thời gian thực trong mục 'Đơn hàng của tôi' trên ứng dụng.";
        } else if (title.contains("Membership")) {
            return "ĐIỀU KHOẢN CHƯƠNG TRÌNH KANILA REWARDS\n\n" +
                   "Chương trình Kanila Rewards nhằm tri ân khách hàng thân thiết. Bằng cách tham gia, bạn đồng ý với các điều khoản sau:\n\n" +
                   "1. ĐIỀU KIỆN THAM GIA\n" +
                   "Chương trình áp dụng cho tất cả khách hàng có tài khoản hợp lệ trên ứng dụng Kanila.\n\n" +
                   "2. TÍCH LŨY ĐIỂM (KANILA POINTS)\n" +
                   "• Điểm được tích lũy dựa trên giá trị đơn hàng thực tế sau khi trừ khuyến mãi.\n" +
                   "• Tỷ lệ quy đổi: 10.000đ chi tiêu = 100 điểm.\n\n" +
                   "3. PHÂN HẠNG THÀNH VIÊN\n" +
                   "Hạng thành viên (Đồng, Bạc, Vàng, Kim Cương) được xét dựa trên tổng chi tiêu trong vòng 6 tháng gần nhất.\n\n" +
                   "4. QUY ĐỊNH SỬ DỤNG VOUCHER\n" +
                   "Voucher đổi từ điểm không có giá trị quy đổi thành tiền mặt và có thời hạn sử dụng theo từng đợt chiến dịch.";
        }
        return "Nội dung chi tiết của " + title + " đang được cập nhật. Vui lòng liên hệ bộ phận CSKH để biết thêm thông tin.";
    }

    private static class PolicyItem {
        String title;
        String preview;
        int iconRes;

        PolicyItem(String title, String preview, int iconRes) {
            this.title = title;
            this.preview = preview;
            this.iconRes = iconRes;
        }
    }
}
