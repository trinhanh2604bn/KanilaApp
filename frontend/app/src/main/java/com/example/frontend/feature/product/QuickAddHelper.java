package com.example.frontend.feature.product;

import android.content.Context;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.data.model.product.ProductVariantDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.model.Product;
import java.util.List;

public class QuickAddHelper {
    public static void quickAddToCart(Context context, FragmentManager fragmentManager, LifecycleOwner lifecycleOwner, 
                                    Product product, CartViewModel cartViewModel) {
        if (product == null || product.getId() == null) return;

        ProductRepository repo = new ProductRepository(context);
        repo.getProductDetail(product.getId()).observe(lifecycleOwner, result -> {
            if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                showVariantSelector(fragmentManager, result.data.getProduct(), result.data.getVariants(), cartViewModel);
            } else if (result.status == NetworkResult.Status.ERROR) {
                Toast.makeText(context, "Không thể tải thông tin phân loại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void showVariantSelector(FragmentManager fragmentManager, Product product, 
                                          List<ProductVariantDto> variants, CartViewModel cartViewModel) {
        VariantSelectorBottomSheet bottomSheet = VariantSelectorBottomSheet.newInstance(
            product, variants, VariantSelectorBottomSheet.ActionMode.ADD_TO_CART);
        
        bottomSheet.setListener((variant, mode, quantity) -> {
            String variantId = variant != null ? variant.getId() : null;
            cartViewModel.addToCart(new AddToCartRequest(product.getId(), variantId, quantity));
        });
        bottomSheet.show(fragmentManager, "VariantSelector");
    }
}
