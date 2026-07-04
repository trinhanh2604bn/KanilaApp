package ui.common.states;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frontend.R;

public class ErrorStateHelper {

    public static void showGenericError(View root, View.OnClickListener retryListener) {
        updateErrorState(root,
            "Đã có lỗi xảy ra",
            "Không thể tải nội dung. Vui lòng thử lại.",
            "Thử lại",
            R.drawable.ic_error_outline,
            retryListener);
    }

    public static void showServerError(View root, View.OnClickListener retryListener) {
        updateErrorState(root,
            "Lỗi máy chủ",
            "Hệ thống đang gặp sự cố tạm thời. Vui lòng quay lại sau.",
            "Thử lại",
            R.drawable.ic_error_outline,
            retryListener);
    }

    public static void showPermissionError(View root, View.OnClickListener retryListener) {
        updateErrorState(root,
            "Thiếu quyền truy cập",
            "Vui lòng cấp quyền để sử dụng tính năng này.",
            "Cài đặt",
            R.drawable.ic_error_outline,
            retryListener);
    }
    
    public static void showNoInternet(View root, View.OnClickListener retryListener) {
        TextView tvTitle = root.findViewById(R.id.tvNoInternetTitle);
        TextView tvDescription = root.findViewById(R.id.tvNoInternetDescription);
        Button btnRetry = root.findViewById(R.id.btnNoInternetRetry);
        ImageView ivIllustration = root.findViewById(R.id.ivNoInternetIllustration);

        if (tvTitle != null) tvTitle.setText("Không có kết nối mạng");
        if (tvDescription != null) tvDescription.setText("Vui lòng kiểm tra kết nối và thử lại.");
        if (btnRetry != null) {
            btnRetry.setText("Thử lại");
            btnRetry.setOnClickListener(retryListener);
        }
        if (ivIllustration != null) ivIllustration.setImageResource(R.drawable.ic_error_outline);
    }

    private static void updateErrorState(View root, String title, String description, String buttonText, int imageResId, View.OnClickListener listener) {
        TextView tvTitle = root.findViewById(R.id.tvErrorTitle);
        TextView tvDescription = root.findViewById(R.id.tvErrorDescription);
        Button btnRetry = root.findViewById(R.id.btnErrorRetry);
        ImageView ivIllustration = root.findViewById(R.id.ivErrorIllustration);

        if (tvTitle != null) tvTitle.setText(title);
        if (tvDescription != null) tvDescription.setText(description);
        if (btnRetry != null) {
            btnRetry.setText(buttonText);
            btnRetry.setOnClickListener(listener);
        }
        if (ivIllustration != null) ivIllustration.setImageResource(imageResId);
    }
}