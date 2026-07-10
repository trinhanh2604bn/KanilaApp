package ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;
import ui.common.FragmentNavigationHelper;

public class FaqTopicFragment extends Fragment {

    private String topicName;
    private RecyclerView rvFaqs;
    private FaqAdapter adapter;
    private FaqViewModel viewModel;

    public static FaqTopicFragment newInstance(String topicName) {
        FaqTopicFragment fragment = new FaqTopicFragment();
        Bundle args = new Bundle();
        args.putString("topic_name", topicName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicName = getArguments().getString("topic_name");
        }
        viewModel = new ViewModelProvider(requireActivity()).get(FaqViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_faq_topic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        if (tvTitle != null && topicName != null) {
            tvTitle.setText(topicName);
        }

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        rvFaqs = view.findViewById(R.id.rvFaqs);
        rvFaqs.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FaqAdapter();
        rvFaqs.setAdapter(adapter);

        loadFaqs();
    }

    private void loadFaqs() {
        List<FaqViewModel.FaqItemData> faqs = new ArrayList<>();

        // Add user submitted questions for this specific category or for "Tất cả"
        faqs.addAll(viewModel.getQuestionsByCategory(topicName));

        if ("Đơn hàng".equals(topicName)) {
            faqs.add(new FaqViewModel.FaqItemData("Làm thế nào để theo dõi đơn hàng?", "Bạn có thể vào mục 'Đơn hàng' trong 'Trung tâm trợ giúp' hoặc 'Tài khoản' để xem trạng thái đơn hàng thời gian thực.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Bao lâu thì tôi nhận được hàng?", "Thời gian giao hàng dự kiến từ 2-5 ngày làm việc tùy thuộc vào địa chỉ của bạn. Nội thành thường chỉ mất 1-2 ngày.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Tôi có thể đổi địa chỉ nhận hàng không?", "Bạn có thể đổi địa chỉ nếu đơn hàng chưa được giao cho đơn vị vận chuyển bằng cách liên hệ Hotline hoặc Chat.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Phí vận chuyển được tính như thế nào?", "Phí vận chuyển dựa trên trọng lượng gói hàng và khoảng cách. Chúng tôi có chính sách Freeship cho đơn hàng trên 500k.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Tôi có thể hủy đơn hàng đã đặt không?", "Bạn chỉ có thể hủy đơn hàng khi trạng thái là 'Chờ xác nhận'. Nếu đã đóng gói, vui lòng từ chối nhận hàng khi Shipper gọi.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Làm sao để kiểm tra mã giảm giá đã áp dụng?", "Trong chi tiết đơn hàng, bạn sẽ thấy phần 'Khuyến mãi' liệt kê các mã đã được trừ vào tổng tiền.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Tại sao đơn hàng của tôi bị hủy?", "Đơn hàng có thể bị hủy do hết hàng đột xuất hoặc hệ thống không liên lạc được với bạn để xác nhận đơn hàng đầu tiên.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Tôi muốn đặt hỏa tốc thì làm thế nào?", "Hiện tại hỏa tốc chỉ áp dụng tại khu vực TP.HCM và Hà Nội. Bạn hãy chọn phương thức vận chuyển 'Hỏa tốc' lúc đặt hàng.", topicName));
        } else if ("Đổi trả".equals(topicName)) {
            faqs.add(new FaqViewModel.FaqItemData("Chính sách đổi trả của Kanila Beauty", "Kanila chấp nhận đổi trả trong vòng 7 ngày kể từ khi nhận hàng đối với sản phẩm còn nguyên tem mác, chưa qua sử dụng.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Sản phẩm bị hư hỏng khi nhận hàng thì sao?", "Vui lòng chụp ảnh/video khui hàng và liên hệ ngay với bộ phận hỗ trợ qua Chat để được gửi bù sản phẩm mới miễn phí.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Tôi có được hoàn tiền 100% không?", "Nếu lỗi do nhà sản xuất hoặc Kanila gửi sai mẫu, bạn sẽ được hoàn tiền 100% bao gồm cả phí ship.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Thời gian xử lý đổi trả là bao lâu?", "Kể từ khi nhận được hàng gửi về, Kanila sẽ kiểm tra và phản hồi kết quả trong vòng 3 ngày làm việc.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Tôi cần chuẩn bị giấy tờ gì khi đổi trả?", "Bạn chỉ cần cung cấp mã đơn hàng và hình ảnh sản phẩm thực tế cần đổi trả.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Có tốn phí vận chuyển khi gửi hàng đổi trả không?", "Nếu đổi trả do nhu cầu cá nhân (đổi màu, đổi ý), khách hàng chịu phí ship. Nếu do lỗi shop, Kanila sẽ chịu toàn bộ phí.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Sản phẩm quà tặng có được đổi trả không?", "Rất tiếc, các sản phẩm quà tặng kèm hoặc hàng tặng không bán sẽ không được áp dụng chính sách đổi trả.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Làm sao để biết yêu cầu đổi trả đã được nhận?", "Hệ thống sẽ gửi thông báo đẩy (Push Notification) và email ngay khi yêu cầu của bạn được tiếp nhận.", topicName));
        } else if ("Thanh toán".equals(topicName)) {
            faqs.add(new FaqViewModel.FaqItemData("Phương thức thanh toán nào được chấp nhận?", "Chúng tôi chấp nhận Thanh toán khi nhận hàng (COD), Ví điện tử (MoMo, ZaloPay, ShopeePay) và Thẻ ATM/Visa/Mastercard.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Làm sao để liên kết ví điện tử?", "Vào mục 'Tài khoản' -> 'Phương thức thanh toán' để liên kết ví mới và nhận nhiều ưu đãi độc quyền.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Thanh toán qua thẻ tín dụng có an toàn không?", "Hệ thống thanh toán của Kanila đạt chuẩn bảo mật quốc tế PCI DSS, đảm bảo thông tin thẻ của bạn luôn được mã hóa.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Tại sao giao dịch thanh toán bị thất bại?", "Giao dịch có thể thất bại do số dư không đủ, thẻ chưa kích hoạt thanh toán online hoặc lỗi kết nối từ phía ngân hàng.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Tôi có thể đổi phương thức thanh toán không?", "Rất tiếc, sau khi đặt hàng thành công bạn không thể đổi phương thức. Bạn có thể hủy đơn và đặt lại đơn mới.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Bao lâu thì nhận được tiền hoàn về ví?", "Tiền sẽ được hoàn về ví điện tử trong 24h, đối với thẻ ngân hàng có thể mất 3-7 ngày làm việc tùy ngân hàng.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Thanh toán COD có cần đặt cọc không?", "Đối với hầu hết các đơn hàng, Kanila không yêu cầu đặt cọc. Tuy nhiên đơn giá trị cao (>5tr) có thể cần xác nhận thêm.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Làm sao để lấy hóa đơn điện tử?", "Vui lòng tick vào ô 'Xuất hóa đơn VAT' ở bước thanh toán và điền thông tin công ty để nhận hóa đơn qua email.", topicName));
        } else if ("Tài khoản".equals(topicName)) {
            faqs.add(new FaqViewModel.FaqItemData("Làm thế nào để đổi mật khẩu?", "Bạn vào Cài đặt -> Bảo mật -> Đổi mật khẩu. Hãy đặt mật khẩu mạnh để bảo vệ thông tin cá nhân.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Tôi quên mật khẩu thì phải làm sao?", "Ở màn hình đăng nhập, hãy nhấn 'Quên mật khẩu' và nhập email/SĐT để nhận mã reset mật khẩu mới.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Cách cập nhật số điện thoại và email?", "Vào trang cá nhân -> Chỉnh sửa hồ sơ. Lưu ý SĐT chính chỉ có thể thay đổi sau khi xác thực OTP.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Làm sao để xóa tài khoản vĩnh viễn?", "Vào Cài đặt -> Yêu cầu xóa tài khoản. Sau khi xóa, mọi dữ liệu và điểm thưởng sẽ không thể khôi phục.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Điểm thưởng Kanila Points dùng để làm gì?", "Điểm thưởng dùng để đổi các Voucher giảm giá trực tiếp hoặc đổi quà tặng hiện vật trong kho quà tặng.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Beauty Profile giúp ích gì cho tôi?", "Beauty Profile giúp Kanila gợi ý sản phẩm phù hợp nhất với loại da, tông da và nhu cầu trang điểm của riêng bạn.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Tôi có thể đăng nhập trên nhiều thiết bị không?", "Có, bạn có thể đăng nhập đồng thời trên điện thoại và máy tính bảng.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Cách bảo mật tài khoản 2 lớp?", "Bạn có thể kích hoạt xác thực qua App hoặc OTP SĐT trong phần cài đặt bảo mật để tăng cường an toàn.", topicName));
        } else if ("Sản phẩm".equals(topicName)) {
            faqs.add(new FaqViewModel.FaqItemData("Làm sao để biết sản phẩm có hợp với da mình?", "Hãy hoàn tất Beauty Profile và sử dụng tính năng 'Skin Match' ở mỗi sản phẩm để xem điểm tương thích.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Sản phẩm của Kanila có phải chính hãng không?", "100% sản phẩm tại Kanila đều là hàng chính hãng, có đầy đủ hóa đơn chứng từ và tem phụ tiếng Việt.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Cách kiểm tra hạn sử dụng của sản phẩm?", "Hạn sử dụng được in trực tiếp trên bao bì. Ngoài ra bạn có thể chat với hỗ trợ và gửi mã Batch Code để check.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Tôi có thể nhận tư vấn trực tiếp không?", "Có, hãy nhấn vào biểu tượng Chatbot AI hoặc yêu cầu gặp chuyên viên tư vấn trực tiếp qua khung chat.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Thành phần sản phẩm có gây kích ứng không?", "Chúng tôi liệt kê đầy đủ thành phần. Nếu bạn có làn da nhạy cảm, hãy lọc sản phẩm theo tag 'Sensitive Friendly'.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Làm sao để đọc bảng thành phần?", "Bạn có thể dùng tính năng 'Phân tích thành phần' trong app để xem công dụng và mức độ an toàn của từng chất.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Sản phẩm nào đang là Best Seller?", "Hãy vào trang Danh mục và chọn bộ lọc 'Bán chạy nhất' để xem xu hướng làm đẹp hiện nay.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Có mẫu thử (Sample) cho sản phẩm mới không?", "Kanila thường xuyên tặng kèm Sample cho các đơn hàng. Bạn cũng có thể mua các set 'Trial Kits' để dùng thử.", topicName));
        } else if ("Khuyến mãi".equals(topicName)) {
            faqs.add(new FaqViewModel.FaqItemData("Làm sao để săn mã giảm giá?", "Mã thường xuất hiện tại banner trang chủ, mục 'Ví Voucher' hoặc trong các khung giờ Flash Sale mỗi ngày.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Tại sao tôi không dùng được voucher?", "Mỗi mã có điều kiện riêng về: Hạn sử dụng, Giá trị đơn hàng tối thiểu, Nhãn hàng áp dụng hoặc Phương thức thanh toán.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Chương trình Flash Sale diễn ra khi nào?", "Flash Sale diễn ra vào các khung giờ cố định: 0h - 9h - 12h - 21h mỗi ngày với ưu đãi cực sâu.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Ưu đãi cho khách hàng mới là gì?", "Bạn sẽ nhận ngay gói quà tặng chào mừng trị giá 200k và mã Freeship 0đ cho đơn hàng đầu tiên.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Cách tham gia Kanila Challenge nhận quà?", "Vào mục Community -> Challenge, thực hiện các nhiệm vụ như review sản phẩm, điểm danh để tích xu đổi quà.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Mã giảm giá có cộng dồn được không?", "Thông thường bạn có thể dùng 1 Mã giảm giá của shop + 1 Mã vận chuyển + 1 Mã từ đối tác thanh toán.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Làm sao để nhận thông báo khuyến mãi?", "Hãy bật 'Thông báo đẩy' trong cài đặt điện thoại and theo dõi Fanpage chính thức của Kanila.", topicName));
            faqs.add(new FaqViewModel.FaqItemData("Voucher có thời hạn sử dụng bao lâu?", "Thời hạn tùy vào từng đợt chiến dịch, thường từ 3-30 ngày. Hãy sử dụng ngay khi nhận được để tránh hết lượt.", topicName));
        } else if ("Tất cả câu hỏi".equals(topicName)) {
            faqs.add(new FaqViewModel.FaqItemData("Làm thế nào để theo dõi đơn hàng?", "Bạn có thể vào mục 'Đơn hàng' trong 'Trung tâm trợ giúp' hoặc 'Tài khoản' để xem trạng thái đơn hàng thời gian thực.", "Đơn hàng"));
            faqs.add(new FaqViewModel.FaqItemData("Bao lâu thì tôi nhận được hàng?", "Thời gian giao hàng dự kiến từ 2-5 ngày làm việc tùy thuộc vào địa chỉ của bạn. Nội thành thường chỉ mất 1-2 ngày.", "Đơn hàng"));
            faqs.add(new FaqViewModel.FaqItemData("Chính sách đổi trả của Kanila Beauty", "Kanila chấp nhận đổi trả trong vòng 7 ngày kể từ khi nhận hàng đối với sản phẩm còn nguyên tem mác, chưa qua sử dụng.", "Đổi trả"));
            faqs.add(new FaqViewModel.FaqItemData("Sản phẩm bị hư hỏng khi nhận hàng thì sao?", "Vui lòng chụp ảnh/video khui hàng và liên hệ ngay với bộ phận hỗ trợ qua Chat để được gửi bù sản phẩm mới miễn phí.", "Đổi trả"));
            faqs.add(new FaqViewModel.FaqItemData("Phương thức thanh toán nào được chấp nhận?", "Chúng tôi chấp nhận Thanh toán khi nhận hàng (COD), Ví điện tử (MoMo, ZaloPay, ShopeePay) and Thẻ ATM/Visa/Mastercard.", "Thanh toán"));
            faqs.add(new FaqViewModel.FaqItemData("Làm sao để liên kết ví điện tử?", "Vào mục 'Tài khoản' -> 'Phương thức thanh toán' để liên kết ví mới and nhận nhiều ưu đãi độc quyền.", "Thanh toán"));
            faqs.add(new FaqViewModel.FaqItemData("Làm thế nào để đổi mật khẩu?", "Bạn vào Cài đặt -> Bảo mật -> Đổi mật khẩu. Hãy đặt mật khẩu mạnh để bảo vệ thông tin cá nhân.", "Tài khoản"));
            faqs.add(new FaqViewModel.FaqItemData("Làm sao để săn mã giảm giá?", "Mã thường xuất hiện tại banner trang chủ, mục 'Ví Voucher' hoặc trong các khung giờ Flash Sale mỗi ngày.", "Khuyến mãi"));
        } else {
            // "Mục khác" or custom category
            for (int i = 1; i <= 3; i++) {
                faqs.add(new FaqViewModel.FaqItemData("Câu hỏi " + i + " cho " + topicName, "Đây là nội dung trả lời chi tiết cho câu hỏi thứ " + i + " thuộc chủ đề " + topicName + ".", topicName));
            }
        }
        adapter.setItems(faqs);
    }

    private class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.ViewHolder> {
        private List<FaqViewModel.FaqItemData> items = new ArrayList<>();

        public void setItems(List<FaqViewModel.FaqItemData> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Using a simple row layout now since we navigate to detail
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FaqViewModel.FaqItemData item = items.get(position);
            holder.tvQuestion.setText(item.question);

            holder.itemView.setOnClickListener(v -> FragmentNavigationHelper.replaceFragment(requireActivity(), 
                    FaqDetailFragment.newInstance(item.question, item.answer)));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvQuestion;

            ViewHolder(View view) {
                super(view);
                tvQuestion = view.findViewById(R.id.tvFaqQuestion);
            }
        }
    }
}
