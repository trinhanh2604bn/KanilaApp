package com.example.frontend.feature.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.frontend.R;
import com.example.frontend.model.HomeBannerItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPagerBanner;
    private HomeBannerAdapter bannerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewPagerBanner = view.findViewById(R.id.viewPagerBanner);
        setupBannerSlider();
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
}
