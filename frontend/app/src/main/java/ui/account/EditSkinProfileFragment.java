package ui.account;

import android.app.Dialog;
import android.content.res.ColorStateList;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.frontend.R;
import com.example.frontend.data.model.beauty.CustomerBeautyProfileDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.beauty.BeautyProfileViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import ui.common.ViewUtils;

public class EditSkinProfileFragment extends Fragment {

    private final List<SelectableItem> skinTypeGroup = new ArrayList<>();
    private final List<SelectableItem> skinConditionGroup = new ArrayList<>();
    private final List<SelectableItem> sensitivityGroup = new ArrayList<>();
    private final List<SelectableItem> skinColorGroup = new ArrayList<>();
    private final List<SelectableItem> skinUndertoneGroup = new ArrayList<>();
    private final List<SelectableItem> finishGroup = new ArrayList<>();
    private final List<SelectableItem> lipstickGroup = new ArrayList<>();
    private final List<SelectableItem> makeupStyleGroup = new ArrayList<>();
    private final List<SelectableItem> budgetGroup = new ArrayList<>();
    private final List<SelectableItem> avoidIngredientsGroup = new ArrayList<>();

    private MaterialButton btnSaveBottom;
    private boolean isDirty = false;
    private BeautyProfileViewModel viewModel;

    private int brandPink, softPinkBg, darkText, grayBorder, dp1;

    public EditSkinProfileFragment() {
        super(R.layout.fragment_edit_skin_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(BeautyProfileViewModel.class);

        initColors();
        initViews(view);
        setupEvents(view);
        loadCurrentData();
    }

    private void initColors() {
        brandPink = ContextCompat.getColor(requireContext(), R.color.button);
        softPinkBg = Color.parseColor("#FFF9FA");
        darkText = ContextCompat.getColor(requireContext(), R.color.accent_dark);
        grayBorder = ContextCompat.getColor(requireContext(), R.color.border_divider);
        dp1 = Math.round(1 * getResources().getDisplayMetrics().density);
    }

    private void initViews(View view) {
        // 1. Skin Type (Single)
        addSelectableItem(view, R.id.cardSkinOily, skinTypeGroup, "Da dầu", R.drawable.ic_drops_filled, false, true);
        addSelectableItem(view, R.id.cardSkinDry, skinTypeGroup, "Da khô", R.drawable.ic_drops, false, true);
        addSelectableItem(view, R.id.cardSkinCombination, skinTypeGroup, "Da hỗn hợp", R.drawable.ic_skin_mixed, false, true);
        addSelectableItem(view, R.id.cardSkinNormal, skinTypeGroup, "Da thường", R.drawable.ic_skin_normal, false, true);
        addSelectableItem(view, R.id.cardSkinSensitive, skinTypeGroup, "Da nhạy cảm", R.drawable.ic_skin_sensitive, false, true);
        addSelectableItem(view, R.id.cardSkinUnknown, skinTypeGroup, "Chưa xác định", R.drawable.ic_unsure, false, true);

        // 2. Skin Condition (Multi)
        addSelectableItem(view, R.id.cardConditionAcne, skinConditionGroup, "Mụn", R.drawable.ic_skin_acne, true, true);
        addSelectableItem(view, R.id.cardConditionDarkSpots, skinConditionGroup, "Thâm mụn", R.drawable.ic_skin_spots, true, true);
        addSelectableItem(view, R.id.cardConditionMelasma, skinConditionGroup, "Nám, sạm màu", R.drawable.ic_skin_spots, true, true);
        addSelectableItem(view, R.id.cardConditionDullness, skinConditionGroup, "Da xỉn màu", R.drawable.ic_skin_dullness, true, true);
        addSelectableItem(view, R.id.cardConditionLargePores, skinConditionGroup, "Lỗ chân lông to", R.drawable.ic_skin_pores, true, true);
        addSelectableItem(view, R.id.cardConditionBlackheads, skinConditionGroup, "Mụn đầu đen", R.drawable.ic_skin_acne, true, true);
        addSelectableItem(view, R.id.cardConditionRedness, skinConditionGroup, "Da dễ đỏ", R.drawable.ic_skin_redness, true, true);
        addSelectableItem(view, R.id.cardConditionDehydrated, skinConditionGroup, "Da thiếu nước", R.drawable.ic_drops, true, true);
        addSelectableItem(view, R.id.cardConditionWrinkles, skinConditionGroup, "Nếp nhăn, lão hóa", R.drawable.ic_skin_aging, true, true);
        addSelectableItem(view, R.id.cardConditionUneven, skinConditionGroup, "Bề mặt da không mịn", R.drawable.ic_skin_mixed, true, true);
        addSelectableItem(view, R.id.cardConditionDamaged, skinConditionGroup, "Hàng rào da yếu", R.drawable.ic_goal_recovery, true, true);
        addSelectableItem(view, R.id.cardConditionSun, skinConditionGroup, "Da chịu tác động của nắng", R.drawable.ic_sun, true, true);

        // 3. Sensitivity (Single)
        addSelectableItem(view, R.id.cardSensitivityLow, sensitivityGroup, "Ít nhạy cảm", R.drawable.ic_shield_star, false, true);
        addSelectableItem(view, R.id.cardSensitivityMedium, sensitivityGroup, "Dễ kích ứng nhẹ", R.drawable.ic_skin_sensitive, false, true);
        addSelectableItem(view, R.id.cardSensitivityHigh, sensitivityGroup, "Rất nhạy cảm", R.drawable.ic_alert, false, true);
        addSelectableItem(view, R.id.cardSensitivityReactive, sensitivityGroup, "Dễ đỏ hoặc rát khi đổi sản phẩm", R.drawable.ic_skin_redness, false, true);

        // 4. Color (Single) - Hide icon to simplify
        addSelectableItem(view, R.id.cardColorFair, skinColorGroup, "Da rất sáng", 0, false, false);
        addSelectableItem(view, R.id.cardColorLight, skinColorGroup, "Da sáng", 0, false, false);
        addSelectableItem(view, R.id.cardColorMedium, skinColorGroup, "Da trung bình", 0, false, false);
        addSelectableItem(view, R.id.cardColorTan, skinColorGroup, "Da ngăm", 0, false, false);
        addSelectableItem(view, R.id.cardColorDeep, skinColorGroup, "Da sẫm màu", 0, false, false);

        // 5. Undertone (Single) - Hide icon to simplify
        addSelectableItem(view, R.id.cardUndertoneCool, skinUndertoneGroup, "Sắc lạnh", 0, false, false);
        addSelectableItem(view, R.id.cardUndertoneWarm, skinUndertoneGroup, "Sắc ấm", 0, false, false);
        addSelectableItem(view, R.id.cardUndertoneNeutral, skinUndertoneGroup, "Sắc trung tính", 0, false, false);
        addSelectableItem(view, R.id.cardUndertoneOlive, skinUndertoneGroup, "Sắc ô liu", 0, false, false);
        addSelectableItem(view, R.id.cardUndertoneUnknown, skinUndertoneGroup, "Chưa xác định", 0, false, false);

        // 6. Finish (Single)
        addSelectableItem(view, R.id.cardFinishNatural, finishGroup, "Tự nhiên", R.drawable.ic_face, false, true);
        addSelectableItem(view, R.id.cardFinishBright, finishGroup, "Sáng hơn tông da", R.drawable.ic_lightbulb, false, true);
        addSelectableItem(view, R.id.cardFinishWarm, finishGroup, "Căng bóng ánh ấm", R.drawable.ic_sun, false, true);
        addSelectableItem(view, R.id.cardFinishPinkish, finishGroup, "Tươi sáng ánh hồng", R.drawable.ic_face, false, true);
        addSelectableItem(view, R.id.cardFinishMatte, finishGroup, "Lì, ít bóng", R.drawable.ic_face, false, true);

        // 7. Lipstick (Multi) - Hide icon to simplify
        addSelectableItem(view, R.id.cardLipstickNude, lipstickGroup, "Màu nude", 0, true, false);
        addSelectableItem(view, R.id.cardLipstickPink, lipstickGroup, "Màu hồng", 0, true, false);
        addSelectableItem(view, R.id.cardLipstickCoral, lipstickGroup, "Màu cam san hô", 0, true, false);
        addSelectableItem(view, R.id.cardLipstickRed, lipstickGroup, "Màu đỏ", 0, true, false);
        addSelectableItem(view, R.id.cardLipstickBrown, lipstickGroup, "Màu nâu", 0, true, false);
        addSelectableItem(view, R.id.cardLipstickMlbb, lipstickGroup, "Màu môi tự nhiên", 0, true, false);
        addSelectableItem(view, R.id.cardLipstickBold, lipstickGroup, "Màu đậm, nổi bật", 0, true, false);

        // 8. Makeup Style (Multi) - Hide icon to simplify
        addSelectableItem(view, R.id.cardMakeupNatural, makeupStyleGroup, "Trang điểm tự nhiên", 0, true, false);
        addSelectableItem(view, R.id.cardMakeupKorean, makeupStyleGroup, "Phong cách Hàn Quốc", 0, true, false);
        addSelectableItem(view, R.id.cardMakeupGlam, makeupStyleGroup, "Trang điểm sắc sảo", 0, true, false);
        addSelectableItem(view, R.id.cardMakeupOffice, makeupStyleGroup, "Trang điểm công sở", 0, true, false);
        addSelectableItem(view, R.id.cardMakeupParty, makeupStyleGroup, "Trang điểm dự tiệc", 0, true, false);
        addSelectableItem(view, R.id.cardMakeupDaily, makeupStyleGroup, "Trang điểm hằng ngày", 0, true, false);

        // 9. Budget (Single) - Hide icon as requested
        addSelectableItem(view, R.id.cardBudgetUnder300, budgetGroup, "Dưới 300K", 0, false, false);
        addSelectableItem(view, R.id.cardBudget300_500, budgetGroup, "300K - 500K", 0, false, false);
        addSelectableItem(view, R.id.cardBudgetOver500, budgetGroup, "500K +", 0, false, false);

        // 10. Avoid Ingredients (Multi)
        addSelectableItem(view, R.id.cardAvoidFragrance, avoidIngredientsGroup, "Hương liệu", R.drawable.ic_drops, true, true);
        addSelectableItem(view, R.id.cardAvoidAlcohol, avoidIngredientsGroup, "Cồn khô", R.drawable.ic_beaker, true, true);
        addSelectableItem(view, R.id.cardAvoidEssentialOil, avoidIngredientsGroup, "Tinh dầu", R.drawable.ic_drops, true, true);
        addSelectableItem(view, R.id.cardAvoidParaben, avoidIngredientsGroup, "Paraben", R.drawable.ic_beaker, true, true);
        addSelectableItem(view, R.id.cardAvoidMineralOil, avoidIngredientsGroup, "Dầu khoáng", R.drawable.ic_drops, true, true);
        addSelectableItem(view, R.id.cardAvoidSilicone, avoidIngredientsGroup, "Silicone", R.drawable.ic_beaker, true, true);
        addSelectableItem(view, R.id.cardAvoidSulfate, avoidIngredientsGroup, "Sulfate", R.drawable.ic_beaker, true, true);
        addSelectableItem(view, R.id.cardAvoidLanolin, avoidIngredientsGroup, "Lanolin", R.drawable.ic_beaker, true, true);
        addSelectableItem(view, R.id.cardAvoidRetinoid, avoidIngredientsGroup, "Retinoid", R.drawable.ic_beaker, true, true);
        addSelectableItem(view, R.id.cardAvoidHighAcid, avoidIngredientsGroup, "Acid cao", R.drawable.ic_beaker, true, true);
    }

    private void addSelectableItem(View root, int id, List<SelectableItem> group, String label, int iconRes, boolean isMultiChoice, boolean showIcon) {
        View itemRoot = root.findViewById(id);
        if (itemRoot != null) {
            ImageView icon = itemRoot.findViewById(R.id.ivIcon);
            TextView tvLabel = itemRoot.findViewById(R.id.tvLabel);
            ImageView tick = itemRoot.findViewById(R.id.ivTick);

            if (icon != null) {
                if (showIcon && iconRes != 0) {
                    icon.setImageResource(iconRes);
                    icon.setVisibility(View.VISIBLE);
                } else {
                    icon.setVisibility(View.GONE);
                }
            }
            if (tvLabel != null) tvLabel.setText(label);

            SelectableItem item = new SelectableItem(itemRoot, icon, tvLabel, tick, label, false);
            group.add(item);
            updateItemUI(item, false);

            ViewUtils.applyClickAnimation(itemRoot);
            itemRoot.setOnClickListener(v -> {
                if (isMultiChoice) {
                    item.selected = !item.selected;
                } else {
                    // Single choice logic
                    if (item.selected) return; // Already selected
                    for (SelectableItem other : group) {
                        if (other.selected) {
                            other.selected = false;
                            updateItemUI(other, true);
                        }
                    }
                    item.selected = true;
                }
                updateItemUI(item, true);
                onDataChanged();
            });
        }
    }

    private void updateItemUI(SelectableItem item, boolean animate) {
        if (item.selected) {
            item.view.setBackgroundResource(R.drawable.bg_chip_pink);
            if (item.label != null) item.label.setTextColor(brandPink);
            if (item.icon != null) item.icon.setImageTintList(ColorStateList.valueOf(brandPink));
            if (item.tick != null) item.tick.setVisibility(View.VISIBLE);
            if (animate) {
                item.view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
            } else {
                item.view.setScaleX(1.05f);
                item.view.setScaleY(1.05f);
            }
        } else {
            item.view.setBackgroundResource(R.drawable.bg_card);
            if (item.label != null) item.label.setTextColor(darkText);
            if (item.icon != null) item.icon.setImageTintList(ColorStateList.valueOf(darkText));
            if (item.tick != null) item.tick.setVisibility(View.GONE);
            if (animate) {
                item.view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
            } else {
                item.view.setScaleX(1.0f);
                item.view.setScaleY(1.0f);
            }
        }
    }

    private void loadCurrentData() {
        NetworkResult<CustomerBeautyProfileDto> result = viewModel.getProfileResult().getValue();
        if (result != null && result.status == NetworkResult.Status.SUCCESS && result.data != null) {
            CustomerBeautyProfileDto profile = result.data;
            if (profile.getSkinType() != null) syncSelection(skinTypeGroup, profile.getSkinType());
            if (profile.getSkinConcerns() != null) syncMultiSelection(skinConditionGroup, profile.getSkinConcerns());
            if (profile.getSensitivityLevel() != null) syncSelection(sensitivityGroup, profile.getSensitivityLevel());
            if (profile.getSkinColor() != null) syncSelection(skinColorGroup, profile.getSkinColor());
            if (profile.getSkinUndertone() != null) syncSelection(skinUndertoneGroup, profile.getSkinUndertone());
            if (profile.getFoundationFinish() != null) syncSelection(finishGroup, profile.getFoundationFinish());
            if (profile.getLipstickColors() != null) syncMultiSelection(lipstickGroup, profile.getLipstickColors());
            if (profile.getMakeupStyles() != null) syncMultiSelection(makeupStyleGroup, profile.getMakeupStyles());
            if (profile.getBudget() != null) syncSelection(budgetGroup, profile.getBudget());
            if (profile.getAvoidIngredients() != null) syncMultiSelection(avoidIngredientsGroup, profile.getAvoidIngredients());
            isDirty = false;
            updateSaveButtonsState(false);
        }
    }

    private void syncSelection(List<SelectableItem> group, String selectedName) {
        for (SelectableItem item : group) {
            item.selected = item.name.equalsIgnoreCase(selectedName);
            updateItemUI(item, false);
        }
    }

    private void syncMultiSelection(List<SelectableItem> group, List<String> selectedNames) {
        if (selectedNames == null) return;
        for (SelectableItem item : group) {
            item.selected = selectedNames.contains(item.name);
            updateItemUI(item, false);
        }
    }

    private void setupEvents(View view) {
        view.findViewById(R.id.btnBack).setOnClickListener(v -> handleBackNavigation());
        btnSaveBottom = view.findViewById(R.id.btnSaveProfile);
        btnSaveBottom.setOnClickListener(v -> { if (isDirty) saveProfile(); });
        
        setupSeeMore(view, R.id.tvSeeMoreCondition, R.id.layoutMoreCondition, R.id.scrollCondition);
        setupSeeMore(view, R.id.tvSeeMoreLipstick, R.id.layoutMoreLipstick, R.id.scrollLipstick);
        setupSeeMore(view, R.id.tvSeeMoreAvoid, R.id.layoutMoreAvoid, R.id.scrollAvoid);

        updateSaveButtonsState(false);
    }

    private void setupSeeMore(View root, int btnId, int layoutId, int scrollId) {
        TextView btn = root.findViewById(btnId);
        View layout = root.findViewById(layoutId);
        View scroll = root.findViewById(scrollId);
        if (btn != null && layout != null && scroll != null) {
            btn.setOnClickListener(v -> {
                if (layout.getVisibility() == View.GONE) {
                    layout.setVisibility(View.VISIBLE);
                    // Keep scroll visible, just show the next row below
                    btn.setText(R.string.action_collapse);
                } else {
                    layout.setVisibility(View.GONE);
                    btn.setText(R.string.action_see_more);
                }
            });
        }
    }

    private void onDataChanged() {
        if (!isDirty) updateSaveButtonsState(true);
    }

    private void updateSaveButtonsState(boolean dirty) {
        this.isDirty = dirty;
        if (btnSaveBottom != null) {
            btnSaveBottom.setEnabled(dirty);
            btnSaveBottom.setAlpha(dirty ? 1.0f : 0.4f);
        }
    }

    private void saveProfile() {
        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Lưu hồ sơ làn da")
                .setMessage("Bạn có muốn lưu các thay đổi này vào hồ sơ của mình không?")
                .setNegativeButton("Hủy", (d, which) -> d.dismiss())
                .setPositiveButton("Lưu", (d, which) -> {
                    d.dismiss();
                    performSave();
                })
                .show();
        ViewUtils.customizeDialogButtons(dialog);
    }

    private void performSave() {
        CustomerBeautyProfileDto profile = viewModel.getProfileResult().getValue() != null && viewModel.getProfileResult().getValue().data != null 
            ? viewModel.getProfileResult().getValue().data : new CustomerBeautyProfileDto();

        profile.setSkinType(getSelectedName(skinTypeGroup));
        profile.setSkinConcerns(getSelectedNames(skinConditionGroup));
        profile.setSensitivityLevel(getSelectedName(sensitivityGroup));
        profile.setSkinColor(getSelectedName(skinColorGroup));
        profile.setSkinUndertone(getSelectedName(skinUndertoneGroup));
        profile.setFoundationFinish(getSelectedName(finishGroup));
        profile.setLipstickColors(getSelectedNames(lipstickGroup));
        profile.setMakeupStyles(getSelectedNames(makeupStyleGroup));
        profile.setBudget(getSelectedName(budgetGroup));
        profile.setAvoidIngredients(getSelectedNames(avoidIngredientsGroup));
        
        int itemsSet = 0;
        if (profile.getSkinType() != null) itemsSet++;
        if (!profile.getSkinConcerns().isEmpty()) itemsSet++;
        if (profile.getSensitivityLevel() != null) itemsSet++;
        if (profile.getSkinColor() != null) itemsSet++;
        if (profile.getSkinUndertone() != null) itemsSet++;
        if (profile.getFoundationFinish() != null) itemsSet++;
        if (!profile.getLipstickColors().isEmpty()) itemsSet++;
        if (!profile.getMakeupStyles().isEmpty()) itemsSet++;
        if (profile.getBudget() != null) itemsSet++;
        if (!profile.getAvoidIngredients().isEmpty()) itemsSet++;
        profile.setProfileCompletion((itemsSet * 100) / 10);

        viewModel.updateProfileLocally(profile);
        showSaveSuccessPopup();
    }

    private String getSelectedName(List<SelectableItem> group) {
        for (SelectableItem item : group) if (item.selected) return item.name;
        return null;
    }

    private List<String> getSelectedNames(List<SelectableItem> group) {
        List<String> names = new ArrayList<>();
        for (SelectableItem item : group) if (item.selected) names.add(item.name);
        return names;
    }

    private void showSaveSuccessPopup() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.overview_popup);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        MaterialButton btnPopupOk = dialog.findViewById(R.id.btnPopupOk);
        if (btnPopupOk != null) {
            btnPopupOk.setOnClickListener(v -> {
                dialog.dismiss();
                updateSaveButtonsState(false);
                handleBackNavigation();
            });
        }
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.5f;
            window.setAttributes(lp);
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private void handleBackNavigation() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) getParentFragmentManager().popBackStack();
        else requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }

    private static class SelectableItem {
        View view;
        ImageView icon;
        TextView label;
        ImageView tick;
        String name;
        boolean selected;
        SelectableItem(View view, ImageView icon, TextView label, ImageView tick, String name, boolean selected) {
            this.view = view;
            this.icon = icon;
            this.label = label;
            this.tick = tick;
            this.name = name;
            this.selected = selected;
        }
    }
}
