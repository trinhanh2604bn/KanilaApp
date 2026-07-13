package com.example.frontend.feature.product;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.widget.EditText;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ui.common.FragmentNavigationHelper;
import ui.order.MyReviewAdapter;
import ui.order.MyReviewsViewModel;
import ui.order.ReviewDetailFragment;

public class ReviewListFragment extends Fragment {

    private static final String TAG = "ReviewList";
    private static final String ARG_PRODUCT_ID = "product_id";

    private String productId;
    
    // ViewModels for different modes
    private ReviewViewModel productViewModel;
    private MyReviewsViewModel myReviewsViewModel;
    
    // Adapters for different modes
    private ReviewAdapter reviewAdapter; // for Product Reviews
    private MyReviewAdapter myReviewAdapter; // for My Reviews
    
    private ChipGroup cgReviewFilters;
    private RecyclerView rvReviews;
    private View layoutEmpty;
    private View scrollFilters;
    private TextView tvMyReviewsTitle;
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
        initViews(view);

        if (productId != null) {
            setupProductReviews(view);
        } else {
            setupMyReviews(view);
        }
    }

    private void initViews(View view) {
        cgReviewFilters = view.findViewById(R.id.cgReviewFilters);
        rvReviews = view.findViewById(R.id.rvReviews);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        scrollFilters = view.findViewById(R.id.scrollFilters);
        tvMyReviewsTitle = view.findViewById(R.id.tvMyReviewsTitle);

        rvReviews.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

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

    private void setupProductReviews(View view) {
        if (tvMyReviewsTitle != null) tvMyReviewsTitle.setVisibility(View.GONE);
        if (scrollFilters != null) scrollFilters.setVisibility(View.VISIBLE);

        reviewAdapter = new ReviewAdapter();
        reviewAdapter.setOnReviewLikeListener(review -> {
            if (com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
                if (productViewModel != null) {
                    productViewModel.toggleReviewVote(review.getId());
                }
            } else {
                com.example.frontend.feature.auth.GuestPromptBottomSheet.newInstance(
                        com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION
                ).show(getChildFragmentManager(), "GuestPromptBottomSheet");
            }
        });
        reviewAdapter.setOnReviewReplyListener(this::showReplyDialog);
        rvReviews.setAdapter(reviewAdapter);

        productViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
        setupFilterChips();
        observeProductViewModel();
        loadProductReviews("all");
    }

    private void setupMyReviews(View view) {
        if (tvMyReviewsTitle != null) tvMyReviewsTitle.setVisibility(View.VISIBLE);
        if (scrollFilters != null) scrollFilters.setVisibility(View.GONE);

        myReviewAdapter = new MyReviewAdapter(review -> 
            FragmentNavigationHelper.replaceFragment(requireActivity(), ReviewDetailFragment.newInstance(review.getReviewId())));
        rvReviews.setAdapter(myReviewAdapter);

        myReviewsViewModel = new ViewModelProvider(this).get(MyReviewsViewModel.class);
        observeMyReviewsViewModel();
        myReviewsViewModel.loadMyReviews();
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
            loadProductReviews(currentFilter);
        });
    }

    private void loadProductReviews(String filterType) {
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

            TextView tvTitle = layoutEmpty.findViewById(R.id.tvEmptyTitle);
            TextView tvDesc = layoutEmpty.findViewById(R.id.tvEmptyDescription);
            View btnAction = layoutEmpty.findViewById(R.id.btnEmptyAction);

            if (tvTitle != null) tvTitle.setText("Chưa có đánh giá");
            if (tvDesc != null) tvDesc.setText(getString(emptyTextRes));
            if (btnAction != null) {
                btnAction.setVisibility("all".equals(filterType) ? View.GONE : View.VISIBLE);
                if (btnAction instanceof TextView) {
                    ((TextView) btnAction).setText("Xem tất cả đánh giá");
                }
            }
        }

        Log.d(TAG, "load reviews productId = " + productId + ", query = " + query);
        if (productViewModel != null) {
            productViewModel.loadReviews(productId, query);
        }
    }

    private void observeProductViewModel() {
        if (productViewModel == null) return;
        
        productViewModel.getReviewsResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

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
                        TextView tvDesc = layoutEmpty.findViewById(R.id.tvEmptyDescription);
                        if (tvDesc != null) tvDesc.setText(result.message != null ? result.message : "Không tải được đánh giá");
                    }
                    if (rvReviews != null) rvReviews.setVisibility(View.GONE);
                    Toast.makeText(getContext(), result.message != null ? result.message : "Không tải được đánh giá", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        productViewModel.getVoteResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case SUCCESS:
                    if (result.data != null && reviewAdapter != null) {
                        reviewAdapter.updateReviewVoteState(
                                result.data.getReviewId(),
                                result.data.isLiked(),
                                result.data.getHelpfulCount()
                        );
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message != null ? result.message : "Không thể cập nhật yêu thích", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        productViewModel.getCommentResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case SUCCESS:
                    Toast.makeText(getContext(), "Đã gửi phản hồi", Toast.LENGTH_SHORT).show();
                    if (result.data != null && reviewAdapter != null) {
                        reviewAdapter.addCommentToReview(result.data);
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message != null ? result.message : "Không thể gửi phản hồi", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void showReplyDialog(com.example.frontend.data.model.review.ReviewDto review) {
        if (!com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
            com.example.frontend.feature.auth.GuestPromptBottomSheet.newInstance(
                    com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION
            ).show(getChildFragmentManager(), "GuestPromptBottomSheet");
            return;
        }

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.layout_comment_input, null);
        
        EditText edtComment = view.findViewById(R.id.edtCommentContent);
        View btnSend = view.findViewById(R.id.btnSendComment);
        TextView tvTitle = view.findViewById(R.id.tvCommentTitle);

        if (tvTitle != null) {
            String userName = review.getCustomer() != null ? review.getCustomer().getFullName() : "người dùng";
            tvTitle.setText(getString(R.string.reply_hint_format, userName));
        }

        btnSend.setOnClickListener(v -> {
            String content = edtComment.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                return;
            }
            if (productViewModel != null) {
                productViewModel.addReviewComment(review.getId(), content);
            }
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void observeMyReviewsViewModel() {
        if (myReviewsViewModel == null) return;
        
        myReviewsViewModel.getMyReviews().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    // handle loading if needed
                    break;
                case SUCCESS:
                    if (result.data != null) {
                        myReviewAdapter.setReviews(result.data);
                        boolean isEmpty = result.data.isEmpty();
                        if (layoutEmpty != null) layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                        if (rvReviews != null) rvReviews.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}
