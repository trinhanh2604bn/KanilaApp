package com.example.frontend.feature.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.frontend.R;
import com.example.frontend.model.HomeBannerItem;
import com.example.frontend.model.HomeShortcutItem;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPagerBanner;
    private HomeBannerAdapter bannerAdapter;
    private RecyclerView rvHomeShortcuts;
    private HomeShortcutAdapter shortcutAdapter;
    private RecyclerView rvRecommendedProducts;
    private HomeProductAdapter productAdapter;
    private HomeViewModel viewModel;
    
    private View layoutHomeStateContainer;
    private View viewHomeLoading;
    private View viewHomeError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        
        viewPagerBanner = view.findViewById(R.id.viewPagerBanner);
        rvHomeShortcuts = view.findViewById(R.id.rvHomeShortcuts);
        rvRecommendedProducts = view.findViewById(R.id.rvRecommendedProducts);
        layoutHomeStateContainer = view.findViewById(R.id.layoutHomeStateContainer);
        viewHomeLoading = view.findViewById(R.id.viewHomeLoading);
        viewHomeError = view.findViewById(R.id.viewHomeError);

        setupBannerSlider();
        setupHomeShortcuts();
        setupProductList();
        
        observeViewModel();
        
        viewModel.loadHomeData();
    }

    private void setupProductList() {
        productAdapter = new HomeProductAdapter();
        productAdapter.setOnProductClickListener(product -> {
            // Navigate to Product Detail
            Toast.makeText(requireContext(), "Product: " + product.getName(), Toast.LENGTH_SHORT).show();
        });
        
        rvRecommendedProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvRecommendedProducts.setAdapter(productAdapter);
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            
            if (state.loading) {
                showLoading();
            } else if (state.error != null) {
                showError(state.error);
            } else if (state.products != null) {
                showContent();
                productAdapter.setProducts(state.products);
            }
        });
    }

    private void showLoading() {
        layoutHomeStateContainer.setVisibility(View.VISIBLE);
        viewHomeLoading.setVisibility(View.VISIBLE);
        viewHomeError.setVisibility(View.GONE);
        rvRecommendedProducts.setVisibility(View.GONE);
    }

    private void showContent() {
        layoutHomeStateContainer.setVisibility(View.GONE);
        rvRecommendedProducts.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        layoutHomeStateContainer.setVisibility(View.VISIBLE);
        viewHomeLoading.setVisibility(View.GONE);
        viewHomeError.setVisibility(View.VISIBLE);
        rvRecommendedProducts.setVisibility(View.GONE);
        
        TextView tvError = viewHomeError.findViewById(R.id.tvErrorTitle);
        if (tvError != null) tvError.setText(message);
        
        View btnRetry = viewHomeError.findViewById(R.id.btnErrorRetry);
        if (btnRetry != null) btnRetry.setOnClickListener(v -> viewModel.loadHomeData());
    }

    private void setupBannerSlider() {
        bannerAdapter = new HomeBannerAdapter();
        bannerAdapter.setOnBannerClickListener(item -> {
            // Handle deeplink navigation here
        });

        viewPagerBanner.setAdapter(bannerAdapter);

        // Banners could be moved to ViewModel and loaded via API if supported
        List<HomeBannerItem> banners = new ArrayList<>();
        banners.add(new HomeBannerItem("1", "Kanila", "Gợi ý chuẩn da", "Sản phẩm chính hãng", "Khám phá", "", R.drawable.bg_reward, "category", "skincare", true, 1));
        banners.add(new HomeBannerItem("2", "Ưu đãi", "Deal hời cho nàng", "Giảm đến 50%", "Săn deal", "", R.drawable.bg_voucher, "promotion", "sale", true, 2));

        bannerAdapter.setItems(banners);
    }

    private void setupHomeShortcuts() {
        shortcutAdapter = new HomeShortcutAdapter();
        shortcutAdapter.setOnShortcutClickListener(item -> {
            // Handle navigation based on item.getDestinationType()
        });

        rvHomeShortcuts.setAdapter(shortcutAdapter);

        List<HomeShortcutItem> shortcuts = new ArrayList<>();
        shortcuts.add(new HomeShortcutItem("orders", "Đơn hàng", R.drawable.ic_shortcut_order, "orders", "", false, false));
        shortcuts.add(new HomeShortcutItem("voucher", "Voucher", R.drawable.ic_shortcut_voucher, "voucher", "", false, false));
        shortcuts.add(new HomeShortcutItem("ar", "AR Try-on", R.drawable.ic_shortcut_ar, "ar_try_on", "", false, false));
        shortcuts.add(new HomeShortcutItem("support", "Hỗ trợ", R.drawable.ic_shortcut_help, "support", "", false, false));

        shortcutAdapter.setItems(shortcuts);
    }
}
