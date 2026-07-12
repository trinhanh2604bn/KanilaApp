package ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.review.MyReviewDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.product.adapter.ReviewMediaAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReviewDetailFragment extends Fragment {

    private static final String ARG_REVIEW_ID = "review_id";

    private String reviewId;
    private ReviewDetailViewModel viewModel;

    private View layoutLoading;
    private ImageView ivProductImage;
    private TextView tvProductName, tvVariantName, tvReviewDate, tvReviewContent, tvMediaCount;
    private RatingBar rbRating;
    private RecyclerView rvMedia;
    private ReviewMediaAdapter mediaAdapter;

    public static ReviewDetailFragment newInstance(String reviewId) {
        ReviewDetailFragment fragment = new ReviewDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REVIEW_ID, reviewId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reviewId = getArguments().getString(ARG_REVIEW_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReviewDetailViewModel.class);

        initViews(view);
        setupHeader(view);
        setupMediaList();
        observeViewModel();

        viewModel.loadReviewDetail(reviewId);
    }

    private void initViews(View view) {
        layoutLoading = view.findViewById(R.id.layoutLoading);
        ivProductImage = view.findViewById(R.id.ivProductImage);
        tvProductName = view.findViewById(R.id.tvProductName);
        tvVariantName = view.findViewById(R.id.tvVariantName);
        tvReviewDate = view.findViewById(R.id.tvReviewDate);
        tvReviewContent = view.findViewById(R.id.tvReviewContent);
        tvMediaCount = view.findViewById(R.id.tvMediaCount);
        rbRating = view.findViewById(R.id.rbRating);
        rvMedia = view.findViewById(R.id.rvMedia);
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutHeader);
        if (header != null) {
            TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
            if (tvTitle != null) tvTitle.setText("Đánh giá của tôi");

            header.findViewById(R.id.btnTopBarBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
    }

    private void setupMediaList() {
        mediaAdapter = new ReviewMediaAdapter();
        rvMedia.setAdapter(mediaAdapter);
    }

    private void observeViewModel() {
        viewModel.getReviewDetail().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            layoutLoading.setVisibility(result.status == NetworkResult.Status.LOADING ? View.VISIBLE : View.GONE);
            if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                bindData(result.data);
            } else if (result.status == NetworkResult.Status.ERROR) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindData(MyReviewDto data) {
        tvProductName.setText(data.getProduct().getProductName());
        tvVariantName.setText(data.getProduct().getVariantName());
        
        // Simple date formatting if needed, but assuming backend returns formatted or readable string
        if (data.getCreatedAt() != null && data.getCreatedAt().contains("T")) {
            String dateStr = data.getCreatedAt().split("T")[0];
            String[] parts = dateStr.split("-");
            if (parts.length == 3) {
                tvReviewDate.setText(parts[2] + "/" + parts[1] + "/" + parts[0]);
            } else {
                tvReviewDate.setText(dateStr);
            }
        } else {
            tvReviewDate.setText(data.getCreatedAt());
        }

        rbRating.setRating(data.getRating());
        tvReviewContent.setText(data.getReviewContent());

        Glide.with(this)
                .load(data.getProduct().getImageUrl())
                .placeholder(R.drawable.ic_product)
                .error(R.drawable.ic_product)
                .centerCrop()
                .into(ivProductImage);

        if (data.getMedia() != null && !data.getMedia().isEmpty()) {
            List<String> urls = new ArrayList<>();
            for (MyReviewDto.ReviewMediaDto m : data.getMedia()) {
                urls.add(m.getMediaUrl());
            }
            mediaAdapter.setMediaUrls(urls);
            tvMediaCount.setText(String.format(Locale.getDefault(), "%d ảnh", urls.size()));
            tvMediaCount.setVisibility(View.VISIBLE);
            rvMedia.setVisibility(View.VISIBLE);
        } else {
            tvMediaCount.setVisibility(View.GONE);
            rvMedia.setVisibility(View.GONE);
        }
    }
}
