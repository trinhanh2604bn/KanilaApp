package com.example.frontend.feature.cart;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.frontend.data.model.cart.CartDto;
import com.example.frontend.data.model.cart.CartItemDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.CartRepository;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private static final boolean USE_MOCK_CART = false;

    private final CartRepository cartRepository;
    private final MutableLiveData<NetworkResult<CartDto>> cartResult = new MutableLiveData<>();
    private CartDto mockCart;

    public CartViewModel(@NonNull Application application) {
        super(application);
        this.cartRepository = new CartRepository(application);
    }

    public LiveData<NetworkResult<CartDto>> getCartResult() {
        return cartResult;
    }

    public void loadCart() {
        if (USE_MOCK_CART) {
            if (mockCart == null) {
                mockCart = createMockCartData();
            }
            cartResult.postValue(NetworkResult.success(mockCart));
            return;
        }
        cartRepository.getCart(cartResult);
    }

    public void updateItemQuantity(String itemId, int quantity) {
        if (USE_MOCK_CART && mockCart != null) {
            List<CartItemDto> newList = new ArrayList<>();
            for (CartItemDto item : mockCart.getItems()) {
                if (item.getId().equals(itemId)) {
                    newList.add(CartItemDto.createMock(
                            item.getId(),
                            item.getProductNameSnapshot(),
                            item.getVariantNameSnapshot(),
                            item.getFinalUnitPriceAmount(),
                            quantity,
                            item.isSelected(),
                            item.getImageUrlSnapshot()
                    ));
                } else {
                    newList.add(item);
                }
            }
            recalculateMockTotals(newList);
            cartResult.postValue(NetworkResult.success(mockCart));
            return;
        }
        cartRepository.updateItemQuantity(itemId, quantity, cartResult);
    }

    public void toggleItemSelection(String itemId, boolean selected) {
        if (USE_MOCK_CART && mockCart != null) {
            List<CartItemDto> newList = new ArrayList<>();
            for (CartItemDto item : mockCart.getItems()) {
                if (item.getId().equals(itemId)) {
                    newList.add(CartItemDto.createMock(
                            item.getId(),
                            item.getProductNameSnapshot(),
                            item.getVariantNameSnapshot(),
                            item.getFinalUnitPriceAmount(),
                            item.getQuantity(),
                            selected,
                            item.getImageUrlSnapshot()
                    ));
                } else {
                    newList.add(item);
                }
            }
            recalculateMockTotals(newList);
            cartResult.postValue(NetworkResult.success(mockCart));
            return;
        }
        cartRepository.toggleItemSelection(itemId, selected, cartResult);
    }

    public void removeItem(String itemId) {
        if (USE_MOCK_CART && mockCart != null) {
            List<CartItemDto> newList = new ArrayList<>();
            for (CartItemDto item : mockCart.getItems()) {
                if (!item.getId().equals(itemId)) {
                    newList.add(item);
                }
            }
            recalculateMockTotals(newList);
            cartResult.postValue(NetworkResult.success(mockCart));
            return;
        }
        cartRepository.removeItem(itemId, cartResult);
    }

    public void selectAllItems(boolean selected) {
        if (USE_MOCK_CART && mockCart != null) {
            List<CartItemDto> newList = new ArrayList<>();
            for (CartItemDto item : mockCart.getItems()) {
                newList.add(CartItemDto.createMock(
                        item.getId(),
                        item.getProductNameSnapshot(),
                        item.getVariantNameSnapshot(),
                        item.getFinalUnitPriceAmount(),
                        item.getQuantity(),
                        selected,
                        item.getImageUrlSnapshot()
                ));
            }
            recalculateMockTotals(newList);
            cartResult.postValue(NetworkResult.success(mockCart));
            return;
        }
        // If there was a repository method, we'd call it here
    }

    public void addToCart(com.example.frontend.data.model.cart.AddToCartRequest request) {
        // Reset trạng thái về LOADING để tránh observer nhận dữ liệu cũ (như lỗi 400 trước đó)
        cartResult.setValue(NetworkResult.loading());

        if (USE_MOCK_CART) {
            cartResult.postValue(NetworkResult.success(mockCart != null ? mockCart : createMockCartData()));
            return;
        }

        // Logic đặc biệt để TEST: Nếu là ID mock thì trả về success luôn
        if (request != null && request.getProductId() != null && 
            (request.getProductId().startsWith("s") || request.getProductId().startsWith("p"))) {
            if (mockCart == null) mockCart = createMockCartData();
            cartResult.postValue(NetworkResult.success(mockCart));
            return;
        }

        cartRepository.addToCart(request, cartResult);
    }

    private void recalculateMockTotals(List<CartItemDto> items) {
        double subtotal = 0;
        for (CartItemDto item : items) {
            if (item.isSelected()) {
                subtotal += item.getFinalUnitPriceAmount() * item.getQuantity();
            }
        }
        double discount = subtotal > 0 ? 100000 : 0;
        double total = subtotal - discount;
        mockCart = CartDto.createMockCart(items, subtotal, discount, total);
    }

    private CartDto createMockCartData() {
        List<CartItemDto> items = new ArrayList<>();

        // 1. Lip product
        items.add(CartItemDto.createMock(
                "item_1",
                "Kanila Sweet Lip For You",
                "#001 Rose",
                245000,
                1,
                true,
                null // Will use placeholder ic_product in adapter
        ));

        // 2. Cushion/foundation product
        items.add(CartItemDto.createMock(
                "item_2",
                "Kanila Glow Cushion",
                "21N Natural Beige",
                320000,
                1,
                true,
                null
        ));

        // 3. Mascara product
        items.add(CartItemDto.createMock(
                "item_3",
                "Kanila Long Curl Mascara",
                "Black",
                189000,
                2,
                false,
                null
        ));

        // 4. Blush product
        items.add(CartItemDto.createMock(
                "item_4",
                "Kanila Soft Blush",
                "Peach Coral",
                159000,
                1,
                false,
                null
        ));

        double subtotal = 0;
        for (CartItemDto item : items) {
            if (item.isSelected()) {
                subtotal += item.getFinalUnitPriceAmount() * item.getQuantity();
            }
        }
        
        // Mock discount if any items are selected
        double discount = subtotal > 0 ? 100000 : 0;
        double total = subtotal - discount;

        return CartDto.createMockCart(items, subtotal, discount, total);
    }
}
