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

    public interface OnCartItemChangeListener {
        void onItemSelectedChanged(CartItemDto item, boolean isSelected);
        void onQuantityChanged(CartItemDto item, int newQuantity);
        void onVariantClick(CartItemDto item, int position);
        void onDeleteClick(CartItemDto item, int position);
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

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_swipeable, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItemDto item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelected;
        ImageView ivProduct;
        TextView tvName, tvVariant, tvPrice, tvQuantity;
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
            tvQuantity = layoutFront.findViewById(R.id.tvCartQuantity);
            btnDecrease = layoutFront.findViewById(R.id.btnDecreaseQuantity);
            btnIncrease = layoutFront.findViewById(R.id.btnIncreaseQuantity);
            btnWishlist = layoutFront.findViewById(R.id.btnCartWishlist);
            layoutVariant = layoutFront.findViewById(R.id.layoutCartVariant);

            tvActionSimilar = layoutAction.findViewById(R.id.tvActionSimilar);
            tvActionDelete = layoutAction.findViewById(R.id.tvActionDelete);
        }

        public void bind(CartItemDto item) {
            tvName.setText(item.getProductNameSnapshot());
            
            if (layoutVariant != null) {
                TextView tvVariantName = layoutVariant.findViewById(R.id.tvVariantName);
                if (tvVariantName != null) {
                    tvVariantName.setText(item.getVariantNameSnapshot());
                }
            } else if (tvVariant != null) {
                tvVariant.setText(item.getVariantNameSnapshot());
            }

            tvPrice.setText(formatPrice(item.getFinalUnitPriceAmount()));
            tvQuantity.setText(String.valueOf(item.getQuantity()));
            
            Glide.with(ivProduct.getContext())
                    .load(item.getImageUrlSnapshot())
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(ivProduct);
            
            // Note: Wishlist status might need separate API call or be part of item DTO if backend supports it
            btnWishlist.setSelected(false); 
            
            layoutFront.setTranslationX(0f);
            
            cbSelected.setOnCheckedChangeListener(null);
            cbSelected.setChecked(item.isSelected());
            cbSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) listener.onItemSelectedChanged(item, isChecked);
            });

            btnDecrease.setOnClickListener(v -> {
                if (item.getQuantity() > 1 && listener != null) {
                    listener.onQuantityChanged(item, item.getQuantity() - 1);
                }
            });

            btnIncrease.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuantityChanged(item, item.getQuantity() + 1);
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
                // Feature logic for similar products
            });
        }

        private String formatPrice(double price) {
            if (price == 0) return "Liên hệ";
            return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
        }
    }
}
