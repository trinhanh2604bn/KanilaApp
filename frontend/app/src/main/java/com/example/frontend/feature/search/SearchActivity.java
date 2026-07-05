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
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
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
    private RecyclerView rvSuggestedProducts;
    private View layoutSearchNoResult;
    
    private SearchHistoryAdapter historyAdapter;
    private SearchSuggestionAdapter suggestionAdapter;
    private SearchSuggestedProductAdapter productAdapter;

    private List<Product> allMockProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        loadAllMockProducts();
        initViews();
        setupSearchHeader();
        setupHistory();
        setupSuggestions();
        setupSuggestedProducts();
        
        handleIntent(getIntent());
    }

    private void loadAllMockProducts() {
        allMockProducts.clear();
        // Face category
        allMockProducts.add(new Product("f1", "BeautyBlender", "Bounce Liquid Foundation", "450000", "4.5", "1.2k", R.drawable.img_foudation, "New", "Foundation"));
        allMockProducts.add(new Product("f2", "Maybelline", "Fit Me Matte + Poreless", "250000", "4.8", "5.1k", R.drawable.img_brand_1, "Best Seller", "Foundation"));
        allMockProducts.add(new Product("f3", "BeautyBlender", "Phấn phủ BOUNCE Soft Focus", "450000", "4.2", "800", R.drawable.img_foudation, "", "Powder"));
        allMockProducts.add(new Product("f4", "Huda Beauty", "Easy Bake Loose Powder", "950000", "4.9", "12k", R.drawable.img_brand_2, "Hot", "Powder"));
        allMockProducts.add(new Product("f5", "Nars", "Radiant Creamy Concealer", "850000", "4.8", "3.2k", R.drawable.brand_nars, "Essential", "Concealer"));
        allMockProducts.add(new Product("f6", "Benefit", "The POREfessional Face Primer", "750000", "4.7", "2.1k", R.drawable.img_brand_3, "", "Primer"));
        
        // Other categories
        allMockProducts.add(new Product("L1", "L'Oreal", "Son Rouge Signature", "280000", "4.7", "3.2k", R.drawable.img_lipstick, "Sale", "Lipstick"));
        allMockProducts.add(new Product("E1", "Anastasia", "Modern Renaissance Palette", "1200000", "4.9", "8k", R.drawable.img_eyeshadow, "Best", "Eyeshadow"));
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
        rvSuggestedProducts = findViewById(R.id.rvSuggestedProducts);
        layoutSearchNoResult = findViewById(R.id.layoutSearchNoResult);
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
                edtExpandedSearchQuery.setHint(initialQuery);
                // We don't perform search automatically unless requested
            }
        }
        focusSearchInput();
    }

    private void setupHistory() {
        historyAdapter = new SearchHistoryAdapter();
        rvSearchHistory.setAdapter(historyAdapter);
        
        List<String> historyItems = new ArrayList<>();
        historyItems.add("kem chống nắng");
        historyItems.add("son dưỡng");
        historyItems.add("serum B5");
        
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
        suggestionItems.add("Phấn phủ");
        
        suggestionAdapter.setItems(suggestionItems);
        suggestionAdapter.setOnSuggestionClickListener(this::performSearch);
    }

    private void setupSuggestedProducts() {
        productAdapter = new SearchSuggestedProductAdapter();
        rvSuggestedProducts.setAdapter(productAdapter);
        // Show all products by default as "suggestions"
        productAdapter.setItems(new ArrayList<>(allMockProducts));
        productAdapter.setOnProductClickListener(product -> {
            Toast.makeText(this, "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
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
            showResults(allMockProducts);
            return;
        }

        edtExpandedSearchQuery.setText(keyword);
        edtExpandedSearchQuery.setSelection(keyword.length());
        
        List<Product> results = new ArrayList<>();
        for (Product product : allMockProducts) {
            if (product.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                product.getBrand().toLowerCase().contains(keyword.toLowerCase()) ||
                product.getSubcategory().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(product);
            }
        }

        showResults(results);
        
        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtExpandedSearchQuery.getWindowToken(), 0);
        }
    }

    private void showResults(List<Product> results) {
        sectionSearchHistory.setVisibility(View.GONE);
        sectionSearchSuggestions.setVisibility(View.GONE);
        
        if (results.isEmpty()) {
            sectionSuggestedProducts.setVisibility(View.GONE);
            layoutSearchNoResult.setVisibility(View.VISIBLE);
        } else {
            sectionSuggestedProducts.setVisibility(View.VISIBLE);
            layoutSearchNoResult.setVisibility(View.GONE);
            productAdapter.setItems(results);
            // Optionally change title to "Search Results"
            // TextView tvTitle = findViewById(R.id.tvSuggestedTitle); ...
        }
    }
}
