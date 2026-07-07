package com.example.frontend.feature.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText edtExpandedSearchQuery;
    private ImageButton btnExpandedSearchBack;
    private ImageButton btnClearHistory;
    private View sectionSearchHistory;
    private View sectionSearchSuggestions;
    private View sectionSuggestedProducts;
    
    private RecyclerView rvSearchHistory;
    private RecyclerView rvSearchSuggestions;
    private RecyclerView rvRecommendProducts;
    private RecyclerView rvQuickDiscovery;
    private RecyclerView rvSuggestedProducts;
    private View layoutSearchNoResult;
    private View layoutSearchLoading;
    
    private SearchHistoryAdapter historyAdapter;
    private SearchSuggestionAdapter suggestionAdapter;
    private SearchRecommendProductAdapter recommendAdapter;
    private SearchQuickDiscoveryAdapter discoveryAdapter;
    private SearchSuggestedProductAdapter productAdapter;

    private SearchViewModel viewModel;
    private CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        
        initViews();
        setupSearchHeader();
        setupHistory();
        setupSuggestions();
        setupRecommendProducts();
        setupQuickDiscovery();
        setupSuggestedProducts();
        
        observeViewModel();
        
        handleIntent(getIntent());
    }

    private void initViews() {
        edtExpandedSearchQuery = findViewById(R.id.edtExpandedSearchQuery);
        btnExpandedSearchBack = findViewById(R.id.btnExpandedSearchBack);
        btnClearHistory = findViewById(R.id.btnClearHistory);
        sectionSearchHistory = findViewById(R.id.sectionSearchHistory);
        sectionSearchSuggestions = findViewById(R.id.sectionSearchSuggestions);
        sectionSuggestedProducts = findViewById(R.id.sectionSuggestedProducts);
        
        rvSearchHistory = findViewById(R.id.rvSearchHistory);
        rvSearchSuggestions = findViewById(R.id.rvSearchSuggestions);
        rvRecommendProducts = findViewById(R.id.rvRecommendProducts);
        rvQuickDiscovery = findViewById(R.id.rvQuickDiscovery);
        rvSuggestedProducts = findViewById(R.id.rvSuggestedProducts);
        layoutSearchNoResult = findViewById(R.id.layoutSearchNoResult);
        layoutSearchLoading = findViewById(R.id.layoutSearchLoading);
    }

    private void setupSearchHeader() {
        btnExpandedSearchBack.setOnClickListener(v -> finish());
        
        edtExpandedSearchQuery.setOnEditorActionListener((v, actionId, event) -> {
            String query = edtExpandedSearchQuery.getText().toString().trim();
            performSearch(query);
            return true;
        });

        View btnSubmit = findViewById(R.id.btnExpandedSearchSubmit);
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                String query = edtExpandedSearchQuery.getText().toString().trim();
                performSearch(query);
            });
        }
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            String initialQuery = intent.getStringExtra("initial_query");
            if (initialQuery != null) {
                edtExpandedSearchQuery.setText(initialQuery);
                performSearch(initialQuery);
            }
        }
        focusSearchInput();
    }

    private void observeViewModel() {
        viewModel.getSearchResults().observe(this, result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    showLoading();
                    break;
                case SUCCESS:
                    showResults(result.data);
                    break;
                case EMPTY:
                    showResults(new ArrayList<>());
                    break;
                case ERROR:
                    hideLoading();
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void showLoading() {
        if (layoutSearchLoading != null) layoutSearchLoading.setVisibility(View.VISIBLE);
        sectionSearchHistory.setVisibility(View.GONE);
        sectionSearchSuggestions.setVisibility(View.GONE);
        sectionSuggestedProducts.setVisibility(View.GONE);
        layoutSearchNoResult.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (layoutSearchLoading != null) layoutSearchLoading.setVisibility(View.GONE);
    }

    private void setupHistory() {
        historyAdapter = new SearchHistoryAdapter();
        rvSearchHistory.setAdapter(historyAdapter);
        
        // This could also be in ViewModel or SharedPreferences
        List<String> historyItems = new ArrayList<>();
        historyItems.add("kem chống nắng");
        historyItems.add("son dưỡng");
        
        historyAdapter.setItems(historyItems);
        historyAdapter.setOnHistoryClickListener(this::performSearch);
        
        btnClearHistory.setOnClickListener(v -> {
            historyItems.clear();
            historyAdapter.setItems(historyItems);
            sectionSearchHistory.setVisibility(View.GONE);
        });
    }

    private void setupSuggestions() {
        suggestionAdapter = new SearchSuggestionAdapter();
        rvSearchSuggestions.setAdapter(suggestionAdapter);
        
        List<String> suggestionItems = new ArrayList<>();
        suggestionItems.add("Da dầu mụn");
        suggestionItems.add("Son tint");
        
        suggestionAdapter.setItems(suggestionItems);
        suggestionAdapter.setOnSuggestionClickListener(this::performSearch);
    }

    private void setupRecommendProducts() {
        recommendAdapter = new SearchRecommendProductAdapter();
        rvRecommendProducts.setAdapter(recommendAdapter);
        // This should eventually come from API as well
        recommendAdapter.setOnProductClickListener(new SearchRecommendProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Toast.makeText(SearchActivity.this, "Sản phẩm: " + product.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToCartClick(Product product) {
                handleAddToCart(product);
            }
        });
    }

    private void setupQuickDiscovery() {
        discoveryAdapter = new SearchQuickDiscoveryAdapter();
        rvQuickDiscovery.setAdapter(discoveryAdapter);

        List<SearchQuickDiscovery> discoveryItems = new ArrayList<>();
        // These can stay as they are likely static entry points
        discoveryItems.add(new SearchQuickDiscovery("Da dầu mụn", "Gợi ý cho bạn", R.drawable.kpn_1));
        discoveryItems.add(new SearchQuickDiscovery("Trang điểm", "Phong cách tự nhiên", R.drawable.kpn_2));

        discoveryAdapter.setItems(discoveryItems);
        discoveryAdapter.setOnItemClickListener(item -> performSearch(item.getTitle()));
    }

    private void setupSuggestedProducts() {
        productAdapter = new SearchSuggestedProductAdapter();
        rvSuggestedProducts.setAdapter(productAdapter);
        productAdapter.setOnProductClickListener(new SearchSuggestedProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, com.example.frontend.feature.product.ProductDetailFragment.newInstance(product.getId()))
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onAddToCartClick(Product product) {
                handleAddToCart(product);
            }
        });
    }

    private void focusSearchInput() {
        edtExpandedSearchQuery.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(edtExpandedSearchQuery, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void performSearch(String keyword) {
        if (keyword.isEmpty()) {
            sectionSearchHistory.setVisibility(View.VISIBLE);
            sectionSearchSuggestions.setVisibility(View.VISIBLE);
            sectionSuggestedProducts.setVisibility(View.GONE);
            return;
        }

        edtExpandedSearchQuery.setText(keyword);
        edtExpandedSearchQuery.setSelection(keyword.length());
        
        viewModel.searchProducts(keyword);
        
        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtExpandedSearchQuery.getWindowToken(), 0);
        }
    }

    private void showResults(List<Product> results) {
        hideLoading();
        sectionSearchHistory.setVisibility(View.GONE);
        sectionSearchSuggestions.setVisibility(View.GONE);
        
        if (results == null || results.isEmpty()) {
            sectionSuggestedProducts.setVisibility(View.GONE);
            layoutSearchNoResult.setVisibility(View.VISIBLE);
        } else {
            sectionSuggestedProducts.setVisibility(View.VISIBLE);
            layoutSearchNoResult.setVisibility(View.GONE);
            productAdapter.setItems(results);
        }
    }

    private void handleAddToCart(Product product) {
        if (product.getId() == null) return;

        AddToCartRequest request = new AddToCartRequest(product.getId(), null, 1);
        cartViewModel.addToCart(request);

        cartViewModel.getCartResult().observe(this, new androidx.lifecycle.Observer<NetworkResult<com.example.frontend.data.model.cart.CartDto>>() {
            @Override
            public void onChanged(NetworkResult<com.example.frontend.data.model.cart.CartDto> result) {
                if (result == null) return;
                if (result.status == NetworkResult.Status.SUCCESS) {
                    Toast.makeText(SearchActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    cartViewModel.getCartResult().removeObserver(this);
                } else if (result.status == NetworkResult.Status.ERROR) {
                    Toast.makeText(SearchActivity.this, result.message != null ? result.message : "Lỗi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
                    cartViewModel.getCartResult().removeObserver(this);
                }
            }
        });
    }
}
