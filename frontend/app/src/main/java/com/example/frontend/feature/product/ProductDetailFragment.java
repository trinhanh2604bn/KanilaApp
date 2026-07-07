package com.example.frontend.feature.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.utils.ToastHelper;
import java.util.Locale;

public class ProductDetailFragment extends Fragment {
    private static final String ARG_PRODUCT_ID = "product_id";
    
    private ProductDetailViewModel viewModel;
    private String productId;
    
    private TextView tvName, tvBrand, tvPrice, tvComparePrice, tvDesc, tvGalleryCounter;
    private ViewPager2 vpGallery;
    private ProductGalleryAdapter galleryAdapter;

    public static ProductDetailFragment newInstance(String productId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putString(ARG_PRODUCT_ID, productId);
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
        return inflater.inflate(R.layout.page_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        
        initViews(view);
        observeViewModel();
        
        if (productId != null) {
            viewModel.loadProductDetails(productId);
        }
    }

    private void initViews(View view) {
        View topBar = view.findViewById(R.id.layoutProductTopBar);
        if (topBar != null) {
            topBar.findViewById(R.id.btnTopBarBack).setOnClickListener(v -> {
                if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
            });
            topBar.findViewById(R.id.btnTopBarSearch).setVisibility(View.GONE);
        }

        tvName = view.findViewById(R.id.tvProductDetailName);
        tvBrand = view.findViewById(R.id.tvProductDetailBrand);
        tvPrice = view.findViewById(R.id.tvProductDetailPrice);
        tvComparePrice = view.findViewById(R.id.tvProductDetailComparePrice);
        tvDesc = view.findViewById(R.id.tvProductDetailDesc);
        tvGalleryCounter = view.findViewById(R.id.tvProductGalleryCounter);
        vpGallery = view.findViewById(R.id.vpProductGallery);

        galleryAdapter = new ProductGalleryAdapter();
        vpGallery.setAdapter(galleryAdapter);
        vpGallery.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateGalleryCounter(position);
            }
        });

        view.findViewById(R.id.btnAddToCart).setOnClickListener(v -> {
            // TODO: Add to cart
            ToastHelper.showShort(getContext(), "Đã thêm vào giỏ hàng");
        });
    }

    private void observeViewModel() {
        viewModel.getProductResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case SUCCESS:
                    bindProductData(result.data);
                    break;
                case ERROR:
                    ToastHelper.showShort(getContext(), result.message);
                    break;
            }
        });

        viewModel.getMediaResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS && result.data != null) {
                galleryAdapter.setMediaList(result.data);
                updateGalleryCounter(vpGallery.getCurrentItem());
            }
        });
    }

    private void updateGalleryCounter(int position) {
        if (galleryAdapter != null && galleryAdapter.getItemCount() > 0) {
            tvGalleryCounter.setText(String.format(Locale.US, "%d/%d", position + 1, galleryAdapter.getItemCount()));
            tvGalleryCounter.setVisibility(View.VISIBLE);
        } else {
            tvGalleryCounter.setVisibility(View.GONE);
        }
    }

    private void bindProductData(Product product) {
        if (product == null) return;
        tvName.setText(product.getName());
        tvBrand.setText(product.getBrand());
        tvPrice.setText(product.getPrice());
        tvDesc.setText(product.getSubcategory()); // Placeholder for description if not mapped
        
        // Setup simple image adapter or use mediaResult
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
    }
}
