package com.example.frontend.ui.category;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.feature.search.SearchActivity;
import com.example.frontend.model.Product;
import java.util.ArrayList;
import java.util.List;
import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.data.remote.TokenManager;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.wishlist.WishlistViewModel;
import com.example.frontend.core.auth.AuthNavigationHelper;
import com.example.frontend.core.auth.PendingAuthAction;
import androidx.lifecycle.ViewModelProvider;
import com.example.frontend.ui.common.BottomNavigationHelper;

public class FaceFragment extends Fragment {

    private RecyclerView rvFaceProducts;
    private ProductAdapter adapter;
    private WishlistViewModel wishlistViewModel;
    private CartViewModel cartViewModel;
    private List<Product> allFaceProducts = new ArrayList<>();
    private View containerSearchNoResult;
    private View loadingState;
    private LinearLayout layoutFaceFilterChips;

    private TextView chipAllFace;
    private TextView chipFoundation;
    private TextView chipConcealer;
    private TextView chipPrimer;
    private TextView chipPowder;
    private TextView chipSettingSpray;
    private TextView chipBbCcCream;
    private TextView chipTintedMoisturizer;

    private ProductRepository productRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_face, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productRepository = new ProductRepository(requireContext());
        wishlistViewModel = new ViewModelProvider(this).get(WishlistViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        initViews(view);
        setupSearch(view);
        setupFilterChips();
        setupProductList();
        setupActions(view);
        loadProductsFromRepository();

        BottomNavigationHelper.setup(view, tabIndex -> {
            // Handle bottom nav
        });
        BottomNavigationHelper.setSelectedTab(view, BottomNavigationHelper.TAB_CATEGORY);
    }

    private void initViews(View root) {
        rvFaceProducts = root.findViewById(R.id.rvFaceProducts);
        containerSearchNoResult = root.findViewById(R.id.containerSearchNoResult);
        loadingState = root.findViewById(R.id.viewFaceLoading);
        layoutFaceFilterChips = root.findViewById(R.id.layoutFaceFilterChips);

        chipAllFace = root.findViewById(R.id.chipAllFace);
        chipFoundation = root.findViewById(R.id.chipFoundation);
        chipConcealer = root.findViewById(R.id.chipConcealer);
        chipPrimer = root.findViewById(R.id.chipPrimer);
        chipPowder = root.findViewById(R.id.chipPowder);
        chipSettingSpray = root.findViewById(R.id.chipSettingSpray);
        chipBbCcCream = root.findViewById(R.id.chipBbCcCream);
        chipTintedMoisturizer = root.findViewById(R.id.chipTintedMoisturizer);
        
        ImageButton btnBack = root.findViewById(R.id.btnFaceBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    private void setupSearch(View root) {
        View searchBar = root.findViewById(R.id.layoutFaceSearchBar);
        if (searchBar != null) {
            TextView tvHint = searchBar.findViewById(R.id.tvSearchHint);
            if (tvHint != null) tvHint.setText(R.string.category_face);
            
            searchBar.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), SearchActivity.class);
                intent.putExtra("initial_query", "Face");
                intent.putExtra("category", "Face");
                startActivity(intent);
            });
        }
    }

    private void setupFilterChips() {
        if (layoutFaceFilterChips == null) return;
        
        chipAllFace.setOnClickListener(v -> {
            selectChip(chipAllFace);
            showProducts(allFaceProducts);
        });

        setupChip(chipFoundation, "Foundation");
        setupChip(chipConcealer, "Concealer");
        setupChip(chipPrimer, "Primer");
        setupChip(chipPowder, "Powder");
        setupChip(chipSettingSpray, "Setting Spray");
        setupChip(chipBbCcCream, "BB & CC Cream");
        setupChip(chipTintedMoisturizer, "Tinted Moisturizer");
    }

    private void setupChip(TextView chip, String subcategory) {
        if (chip == null) return;
        chip.setOnClickListener(v -> filterBySubcategory(chip, subcategory));
    }

    private void filterBySubcategory(TextView selectedChip, String subcategory) {
        selectChip(selectedChip);

        List<Product> filtered = new ArrayList<>();
        for (Product product : allFaceProducts) {
            if (subcategory.equalsIgnoreCase(product.getSubcategory())) {
                filtered.add(product);
            }
        }

        showProducts(filtered);
    }

    private void selectChip(TextView selectedChip) {
        resetAllFaceChips();

        selectedChip.setSelected(true);
        selectedChip.setBackgroundResource(R.drawable.bg_chip_selected);
        selectedChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.background_main));
    }

    private void resetAllFaceChips() {
        TextView[] chips = {
            chipAllFace,
            chipFoundation,
            chipConcealer,
            chipPrimer,
            chipPowder,
            chipSettingSpray,
            chipBbCcCream,
            chipTintedMoisturizer
        };

        for (TextView chip : chips) {
            if (chip == null) continue;
            chip.setSelected(false);
            chip.setBackgroundResource(R.drawable.bg_chip_outline);
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        }
    }

    private void setupProductList() {
        adapter = new ProductAdapter();
        adapter.setOnWishlistClickListener((product, position) -> {
            if (TokenManager.getInstance(requireContext()).isLoggedIn()) {
                wishlistViewModel.toggleWishlist(product.getId(), product.isFavorite());
                product.setFavorite(!product.isFavorite());
                adapter.notifyItemChanged(position);
                String msg = product.isFavorite() ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích";
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            } else {
                Bundle extras = new Bundle();
                extras.putString("productId", product.getId());
                extras.putBoolean("wasWishlisted", product.isFavorite());
                PendingAuthAction action = new PendingAuthAction(PendingAuthAction.ActionType.ADD_TO_WISHLIST, "FaceFragment", R.id.main, extras);
                AuthNavigationHelper.showAuthPrompt(requireActivity(), action);
            }
        });
        adapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main, com.example.frontend.feature.product.ProductDetailFragment.newInstance(product.getId()))
                            .addToBackStack(null)
                            .commit();
                }
            }

            @Override
            public void onAddToCartClick(Product product) {
                handleAddToCart(product);
            }
        });
        rvFaceProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvFaceProducts.setAdapter(adapter);
    }

    private void handleAddToCart(Product product) {
        if (product == null || product.getId() == null) return;

        AddToCartRequest request = new AddToCartRequest(product.getId(), null, 1);
        cartViewModel.addToCart(request);

        cartViewModel.getCartResult().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<NetworkResult<com.example.frontend.data.model.cart.CartDto>>() {
            @Override
            public void onChanged(NetworkResult<com.example.frontend.data.model.cart.CartDto> result) {
                if (result == null) return;
                if (result.status == NetworkResult.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    cartViewModel.getCartResult().removeObserver(this);
                } else if (result.status == NetworkResult.Status.ERROR) {
                    Toast.makeText(requireContext(), result.message != null ? result.message : "Lỗi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
                    cartViewModel.getCartResult().removeObserver(this);
                }
            }
        });
    }

    private void loadProductsFromRepository() {
        // Fetch products for "Face" category.
        // Usually, the category name is "Face" and slug might be "face".
        // Trying "face" first as a slug.
        String categorySlug = "face";
        android.util.Log.d("FaceFragment", "Loading products for category: " + categorySlug);
        
        showLoading(true);
        productRepository.getProducts(null, categorySlug, null).observe(getViewLifecycleOwner(), result -> {
            if (result == null) {
                showLoading(false);
                android.util.Log.e("FaceFragment", "NetworkResult is null");
                return;
            }

            android.util.Log.d("FaceFragment", "Status: " + result.status + ", Message: " + result.message);

            switch (result.status) {
                case SUCCESS:
                    showLoading(false);
                    if (result.data != null && !result.data.isEmpty()) {
                        android.util.Log.d("FaceFragment", "Received " + result.data.size() + " products");
                        allFaceProducts = result.data;
                        selectChip(chipAllFace);
                        showProducts(allFaceProducts);
                    } else {
                        android.util.Log.d("FaceFragment", "Data is empty, showing no result container");
                        showProducts(new ArrayList<>());
                    }
                    break;
                case EMPTY:
                    showLoading(false);
                    android.util.Log.d("FaceFragment", "Category returned empty results");
                    showProducts(new ArrayList<>());
                    break;
                case ERROR:
                    showLoading(false);
                    android.util.Log.e("FaceFragment", "Error loading products: " + result.message);
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
                case NO_INTERNET:
                    showLoading(false);
                    Toast.makeText(getContext(), R.string.error_no_internet, Toast.LENGTH_SHORT).show();
                    break;
                case LOADING:
                    // Already showing loading
                    break;
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (loadingState != null) loadingState.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            rvFaceProducts.setVisibility(View.GONE);
            containerSearchNoResult.setVisibility(View.GONE);
        } else {
            rvFaceProducts.setVisibility(View.VISIBLE);
        }
    }

    private void setupActions(View root) {
        View layoutFilter = root.findViewById(R.id.layoutFilterAction);
        if (layoutFilter != null) {
            layoutFilter.setOnClickListener(v -> {
                FilterBottomSheetDialog dialog = new FilterBottomSheetDialog();
                dialog.setOnFilterAppliedListener(filterState -> {
                    Toast.makeText(getContext(), "Đã áp dụng bộ lọc", Toast.LENGTH_SHORT).show();
                });
                dialog.show(getChildFragmentManager(), "FilterBottomSheet");
            });
        }

        View layoutSort = root.findViewById(R.id.layoutSortAction);
        if (layoutSort != null) {
            layoutSort.setOnClickListener(v -> Toast.makeText(getContext(), "Mở sắp xếp", Toast.LENGTH_SHORT).show());
        }
    }

    private void showProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            rvFaceProducts.setVisibility(View.GONE);
            containerSearchNoResult.setVisibility(View.VISIBLE);
            adapter.setProducts(new ArrayList<>());
            return;
        }

        containerSearchNoResult.setVisibility(View.GONE);
        rvFaceProducts.setVisibility(View.VISIBLE);
        adapter.setProducts(new ArrayList<>(products));
    }
}
