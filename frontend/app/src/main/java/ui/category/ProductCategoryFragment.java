package ui.category;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.frontend.R;
import com.example.frontend.model.HomeBannerItem;

import ui.category.BrandPageFragment;
import java.util.ArrayList;
import java.util.List;

import ui.common.BottomNavigationHelper;

public class ProductCategoryFragment extends Fragment {

    private ViewPager2 vpCategoryBanner;
    private final Handler autoSlideHandler = new Handler(Looper.getMainLooper());
    private Runnable autoSlideRunnable;
    private final List<View> indicators = new ArrayList<>();
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_product_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTopBar(view);
        setupHeroSlider(view);
        
        setupStaticCategories(view);
        setupStaticBrands(view);

        BottomNavigationHelper.setup(view, tabIndex -> {
            if (tabIndex == BottomNavigationHelper.TAB_HOME) {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        BottomNavigationHelper.setSelectedTab(view, BottomNavigationHelper.TAB_CATEGORY);

        TextView tvSeeAllBrands = view.findViewById(R.id.tvSeeAllBrands);
        if (tvSeeAllBrands != null) {
            tvSeeAllBrands.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main, new BrandPageFragment())
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }

    private void setupTopBar(View root) {
        View topBar = root.findViewById(R.id.includeTopBar);
        if (topBar == null) return;
        TextView tvTitle = topBar.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText(R.string.top_bar_category_title);

        ImageButton btnBack = topBar.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    private void setupHeroSlider(View root) {
        vpCategoryBanner = root.findViewById(R.id.vpCategoryBanner);
        if (vpCategoryBanner == null) return;

        indicators.clear();
        indicators.add(root.findViewById(R.id.indicator0));
        indicators.add(root.findViewById(R.id.indicator1));
        indicators.add(root.findViewById(R.id.indicator2));

        CategoryBannerAdapter bannerAdapter = new CategoryBannerAdapter();
        vpCategoryBanner.setAdapter(bannerAdapter);

        List<HomeBannerItem> items = new ArrayList<>();
        items.add(new HomeBannerItem("1", "", "", "", "Mua ngay", null, R.drawable.img_cate_list_1, "promotion", "1", true, 1));
        items.add(new HomeBannerItem("2", "", "", "", "Khám phá", null, R.drawable.img_cate_list_2, "promotion", "2", true, 2));
        items.add(new HomeBannerItem("3", "", "", "", "Xem thêm", null, R.drawable.img_cate_list_3, "promotion", "3", true, 3));

        bannerAdapter.setItems(items);

        int startPosition = (Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % items.size());
        vpCategoryBanner.setCurrentItem(startPosition, false);
        updateIndicators(startPosition % items.size());

        setupAutoSlide(items.size());

        vpCategoryBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int realPos = position % items.size();
                updateIndicators(realPos);
                autoSlideHandler.removeCallbacks(autoSlideRunnable);
                if (autoSlideRunnable != null) autoSlideHandler.postDelayed(autoSlideRunnable, 4000);
            }
        });
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicators.size(); i++) {
            View indicator = indicators.get(i);
            if (indicator != null) {
                int color = (i == position) ? R.color.accent_dark : R.color.border_divider;
                indicator.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color)));
            }
        }
    }

    private void setupAutoSlide(int size) {
        if (size <= 1) return;
        autoSlideRunnable = () -> {
            if (vpCategoryBanner != null) {
                vpCategoryBanner.setCurrentItem(vpCategoryBanner.getCurrentItem() + 1, true);
                autoSlideHandler.postDelayed(autoSlideRunnable, 4000);
            }
        };
        autoSlideHandler.postDelayed(autoSlideRunnable, 4000);
    }

    private void setupStaticCategories(View root) {
        // Face
        setupCategoryCard(root.findViewById(R.id.cardCategoryFace), R.string.category_face, R.drawable.ic_face, R.drawable.img_foudation, new FaceFragment());
        // Lips
        setupCategoryCard(root.findViewById(R.id.cardCategoryLips), R.string.category_lips, R.drawable.ic_lipstick, R.drawable.img_lipstick, ProductListingFragment.newCategoryInstance("Lips"));
        // Eyes
        setupCategoryCard(root.findViewById(R.id.cardCategoryEyes), R.string.category_eyes, R.drawable.ic_eye, R.drawable.img_eyeshadow, ProductListingFragment.newCategoryInstance("Eyes"));
        // Cheeks
        setupCategoryCard(root.findViewById(R.id.cardCategoryCheeks), R.string.category_cheeks, R.drawable.ic_blush, R.drawable.img_blush, ProductListingFragment.newCategoryInstance("Cheeks"));
        // Gift
        setupCategoryCard(root.findViewById(R.id.cardCategoryGift), R.string.category_gift, R.drawable.ic_gift, R.drawable.img_gift, ProductListingFragment.newCategoryInstance("Gift"));
        // New
        setupCategoryCard(root.findViewById(R.id.cardCategoryNew), R.string.category_new, R.drawable.ic_new, R.drawable.img_new, ProductListingFragment.newCategoryInstance("New Arrivals"));
        // Hot
        setupCategoryCard(root.findViewById(R.id.cardCategoryHot), R.string.category_hot, R.drawable.ic_hot, R.drawable.img_hot, ProductListingFragment.newCategoryInstance("Hot Products"));
        // Brushes -> Mini & Travel
        setupCategoryCard(root.findViewById(R.id.cardCategoryBrushes), R.string.category_brushes, R.drawable.ic_brush, R.drawable.img_brush, ProductListingFragment.newCategoryInstance("Mini & Travel"));
        // AR
        setupCategoryCard(root.findViewById(R.id.cardCategoryAR), R.string.category_ar, R.drawable.ic_ar, R.drawable.img_ar, null);
    }

    private void setupCategoryCard(View card, int titleRes, int iconRes, int imgRes, Fragment destination) {
        if (card == null) return;
        TextView tvName = card.findViewById(R.id.tvCategoryName);
        ImageView ivIcon = card.findViewById(R.id.ivCategoryIcon);
        ImageView ivProduct = card.findViewById(R.id.ivCategoryProductImage);

        if (tvName != null) tvName.setText(titleRes);
        if (ivIcon != null) ivIcon.setImageResource(iconRes);
        if (ivProduct != null) ivProduct.setImageResource(imgRes);

        card.setOnClickListener(v -> {
            if (destination != null && getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, destination)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), getString(titleRes), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupStaticBrands(View root) {
        setupBrandCard(root.findViewById(R.id.cardBrandMaybelline), R.drawable.brand_mbl, "Maybelline");
        setupBrandCard(root.findViewById(R.id.cardBrandHuda), R.drawable.brand_hdbt, "Huda Beauty");
        setupBrandCard(root.findViewById(R.id.cardBrandFwee), R.drawable.brand_fee, "Fwee");
        setupBrandCard(root.findViewById(R.id.cardBrandJudydoll), R.drawable.brand_jd, "Judydoll");
        setupBrandCard(root.findViewById(R.id.cardBrandAnastasia), R.drawable.brand_nars, "Anastasia");
    }

    private void setupBrandCard(View card, int logoRes, String brandName) {
        if (card == null) return;
        ImageView ivLogo = card.findViewById(R.id.ivBrandLogo);
        if (ivLogo != null) ivLogo.setImageResource(logoRes);
        card.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, ProductListingFragment.newBrandInstance(brandName))
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        autoSlideHandler.removeCallbacks(autoSlideRunnable);
    }
}
