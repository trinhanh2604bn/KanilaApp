package ui.account;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.frontend.R;
import com.example.frontend.data.model.beauty.CustomerBeautyProfileDto;
import com.example.frontend.feature.beauty.BeautyProfileViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ui.common.ViewUtils;

public class BeautyProfileOverviewFragment extends Fragment {

    private final List<GoalItem> goalItems = new ArrayList<>();

    private final Map<String, IndicatorItem> indicatorMap =
            new HashMap<>();

    private final Map<String, GoalItem> goalMap =
            new HashMap<>();

    private final Set<String> selectedGoalKeys =
            new LinkedHashSet<>();

    private final Set<String> selectedIndicatorKeys =
            new LinkedHashSet<>();

    private LinearLayout layoutSelectedGoalsSummary;
    private BeautyProfileViewModel viewModel;

    public BeautyProfileOverviewFragment() {
        super(R.layout.fragment_beauty_profile_overview);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this)
                .get(BeautyProfileViewModel.class);

        setupViews(view);
        setupEvents(view);
        renderInitialStates();
        observeViewModel();

        /*
         * Sau này có thể thay "me" bằng customer ID
         * lấy từ Auth hoặc SharedPreferences.
         */
        viewModel.loadProfile("me");
    }

    /**
     * Kết nối các View trong XML.
     */
    private void setupViews(@NonNull View view) {

        goalItems.clear();
        indicatorMap.clear();
        goalMap.clear();

        layoutSelectedGoalsSummary =
                view.findViewById(
                        R.id.layoutSelectedGoalsSummary
                );

        /*
         * Chỉ số làn da.
         */
        addIndicatorItem(
                view,
                R.id.indicatorOily,
                "oily",
                R.drawable.ic_drops,
                "Độ dầu"
        );

        addIndicatorItem(
                view,
                R.id.indicatorDry,
                "dry",
                R.drawable.ic_drops_filled,
                "Độ ẩm"
        );

        addIndicatorItem(
                view,
                R.id.indicatorCombination,
                "acne",
                R.drawable.ic_skin_acne,
                "Tình trạng mụn"
        );

        addIndicatorItem(
                view,
                R.id.indicatorNormal,
                "barrier",
                R.drawable.ic_goal_recovery,
                "Hàng rào da"
        );

        addIndicatorItem(
                view,
                R.id.indicatorSensitive,
                "sensitive",
                R.drawable.ic_skin_sensitive,
                "Độ nhạy cảm"
        );

        addIndicatorItem(
                view,
                R.id.indicatorUnknown,
                "even_tone",
                R.drawable.ic_skin_spots,
                "Độ đều màu"
        );

        /*
         * Mục tiêu làm đẹp.
         */
        addGoalItem(
                view,
                R.id.goalAcne,
                "acne",
                "Giảm mụn",
                R.drawable.ic_goal_target
        );

        addGoalItem(
                view,
                R.id.goalBrightening,
                "brightening",
                "Mờ thâm",
                R.drawable.ic_goal_sparkle
        );

        addGoalItem(
                view,
                R.id.goalHydrating,
                "hydrating",
                "Dưỡng ẩm",
                R.drawable.ic_drops_filled
        );

        addGoalItem(
                view,
                R.id.goalOilControl,
                "oil_control",
                "Kiểm soát dầu",
                R.drawable.ic_skin_pores
        );

        addGoalItem(
                view,
                R.id.goalBarrier,
                "barrier",
                "Phục hồi da",
                R.drawable.ic_goal_recovery
        );

        addGoalItem(
                view,
                R.id.goalAntiAging,
                "anti_aging",
                "Chống lão hóa",
                R.drawable.ic_skin_aging
        );

        addGoalItem(
                view,
                R.id.goalPores,
                "pores",
                "Lỗ chân lông",
                R.drawable.ic_skin_pores
        );

        addGoalItem(
                view,
                R.id.goalSoothing,
                "soothing",
                "Làm dịu da",
                R.drawable.ic_skin_sensitive
        );

        addGoalItem(
                view,
                R.id.goalSun,
                "sun",
                "Chống nắng",
                R.drawable.ic_sun
        );

        addGoalItem(
                view,
                R.id.goalEvenTone,
                "even_tone",
                "Đều màu da",
                R.drawable.ic_skin_spots
        );
    }

    /**
     * Kết nối một thẻ chỉ số làn da.
     */
    private void addIndicatorItem(
            @NonNull View root,
            int containerId,
            @NonNull String key,
            int iconResource,
            @NonNull String name
    ) {
        View itemRoot = root.findViewById(containerId);

        if (!(itemRoot instanceof MaterialCardView)) {
            return;
        }

        MaterialCardView card =
                (MaterialCardView) itemRoot;

        ImageView icon =
                itemRoot.findViewById(
                        R.id.ivIndicatorIcon
                );

        TextView scoreText =
                itemRoot.findViewById(
                        R.id.tvIndicatorScore
                );

        TextView nameText =
                itemRoot.findViewById(
                        R.id.tvIndicatorName
                );

        TextView statusText =
                itemRoot.findViewById(
                        R.id.tvIndicatorStatus
                );

        ImageView tick =
                itemRoot.findViewById(
                        R.id.ivIndicatorTick
                );

        if (icon == null
                || scoreText == null
                || nameText == null
                || statusText == null
                || tick == null) {
            return;
        }

        icon.setImageResource(iconResource);
        nameText.setText(name);

        IndicatorItem item = new IndicatorItem(
                card,
                icon,
                scoreText,
                nameText,
                statusText,
                tick,
                key,
                name,
                false
        );

        indicatorMap.put(key, item);
    }

    /**
     * Kết nối một thẻ mục tiêu làm đẹp.
     */
    private void addGoalItem(
            @NonNull View root,
            int containerId,
            @NonNull String key,
            @NonNull String name,
            int iconResource
    ) {
        View itemRoot = root.findViewById(containerId);

        if (!(itemRoot instanceof MaterialCardView)) {
            return;
        }

        MaterialCardView card =
                (MaterialCardView) itemRoot;

        ImageView icon =
                itemRoot.findViewById(
                        R.id.ivGoalIcon
                );

        TextView label =
                itemRoot.findViewById(
                        R.id.tvGoalLabel
                );

        ImageView tick =
                itemRoot.findViewById(
                        R.id.ivGoalTick
                );

        if (icon == null
                || label == null
                || tick == null) {
            return;
        }

        icon.setImageResource(iconResource);
        label.setText(name);

        GoalItem item = new GoalItem(
                card,
                icon,
                label,
                tick,
                key,
                name,
                false
        );

        goalItems.add(item);
        goalMap.put(key, item);
    }

    /**
     * Trạng thái mặc định trước khi API trả dữ liệu.
     */
    private void renderInitialStates() {

        for (IndicatorItem item : indicatorMap.values()) {
            updateIndicatorView(item, false);
        }

        for (GoalItem item : goalItems) {
            updateGoalView(item, false);
        }

        updateSelectedGoalsSummary();
    }

    /**
     * Theo dõi dữ liệu hồ sơ từ ViewModel.
     */
    private void observeViewModel() {

        viewModel.getProfileResult().observe(
                getViewLifecycleOwner(),
                result -> {

                    if (result == null
                            || result.status == null) {
                        return;
                    }

                    switch (result.status) {

                        case SUCCESS:
                            bindProfileData(result.data);
                            break;

                        case ERROR:
                            Toast.makeText(
                                    requireContext(),
                                    result.message != null
                                            ? result.message
                                            : "Không thể tải hồ sơ",
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;

                        default:
                            break;
                    }
                }
        );
    }

    /**
     * Đưa dữ liệu API lên giao diện.
     */
    private void bindProfileData(
            @Nullable CustomerBeautyProfileDto profile
    ) {
        if (profile == null) {
            return;
        }

        /*
         * Reset chỉ số cũ.
         */
        selectedIndicatorKeys.clear();

        for (IndicatorItem item : indicatorMap.values()) {
            item.selected = false;
            item.score.setText("0/100");
            item.status.setText("Chưa có dữ liệu");

            updateIndicatorView(item, false);
        }

        /*
         * Cập nhật chỉ số từ API.
         */
        if (profile.getSkinIndicators() != null) {

            for (CustomerBeautyProfileDto.SkinIndicatorDto indicator
                    : profile.getSkinIndicators()) {

                if (indicator == null
                        || indicator.getCode() == null) {
                    continue;
                }

                IndicatorItem uiItem =
                        indicatorMap.get(
                                indicator.getCode()
                        );

                if (uiItem == null) {
                    continue;
                }

                uiItem.score.setText(
                        indicator.getScore() + "/100"
                );

                String status = indicator.getStatus();

                uiItem.status.setText(
                        status != null && !status.trim().isEmpty()
                                ? status
                                : "Chưa đánh giá"
                );

                uiItem.selected = true;
                selectedIndicatorKeys.add(uiItem.key);

                updateIndicatorView(uiItem, false);
            }
        }

        /*
         * Reset mục tiêu cũ.
         */
        selectedGoalKeys.clear();

        for (GoalItem item : goalItems) {
            item.selected = false;
            updateGoalView(item, false);
        }

        /*
         * Cập nhật mục tiêu từ API.
         */
        if (profile.getBeautyGoals() != null) {

            for (String goalCode
                    : profile.getBeautyGoals()) {

                if (goalCode == null) {
                    continue;
                }

                GoalItem uiItem =
                        goalMap.get(goalCode);

                if (uiItem == null) {
                    continue;
                }

                uiItem.selected = true;
                selectedGoalKeys.add(goalCode);

                updateGoalView(uiItem, false);
            }
        }

        updateSelectedGoalsSummary();
    }

    /**
     * Gán sự kiện cho các nút.
     */
    private void setupEvents(@NonNull View view) {

        /*
         * Nút quay lại.
         */
        View btnBack =
                view.findViewById(R.id.btnBack);

        if (btnBack != null) {
            ViewUtils.applyClickAnimation(btnBack);

            btnBack.setOnClickListener(v ->
                    handleBackNavigation()
            );
        }

        /*
         * Nút chỉnh sửa hồ sơ.
         */
        View btnEdit =
                view.findViewById(R.id.btnEdit);

        if (btnEdit != null) {
            ViewUtils.applyClickAnimation(btnEdit);

            btnEdit.setOnClickListener(v -> {
                getParentFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                        )
                        .replace(
                                R.id.main,
                                new EditSkinProfileFragment()
                        )
                        .addToBackStack(
                                "beauty_profile_to_edit"
                        )
                        .commit();
            });
        }

        /*
         * Nút cập nhật hồ sơ.
         */
        MaterialButton btnUpdateProfile =
                view.findViewById(
                        R.id.btnUpdateProfile
                );

        if (btnUpdateProfile != null) {
            ViewUtils.applyClickAnimation(
                    btnUpdateProfile
            );

            btnUpdateProfile.setOnClickListener(v ->
                    showUpdateConfirmDialog()
            );
        }

        /*
         * Nút xem màn phân tích.
         */
        MaterialButton btnViewRoutine =
                view.findViewById(
                        R.id.btnViewRoutine
                );

        if (btnViewRoutine != null) {
            ViewUtils.applyClickAnimation(
                    btnViewRoutine
            );

            btnViewRoutine.setOnClickListener(v -> {
                getParentFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                        )
                        .replace(
                                R.id.main,
                                new SkinAnalysisFragment()
                        )
                        .addToBackStack(
                                "beauty_profile_to_analysis"
                        )
                        .commit();
            });
        }

        setupGoalClickEvents();
        setupIndicatorClickEvents();
    }

    /**
     * Chọn hoặc bỏ chọn mục tiêu.
     */
    private void setupGoalClickEvents() {

        for (GoalItem item : goalItems) {

            ViewUtils.applyClickAnimation(item.card);

            item.card.setOnClickListener(v -> {

                item.selected = !item.selected;

                if (item.selected) {
                    selectedGoalKeys.add(item.key);
                } else {
                    selectedGoalKeys.remove(item.key);
                }

                updateGoalView(item, true);
                updateSelectedGoalsSummary();
            });
        }
    }

    /**
     * Chọn hoặc bỏ chọn chỉ số.
     */
    private void setupIndicatorClickEvents() {

        for (IndicatorItem item
                : indicatorMap.values()) {

            ViewUtils.applyClickAnimation(item.card);

            item.card.setOnClickListener(v -> {

                item.selected = !item.selected;

                if (item.selected) {
                    selectedIndicatorKeys.add(item.key);
                } else {
                    selectedIndicatorKeys.remove(item.key);
                }

                updateIndicatorView(item, true);
            });
        }
    }

    /**
     * Giao diện mục tiêu được chọn.
     */
    private void updateGoalView(
            @NonNull GoalItem item,
            boolean animate
    ) {
        int pinkColor = ContextCompat.getColor(
                requireContext(),
                R.color.button
        );

        int selectedBackground =
                ContextCompat.getColor(
                        requireContext(),
                        R.color.icon_bg_pink
                );

        int normalBackground =
                ContextCompat.getColor(
                        requireContext(),
                        R.color.background_main
                );

        int normalTextColor =
                ContextCompat.getColor(
                        requireContext(),
                        R.color.accent_dark
                );

        int normalStrokeColor =
                ContextCompat.getColor(
                        requireContext(),
                        R.color.border_divider
                );

        item.card.setCardBackgroundColor(
                item.selected
                        ? selectedBackground
                        : normalBackground
        );

        item.card.setStrokeColor(
                item.selected
                        ? pinkColor
                        : normalStrokeColor
        );

        item.card.setStrokeWidth(dpToPx(1));

        item.label.setTextColor(
                item.selected
                        ? pinkColor
                        : normalTextColor
        );

        item.icon.setImageTintList(
                ColorStateList.valueOf(
                        item.selected
                                ? pinkColor
                                : normalTextColor
                )
        );

        animateCardScale(
                item.card,
                item.selected,
                animate
        );

        updateTickState(
                item.tick,
                item.selected,
                animate
        );

        item.card.setContentDescription(
                item.name
                        + (
                        item.selected
                                ? " đã được chọn"
                                : " chưa được chọn"
                )
        );
    }

    /**
     * Giao diện chỉ số được chọn.
     */
    private void updateIndicatorView(
            @NonNull IndicatorItem item,
            boolean animate
    ) {
        int pinkColor =
                ContextCompat.getColor(
                        requireContext(),
                        R.color.button
                );

        int selectedBackground =
                ContextCompat.getColor(
                        requireContext(),
                        R.color.icon_bg_pink
                );

        int normalBackground =
                ContextCompat.getColor(
                        requireContext(),
                        R.color.background_main
                );

        int darkTextColor =
                ContextCompat.getColor(
                        requireContext(),
                        R.color.accent_dark
                );

        int mainTextColor =
                ContextCompat.getColor(
                        requireContext(),
                        R.color.text_main
                );

        int normalStrokeColor =
                ContextCompat.getColor(
                        requireContext(),
                        R.color.border_divider
                );

        item.card.setCardBackgroundColor(
                item.selected
                        ? selectedBackground
                        : normalBackground
        );

        item.card.setStrokeColor(
                item.selected
                        ? pinkColor
                        : normalStrokeColor
        );

        item.card.setStrokeWidth(dpToPx(1));

        item.score.setTextColor(pinkColor);

        item.name.setTextColor(
                item.selected
                        ? pinkColor
                        : darkTextColor
        );

        item.status.setTextColor(
                item.selected
                        ? pinkColor
                        : mainTextColor
        );

        item.icon.setImageTintList(
                ColorStateList.valueOf(pinkColor)
        );

        animateCardScale(
                item.card,
                item.selected,
                animate
        );

        updateTickState(
                item.tick,
                item.selected,
                animate
        );

        item.card.setContentDescription(
                item.displayName
                        + (
                        item.selected
                                ? " đã được chọn"
                                : " chưa được chọn"
                )
        );
    }

    /**
     * Phóng nhẹ thẻ khi được chọn.
     */
    private void animateCardScale(
            @NonNull MaterialCardView card,
            boolean selected,
            boolean animate
    ) {
        float scale = selected ? 1.03f : 1f;
        float elevation = selected ? dpToPx(3) : 0f;

        if (!animate) {
            card.setScaleX(scale);
            card.setScaleY(scale);
            card.setTranslationZ(elevation);
            return;
        }

        card.animate()
                .scaleX(scale)
                .scaleY(scale)
                .translationZ(elevation)
                .setDuration(220)
                .setInterpolator(
                        new androidx.interpolator.view.animation
                                .FastOutSlowInInterpolator()
                )
                .start();
    }

    /**
     * Hiện hoặc ẩn dấu tích.
     */
    private void updateTickState(
            @NonNull ImageView tick,
            boolean selected,
            boolean animate
    ) {
        tick.animate().cancel();

        if (selected) {

            tick.setVisibility(View.VISIBLE);

            if (!animate) {
                tick.setAlpha(1f);
                tick.setScaleX(1f);
                tick.setScaleY(1f);
                return;
            }

            tick.setAlpha(0f);
            tick.setScaleX(0.7f);
            tick.setScaleY(0.7f);

            tick.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(250)
                    .start();

        } else {

            if (!animate) {
                tick.setVisibility(View.GONE);
                tick.setAlpha(1f);
                tick.setScaleX(1f);
                tick.setScaleY(1f);
                return;
            }

            tick.animate()
                    .alpha(0f)
                    .scaleX(0.7f)
                    .scaleY(0.7f)
                    .setDuration(180)
                    .withEndAction(() -> {
                        tick.setVisibility(View.GONE);
                        tick.setAlpha(1f);
                        tick.setScaleX(1f);
                        tick.setScaleY(1f);
                    })
                    .start();
        }
    }

    /**
     * Cập nhật các chip trong phần Mục tiêu của bạn.
     */
    private void updateSelectedGoalsSummary() {

        if (layoutSelectedGoalsSummary == null) {
            return;
        }

        layoutSelectedGoalsSummary.removeAllViews();

        boolean hasSelectedGoal = false;

        for (GoalItem item : goalItems) {

            if (!item.selected) {
                continue;
            }

            hasSelectedGoal = true;
            addGoalChip(item.name);
        }

        if (!hasSelectedGoal) {
            addGoalChip("Chưa chọn mục tiêu");
        }
    }

    /**
     * Tạo chip mục tiêu.
     */
    private void addGoalChip(
            @NonNull String text
    ) {
        TextView chip =
                new TextView(requireContext());

        chip.setText(text);

        chip.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                13
        );

        chip.setTypeface(
                ResourcesCompat.getFont(
                        requireContext(),
                        R.font.nunito_semibold
                )
        );

        chip.setTextColor(
                ContextCompat.getColor(
                        requireContext(),
                        R.color.button
                )
        );

        chip.setBackgroundResource(
                R.drawable.bg_chip_pink
        );

        chip.setPadding(
                dpToPx(14),
                dpToPx(7),
                dpToPx(14),
                dpToPx(7)
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMarginEnd(dpToPx(8));
        chip.setLayoutParams(params);

        layoutSelectedGoalsSummary.addView(chip);
    }

    /**
     * Quay lại màn hình trước.
     */
    private void handleBackNavigation() {

        if (getParentFragmentManager()
                .getBackStackEntryCount() > 0) {

            getParentFragmentManager()
                    .popBackStack();

        } else {

            requireActivity()
                    .getOnBackPressedDispatcher()
                    .onBackPressed();
        }
    }

    /**
     * Xác nhận cập nhật hồ sơ.
     */
    private void showUpdateConfirmDialog() {

        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle("Xác nhận cập nhật")
                .setMessage(
                        "Bạn có chắc chắn muốn cập nhật hồ sơ không?"
                )
                .setNegativeButton(
                        "Hủy",
                        (dialog, which) ->
                                dialog.dismiss()
                )
                .setPositiveButton(
                        "Cập nhật",
                        (dialog, which) -> {
                            dialog.dismiss();
                            showSuccessPopup();
                        }
                )
                .show();
    }

    /**
     * Popup cập nhật thành công.
     */
    private void showSuccessPopup() {

        Dialog dialog =
                new Dialog(requireContext());

        dialog.requestWindowFeature(
                Window.FEATURE_NO_TITLE
        );

        dialog.setContentView(
                R.layout.overview_popup
        );

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        MaterialButton btnPopupOk =
                dialog.findViewById(
                        R.id.btnPopupOk
                );

        if (btnPopupOk != null) {
            btnPopupOk.setOnClickListener(v ->
                    dialog.dismiss()
            );
        }

        dialog.show();

        Window window = dialog.getWindow();

        if (window != null) {

            window.setBackgroundDrawable(
                    new ColorDrawable(
                            Color.TRANSPARENT
                    )
            );

            window.addFlags(
                    WindowManager.LayoutParams
                            .FLAG_DIM_BEHIND
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

    @Override
    public void onDestroyView() {

        goalItems.clear();
        indicatorMap.clear();
        goalMap.clear();

        layoutSelectedGoalsSummary = null;

        super.onDestroyView();
    }

    /**
     * Dữ liệu của một thẻ mục tiêu.
     */
    private static class GoalItem {

        private final MaterialCardView card;
        private final ImageView icon;
        private final TextView label;
        private final ImageView tick;

        private final String key;
        private final String name;

        private boolean selected;

        private GoalItem(
                MaterialCardView card,
                ImageView icon,
                TextView label,
                ImageView tick,
                String key,
                String name,
                boolean selected
        ) {
            this.card = card;
            this.icon = icon;
            this.label = label;
            this.tick = tick;
            this.key = key;
            this.name = name;
            this.selected = selected;
        }
    }

    /**
     * Dữ liệu của một thẻ chỉ số.
     */
    private static class IndicatorItem {

        private final MaterialCardView card;
        private final ImageView icon;
        private final TextView score;
        private final TextView name;
        private final TextView status;
        private final ImageView tick;

        private final String key;
        private final String displayName;

        private boolean selected;

        private IndicatorItem(
                MaterialCardView card,
                ImageView icon,
                TextView score,
                TextView name,
                TextView status,
                ImageView tick,
                String key,
                String displayName,
                boolean selected
        ) {
            this.card = card;
            this.icon = icon;
            this.score = score;
            this.name = name;
            this.status = status;
            this.tick = tick;
            this.key = key;
            this.displayName = displayName;
            this.selected = selected;
        }
    }
}