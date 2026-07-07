package ui.account;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import ui.common.ViewUtils;

public class RecommendationLookFragment extends Fragment {

    public RecommendationLookFragment() {
        // Kết nối Fragment với fragment_recommendation_look.xml
        super(R.layout.fragment_recommendation_look);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        setupEvents(view);
    }

    private void setupEvents(@NonNull View view) {

        /*
         * Nút quay lại bo tròn đồng bộ.
         */
        View btnBack = view.findViewById(R.id.btnBack);

        if (btnBack != null) {
            ViewUtils.applyClickAnimation(btnBack);
            btnBack.setOnClickListener(v ->
                    handleBackNavigation()
            );
        }

        /*
         * Nút Mua trọn bộ.
         */
        MaterialButton btnBuyAllRec = view.findViewById(R.id.btnBuyAllRec);
        if (btnBuyAllRec != null) {
            ViewUtils.applyClickAnimation(btnBuyAllRec);
        }

        /*
         * Nút Lưu look.
         */
        MaterialButton btnSaveLookRec = view.findViewById(R.id.btnSaveLookRec);
        if (btnSaveLookRec != null) {
            ViewUtils.applyClickAnimation(btnSaveLookRec);
            btnSaveLookRec.setOnClickListener(v -> showSaveConfirmDialog());
        }
    }

    /**
     * Hiển thị hộp thoại xác nhận lưu look.
     */
    private void showSaveConfirmDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Lưu look trang điểm")
                .setMessage("Bạn có muốn lưu look này vào danh sách yêu thích của mình không?")
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Lưu", (dialog, which) -> {
                    dialog.dismiss();
                    showSaveSuccessPopup();
                })
                .show();
    }

    /**
     * Hiển thị popup thành công từ overview_popup.xml.
     */
    private void showSaveSuccessPopup() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.overview_popup);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        // Thay đổi nội dung popup để hiển thị "Lưu thành công"
        TextView tvTitle = dialog.findViewById(R.id.tvPopupTitle);
        TextView tvMessage = dialog.findViewById(R.id.tvPopupMessage);
        
        if (tvTitle != null) {
            tvTitle.setText("Lưu thành công!");
        }
        if (tvMessage != null) {
            tvMessage.setText("Look trang điểm này đã được lưu vào danh sách yêu thích của bạn.");
        }

        MaterialButton btnPopupOk = dialog.findViewById(R.id.btnPopupOk);
        if (btnPopupOk != null) {
            btnPopupOk.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.dimAmount = 0.5f;
            window.setAttributes(layoutParams);
            window.setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

    /**
     * Quay lại màn hình BeautyProfileOverviewFragment.
     */
    private void handleBackNavigation() {

        FragmentManager fragmentManager =
                getParentFragmentManager();

        /*
         * Nếu có màn hình trong Back Stack,
         * quay lại màn hình trước.
         */
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {

            /*
             * Trường hợp Fragment không được mở bằng Back Stack,
             * sử dụng nút Back của Activity.
             */
            requireActivity()
                    .getOnBackPressedDispatcher()
                    .onBackPressed();
        }
    }
}