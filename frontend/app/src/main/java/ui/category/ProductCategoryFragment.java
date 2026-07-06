package ui.category;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.model.Brand;
import com.example.frontend.model.Category;
import com.example.frontend.model.HomeBannerItem;

import ui.category.BrandPageFragment;
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
        setupHeroSlider(view);
        
        observeViewModel(view);

        BottomNavigationHelper.setup(view, tabIndex -> {
            if (tabIndex == BottomNavigationHelper.TAB_HOME) {
                Intent intent = new Intent(requireContext(), com.example.frontend.MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (tabIndex == BottomNavigationHelper.TAB_ACCOUNT) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new ui.account.AccountFragment())
                        .commit();
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
}
