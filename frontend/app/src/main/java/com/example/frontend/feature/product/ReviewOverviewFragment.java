package com.example.frontend.feature.product;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.frontend.R;
import com.example.frontend.data.model.review.ReviewSummaryDto;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.Locale;
import java.util.Map;

public class ReviewOverviewFragment extends Fragment {
    private static final String TAG = "ReviewOverview";
    private static final String ARG_PRODUCT_ID = "product_id";
    
    private String productId;
    private ReviewViewModel viewModel;
    
    private TextView tvAverageRating, tvReviewCount, tvAiSummary;
    private RatingBar rbStars;
    private ChipGroup cgKeywords;
    private ProgressBar pb5, pb4, pb3, pb2, pb1;
    private TextView tv5, tv4, tv3, tv2, tv1;

    public static ReviewOverviewFragment newInstance(String productId) {
        ReviewOverviewFragment fragment = new ReviewOverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString(ARG_PRODUCT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
        
        initViews(view);
        observeViewModel();
        
        if (productId != null) {
            viewModel.loadReviewSummary(productId);
        }
    }

    private void initViews(View view) {
        View layoutSummary = view.findViewById(R.id.layoutRatingSummary);
        tvAverageRating = layoutSummary.findViewById(R.id.tvAverageRating);
        tvReviewCount = layoutSummary.findViewById(R.id.tvReviewCount);
        rbStars = layoutSummary.findViewById(R.id.tvRatingStars);
        
        pb5 = layoutSummary.findViewById(R.id.progressRating5);
        pb4 = layoutSummary.findViewById(R.id.progressRating4);
        pb3 = layoutSummary.findViewById(R.id.progressRating3);
        pb2 = layoutSummary.findViewById(R.id.progressRating2);
        pb1 = layoutSummary.findViewById(R.id.progressRating1);
        
        tv5 = layoutSummary.findViewById(R.id.tvRating5Percent);
        tv4 = layoutSummary.findViewById(R.id.tvRating4Percent);
        tv3 = layoutSummary.findViewById(R.id.tvRating3Percent);
        tv2 = layoutSummary.findViewById(R.id.tvRating2Percent);
        tv1 = layoutSummary.findViewById(R.id.tvRating1Percent);
        
        tvAiSummary = view.findViewById(R.id.tvReviewAiSummary);
        cgKeywords = view.findViewById(R.id.cgReviewKeywords);
    }

    private void observeViewModel() {
        viewModel.getSummaryResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null || result.data == null) return;
            
            Log.d(TAG, "summary status = " + result.status);
            if (result.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS) {
                bindSummaryData(result.data);
            }
        });
    }

    private void bindSummaryData(ReviewSummaryDto summary) {
        tvAverageRating.setText(String.format(Locale.US, "%.1f", summary.getAverageRating()));
        tvReviewCount.setText(String.format(Locale.US, "(%d đánh giá)", summary.getReviewCount()));
        rbStars.setRating((float) summary.getAverageRating());
        
        if (summary.getAiSummary() != null) {
            tvAiSummary.setText(summary.getAiSummary());
        } else {
            tvAiSummary.setText("Chưa có tóm tắt đánh giá");
        }
        
        bindRatingDistribution(summary.getRatingDistribution(), summary.getReviewCount());
        bindKeywords(summary.getKeywords());
    }

    private void bindRatingDistribution(Map<String, Integer> dist, int total) {
        if (dist == null || total == 0) return;
        
        updateRow(pb5, tv5, dist.getOrDefault("5", 0), total);
        updateRow(pb4, tv4, dist.getOrDefault("4", 0), total);
        updateRow(pb3, tv3, dist.getOrDefault("3", 0), total);
        updateRow(pb2, tv2, dist.getOrDefault("2", 0), total);
        updateRow(pb1, tv1, dist.getOrDefault("1", 0), total);
    }

    private void updateRow(ProgressBar pb, TextView tv, Integer count, int total) {
        if (count == null) count = 0;
        int percent = (int) ((count * 100.0) / total);
        pb.setProgress(percent);
        tv.setText(String.format(Locale.US, "%d%%", percent));
    }

    private void bindKeywords(java.util.List<String> keywords) {
        if (cgKeywords == null) return;
        cgKeywords.removeAllViews();
        if (keywords == null) return;
        
        for (String keyword : keywords) {
            Chip chip = new Chip(getContext());
            chip.setText(keyword);
            chip.setChipBackgroundColorResource(R.color.background_sub);
            chip.setChipStrokeWidth(0f);
            cgKeywords.addView(chip);
        }
    }
}
