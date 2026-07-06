package ui.account;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
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

public class BeautyProfileOverviewFragment extends Fragment {

    private final List<GoalItem> goalItems = new ArrayList<>();
    private final Map<String, IndicatorItem> indicatorMap = new HashMap<>();
    private final Map<String, GoalItem> goalMap = new HashMap<>();

    private final Set<String> selectedGoalKeys = new LinkedHashSet<>();
    private final Set<String> selectedIndicatorKeys = new LinkedHashSet<>();

    private LinearLayout layoutSelectedGoalsSummary;
    private BeautyProfileViewModel viewModel;

    public BeautyProfileOverviewFragment() {
        super(R.layout.fragment_beauty_profile_overview);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(BeautyProfileViewModel.class);
        
        setupViews(view);
        setupEvents(view);
        observeViewModel();
        
        // In a real app, we would get the customer ID from Auth state or use a /me endpoint
        viewModel.loadProfile("me"); 
    }

    private void setupViews(@NonNull View view) {
        goalItems.clear();
        indicatorMap.clear();
        goalMap.clear();

        layoutSelectedGoalsSummary = view.findViewById(R.id.layoutSelectedGoalsSummary);

        // Map UI IDs to API codes
        addIndicatorItem(view, R.id.indicatorOily, "oily", R.drawable.ic_drops, "Độ dầu");
        addIndicatorItem(view, R.id.indicatorDry, "dry", R.drawable.ic_drops_filled, "Độ ẩm");
        addIndicatorItem(view, R.id.indicatorCombination, "acne", R.drawable.ic_skin_acne, "Tình trạng mụn");
        addIndicatorItem(view, R.id.indicatorNormal, "barrier", R.drawable.ic_goal_recovery, "Hàng rào da");
        addIndicatorItem(view, R.id.indicatorSensitive, "sensitive", R.drawable.ic_skin_sensitive, "Độ nhạy cảm");
        addIndicatorItem(view, R.id.indicatorUnknown, "even_tone", R.drawable.ic_skin_spots, "Độ đều màu");

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

    private void addIndicatorItem(@NonNull View root, int containerId, @NonNull String key, int iconResource, @NonNull String name) {
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
        nameText.setText(name);

        IndicatorItem item = new IndicatorItem(card, icon, scoreText, nameText, statusText, tick, key, name, false);
        indicatorMap.put(key, item);
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

        GoalItem item = new GoalItem(card, icon, label, tick, key, name, false);
        goalItems.add(item);
        goalMap.put(key, item);
    }

    private void observeViewModel() {
        viewModel.getProfileResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case SUCCESS:
                    bindProfileData(result.data);
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void bindProfileData(CustomerBeautyProfileDto profile) {
        if (profile == null) return;

        // Update Indicators
        if (profile.getSkinIndicators() != null) {
            for (CustomerBeautyProfileDto.SkinIndicatorDto indicator : profile.getSkinIndicators()) {
                IndicatorItem uiItem = indicatorMap.get(indicator.getCode());
                if (uiItem != null) {
                    uiItem.score.setText(indicator.getScore() + "/100");
                    uiItem.status.setText(indicator.getStatus());
                    uiItem.selected = true; // For now assuming if it has a score it's active
                    updateIndicatorView(uiItem);
                }
            }
        }

        // Update Goals
        selectedGoalKeys.clear();
        if (profile.getBeautyGoals() != null) {
            selectedGoalKeys.addAll(profile.getBeautyGoals());
            for (String goalCode : profile.getBeautyGoals()) {
                GoalItem uiItem = goalMap.get(goalCode);
                if (uiItem != null) {
                    uiItem.selected = true;
                    updateGoalView(uiItem);
                }
            }
        }
        
        updateSelectedGoalsSummary();
    }

    private void setupEvents(@NonNull View view) {
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> handleBackNavigation());

        View btnEdit = view.findViewById(R.id.btnEdit);
        if (btnEdit != null) btnEdit.setOnClickListener(v -> showUpdateConfirmDialog());

        MaterialButton btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        if (btnUpdateProfile != null) btnUpdateProfile.setOnClickListener(v -> showUpdateConfirmDialog());

        MaterialButton btnViewRoutine = view.findViewById(R.id.btnViewRoutine);
        if (btnViewRoutine != null) {
            btnViewRoutine.setOnClickListener(v -> {
                // Navigate to RecommendationLookFragment
            });
        }
    }

    private void updateGoalView(@NonNull GoalItem item) {
        int pink = ContextCompat.getColor(requireContext(), R.color.button);
        int lightPinkBg = ContextCompat.getColor(requireContext(), R.color.background_sub);
        int dark = ContextCompat.getColor(requireContext(), R.color.accent_dark);

        if (item.selected) {
            item.card.setStrokeColor(pink);
            item.card.setStrokeWidth(dpToPx(1));
            item.card.setCardBackgroundColor(lightPinkBg);
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
            item.card.setStrokeWidth(dpToPx(1));
            item.card.setCardBackgroundColor(lightPinkBg);
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
        if (d.getWindow() != null) d.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        d.findViewById(R.id.btnPopupOk).setOnClickListener(v -> d.dismiss());
        d.show();
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
