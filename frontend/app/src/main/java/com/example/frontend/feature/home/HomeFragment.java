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
import com.example.frontend.data.remote.NetworkResult;
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
            Toast.makeText(requireContext(), "Product: " + product.getName(), Toast.LENGTH_SHORT).show();
            // Navigate to Product Detail
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
            Toast.makeText(requireContext(), "Click: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            // Handle deeplink navigation here
        });

        viewPagerBanner.setAdapter(bannerAdapter);

        // Sample Data
        List<HomeBannerItem> sampleBanners = new ArrayList<>();
        sampleBanners.add(new HomeBannerItem(
                "1",
                "Khám phá vẻ đẹp cùng Kanila",
                "Gợi ý chuẩn da\nRạng ngời mỗi ngày",
                "Sản phẩm chính hãng • Gợi ý cá nhân hóa",
                "Khám phá ngay",
                "", // No URL for now
                R.drawable.bg_reward, // Using an existing drawable as fallback
                "category",
                "skincare",
                true,
                1
        ));
        sampleBanners.add(new HomeBannerItem(
                "2",
                "Ưu đãi độc quyền",
                "Deal hời cho nàng\nTự tin tỏa sáng",
                "Giảm đến 50% cho các sản phẩm best-seller",
                "Săn deal ngay",
                "",
                R.drawable.bg_voucher,
                "promotion",
                "summer_sale",
                true,
                2
        ));
        sampleBanners.add(new HomeBannerItem(
                "3",
                "Thành viên mới",
                "Quà tặng chào mừng\nĐặc quyền Kanila",
                "Nhận ngay voucher 50k cho đơn đầu tiên",
                "Nhận quà ngay",
                "",
                R.drawable.bg_reward,
                "auth",
                "register",
                true,
                3
        ));

        bannerAdapter.setItems(sampleBanners);

        // ViewPager2 doesn't have a direct "counter" view inside it, 
        // but we handle it inside the item layout (item_home_banner.xml).
        // The adapter already handles updating the counter text in onBindViewHolder.
    }

    private void setupHomeShortcuts() {
        shortcutAdapter = new HomeShortcutAdapter();
        shortcutAdapter.setOnShortcutClickListener(item -> {
            Toast.makeText(requireContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            // Handle navigation based on item.getDestinationType()
        });

        rvHomeShortcuts.setAdapter(shortcutAdapter);

        List<HomeShortcutItem> shortcuts = new ArrayList<>();
        shortcuts.add(new HomeShortcutItem("orders", "Đơn hàng", R.drawable.ic_shortcut_order, "orders", "", false, false));
        shortcuts.add(new HomeShortcutItem("voucher", "Voucher", R.drawable.ic_shortcut_voucher, "voucher", "", false, true)); // Example badge
        shortcuts.add(new HomeShortcutItem("ar", "AR", R.drawable.ic_shortcut_ar, "ar_try_on", "", false, false));
        shortcuts.add(new HomeShortcutItem("kanila_beauty", "Kanila Beauty", R.drawable.ic_shortcut_kanila_beauty, "beauty", "", false, false));
        shortcuts.add(new HomeShortcutItem("creator", "Creator", R.drawable.ic_shortcut_creator, "creator", "", false, false));
        shortcuts.add(new HomeShortcutItem("royalty", "Royalty", R.drawable.ic_shortcut_royalty, "loyalty", "", false, false));
        shortcuts.add(new HomeShortcutItem("help", "Trợ giúp", R.drawable.ic_shortcut_help, "support", "", false, false));
        shortcuts.add(new HomeShortcutItem("policy", "Chính sách", R.drawable.ic_shortcut_policy, "policy", "", false, false));

        shortcutAdapter.setItems(shortcuts);
    }
}
