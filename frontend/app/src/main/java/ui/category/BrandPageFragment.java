package ui.category;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.frontend.R;
import com.example.frontend.model.Brand;
import com.example.frontend.model.HomeBannerItem;
import java.util.ArrayList;
import java.util.List;
import ui.common.BottomNavigationHelper;

public class BrandPageFragment extends Fragment {

    private RecyclerView rvBrandGrid;
    private List<Brand> fullBrandList;
    private BrandAdapter adapter;
    private LinearLayout layoutFilterChips;
    
    private ViewPager2 vpBrandBanner;
    private CategoryBannerAdapter bannerAdapter;
    private final Handler autoSlideHandler = new Handler(Looper.getMainLooper());
    private Runnable autoSlideRunnable;
    private final List<View> indicators = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_brand, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupTopBar(view);
        setupMockData();
        setupFilterLogic();
        setupHeroSlider(view);
        
        // Mặc định hiển thị "Tất cả"
        filterBrands(getString(R.string.filter_all));

        BottomNavigationHelper.setup(view, tabIndex -> {
            // Handle tab navigation
        });
        BottomNavigationHelper.setSelectedTab(view, BottomNavigationHelper.TAB_CATEGORY);
    }

    private void initViews(View root) {
        rvBrandGrid = root.findViewById(R.id.rvBrandGrid);
        layoutFilterChips = root.findViewById(R.id.layoutFilterChips);
        rvBrandGrid.setLayoutManager(new GridLayoutManager(requireContext(), 3));
    }

    private void setupTopBar(View root) {
        View topBar = root.findViewById(R.id.includeBrandTopBar);
        TextView tvTitle = topBar.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText(R.string.top_bar_brand_title);

        ImageButton btnBack = topBar.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
            });
        }

        ImageButton btnSearch = topBar.findViewById(R.id.btnTopBarSearch);
        View containerSearch = root.findViewById(R.id.containerBrandSearchBar);
        if (btnSearch != null && containerSearch != null) {
            btnSearch.setOnClickListener(v -> {
                int visibility = containerSearch.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                containerSearch.setVisibility(visibility);
            });
        }
    }

    private void setupHeroSlider(View root) {
        vpBrandBanner = root.findViewById(R.id.vpBrandBanner);
        if (vpBrandBanner == null) return;

        indicators.clear();
        indicators.add(root.findViewById(R.id.indicator0));
        indicators.add(root.findViewById(R.id.indicator1));
        indicators.add(root.findViewById(R.id.indicator2));

        bannerAdapter = new CategoryBannerAdapter();
        vpBrandBanner.setAdapter(bannerAdapter);

        List<HomeBannerItem> items = new ArrayList<>();
        // Using existing slide images as requested
        items.add(new HomeBannerItem("1", "", "", "", "Xem ngay", null, R.drawable.img_brand_1, "promotion", "4", true, 1));
        items.add(new HomeBannerItem("2", "", "", "", "Xem ngay", null, R.drawable.img_brand_2, "promotion", "5", true, 2));
        items.add(new HomeBannerItem("3", "", "", "", "Xem ngay", null, R.drawable.img_brand_3, "promotion", "1", true, 3));
        items.add(new HomeBannerItem("4", "", "", "", "Xem ngay", null, R.drawable.img_brand_4, "promotion", "1", true, 3));

        bannerAdapter.setItems(items);

        int startPosition = (Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % items.size());
        vpBrandBanner.setCurrentItem(startPosition, false);
        updateIndicators(startPosition % items.size());

        setupAutoSlide(items.size());

        vpBrandBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int realPos = position % items.size();
                updateIndicators(realPos);
                
                autoSlideHandler.removeCallbacks(autoSlideRunnable);
                if (autoSlideRunnable != null) {
                    autoSlideHandler.postDelayed(autoSlideRunnable, 4000);
                }
            }
        });
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicators.size(); i++) {
            View indicator = indicators.get(i);
            if (indicator != null) {
                if (i == position) {
                    indicator.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.accent_dark)));
                } else {
                    indicator.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.border_divider)));
                }
            }
        }
    }

    private void setupAutoSlide(int size) {
        if (size <= 1) return;

        autoSlideRunnable = new Runnable() {
            @Override
            public void run() {
                if (vpBrandBanner == null) return;
                int currentItem = vpBrandBanner.getCurrentItem();
                vpBrandBanner.setCurrentItem(currentItem + 1, true);
                autoSlideHandler.postDelayed(this, 4000);
            }
        };

        autoSlideHandler.postDelayed(autoSlideRunnable, 4000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        autoSlideHandler.removeCallbacks(autoSlideRunnable);
    }

    private void setupMockData() {
        fullBrandList = new ArrayList<>();
        fullBrandList.add(new Brand("Fwee", R.drawable.brand_fee, true, getString(R.string.filter_korea)));
        fullBrandList.add(new Brand("Maybelline", R.drawable.brand_mbl, false, getString(R.string.filter_western)));
        fullBrandList.add(new Brand("Nars", R.drawable.brand_nars, false, getString(R.string.filter_western)));
        fullBrandList.add(new Brand("Judydoll", R.drawable.brand_jd, false, getString(R.string.filter_vietnam)));
        fullBrandList.add(new Brand("Anastasia", R.drawable.img_eyeshadow, false, getString(R.string.filter_western)));
        fullBrandList.add(new Brand("Hudabeauty", R.drawable.brand_hdbt, false, getString(R.string.filter_western)));
        fullBrandList.add(new Brand("Peripera", R.drawable.img_hot, false, getString(R.string.filter_korea)));
        fullBrandList.add(new Brand("Charlotte Tilbury", R.drawable.img_gift, false, getString(R.string.filter_western)));
    }

    private void setupFilterLogic() {
        if (layoutFilterChips == null) return;
        for (int i = 0; i < layoutFilterChips.getChildCount(); i++) {
            View child = layoutFilterChips.getChildAt(i);
            if (child instanceof TextView) {
                TextView chip = (TextView) child;
                chip.setOnClickListener(v -> {
                    updateFilterUI(chip);
                    filterBrands(chip.getText().toString());
                });
            }
        }
    }

    private void updateFilterUI(TextView selectedChip) {
        for (int i = 0; i < layoutFilterChips.getChildCount(); i++) {
            View child = layoutFilterChips.getChildAt(i);
            if (child instanceof TextView) {
                TextView chip = (TextView) child;
                if (chip == selectedChip) {
                    chip.setBackgroundResource(R.drawable.bg_chip_selected);
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.background_main));
                } else {
                    chip.setBackgroundResource(R.drawable.bg_chip_outline);
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
                }
            }
        }
    }

    private void filterBrands(String region) {
        List<Brand> filteredList = new ArrayList<>();
        String allText = getString(R.string.filter_all);

        if (region.equals(allText)) {
            filteredList.addAll(fullBrandList);
        } else {
            for (Brand brand : fullBrandList) {
                if (brand.getRegion() != null && brand.getRegion().equals(region)) {
                    filteredList.add(brand);
                }
            }
        }

        if (adapter == null) {
            adapter = new BrandAdapter(filteredList);
            adapter.setOnBrandClickListener(brand -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main, ProductListingFragment.newBrandInstance(brand.getBrandName()))
                            .addToBackStack(null)
                            .commit();
                }
            });
        } else {
            adapter.updateData(filteredList);
        }

        // Always set adapter to the RecyclerView because the View might have been recreated
        rvBrandGrid.setAdapter(adapter);
    }
}
