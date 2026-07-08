package ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpCenterFragment extends Fragment {

    private LinearLayout layoutTabs;
    private RecyclerView rvFaqList;
    private FaqAdapter faqAdapter;
    private final Map<String, List<FaqItem>> faqData = new HashMap<>();
    private final String[] categories = {
            "Gợi ý", "Mua sắm cùng Kanila", "Khuyến Mãi & Ưu Đãi",
            "Thanh toán", "Đơn hàng & Vận chuyển", "Trả hàng & Hoàn tiền", "Thông tin chung"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_help_center, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupHeader(view);
        setupSearchBar(view);
        setupMockData();
        setupTabs();
        setupRecyclerView();
        setupFooter(view);

        // Select first tab by default
        selectTab(0);
    }

    private void initViews(View view) {
        layoutTabs = view.findViewById(R.id.layoutHelpTabs);
        rvFaqList = view.findViewById(R.id.rvFaqList);
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutHeader);
        header.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        
        TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) {
            tvTitle.setText("Trung tâm trợ giúp Kanila");
            tvTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        }
        
        ImageView btnBack = header.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
            btnBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
        }
        
        View btnSearch = header.findViewById(R.id.btnTopBarSearch);
        if (btnSearch != null) btnSearch.setVisibility(View.GONE);
    }

    private void setupSearchBar(View view) {
        View search = view.findViewById(R.id.layoutHelpSearch);
        if (search == null) return;

        TextView hint = search.findViewById(R.id.tvSearchHint);
        if (hint != null) hint.setText("Hỏi trợ lý AI");

        ImageView icon = search.findViewById(R.id.ivSearchIcon);
        if (icon != null) icon.setImageResource(R.drawable.ic_chatbot);

        View camera = search.findViewById(R.id.btnExpandedSearchCamera);
        if (camera != null) camera.setVisibility(View.GONE);
    }

    private void setupTabs() {
        layoutTabs.removeAllViews();
        for (int i = 0; i < categories.length; i++) {
            final int index = i;
            View tabView = getLayoutInflater().inflate(R.layout.item_help_tab, layoutTabs, false);
            TextView tvTab = tabView.findViewById(R.id.tvTabText);
            tvTab.setText(categories[i]);
            
            tabView.setOnClickListener(v -> selectTab(index));
            layoutTabs.addView(tabView);
        }
    }

    private void selectTab(int index) {
        for (int i = 0; i < layoutTabs.getChildCount(); i++) {
            View child = layoutTabs.getChildAt(i);
            TextView tv = child.findViewById(R.id.tvTabText);
            if (i == index) {
                tv.setBackgroundResource(R.drawable.bg_help_tab_selected);
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.button));
            } else {
                tv.setBackgroundResource(R.drawable.bg_help_tab_default);
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
            }
        }
        
        String category = categories[index];
        faqAdapter.setItems(faqData.get(category));
    }

    private void setupRecyclerView() {
        faqAdapter = new FaqAdapter();
        rvFaqList.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFaqList.setAdapter(faqAdapter);
    }

    private void setupFooter(View view) {
        View menuCSKH = view.findViewById(R.id.menuCSKH);
        ((TextView) menuCSKH.findViewById(R.id.tvMenuTitle)).setText("Chăm sóc khách hàng");
        ((ImageView) menuCSKH.findViewById(R.id.ivMenuIcon)).setImageResource(R.drawable.ic_support);

        View menuCall = view.findViewById(R.id.menuCall);
        ((TextView) menuCall.findViewById(R.id.tvMenuTitle)).setText("Gọi tổng đài Kanila");
        ((ImageView) menuCall.findViewById(R.id.ivMenuIcon)).setImageResource(R.drawable.ic_zalo);
    }

    private void setupMockData() {
        // Gợi ý
        List<FaqItem> items = new ArrayList<>();
        items.add(new FaqItem("[Lỗi] Tại sao tôi không dùng được Voucher/Mã giảm giá?", "Có thể mã đã hết hạn hoặc không đủ điều kiện áp dụng cho đơn hàng của bạn. Hãy kiểm tra điều kiện áp dụng ở mục Ví Voucher."));
        items.add(new FaqItem("[Lỗi] Tại sao tài khoản Kanila của tôi bị khóa?", "Hệ thống có thể tạm khóa tài khoản nếu phát hiện hành vi vi phạm điều khoản. Vui lòng liên hệ CSKH để được kiểm tra."));
        items.add(new FaqItem("[Trả hàng] Cách đóng gói đơn hàng hoàn trả", "Vui lòng đóng gói sản phẩm trong hộp gốc và bọc kỹ bằng xốp chống sốc để tránh hư hỏng trong quá trình vận chuyển."));
        faqData.put(categories[0], items);

        // Mua sắm cùng Kanila
        items = new ArrayList<>();
        items.add(new FaqItem("Làm sao để mua hàng trên ứng dụng Kanila?", "Bạn chỉ cần chọn sản phẩm, chọn phân loại (màu sắc/dung tích), nhấn 'Mua ngay' hoặc thêm vào giỏ hàng và tiến hành đặt hàng."));
        items.add(new FaqItem("Tính năng Thích sản phẩm là gì?", "Bạn có thể nhấn biểu tượng tim để lưu sản phẩm vào danh sách yêu thích để dễ dàng tìm lại sau này."));
        items.add(new FaqItem("Cách thay đổi địa chỉ nhận hàng?", "Trong mục Tài khoản > Sổ địa chỉ, bạn có thể thêm mới hoặc chỉnh sửa các địa chỉ đã lưu."));
        faqData.put(categories[1], items);

        // Fill other categories with similar mock data
        for(int i = 2; i < categories.length; i++) {
            List<FaqItem> otherItems = new ArrayList<>();
            otherItems.add(new FaqItem("Câu hỏi liên quan đến " + categories[i], "Đây là câu trả lời chi tiết cho mục " + categories[i] + ". Chúng tôi luôn sẵn sàng hỗ trợ bạn."));
            otherItems.add(new FaqItem("Quy định về " + categories[i], "Bạn có thể xem chi tiết quy định này trong mục Điều khoản sử dụng của Kanila VN."));
            otherItems.add(new FaqItem("Hướng dẫn xử lý lỗi " + categories[i], "Vui lòng thử tải lại trang hoặc liên hệ nhân viên hỗ trợ trực tuyến để được trợ giúp kịp thời."));
            faqData.put(categories[i], otherItems);
        }
    }

    static class FaqItem {
        String question;
        String answer;
        boolean isExpanded = false;

        FaqItem(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }

    static class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {
        private List<FaqItem> items = new ArrayList<>();

        void setItems(List<FaqItem> newItems) {
            this.items = newItems != null ? newItems : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq, parent, false);
            return new FaqViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
            FaqItem item = items.get(position);
            holder.tvQuestion.setText(item.question);
            holder.tvAnswer.setText(item.answer);
            holder.tvAnswer.setVisibility(item.isExpanded ? View.VISIBLE : View.GONE);
            holder.ivArrow.setRotation(item.isExpanded ? 180 : 0);

            holder.itemView.setOnClickListener(v -> {
                item.isExpanded = !item.isExpanded;
                notifyItemChanged(position);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class FaqViewHolder extends RecyclerView.ViewHolder {
            TextView tvQuestion, tvAnswer;
            ImageView ivArrow;

            FaqViewHolder(@NonNull View itemView) {
                super(itemView);
                tvQuestion = itemView.findViewById(R.id.tvQuestion);
                tvAnswer = itemView.findViewById(R.id.faq_answer);
                ivArrow = itemView.findViewById(R.id.faq_arrow);
            }
        }
    }
}
