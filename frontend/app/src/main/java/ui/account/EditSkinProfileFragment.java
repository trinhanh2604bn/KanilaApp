package ui.account;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.frontend.R;
import com.example.frontend.data.model.beauty.BeautyReferenceDto;
import com.example.frontend.data.model.beauty.CustomerBeautyProfileDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.beauty.BeautyProfileViewModel;
import com.example.frontend.feature.beauty.BeautyReferenceMapper;
import com.example.frontend.feature.beauty.UpdateBeautyProfileRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ui.common.ViewUtils;

public class EditSkinProfileFragment extends Fragment {

    private final Map<String, List<SelectableItem>> controllers = new HashMap<>();
    private final Map<String, Integer> referenceIcons = new HashMap<>();

    private MaterialButton btnSaveBottom;
    private boolean isDirty = false;
    private BeautyProfileViewModel viewModel;
    private Dialog loadingDialog;

    private int brandPink, darkText;

    public EditSkinProfileFragment() {
        super(R.layout.fragment_edit_skin_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(BeautyProfileViewModel.class);

        initColors();
        initIconMapping();
        setupEvents(view);
        observeViewModel(view);
        
        // Load references if needed
        if (viewModel.getReferencesResult().getValue() == null) {
            viewModel.loadReferences();
        }
        
        // Ensure profile is loaded
        String customerId = com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).getCustomerId();
        if (viewModel.getProfileResult().getValue() == null || 
            viewModel.getProfileResult().getValue().status != NetworkResult.Status.SUCCESS) {
            viewModel.loadProfile(customerId);
        } else {
            loadCurrentData();
        }
    }

    private void initColors() {
        brandPink = ContextCompat.getColor(requireContext(), R.color.button);
        darkText = ContextCompat.getColor(requireContext(), R.color.accent_dark);
    }

    private void initIconMapping() {
        // Skin Type
        referenceIcons.put("OILY_SKIN", R.drawable.ic_drops_filled);
        referenceIcons.put("oily", R.drawable.ic_drops_filled);
        referenceIcons.put("DRY_SKIN", R.drawable.ic_drops);
        referenceIcons.put("dry", R.drawable.ic_drops);
        referenceIcons.put("COMBINATION_SKIN", R.drawable.ic_skin_mixed);
        referenceIcons.put("combination", R.drawable.ic_skin_mixed);
        referenceIcons.put("NORMAL_SKIN", R.drawable.ic_skin_normal);
        referenceIcons.put("normal", R.drawable.ic_skin_normal);
        referenceIcons.put("SENSITIVE_SKIN", R.drawable.ic_skin_sensitive);
        referenceIcons.put("sensitive", R.drawable.ic_skin_sensitive);
        referenceIcons.put("UNKNOWN_SKIN", R.drawable.ic_unsure);
        referenceIcons.put("unknown", R.drawable.ic_unsure);

        // Concerns
        referenceIcons.put("ACNE", R.drawable.ic_skin_acne);
        referenceIcons.put("acne", R.drawable.ic_skin_acne);
        referenceIcons.put("DARK_SPOT", R.drawable.ic_skin_spots);
        referenceIcons.put("dark_spots", R.drawable.ic_skin_spots);
        referenceIcons.put("MELASMA", R.drawable.ic_skin_spots);
        referenceIcons.put("melasma", R.drawable.ic_skin_spots);
        referenceIcons.put("DULLNESS", R.drawable.ic_skin_dullness);
        referenceIcons.put("dullness", R.drawable.ic_skin_dullness);
        referenceIcons.put("LARGE_PORES", R.drawable.ic_skin_pores);
        referenceIcons.put("large_pores", R.drawable.ic_skin_pores);
        referenceIcons.put("BLACKHEADS", R.drawable.ic_skin_acne);
        referenceIcons.put("blackheads", R.drawable.ic_skin_acne);
        referenceIcons.put("REDNESS", R.drawable.ic_skin_redness);
        referenceIcons.put("redness", R.drawable.ic_skin_redness);
        referenceIcons.put("DEHYDRATED", R.drawable.ic_drops);
        referenceIcons.put("dehydrated", R.drawable.ic_drops);
        referenceIcons.put("AGING", R.drawable.ic_skin_aging);
        referenceIcons.put("wrinkles", R.drawable.ic_skin_aging);
        referenceIcons.put("UNEVEN", R.drawable.ic_skin_mixed);
        referenceIcons.put("uneven_texture", R.drawable.ic_skin_mixed);
        referenceIcons.put("DAMAGED", R.drawable.ic_goal_recovery);
        referenceIcons.put("damaged_barrier", R.drawable.ic_goal_recovery);
        referenceIcons.put("SUN_DAMAGE", R.drawable.ic_sun);
        referenceIcons.put("sun_damage", R.drawable.ic_sun);

        // Sensitivity
        referenceIcons.put("LOW_SENSITIVITY", R.drawable.ic_shield_star);
        referenceIcons.put("low", R.drawable.ic_shield_star);
        referenceIcons.put("MEDIUM_SENSITIVITY", R.drawable.ic_skin_sensitive);
        referenceIcons.put("medium", R.drawable.ic_skin_sensitive);
        referenceIcons.put("HIGH_SENSITIVITY", R.drawable.ic_alert);
        referenceIcons.put("high", R.drawable.ic_alert);
        referenceIcons.put("REACTIVE_SENSITIVITY", R.drawable.ic_skin_redness);
        referenceIcons.put("reactive", R.drawable.ic_skin_redness);

        // Finish
        referenceIcons.put("NATURAL_FINISH", R.drawable.ic_face);
        referenceIcons.put("natural", R.drawable.ic_face);
        referenceIcons.put("BRIGHT_FINISH", R.drawable.ic_lightbulb);
        referenceIcons.put("glowy", R.drawable.ic_lightbulb);
        referenceIcons.put("WARM_FINISH", R.drawable.ic_sun);
        referenceIcons.put("PINKISH_FINISH", R.drawable.ic_face);
        referenceIcons.put("MATTE_FINISH", R.drawable.ic_face);
        referenceIcons.put("matte", R.drawable.ic_face);
        referenceIcons.put("dewy", R.drawable.ic_drops);
        
        // Avoid
        referenceIcons.put("FRAGRANCE", R.drawable.ic_drops);
        referenceIcons.put("fragrance", R.drawable.ic_drops);
        referenceIcons.put("ALCOHOL", R.drawable.ic_beaker);
        referenceIcons.put("alcohol_denat", R.drawable.ic_beaker);
        referenceIcons.put("ESSENTIAL_OIL", R.drawable.ic_drops);
        referenceIcons.put("essential_oil", R.drawable.ic_drops);
        referenceIcons.put("PARABEN", R.drawable.ic_beaker);
        referenceIcons.put("paraben", R.drawable.ic_beaker);
        referenceIcons.put("MINERAL_OIL", R.drawable.ic_drops);
        referenceIcons.put("mineral_oil", R.drawable.ic_drops);
        referenceIcons.put("SILICONE", R.drawable.ic_beaker);
        referenceIcons.put("silicone", R.drawable.ic_beaker);
        referenceIcons.put("SULFATE", R.drawable.ic_beaker);
        referenceIcons.put("sulfate", R.drawable.ic_beaker);
        referenceIcons.put("LANOLIN", R.drawable.ic_beaker);
        referenceIcons.put("lanolin", R.drawable.ic_beaker);
        referenceIcons.put("RETINOID", R.drawable.ic_beaker);
        referenceIcons.put("retinoid", R.drawable.ic_beaker);
        referenceIcons.put("HIGH_ACID", R.drawable.ic_beaker);
        referenceIcons.put("aha_bha_high", R.drawable.ic_beaker);
    }

    private void observeViewModel(View rootView) {
        viewModel.getReferencesResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                renderAllGroups(rootView, result.data);
                loadCurrentData();
            }
        });

        viewModel.getProfileResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                loadCurrentData();
            }
        });

        viewModel.getSaveResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.status == NetworkResult.Status.LOADING) {
                showLoadingDialog("Kanila AI đang phân tích làn da của bạn...");
            } else if (result.status == NetworkResult.Status.SUCCESS) {
                hideLoadingDialog();
                showSaveSuccessPopup();
                viewModel.resetSaveResult();
            } else if (result.status == NetworkResult.Status.ERROR) {
                hideLoadingDialog();
                // Hiển thị Dialog lỗi thay vì Toast để xem được toàn bộ nội dung
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Lỗi cập nhật")
                        .setMessage(result.message)
                        .setPositiveButton("Đóng", (d, w) -> d.dismiss())
                        .show();
                updateSaveButtonsState(true);
                viewModel.resetSaveResult();
            }
        });
    }

    private void renderAllGroups(View rootView, List<BeautyReferenceDto> allRefs) {
        Map<String, List<BeautyReferenceDto>> grouped = new HashMap<>();
        for (BeautyReferenceDto ref : allRefs) {
            List<BeautyReferenceDto> groupList = grouped.get(ref.getReferenceGroup());
            if (groupList == null) {
                groupList = new ArrayList<>();
                grouped.put(ref.getReferenceGroup(), groupList);
            }
            groupList.add(ref);
        }

        renderGroup(grouped.get(BeautyReferenceMapper.SKIN_TYPE), rootView.findViewById(R.id.containerSkinType), false, rootView.findViewById(R.id.tvSeeMoreSkinType));
        renderGroup(grouped.get(BeautyReferenceMapper.SKIN_CONCERN), rootView.findViewById(R.id.containerSkinCondition), true, rootView.findViewById(R.id.tvSeeMoreCondition));
        renderGroup(grouped.get(BeautyReferenceMapper.SENSITIVITY_LEVEL), rootView.findViewById(R.id.containerSensitivity), false, rootView.findViewById(R.id.tvSeeMoreSensitivity));
        renderGroup(grouped.get(BeautyReferenceMapper.SKIN_COLOR), rootView.findViewById(R.id.containerSkinColor), false, rootView.findViewById(R.id.tvSeeMoreColor));
        renderGroup(grouped.get(BeautyReferenceMapper.SKIN_UNDERTONE), rootView.findViewById(R.id.containerSkinUndertone), false, rootView.findViewById(R.id.tvSeeMoreUndertone));
        renderGroup(grouped.get(BeautyReferenceMapper.FOUNDATION_FINISH), rootView.findViewById(R.id.containerFoundationFinish), false, rootView.findViewById(R.id.tvSeeMoreFinish));
        renderGroup(grouped.get(BeautyReferenceMapper.LIPSTICK_COLOR), rootView.findViewById(R.id.containerLipstickColors), true, rootView.findViewById(R.id.tvSeeMoreLipstick));
        renderGroup(grouped.get(BeautyReferenceMapper.MAKEUP_STYLE), rootView.findViewById(R.id.containerMakeupStyles), true, rootView.findViewById(R.id.tvSeeMoreMakeup));
        renderGroup(grouped.get(BeautyReferenceMapper.BUDGET), rootView.findViewById(R.id.containerBudget), false, null);
        renderGroup(grouped.get(BeautyReferenceMapper.AVOID_INGREDIENT), rootView.findViewById(R.id.containerAvoidIngredients), true, rootView.findViewById(R.id.tvSeeMoreAvoid));
    }

    private void renderGroup(List<BeautyReferenceDto> refs, ChipGroup container, boolean isMulti, TextView seeMoreBtn) {
        if (refs == null || container == null) return;
        container.removeAllViews();
        List<SelectableItem> items = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        // Lọc bỏ trùng lặp nếu Backend trả về cả mã hoa và thường (ví dụ: OILY_SKIN và oily)
        Map<String, BeautyReferenceDto> uniqueRefs = new HashMap<>();
        for (BeautyReferenceDto r : refs) {
            if (r.getReferenceCode() == null) continue;
            String key = r.getReferenceCode().toLowerCase();
            if (!uniqueRefs.containsKey(key)) {
                uniqueRefs.put(key, r);
            }
        }
        List<BeautyReferenceDto> filteredRefs = new ArrayList<>(uniqueRefs.values());

        for (int i = 0; i < filteredRefs.size(); i++) {
            BeautyReferenceDto ref = filteredRefs.get(i);
            View itemView = inflater.inflate(R.layout.item_edit_grid_selection, container, false);
            
            ImageView icon = itemView.findViewById(R.id.ivIcon);
            TextView label = itemView.findViewById(R.id.tvLabel);
            ImageView tick = itemView.findViewById(R.id.ivTick);

            label.setText(ref.getDisplayNameVi());
            
            // Tìm icon hỗ trợ cả hoa và thường
            Integer iconRes = referenceIcons.get(ref.getReferenceCode());
            if (iconRes == null) iconRes = referenceIcons.get(ref.getReferenceCode().toLowerCase());
            if (iconRes == null) iconRes = referenceIcons.get(ref.getReferenceCode().toUpperCase());

            if (iconRes != null) {
                icon.setImageResource(iconRes);
                icon.setVisibility(View.VISIBLE);
            } else {
                icon.setVisibility(View.GONE);
            }

            SelectableItem item = new SelectableItem(itemView, icon, label, tick, ref.getReferenceCode(), false);
            items.add(item);
            updateItemUI(item, false);

            ViewUtils.applyClickAnimation(itemView);
            itemView.setOnClickListener(v -> {
                if (isMulti) {
                    item.selected = !item.selected;
                } else {
                    if (item.selected) return;
                    for (SelectableItem other : items) {
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

            container.addView(itemView);
            
            if (i >= 6 && seeMoreBtn != null) {
                itemView.setVisibility(View.GONE);
                seeMoreBtn.setVisibility(View.VISIBLE);
            }
        }
        
        if (seeMoreBtn != null) {
            seeMoreBtn.setOnClickListener(v -> {
                boolean currentlyCollapsed = seeMoreBtn.getText().toString().equals(getString(R.string.action_see_more));
                for (int i = 6; i < container.getChildCount(); i++) {
                    container.getChildAt(i).setVisibility(currentlyCollapsed ? View.VISIBLE : View.GONE);
                }
                seeMoreBtn.setText(currentlyCollapsed ? R.string.action_collapse : R.string.action_see_more);
            });
        }

        if (!filteredRefs.isEmpty()) {
            controllers.put(filteredRefs.get(0).getReferenceGroup(), items);
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
            syncSelection(BeautyReferenceMapper.SKIN_TYPE, profile.getSkinType());
            syncMultiSelection(BeautyReferenceMapper.SKIN_CONCERN, profile.getSkinConcerns());
            syncSelection(BeautyReferenceMapper.SENSITIVITY_LEVEL, profile.getSensitivityLevel());
            syncSelection(BeautyReferenceMapper.SKIN_COLOR, profile.getSkinColor());
            syncSelection(BeautyReferenceMapper.SKIN_UNDERTONE, profile.getSkinUndertone());
            syncSelection(BeautyReferenceMapper.FOUNDATION_FINISH, profile.getFoundationFinish());
            syncMultiSelection(BeautyReferenceMapper.LIPSTICK_COLOR, profile.getLipstickColors());
            syncMultiSelection(BeautyReferenceMapper.MAKEUP_STYLE, profile.getMakeupStyles());
            syncSelection(BeautyReferenceMapper.BUDGET, profile.getBudget());
            syncMultiSelection(BeautyReferenceMapper.AVOID_INGREDIENT, profile.getAvoidIngredients());
            isDirty = false;
            updateSaveButtonsState(false);
        }
    }

    private void syncSelection(String group, String selectedCode) {
        List<SelectableItem> items = controllers.get(group);
        if (items == null) return;
        for (SelectableItem item : items) {
            item.selected = item.code != null && item.code.equalsIgnoreCase(selectedCode);
            updateItemUI(item, false);
        }
    }

    private void syncMultiSelection(String group, List<String> selectedCodes) {
        List<SelectableItem> items = controllers.get(group);
        if (items == null || selectedCodes == null) return;
        
        List<String> normalizedCodes = new ArrayList<>();
        for (String c : selectedCodes) if (c != null) normalizedCodes.add(c.toUpperCase());

        for (SelectableItem item : items) {
            item.selected = item.code != null && normalizedCodes.contains(item.code.toUpperCase());
            updateItemUI(item, false);
        }
    }

    private void setupEvents(View view) {
        view.findViewById(R.id.btnBack).setOnClickListener(v -> handleBackNavigation());
        btnSaveBottom = view.findViewById(R.id.btnSaveProfile);
        btnSaveBottom.setOnClickListener(v -> { if (isDirty) saveProfile(); });
        updateSaveButtonsState(false);
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
        UpdateBeautyProfileRequest request = new UpdateBeautyProfileRequest();

        // 1. Gán các giá trị từ giao diện hiện tại
        request.setSkinType(getSelectedCode(BeautyReferenceMapper.SKIN_TYPE));
        request.setSkinConcerns(ensureList(getSelectedCodes(BeautyReferenceMapper.SKIN_CONCERN)));
        request.setSensitivityLevel(getSelectedCode(BeautyReferenceMapper.SENSITIVITY_LEVEL));
        request.setSkinColor(getSelectedCode(BeautyReferenceMapper.SKIN_COLOR));
        request.setSkinUndertone(getSelectedCode(BeautyReferenceMapper.SKIN_UNDERTONE));
        request.setFoundationFinish(getSelectedCode(BeautyReferenceMapper.FOUNDATION_FINISH));
        request.setLipstickColors(ensureList(getSelectedCodes(BeautyReferenceMapper.LIPSTICK_COLOR)));
        request.setMakeupStyles(ensureList(getSelectedCodes(BeautyReferenceMapper.MAKEUP_STYLE)));
        request.setBudget(getSelectedCode(BeautyReferenceMapper.BUDGET));
        request.setAvoidIngredients(ensureList(getSelectedCodes(BeautyReferenceMapper.AVOID_INGREDIENT)));
        
        // 2. Theo Guide: Gửi mảng rỗng [] cho các trường quan hệ ID (Nhóm 3) và các mảng không sửa ở đây
        // Điều này cực kỳ quan trọng để tránh lỗi mapping ObjectID ở Backend
        request.setPreferredBrands(new ArrayList<>());
        request.setDislikedBrands(new ArrayList<>());
        request.setPreferredCategories(new ArrayList<>());
        request.setBeautyGoals(new ArrayList<>());
        request.setPreferredIngredients(new ArrayList<>());
        request.setTexturePreference(new ArrayList<>());
        request.setPurchaseIntent(new ArrayList<>());

        // Giữ lại sở thích mùi hương từ profile cũ (nếu có)
        NetworkResult<CustomerBeautyProfileDto> currentProfile = viewModel.getProfileResult().getValue();
        if (currentProfile != null && currentProfile.data != null) {
            request.setFragrancePreference(currentProfile.data.getFragrancePreference());
        }
        
        updateSaveButtonsState(false);
        // Ưu tiên dùng "me" để Backend tự xác định User từ Token
        viewModel.updateProfile("me", request);
    }

    private <T> List<T> ensureList(List<T> list) {
        return list != null ? list : new ArrayList<>();
    }

    private String getSelectedCode(String group) {
        List<SelectableItem> items = controllers.get(group);
        if (items != null) {
            for (SelectableItem item : items) {
                if (item.selected) return item.code;
            }
        }
        return null;
    }

    private List<String> getSelectedCodes(String group) {
        List<String> codes = new ArrayList<>();
        List<SelectableItem> items = controllers.get(group);
        if (items != null) {
            for (SelectableItem item : items) {
                if (item.selected && item.code != null) codes.add(item.code);
            }
        }
        return codes;
    }

    private void showLoadingDialog(String message) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            TextView tv = loadingDialog.findViewById(R.id.tvLoadingMessage);
            if (tv != null) tv.setText(message);
            return;
        }

        loadingDialog = new Dialog(requireContext());
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.dialog_loading_ai);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        TextView tv = loadingDialog.findViewById(R.id.tvLoadingMessage);
        if (tv != null) tv.setText(message);

        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void showSaveSuccessPopup() {
        if (!isAdded() || getContext() == null) return;
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
                
                // Chuyển hướng sang trang Kết quả phân tích theo tài liệu
                int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                        ? R.id.main_fragment_container : R.id.main;
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(containerId, new SkinAnalysisFragment())
                        .addToBackStack(null)
                        .commit();
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
        String code;
        boolean selected;
        SelectableItem(View view, ImageView icon, TextView label, ImageView tick, String code, boolean selected) {
            this.view = view;
            this.icon = icon;
            this.label = label;
            this.tick = tick;
            this.code = code;
            this.selected = selected;
        }
    }
}
