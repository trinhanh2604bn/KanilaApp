package com.example.frontend.feature.product;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.product.ProductVariantDto;
import com.example.frontend.model.Product;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class VariantSelectorBottomSheet extends BottomSheetDialogFragment {

    public enum ActionMode {
        ADD_TO_CART,
        BUY_NOW,
        CONFIRM
    }

    private Product product;
    private List<ProductVariantDto> variants;
    private OnVariantSelectedListener listener;
    private ActionMode mode = ActionMode.ADD_TO_CART;

    private ProductVariantDto selectedVariant;
    private int quantity = 1;

    private ImageView ivProduct;
    private TextView tvBrand, tvName, tvCurrentPrice, tvOriginalPrice, tvStock, tvQuantity;
    private LinearLayout layoutColorOptions, layoutSizeOptions;
    private MaterialButton btnApply;
    private View btnDecrease, btnIncrease;

    public interface OnVariantSelectedListener {
        void onVariantSelected(ProductVariantDto variant, ActionMode mode, int quantity);
    }

    public static VariantSelectorBottomSheet newInstance(Product product, List<ProductVariantDto> variants, ActionMode mode) {
        VariantSelectorBottomSheet fragment = new VariantSelectorBottomSheet();
        fragment.product = product;
        fragment.variants = variants;
        fragment.mode = mode;
        return fragment;
    }

    public void setListener(OnVariantSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_variant_selector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (product == null) return;

        initViews(view);
        ensureDefaultSelectedVariant();
        bindBaseData();
        renderVariants(view);
        updateSelectedVariantUI();

        view.findViewById(R.id.btnVariantClose).setOnClickListener(v -> dismiss());

        btnApply.setOnClickListener(v -> {
            if (selectedVariant == null && variants != null && !variants.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn phân loại sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }
            if (listener != null) {
                listener.onVariantSelected(selectedVariant, mode, quantity);
            }
            dismiss();
        });

        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityUI();
            }
        });

        btnIncrease.setOnClickListener(v -> {
            int maxStock = selectedVariant != null ? getSafeVariantStock(selectedVariant) : product.getStock();
            if (quantity < maxStock) {
                quantity++;
                updateQuantityUI();
            } else {
                Toast.makeText(getContext(), "Số lượng đạt giới hạn tồn kho", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews(View view) {
        ivProduct = view.findViewById(R.id.ivVariantProductImage);
        tvBrand = view.findViewById(R.id.tvVariantBrand);
        tvName = view.findViewById(R.id.tvVariantProductName);
        tvCurrentPrice = view.findViewById(R.id.tvVariantCurrentPrice);
        tvOriginalPrice = view.findViewById(R.id.tvVariantOriginalPrice);
        tvStock = view.findViewById(R.id.tvVariantStock);
        tvQuantity = view.findViewById(R.id.tvVariantQuantity);
        layoutColorOptions = view.findViewById(R.id.layoutColorOptions);
        layoutSizeOptions = view.findViewById(R.id.layoutSizeOptions);
        btnApply = view.findViewById(R.id.btnVariantApply);
        btnDecrease = view.findViewById(R.id.btnVariantDecrease);
        btnIncrease = view.findViewById(R.id.btnVariantIncrease);

        if (mode == ActionMode.BUY_NOW) {
            btnApply.setText("MUA NGAY");
        } else if (mode == ActionMode.CONFIRM) {
            btnApply.setText("Xác nhận");
        } else {
            btnApply.setText("Thêm vào giỏ hàng");
        }
    }

    private void bindBaseData() {
        tvBrand.setText(product.getBrand());
        tvName.setText(product.getName());
        updatePrice(product.getPriceValue(), product.getCompareAtPrice());
        tvStock.setText(String.format(Locale.US, "Còn %d sản phẩm", product.getStock()));

        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_product)
                .into(ivProduct);

        updateQuantityUI();
    }

    private void renderVariants(View rootView) {
        if (variants == null || variants.isEmpty()) {
            viewVisible(rootView, R.id.tvColorOptionsTitle, false);
            viewVisible(rootView, R.id.layoutColorOptions, false);
            viewVisible(rootView, R.id.tvSizeOptionsTitle, false);
            viewVisible(rootView, R.id.layoutSizeOptions, false);
            return;
        }

        layoutColorOptions.removeAllViews();
        layoutSizeOptions.removeAllViews();

        boolean colorSectionVisible = false;
        boolean sizeSectionVisible = false;

        for (ProductVariantDto variant : variants) {
            Product.Shade matchedShade = findMatchingShade(variant.getVariantName());
            if (matchedShade != null) {
                addColorOption(variant, matchedShade, rootView);
                colorSectionVisible = true;
            } else {
                addSizeOption(variant, rootView);
                sizeSectionVisible = true;
            }
        }

        viewVisible(rootView, R.id.tvColorOptionsTitle, colorSectionVisible);
        viewVisible(rootView, R.id.layoutColorOptions, colorSectionVisible);
        viewVisible(rootView, R.id.tvSizeOptionsTitle, sizeSectionVisible);
        viewVisible(rootView, R.id.layoutSizeOptions, sizeSectionVisible);
    }

    private Product.Shade findMatchingShade(String variantName) {
        if (product.getShades() == null || variantName == null) return null;
        for (Product.Shade shade : product.getShades()) {
            if (shade != null && shade.getShadeName() != null) {
                if (variantName.toLowerCase().contains(shade.getShadeName().toLowerCase())) {
                    return shade;
                }
            }
        }
        return null;
    }

    private String getVariantDisplayName(ProductVariantDto variant) {
        if (variant == null) return "";

        String rawName = variant.getVariantName();
        if (rawName == null || rawName.trim().isEmpty()) {
            return "Phân loại";
        }

        rawName = rawName.trim();

        Product.Shade matchedShade = findMatchingShade(rawName);
        if (matchedShade != null
                && matchedShade.getShadeName() != null
                && !matchedShade.getShadeName().trim().isEmpty()) {
            return matchedShade.getShadeName().trim();
        }

        return rawName;
    }

    private void addColorOption(ProductVariantDto variant, Product.Shade shade, View rootView) {
        if (variant == null || shade == null) return;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_variant_color, layoutColorOptions, false);
        View selectionBg = view.findViewById(R.id.viewSelectionBg);
        View colorCircle = view.findViewById(R.id.viewColorCircle);
        ImageView ivTick = view.findViewById(R.id.ivTick);
        TextView tvVariantName = view.findViewById(R.id.tvVariantName);

        tvVariantName.setText(getVariantDisplayName(variant));

        try {
            if (shade.getHex() != null) {
                colorCircle.getBackground().setTint(Color.parseColor(shade.getHex()));
            } else {
                colorCircle.getBackground().setTint(Color.GRAY);
            }
        } catch (Exception e) {
            colorCircle.getBackground().setTint(Color.GRAY);
        }

        boolean isSelected = isSameVariant(selectedVariant, variant);

        view.setBackgroundResource(isSelected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_outline);
        if (getContext() != null) {
            tvVariantName.setTextColor(isSelected ? Color.WHITE : ContextCompat.getColor(getContext(), R.color.text_main));
        }
        selectionBg.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        ivTick.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        view.setOnClickListener(v -> selectVariant(variant, rootView));

        layoutColorOptions.addView(view);
    }

    private void addSizeOption(ProductVariantDto variant, View rootView) {
        if (variant == null) return;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_variant_size, layoutSizeOptions, false);
        TextView tvSize;
        if (view instanceof TextView) {
            tvSize = (TextView) view;
        } else {
            tvSize = view.findViewById(R.id.tvVariantSize);
        }

        if (tvSize == null) return;

        tvSize.setText(getVariantDisplayName(variant));

        boolean isSelected = isSameVariant(selectedVariant, variant);

        tvSize.setBackgroundResource(isSelected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_outline);
        if (getContext() != null) {
            tvSize.setTextColor(isSelected ? Color.WHITE : ContextCompat.getColor(getContext(), R.color.text_main));
        }

        if (getSafeVariantStock(variant) <= 0) {
            view.setEnabled(false);
            view.setAlpha(0.5f);
        } else {
            view.setEnabled(true);
            view.setAlpha(1.0f);
            view.setOnClickListener(v -> selectVariant(variant, rootView));
            if (!(view instanceof TextView)) {
                tvSize.setOnClickListener(v -> selectVariant(variant, rootView));
            }
        }

        layoutSizeOptions.addView(view);
    }

    private void updateSelectedVariantUI() {
        if (selectedVariant == null) return;

        String vName = selectedVariant.getVariantName() != null ? selectedVariant.getVariantName() : "";
        tvName.setText(vName);

        double currentPrice = selectedVariant.getPrice() != null ? selectedVariant.getPrice() : product.getPriceValue();
        updatePrice(currentPrice, product.getCompareAtPrice());

        int stock = getSafeVariantStock(selectedVariant);
        tvStock.setText(String.format(Locale.US, "Còn %d sản phẩm", stock));

        if (selectedVariant.getImageUrl() != null && !selectedVariant.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(selectedVariant.getImageUrl())
                    .placeholder(R.drawable.ic_product)
                    .into(ivProduct);
        }

        if (stock <= 0) {
            btnApply.setEnabled(false);
            btnApply.setAlpha(0.5f);
        } else {
            btnApply.setEnabled(true);
            btnApply.setAlpha(1.0f);
        }

        if (quantity > stock && stock > 0) {
            quantity = stock;
            updateQuantityUI();
        } else if (quantity < 1) {
            quantity = 1;
            updateQuantityUI();
        }
    }

    private boolean isSameVariant(ProductVariantDto a, ProductVariantDto b) {
        if (a == null || b == null) return false;
        if (a == b) return true;

        String aId = a.getId();
        String bId = b.getId();

        if (aId != null && !aId.trim().isEmpty() && bId != null && !bId.trim().isEmpty()) {
            return aId.equals(bId);
        }

        String aName = a.getVariantName();
        String bName = b.getVariantName();

        return aName != null && aName.equalsIgnoreCase(bName);
    }

    private int getSafeVariantStock(ProductVariantDto variant) {
        if (variant == null) {
            return product != null ? Math.max(product.getStock(), 0) : 0;
        }

        int variantStock = variant.getStockQuantity();
        if (variantStock > 0) {
            return variantStock;
        }

        if (product != null && product.getStock() > 0) {
            return product.getStock();
        }

        return 0;
    }

    private void selectVariant(ProductVariantDto variant, View rootView) {
        selectedVariant = variant;
        renderVariants(rootView);
        updateSelectedVariantUI();
    }

    private void ensureDefaultSelectedVariant() {
        if (selectedVariant != null) return;
        if (variants == null || variants.isEmpty()) return;

        for (ProductVariantDto variant : variants) {
            if (getSafeVariantStock(variant) > 0) {
                selectedVariant = variant;
                return;
            }
        }

        selectedVariant = variants.get(0);
    }

    private void updatePrice(double current, Double original) {
        tvCurrentPrice.setText(formatPrice(current));
        if (original != null && original > current) {
            tvOriginalPrice.setVisibility(View.VISIBLE);
            tvOriginalPrice.setText(formatPrice(original));
            tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvOriginalPrice.setVisibility(View.GONE);
        }
    }

    private void updateQuantityUI() {
        tvQuantity.setText(String.valueOf(quantity));
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
    }

    private void viewVisible(View rootView, int id, boolean visible) {
        View v = rootView.findViewById(id);
        if (v != null) v.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
