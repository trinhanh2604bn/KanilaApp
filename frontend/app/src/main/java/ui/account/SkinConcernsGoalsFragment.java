package ui.account;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SkinConcernsGoalsFragment extends Fragment {

    public SkinConcernsGoalsFragment() {
        // Kết nối với fragment_skin_concerns_goals.xml
        super(R.layout.fragment_skin_concerns_goals);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupEvents(view);
    }

    private void setupViews(@NonNull View view) {
        // Ánh xạ các View khác tại đây khi cần.
    }

    private void setupEvents(@NonNull View view) {

        /*
         * Nút quay lại ở góc trên bên trái.
         */
        View btnBack = view.findViewById(R.id.btnBack);

        if (btnBack != null) {
            // Thêm hiệu ứng vòng tròn khi click
            TypedValue outValue = new TypedValue();
            requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
            btnBack.setBackgroundResource(outValue.resourceId);

            btnBack.setOnClickListener(v ->
                    handleBackNavigation()
            );
        }

        /*
         * Nút dấu tích ở góc trên bên phải.
         * Hiện tại chưa xử lý chức năng.
         */
        View btnEdit = view.findViewById(R.id.btnEdit);

        if (btnEdit != null) {
            // Thêm hiệu ứng vòng tròn cho nút edit/tick
            TypedValue outValue = new TypedValue();
            requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
            btnEdit.setBackgroundResource(outValue.resourceId);

            btnEdit.setOnClickListener(v -> {
                // Thêm chức năng lưu thay đổi tại đây khi cần.
            });
        }

        /*
         * Nút Cập nhật hồ sơ.
         *
         * Nhấn nút:
         * 1. Hiện hộp thoại xác nhận.
         * 2. Chọn Cập nhật.
         * 3. Hiện overview_popup.xml.
         */
        MaterialButton btnUpdateProfile =
                view.findViewById(R.id.btnUpdateProfile);

        if (btnUpdateProfile != null) {
            btnUpdateProfile.setOnClickListener(v ->
                    showUpdateConfirmDialog()
            );
        }

        /*
         * Nút Xem Look đề xuất.
         *
         * Nhấn nút sẽ mở RecommendationLookFragment.
         */
        MaterialButton btnViewRoutine =
                view.findViewById(R.id.btnViewRoutine);

        if (btnViewRoutine != null) {
            btnViewRoutine.setOnClickListener(v ->
                    openRecommendationLook()
            );
        }
    }

    /**
     * Quay lại màn hình trước.
     */
    private void handleBackNavigation() {

        FragmentManager fragmentManager =
                getParentFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            requireActivity()
                    .getOnBackPressedDispatcher()
                    .onBackPressed();
        }
    }

    /**
     * Hiển thị hộp thoại xác nhận cập nhật hồ sơ.
     */
    private void showUpdateConfirmDialog() {

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xác nhận cập nhật")
                .setMessage(
                        "Bạn có chắc chắn muốn cập nhật hồ sơ làm đẹp của mình không?"
                )
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Cập nhật", (dialog, which) -> {

                    dialog.dismiss();

                    // Sau khi xác nhận, hiện popup cập nhật thành công.
                    showUpdateSuccessPopup();
                })
                .show();
    }

    /**
     * Hiển thị popup thành công bằng overview_popup.xml.
     */
    private void showUpdateSuccessPopup() {

        Dialog dialog = new Dialog(requireContext());

        // Bỏ thanh tiêu đề mặc định của Dialog.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Kết nối Dialog với overview_popup.xml.
        dialog.setContentView(R.layout.overview_popup);

        // Cho phép đóng popup bằng nút Back.
        dialog.setCancelable(true);

        // Không đóng popup khi nhấn ra vùng bên ngoài.
        dialog.setCanceledOnTouchOutside(false);

        // Nút "Tuyệt vời" trong overview_popup.xml.
        MaterialButton btnPopupOk =
                dialog.findViewById(R.id.btnPopupOk);

        if (btnPopupOk != null) {
            btnPopupOk.setOnClickListener(v ->
                    dialog.dismiss()
            );
        }

        // Hiển thị popup.
        dialog.show();

        Window window = dialog.getWindow();

        if (window != null) {

            // Xóa nền trắng mặc định bên ngoài popup.
            window.setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );

            // Làm tối màn hình phía sau popup.
            window.addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );

            WindowManager.LayoutParams layoutParams =
                    window.getAttributes();

            layoutParams.dimAmount = 0.5f;
            window.setAttributes(layoutParams);

            // Sử dụng kích thước từ overview_popup.xml.
            window.setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

    /**
     * Mở màn hình Look trang điểm đề xuất.
     */
    private void openRecommendationLook() {

        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(
                        R.id.main,
                        new RecommendationLookFragment()
                )

                /*
                 * Lưu màn hình hiện tại để khi bấm Back
                 * trong RecommendationLookFragment sẽ quay lại đây.
                 */
                .addToBackStack("skin_concerns_goals")
                .commit();
    }
}