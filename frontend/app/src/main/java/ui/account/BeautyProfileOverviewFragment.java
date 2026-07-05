package ui.account;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BeautyProfileOverviewFragment extends Fragment {

    private static final String STATE_SELECTED_GOALS = "state_selected_goals";
    private static final String STATE_SELECTED_INDICATORS = "state_selected_indicators";

    private final List<GoalItem> goalItems = new ArrayList<>();
    private final List<IndicatorItem> indicatorItems = new ArrayList<>();

    private final Set<String> selectedGoalKeys = new LinkedHashSet<>();
    private final Set<String> selectedIndicatorKeys = new LinkedHashSet<>();

    private LinearLayout layoutSelectedGoalsSummary;

    public BeautyProfileOverviewFragment() {
        super(R.layout.fragment_beauty_profile_overview);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedGoalKeys.clear();
        selectedIndicatorKeys.clear();

        if (savedInstanceState != null) {
            ArrayList<String> savedGoals = savedInstanceState.getStringArrayList(STATE_SELECTED_GOALS);
            if (savedGoals != null) selectedGoalKeys.addAll(savedGoals);

            ArrayList<String> savedIndicators = savedInstanceState.getStringArrayList(STATE_SELECTED_INDICATORS);
            if (savedIndicators != null) selectedIndicatorKeys.addAll(savedIndicators);
        } else {
            // Mặc định chọn một số mục
            selectedGoalKeys.add("acne");
            selectedGoalKeys.add("brightening");
            selectedGoalKeys.add("hydrating");
            selectedIndicatorKeys.add("oily");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(STATE_SELECTED_GOALS, new ArrayList<>(selectedGoalKeys));
        outState.putStringArrayList(STATE_SELECTED_INDICATORS, new ArrayList<>(selectedIndicatorKeys));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        setupEvents(view);
        updateAllStates();
    }

    private void setupViews(@NonNull View view) {
        goalItems.clear();
        indicatorItems.clear();

        layoutSelectedGoalsSummary = view.findViewById(R.id.layoutSelectedGoalsSummary);

        // Khởi tạo các thẻ Chỉ số làn da
        addIndicatorItem(view, R.id.indicatorOily, "oily", R.drawable.ic_drops, 72, "Độ dầu", "Khá cao");
        addIndicatorItem(view, R.id.indicatorDry, "dry", R.drawable.ic_drops_filled, 58, "Độ ẩm", "Cần cải thiện");
        addIndicatorItem(view, R.id.indicatorCombination, "combination", R.drawable.ic_skin_acne, 64, "Tình trạng mụn", "Trung bình");
        addIndicatorItem(view, R.id.indicatorNormal, "barrier", R.drawable.ic_goal_recovery, 70, "Hàng rào da", "Khá tốt");
        addIndicatorItem(view, R.id.indicatorSensitive, "sensitive", R.drawable.ic_skin_sensitive, 55, "Độ nhạy cảm", "Cần theo dõi");
        addIndicatorItem(view, R.id.indicatorUnknown, "even_tone", R.drawable.ic_skin_spots, 60, "Độ đều màu", "Trung bình");

        // Khởi tạo các thẻ Mục tiêu làm đẹp
        addGoalItem(view, R.id.goalAcne, "acne", "Giảm mụn", R.drawable.ic_goal_target);
        addGoalItem(view, R.id.goalBrightening, "brightening", "Mờ thâm", R.drawable.ic_goal_sparkle);
        addGoalItem(view, R.id.goalHydrating, "hydrating", "Dưỡng ẩm", R.drawable.ic_drops_filled);
        addGoalItem(view, R.id.goalOilControl, "oil_control", "Kiểm soát dầu", R.drawable.ic_skin_pores);
        addGoalItem(view, R.id.goalBarrier, "barrier", "Phục hồi da", R.drawable.ic_goal_recovery);
        addGoalItem(view, R.id.goalAntiAging, "anti_aging", "Chống lão hóa", R.drawable.ic_skin_aging);
        addGoalItem(view, R.id.goalPores, "pores", "Lỗ chân lông", R.drawable.ic_skin_pores);
        addGoalItem(view, R.id.goalSoothing, "soothing", "Làm dịu da", R.drawable.ic_skin_sensitive);
        addGoalItem(view, R.id.goalSun, "sun", "Chống nắng", R.drawable.ic_sun);
        addGoalItem(view, R.id.goalEvenTone, "even_tone", "Đều màu da", R.drawable.ic_skin_spots);
    }

    private void addIndicatorItem(@NonNull View root, int containerId, @NonNull String key, int iconResource, int score, @NonNull String name, @NonNull String status) {
        View itemRoot = root.findViewById(containerId);
        if (!(itemRoot instanceof MaterialCardView)) return;
        MaterialCardView card = (MaterialCardView) itemRoot;

        ImageView icon = itemRoot.findViewById(R.id.ivIndicatorIcon);
        TextView scoreText = itemRoot.findViewById(R.id.tvIndicatorScore);
        TextView nameText = itemRoot.findViewById(R.id.tvIndicatorName);
        TextView statusText = itemRoot.findViewById(R.id.tvIndicatorStatus);
        ImageView tick = itemRoot.findViewById(R.id.ivIndicatorTick);

        if (icon == null || scoreText == null || nameText == null || statusText == null || tick == null) return;

        icon.setImageResource(iconResource);
        scoreText.setText(score + "/100");
        nameText.setText(name);
        statusText.setText(status);

        indicatorItems.add(new IndicatorItem(card, icon, scoreText, nameText, statusText, tick, key, name, selectedIndicatorKeys.contains(key)));
    }

    private void addGoalItem(@NonNull View root, int containerId, @NonNull String key, @NonNull String name, int iconResource) {
        View itemRoot = root.findViewById(containerId);
        if (!(itemRoot instanceof MaterialCardView)) return;
        MaterialCardView card = (MaterialCardView) itemRoot;

        ImageView icon = itemRoot.findViewById(R.id.ivGoalIcon);
        TextView label = itemRoot.findViewById(R.id.tvGoalLabel);
        ImageView tick = itemRoot.findViewById(R.id.ivGoalTick);

        if (icon == null || label == null || tick == null) return;

        icon.setImageResource(iconResource);
        label.setText(name);

        goalItems.add(new GoalItem(card, icon, label, tick, key, name, selectedGoalKeys.contains(key)));
    }

    private void setupEvents(@NonNull View view) {
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            // MaterialCardView đã có ripple mặc định qua app:rippleColor trong XML
            // Không gán background mới để tránh làm mất icon/style của Card
            btnBack.setOnClickListener(v -> handleBackNavigation());
        }

        View btnEdit = view.findViewById(R.id.btnEdit);
        if (btnEdit != null) {
            applyRipple(btnEdit);
            btnEdit.setOnClickListener(v -> showUpdateConfirmDialog());
        }

        MaterialButton btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        if (btnUpdateProfile != null) {
            btnUpdateProfile.setOnClickListener(v -> showUpdateConfirmDialog());
        }

        MaterialButton btnViewRoutine = view.findViewById(R.id.btnViewRoutine);
        if (btnViewRoutine != null) {
            btnViewRoutine.setOnClickListener(v -> openRecommendationLook());
        }

        setupGoalClickEvents();
        setupIndicatorClickEvents();
    }

    private void setupGoalClickEvents() {
        for (GoalItem item : goalItems) {
            item.card.setOnClickListener(v -> {
                item.selected = !item.selected;
                if (item.selected) selectedGoalKeys.add(item.key);
                else selectedGoalKeys.remove(item.key);
                updateGoalView(item);
                updateSelectedGoalsSummary();
            });
        }
    }

    private void setupIndicatorClickEvents() {
        for (IndicatorItem item : indicatorItems) {
            item.card.setOnClickListener(v -> {
                item.selected = !item.selected;
                if (item.selected) selectedIndicatorKeys.add(item.key);
                else selectedIndicatorKeys.remove(item.key);
                updateIndicatorView(item);
            });
        }
    }

    private void updateAllStates() {
        for (GoalItem item : goalItems) updateGoalView(item);
        for (IndicatorItem item : indicatorItems) updateIndicatorView(item);
        updateSelectedGoalsSummary();
    }

    private void updateGoalView(@NonNull GoalItem item) {
        int pink = ContextCompat.getColor(requireContext(), R.color.button);
        int lightPinkStroke = ContextCompat.getColor(requireContext(), R.color.primary);
        int lightPinkBg = ContextCompat.getColor(requireContext(), R.color.background_sub);
        int dark = ContextCompat.getColor(requireContext(), R.color.accent_dark);

        if (item.selected) {
            item.card.setStrokeColor(pink);
            item.card.setStrokeWidth(dpToPx(1)); // Giảm độ dày viền
            item.card.setCardBackgroundColor(lightPinkBg); // Sử dụng nền cực nhạt
            item.label.setTextColor(pink);
            item.tick.setVisibility(View.VISIBLE);
        } else {
            item.card.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.border_divider));
            item.card.setStrokeWidth(dpToPx(1));
            item.card.setCardBackgroundColor(Color.WHITE);
            item.label.setTextColor(dark);
            item.tick.setVisibility(View.GONE);
        }
    }

    private void updateIndicatorView(@NonNull IndicatorItem item) {
        int pink = ContextCompat.getColor(requireContext(), R.color.button);
        int lightPinkBg = ContextCompat.getColor(requireContext(), R.color.background_sub);

        if (item.selected) {
            item.card.setStrokeColor(pink);
            item.card.setStrokeWidth(dpToPx(1)); // Giảm độ dày viền
            item.card.setCardBackgroundColor(lightPinkBg); // Sử dụng nền cực nhạt
            item.tick.setVisibility(View.VISIBLE);
        } else {
            item.card.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.border_divider));
            item.card.setStrokeWidth(dpToPx(1));
            item.card.setCardBackgroundColor(Color.WHITE);
            item.tick.setVisibility(View.GONE);
        }
    }

    private void updateSelectedGoalsSummary() {
        if (layoutSelectedGoalsSummary == null) return;
        layoutSelectedGoalsSummary.removeAllViews();
        boolean any = false;
        for (GoalItem item : goalItems) {
            if (item.selected) {
                any = true;
                addGoalChip(item.name);
            }
        }
        if (!any) addGoalChip("Chưa chọn mục tiêu");
    }

    private void addGoalChip(@NonNull String text) {
        TextView chip = new TextView(requireContext());
        chip.setText(text);
        chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        chip.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.nunito_semibold));
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.button));
        chip.setBackgroundResource(R.drawable.bg_chip_pink);
        chip.setPadding(dpToPx(14), dpToPx(7), dpToPx(14), dpToPx(7));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-2, -2);
        p.setMarginEnd(dpToPx(8));
        chip.setLayoutParams(p);
        layoutSelectedGoalsSummary.addView(chip);
    }

    private void applyRipple(View view) {
        TypedValue v = new TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, v, true);
        view.setBackgroundResource(v.resourceId);
    }

    private void handleBackNavigation() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) getParentFragmentManager().popBackStack();
        else requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }

    private void showUpdateConfirmDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xác nhận cập nhật")
                .setMessage("Bạn có chắc chắn muốn cập nhật hồ sơ không?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Cập nhật", (d, w) -> showSuccessPopup())
                .show();
    }

    private void showSuccessPopup() {
        Dialog d = new Dialog(requireContext());
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.overview_popup);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        d.findViewById(R.id.btnPopupOk).setOnClickListener(v -> d.dismiss());
        d.show();
    }

    private void openRecommendationLook() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.main, new RecommendationLookFragment())
                .addToBackStack(null).commit();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private static class GoalItem {
        MaterialCardView card; ImageView icon; TextView label; ImageView tick;
        String key; String name; boolean selected;
        GoalItem(MaterialCardView c, ImageView i, TextView l, ImageView t, String k, String n, boolean s) {
            card = c; icon = i; label = l; tick = t; key = k; name = n; selected = s;
        }
    }

    private static class IndicatorItem {
        MaterialCardView card; ImageView icon; TextView score; TextView name; TextView status; ImageView tick;
        String key; String displayName; boolean selected;
        IndicatorItem(MaterialCardView c, ImageView i, TextView sc, TextView n, TextView st, ImageView t, String k, String d, boolean s) {
            card = c; icon = i; score = sc; name = n; status = st; tick = t; key = k; displayName = d; selected = s;
        }
    }
}