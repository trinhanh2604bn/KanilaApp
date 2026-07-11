package com.example.frontend.feature.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ReviewHubFragment extends Fragment {
    private static final String ARG_PRODUCT_ID = "product_id";
    private String productId;

    public static ReviewHubFragment newInstance(String productId) {
        ReviewHubFragment fragment = new ReviewHubFragment();
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
        return inflater.inflate(R.layout.fragment_review_hub, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        view.findViewById(R.id.btnTopBarBack).setOnClickListener(v -> {
            if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
        });
        
        TextView tvTitle = view.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText("Đánh giá sản phẩm");

        TabLayout tabLayout = view.findViewById(R.id.tabReviewHub);
        ViewPager2 viewPager = view.findViewById(R.id.vpReviewHub);

        viewPager.setAdapter(new ReviewPagerAdapter(this, productId));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Tổng quan" : "Đánh giá");
        }).attach();
    }

    private static class ReviewPagerAdapter extends FragmentStateAdapter {
        private final String productId;

        public ReviewPagerAdapter(@NonNull Fragment fragment, String productId) {
            super(fragment);
            this.productId = productId;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return new ReviewOverviewFragment();
            
            ReviewListFragment fragment = new ReviewListFragment();
            Bundle args = new Bundle();
            args.putString("product_id", productId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
