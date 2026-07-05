package ui.commerce;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.frontend.R;
import com.example.frontend.model.CartItem;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class VariantBottomSheetDialog extends BottomSheetDialog {

    private static final float VARIANT_SHEET_HEIGHT_RATIO = 0.65f;
    private final CartItem cartItem;
    private OnVariantAppliedListener listener;

    public interface OnVariantAppliedListener {
        void onVariantApplied(String variant, int quantity);
    }

    public VariantBottomSheetDialog(@NonNull Context context, CartItem cartItem) {
        super(context);
        this.cartItem = cartItem;
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

        if (cartItem != null) {
            if (ivProduct != null) ivProduct.setImageResource(cartItem.getProduct().getImageResource());
            if (tvBrand != null) tvBrand.setText(cartItem.getProduct().getBrand());
            if (tvName != null) tvName.setText(cartItem.getProduct().getName());
            if (tvPrice != null) tvPrice.setText(cartItem.getProduct().getPrice());
            if (tvQuantity != null) tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
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
                if (listener != null) {
                    // For now, using current variant as we don't have full selection logic implemented in UI
                    TextView tvQuantity = findViewById(R.id.tvVariantQuantity);
                    int quantity = cartItem.getQuantity();
                    if (tvQuantity != null) {
                        try {
                            quantity = Integer.parseInt(tvQuantity.getText().toString());
                        } catch (NumberFormatException ignored) {}
                    }
                    listener.onVariantApplied(cartItem.getVariant(), quantity);
                }
                dismiss();
            });
        }

        View btnIncrease = findViewById(R.id.btnVariantIncrease);
        View btnDecrease = findViewById(R.id.btnVariantDecrease);
        TextView tvQuantity = findViewById(R.id.tvVariantQuantity);

        if (btnIncrease != null && tvQuantity != null) {
            btnIncrease.setOnClickListener(v -> {
                int q = Integer.parseInt(tvQuantity.getText().toString());
                tvQuantity.setText(String.valueOf(q + 1));
            });
        }

        if (btnDecrease != null && tvQuantity != null) {
            btnDecrease.setOnClickListener(v -> {
                int q = Integer.parseInt(tvQuantity.getText().toString());
                if (q > 1) {
                    tvQuantity.setText(String.valueOf(q - 1));
                }
            });
        }
    }
}
