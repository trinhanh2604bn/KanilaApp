package com.example.frontend.feature.product;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.frontend.R;
import com.example.frontend.data.model.product.ProductDetailResponse;
import com.example.frontend.data.model.product.SkinMatchDto;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;
import java.util.Locale;

public class SkinMatchDetailBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_DATA = "arg_data";
    private static final String ARG_LEGACY_DATA = "arg_legacy_data";

    public static SkinMatchDetailBottomSheet newInstance(SkinMatchDto data) {
        SkinMatchDetailBottomSheet fragment = new SkinMatchDetailBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }
    
    public static SkinMatchDetailBottomSheet newInstance(ProductDetailResponse.SkinMatchDto legacyData) {
        SkinMatchDetailBottomSheet fragment = new SkinMatchDetailBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LEGACY_DATA, legacyData);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_skin_match_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        SkinMatchDto data = null;
        ProductDetailResponse.SkinMatchDto legacyData = null;
        
        if (getArguments() != null) {
            data = (SkinMatchDto) getArguments().getSerializable(ARG_DATA);
            legacyData = (ProductDetailResponse.SkinMatchDto) getArguments().getSerializable(ARG_LEGACY_DATA);
        }

        if (data == null && legacyData == null) {
            dismiss();
            return;
        }

        setupViews(view, data, legacyData);
    }

    private void setupViews(View view, SkinMatchDto data, ProductDetailResponse.SkinMatchDto legacyData) {
        view.findViewById(R.id.btnSkinMatchClose).setOnClickListener(v -> dismiss());

        TextView tvScore = view.findViewById(R.id.tvDetailScore);
        TextView tvLevel = view.findViewById(R.id.tvDetailLevel);
        TextView tvStatusDesc = view.findViewById(R.id.tvDetailStatusDesc);
        TextView tvMatchExplanation = view.findViewById(R.id.tvMatchExplanation);
        View viewScoreCircle = view.findViewById(R.id.viewScoreCircle);

        int scoreValue = 0;
        String levelName = "Phù hợp";
        int colorRes = R.color.button;
        String explanation = null;
        boolean isEstimated = false;

        if (data != null) {
            // Determine the display score: prefer actual score, fall back to estimated_score
            Integer actualScore = data.getScore();
            Integer estimatedScore = data.getEstimatedScore();
            isEstimated = data.isEstimated() || (actualScore == null || actualScore == 0) && estimatedScore != null && estimatedScore > 0;

            if (isEstimated && estimatedScore != null && estimatedScore > 0) {
                scoreValue = estimatedScore;
            } else {
                scoreValue = actualScore != null ? actualScore : 0;
            }

            explanation = data.getMatchExplanation();

            // Handle Status Description based on Integration Guide
            if (data.getStatus() != null) {
                switch (data.getStatus()) {
                    case PROFILE_REQUIRED:
                        tvStatusDesc.setText("Cần tạo hồ sơ làm đẹp để xem kết quả phân tích.");
                        break;
                    case PROFILE_INCOMPLETE:
                        if (isEstimated) {
                            tvStatusDesc.setText("Kết quả ước tính. Hoàn thiện hồ sơ để độ chính xác cao hơn.");
                        } else {
                            tvStatusDesc.setText("Hồ sơ chưa hoàn thiện. Cập nhật ngay để kết quả chính xác hơn.");
                        }
                        break;
                    case INSUFFICIENT_PRODUCT_DATA:
                        tvStatusDesc.setText("Chưa đủ dữ liệu sản phẩm để phân tích chi tiết.");
                        break;
                    default:
                        int indicators = data.getConfidenceScore() != null ? data.getConfidenceScore() : 24;
                        if (isEstimated) {
                            tvStatusDesc.setText(String.format(Locale.US, "Kết quả ước tính dựa trên %d chỉ số da (độ tin cậy %d%%).", indicators, indicators));
                        } else {
                            tvStatusDesc.setText(String.format(Locale.US, "Dựa trên phân tích %d chỉ số da của bạn.", indicators));
                        }
                        break;
                }
            }

            if (data.getMatchLevel() != null) {
                switch (data.getMatchLevel()) {
                    case EXCELLENT_MATCH:
                        levelName = "Phù hợp tuyệt vời";
                        colorRes = R.color.success;
                        break;
                    case GOOD_MATCH:
                        levelName = "Phù hợp tốt";
                        colorRes = R.color.success;
                        break;
                    case MODERATE_MATCH:
                        levelName = "Phù hợp trung bình";
                        colorRes = R.color.status_pending_text;
                        break;
                    case CAUTION:
                        // CAUTION with hard conflicts: use red; CAUTION without conflicts: use amber
                        boolean hasHardConflicts = data.getHardConflicts() != null && !data.getHardConflicts().isEmpty();
                        levelName = hasHardConflicts ? "Cần lưu ý" : "Thận trọng";
                        colorRes = hasHardConflicts ? R.color.error : R.color.status_pending_text;
                        break;
                    case INSUFFICIENT_DATA:
                        levelName = "Chưa đủ dữ liệu";
                        colorRes = R.color.text_secondary;
                        break;
                }
            }
        } else if (legacyData != null) {
            scoreValue = legacyData.getScore();
            levelName = legacyData.getLevel() != null ? legacyData.getLevel() : "Phù hợp";
            explanation = legacyData.getMatchExplanation();
            if (scoreValue >= 80) colorRes = R.color.success;
            else if (scoreValue >= 50) colorRes = R.color.status_pending_text;
            else colorRes = R.color.error;
        }

        android.util.Log.d("SkinMatchSheet", "Match Explanation: " + explanation + ", estimated: " + isEstimated + ", score: " + scoreValue);

        // Bind Match Explanation
        if (tvMatchExplanation != null) {
            if (explanation != null && !explanation.isEmpty()) {
                tvMatchExplanation.setText(explanation);
                tvMatchExplanation.setVisibility(View.VISIBLE);
            } else {
                tvMatchExplanation.setVisibility(View.GONE);
            }
        }

        // Show score: "?" for PROFILE_REQUIRED, "≈XX%" for estimated, "XX%" otherwise
        if (data != null && data.getStatus() == com.example.frontend.data.model.product.SkinMatchDto.Status.PROFILE_REQUIRED) {
            tvScore.setText("?");
        } else if (isEstimated && scoreValue > 0) {
            tvScore.setText(String.format(Locale.US, "≈%d%%", scoreValue));
        } else if (scoreValue > 0) {
            tvScore.setText(String.format(Locale.US, "%d%%", scoreValue));
        } else {
            tvScore.setText("?");
        }
        tvLevel.setText(levelName);
        tvLevel.setTextColor(ContextCompat.getColor(requireContext(), colorRes));
        viewScoreCircle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorRes)));

        // Matched Attributes (Chips)
        ChipGroup cgMatched = view.findViewById(R.id.cgMatchedAttributes);
        List<String> attributes = null;
        if (data != null) {
            attributes = data.getMatchedAttributes();
        } else if (legacyData != null) {
            attributes = legacyData.getProfileChips();
        }

        if (attributes != null && !attributes.isEmpty()) {
            cgMatched.removeAllViews();
            for (String attr : attributes) {
                Chip chip = new Chip(requireContext());
                chip.setText(attr);
                chip.setChipBackgroundColorResource(R.color.background_sub);
                chip.setChipStrokeWidth(1f);
                chip.setChipStrokeColorResource(R.color.border_divider);
                chip.setTextSize(12f);
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_main));
                cgMatched.addView(chip);
            }
            view.findViewById(R.id.tvMatchedTitle).setVisibility(View.VISIBLE);
            cgMatched.setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.tvMatchedTitle).setVisibility(View.GONE);
            cgMatched.setVisibility(View.GONE);
        }

        // Reasons Section
        LinearLayout layoutReasons = view.findViewById(R.id.layoutReasonsList);
        layoutReasons.removeAllViews();
        
        List<com.example.frontend.data.model.product.SkinMatchDto.Reason> reasons = null;
        if (data != null) reasons = data.getReasons();
        else if (legacyData != null) reasons = legacyData.getReasons();

        if (reasons != null && !reasons.isEmpty()) {
            view.findViewById(R.id.tvReasonsTitle).setVisibility(View.VISIBLE);
            layoutReasons.setVisibility(View.VISIBLE);
            for (com.example.frontend.data.model.product.SkinMatchDto.Reason reason : reasons) {
                addReasonItem(layoutReasons, reason.getText(), reason.getContribution(), R.drawable.ic_check_circle, R.color.success);
            }
        } else {
            view.findViewById(R.id.tvReasonsTitle).setVisibility(View.GONE);
            layoutReasons.setVisibility(View.GONE);
        }

        // Cautions Section
        LinearLayout layoutCautions = view.findViewById(R.id.layoutCautionsList);
        layoutCautions.removeAllViews();
        
        List<com.example.frontend.data.model.product.SkinMatchDto.Caution> cautions = null;
        if (data != null) cautions = data.getCautions();
        else if (legacyData != null) cautions = legacyData.getCautions();

        if (cautions != null && !cautions.isEmpty()) {
            view.findViewById(R.id.tvCautionsTitle).setVisibility(View.VISIBLE);
            layoutCautions.setVisibility(View.VISIBLE);
            for (com.example.frontend.data.model.product.SkinMatchDto.Caution caution : cautions) {
                int cautionColor = "HIGH".equalsIgnoreCase(caution.getSeverity()) ? R.color.error : R.color.status_pending_text;
                addReasonItem(layoutCautions, caution.getText(), null, R.drawable.ic_alert, cautionColor);
            }
        } else {
            view.findViewById(R.id.tvCautionsTitle).setVisibility(View.GONE);
            layoutCautions.setVisibility(View.GONE);
        }

        // Conflicts Section
        LinearLayout layoutConflicts = view.findViewById(R.id.layoutConflictsList);
        layoutConflicts.removeAllViews();

        List<com.example.frontend.data.model.product.SkinMatchDto.HardConflict> conflicts = null;
        if (data != null) conflicts = data.getHardConflicts();
        else if (legacyData != null) conflicts = legacyData.getHardConflicts();

        if (conflicts != null && !conflicts.isEmpty()) {
            view.findViewById(R.id.tvConflictsTitle).setVisibility(View.VISIBLE);
            layoutConflicts.setVisibility(View.VISIBLE);
            for (com.example.frontend.data.model.product.SkinMatchDto.HardConflict conflict : conflicts) {
                addReasonItem(layoutConflicts, conflict.getText(), null, R.drawable.ic_close, R.color.error);
            }
        } else {
            view.findViewById(R.id.tvConflictsTitle).setVisibility(View.GONE);
            layoutConflicts.setVisibility(View.GONE);
        }

        view.findViewById(R.id.btnUpdateProfile).setOnClickListener(v -> {
            // Updated to use appropriate activity for skin profile update
            startActivity(new Intent(requireContext(), ui.account.BeautyProfileActivity.class));
            dismiss();
        });
    }

    private void addReasonItem(LinearLayout container, String text, Integer contribution, int iconRes, int colorRes) {
        if (getContext() == null) return;
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_skin_match_detail_reason, container, false);
        ImageView ivIcon = itemView.findViewById(R.id.ivReasonIcon);
        TextView tvText = itemView.findViewById(R.id.tvReasonText);
        TextView tvDetail = itemView.findViewById(R.id.tvReasonDetail);
        TextView tvContribution = itemView.findViewById(R.id.tvReasonContribution);

        ivIcon.setImageResource(iconRes);
        ivIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorRes)));
        
        // Simple logic to separate title and detail if the text is long and contains a colon or similar
        if (text.contains(":")) {
            String[] parts = text.split(":", 2);
            tvText.setText(parts[0].trim());
            tvDetail.setText(parts[1].trim());
            tvDetail.setVisibility(View.VISIBLE);
        } else if (text.length() > 60) {
            tvText.setText(text);
            tvDetail.setVisibility(View.GONE);
        } else {
            tvText.setText(text);
            tvDetail.setVisibility(View.GONE);
        }

        if (contribution != null && contribution > 0) {
            tvContribution.setText(String.format(Locale.US, "+%d%%", contribution));
            tvContribution.setVisibility(View.VISIBLE);
        } else {
            tvContribution.setVisibility(View.GONE);
        }

        // Navigate to Kanila Beauty profile when clicking on a reason
        itemView.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ui.account.BeautyProfileActivity.class));
            dismiss();
        });

        container.addView(itemView);
    }
}
