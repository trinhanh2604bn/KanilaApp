package com.example.frontend.feature.checkout;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.cart.CartItemDto;
import com.example.frontend.data.model.checkout.CheckoutSessionDto;
import com.example.frontend.data.model.shipping.ShippingMethodDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.remote.TokenManager;
import com.example.frontend.data.repository.CheckoutRepository;

import java.util.ArrayList;
import java.util.List;

public class CheckoutViewModel extends AndroidViewModel {
    private static final boolean USE_MOCK_CHECKOUT = true;

    private final CheckoutRepository checkoutRepository;
    private final MutableLiveData<NetworkResult<CheckoutSessionDto>> checkoutSession = new MutableLiveData<>();
    private final MutableLiveData<com.example.frontend.data.model.address.AddressDto> selectedAddress = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<com.example.frontend.data.model.order.OrderDto>> placeOrderResult = new MutableLiveData<>();

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

    public LiveData<NetworkResult<com.example.frontend.data.model.order.OrderDto>> getPlaceOrderResult() {
        return placeOrderResult;
    }

    public void placeOrder() {
        if (USE_MOCK_CHECKOUT) {
            CheckoutSessionDto session = checkoutSession.getValue() != null ? checkoutSession.getValue().data : null;
            if (session != null) {
                java.util.Map<String, Object> request = new java.util.HashMap<>();
                request.put("checkout_session_id", session.getId());
                request.put("currency_code", "VND");
                request.put("items", session.getItems());
                request.put("shipping_address", session.getShippingAddress());
                request.put("shipping_method", session.getShippingMethod());
                request.put("payment_method", session.getPaymentMethod());
                request.put("subtotal_amount", session.getSubtotalAmount());
                request.put("shipping_fee_amount", session.getShippingAmount());
                request.put("discount_amount", session.getDiscountAmount());
                request.put("coupon_discount_amount", 0.0);
                request.put("tax_amount", 0.0);
                request.put("total_amount", session.getTotalAmount());
                
                // Guest session ID if available
                String guestId = TokenManager.getInstance(getApplication()).getGuestSession();
                if (guestId != null) request.put("guest_session_id", guestId);

                MutableLiveData<NetworkResult<com.example.frontend.data.model.order.MockOrderResponse>> mockResult = new MutableLiveData<>();
                mockResult.observeForever(new androidx.lifecycle.Observer<NetworkResult<com.example.frontend.data.model.order.MockOrderResponse>>() {
                    @Override
                    public void onChanged(NetworkResult<com.example.frontend.data.model.order.MockOrderResponse> result) {
                        if (result != null) {
                            if (result.status == NetworkResult.Status.SUCCESS) {
                                String mockJson = new com.google.gson.Gson().toJson(result.data);
                                // Map backend fields to OrderDto fields
                                mockJson = mockJson.replace("\"order_code\":", "\"order_number\":");
                                mockJson = mockJson.replace("\"order_id\":", "\"_id\":");
                                
                                com.example.frontend.data.model.order.OrderDto order = new com.google.gson.Gson().fromJson(mockJson, com.example.frontend.data.model.order.OrderDto.class);
                                placeOrderResult.postValue(NetworkResult.success(order));
                                mockResult.removeObserver(this);
                            } else if (result.status == NetworkResult.Status.ERROR) {
                                placeOrderResult.postValue(NetworkResult.error(result.message));
                                mockResult.removeObserver(this);
                            } else if (result.status == NetworkResult.Status.LOADING) {
                                placeOrderResult.postValue(NetworkResult.loading());
                            }
                        }
                    }
                });
                
                checkoutRepository.createMockOrder(request, mockResult);
            } else {
                placeOrderResult.postValue(NetworkResult.error("Session không hợp lệ"));
            }
            return;
        }

        CheckoutSessionDto session = checkoutSession.getValue() != null ? checkoutSession.getValue().data : null;
        if (session != null && session.getId() != null) {
            boolean isGuest = !com.example.frontend.data.remote.TokenManager.getInstance(getApplication()).isLoggedIn();
            checkoutRepository.placeOrder(session.getId(), isGuest, placeOrderResult);
        } else {
            placeOrderResult.postValue(NetworkResult.error("Session không hợp lệ"));
        }
    }

    public void setSelectedAddress(com.example.frontend.data.model.address.AddressDto address) {
        selectedAddress.setValue(address);
    }

    public void updateShippingMethod(ShippingMethodDto method) {
        if (method == null) return;
        android.util.Log.d("CheckoutViewModel", "Updating shipping method: " + method.getName() + ", ID: " + method.getId() + ", Fee: " + method.getShippingFee());

        if (USE_MOCK_CHECKOUT) {
            CheckoutSessionDto session = checkoutSession.getValue() != null ? checkoutSession.getValue().data : null;
            if (session != null) {
                session.setShippingMethod(method.getName());
                session.setShippingAmount(method.getShippingFee());
                session.setEstimatedDelivery(method.getEstimatedDelivery());
                // Update total
                double subtotal = session.getSubtotalAmount() != null ? session.getSubtotalAmount() : 0.0;
                double discount = session.getDiscountAmount() != null ? session.getDiscountAmount() : 0.0;

                double total = subtotal + method.getShippingFee() - discount;
                session.setTotalAmount(Math.max(0, total));
                android.util.Log.d("CheckoutViewModel", "Mock session updated. New total: " + session.getTotalAmount());

                // Use setValue for immediate update if on main thread
                checkoutSession.setValue(NetworkResult.success(session));
            } else {
                android.util.Log.e("CheckoutViewModel", "Cannot update shipping: Session is null");
            }
            return;
        }

        CheckoutSessionDto currentSession = checkoutSession.getValue() != null ? checkoutSession.getValue().data : null;
        if (currentSession != null && currentSession.getId() != null) {
            boolean isGuest = !com.example.frontend.data.remote.TokenManager.getInstance(getApplication()).isLoggedIn();
            
            MutableLiveData<NetworkResult<CheckoutSessionDto>> updateResult = new MutableLiveData<>();
            updateResult.observeForever(new androidx.lifecycle.Observer<NetworkResult<CheckoutSessionDto>>() {
                @Override
                public void onChanged(NetworkResult<CheckoutSessionDto> result) {
                    if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                        mergeSession(result.data);
                        updateResult.removeObserver(this);
                    } else if (result != null && result.status == NetworkResult.Status.ERROR) {
                        checkoutSession.postValue(NetworkResult.error(result.message));
                        updateResult.removeObserver(this);
                    }
                }
            });
            
            checkoutRepository.updateShippingMethod(currentSession.getId(), method.getId(), isGuest, updateResult);
        }
    }

    public void applyCoupon(String couponCode) {
        CheckoutSessionDto currentSession = checkoutSession.getValue() != null ? checkoutSession.getValue().data : null;
        if (currentSession != null && currentSession.getId() != null) {
            boolean isGuest = !com.example.frontend.data.remote.TokenManager.getInstance(getApplication()).isLoggedIn();
            
            java.util.Map<String, Object> body = new java.util.HashMap<>();
            body.put("couponCode", couponCode);
            
            MutableLiveData<NetworkResult<CheckoutSessionDto>> updateResult = new MutableLiveData<>();
            updateResult.observeForever(new androidx.lifecycle.Observer<NetworkResult<CheckoutSessionDto>>() {
                @Override
                public void onChanged(NetworkResult<CheckoutSessionDto> result) {
                    if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                        mergeSession(result.data);
                        updateResult.removeObserver(this);
                    } else if (result != null && result.status == NetworkResult.Status.ERROR) {
                        checkoutSession.postValue(NetworkResult.error(result.message));
                        updateResult.removeObserver(this);
                    }
                }
            });
            
            checkoutRepository.updateCheckoutSession(currentSession.getId(), body, isGuest, updateResult);
        }
    }

    public void updatePaymentMethod(String paymentMethod) {
        if (USE_MOCK_CHECKOUT) {
            CheckoutSessionDto session = checkoutSession.getValue() != null ? checkoutSession.getValue().data : null;
            if (session != null) {
                session.setPaymentMethod(paymentMethod);
                checkoutSession.setValue(NetworkResult.success(session));
            }
            return;
        }

        CheckoutSessionDto currentSession = checkoutSession.getValue() != null ? checkoutSession.getValue().data : null;
        if (currentSession != null && currentSession.getId() != null) {
            boolean isGuest = !com.example.frontend.data.remote.TokenManager.getInstance(getApplication()).isLoggedIn();
            
            java.util.Map<String, Object> body = new java.util.HashMap<>();
            body.put("paymentMethod", paymentMethod);
            
            MutableLiveData<NetworkResult<CheckoutSessionDto>> updateResult = new MutableLiveData<>();
            updateResult.observeForever(new androidx.lifecycle.Observer<NetworkResult<CheckoutSessionDto>>() {
                @Override
                public void onChanged(NetworkResult<CheckoutSessionDto> result) {
                    if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                        mergeSession(result.data);
                        updateResult.removeObserver(this);
                    } else if (result != null && result.status == NetworkResult.Status.ERROR) {
                        checkoutSession.postValue(NetworkResult.error(result.message));
                        updateResult.removeObserver(this);
                    }
                }
            });
            
            checkoutRepository.updateCheckoutSession(currentSession.getId(), body, isGuest, updateResult);
        }
    }

    private void mergeSession(CheckoutSessionDto newSession) {
        if (newSession != null) {
            checkoutSession.postValue(NetworkResult.success(newSession));
        }
    }

    public void prepareCheckout() {
        // Quan trọng: Reset trạng thái đặt hàng để tránh lỗi "sticky state" khi thanh toán đơn hàng tiếp theo
        placeOrderResult.setValue(null);

        if (USE_MOCK_CHECKOUT) {
            // If already set by updateCheckoutSession or setMockDataFromCart (has items), don't overwrite
            CheckoutSessionDto current = checkoutSession.getValue() != null ? checkoutSession.getValue().data : null;
            if (current == null || current.getItems() == null || current.getItems().isEmpty()) {
                android.util.Log.d("CheckoutViewModel", "Preparing default mock session (current is empty)");
                checkoutSession.setValue(NetworkResult.success(createDefaultMockSession()));
            } else {
                android.util.Log.d("CheckoutViewModel", "Skipping prepareCheckout: Session already has " + current.getItems().size() + " items");
                // Explicitly notify observers to ensure UI updates with the existing data
                checkoutSession.setValue(checkoutSession.getValue());
            }
            return;
        }

        if (com.example.frontend.data.remote.TokenManager.getInstance(getApplication()).isLoggedIn()) {
            checkoutRepository.prepareCheckout(checkoutSession);
        } else {
            checkoutRepository.prepareGuestCheckout(checkoutSession);
        }
    }

    public void updateCheckoutSession(CheckoutSessionDto session) {
        if (session != null) {
            checkoutSession.setValue(NetworkResult.success(session));
        }
    }

    public void updateItems(List<CheckoutSessionDto.CheckoutItemDto> items) {
        if (USE_MOCK_CHECKOUT) {
            CheckoutSessionDto session = checkoutSession.getValue() != null ? checkoutSession.getValue().data : null;
            if (session != null) {
                session.setItems(items);
                
                double subtotal = 0;
                for (CheckoutSessionDto.CheckoutItemDto item : items) {
                    subtotal += item.getPrice() * item.getQuantity();
                }
                session.setSubtotalAmount(subtotal);
                
                double shipping = session.getShippingAmount() != null ? session.getShippingAmount() : 0.0;
                double discount = session.getDiscountAmount() != null ? session.getDiscountAmount() : 0.0;
                
                double total = subtotal + shipping - discount;
                session.setTotalAmount(Math.max(0, total));
                
                checkoutSession.setValue(NetworkResult.success(session));
            }
            return;
        }

        CheckoutSessionDto currentSession = checkoutSession.getValue() != null ? checkoutSession.getValue().data : null;
        if (currentSession != null && currentSession.getId() != null) {
            boolean isGuest = !com.example.frontend.data.remote.TokenManager.getInstance(getApplication()).isLoggedIn();
            
            java.util.Map<String, Object> body = new java.util.HashMap<>();
            body.put("items", items);
            
            MutableLiveData<NetworkResult<CheckoutSessionDto>> updateResult = new MutableLiveData<>();
            updateResult.observeForever(new androidx.lifecycle.Observer<NetworkResult<CheckoutSessionDto>>() {
                @Override
                public void onChanged(NetworkResult<CheckoutSessionDto> result) {
                    if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                        mergeSession(result.data);
                        updateResult.removeObserver(this);
                    } else if (result != null && result.status == NetworkResult.Status.ERROR) {
                        checkoutSession.postValue(NetworkResult.error(result.message));
                        updateResult.removeObserver(this);
                    }
                }
            });
            
            checkoutRepository.updateCheckoutSession(currentSession.getId(), body, isGuest, updateResult);
        }
    }

    public void setMockDataFromCart(List<CartItemDto> selectedItems, com.example.frontend.data.model.coupon.CouponDto selectedVoucher) {
        if (!USE_MOCK_CHECKOUT) return;

        // Try to get existing session to preserve shipping/address selection
        CheckoutSessionDto existingSession = checkoutSession.getValue() != null ? checkoutSession.getValue().data : null;
        
        CheckoutSessionDto session = new CheckoutSessionDto();
        session.setId("mock_checkout_session");
        
        // 1. Update Items and Subtotal from Cart
        List<CheckoutSessionDto.CheckoutItemDto> checkoutItems = new ArrayList<>();
        double subtotal = 0;
        for (CartItemDto cartItem : selectedItems) {
            CheckoutSessionDto.CheckoutItemDto item = new CheckoutSessionDto.CheckoutItemDto();
            item.setId(cartItem.getId());
            item.setProductId(cartItem.getProductId());
            item.setVariantId(cartItem.getVariantId());
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
        
        // 2. Preserve or Initialize Shipping/Address
        if (existingSession != null && existingSession.getShippingMethod() != null && !existingSession.getShippingMethod().isEmpty()) {
            android.util.Log.d("CheckoutViewModel", "Preserving existing shipping: " + existingSession.getShippingMethod());
            session.setShippingMethod(existingSession.getShippingMethod());
            session.setShippingAmount(existingSession.getShippingAmount());
            session.setEstimatedDelivery(existingSession.getEstimatedDelivery());
        } else {
            session.setShippingMethod(""); // Trigger auto-select logic in Fragment
            session.setShippingAmount(0.0);
        }

        if (existingSession != null && existingSession.getShippingAddress() != null) {
            session.setShippingAddress(existingSession.getShippingAddress());
        } else {
            session.setShippingAddress(null);
        }

        if (existingSession != null && existingSession.getPaymentMethod() != null) {
            session.setPaymentMethod(existingSession.getPaymentMethod());
        } else {
            session.setPaymentMethod("Thanh toán khi nhận hàng (COD)");
        }

        // 3. Update Voucher/Discount from Cart
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
            discount = 0;
        }

        session.setDiscountAmount(discount);
        session.setPointsAmount(0.0);
        
        // 4. Calculate Total
        double shippingFee = session.getShippingAmount() != null ? session.getShippingAmount() : 0.0;
        double total = subtotal + shippingFee - discount;
        if (total < 0) total = 0;
        session.setTotalAmount(total);

        checkoutSession.setValue(NetworkResult.success(session));
    }

    private CheckoutSessionDto createDefaultMockSession() {
        CheckoutSessionDto session = new CheckoutSessionDto();
        // Just return a basic one if no cart items were passed
        session.setShippingMethod(""); 
        session.setShippingAmount(0.0);
        session.setPaymentMethod("Thanh toán khi nhận hàng (COD)");
        
        // Remove Mock Address to allow "Hãy nhập địa chỉ nhận hàng" logic
        session.setShippingAddress(null);
        
        return session;
    }
}
