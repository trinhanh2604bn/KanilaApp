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

        // 2. Fetch profile and execute pending action
        if (AuthRequiredManager.getInstance().hasPendingAction()) {
            PendingAuthAction action = AuthRequiredManager.getInstance().getPendingAction();
            executeAction(activity, action);
            AuthRequiredManager.getInstance().clearPendingAction();
        } else {
            // Default behavior - go back
            activity.getSupportFragmentManager().popBackStack();
            activity.getSupportFragmentManager().popBackStack();
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
            }
        });
    }

    private static void executeAction(FragmentActivity activity, PendingAuthAction action) {
        // Pop back to the screen that initiated auth
        activity.getSupportFragmentManager().popBackStack(); // Pop OTP
        activity.getSupportFragmentManager().popBackStack(); // Pop Login/Register

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
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, new ui.commerce.CheckoutFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case OPEN_ACCOUNT:
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, new ui.account.AccountFragment())
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }
}
