package com.example.frontend.feature.cart;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.cart.CartDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.CartRepository;

public class CartViewModel extends AndroidViewModel {
    private final CartRepository cartRepository;
    private final MutableLiveData<NetworkResult<CartDto>> cartResult = new MutableLiveData<>();

    public CartViewModel(@NonNull Application application) {
        super(application);
        this.cartRepository = new CartRepository(application);
    }

    public LiveData<NetworkResult<CartDto>> getCartResult() {
        return cartResult;
    }

    public void loadCart() {
        cartRepository.getCart(cartResult);
    }

    public void updateItemQuantity(String itemId, int quantity) {
        cartRepository.updateItemQuantity(itemId, quantity, cartResult);
    }

    public void toggleItemSelection(String itemId, boolean selected) {
        cartRepository.toggleItemSelection(itemId, selected, cartResult);
    }

    public void removeItem(String itemId) {
        cartRepository.removeItem(itemId, cartResult);
    }
}
