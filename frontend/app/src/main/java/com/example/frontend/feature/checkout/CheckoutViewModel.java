package com.example.frontend.feature.checkout;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.cart.CartItemDto;
import com.example.frontend.data.model.checkout.CheckoutSessionDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.CheckoutRepository;

import java.util.ArrayList;
import java.util.List;

public class CheckoutViewModel extends AndroidViewModel {
    private static final boolean USE_MOCK_CHECKOUT = true;

    private final CheckoutRepository checkoutRepository;
    private final MutableLiveData<NetworkResult<CheckoutSessionDto>> checkoutSession = new MutableLiveData<>();
    private final MutableLiveData<com.example.frontend.data.model.address.AddressDto> selectedAddress = new MutableLiveData<>();

    public CheckoutViewModel(@NonNull Application application) {
        super(application);
        this.checkoutRepository = new CheckoutRepository(application);
    }

    public LiveData<NetworkResult<CheckoutSessionDto>> getCheckoutSession() {
        return checkoutSession;
    }

    public LiveData<com.example.frontend.data.model.address.AddressDto> getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(com.example.frontend.data.model.address.AddressDto address) {
        selectedAddress.setValue(address);
    }

    public void prepareCheckout() {
        if (USE_MOCK_CHECKOUT) {
            // If already set by setMockDataFromCart, don't overwrite with default mock
            if (checkoutSession.getValue() == null || checkoutSession.getValue().data == null) {
                checkoutSession.postValue(NetworkResult.success(createDefaultMockSession()));
            }
            return;
        }
        checkoutRepository.prepareCheckout(checkoutSession);
    }

    public void setMockDataFromCart(List<CartItemDto> selectedItems, double coinsDiscount, com.example.frontend.data.model.coupon.CouponDto selectedVoucher) {
        if (!USE_MOCK_CHECKOUT) return;

        CheckoutSessionDto session = new CheckoutSessionDto();
        session.setId("mock_checkout_session");
        
        List<CheckoutSessionDto.CheckoutItemDto> checkoutItems = new ArrayList<>();
        double subtotal = 0;
        for (CartItemDto cartItem : selectedItems) {
            CheckoutSessionDto.CheckoutItemDto item = new CheckoutSessionDto.CheckoutItemDto();
            item.setProductName(cartItem.getProductNameSnapshot());
            item.setVariantName(cartItem.getVariantNameSnapshot());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getFinalUnitPriceAmount());
            item.setImageUrl(cartItem.getImageUrlSnapshot());
            checkoutItems.add(item);
            
            subtotal += cartItem.getFinalUnitPriceAmount() * cartItem.getQuantity();
        }
        
        session.setItems(checkoutItems);
        session.setSubtotalAmount(subtotal);
        
        double shipping = 30000;
        double discount = 0;
        if (selectedVoucher != null) {
            if ("percentage".equalsIgnoreCase(selectedVoucher.getDiscountType())) {
                discount = subtotal * (selectedVoucher.getDiscountValue() / 100.0);
                if (selectedVoucher.getMaxDiscountAmount() > 0) {
                    discount = Math.min(discount, selectedVoucher.getMaxDiscountAmount());
                }
            } else {
                discount = selectedVoucher.getDiscountValue();
            }
            session.setCouponCode(selectedVoucher.getCouponCode());
        } else {
            discount = subtotal > 0 ? 100000 : 0; // Default mock discount
        }

        double points = coinsDiscount;
        
        session.setShippingAmount(shipping);
        session.setDiscountAmount(discount);
        session.setPointsAmount(points);
        
        double total = subtotal + shipping - discount - points;
        if (total < 0) total = 0;
        session.setTotalAmount(total);

        // Mock Address
        CheckoutSessionDto.CheckoutAddressDto address = new CheckoutSessionDto.CheckoutAddressDto();
        address.setFullName("Nguyễn Thanh Thanh");
        address.setPhone("0794 644 108");
        address.setAddressLine("12 Trần Hưng Đạo, Phường Bến Thành, Hồ Chí Minh");
        session.setShippingAddress(address);

        session.setShippingMethod("Giao hàng tiêu chuẩn");
        session.setPaymentMethod("Thanh toán khi nhận hàng (COD)");

        checkoutSession.postValue(NetworkResult.success(session));
    }

    private CheckoutSessionDto createDefaultMockSession() {
        CheckoutSessionDto session = new CheckoutSessionDto();
        // Just return a basic one if no cart items were passed
        session.setShippingMethod("Giao hàng tiêu chuẩn");
        session.setPaymentMethod("Thanh toán khi nhận hàng (COD)");
        
        CheckoutSessionDto.CheckoutAddressDto address = new CheckoutSessionDto.CheckoutAddressDto();
        address.setFullName("Nguyễn Thanh Thanh");
        address.setPhone("0794 644 108");
        address.setAddressLine("12 Trần Hưng Đạo, Phường Bến Thành, Hồ Chí Minh");
        session.setShippingAddress(address);
        
        return session;
    }
}
