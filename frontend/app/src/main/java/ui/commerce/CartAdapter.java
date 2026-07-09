package ui.commerce;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.cart.CartItemDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItemDto> items = new ArrayList<>();
    private OnCartItemChangeListener listener;
    private int swipedPosition = -1;

    public interface OnCartItemChangeListener {
        void onItemSelectedChanged(CartItemDto item, int position, boolean isSelected);
        void onQuantityChanged(CartItemDto item, int position, int newQuantity);
        void onVariantClick(CartItemDto item, int position);
        void onDeleteClick(CartItemDto item, int position);
        void onWishlistClick(CartItemDto item, int position);
        void onSimilarClick(CartItemDto item, int position);
    }

    public void setOnCartItemChangeListener(OnCartItemChangeListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setSwipedPosition(int position) {
        if (swipedPosition == position) return;

        int oldPosition = swipedPosition;
        swipedPosition = position;

        if (oldPosition != -1 && oldPosition < items.size()) {
            notifyItemChanged(oldPosition);
        }
        if (swipedPosition != -1 && swipedPosition < items.size()) {
            notifyItemChanged(swipedPosition);
        }
    }

    public int getSwipedPosition() {
        return swipedPosition;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_swipeable, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItemDto item = items.get(position);
        boolean isSwiped = (position == swipedPosition);
        holder.bind(item, isSwiped);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelected;
        ImageView ivProduct;
        TextView tvName, tvPrice, tvOldPrice, tvDiscount, tvQuantity;
        ImageButton btnDecrease, btnIncrease, btnWishlist;
        View layoutFront, layoutAction, layoutVariant;
        View tvActionSimilar, tvActionDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutFront = itemView.findViewById(R.id.layoutCartFront);
            layoutAction = itemView.findViewById(R.id.layoutCartAction);

            cbSelected = layoutFront.findViewById(R.id.cbCartSelected);
            ivProduct = layoutFront.findViewById(R.id.ivCartProductImage);
            tvName = layoutFront.findViewById(R.id.tvCartProductName);
            // tvVariant = layoutFront.findViewById(R.id.tvCartProductVariant); // Removed as we use included layout
            tvPrice = layoutFront.findViewById(R.id.tvCartPrice);
            tvOldPrice = layoutFront.findViewById(R.id.tvCartOldPrice);
            tvDiscount = layoutFront.findViewById(R.id.tvCartDiscount);
            tvQuantity = layoutFront.findViewById(R.id.tvCartQuantity);
            btnDecrease = layoutFront.findViewById(R.id.btnDecreaseQuantity);
            btnIncrease = layoutFront.findViewById(R.id.btnIncreaseQuantity);
            btnWishlist = layoutFront.findViewById(R.id.btnCartWishlist);
            layoutVariant = layoutFront.findViewById(R.id.item_variant_selection);

            tvActionSimilar = layoutAction.findViewById(R.id.tvActionSimilar);
            tvActionDelete = layoutAction.findViewById(R.id.tvActionDelete);
        }

        public void bind(CartItemDto item, boolean isSwiped) {
            String name = item.getProductNameSnapshot();
            String variant = getDisplayVariantName(item);
            double price = item.getFinalUnitPriceAmount();

            android.util.Log.d("CartAdapter", "Binding item: " + item.getId() + 
                ", Name=" + name + ", Variant=" + variant + ", Price=" + price);

            tvName.setText(name != null && !name.isEmpty() ? name : "Sản phẩm");

            if (layoutVariant != null) {
                TextView tvVariantName = layoutVariant.findViewById(R.id.tvVariantName);
                if (tvVariantName != null) {
                    tvVariantName.setText(variant != null && !variant.isEmpty() ? variant : "Mặc định");
                }
            }

            tvPrice.setText(formatPrice(price));

            if (tvOldPrice != null) {
                double oldPrice = item.getCompareAtPriceAmount();
                if (oldPrice > item.getFinalUnitPriceAmount()) {
                    tvOldPrice.setVisibility(View.VISIBLE);
                    tvOldPrice.setText(formatPrice(oldPrice));
                    tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

                    if (tvDiscount != null) {
                        tvDiscount.setVisibility(View.VISIBLE);
                        int percent = (int) Math.round((oldPrice - item.getFinalUnitPriceAmount()) / oldPrice * 100);
                        tvDiscount.setText("-" + percent + "%");
                    }
                } else {
                    tvOldPrice.setVisibility(View.GONE);
                    if (tvDiscount != null) tvDiscount.setVisibility(View.GONE);
                }
            }

            tvQuantity.setText(String.valueOf(item.getQuantity()));

            Glide.with(ivProduct.getContext())
                    .load(item.getImageUrlSnapshot() != null ? item.getImageUrlSnapshot() : "")
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(ivProduct);

            btnWishlist.setSelected(item.isFavorite());

            btnWishlist.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    boolean newState = !item.isFavorite();
                    item.setFavorite(newState);
                    v.setSelected(newState);
                    listener.onWishlistClick(item, pos);
                }
            });

            if (isSwiped) {
                float actionWidth = 160 * layoutFront.getContext().getResources().getDisplayMetrics().density;
                layoutFront.setTranslationX(-actionWidth);
            } else {
                layoutFront.setTranslationX(0f);
            }

            layoutFront.setOnTouchListener((v, event) -> {
                if (isSwiped) {
                    if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                        v.performClick();
                        setSwipedPosition(-1);
                    }
                    return true;
                }
                return false;
            });

            cbSelected.setOnCheckedChangeListener(null);
            cbSelected.setChecked(item.isSelected());
            cbSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemSelectedChanged(item, pos, isChecked);
                }
            });

            btnDecrease.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && item.getQuantity() > 1 && listener != null) {
                    listener.onQuantityChanged(item, pos, item.getQuantity() - 1);
                }
            });

            btnIncrease.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onQuantityChanged(item, pos, item.getQuantity() + 1);
                }
            });

            if (layoutVariant != null) {
                layoutVariant.setOnClickListener(v -> {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && listener != null) {
                        listener.onVariantClick(item, pos);
                    }
                });
            }

            tvActionDelete.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteClick(item, pos);
                }
            });

            tvActionSimilar.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onSimilarClick(item, pos);
                }
            });
        }

        private String getDisplayVariantName(CartItemDto item) {
            String variantName = item.getVariantNameSnapshot();
            String productName = item.getProductNameSnapshot();

            if (variantName == null || variantName.isEmpty()) return "";
            if (productName == null || productName.isEmpty()) return variantName;

            String display = variantName;

            // Standard case: "Product Name - Variant Detail"
            if (display.contains(productName + " - ")) {
                display = display.replace(productName + " - ", "");
            } else if (display.startsWith(productName)) {
                // Handle case where it starts with product name but maybe different separator
                String potential = display.substring(productName.length()).trim();
                if (!potential.isEmpty()) {
                    if (potential.startsWith("-") || potential.startsWith(":") || potential.startsWith("•")) {
                        display = potential.substring(1).trim();
                    } else {
                        display = potential;
                    }
                }
            }

            // Append SKU if available and not already in variant name
            String sku = item.getSkuSnapshot();
            if (sku != null && !sku.isEmpty() && !display.contains(sku)) {
                display += " (SKU: " + sku + ")";
            }

            return display;
        }

        private String formatPrice(double price) {
            if (price == 0) return "Liên hệ";
            return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
        }
    }
}
