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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import ui.common.ViewUtils;

public class EditSkinProfileFragment extends Fragment {

    private final List<SelectableItem> skinTypeGroup = new ArrayList<>();
    private final List<SelectableItem> skinToneGroup = new ArrayList<>();
    private final List<SelectableItem> budgetGroup = new ArrayList<>();
    private final List<SelectableItem> avoidIngredientsGroup = new ArrayList<>();

    private MaterialButton btnSaveBottom;
    private boolean isDirty = false;

    public EditSkinProfileFragment() {
        super(R.layout.fragment_edit_skin_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupEvents(view);
    }

    private void initViews(View view) {
        // 1. Skin Type Group (Single Choice)
        addSelectableItem(view, R.id.cardSkinOily, skinTypeGroup, "Da dầu", R.drawable.ic_drops_filled, true);
        addSelectableItem(view, R.id.cardSkinDry, skinTypeGroup, "Da khô", R.drawable.ic_drops, false);
        addSelectableItem(view, R.id.cardSkinMixed, skinTypeGroup, "Da hỗn hợp", R.drawable.ic_skin_mixed, false);
        addSelectableItem(view, R.id.cardSkinNormal, skinTypeGroup, "Da thường", R.drawable.ic_skin_normal, false);
        addSelectableItem(view, R.id.cardSkinSensitive, skinTypeGroup, "Da nhạy cảm", R.drawable.ic_skin_sensitive, false);
        addSelectableItem(view, R.id.cardSkinUnsure, skinTypeGroup, "Chưa chắc chắn", R.drawable.ic_unsure, false);

        // 2. Skin Tone Group (Single Choice)
        addSelectableItem(view, R.id.cardToneLight, skinToneGroup, "Sáng", R.drawable.ic_sun, true);
        addSelectableItem(view, R.id.cardToneMedium, skinToneGroup, "Trung bình", R.drawable.ic_sun, false);
        addSelectableItem(view, R.id.cardToneDark, skinToneGroup, "Ngăm", R.drawable.ic_sun, false);

        // 3. Budget Group (Single Choice)
        addSelectableItem(view, R.id.cardBudgetUnder300, budgetGroup, "Dưới 300K", R.drawable.ic_wallet, false);
        addSelectableItem(view, R.id.cardBudget300_500, budgetGroup, "300K - 500K", R.drawable.ic_wallet, true);
        addSelectableItem(view, R.id.cardBudgetOver500, budgetGroup, "500K +", R.drawable.ic_wallet, false);

        // 4. Avoid Ingredients Group (Multi Choice)
        addSelectableItem(view, R.id.cardAvoidFragrance, avoidIngredientsGroup, "Hương liệu", R.drawable.ic_drops, true);
        addSelectableItem(view, R.id.cardAvoidAlcohol, avoidIngredientsGroup, "Cồn khô", R.drawable.ic_beaker, false);
        addSelectableItem(view, R.id.cardAvoidEssentialOil, avoidIngredientsGroup, "Tinh dầu", R.drawable.ic_drops, false);
        addSelectableItem(view, R.id.cardAvoidParaben, avoidIngredientsGroup, "Paraben", R.drawable.ic_beaker, false);
        addSelectableItem(view, R.id.cardAvoidMineralOil, avoidIngredientsGroup, "Dầu khoáng", R.drawable.ic_drops, false);
        addSelectableItem(view, R.id.cardAvoidSilicone, avoidIngredientsGroup, "Silicone", R.drawable.ic_beaker, false);
        addSelectableItem(view, R.id.cardAvoidSulfate, avoidIngredientsGroup, "Sulfate", R.drawable.ic_beaker, false);
        addSelectableItem(view, R.id.cardAvoidLanolin, avoidIngredientsGroup, "Lanolin", R.drawable.ic_beaker, false);
        addSelectableItem(view, R.id.cardAvoidRetinoid, avoidIngredientsGroup, "Retinoid", R.drawable.ic_beaker, false);
        addSelectableItem(view, R.id.cardAvoidHighAcid, avoidIngredientsGroup, "Acid cao", R.drawable.ic_beaker, false);
    }

    private void addSelectableItem(View root, int id, List<SelectableItem> group, String label, int iconRes, boolean selected) {
        View itemRoot = root.findViewById(id);
        if (itemRoot instanceof MaterialCardView) {
            MaterialCardView card = (MaterialCardView) itemRoot;
            ImageView icon = itemRoot.findViewById(R.id.ivIcon);
            TextView tvLabel = itemRoot.findViewById(R.id.tvLabel);
            ImageView tick = itemRoot.findViewById(R.id.ivTick);

            icon.setImageResource(iconRes);
            tvLabel.setText(label);

            SelectableItem item = new SelectableItem(card, icon, tvLabel, tick, label, selected);
            group.add(item);
            updateItemUI(item);

            ViewUtils.applyClickAnimation(card);
            card.setOnClickListener(v -> {
                boolean isMultiChoice = (group == avoidIngredientsGroup);
                if (isMultiChoice) {
                    item.selected = !item.selected;
                    updateItemUI(item);
                } else {
                    if (!item.selected) {
                        for (SelectableItem i : group) {
                            if (i.selected) {
                                i.selected = false;
                                updateItemUI(i);
                            }
                        }
                        item.selected = true;
                        updateItemUI(item);
                    }
                }
                onDataChanged(); // Thông báo có thay đổi dữ liệu
            });
        }
    }

    private void updateItemUI(SelectableItem item) {
        int brandPink = ContextCompat.getColor(requireContext(), R.color.button);
        int softPinkBg = Color.parseColor("#FFF9FA");
        int darkText = ContextCompat.getColor(requireContext(), R.color.accent_dark);
        int grayBorder = ContextCompat.getColor(requireContext(), R.color.border_divider);

        if (item.selected) {
            animateCardTransition(item.card, Color.WHITE, softPinkBg, grayBorder, Color.TRANSPARENT, true);
            item.label.setTextColor(brandPink);
            item.icon.setImageTintList(ColorStateList.valueOf(brandPink));
            item.tick.setVisibility(View.VISIBLE);
            item.tick.setAlpha(0f);
            item.tick.setScaleX(0.7f);
            item.tick.setScaleY(0.7f);
            item.tick.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(300).start();
        } else {
            animateCardTransition(item.card, softPinkBg, Color.WHITE, Color.TRANSPARENT, grayBorder, false);
            item.label.setTextColor(darkText);
            item.icon.setImageTintList(ColorStateList.valueOf(darkText));
            item.tick.animate().alpha(0f).scaleX(0.7f).scaleY(0.7f).setDuration(200).withEndAction(() -> item.tick.setVisibility(View.GONE)).start();
        }
    }

    private void animateCardTransition(MaterialCardView card, int fromBg, int toBg, int fromStroke, int toStroke, boolean selected) {
        float scale = selected ? 1.05f : 1.0f;
        float elevation = selected ? dpToPx(4) : 0;
        
        card.animate()
                .scaleX(scale)
                .scaleY(scale)
                .translationZ(elevation)
                .setDuration(350)
                .setInterpolator(new androidx.interpolator.view.animation.FastOutSlowInInterpolator())
                .start();

        android.animation.ValueAnimator colorAnim = android.animation.ValueAnimator.ofFloat(0f, 1f);
        colorAnim.setDuration(350);
        android.animation.ArgbEvaluator evaluator = new android.animation.ArgbEvaluator();
        
        colorAnim.addUpdateListener(animation -> {
            float fraction = animation.getAnimatedFraction();
            int bg = (int) evaluator.evaluate(fraction, fromBg, toBg);
            int stroke = (int) evaluator.evaluate(fraction, fromStroke, toStroke);
            
            card.setCardBackgroundColor(bg);
            card.setStrokeColor(stroke);
            if (selected) {
                card.setStrokeWidth(Math.round(dpToPx(1) * (1 - fraction)));
            } else {
                card.setStrokeWidth(Math.round(dpToPx(1) * fraction));
            }
        });
        colorAnim.start();
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
        
        updateSaveButtonsState(false); // Ban đầu chưa có thay đổi
    }

    private void onDataChanged() {
        if (!isDirty) {
            updateSaveButtonsState(true);
        }
    }

    private void updateSaveButtonsState(boolean dirty) {
        this.isDirty = dirty;
        
        // Nút dưới vẫn giữ nguyên để người dùng có chỗ nhấn chính thức
        if (btnSaveBottom != null) {
            btnSaveBottom.setEnabled(dirty);
            btnSaveBottom.setAlpha(dirty ? 1.0f : 0.4f);
        }
    }

    private void saveProfile() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Lưu hồ sơ làn da")
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
        if (tvMessage != null) tvMessage.setText("Hồ sơ làn da của bạn đã được cập nhật thành công.");

        MaterialButton btnPopupOk = dialog.findViewById(R.id.btnPopupOk);
        if (btnPopupOk != null) {
            btnPopupOk.setOnClickListener(v -> {
                dialog.dismiss();
                updateSaveButtonsState(false); // Sau khi lưu, nút Save sẽ mờ đi
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
            window.setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void handleBackNavigation() {
        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            requireActivity()
                    .getOnBackPressedDispatcher()
                    .onBackPressed();
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private static class SelectableItem {
        MaterialCardView card;
        ImageView icon;
        TextView label;
        ImageView tick;
        String name;
        boolean selected;

        SelectableItem(MaterialCardView card, ImageView icon, TextView label, ImageView tick, String name, boolean selected) {
            this.card = card;
            this.icon = icon;
            this.label = label;
            this.tick = tick;
            this.name = name;
            this.selected = selected;
        }
    }
}
