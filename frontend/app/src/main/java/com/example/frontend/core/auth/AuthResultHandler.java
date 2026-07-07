package com.example.frontend.core.auth;

import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import com.example.frontend.MainActivity;
import com.example.frontend.R;
import com.example.frontend.data.model.cart.CartDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.CartRepository;
import com.example.frontend.feature.home.HomeViewModel;
import com.example.frontend.utils.ToastHelper;
import ui.common.BottomNavigationHelper;

public class AuthResultHandler {

    private static boolean isHandlingSuccess = false;

    public static synchronized void handleSuccess(FragmentActivity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;
        if (isHandlingSuccess) return;
        isHandlingSuccess = true;

        try {
            // 1. Merge guest cart
            mergeGuestCart(activity);

            // 2. Sync Home/Profile data
            refreshAppData(activity);

            // 3. Handle navigation based on context
            if (AuthRequiredManager.getInstance().hasPendingAction()) {
                PendingAuthAction action = AuthRequiredManager.getInstance().getPendingAction();
                executeAction(activity, action);
                AuthRequiredManager.getInstance().clearPendingAction();
            } else {
                // Success destination: Account screen
                // Clear all auth fragments from back stack immediately
                activity.getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                // Navigate to AccountFragment
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new ui.account.AccountFragment())
                        .commit();

                // If MainActivity, update bottom nav selection
                if (activity instanceof MainActivity) {
                    View bottomNav = activity.findViewById(R.id.layoutBottomNavigation);
                    if (bottomNav != null) {
                        BottomNavigationHelper.setSelectedTab(bottomNav, BottomNavigationHelper.TAB_ACCOUNT);
                    }
                }
            }
        } finally {
            // Reset flag after a delay to prevent rapid success triggers
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                isHandlingSuccess = false;
            }, 1000);
        }
    }

    private static void mergeGuestCart(FragmentActivity activity) {
        CartRepository cartRepository = new CartRepository(activity);
        MutableLiveData<NetworkResult<CartDto>> result = new MutableLiveData<>();
        cartRepository.mergeGuestCart(result);
        // Use a one-time observer to avoid leaking on the Activity
        result.observeForever(new androidx.lifecycle.Observer<NetworkResult<CartDto>>() {
            @Override
            public void onChanged(NetworkResult<CartDto> networkResult) {
                if (networkResult.status == NetworkResult.Status.SUCCESS) {
                    ToastHelper.showShort(activity, "Giỏ hàng của bạn đã được đồng bộ");
                    result.removeObserver(this);
                } else if (networkResult.status == NetworkResult.Status.ERROR) {
                    result.removeObserver(this);
                }
            }
        });
    }

    private static void refreshAppData(FragmentActivity activity) {
        // Refresh Home data to show personalized recommendations
        HomeViewModel homeViewModel = new ViewModelProvider(activity).get(HomeViewModel.class);
        homeViewModel.loadHomeData();
        
        // Refresh Wishlist status
        com.example.frontend.feature.wishlist.WishlistViewModel wishlistViewModel = 
                new ViewModelProvider(activity).get(com.example.frontend.feature.wishlist.WishlistViewModel.class);
        // loading home data will trigger wishlist update if implemented in HomeViewModel
    }

    private static void executeAction(FragmentActivity activity, PendingAuthAction action) {
        // Pop back to the screen that initiated auth (usually LoginFragment)
        activity.getSupportFragmentManager().popBackStackImmediate();

        switch (action.getActionType()) {
            case ADD_TO_WISHLIST:
                String productId = action.getExtras().getString("productId");
                if (productId != null) {
                    com.example.frontend.feature.wishlist.WishlistViewModel wishlistViewModel = 
                        new ViewModelProvider(activity).get(com.example.frontend.feature.wishlist.WishlistViewModel.class);
                    wishlistViewModel.toggleWishlist(productId, action.getExtras().getBoolean("wasWishlisted"));
                    ToastHelper.showShort(activity, "Đã thêm vào wishlist");
                }
                break;
            case START_CHECKOUT:
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new ui.commerce.CheckoutFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case OPEN_ACCOUNT:
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new ui.account.AccountFragment())
                        .commit();
                
                // Update bottom nav
                if (activity instanceof MainActivity) {
                    View bottomNav = activity.findViewById(R.id.layoutBottomNavigation);
                    if (bottomNav != null) {
                        BottomNavigationHelper.setSelectedTab(bottomNav, BottomNavigationHelper.TAB_ACCOUNT);
                    }
                }
                break;
        }
    }
}
