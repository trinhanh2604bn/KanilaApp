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

public class SkinJourneyFragment extends Fragment {

    public SkinJourneyFragment() {
        // Kết nối Fragment với fragment_skin_journey.xml
        super(R.layout.fragment_skin_journey);
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

        // Nút quay lại góc trên bên trái
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

        // Nút Lưu thay đổi
        MaterialButton btnSave = view.findViewById(R.id.btnSave);

        if (btnSave != null) {
            btnSave.setOnClickListener(v ->
                    showSaveConfirmDialog()
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
     * Hiển thị thông báo xác nhận lưu thay đổi.
     */
    private void showSaveConfirmDialog() {

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xác nhận lưu")
                .setMessage(
                        "Bạn có chắc chắn muốn lưu lại hành trình làn da này không?"
                )
                .setNegativeButton("Hủy", (dialog, which) ->
                        dialog.dismiss()
                )
                .setPositiveButton("Lưu", (dialog, which) -> {
                    dialog.dismiss();

                    // Sau khi xác nhận, hiện popup thành công
                    showSaveSuccessPopup();
                })
                .show();
    }

    /**
     * Hiển thị popup thành công từ overview_popup.xml.
     */
    private void showSaveSuccessPopup() {

        Dialog dialog = new Dialog(requireContext());

        // Bỏ tiêu đề mặc định của Dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Kết nối với overview_popup.xml
        dialog.setContentView(R.layout.overview_popup);

        // Cho phép đóng bằng nút Back
        dialog.setCancelable(true);

        // Không đóng khi nhấn ra ngoài popup
        dialog.setCanceledOnTouchOutside(false);

        // Nút Tuyệt vời trong overview_popup.xml
        MaterialButton btnPopupOk =
                dialog.findViewById(R.id.btnPopupOk);

        if (btnPopupOk != null) {
            btnPopupOk.setOnClickListener(v ->
                    dialog.dismiss()
            );
        }

        dialog.show();

        Window window = dialog.getWindow();

        if (window != null) {

            // Xóa nền trắng mặc định bên ngoài popup
            window.setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );

            // Làm tối màn hình phía sau popup
            window.addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );

            WindowManager.LayoutParams layoutParams =
                    window.getAttributes();

            layoutParams.dimAmount = 0.5f;
            window.setAttributes(layoutParams);

            // Kích thước theo overview_popup.xml
            window.setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }
}