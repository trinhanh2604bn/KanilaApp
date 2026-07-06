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
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import ui.common.ViewUtils;

public class SkinConcernsGoalsFragment extends Fragment {

    private final List<SelectableItem> selectableItems = new ArrayList<>();
    private MaterialButton btnSaveBottom;
    private boolean isDirty = false;

    public SkinConcernsGoalsFragment() {
        super(R.layout.fragment_skin_concerns_goals);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupEvents(view);
        updateSaveButtonsState(false);
    }

    private void initViews(View view) {
        selectableItems.clear();

        // 1. Skin Concerns
        addSelectableItem(view, R.id.cardConcernAcne, R.id.iconAcne, R.id.labelAcne, R.id.tickAcne, true);
        addSelectableItem(view, R.id.cardConcernSpots, R.id.iconSpots, R.id.labelSpots, R.id.tickSpots, true);
        addSelectableItem(view, R.id.cardConcernPores, R.id.iconPores, R.id.labelPores, R.id.tickPores, false);
        addSelectableItem(view, R.id.cardConcernRedness, R.id.iconRedness, R.id.labelRedness, R.id.tickRedness, false);
        addSelectableItem(view, R.id.cardConcernDullness, R.id.iconDullness, R.id.labelDullness, R.id.tickDullness, false);
        addSelectableItem(view, R.id.cardConcernDehydration, R.id.iconDehydration, R.id.labelDehydration, R.id.tickDehydration, true);
        addSelectableItem(view, R.id.cardConcernAging, R.id.iconAging, R.id.labelAging, R.id.tickAging, false);
        addSelectableItem(view, R.id.cardConcernSensitive, R.id.iconSensitive, R.id.labelSensitive, R.id.tickSensitive, false);

        // 2. Care Goals
        addSelectableItem(view, R.id.cardGoalReduceAcne, R.id.iconGoalAcne, R.id.labelGoalAcne, R.id.tickGoalAcne, true);
        addSelectableItem(view, R.id.cardGoalFadeSpots, R.id.iconGoalSpots, R.id.labelGoalSpots, R.id.tickGoalSpots, true);
        addSelectableItem(view, R.id.cardGoalRecovery, R.id.iconGoalRecovery, R.id.labelGoalRecovery, R.id.tickGoalRecovery, false);
        addSelectableItem(view, R.id.cardGoalHydrate, R.id.iconGoalHydrate, R.id.labelGoalHydrate, R.id.tickGoalHydrate, true);
        addSelectableItem(view, R.id.cardGoalBrighten, R.id.iconGoalBrighten, R.id.labelGoalBrighten, R.id.tickGoalBrighten, false);
        addSelectableItem(view, R.id.cardGoalSun, R.id.iconGoalSun, R.id.labelGoalSun, R.id.tickGoalSun, false);
    }

    private void addSelectableItem(View root, int cardId, int iconId, int labelId, int tickId, boolean selected) {
        MaterialCardView card = root.findViewById(cardId);
        ImageView icon = root.findViewById(iconId);
        TextView label = root.findViewById(labelId);
        ImageView tick = root.findViewById(tickId);

        if (card == null) return;

        SelectableItem item = new SelectableItem(card, icon, label, tick, selected);
        selectableItems.add(item);
        updateItemUI(item, false);

        ViewUtils.applyClickAnimation(card);
        card.setOnClickListener(v -> {
            item.selected = !item.selected;
            updateItemUI(item, true);
            onDataChanged();
        });
    }

    private void updateItemUI(SelectableItem item, boolean animate) {
        int pinkColor = ContextCompat.getColor(requireContext(), R.color.button);
        int softPinkBg = Color.parseColor("#FFF0F3");
        int normalTextColor = ContextCompat.getColor(requireContext(), R.color.text_main);
        int borderDivider = ContextCompat.getColor(requireContext(), R.color.border_divider);

        if (item.selected) {
            item.card.setStrokeColor(pinkColor);
            item.card.setStrokeWidth(dpToPx(1.5f));
            item.card.setCardBackgroundColor(softPinkBg);
            if (item.label != null) item.label.setTextColor(normalTextColor);
            if (item.tick != null) {
                item.tick.setVisibility(View.VISIBLE);
                if (animate) {
                    item.tick.setAlpha(0f);
                    item.tick.setScaleX(0.5f);
                    item.tick.setScaleY(0.5f);
                    item.tick.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(200).start();
                }
            }
        } else {
            item.card.setStrokeColor(borderDivider);
            item.card.setStrokeWidth(dpToPx(1));
            item.card.setCardBackgroundColor(Color.WHITE);
            if (item.label != null) item.label.setTextColor(normalTextColor);
            if (item.tick != null) item.tick.setVisibility(View.GONE);
        }
    }

    private void setupEvents(View view) {
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            ViewUtils.applyClickAnimation(btnBack);
            btnBack.setOnClickListener(v -> handleBackNavigation());
        }

        btnSaveBottom = view.findViewById(R.id.btnSaveProfile);
        if (btnSaveBottom != null) {
            ViewUtils.applyClickAnimation(btnSaveBottom);
            btnSaveBottom.setOnClickListener(v -> {
                if (isDirty) saveProfile();
            });
        }

        MaterialButton btnViewRoutine = view.findViewById(R.id.btnViewRoutine);
        if (btnViewRoutine != null) {
            ViewUtils.applyClickAnimation(btnViewRoutine);
            btnViewRoutine.setOnClickListener(v -> openAnalysis());
        }
    }

    private void onDataChanged() {
        if (!isDirty) {
            updateSaveButtonsState(true);
        }
    }

    private void updateSaveButtonsState(boolean dirty) {
        this.isDirty = dirty;
        if (btnSaveBottom != null) {
            btnSaveBottom.setEnabled(dirty);
            btnSaveBottom.setAlpha(dirty ? 1.0f : 0.4f);
        }
    }

    private void saveProfile() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Lưu hồ sơ làm đẹp")
                .setMessage("Bạn có muốn lưu các thay đổi này vào hồ sơ của mình không?")
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Lưu", (dialog, which) -> {
                    dialog.dismiss();
                    showSaveSuccessPopup();
                })
                .show();
    }

    private void showSaveSuccessPopup() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.overview_popup);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        TextView tvTitle = dialog.findViewById(R.id.tvPopupTitle);
        TextView tvMessage = dialog.findViewById(R.id.tvPopupMessage);
        
        if (tvTitle != null) tvTitle.setText("Lưu thành công!");
        if (tvMessage != null) tvMessage.setText("Mối quan tâm và mục tiêu của bạn đã được cập nhật.");

        MaterialButton btnPopupOk = dialog.findViewById(R.id.btnPopupOk);
        if (btnPopupOk != null) {
            btnPopupOk.setOnClickListener(v -> {
                dialog.dismiss();
                updateSaveButtonsState(false);
            });
        }

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.dimAmount = 0.5f;
            window.setAttributes(layoutParams);
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private void openAnalysis() {
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main, new SkinAnalysisFragment())
                .addToBackStack(null)
                .commit();
    }

    private void handleBackNavigation() {
        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    }

    private int dpToPx(float dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private static class SelectableItem {
        MaterialCardView card;
        ImageView icon;
        TextView label;
        ImageView tick;
        boolean selected;

        SelectableItem(MaterialCardView card, ImageView icon, TextView label, ImageView tick, boolean selected) {
            this.card = card;
            this.icon = icon;
            this.label = label;
            this.tick = tick;
            this.selected = selected;
        }
    }
}
