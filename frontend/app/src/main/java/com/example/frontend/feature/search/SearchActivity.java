package com.example.frontend.feature.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.ActivityNotFoundException;
import android.speech.RecognizerIntent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.core.auth.AuthNavigationHelper;
import com.example.frontend.core.auth.PendingAuthAction;
import com.example.frontend.data.local.AppDatabase;
import com.example.frontend.data.local.SearchHistoryEntity;
import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.data.model.search.SearchResponse;
import com.example.frontend.data.model.search.SearchSuggestionResponse;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.remote.TokenManager;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.feature.wishlist.WishlistViewModel;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {

    // ─── Extra key used when launching from home/other screens ───────────────
    public static final String EXTRA_INITIAL_QUERY = "initial_query";

    // ─── Views ────────────────────────────────────────────────────────────────
    private EditText edtExpandedSearchQuery;
    private ImageButton btnExpandedSearchBack;
    private ImageButton btnClearHistory;
    private View sectionSearchHistory;
    private View sectionSearchSuggestions;
    private View sectionRecommendProducts;
    private View sectionSuggestedProducts;

    private RecyclerView rvSearchHistory;
    private RecyclerView rvSearchSuggestions;
    private RecyclerView rvRecommendProducts;
    private RecyclerView rvSuggestedProducts;
    private View layoutSearchNoResult;
    private View layoutSearchLoading;
    private View scrollSearchContent;

    // ─── Adapters ─────────────────────────────────────────────────────────────
    private SearchHistoryAdapter historyAdapter;
    private SearchSuggestionAdapter suggestionAdapter;
    private SearchRecommendProductAdapter recommendAdapter;
    private SearchSuggestedProductAdapter productAdapter;

    // ─── ViewModels ───────────────────────────────────────────────────────────
    private SearchViewModel viewModel;
    private WishlistViewModel wishlistViewModel;
    private CartViewModel cartViewModel;

    // ─── State ────────────────────────────────────────────────────────────────
    private boolean isShowingResults = false;
    private boolean historyObserved = false;

    // ─── Voice Search ─────────────────────────────────────────────────────────
    private ActivityResultLauncher<Intent> voiceSearchLauncher;
    private boolean isVoiceSearchActive = false;
    private ImageButton btnExpandedSearchVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        wishlistViewModel = new ViewModelProvider(this).get(WishlistViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        setupVoiceSearchLauncher();
        initViews();
        setupSearchBar();
        setupHistory();
        setupSuggestions();
        setupRecommendProducts();
        setupResultsList();

        observeViewModel();
        handleIntent(getIntent());
    }

    // ─── View Initialization ──────────────────────────────────────────────────

    private void initViews() {
        edtExpandedSearchQuery  = findViewById(R.id.edtExpandedSearchQuery);
        btnExpandedSearchBack   = findViewById(R.id.btnExpandedSearchBack);
        btnClearHistory         = findViewById(R.id.btnClearHistory);
        sectionSearchHistory    = findViewById(R.id.sectionSearchHistory);
        sectionSearchSuggestions = findViewById(R.id.sectionSearchSuggestions);
        sectionRecommendProducts = findViewById(R.id.sectionRecommendProducts);
        sectionSuggestedProducts = findViewById(R.id.sectionSuggestedProducts);

        rvSearchHistory       = findViewById(R.id.rvSearchHistory);
        rvSearchSuggestions   = findViewById(R.id.rvSearchSuggestions);
        rvRecommendProducts   = findViewById(R.id.rvRecommendProducts);
        rvSuggestedProducts   = findViewById(R.id.rvSuggestedProducts);
        layoutSearchNoResult  = findViewById(R.id.layoutSearchNoResult);
        layoutSearchLoading   = findViewById(R.id.layoutSearchLoading);
        scrollSearchContent   = findViewById(R.id.scrollSearchContent);
    }

    private void setupVoiceSearchLauncher() {
        voiceSearchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                isVoiceSearchActive = false;
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && !matches.isEmpty()) {
                        String spokenText = matches.get(0);
                        if (spokenText != null && !spokenText.trim().isEmpty()) {
                            edtExpandedSearchQuery.setText(spokenText);
                            edtExpandedSearchQuery.setSelection(spokenText.length());
                            hideKeyboard();
                            
                            // Hide discovery, show loading
                            sectionSearchSuggestions.setVisibility(View.GONE);
                            sectionSearchHistory.setVisibility(View.GONE);
                            sectionRecommendProducts.setVisibility(View.GONE);
                            
                            viewModel.submitVoiceSearch(spokenText);
                        } else {
                            Toast.makeText(this, getString(R.string.voice_search_no_result), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.voice_search_no_result), Toast.LENGTH_SHORT).show();
                    }
                } else if (result.getResultCode() == RESULT_CANCELED) {
                    // Do nothing
                } else {
                    Toast.makeText(this, getString(R.string.voice_search_error), Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    // ─── Search Bar ───────────────────────────────────────────────────────────

    private void launchVoiceSearch() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_search_prompt));
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                isVoiceSearchActive = true;
                voiceSearchLauncher.launch(intent);
            } catch (ActivityNotFoundException e) {
                isVoiceSearchActive = false;
                Toast.makeText(this, getString(R.string.voice_search_not_supported), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.voice_search_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSearchBar() {
        btnExpandedSearchBack.setOnClickListener(v -> finish());

        btnExpandedSearchVoice = findViewById(R.id.btnExpandedSearchVoice);
        if (btnExpandedSearchVoice != null) {
            btnExpandedSearchVoice.setOnClickListener(v -> {
                if (isVoiceSearchActive) return;
                
                android.content.SharedPreferences prefs = getSharedPreferences("kanila_prefs", MODE_PRIVATE);
                boolean hasSeenVoicePrompt = prefs.getBoolean("has_seen_voice_prompt", false);
                
                if (hasSeenVoicePrompt) {
                    launchVoiceSearch();
                } else {
                    VoicePermissionBottomSheet bottomSheet = new VoicePermissionBottomSheet();
                    bottomSheet.setOnAllowListener(() -> {
                        prefs.edit().putBoolean("has_seen_voice_prompt", true).apply();
                        launchVoiceSearch();
                    });
                    bottomSheet.show(getSupportFragmentManager(), "VoicePermission");
                }
            });
        }

        // Submit on keyboard action
        edtExpandedSearchQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String query = edtExpandedSearchQuery.getText().toString().trim();
                if (!query.isEmpty()) performSearch(query);
                return true;
            }
            return false;
        });

        // Search button (if present in view_search_expanded_bar)
        View btnSubmit = findViewById(R.id.btnExpandedSearchSubmit);
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                String query = edtExpandedSearchQuery.getText().toString().trim();
                if (!query.isEmpty()) performSearch(query);
            });
        }

        // Text change listener: show history when empty, debounce suggestions otherwise
        edtExpandedSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    // Returned to empty state
                    isShowingResults = false;
                    sectionSearchSuggestions.setVisibility(View.GONE);
                    sectionSuggestedProducts.setVisibility(View.GONE);
                    if (layoutSearchNoResult != null) layoutSearchNoResult.setVisibility(View.GONE);
                    showDiscoveryState();
                } else {
                    sectionSearchHistory.setVisibility(View.GONE);
                    viewModel.getSuggestionsDebounced(query);
                }
            }
        });
    }

    // ─── History ──────────────────────────────────────────────────────────────

    private void setupHistory() {
        historyAdapter = new SearchHistoryAdapter();
        if (rvSearchHistory.getLayoutManager() == null) {
            rvSearchHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
        rvSearchHistory.setAdapter(historyAdapter);
        historyAdapter.setOnHistoryClickListener(this::performSearch);
        historyAdapter.setOnDeleteClickListener(query -> {
            String normalized = query.trim().toLowerCase();
            Executors.newSingleThreadExecutor().execute(() ->
                AppDatabase.getDatabase(this).searchHistoryDao().deleteByQuery(normalized)
            );
        });

        btnClearHistory.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() ->
                AppDatabase.getDatabase(this).searchHistoryDao().clearAll()
            );
        });
    }

    private void observeHistory() {
        if (historyObserved) return;
        historyObserved = true;
        AppDatabase.getDatabase(this).searchHistoryDao().getRecentSearches().observe(this, historyEntities -> {
            List<String> historyStrings = new ArrayList<>();
            for (SearchHistoryEntity entity : historyEntities) {
                historyStrings.add(entity.displayQuery);
            }
            historyAdapter.setItems(historyStrings);
            if (historyStrings.isEmpty()) {
                sectionSearchHistory.setVisibility(View.GONE);
            } else if (!isShowingResults && edtExpandedSearchQuery.getText().toString().trim().isEmpty()) {
                sectionSearchHistory.setVisibility(View.VISIBLE);
            }
        });
    }

    // ─── Suggestions ──────────────────────────────────────────────────────────

    private void setupSuggestions() {
        suggestionAdapter = new SearchSuggestionAdapter();
        if (rvSearchSuggestions.getLayoutManager() == null) {
            rvSearchSuggestions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
        rvSearchSuggestions.setAdapter(suggestionAdapter);
        suggestionAdapter.setOnSuggestionClickListener(this::performSearch);
    }

    // ─── Discovery ────────────────────────────────────────────────────────────

    private void setupRecommendProducts() {
        recommendAdapter = new SearchRecommendProductAdapter();
        rvRecommendProducts.setAdapter(recommendAdapter);
        recommendAdapter.setOnProductClickListener(new SearchRecommendProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                navigateToProduct(product);
            }
            @Override
            public void onAddToCartClick(Product product) {
                handleAddToCart(product);
            }
        });
        recommendAdapter.setOnWishlistClickListener(SearchActivity.this::onWishlistClick);
    }

    // ─── Results List ─────────────────────────────────────────────────────────

    private void setupResultsList() {
        productAdapter = new SearchSuggestedProductAdapter();
        rvSuggestedProducts.setAdapter(productAdapter);
        productAdapter.setOnProductClickListener(new SearchSuggestedProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                navigateToProduct(product);
            }
            @Override
            public void onAddToCartClick(Product product) {
                handleAddToCart(product);
            }
        });
        productAdapter.setOnWishlistClickListener(this::onWishlistClick);

        // Pagination on scroll
        rvSuggestedProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore();
                }
            }
        });
    }

    // ─── ViewModel observation ────────────────────────────────────────────────

    private void observeViewModel() {
        viewModel.getSearchResults().observe(this, result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    showLoading();
                    break;
                case SUCCESS:
                    showResults(result.data != null ? result.data.items : new ArrayList<>());
                    break;
                case EMPTY:
                    showNoResult();
                    break;
                case ERROR:
                    hideLoading();
                    Toast.makeText(this, result.message != null ? result.message : getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    hideLoading();
                    break;
            }
        });

        viewModel.getSuggestionResults().observe(this, result -> {
            if (result == null) return;
            if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                SearchSuggestionResponse data = result.data;
                List<String> suggestions = new ArrayList<>();
                // Add query suggestions first
                if (data.querySuggestions != null) suggestions.addAll(data.querySuggestions);
                // Add product names
                if (data.products != null) {
                    for (SearchSuggestionResponse.SuggestedProduct p : data.products) {
                        if (p.name != null && !suggestions.contains(p.name)) suggestions.add(p.name);
                    }
                }
                // Limit to 10
                if (suggestions.size() > 10) suggestions = suggestions.subList(0, 10);

                if (!suggestions.isEmpty()) {
                    suggestionAdapter.setItems(suggestions);
                    sectionSearchSuggestions.setVisibility(View.VISIBLE);
                } else {
                    sectionSearchSuggestions.setVisibility(View.GONE);
                }
            } else {
                sectionSearchSuggestions.setVisibility(View.GONE);
            }
        });

        viewModel.getDiscoveryProducts().observe(this, result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                recommendAdapter.setItems(result.data);
            }
        });
    }

    // ─── Intent handling ──────────────────────────────────────────────────────

    private void handleIntent(Intent intent) {
        if (intent != null) {
            String initialQuery = intent.getStringExtra(EXTRA_INITIAL_QUERY);
            if (initialQuery != null && !initialQuery.isEmpty()) {
                edtExpandedSearchQuery.setText(initialQuery);
                performSearch(initialQuery);
                return;
            }
        }
        // Default: show discovery state
        showDiscoveryState();
        focusSearchInput();
    }

    // ─── UI State transitions ─────────────────────────────────────────────────

    private void showDiscoveryState() {
        if (layoutSearchLoading != null) layoutSearchLoading.setVisibility(View.GONE);
        if (layoutSearchNoResult != null) layoutSearchNoResult.setVisibility(View.GONE);
        sectionSuggestedProducts.setVisibility(View.GONE);
        sectionSearchSuggestions.setVisibility(View.GONE);
        if (scrollSearchContent != null) scrollSearchContent.setVisibility(View.VISIBLE);
        sectionRecommendProducts.setVisibility(View.VISIBLE);

        observeHistory();
        viewModel.loadDiscovery();
    }

    private void showLoading() {
        if (layoutSearchLoading != null) layoutSearchLoading.setVisibility(View.VISIBLE);
        if (scrollSearchContent != null) scrollSearchContent.setVisibility(View.GONE);
        if (layoutSearchNoResult != null) layoutSearchNoResult.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (layoutSearchLoading != null) layoutSearchLoading.setVisibility(View.GONE);
    }

    private void showResults(List<Product> results) {
        hideLoading();
        isShowingResults = true;
        if (scrollSearchContent != null) scrollSearchContent.setVisibility(View.VISIBLE);
        sectionSearchHistory.setVisibility(View.GONE);
        sectionSearchSuggestions.setVisibility(View.GONE);
        sectionRecommendProducts.setVisibility(View.GONE);

        if (results == null || results.isEmpty()) {
            sectionSuggestedProducts.setVisibility(View.GONE);
            if (layoutSearchNoResult != null) layoutSearchNoResult.setVisibility(View.VISIBLE);
        } else {
            sectionSuggestedProducts.setVisibility(View.VISIBLE);
            if (layoutSearchNoResult != null) layoutSearchNoResult.setVisibility(View.GONE);
            productAdapter.setItems(results);
        }
    }

    private void showNoResult() {
        hideLoading();
        isShowingResults = true;
        if (scrollSearchContent != null) scrollSearchContent.setVisibility(View.VISIBLE);
        sectionSearchHistory.setVisibility(View.GONE);
        sectionSearchSuggestions.setVisibility(View.GONE);
        sectionRecommendProducts.setVisibility(View.GONE);
        sectionSuggestedProducts.setVisibility(View.GONE);
        if (layoutSearchNoResult != null) layoutSearchNoResult.setVisibility(View.VISIBLE);
    }

    // ─── Search execution ─────────────────────────────────────────────────────

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;
        String trimmed = query.trim();

        hideKeyboard();

        // Update search bar text
        edtExpandedSearchQuery.setText(trimmed);
        edtExpandedSearchQuery.setSelection(trimmed.length());

        // Save to history (background thread)
        String normalizedQuery = trimmed.toLowerCase();
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            SearchHistoryEntity existing = db.searchHistoryDao().getByNormalizedQuery(normalizedQuery);
            if (existing != null) {
                existing.timestamp = System.currentTimeMillis();
                db.searchHistoryDao().update(existing);
            } else {
                db.searchHistoryDao().insert(
                    new SearchHistoryEntity(trimmed, normalizedQuery, System.currentTimeMillis())
                );
            }
        });

        // Hide discovery, show loading
        sectionSearchSuggestions.setVisibility(View.GONE);
        sectionSearchHistory.setVisibility(View.GONE);
        sectionRecommendProducts.setVisibility(View.GONE);

        // Navigate to MainActivity with search result fragment
        Intent intent = new Intent(this, com.example.frontend.MainActivity.class);
        intent.putExtra("TARGET_FRAGMENT", "search_results");
        intent.putExtra("search_query", trimmed);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void focusSearchInput() {
        edtExpandedSearchQuery.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(edtExpandedSearchQuery, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtExpandedSearchQuery.getWindowToken(), 0);
        }
    }

    private void navigateToProduct(Product product) {
        if (product == null || product.getId() == null) return;
        Intent intent = new Intent(this, com.example.frontend.MainActivity.class);
        intent.putExtra("TARGET_FRAGMENT", "product_detail");
        intent.putExtra("product_id", product.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void handleAddToCart(Product product) {
        if (product == null || product.getId() == null) return;
        AddToCartRequest request = new AddToCartRequest(product.getId(), null, 1);
        cartViewModel.addToCart(request);

        cartViewModel.getCartResult().observe(this, new Observer<NetworkResult<com.example.frontend.data.model.cart.CartDto>>() {
            @Override
            public void onChanged(NetworkResult<com.example.frontend.data.model.cart.CartDto> result) {
                if (result == null) return;
                if (result.status == NetworkResult.Status.SUCCESS) {
                    Toast.makeText(SearchActivity.this, getString(R.string.cart_item_added), Toast.LENGTH_SHORT).show();
                    cartViewModel.getCartResult().removeObserver(this);
                } else if (result.status == NetworkResult.Status.ERROR) {
                    Toast.makeText(SearchActivity.this,
                        result.message != null ? result.message : getString(R.string.error_generic),
                        Toast.LENGTH_SHORT).show();
                    cartViewModel.getCartResult().removeObserver(this);
                }
            }
        });
    }

    private void onWishlistClick(Product product, int position) {
        if (TokenManager.getInstance(this).isLoggedIn()) {
            wishlistViewModel.toggleWishlist(product.getId(), product.isFavorite());
            product.setFavorite(!product.isFavorite());
            if (recommendAdapter != null) recommendAdapter.notifyDataSetChanged();
            if (productAdapter != null) productAdapter.notifyDataSetChanged();
            String msg = product.isFavorite()
                ? getString(R.string.wishlist_added)
                : getString(R.string.wishlist_removed);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Bundle extras = new Bundle();
            extras.putString("productId", product.getId());
            extras.putBoolean("wasWishlisted", product.isFavorite());
            PendingAuthAction action = new PendingAuthAction(
                PendingAuthAction.ActionType.ADD_TO_WISHLIST, "SearchActivity", 0, extras);
            AuthNavigationHelper.showAuthPrompt(this, action);
        }
    }
}
