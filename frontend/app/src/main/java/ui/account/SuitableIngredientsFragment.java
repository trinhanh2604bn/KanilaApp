package ui.account;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class SuitableIngredientsFragment extends Fragment {

    private final List<IngredientItem> priorityItems = new ArrayList<>();
    private final List<IngredientItem> avoidItems = new ArrayList<>();

    private TextView tvPriorityCount;
    private TextView tvPriorityList;
    private TextView tvAvoidCount;
    private TextView tvAvoidList;

    private MaterialCardView layoutSensitiveTreatment;
    private SwitchMaterial switchSensitiveTreatment;

    public SuitableIngredientsFragment() {
        super(R.layout.fragment_suitable_ingredients);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupEvents(view);
        updateAllIngredientStates();
        updateSensitiveTreatmentState(
                switchSensitiveTreatment != null
                        && switchSensitiveTreatment.isChecked()
        );
    }

    private void setupViews(@NonNull View view) {

        // Xóa dữ liệu cũ nếu View của Fragment được tạo lại.
        priorityItems.clear();
        avoidItems.clear();

        // Khu vực thống kê phía trên.
        tvPriorityCount = view.findViewById(R.id.tvPriorityCount);
        tvPriorityList = view.findViewById(R.id.tvPriorityList);
        tvAvoidCount = view.findViewById(R.id.tvAvoidCount);
        tvAvoidList = view.findViewById(R.id.tvAvoidList);

        // Khu vực công tắc da nhạy cảm.
        layoutSensitiveTreatment =
                view.findViewById(R.id.layoutSensitiveTreatment);

        switchSensitiveTreatment =
                view.findViewById(R.id.switchSensitiveTreatment);

        /*
         * NHÓM THÀNH PHẦN ƯU TIÊN
         * Ba thành phần đầu được chọn mặc định.
         */

        priorityItems.add(createIngredientItem(
                view,
                R.id.cardNiacinamide,
                R.id.iconNiacinamide,
                R.id.tvNiacinamide,
                R.id.tickNiacinamide,
                "Niacinamide",
                true
        ));

        priorityItems.add(createIngredientItem(
                view,
                R.id.cardCeramide,
                R.id.iconCeramide,
                R.id.tvCeramide,
                R.id.tickCeramide,
                "Ceramide",
                true
        ));

        priorityItems.add(createIngredientItem(
                view,
                R.id.cardHyaluronicAcid,
                R.id.iconHyaluronicAcid,
                R.id.tvHyaluronicAcid,
                R.id.tickHyaluronicAcid,
                "Hyaluronic Acid",
                true
        ));

        priorityItems.add(createIngredientItem(
                view,
                R.id.cardBha,
                R.id.iconBha,
                R.id.tvBha,
                R.id.tickBha,
                "BHA",
                false
        ));

        priorityItems.add(createIngredientItem(
                view,
                R.id.cardVitaminC,
                R.id.iconVitaminC,
                R.id.tvVitaminC,
                R.id.tickVitaminC,
                "Vitamin C",
                false
        ));

        priorityItems.add(createIngredientItem(
                view,
                R.id.cardPeptide,
                R.id.iconPeptide,
                R.id.tvPeptide,
                R.id.tickPeptide,
                "Peptide",
                false
        ));

        /*
         * NHÓM THÀNH PHẦN CẦN TRÁNH
         * Hai thành phần đầu được chọn mặc định.
         */

        avoidItems.add(createIngredientItem(
                view,
                R.id.cardAlcohol,
                R.id.iconAlcohol,
                R.id.tvAlcohol,
                R.id.tickAlcohol,
                "Cồn khô",
                true
        ));

        avoidItems.add(createIngredientItem(
                view,
                R.id.cardFragrance,
                R.id.iconFragrance,
                R.id.tvFragrance,
                R.id.tickFragrance,
                "Hương liệu",
                true
        ));

        avoidItems.add(createIngredientItem(
                view,
                R.id.cardParaben,
                R.id.iconParaben,
                R.id.tvParaben,
                R.id.tickParaben,
                "Paraben",
                false
        ));

        avoidItems.add(createIngredientItem(
                view,
                R.id.cardRetinol,
                R.id.iconRetinol,
                R.id.tvRetinol,
                R.id.tickRetinol,
                "Retinol",
                false
        ));

        avoidItems.add(createIngredientItem(
                view,
                R.id.cardEssentialOil,
                R.id.iconEssentialOil,
                R.id.tvEssentialOil,
                R.id.tickEssentialOil,
                "Tinh dầu",
                false
        ));
    }

    private IngredientItem createIngredientItem(
            @NonNull View root,
            int cardId,
            int iconId,
            int labelId,
            int tickId,
            @NonNull String name,
            boolean selected
    ) {

        MaterialCardView card = root.findViewById(cardId);
        ImageView icon = root.findViewById(iconId);
        TextView label = root.findViewById(labelId);
        ImageView tick = root.findViewById(tickId);

        return new IngredientItem(
                card,
                icon,
                label,
                tick,
                name,
                selected
        );
    }

    private void setupEvents(@NonNull View view) {

        // Nút quay lại.
        View btnBack = view.findViewById(R.id.btnBack);

        if (btnBack != null) {
            applyPressedEffect(btnBack);

            btnBack.setOnClickListener(v ->
                    handleBackNavigation()
            );
        }

        // Nút dấu tích trên Toolbar.
        View btnEdit = view.findViewById(R.id.btnEdit);

        if (btnEdit != null) {
            applyPressedEffect(btnEdit);

            btnEdit.setOnClickListener(v ->
                    showSaveConfirmDialog()
            );
        }

        // Nút lưu thiết lập phía dưới.
        MaterialButton btnSave = view.findViewById(R.id.btnSave);

        if (btnSave != null) {
            applyPressedEffect(btnSave);

            btnSave.setOnClickListener(v ->
                    showSaveConfirmDialog()
            );
        }

        // Bắt sự kiện chọn thành phần.
        setupIngredientClickEvents(priorityItems);
        setupIngredientClickEvents(avoidItems);

        /*
         * Bật hoặc tắt khi người dùng nhấn trực tiếp vào Switch.
         */
        if (switchSensitiveTreatment != null) {

            applyPressedEffect(switchSensitiveTreatment);

            switchSensitiveTreatment.setOnCheckedChangeListener(
                    (buttonView, isChecked) ->
                            updateSensitiveTreatmentState(isChecked)
            );
        }

        /*
         * Nhấn vào toàn bộ khung cũng bật hoặc tắt Switch.
         */
        if (layoutSensitiveTreatment != null
                && switchSensitiveTreatment != null) {

            applyPressedEffect(layoutSensitiveTreatment);

            layoutSensitiveTreatment.setOnClickListener(v ->
                    switchSensitiveTreatment.toggle()
            );
        }
    }

    private void setupIngredientClickEvents(
            @NonNull List<IngredientItem> items
    ) {

        for (IngredientItem item : items) {

            if (item.card == null) {
                continue;
            }

            applyPressedEffect(item.card);

            item.card.setOnClickListener(v -> {

                item.selected = !item.selected;

                updateIngredientView(item);
                updateSummary();
            });
        }
    }

    /**
     * Cập nhật trạng thái ban đầu của tất cả thành phần.
     */
    private void updateAllIngredientStates() {

        for (IngredientItem item : priorityItems) {
            updateIngredientView(item);
        }

        for (IngredientItem item : avoidItems) {
            updateIngredientView(item);
        }

        updateSummary();
    }

    /**
     * Thay đổi giao diện của một thành phần.
     */
    private void updateIngredientView(
            @NonNull IngredientItem item
    ) {

        if (item.card == null
                || item.icon == null
                || item.label == null
                || item.tick == null) {
            return;
        }

        int pinkColor = ContextCompat.getColor(
                requireContext(),
                R.color.button
        );

        int normalTextColor = ContextCompat.getColor(
                requireContext(),
                R.color.text_main
        );

        int selectedBackgroundColor = ContextCompat.getColor(
                requireContext(),
                R.color.icon_bg_pink
        );

        int normalBackgroundColor = ContextCompat.getColor(
                requireContext(),
                R.color.background_main
        );

        int normalStrokeColor = ContextCompat.getColor(
                requireContext(),
                R.color.border_divider
        );

        if (item.selected) {

            // Trạng thái được chọn.
            item.card.setStrokeColor(pinkColor);
            item.card.setStrokeWidth(dpToPx(2));
            item.card.setCardBackgroundColor(selectedBackgroundColor);

            item.label.setTextColor(pinkColor);

            ImageViewCompat.setImageTintList(
                    item.icon,
                    ColorStateList.valueOf(pinkColor)
            );

            item.tick.setVisibility(View.VISIBLE);

        } else {

            // Trạng thái chưa chọn.
            item.card.setStrokeColor(normalStrokeColor);
            item.card.setStrokeWidth(dpToPx(1));
            item.card.setCardBackgroundColor(normalBackgroundColor);

            item.label.setTextColor(normalTextColor);

            ImageViewCompat.setImageTintList(
                    item.icon,
                    ColorStateList.valueOf(normalTextColor)
            );

            item.tick.setVisibility(View.GONE);
        }

        item.card.setContentDescription(
                item.name
                        + (item.selected
                        ? " đã được chọn"
                        : " chưa được chọn")
        );
    }

    /**
     * Cập nhật số lượng và danh sách trong Thiết lập nhanh.
     */
    private void updateSummary() {

        int priorityCount = countSelected(priorityItems);
        int avoidCount = countSelected(avoidItems);

        if (tvPriorityCount != null) {
            tvPriorityCount.setText(
                    priorityCount + " thành phần"
            );
        }

        if (tvPriorityList != null) {
            tvPriorityList.setText(
                    getSelectedNames(priorityItems)
            );
        }

        if (tvAvoidCount != null) {
            tvAvoidCount.setText(
                    avoidCount + " thành phần"
            );
        }

        if (tvAvoidList != null) {
            tvAvoidList.setText(
                    getSelectedNames(avoidItems)
            );
        }
    }

    private int countSelected(
            @NonNull List<IngredientItem> items
    ) {

        int count = 0;

        for (IngredientItem item : items) {
            if (item.selected) {
                count++;
            }
        }

        return count;
    }

    @NonNull
    private String getSelectedNames(
            @NonNull List<IngredientItem> items
    ) {

        StringBuilder result = new StringBuilder();

        for (IngredientItem item : items) {

            if (!item.selected) {
                continue;
            }

            if (result.length() > 0) {
                result.append(", ");
            }

            result.append(item.name);
        }

        if (result.length() == 0) {
            return "Chưa chọn";
        }

        return result.toString();
    }

    /**
     * Switch ON: màu hồng.
     * Switch OFF: màu đỏ.
     */
    private void updateSensitiveTreatmentState(boolean isChecked) {

        if (layoutSensitiveTreatment == null
                || switchSensitiveTreatment == null) {
            return;
        }

        int pinkColor = ContextCompat.getColor(
                requireContext(),
                R.color.button
        );

        int redColor = ContextCompat.getColor(
                requireContext(),
                R.color.error
        );

        int pinkBackground = ContextCompat.getColor(
                requireContext(),
                R.color.icon_bg_pink
        );

        int currentColor = isChecked
                ? pinkColor
                : redColor;

        int backgroundColor = isChecked
                ? pinkBackground
                : ColorUtils.setAlphaComponent(redColor, 24);

        int trackColor = ColorUtils.setAlphaComponent(
                currentColor,
                90
        );

        // Màu nút tròn của Switch.
        switchSensitiveTreatment.setThumbTintList(
                ColorStateList.valueOf(currentColor)
        );

        // Màu thanh phía sau của Switch.
        switchSensitiveTreatment.setTrackTintList(
                ColorStateList.valueOf(trackColor)
        );

        // Màu khung bao quanh.
        layoutSensitiveTreatment.setStrokeColor(currentColor);
        layoutSensitiveTreatment.setStrokeWidth(dpToPx(1));
        layoutSensitiveTreatment.setCardBackgroundColor(backgroundColor);
    }

    /**
     * Hiệu ứng làm View nhạt đi khi đang nhấn.
     */
    private void applyPressedEffect(@Nullable View target) {

        if (target == null) {
            return;
        }

        target.setOnTouchListener((view, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    view.animate()
                            .alpha(0.55f)
                            .setDuration(70)
                            .start();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    view.animate()
                            .alpha(1f)
                            .setDuration(120)
                            .start();
                    break;
            }

            // Không chặn sự kiện click.
            return false;
        });
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
     * Hiển thị hộp thoại xác nhận lưu.
     */
    private void showSaveConfirmDialog() {

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xác nhận lưu")
                .setMessage(
                        "Bạn có chắc chắn muốn lưu thiết lập thành phần này không?"
                )
                .setNegativeButton(
                        "Hủy",
                        (dialog, which) ->
                                dialog.dismiss()
                )
                .setPositiveButton(
                        "Lưu",
                        (dialog, which) -> {
                            dialog.dismiss();
                            showSaveSuccessPopup();
                        }
                )
                .show();
    }

    /**
     * Hiển thị overview_popup.xml sau khi lưu.
     */
    private void showSaveSuccessPopup() {

        Dialog dialog = new Dialog(requireContext());

        dialog.requestWindowFeature(
                Window.FEATURE_NO_TITLE
        );

        dialog.setContentView(
                R.layout.overview_popup
        );

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

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

            window.setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );

            window.addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );

            WindowManager.LayoutParams params =
                    window.getAttributes();

            params.dimAmount = 0.5f;
            window.setAttributes(params);

            window.setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private int dpToPx(int dp) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return Math.round(dp * density);
    }

    /**
     * Lưu thông tin của một thẻ thành phần.
     */
    private static class IngredientItem {

        private final MaterialCardView card;
        private final ImageView icon;
        private final TextView label;
        private final ImageView tick;
        private final String name;

        private boolean selected;

        private IngredientItem(
                MaterialCardView card,
                ImageView icon,
                TextView label,
                ImageView tick,
                String name,
                boolean selected
        ) {
            this.card = card;
            this.icon = icon;
            this.label = label;
            this.tick = tick;
            this.name = name;
            this.selected = selected;
        }
    }
}