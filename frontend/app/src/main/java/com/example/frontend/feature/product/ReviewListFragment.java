package com.example.frontend.feature.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.repository.ReviewRepository;
import java.util.ArrayList;
import java.util.List;

import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.frontend.data.remote.NetworkResult;
import ui.order.MyReviewAdapter;
import ui.order.MyReviewsViewModel;
import ui.order.ReviewDetailFragment;
import ui.common.FragmentNavigationHelper;

public class ReviewListFragment extends Fragment {

    private static final String ARG_PRODUCT_ID = "product_id";
    private String productId;
    
    private MyReviewsViewModel viewModel;
    private MyReviewAdapter adapter;
    private View layoutLoading;
    private TextView tvMyReviewsTitle;

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
        return inflater.inflate(R.layout.fragment_review_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvMyReviewsTitle = view.findViewById(R.id.tvMyReviewsTitle);
        
        if (productId == null) {
            setupMyReviews(view);
        } else {
            setupProductReviews(view);
        }
    }

    private void setupMyReviews(View view) {
        tvMyReviewsTitle.setVisibility(View.VISIBLE);
        viewModel = new ViewModelProvider(this).get(MyReviewsViewModel.class);

        RecyclerView rv = view.findViewById(R.id.rvReviews);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        adapter = new MyReviewAdapter(review -> FragmentNavigationHelper.replaceFragment(requireActivity(), ReviewDetailFragment.newInstance(review.getReviewId())));
        rv.setAdapter(adapter);

        View scrollFilters = view.findViewById(R.id.scrollFilters);
        if (scrollFilters != null) scrollFilters.setVisibility(View.GONE);

        observeViewModel();
        viewModel.loadMyReviews();
    }

    private void setupProductReviews(View view) {
        tvMyReviewsTitle.setVisibility(View.GONE);
        RecyclerView rv = view.findViewById(R.id.rvReviews);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        com.example.frontend.feature.product.adapter.ReviewAdapter productAdapter = new com.example.frontend.feature.product.adapter.ReviewAdapter();
        rv.setAdapter(productAdapter);

        ReviewRepository repo = new ReviewRepository(requireContext());
        MutableLiveData<NetworkResult<List<com.example.frontend.data.model.review.ReviewDto>>> productReviewsResult = new MutableLiveData<>();
        productReviewsResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                productAdapter.setReviews(result.data);
            } else if (result.status == NetworkResult.Status.ERROR) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
            }
        });
        repo.getReviewsByProductId(productId, productReviewsResult);
    }

    private void observeViewModel() {
        viewModel.getMyReviews().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            // Assuming there's a loading state in the layout, but if not we can just check
            switch (result.status) {
                case LOADING:
                    // show loading
                    break;
                case SUCCESS:
                    if (result.data != null) {
                        adapter.setReviews(result.data);
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}
