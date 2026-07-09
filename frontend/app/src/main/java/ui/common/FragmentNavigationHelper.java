package ui.common;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.example.frontend.MainActivity;
import ui.support.SupportActivity;
import com.example.frontend.R;

public class FragmentNavigationHelper {

    public static void loadFragment(FragmentActivity activity, Fragment fragment) {
        if (activity == null || fragment == null) return;

        if (activity instanceof MainActivity) {
            ((MainActivity) activity) .loadFragment(fragment);
        } else if (activity instanceof SupportActivity) {
            ((SupportActivity) activity).loadFragment(fragment);
        } else {
            // Fallback for other activities
            replaceFragment(activity, fragment);
        }
    }

    public static void replaceFragment(FragmentActivity activity, Fragment fragment) {
        if (activity == null || fragment == null) return;

        int containerId = R.id.main_fragment_container;
        boolean isRootReplacement = false;

        if (activity.findViewById(R.id.main_fragment_container) == null) {
            if (activity.findViewById(R.id.main) != null) {
                containerId = R.id.main;
                isRootReplacement = true;
            } else {
                // No suitable container found
                return;
            }
        }

        var transaction = activity.getSupportFragmentManager().beginTransaction()
                .replace(containerId, fragment);

        if (!isRootReplacement) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }
}
