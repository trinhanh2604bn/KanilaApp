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
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.model.Brand;
import com.example.frontend.model.Category;
import com.example.frontend.model.HomeBannerItem;

<<<<<<< HEAD
=======
import ui.category.BrandPageFragment;

>>>>>>> origin/main
import java.util.ArrayList;
import java.util.List;

import ui.common.BottomNavigationHelper;

public class ProductCategoryFragment extends Fragment {

    private ViewPager2 vpCategoryBanner;
    private CategoryBannerAdapter bannerAdapter;
    private final Handler autoSlideHandler = new Handler(Looper.getMainLooper());
    private Runnable autoSlideRunnable;
    private final List<View> indicators = new ArrayList<>();
    
    private CatalogViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_product_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CatalogViewModel.class);

        setupTopBar(view);
<<<<<<< HEAD
=======

        // 2. Bind Category Grid
        bindCategoryCards(view);

        // 3. Setup Hero Slider
>>>>>>> origin/main
        setupHeroSlider(view);
        
        observeViewModel(view);

        BottomNavigationHelper.setup(view, tabIndex -> {
<<<<<<< HEAD
            // Handle cross-tab navigation if needed
=======
            if (tabIndex == BottomNavigationHelper.TAB_HOME) {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
>>>>>>> origin/main
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
    }

<<<<<<< HEAD
    private void observeViewModel(View root) {
        viewModel.getCategories().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.status == NetworkResult.Status.SUCCESS) {
                bindCategoryCards(root, result.data);
            }
        });

        viewModel.getBrands().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.status == NetworkResult.Status.SUCCESS) {
                bindBrandCards(root, result.data);
            }
        });
=======
        ImageButton btnBack = topBar.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }

        ImageButton btnSearch = topBar.findViewById(R.id.btnTopBarSearch);
>>>>>>> origin/main
    }

    private void setupHeroSlider(View root) {
        vpCategoryBanner = root.findViewById(R.id.vpCategoryBanner);
        if (vpCategoryBanner == null) return;

        indicators.clear();
        indicators.add(root.findViewById(R.id.indicator0));
        indicators.add(root.findViewById(R.id.indicator1));
        indicators.add(root.findViewById(R.id.indicator2));

        bannerAdapter = new CategoryBannerAdapter();
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

    private void bindCategoryCards(View root, List<Category> categories) {
        if (categories == null || categories.isEmpty()) return;

        int[] cardIds = {
            R.id.cardCategoryFace, R.id.cardCategoryLips, R.id.cardCategoryEyes,
            R.id.cardCategoryCheeks, R.id.cardCategoryGift, R.id.cardCategoryNew,
            R.id.cardCategoryHot, R.id.cardCategoryBrushes, R.id.cardCategoryAR
        };

        for (int i = 0; i < Math.min(categories.size(), cardIds.length); i++) {
            Category category = categories.get(i);
            View card = root.findViewById(cardIds[i]);
            if (card != null) {
                TextView titleView = card.findViewById(R.id.tvCategoryName);
                if (titleView != null) titleView.setText(category.getCategoryName());
                
                ImageView image = card.findViewById(R.id.ivCategoryProductImage);
                // Assume category might have an icon/image URL in future, or use dynamic mapping
                // Glide.with(this).load(category.getImageUrl()).into(image);

                card.setOnClickListener(v -> {
                    // Navigate to category details
                    Toast.makeText(getContext(), category.getCategoryName(), Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private void bindBrandCards(View root, List<Brand> brands) {
        if (brands == null || brands.isEmpty()) return;

        int[] brandCardIds = {
            R.id.cardBrandMaybelline, R.id.cardBrandHuda, R.id.cardBrandFwee,
            R.id.cardBrandJudydoll, R.id.cardBrandAnastasia
        };

        for (int i = 0; i < Math.min(brands.size(), brandCardIds.length); i++) {
            Brand brand = brands.get(i);
            View card = root.findViewById(brandCardIds[i]);
            if (card != null) {
                ImageView logo = card.findViewById(R.id.ivBrandLogo);
                if (logo != null) {
                    Glide.with(this)
                            .load(brand.getLogoUrl())
                            .placeholder(R.drawable.ic_product)
                            .into(logo);
                }
                card.setOnClickListener(v -> {
                    // Navigate to brand details
                    Toast.makeText(getContext(), brand.getBrandName(), Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        autoSlideHandler.removeCallbacks(autoSlideRunnable);
    }
<<<<<<< HEAD
=======

    private void bindCategoryCards(View root) {
        bindCategoryCard(root.findViewById(R.id.cardCategoryFace), R.drawable.ic_face, R.drawable.img_foudation, "Face");
        bindCategoryCard(root.findViewById(R.id.cardCategoryLips), R.drawable.ic_lipstick, R.drawable.img_lipstick, "Lips");
        bindCategoryCard(root.findViewById(R.id.cardCategoryEyes), R.drawable.ic_eyeshadow, R.drawable.img_eyeshadow, "Eyes");
        bindCategoryCard(root.findViewById(R.id.cardCategoryCheeks), R.drawable.ic_blush, R.drawable.img_blush, "Cheeks");
        bindCategoryCard(root.findViewById(R.id.cardCategoryGift), R.drawable.ic_gift, R.drawable.img_gift, "Gift");
        bindCategoryCard(root.findViewById(R.id.cardCategoryNew), R.drawable.ic_new, R.drawable.img_new, "New");
        bindCategoryCard(root.findViewById(R.id.cardCategoryHot), R.drawable.ic_hot, R.drawable.img_hot, "Hot");
        bindCategoryCard(root.findViewById(R.id.cardCategoryBrushes), R.drawable.ic_brush, R.drawable.img_brush, "Mini & Travel");
        bindCategoryCard(root.findViewById(R.id.cardCategoryAR), R.drawable.ic_ar, R.drawable.img_ar, "AR");
    }

    private void bindCategoryCard(View card, int iconRes, int imageRes, String title) {
        if (card == null) return;
        ImageView icon = card.findViewById(R.id.ivCategoryIcon);
        ImageView image = card.findViewById(R.id.ivCategoryProductImage);
        TextView titleView = card.findViewById(R.id.tvCategoryName);

        if (icon != null) icon.setImageResource(iconRes);
        if (image != null) image.setImageResource(imageRes);
        if (titleView != null) titleView.setText(title);

        card.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, ProductListingFragment.newCategoryInstance(title))
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void bindDemandCards(View root) {
        bindDemandCard(root.findViewById(R.id.cardDemandConcealing), R.drawable.ic_face, "Concealing", R.color.status_success_bg);
        bindDemandCard(root.findViewById(R.id.cardDemandMoisturizing), R.drawable.ic_drops, "Dưỡng ẩm", R.color.status_preparing_bg);
        bindDemandCard(root.findViewById(R.id.cardDemandSunproof), R.drawable.ic_sun, "Sunproof", R.color.status_pending_bg);
        bindDemandCard(root.findViewById(R.id.cardDemandToneUp), R.drawable.ic_cream, "Nâng tone", R.color.pink_bg);
        bindDemandCard(root.findViewById(R.id.cardDemandWaterproof), R.drawable.ic_check, "Waterproof", R.color.status_exchange_bg);
    }

    private void bindDemandCard(View card, int iconRes, String title, int circleColorRes) {
        if (card == null) return;
        ImageView icon = card.findViewById(R.id.ivDemandIcon);
        TextView titleView = card.findViewById(R.id.tvDemandTitle);
        View circle = card.findViewById(R.id.layoutCategoryIconCircle);

        if (icon != null) icon.setImageResource(iconRes);
        if (titleView != null) titleView.setText(title);
        if (circle != null && getContext() != null) {
            circle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), circleColorRes)));
        }
    }

    private void bindBrandCards(View root) {
        bindBrandCard(root.findViewById(R.id.cardBrandMaybelline), R.drawable.brand_mbl, R.color.text_main);
        bindBrandCard(root.findViewById(R.id.cardBrandHuda), R.drawable.brand_hdbt, R.color.button);
        bindBrandCard(root.findViewById(R.id.cardBrandFwee), R.drawable.brand_fee, R.color.info);
        bindBrandCard(root.findViewById(R.id.cardBrandJudydoll), R.drawable.brand_jd, R.color.primary);
        bindBrandCard(root.findViewById(R.id.cardBrandAnastasia), R.drawable.brand_nars, R.color.background_main);
    }

    private void bindBrandCard(View card, int logoRes, int bgColorRes) {
        if (card == null) return;
        ImageView logo = card.findViewById(R.id.ivBrandLogo);
        if (logo != null) {
            logo.setImageResource(logoRes);
            logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        if (getContext() != null) {
            card.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), bgColorRes)));
        }
    }
>>>>>>> origin/main
}
