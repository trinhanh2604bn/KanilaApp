package com.example.frontend.feature.product;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.product.adapter.ReviewAdapter;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviewListFragment extends Fragment {
    private static final String TAG = "ReviewList";
    private static final String ARG_PRODUCT_ID = "product_id";
    
    private String productId;
    private ReviewViewModel viewModel;
    private ReviewAdapter reviewAdapter;
    private ChipGroup cgReviewFilters;
    private RecyclerView rvReviews;
    private View layoutEmpty;
    private String currentFilter = "all";

    public static ReviewListFragment newInstance(String productId) {
        ReviewListFragment fragment = new ReviewListFragment();
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
        Log.d(TAG, "onCreate productId = " + productId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
        
        initViews(view);
        setupFilterChips();
        observeViewModel();
        
        if (productId != null) {
            loadReviews("all");
        } else {
            Toast.makeText(getContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews(View view) {
        cgReviewFilters = view.findViewById(R.id.cgReviewFilters);
        rvReviews = view.findViewById(R.id.rvReviews);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        
        rvReviews.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        reviewAdapter = new ReviewAdapter();
        reviewAdapter.setOnReviewLikeListener(review -> {
            viewModel.toggleReviewVote(review.getId());
        });
        rvReviews.setAdapter(reviewAdapter);

        if (layoutEmpty != null) {
            View btnAction = layoutEmpty.findViewById(R.id.btnEmptyAction);
            if (btnAction != null) {
                btnAction.setOnClickListener(v -> {
                    // Reset to "All" filter
                    if (cgReviewFilters != null) {
                        cgReviewFilters.check(R.id.chipReviewAll);
                    }
                });
            }
        }
    }

    private void setupFilterChips() {
        if (cgReviewFilters == null) return;
        cgReviewFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds == null || checkedIds.isEmpty()) {
                currentFilter = "all";
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chipReviewFiveStar) {
                    currentFilter = "five_star";
                } else if (checkedId == R.id.chipReviewFourStar) {
                    currentFilter = "four_star";
                } else if (checkedId == R.id.chipReviewThreeStar) {
                    currentFilter = "three_star";
                } else if (checkedId == R.id.chipReviewTwoStar) {
                    currentFilter = "two_star";
                } else if (checkedId == R.id.chipReviewOneStar) {
                    currentFilter = "one_star";
                } else if (checkedId == R.id.chipReviewHasMedia) {
                    currentFilter = "has_media";
                } else if (checkedId == R.id.chipReviewAll) {
                    currentFilter = "all";
                }
            }
            loadReviews(currentFilter);
        });
    }

    private void loadReviews(String filterType) {
        if (productId == null || productId.trim().isEmpty()) return;

        Map<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("limit", "20");
        query.put("sort", "newest");

        int emptyTextRes = R.string.review_empty_all;

        if ("five_star".equals(filterType)) {
            query.put("rating", "5");
            emptyTextRes = R.string.review_empty_5_star;
        } else if ("four_star".equals(filterType)) {
            query.put("rating", "4");
            emptyTextRes = R.string.review_empty_4_star;
        } else if ("three_star".equals(filterType)) {
            query.put("rating", "3");
            emptyTextRes = R.string.review_empty_3_star;
        } else if ("two_star".equals(filterType)) {
            query.put("rating", "2");
            emptyTextRes = R.string.review_empty_2_star;
        } else if ("one_star".equals(filterType)) {
            query.put("rating", "1");
            emptyTextRes = R.string.review_empty_1_star;
        } else if ("has_media".equals(filterType)) {
            query.put("hasMedia", "true");
            emptyTextRes = R.string.review_empty_has_media;
        }

        if (layoutEmpty != null) {
            View header = layoutEmpty.findViewById(R.id.layoutEmptyHeader);
            if (header != null) header.setVisibility(View.GONE);

            android.widget.TextView tvTitle = layoutEmpty.findViewById(R.id.tvEmptyTitle);
            android.widget.TextView tvDesc = layoutEmpty.findViewById(R.id.tvEmptyDescription);
            android.widget.Button btnAction = layoutEmpty.findViewById(R.id.btnEmptyAction);

            if (tvTitle != null) tvTitle.setText("Chưa có đánh giá");
            if (tvDesc != null) tvDesc.setText(getString(emptyTextRes));
            if (btnAction != null) {
                btnAction.setText("Xem tất cả đánh giá");
                btnAction.setVisibility("all".equals(filterType) ? View.GONE : View.VISIBLE);
            }
        }

        Log.d(TAG, "load reviews productId = " + productId + ", query = " + query);
        viewModel.loadReviews(productId, query);
    }

    private void observeViewModel() {
        viewModel.getReviewsResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            
            Log.d(TAG, "status = " + result.status 
                    + ", size = " + (result.data == null ? "null" : result.data.size()) 
                    + ", message = " + result.message);
            
            switch (result.status) {
                case SUCCESS:
                    boolean isEmpty = result.data == null || result.data.isEmpty();
                    reviewAdapter.submitList(result.data != null ? result.data : new ArrayList<>());
                    if (layoutEmpty != null) layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                    if (rvReviews != null) rvReviews.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                    break;
                case ERROR:
                    if (layoutEmpty != null) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        android.widget.TextView tvDesc = layoutEmpty.findViewById(R.id.tvEmptyDescription);
                        if (tvDesc != null) tvDesc.setText(result.message != null ? result.message : "Không tải được đánh giá");
                    }
                    if (rvReviews != null) rvReviews.setVisibility(View.GONE);
                    Toast.makeText(getContext(), result.message != null ? result.message : "Không tải được đánh giá", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        viewModel.getVoteResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                // Refresh reviews to get updated like status and count
                loadReviews(currentFilter);
            } else if (result.status == NetworkResult.Status.ERROR) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
