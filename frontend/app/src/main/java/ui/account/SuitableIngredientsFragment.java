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

import ui.common.ViewUtils;

public class SuitableIngredientsFragment extends Fragment {

    private final List<IngredientItem> priorityItems = new ArrayList<>();
    private final List<IngredientItem> avoidItems = new ArrayList<>();

    private TextView tvPriorityCount;
    private TextView tvPriorityList;
    private TextView tvAvoidCount;
    private TextView tvAvoidList;

    private MaterialCardView layoutSensitiveTreatment;
    private SwitchMaterial switchSensitiveTreatment;

    private MaterialButton btnSaveBottom;
    private boolean isDirty = false;

    private MaterialCardView cardSmartInsight;
    private View layoutSmartInsightIcon;
    private ImageView ivSmartInsightIcon;
    private TextView tvSmartInsightMessage;

    public SuitableIngredientsFragment() {
        super(R.layout.fragment_suitable_ingredients);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupEvents(view);
        updateAllIngredientStates();
        updateSensitiveTreatmentState(switchSensitiveTreatment != null && switchSensitiveTreatment.isChecked());
    }

    private void setupViews(@NonNull View view) {
        priorityItems.clear();
        avoidItems.clear();

        tvPriorityCount = view.findViewById(R.id.tvPriorityCount);
        tvPriorityList = view.findViewById(R.id.tvPriorityList);
        tvAvoidCount = view.findViewById(R.id.tvAvoidCount);
        tvAvoidList = view.findViewById(R.id.tvAvoidList);

        layoutSensitiveTreatment = view.findViewById(R.id.layoutSensitiveTreatment);
        switchSensitiveTreatment = view.findViewById(R.id.switchSensitiveTreatment);
        cardSmartInsight = view.findViewById(R.id.cardSmartInsight);
        layoutSmartInsightIcon = view.findViewById(R.id.layoutSmartInsightIcon);
        ivSmartInsightIcon = view.findViewById(R.id.ivSmartInsightIcon);
        tvSmartInsightMessage = view.findViewById(R.id.tvSmartInsightMessage);

        priorityItems.add(createIngredientItem(view, R.id.cardNiacinamide, R.id.iconNiacinamide, R.id.tvNiacinamide, R.id.tickNiacinamide, "Niacinamide", true));
        priorityItems.add(createIngredientItem(view, R.id.cardCeramide, R.id.iconCeramide, R.id.tvCeramide, R.id.tickCeramide, "Ceramide", true));
        priorityItems.add(createIngredientItem(view, R.id.cardHyaluronicAcid, R.id.iconHyaluronicAcid, R.id.tvHyaluronicAcid, R.id.tickHyaluronicAcid, "Hyaluronic Acid", true));
        priorityItems.add(createIngredientItem(view, R.id.cardBha, R.id.iconBha, R.id.tvBha, R.id.tickBha, "BHA", false));
        priorityItems.add(createIngredientItem(view, R.id.cardVitaminC, R.id.iconVitaminC, R.id.tvVitaminC, R.id.tickVitaminC, "Vitamin C", false));
        priorityItems.add(createIngredientItem(view, R.id.cardPeptide, R.id.iconPeptide, R.id.tvPeptide, R.id.tickPeptide, "Peptide", false));

        avoidItems.add(createIngredientItem(view, R.id.cardAlcohol, R.id.iconAlcohol, R.id.tvAlcohol, R.id.tickAlcohol, "Cồn khô", true));
        avoidItems.add(createIngredientItem(view, R.id.cardFragrance, R.id.iconFragrance, R.id.tvFragrance, R.id.tickFragrance, "Hương liệu", true));
        avoidItems.add(createIngredientItem(view, R.id.cardParaben, R.id.iconParaben, R.id.tvParaben, R.id.tickParaben, "Paraben", false));
        avoidItems.add(createIngredientItem(view, R.id.cardRetinol, R.id.iconRetinol, R.id.tvRetinol, R.id.tickRetinol, "Retinol", false));
        avoidItems.add(createIngredientItem(view, R.id.cardEssentialOil, R.id.iconEssentialOil, R.id.tvEssentialOil, R.id.tickEssentialOil, "Tinh dầu", false));
    }

    private IngredientItem createIngredientItem(@NonNull View root, int cardId, int iconId, int labelId, int tickId, @NonNull String name, boolean selected) {
        MaterialCardView card = root.findViewById(cardId);
        ImageView icon = root.findViewById(iconId);
        TextView label = root.findViewById(labelId);
        ImageView tick = root.findViewById(tickId);
        return new IngredientItem(card, icon, label, tick, name, selected);
    }

    private void setupEvents(@NonNull View view) {
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            ViewUtils.applyClickAnimation(btnBack);
            btnBack.setOnClickListener(v -> handleBackNavigation());
        }

        btnSaveBottom = view.findViewById(R.id.btnSave);
        if (btnSaveBottom != null) {
            ViewUtils.applyClickAnimation(btnSaveBottom);
            btnSaveBottom.setOnClickListener(v -> {
                if (isDirty) showSaveConfirmDialog();
            });
        }

        setupIngredientClickEvents(priorityItems);
        setupIngredientClickEvents(avoidItems);

        if (switchSensitiveTreatment != null) {
            switchSensitiveTreatment.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateSensitiveTreatmentState(isChecked);
                onDataChanged();
            });
        }

        if (layoutSensitiveTreatment != null && switchSensitiveTreatment != null) {
            layoutSensitiveTreatment.setOnClickListener(v -> switchSensitiveTreatment.toggle());
        }
        
        updateSaveButtonsState(false);
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

    private void setupIngredientClickEvents(@NonNull List<IngredientItem> items) {
        for (IngredientItem item : items) {
            if (item.card == null) continue;
            
            // Đồng bộ hiệu ứng click giống các màn hình khác
            ViewUtils.applyClickAnimation(item.card);

            item.card.setOnClickListener(v -> {
                item.selected = !item.selected;
                updateIngredientView(item);
                updateSummary();
                onDataChanged();
            });
        }
    }

    private void updateAllIngredientStates() {
        for (IngredientItem item : priorityItems) updateIngredientView(item);
        for (IngredientItem item : avoidItems) updateIngredientView(item);
        updateSummary();
    }

    private void updateIngredientView(@NonNull IngredientItem item) {
        if (item.card == null || item.icon == null || item.label == null || item.tick == null) return;

        int brandPink = ContextCompat.getColor(requireContext(), R.color.button);
        int softPinkBg = Color.parseColor("#FFF9FA");
        int darkText = ContextCompat.getColor(requireContext(), R.color.accent_dark);
        int grayBorder = ContextCompat.getColor(requireContext(), R.color.border_divider);

        if (item.selected) {
            animateCardTransition(item.card, Color.WHITE, softPinkBg, grayBorder, Color.TRANSPARENT, true);
            item.label.setTextColor(brandPink);
            ImageViewCompat.setImageTintList(item.icon, ColorStateList.valueOf(brandPink));
            item.tick.setVisibility(View.VISIBLE);
            item.tick.setAlpha(0f);
            item.tick.setScaleX(0.7f);
            item.tick.setScaleY(0.7f);
            item.tick.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(300).start();
        } else {
            animateCardTransition(item.card, softPinkBg, Color.WHITE, Color.TRANSPARENT, grayBorder, false);
            item.label.setTextColor(darkText);
            ImageViewCompat.setImageTintList(item.icon, ColorStateList.valueOf(darkText));
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
        colorAnim.setInterpolator(new androidx.interpolator.view.animation.FastOutSlowInInterpolator());
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

    private void updateSummary() {
        int priorityCount = countSelected(priorityItems);
        int avoidCount = countSelected(avoidItems);

        if (tvPriorityCount != null) tvPriorityCount.setText(priorityCount + " thành phần");
        if (tvPriorityList != null) tvPriorityList.setText(getSelectedNames(priorityItems));
        if (tvAvoidCount != null) tvAvoidCount.setText(avoidCount + " thành phần");
        if (tvAvoidList != null) tvAvoidList.setText(getSelectedNames(avoidItems));
    }

    private int countSelected(@NonNull List<IngredientItem> items) {
        int count = 0;
        for (IngredientItem item : items) {
            if (item.selected) count++;
        }
        return count;
    }

    @NonNull
    private String getSelectedNames(@NonNull List<IngredientItem> items) {
        StringBuilder result = new StringBuilder();
        for (IngredientItem item : items) {
            if (!item.selected) continue;
            if (result.length() > 0) result.append(", ");
            result.append(item.name);
        }
        return result.length() == 0 ? "Chưa chọn" : result.toString();
    }

    private void updateSensitiveTreatmentState(boolean isChecked) {
        if (layoutSensitiveTreatment == null || switchSensitiveTreatment == null) return;
        
        int pinkColor = ContextCompat.getColor(requireContext(), R.color.button);
        int softPinkBg = Color.parseColor("#FFF9FA");
        int grayBorder = ContextCompat.getColor(requireContext(), R.color.border_divider);
        int darkText = ContextCompat.getColor(requireContext(), R.color.accent_dark);
        int tertiaryText = ContextCompat.getColor(requireContext(), R.color.text_tertiary);

        if (isChecked) {
            // Trạng thái ON: Rực rỡ và Kích hoạt
            layoutSensitiveTreatment.setCardBackgroundColor(softPinkBg);
            layoutSensitiveTreatment.setStrokeColor(pinkColor);
            switchSensitiveTreatment.setThumbTintList(ColorStateList.valueOf(pinkColor));
            switchSensitiveTreatment.setTrackTintList(ColorStateList.valueOf(ColorUtils.setAlphaComponent(pinkColor, 80)));
            
            // TỰ ĐỘNG THIẾT LẬP: Chuyển các thành phần nhạy cảm vào danh sách Cần tránh
            boolean changed = false;
            for (IngredientItem item : avoidItems) {
                if (item.name.equals("Cồn khô") || item.name.equals("Hương liệu") || item.name.equals("Retinol")) {
                    if (!item.selected) {
                        item.selected = true;
                        updateIngredientView(item);
                        changed = true;
                    }
                }
            }
            if (changed) updateSummary();

            // Làm thẻ Insight "bừng sáng"
            if (cardSmartInsight != null) {
                cardSmartInsight.setCardBackgroundColor(Color.parseColor("#FDF9FB"));
                cardSmartInsight.setStrokeColor(Color.parseColor("#FFD6DE")); 
                if (layoutSmartInsightIcon != null) layoutSmartInsightIcon.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                if (ivSmartInsightIcon != null) {
                    ivSmartInsightIcon.setImageResource(R.drawable.ic_shield_star); 
                    ivSmartInsightIcon.setImageTintList(ColorStateList.valueOf(pinkColor));
                }
                if (tvSmartInsightMessage != null) {
                    tvSmartInsightMessage.setText("Chế độ bảo vệ: Tự động loại bỏ các sản phẩm chứa Cồn khô, Hương liệu và hoạt chất nồng độ cao.");
                    tvSmartInsightMessage.setTextColor(darkText);
                    tvSmartInsightMessage.animate().alpha(1f).setDuration(300).start();
                }
            }
        } else {
            // Trạng thái OFF: Trầm lắng và Giải thích
            layoutSensitiveTreatment.setCardBackgroundColor(Color.WHITE);
            layoutSensitiveTreatment.setStrokeColor(grayBorder);
            switchSensitiveTreatment.setThumbTintList(ColorStateList.valueOf(grayBorder));
            switchSensitiveTreatment.setTrackTintList(ColorStateList.valueOf(ColorUtils.setAlphaComponent(grayBorder, 80)));
            
            // Làm thẻ Insight mờ đi (Trạng thái chờ)
            if (cardSmartInsight != null) {
                cardSmartInsight.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                cardSmartInsight.setStrokeColor(grayBorder);
                if (layoutSmartInsightIcon != null) layoutSmartInsightIcon.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                if (ivSmartInsightIcon != null) {
                    ivSmartInsightIcon.setImageResource(R.drawable.ic_goal_sparkle);
                    ivSmartInsightIcon.setImageTintList(ColorStateList.valueOf(tertiaryText));
                }
                if (tvSmartInsightMessage != null) {
                    tvSmartInsightMessage.setText("Bật chế độ này để AI tự động ưu tiên các sản phẩm lành tính và phục hồi cho da đang treatment.");
                    tvSmartInsightMessage.setTextColor(tertiaryText);
                    tvSmartInsightMessage.setAlpha(0.6f);
                }
            }
        }
    }

    private void handleBackNavigation() {
        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    }

    private void showSaveConfirmDialog() {
        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xác nhận lưu")
                .setMessage("Bạn có chắc chắn muốn lưu thiết lập thành phần này không?")
                .setNegativeButton("Hủy", (d, which) -> d.dismiss())
                .setPositiveButton("Lưu", (d, which) -> {
                    d.dismiss();
                    showSaveSuccessPopup();
                })
                .show();
        ViewUtils.customizeDialogButtons(dialog);
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
        if (tvMessage != null) tvMessage.setText("Thiết lập thành phần của bạn đã được cập nhật.");

        MaterialButton btnPopupOk = dialog.findViewById(R.id.btnPopupOk);
        if (btnPopupOk != null) {
            btnPopupOk.setOnClickListener(v -> {
                dialog.dismiss();
                updateSaveButtonsState(false); // Làm mờ nút sau khi lưu thành công
            });
        }
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.5f;
            window.setAttributes(params);
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private static class IngredientItem {
        private final MaterialCardView card;
        private final ImageView icon;
        private final TextView label;
        private final ImageView tick;
        private final String name;
        private boolean selected;

        private IngredientItem(MaterialCardView card, ImageView icon, TextView label, ImageView tick, String name, boolean selected) {
            this.card = card;
            this.icon = icon;
            this.label = label;
            this.tick = tick;
            this.name = name;
            this.selected = selected;
        }
    }
}
