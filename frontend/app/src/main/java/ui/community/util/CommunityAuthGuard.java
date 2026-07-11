package ui.community.util;

import androidx.fragment.app.Fragment;
import com.example.frontend.core.auth.PendingAuthAction;
import com.example.frontend.data.remote.TokenManager;
import com.example.frontend.feature.auth.GuestPromptBottomSheet;

public class CommunityAuthGuard {
    /**
     * Checks if the user is logged in. If not, shows the GuestPromptBottomSheet.
     * @param fragment The calling fragment
     * @param actionType The type of action being guarded
     * @return true if logged in, false if guest (and prompt shown)
     */
    public static boolean checkMember(Fragment fragment, PendingAuthAction.ActionType actionType) {
        if (fragment == null || fragment.getContext() == null) return false;
        
        if (TokenManager.getInstance(fragment.requireContext()).isLoggedIn()) {
            return true;
        } else {
            GuestPromptBottomSheet.newInstance(actionType)
                    .show(fragment.getParentFragmentManager(), "GuestPromptBottomSheet");
            return false;
        }
    }
}
