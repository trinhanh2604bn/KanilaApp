package com.example.frontend.ui.category;

import android.content.Intent;
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

import com.example.frontend.data.model.category.CategoryDto;
import com.example.frontend.data.repository.CategoryRepository;
import com.bumptech.glide.Glide;
import com.example.frontend.data.repository.CatalogRepository;
import com.example.frontend.model.Brand;
import com.example.frontend.data.remote.NetworkResult;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCategoryFragment extends Fragment {

    private ViewPager2 vpCategoryBanner;
    private final Handler autoSlideHandler = new Handler(Looper.getMainLooper());
    private Runnable autoSlideRunnable;
    private final List<View> indicators = new ArrayList<>();
    private CategoryRepository categoryRepository;
    private CatalogRepository catalogRepository;

    private final Map<String, Integer> categoryCardMap = new HashMap<String, Integer>() {{
        put("FACE", R.id.cardCategoryFace);
        put("LIPS", R.id.cardCategoryLips);
        put("EYES", R.id.cardCategoryEyes);
        put("CHEEKS", R.id.cardCategoryCheeks);
        put("GIFT", R.id.cardCategoryGift);
        put("MINITRAVEL", R.id.cardCategoryBrushes);
    }};

    private final Map<String, Integer> categoryImageMap = new HashMap<String, Integer>() {{
        put("FACE", R.drawable.img_foudation);
        put("LIPS", R.drawable.img_lipstick);
        put("EYES", R.drawable.img_eyeshadow);
        put("CHEEKS", R.drawable.img_blush);
        put("GIFT", R.drawable.img_gift);
        put("MINITRAVEL", R.drawable.img_brush);
    }};

    private final Map<String, Integer> categoryIconMap = new HashMap<String, Integer>() {{
        put("FACE", R.drawable.ic_face);
        put("LIPS", R.drawable.ic_lipstick);
        put("EYES", R.drawable.ic_eye);
        put("CHEEKS", R.drawable.ic_blush);
        put("GIFT", R.drawable.ic_gift);
        put("MINITRAVEL", R.drawable.ic_brush);
    }};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_product_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryRepository = new CategoryRepository(requireContext());
        catalogRepository = new CatalogRepository(requireContext());

        setupTopBar(view);
        setupHeroSlider(view);
        
        loadRootCategories(view);
        setupSpecialCollectionCards(view);
        loadFeaturedBrands(view);

        TextView tvSeeAllBrands = view.findViewById(R.id.tvSeeAllBrands);
        if (tvSeeAllBrands != null) {
            tvSeeAllBrands.setOnClickListener(v -> {
                ui.common.FragmentNavigationHelper.replaceFragment(getActivity(), new BrandPageFragment());
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

    private void loadRootCategories(View root) {
        categoryRepository.getRootCategories().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.status) {
                case SUCCESS:
                    if (result.data != null) {
                        bindRootCategoryCards(root, result.data);
                    }
                    break;

                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;

                case EMPTY:
                    break;
            }
        });

        // AR and New/Hot if not in root categories
        setupCategoryCard(root.findViewById(R.id.cardCategoryAR), R.string.category_ar, R.drawable.ic_ar, R.drawable.img_ar, null);
    }

    private void bindRootCategoryCards(View root, List<CategoryDto> categories) {
        for (CategoryDto category : categories) {
            if (category == null || category.getCategoryCode() == null) continue;

            Integer cardId = categoryCardMap.get(category.getCategoryCode());
            if (cardId == null) continue;

            View card = root.findViewById(cardId);
            bindCategoryCard(card, category);
        }
    }

    private void bindCategoryCard(View card, CategoryDto category) {
        if (card == null || category == null) return;

        TextView tvName = card.findViewById(R.id.tvCategoryName);
        ImageView ivIcon = card.findViewById(R.id.ivCategoryIcon);
        ImageView ivProduct = card.findViewById(R.id.ivCategoryProductImage);

        if (tvName != null) {
            tvName.setText(category.getName());
        }

        Integer imageRes = categoryImageMap.get(category.getCategoryCode());
        if (imageRes != null && ivProduct != null) {
            ivProduct.setImageResource(imageRes);
        }

        Integer iconRes = categoryIconMap.get(category.getCategoryCode());
        if (iconRes != null && ivIcon != null) {
            ivIcon.setImageResource(iconRes);
        }

        card.setOnClickListener(v -> {
            ui.common.FragmentNavigationHelper.replaceFragment(getActivity(), 
                ProductListingFragment.newCategoryInstance(category.getId(), category.getName()));
        });
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
            if (destination != null) {
                ui.common.FragmentNavigationHelper.replaceFragment(getActivity(), destination);
            } else {
                Toast.makeText(getContext(), getString(titleRes), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpecialCollectionCards(View root) {
        View hotCard = root.findViewById(R.id.cardCategoryHot);
        View newCard = root.findViewById(R.id.cardCategoryNew);

        if (hotCard != null) {
            setupCategoryCard(hotCard, R.string.category_hot, R.drawable.ic_hot, R.drawable.img_hot, null);
            hotCard.setOnClickListener(v -> {
                ui.common.FragmentNavigationHelper.replaceFragment(getActivity(),
                        ProductListingFragment.newCollectionInstance("hot", getString(R.string.category_hot)));
            });
        }

        if (newCard != null) {
            setupCategoryCard(newCard, R.string.category_new_arrival, R.drawable.ic_new, R.drawable.img_new, null);
            newCard.setOnClickListener(v -> {
                ui.common.FragmentNavigationHelper.replaceFragment(getActivity(),
                        ProductListingFragment.newCollectionInstance("new-arrival", getString(R.string.category_new_arrival)));
            });
        }
    }

    private void loadFeaturedBrands(View root) {
        catalogRepository.getBrands().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    break;

                case SUCCESS:
                    if (result.data != null) {
                        bindFeaturedBrands(root, result.data);
                    } else {
                        hideFeaturedBrandCards(root);
                    }
                    break;

                case EMPTY:
                    hideFeaturedBrandCards(root);
                    break;

                case ERROR:
                case NO_INTERNET:
                    Toast.makeText(
                        getContext(),
                        result.message != null ? result.message : "Không tải được thương hiệu",
                        Toast.LENGTH_SHORT
                    ).show();
                    hideFeaturedBrandCards(root);
                    break;
            }
        });
    }

    private void bindFeaturedBrands(View root, List<Brand> brands) {
        int[] cardIds = {
            R.id.cardBrandMaybelline,
            R.id.cardBrandHuda,
            R.id.cardBrandFwee,
            R.id.cardBrandJudydoll,
            R.id.cardBrandAnastasia
        };

        for (int i = 0; i < cardIds.length; i++) {
            View card = root.findViewById(cardIds[i]);

            if (card == null) continue;

            if (brands != null && i < brands.size() && brands.get(i) != null) {
                card.setVisibility(View.VISIBLE);
                bindFeaturedBrandCard(card, brands.get(i));
            } else {
                card.setVisibility(View.GONE);
            }
        }
    }

    private void hideFeaturedBrandCards(View root) {
        int[] cardIds = {
            R.id.cardBrandMaybelline,
            R.id.cardBrandHuda,
            R.id.cardBrandFwee,
            R.id.cardBrandJudydoll,
            R.id.cardBrandAnastasia
        };

        for (int cardId : cardIds) {
            View card = root.findViewById(cardId);
            if (card != null) card.setVisibility(View.GONE);
        }
    }

    private void bindFeaturedBrandCard(View card, Brand brand) {
        if (card == null || brand == null) return;

        Log.d("ProductCategory", "Featured brand = " + brand.getBrandName() + ", id = " + brand.getId());

        ImageView ivLogo = card.findViewById(R.id.ivBrandLogo);
        TextView tvName = card.findViewById(R.id.tvBrandName);

        if (tvName != null) {
            tvName.setText(brand.getBrandName());
        }

        if (ivLogo != null) {
            if (brand.getLogoUrl() != null && !brand.getLogoUrl().trim().isEmpty()) {
                Glide.with(ivLogo.getContext())
                    .load(brand.getLogoUrl())
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(ivLogo);
            } else {
                ivLogo.setImageResource(R.drawable.bg_circle);
            }
        }

        card.setOnClickListener(v -> {
            if (brand.getId() == null || brand.getId().trim().isEmpty()) {
                Toast.makeText(getContext(), "Không tìm thấy ID thương hiệu", Toast.LENGTH_SHORT).show();
                return;
            }

            ui.common.FragmentNavigationHelper.replaceFragment(
                getActivity(),
                ProductListingFragment.newBrandInstance(brand.getId(), brand.getBrandName())
            );
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        autoSlideHandler.removeCallbacks(autoSlideRunnable);
    }
}
