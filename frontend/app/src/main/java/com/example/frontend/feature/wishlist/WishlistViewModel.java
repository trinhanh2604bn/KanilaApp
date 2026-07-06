package com.example.frontend.feature.wishlist;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.WishlistRepository;
import com.example.frontend.data.model.wishlist.WishlistActionResponse;
import com.example.frontend.data.model.wishlist.WishlistItemResponse;
import java.util.List;

public class WishlistViewModel extends AndroidViewModel {
    private final WishlistRepository repository;
    private final MutableLiveData<NetworkResult<List<WishlistItemResponse>>> wishlistResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<WishlistActionResponse>> toggleResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<Object>> bulkDeleteResult = new MutableLiveData<>();
    private String currentSort = "latest";

    public WishlistViewModel(@NonNull Application application) {
        super(application);
        this.repository = new WishlistRepository(application);
    }

    public LiveData<NetworkResult<List<WishlistItemResponse>>> getWishlistResult() {
        return wishlistResult;
    }

    public LiveData<NetworkResult<WishlistActionResponse>> getToggleResult() {
        return toggleResult;
    }

    public LiveData<NetworkResult<Object>> getBulkDeleteResult() {
        return bulkDeleteResult;
    }

    public void loadWishlist() {
        repository.getMyWishlistItems(currentSort, wishlistResult);
    }

    public void setSort(String sort) {
        this.currentSort = sort;
        loadWishlist();
    }

    public void toggleWishlist(String productId, boolean isWishlisted) {
        if (isWishlisted) {
            // Already wishlisted, so remove it
            repository.removeFromWishlist(productId, new MutableLiveData<NetworkResult<Void>>() {
                @Override
                protected void onActive() {
                    // We don't really need a separate LiveData for each toggle if we just want to update local state
                    // but for global sync it's better.
                }
            });
        } else {
            repository.addToWishlist(productId, toggleResult);
        }
    }
    
    public void removeFromWishlist(String productId) {
        repository.removeFromWishlist(productId, new MutableLiveData<NetworkResult<Void>>());
    }

    public void bulkDelete(List<String> itemIds) {
        repository.bulkDeleteWishlistItems(itemIds, bulkDeleteResult);
    }
}
