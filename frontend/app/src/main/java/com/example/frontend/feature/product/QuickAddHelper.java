package com.example.frontend.feature.product;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.data.model.cart.CartItemDto;
import com.example.frontend.data.model.checkout.CheckoutSessionDto;
import com.example.frontend.data.model.product.ProductVariantDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.remote.TokenManager;
import com.example.frontend.data.repository.CheckoutRepository;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.model.Product;
import com.example.frontend.core.auth.AuthNavigationHelper;
import com.example.frontend.core.auth.PendingAuthAction;
import java.util.ArrayList;
import java.util.List;
import ui.commerce.CheckoutFragment;
import ui.common.FragmentNavigationHelper;

public class QuickAddHelper {
    public static void quickAddToCart(Context context, FragmentManager fragmentManager, LifecycleOwner lifecycleOwner, 
                                    Product product, CartViewModel cartViewModel) {
        showSelector(context, fragmentManager, lifecycleOwner, product, cartViewModel, VariantSelectorBottomSheet.ActionMode.ADD_TO_CART);
    }

    public static void quickBuyNow(Context context, FragmentManager fragmentManager, LifecycleOwner lifecycleOwner, 
                                 Product product, CartViewModel cartViewModel) {
        showSelector(context, fragmentManager, lifecycleOwner, product, cartViewModel, VariantSelectorBottomSheet.ActionMode.BUY_NOW);
    }

    private static void showSelector(Context context, FragmentManager fragmentManager, LifecycleOwner lifecycleOwner, 
                                   Product product, CartViewModel cartViewModel, VariantSelectorBottomSheet.ActionMode mode) {
        if (product == null || product.getId() == null) return;

        ProductRepository repo = new ProductRepository(context);
        repo.getProductDetail(product.getId()).observe(lifecycleOwner, result -> {
            if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                showVariantSelector(context, lifecycleOwner, fragmentManager, result.data.getProduct(), result.data.getVariants(), cartViewModel, mode);
            } else if (result.status == NetworkResult.Status.ERROR) {
                Toast.makeText(context, "Không thể tải thông tin phân loại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void showVariantSelector(Context context, LifecycleOwner lifecycleOwner, FragmentManager fragmentManager, Product product,
                                          List<ProductVariantDto> variants, CartViewModel cartViewModel, VariantSelectorBottomSheet.ActionMode mode) {
        VariantSelectorBottomSheet bottomSheet = VariantSelectorBottomSheet.newInstance(
            product, variants, mode);
        
        bottomSheet.setListener((variant, selectedMode, quantity) -> {
            String variantId = variant != null ? variant.getId() : null;
            
            if (selectedMode == VariantSelectorBottomSheet.ActionMode.ADD_TO_CART) {
                cartViewModel.getCartResult().observe(lifecycleOwner, new androidx.lifecycle.Observer<NetworkResult<com.example.frontend.data.model.cart.CartDto>>() {
                    @Override
                    public void onChanged(NetworkResult<com.example.frontend.data.model.cart.CartDto> result) {
                        if (result == null) return;
                        if (result.status == NetworkResult.Status.SUCCESS) {
                            Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                            cartViewModel.getCartResult().removeObserver(this);
                        } else if (result.status == NetworkResult.Status.ERROR) {
                            cartViewModel.getCartResult().removeObserver(this);
                        }
                    }
                });
                cartViewModel.addToCart(new AddToCartRequest(product.getId(), variantId, quantity));
            } else if (selectedMode == VariantSelectorBottomSheet.ActionMode.BUY_NOW) {
                // Check if logged in for Buy Now
                if (!TokenManager.getInstance(context).isLoggedIn()) {
                    if (context instanceof FragmentActivity) {
                        // Prepare selected items to pass to Checkout after login
                        ArrayList<CartItemDto> selectedItems = new ArrayList<>();
                        CartItemDto cartItem = CartItemDto.createMock(
                            "buy_now_" + System.currentTimeMillis(),
                            product.getName(),
                            variant != null ? variant.getVariantName() : "Mặc định",
                            variant != null && variant.getPrice() != null ? variant.getPrice() : product.getPriceValue(),
                            quantity,
                            true,
                            variant != null && variant.getImageUrl() != null && !variant.getImageUrl().isEmpty() ?
                                variant.getImageUrl() : (product.getImageUrl() != null ? product.getImageUrl() : "")
                        );
                        cartItem.setProductId(product.getId());
                        cartItem.setVariantId(variantId);
                        cartItem.setBrandNameSnapshot(product.getBrand());
                        selectedItems.add(cartItem);

                        Bundle extras = new Bundle();
                        extras.putSerializable("selected_items", selectedItems);

                        PendingAuthAction action = new PendingAuthAction(
                            PendingAuthAction.ActionType.START_CHECKOUT,
                            "QuickBuy",
                            0,
                            extras
                        );
                        AuthNavigationHelper.showAuthPrompt((FragmentActivity) context, action);
                    } else {
                        Toast.makeText(context, "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                // Execute Buy Now Logic: Create Session then Navigate to Checkout
                CheckoutRepository checkoutRepo = new CheckoutRepository(context);
                MutableLiveData<NetworkResult<CheckoutSessionDto>> buyNowResult = new MutableLiveData<>();
                
                buyNowResult.observe(lifecycleOwner, result -> {
                    if (result == null) return;
                    if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                        navigateToCheckout(context, result.data);
                    } else if (result.status == NetworkResult.Status.ERROR) {
                        Toast.makeText(context, "Lỗi: " + result.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                checkoutRepo.createBuyNowSession(product.getId(), variantId, quantity, buyNowResult);
            }
        });
        bottomSheet.show(fragmentManager, "VariantSelector");
    }

    private static void navigateToCheckout(Context context, CheckoutSessionDto session) {
        if (!(context instanceof FragmentActivity)) return;
        FragmentActivity activity = (FragmentActivity) context;

        CheckoutFragment checkoutFragment = new CheckoutFragment();
        Bundle args = new Bundle();
        args.putSerializable("checkout_session", session);
        checkoutFragment.setArguments(args);

        FragmentNavigationHelper.loadFragment(activity, checkoutFragment);
    }
}
