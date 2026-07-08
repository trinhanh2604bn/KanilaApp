package ui.commerce;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.cart.CartItemDto;
import com.example.frontend.data.model.product.ProductVariantDto;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VariantBottomSheetDialog extends BottomSheetDialog {

    private static final float VARIANT_SHEET_HEIGHT_RATIO = 0.75f;
    private final CartItemDto cartItem;
    private OnVariantAppliedListener listener;
    private List<ProductVariantDto> variants;
    private ProductVariantDto selectedVariant;
    private int quantity;

    public interface OnVariantAppliedListener {
        void onVariantApplied(ProductVariantDto variant, int quantity);
    }

    public VariantBottomSheetDialog(@NonNull Context context, CartItemDto cartItem) {
        super(context);
        this.cartItem = cartItem;
        this.quantity = cartItem != null ? cartItem.getQuantity() : 1;
    }

    public void setOnVariantAppliedListener(OnVariantAppliedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_variant);

        setOnShowListener(dialog -> setupHeight());
        setupViews();
        setupActions();
        loadVariants();
    }

    private void setupHeight() {
        View bottomSheet = findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            if (getWindow() != null) {
                getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            }
            int screenHeight = displayMetrics.heightPixels;
            int maxSheetHeight = (int) (screenHeight * VARIANT_SHEET_HEIGHT_RATIO);

            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setMaxHeight(maxSheetHeight);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
            behavior.setFitToContents(true);

            ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                bottomSheet.setLayoutParams(layoutParams);
            }
        }
    }

    private void setupViews() {
        ImageView ivProduct = findViewById(R.id.ivVariantProductImage);
        TextView tvBrand = findViewById(R.id.tvVariantBrand);
        TextView tvName = findViewById(R.id.tvVariantProductName);
        TextView tvPrice = findViewById(R.id.tvVariantCurrentPrice);
        TextView tvQuantity = findViewById(R.id.tvVariantQuantity);
        TextView tvStock = findViewById(R.id.tvVariantStock);

        if (cartItem != null) {
            if (ivProduct != null) {
                Glide.with(getContext())
                        .load(cartItem.getImageUrlSnapshot() != null ? cartItem.getImageUrlSnapshot() : "")
                        .placeholder(R.drawable.ic_product)
                        .error(R.drawable.ic_product)
                        .into(ivProduct);
            }
            if (tvBrand != null) tvBrand.setText(cartItem.getBrandNameSnapshot());
            if (tvName != null) tvName.setText(cartItem.getProductNameSnapshot());
            if (tvPrice != null) tvPrice.setText(formatPrice(cartItem.getFinalUnitPriceAmount()));
            if (tvQuantity != null) tvQuantity.setText(String.valueOf(quantity));
            if (tvStock != null) {
                if ("in_stock".equalsIgnoreCase(cartItem.getStockStatus())) {
                    tvStock.setText("Còn hàng");
                } else {
                    tvStock.setText("Hết hàng");
                }
            }
        }
    }

    private void loadVariants() {
        if (cartItem == null || cartItem.getProductId() == null) return;

        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        apiService.getProductVariants(cartItem.getProductId()).enqueue(new Callback<ApiResponse<List<ProductVariantDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductVariantDto>>> call, Response<ApiResponse<List<ProductVariantDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    variants = response.body().getData();
                    
                    if (variants != null && cartItem != null) {
                        for (ProductVariantDto v : variants) {
                            if (v.getId() != null && v.getId().equals(cartItem.getVariantId())) {
                                selectedVariant = v;
                                break;
                            }
                        }
                        if (selectedVariant == null && !variants.isEmpty()) {
                            selectedVariant = variants.get(0);
                        }
                    }

                    bindVariantsUI();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ProductVariantDto>>> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void bindVariantsUI() {
        if (variants == null || variants.isEmpty()) return;

        Log.d("VariantBottomSheet", "Rendering variants for product: " + cartItem.getProductNameSnapshot());

        LinearLayout layoutColorOptions = findViewById(R.id.layoutColorOptions);
        LinearLayout layoutSizeOptions = findViewById(R.id.layoutSizeOptions);

        if (layoutColorOptions != null) layoutColorOptions.removeAllViews();
        if (layoutSizeOptions != null) layoutSizeOptions.removeAllViews();

        for (ProductVariantDto variant : variants) {
            // Check if it's a color variant by name or some logic
            if (isColorVariant(variant)) {
                addColorOption(variant, layoutColorOptions);
            } else {
                addSizeOption(variant, layoutSizeOptions);
            }
        }
        
        updateSelectedVariantUI();
    }

    private boolean isColorVariant(ProductVariantDto variant) {
        String name = variant.getVariantName().toLowerCase();
        return name.contains("#") || name.contains("red") || name.contains("pink") || name.contains("rose");
    }

    private void addColorOption(ProductVariantDto variant, LinearLayout container) {
        if (container == null) return;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_variant_color, container, false);
        View selectionBg = view.findViewById(R.id.viewSelectionBg);
        View colorCircle = view.findViewById(R.id.viewColorCircle);
        ImageView ivTick = view.findViewById(R.id.ivTick);
        TextView tvName = view.findViewById(R.id.tvVariantName);
        
        tvName.setText(variant.getVariantName());
        
        boolean isSelected = selectedVariant != null && selectedVariant.getId().equals(variant.getId());
        
        // Mock color or try to parse from name
        if (colorCircle != null && colorCircle.getBackground() != null) {
            int color = android.graphics.Color.LTGRAY;
            String name = variant.getVariantName().toLowerCase();
            if (name.contains("red")) color = android.graphics.Color.RED;
            else if (name.contains("pink")) color = android.graphics.Color.parseColor("#FFC0CB");
            else if (name.contains("black")) color = android.graphics.Color.BLACK;
            
            colorCircle.getBackground().setTint(color);
        }

        if (selectionBg != null) selectionBg.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        if (ivTick != null) ivTick.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        
        view.setOnClickListener(v -> {
            Log.d("VariantBottomSheet", "Variant selected: " + variant.getVariantName());
            selectedVariant = variant;
            bindVariantsUI();
        });

        container.addView(view);
    }

    private void addSizeOption(ProductVariantDto variant, LinearLayout container) {
        if (container == null) return;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_variant_size, container, false);
        TextView tvSize = view instanceof TextView ? (TextView) view : view.findViewById(R.id.tvVariantSize);
        
        if (tvSize != null) {
            tvSize.setText(variant.getVariantName());
            boolean isSelected = selectedVariant != null && selectedVariant.getId().equals(variant.getId());
            tvSize.setBackgroundResource(isSelected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_outline);
            tvSize.setTextColor(isSelected ? android.graphics.Color.WHITE : android.graphics.Color.BLACK);
        }

        view.setOnClickListener(v -> {
            Log.d("VariantBottomSheet", "Variant selected: " + variant.getVariantName());
            selectedVariant = variant;
            bindVariantsUI();
        });

        container.addView(view);
    }

    private void updateSelectedVariantUI() {
        TextView tvPrice = findViewById(R.id.tvVariantCurrentPrice);
        TextView tvStock = findViewById(R.id.tvVariantStock);
        ImageView ivProduct = findViewById(R.id.ivVariantProductImage);

        if (selectedVariant != null) {
            if (tvPrice != null && selectedVariant.getPrice() != null) {
                tvPrice.setText(formatPrice(selectedVariant.getPrice()));
            }
            if (tvStock != null) {
                tvStock.setText("Còn " + selectedVariant.getStockQuantity() + " sản phẩm");
            }
            if (ivProduct != null && selectedVariant.getImageUrl() != null) {
                Glide.with(getContext()).load(selectedVariant.getImageUrl()).into(ivProduct);
            }
        }
    }

    private void setupActions() {
        View btnClose = findViewById(R.id.btnVariantClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dismiss());
        }

        View btnApply = findViewById(R.id.btnVariantApply);
        if (btnApply != null) {
            btnApply.setOnClickListener(v -> {
                Log.d("VariantBottomSheet", "Apply variant: " + (selectedVariant != null ? selectedVariant.getVariantName() : "none") + ", qty: " + quantity);
                if (listener != null) {
                    listener.onVariantApplied(selectedVariant, quantity);
                }
                dismiss();
            });
        }

        View btnIncrease = findViewById(R.id.btnVariantIncrease);
        View btnDecrease = findViewById(R.id.btnVariantDecrease);
        TextView tvQuantity = findViewById(R.id.tvVariantQuantity);

        if (btnIncrease != null && tvQuantity != null) {
            btnIncrease.setOnClickListener(v -> {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            });
        }

        if (btnDecrease != null && tvQuantity != null) {
            btnDecrease.setOnClickListener(v -> {
                if (quantity > 1) {
                    quantity--;
                    tvQuantity.setText(String.valueOf(quantity));
                }
            });
        }
    }

    private String formatPrice(double price) {
        if (price == 0) return "Liên hệ";
        return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
    }
}
