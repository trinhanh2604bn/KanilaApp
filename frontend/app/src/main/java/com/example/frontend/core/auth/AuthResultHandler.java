package com.example.frontend.core.auth;

import android.content.Context;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.R;
import com.example.frontend.data.model.cart.CartDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.CartRepository;

public class AuthResultHandler {

    public static void handleSuccess(FragmentActivity activity) {
        // 1. Merge guest cart
        mergeGuestCart(activity);

        // 2. Clear auth screens and execute pending action or navigate to account
        popAuthFlow(activity);
        
        if (AuthRequiredManager.getInstance().hasPendingAction()) {
            PendingAuthAction action = AuthRequiredManager.getInstance().getPendingAction();
            executeAction(activity, action);
            AuthRequiredManager.getInstance().clearPendingAction();
        } else {
            // Default behavior - navigate to Account
            ui.common.FragmentNavigationHelper.replaceFragment(activity, new ui.account.AccountFragment());
        }
    }

    private static void popAuthFlow(FragmentActivity activity) {
        androidx.fragment.app.FragmentManager fragmentManager = activity.getSupportFragmentManager();
        // Pop the entire backstack at once to avoid memory-intensive intermediate fragment restorations
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private static void mergeGuestCart(FragmentActivity activity) {
        CartRepository cartRepository = new CartRepository(activity);
        MutableLiveData<NetworkResult<CartDto>> result = new MutableLiveData<>();
        cartRepository.mergeGuestCart(result);
        // We don't necessarily need to observe it here, but we could for showing a toast
        result.observe(activity, networkResult -> {
            if (networkResult.status == NetworkResult.Status.SUCCESS) {
                Toast.makeText(activity, "Giỏ hàng của bạn đã được đồng bộ", Toast.LENGTH_SHORT).show();
                // Clear guest session after successful merge
                com.example.frontend.data.remote.TokenManager.getInstance(activity).clearGuestSession();
            }
        });
    }

    private static void executeAction(FragmentActivity activity, PendingAuthAction action) {
        switch (action.getActionType()) {
            case ADD_TO_WISHLIST:
                String productId = action.getExtras().getString("productId");
                if (productId != null) {
                    com.example.frontend.feature.wishlist.WishlistViewModel wishlistViewModel = 
                        new androidx.lifecycle.ViewModelProvider(activity).get(com.example.frontend.feature.wishlist.WishlistViewModel.class);
                    wishlistViewModel.toggleWishlist(productId, action.getExtras().getBoolean("wasWishlisted"));
                    Toast.makeText(activity, "Đã thêm vào wishlist", Toast.LENGTH_SHORT).show();
                }
                break;
            case START_CHECKOUT:
                ui.common.FragmentNavigationHelper.replaceFragment(activity, new ui.commerce.CheckoutFragment());
                break;
            case OPEN_ACCOUNT:
                ui.common.FragmentNavigationHelper.replaceFragment(activity, new ui.account.AccountFragment());
                break;
            case OPEN_ORDER_LIST:
                ui.common.FragmentNavigationHelper.replaceFragment(activity, new ui.order.OrderListFragment());
                break;
            case OPEN_VOUCHER_WALLET:
                ui.common.FragmentNavigationHelper.replaceFragment(activity, new com.example.frontend.feature.voucher.VoucherListFragment());
                break;
            case OPEN_WISHLIST:
                ui.common.FragmentNavigationHelper.replaceFragment(activity, new com.example.frontend.feature.wishlist.WishlistFragment());
                break;
            case SAVE_BEAUTY_PROFILE:
                ui.common.FragmentNavigationHelper.replaceFragment(activity, new ui.account.BeautyProfileOverviewFragment());
                break;
            case OPEN_LOYALTY:
                ui.common.FragmentNavigationHelper.replaceFragment(activity, new ui.loyalty.LoyaltyFragment());
                break;
        }
    }
}
