package ui.common.states;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frontend.R;

public class EmptyStateViewHelper {

    public static void showEmptyCart(View root, View.OnClickListener listener) {
        updateEmptyState(root, 
            "Empty Cart", 0,
            "Giỏ hàng trống", 
            "Thêm sản phẩm vào giỏ hàng để tiếp tục mua sắm nhé", 
            "Mua sắm ngay", 
            R.drawable.empty_cart, 
            listener);
    }

    public static void showEmptyWishlist(View root, View.OnClickListener listener) {
        updateEmptyState(root, 
            "Empty Wishlist", R.drawable.ic_check_circle, // Placeholder for heart icon
            "Chưa có sản phẩm yêu thích", 
            "Thêm sản phẩm bạn yêu thích để xem lại dễ dàng hơn", 
            "Khám phá ngay", 
            R.drawable.empty_wishlist, 
            listener);
    }

    public static void showEmptyCommunity(View root, View.OnClickListener listener) {
        updateEmptyState(root, 
            "Empty Community Feed", 0,
            "Chưa có bài viết nào", 
            "Hãy là người đầu tiên chia sẻ bí quyết làm đẹp với Kanila", 
            "Tạo bài viết", 
            R.drawable.community_feed, 
            listener);
    }

    public static void showNoSupportTickets(View root, View.OnClickListener listener) {
        updateEmptyState(root, 
            "No Support Tickets", 0,
            "Chưa có yêu cầu hỗ trợ nào", 
            "Khi bạn yêu cầu hỗ trợ, lịch sử yêu cầu của bạn sẽ hiện ở đây", 
            "Liên hệ hỗ trợ", 
            R.drawable.support_ticket, 
            listener);
    }

    public static void showSearchNoResult(View root, View.OnClickListener listener) {
        updateEmptyState(root, 
            "", 0, // No header text in screenshot for search
            "Không tìm thấy kết quả", 
            "Thử từ khóa khác hoặc kiểm tra lại chính tả để tìm kiếm kết quả phù hợp", 
            "Thử tìm kiếm khác", 
            R.drawable.no_result, 
            listener);
    }

    private static void updateEmptyState(View root, String headerText, int headerIconResId, String title, String description, String buttonText, int imageResId, View.OnClickListener listener) {
        View layoutHeader = root.findViewById(R.id.layoutEmptyHeader);
        TextView tvHeaderText = root.findViewById(R.id.tvEmptyHeaderText);
        ImageView ivHeaderIcon = root.findViewById(R.id.ivEmptyHeaderIcon);
        
        TextView tvTitle = root.findViewById(R.id.tvEmptyTitle);
        TextView tvDescription = root.findViewById(R.id.tvEmptyDescription);
        Button btnAction = root.findViewById(R.id.btnEmptyAction);
        ImageView ivIllustration = root.findViewById(R.id.ivEmptyIllustration);

        if (layoutHeader != null) {
            layoutHeader.setVisibility(headerText == null || headerText.isEmpty() ? View.GONE : View.VISIBLE);
        }
        if (tvHeaderText != null) tvHeaderText.setText(headerText);
        if (ivHeaderIcon != null) {
            if (headerIconResId != 0) {
                ivHeaderIcon.setVisibility(View.VISIBLE);
                ivHeaderIcon.setImageResource(headerIconResId);
            } else {
                ivHeaderIcon.setVisibility(View.GONE);
            }
        }

        if (tvTitle != null) tvTitle.setText(title);
        if (tvDescription != null) tvDescription.setText(description);
        if (btnAction != null) {
            btnAction.setText(buttonText);
            btnAction.setOnClickListener(listener);
        }
        if (ivIllustration != null) ivIllustration.setImageResource(imageResId);
    }
}