package com.example.frontend.feature.search;

import android.content.Context;
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

    private View layoutSearchExpandedBar;
    private EditText edtExpandedSearchQuery;
    private ImageButton btnExpandedSearchBack;
    private ImageButton btnClearHistory;
    private View sectionSearchHistory;
    
    private RecyclerView rvSearchHistory;
    private RecyclerView rvSearchSuggestions;
    private RecyclerView rvSuggestedProducts;
    
    private SearchHistoryAdapter historyAdapter;
    private SearchSuggestionAdapter suggestionAdapter;
    private SearchSuggestedProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupSearchHeader();
        setupHistory();
        setupSuggestions();
        setupSuggestedProducts();
        
        focusSearchInput();
    }

    private void initViews() {
        layoutSearchExpandedBar = findViewById(R.id.layoutSearchExpandedBar);
        edtExpandedSearchQuery = findViewById(R.id.edtExpandedSearchQuery);
        btnExpandedSearchBack = findViewById(R.id.btnExpandedSearchBack);
        btnClearHistory = findViewById(R.id.btnClearHistory);
        sectionSearchHistory = findViewById(R.id.sectionSearchHistory);
        
        rvSearchHistory = findViewById(R.id.rvSearchHistory);
        rvSearchSuggestions = findViewById(R.id.rvSearchSuggestions);
        rvSuggestedProducts = findViewById(R.id.rvSuggestedProducts);
    }

    private void setupSearchHeader() {
        btnExpandedSearchBack.setOnClickListener(v -> finish());
        
        // Optional: Handle search action from keyboard
        edtExpandedSearchQuery.setOnEditorActionListener((v, actionId, event) -> {
            String query = edtExpandedSearchQuery.getText().toString().trim();
            if (!query.isEmpty()) {
                performSearch(query);
            }
            return true;
        });
    }

    private void setupHistory() {
        historyAdapter = new SearchHistoryAdapter();
        rvSearchHistory.setAdapter(historyAdapter);
        
        List<String> historyItems = new ArrayList<>();
        historyItems.add("kem chống nắng");
        historyItems.add("son dưỡng");
        historyItems.add("serum B5");
        historyItems.add("nước tẩy trang");
        historyItems.add("da dầu mụn");
        
        historyAdapter.setItems(historyItems);
        historyAdapter.setOnHistoryClickListener(this::performSearch);
        
        btnClearHistory.setOnClickListener(v -> {
            historyItems.clear();
            historyAdapter.setItems(historyItems);
            sectionSearchHistory.setVisibility(View.GONE);
            Toast.makeText(this, "Đã xóa lịch sử tìm kiếm", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSuggestions() {
        suggestionAdapter = new SearchSuggestionAdapter();
        rvSearchSuggestions.setAdapter(suggestionAdapter);
        
        List<String> suggestionItems = new ArrayList<>();
        suggestionItems.add("Da dầu mụn");
        suggestionItems.add("Da nhạy cảm");
        suggestionItems.add("Dưỡng ẩm phục hồi");
        suggestionItems.add("Serum Niacinamide");
        suggestionItems.add("Son tint");
        
        suggestionAdapter.setItems(suggestionItems);
        suggestionAdapter.setOnSuggestionClickListener(this::performSearch);
    }

    private void setupSuggestedProducts() {
        productAdapter = new SearchSuggestedProductAdapter();
        rvSuggestedProducts.setAdapter(productAdapter);
        
        List<Product> sampleProducts = new ArrayList<>();
        sampleProducts.add(new Product("1", "BeautyBlender", "Bounce Liquid Foundation", "450.000đ", "4.5", "120", R.drawable.img_foudation, "New"));
        sampleProducts.add(new Product("2", "L\'Oreal", "Serum Revitalift 1.5% Hyaluronic Acid", "380.000đ", "4.8", "2.1k", R.drawable.bg_slide_2, "Sale"));
        sampleProducts.add(new Product("3", "La Roche-Posay", "Anthelios Oil Correct", "520.000đ", "4.7", "850", R.drawable.bg_slide_1, "Best"));
        sampleProducts.add(new Product("4", "Innisfree", "Green Tea Seed Serum", "410.000đ", "4.6", "1.5k", R.drawable.bg_slide_4, ""));
        
        productAdapter.setItems(sampleProducts);
        productAdapter.setOnProductClickListener(product -> {
            Toast.makeText(this, "Clicked product: " + product.getName(), Toast.LENGTH_SHORT).show();
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
        edtExpandedSearchQuery.setText(keyword);
        edtExpandedSearchQuery.setSelection(keyword.length());
        Toast.makeText(this, "Tìm kiếm: " + keyword, Toast.LENGTH_SHORT).show();
        // TODO: Navigate to SearchResultActivity or update UI
    }
}
