package com.example.frontend.feature.search;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.HomeRepository;
import com.example.frontend.model.Product;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private final HomeRepository homeRepository;
    private final MutableLiveData<NetworkResult<List<Product>>> searchResults = new MutableLiveData<>();

    public SearchViewModel(@NonNull Application application) {
        super(application);
        this.homeRepository = new HomeRepository(application);
    }

    public LiveData<NetworkResult<List<Product>>> getSearchResults() {
        return searchResults;
    }

    public void searchProducts(String query) {
        homeRepository.getProducts(query, searchResults);
    }
}
