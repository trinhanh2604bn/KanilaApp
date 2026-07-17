package com.example.frontend.feature.search;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.frontend.data.model.search.SearchResponse;
import com.example.frontend.data.model.search.SearchSuggestionResponse;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.model.Product;

import retrofit2.Call;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private final SearchRepository searchRepository;

    // ─── Search results ───────────────────────────────────────────────────────
    private final MutableLiveData<NetworkResult<SearchResponse>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<SearchSuggestionResponse>> suggestionResults = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<Product>>> discoveryProducts = new MutableLiveData<>();

    // ─── In-flight calls ──────────────────────────────────────────────────────
    private Call<?> currentSearchCall;
    private Call<?> currentSuggestionCall;
    private Call<?> currentDiscoveryCall;

    // ─── Debounce ─────────────────────────────────────────────────────────────
    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable suggestionRunnable;

    // ─── Pagination state ─────────────────────────────────────────────────────
    private int currentPage  = 1;
    private int pageSize     = 20;
    private boolean hasMore  = true;
    private boolean isLoadingMore    = false;
    private boolean isInitialLoading = true;

    // ─── Filter/sort state ────────────────────────────────────────────────────
    private String currentQuery      = "";
    private String currentSort       = "relevance";
    private String currentBrandIds   = null;
    private String currentCategoryIds = null;
    private String currentMinPrice   = null;
    private String currentMaxPrice   = null;
    private String currentMinRating  = null;

    // Makeup-specific filters
    private String currentFinishTypes    = null;
    private String currentCoverageLevels = null;
    private String currentColorFamilies  = null;
    private String currentShadeCodes     = null;
    private Boolean currentInStock       = null;
    private Boolean currentOnSale        = null;
    private Boolean currentArSupported   = null;
    private Boolean currentWaterproof    = null;
    private Boolean currentLongWear      = null;

    // ─── Cumulative product list for pagination ───────────────────────────────
    private final List<Product> cumulativeProducts = new ArrayList<>();

    // ─── Discovery loaded flag ────────────────────────────────────────────────
    private boolean discoveryLoaded = false;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        this.searchRepository = new SearchRepository(application);
    }

    // ─── Exposed LiveData ─────────────────────────────────────────────────────

    public LiveData<NetworkResult<SearchResponse>> getSearchResults() {
        return searchResults;
    }

    public LiveData<NetworkResult<SearchSuggestionResponse>> getSuggestionResults() {
        return suggestionResults;
    }

    public LiveData<NetworkResult<List<Product>>> getDiscoveryProducts() {
        return discoveryProducts;
    }

    public void searchProducts(String query) {
        resetPagination();
        this.currentQuery = query;
        executeSearch();
    }

    public void submitVoiceSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;
        String trimmed = query.trim();

        // Cancel pending autocomplete debounce
        if (suggestionRunnable != null) {
            debounceHandler.removeCallbacks(suggestionRunnable);
        }
        // Cancel the active suggestion request
        if (currentSuggestionCall != null && !currentSuggestionCall.isExecuted()) {
            currentSuggestionCall.cancel();
        }

        // Save Search History once
        String normalizedQuery = trimmed.toLowerCase();
        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            com.example.frontend.data.local.AppDatabase db = com.example.frontend.data.local.AppDatabase.getDatabase(getApplication());
            com.example.frontend.data.local.SearchHistoryEntity existing = db.searchHistoryDao().getByNormalizedQuery(normalizedQuery);
            if (existing != null) {
                existing.timestamp = System.currentTimeMillis();
                db.searchHistoryDao().update(existing);
            } else {
                db.searchHistoryDao().insert(
                    new com.example.frontend.data.local.SearchHistoryEntity(trimmed, normalizedQuery, System.currentTimeMillis())
                );
            }
        });

        // Emit VOICE_SEARCH analytics
        searchRepository.recordEvent("VOICE_SEARCH", trimmed, java.util.UUID.randomUUID().toString());

        resetPagination();
        this.currentQuery = trimmed;
        executeSearch();
    }

    public void applyFiltersAndSort(
        String sort, String brandIds, String categoryIds,
        String minPrice, String maxPrice, String minRating,
        String finishTypes, String coverageLevels, String colorFamilies,
        String shadeCodes,
        Boolean inStock, Boolean onSale, Boolean arSupported,
        Boolean waterproof, Boolean longWear
    ) {
        this.currentSort          = sort != null ? sort : "relevance";
        this.currentBrandIds      = brandIds;
        this.currentCategoryIds   = categoryIds;
        this.currentMinPrice      = minPrice;
        this.currentMaxPrice      = maxPrice;
        this.currentMinRating     = minRating;
        this.currentFinishTypes   = finishTypes;
        this.currentCoverageLevels = coverageLevels;
        this.currentColorFamilies = colorFamilies;
        this.currentShadeCodes    = shadeCodes;
        this.currentInStock       = inStock;
        this.currentOnSale        = onSale;
        this.currentArSupported   = arSupported;
        this.currentWaterproof    = waterproof;
        this.currentLongWear      = longWear;
        resetPagination();
        executeSearch();
    }

    public void clearFilters() {
        this.currentSort           = "relevance";
        this.currentBrandIds       = null;
        this.currentCategoryIds    = null;
        this.currentMinPrice       = null;
        this.currentMaxPrice       = null;
        this.currentMinRating      = null;
        this.currentFinishTypes    = null;
        this.currentCoverageLevels = null;
        this.currentColorFamilies  = null;
        this.currentShadeCodes     = null;
        this.currentInStock        = null;
        this.currentOnSale         = null;
        this.currentArSupported    = null;
        this.currentWaterproof     = null;
        this.currentLongWear       = null;
        resetPagination();
        executeSearch();
    }

    private void resetPagination() {
        currentPage = 1;
        hasMore = true;
        cumulativeProducts.clear();
        isInitialLoading = true;
    }

    // ─── Pagination ───────────────────────────────────────────────────────────

    public void loadMore() {
        if (!hasMore || isLoadingMore || isInitialLoading) return;
        isLoadingMore = true;
        currentPage++;
        executeSearch();
    }

    // ─── Internal search execution ────────────────────────────────────────────

    private void executeSearch() {
        if (currentSearchCall != null && !currentSearchCall.isExecuted()) {
            currentSearchCall.cancel();
        }

        MutableLiveData<NetworkResult<SearchResponse>> tempResult = new MutableLiveData<>();

        if (isInitialLoading) {
            searchResults.setValue(NetworkResult.loading());
        }

        currentSearchCall = searchRepository.searchProducts(
            currentQuery, currentPage, pageSize, currentSort,
            currentBrandIds, currentCategoryIds,
            currentMinPrice, currentMaxPrice, currentMinRating,
            currentFinishTypes, currentCoverageLevels, currentColorFamilies,
            currentShadeCodes, currentInStock, currentOnSale,
            currentArSupported, currentWaterproof, currentLongWear,
            tempResult
        );

        tempResult.observeForever(result -> {
            if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                SearchResponse response = result.data;
                if (response.pagination != null) {
                    hasMore = response.pagination.hasMore;
                }
                if (response.items != null) {
                    cumulativeProducts.addAll(response.items);
                }
                response.items = new ArrayList<>(cumulativeProducts);

                if (cumulativeProducts.isEmpty()) {
                    searchResults.setValue(NetworkResult.empty());
                } else {
                    searchResults.setValue(NetworkResult.success(response));
                }
            } else if (result.status == NetworkResult.Status.ERROR && isInitialLoading) {
                searchResults.setValue(result);
            } else if (result.status == NetworkResult.Status.EMPTY && isInitialLoading) {
                searchResults.setValue(result);
            }

            isInitialLoading = false;
            isLoadingMore = false;
        });
    }

    // ─── Suggestions ──────────────────────────────────────────────────────────

    public void getSuggestionsDebounced(String query) {
        if (suggestionRunnable != null) {
            debounceHandler.removeCallbacks(suggestionRunnable);
        }

        if (query == null || query.trim().length() < 2) {
            suggestionResults.setValue(NetworkResult.empty());
            return;
        }

        suggestionRunnable = () -> {
            if (currentSuggestionCall != null && !currentSuggestionCall.isExecuted()) {
                currentSuggestionCall.cancel();
            }
            currentSuggestionCall = searchRepository.getSuggestions(query, 10, suggestionResults);
        };

        debounceHandler.postDelayed(suggestionRunnable, 350);
    }

    // ─── Discovery ────────────────────────────────────────────────────────────

    public void loadDiscovery() {
        if (discoveryLoaded) return;
        discoveryLoaded = true;

        if (currentDiscoveryCall != null && !currentDiscoveryCall.isExecuted()) {
            currentDiscoveryCall.cancel();
        }
        currentDiscoveryCall = searchRepository.getDiscovery(discoveryProducts);
    }
}
