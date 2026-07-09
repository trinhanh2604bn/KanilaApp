package com.example.frontend.core.auth;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.example.frontend.R;
import com.example.frontend.feature.auth.GuestPromptBottomSheet;
import com.example.frontend.feature.auth.LoginFragment;
import com.example.frontend.feature.auth.RegisterFragment;

public class AuthNavigationHelper {

    public static void showAuthPrompt(FragmentActivity activity, PendingAuthAction action) {
        AuthRequiredManager.getInstance().setPendingAction(action);
        GuestPromptBottomSheet bottomSheet = GuestPromptBottomSheet.newInstance(action.getActionType());
        bottomSheet.show(activity.getSupportFragmentManager(), "GuestPromptBottomSheet");
    }

    public static void navigateToLogin(FragmentActivity activity) {
        ui.common.FragmentNavigationHelper.replaceFragment(activity, new LoginFragment());
    }

    public static void navigateToRegister(FragmentActivity activity) {
        ui.common.FragmentNavigationHelper.replaceFragment(activity, new RegisterFragment());
    }
}
