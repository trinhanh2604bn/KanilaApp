package ui.account;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.frontend.R;
import com.example.frontend.data.model.beauty.SavedRoutineDto;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import ui.common.ViewUtils;

public class RecommendationLookFragment extends Fragment {

    private SavedRoutineDto routineData;
    private com.example.frontend.feature.beauty.BeautyProfileViewModel viewModel;

    public static RecommendationLookFragment newInstance(SavedRoutineDto routine) {
        RecommendationLookFragment fragment = new RecommendationLookFragment();
        Bundle args = new Bundle();
        args.putSerializable("routine_data", routine);
        fragment.setArguments(args);
        return fragment;
    }

    public RecommendationLookFragment() {
        // Kết nối Fragment với fragment_recommendation_look.xml
        super(R.layout.fragment_recommendation_look);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            routineData = (SavedRoutineDto) getArguments().getSerializable("routine_data");
        }
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new androidx.lifecycle.ViewModelProvider(requireActivity()).get(com.example.frontend.feature.beauty.BeautyProfileViewModel.class);

        if (routineData != null) {
            TextView tvTitle = view.findViewById(R.id.tvTitle);
            TextView tvHeroName = view.findViewById(R.id.tvRoutineHeroName);
            ImageView ivHero = view.findViewById(R.id.ivHeroImage);
            
            if (tvTitle != null) tvTitle.setText(routineData.getName());
            if (tvHeroName != null) tvHeroName.setText(routineData.getName());
            if (ivHero != null && routineData.getImageRes() != 0) {
                ivHero.setImageResource(routineData.getImageRes());
            }
        }

        setupSteps(view);
        setupEvents(view);
    }

    private void setupSteps(View view) {
        // Makeup steps (Now renamed to "Các quy trình gợi ý")
        setupMakeupStepClick(view, R.id.stepMakeup1, "1", "Kem lót", "Làm mờ lỗ chân lông", R.drawable.img_foudation);
        setupMakeupStepClick(view, R.id.stepMakeup2, "2", "Kem nền", "Che phủ tự nhiên", R.drawable.cl_product);
        setupMakeupStepClick(view, R.id.stepMakeup3, "3", "Phấn phủ", "Kiềm dầu bền màu", R.drawable.img_eyeshadow);
        setupMakeupStepClick(view, R.id.stepMakeup4, "4", "Son môi", "Màu sắc rạng rỡ", R.drawable.img_lipstick);
    }

    private void setupStepClick(View root, int id, String num, String name, String time) {
        // Not used currently after skincare section removal, but keeping for reference
    }

    private void setupMakeupStepClick(View root, int id, String num, String title, String effect, int imageRes) {
        View step = root.findViewById(id);
        if (step == null) return;

        TextView tvNum = step.findViewById(R.id.tvStepNumber);
        TextView tvTitle = step.findViewById(R.id.tvStepTitle);
        TextView tvEffect = step.findViewById(R.id.tvStepEffect);
        ImageView ivProduct = step.findViewById(R.id.ivStepProduct);

        if (tvNum != null) tvNum.setText(num);
        if (tvTitle != null) tvTitle.setText(title);
        if (tvEffect != null) tvEffect.setText(effect);
        if (ivProduct != null && imageRes != 0) {
            ivProduct.setImageResource(imageRes);
        }

        ViewUtils.applyClickAnimation(step);
        step.setOnClickListener(v -> navigateToStepProducts(title));
    }

    private void navigateToStepProducts(String stepName) {
        int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                ? R.id.main_fragment_container : R.id.main;
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(containerId, StepProductSuggestionsFragment.newInstance(stepName))
                .addToBackStack(null)
                .commit();
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
        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Lưu look trang điểm")
                .setMessage("Bạn có muốn lưu look này vào danh sách yêu thích của mình không?")
                .setNegativeButton("Hủy", (d, which) -> d.dismiss())
                .setPositiveButton("Lưu", (d, which) -> {
                    d.dismiss();
                    performSaveLook();
                })
                .show();
        ViewUtils.customizeDialogButtons(dialog);
    }

    private void performSaveLook() {
        if (routineData == null) {
            // If we opened this from "Analysis" it might not have data, create default
            routineData = new SavedRoutineDto(
                    String.valueOf(System.currentTimeMillis()),
                    "Quy trình của bạn",
                    System.currentTimeMillis(),
                    R.drawable.hinh_nen
            );
        } else {
            // Update timestamp to now when user explicitly saves
            routineData = new SavedRoutineDto(
                    routineData.getId(),
                    routineData.getName(),
                    System.currentTimeMillis(),
                    routineData.getImageRes()
            );
        }
        viewModel.saveRoutine(routineData);
        showSaveSuccessPopup();
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
