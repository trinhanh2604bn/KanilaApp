package ui.common;

import android.view.View;
import android.widget.TextView;
import androidx.lifecycle.LifecycleOwner;
import com.example.frontend.R;
import com.example.frontend.feature.cart.CartViewModel;

public class CartBadgeHelper {

    /**
     * Finds tvCartBadge inside the provided view and binds it to CartViewModel.
     * @param lifecycleOwner The lifecycle owner (Activity or Fragment)
     * @param container The view containing tvCartBadge (can be the top-level container or the toolbar)
     * @param cartViewModel The CartViewModel instance
     */
    public static void bindBadge(LifecycleOwner lifecycleOwner, View container, CartViewModel cartViewModel) {
        if (container == null || cartViewModel == null) return;

        TextView tvBadge = container.findViewById(R.id.tvCartBadge);
        if (tvBadge == null) return;

        cartViewModel.getTotalCartQuantity().observe(lifecycleOwner, quantity -> {
            if (quantity != null && quantity > 0) {
                tvBadge.setText(quantity > 99 ? "99+" : String.valueOf(quantity));
                tvBadge.setVisibility(View.VISIBLE);
            } else {
                tvBadge.setVisibility(View.GONE);
            }
        });
    }
}
