package com.example.frontend.feature.wishlist;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.WishlistRepository;
import java.util.List;

public class WishlistViewModel extends AndroidViewModel {
    private final WishlistRepository repository;
    private final MutableLiveData<NetworkResult<List<Object>>> wishlistResult = new MutableLiveData<>();

    public WishlistViewModel(@NonNull Application application) {
        super(application);
        this.repository = new WishlistRepository(application);
    }

    public LiveData<NetworkResult<List<Object>>> getWishlistResult() {
        return wishlistResult;
    }

    public void loadWishlist() {
        repository.getMyWishlistItems(wishlistResult);
    }
}
