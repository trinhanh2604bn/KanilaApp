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

import com.example.frontend.R;
import com.example.frontend.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> items = new ArrayList<>();
    private OnCartItemChangeListener listener;

    public interface OnCartItemChangeListener {
        void onItemSelectedChanged();
        void onQuantityChanged();
        void onVariantClick(CartItem item, int position);
    }

    public void setOnCartItemChangeListener(OnCartItemChangeListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public List<CartItem> getItems() {
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
        CartItem item = items.get(position);
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
            tvVariant = layoutFront.findViewById(R.id.tvCartProductVariant);
            tvPrice = layoutFront.findViewById(R.id.tvCartPrice);
            tvQuantity = layoutFront.findViewById(R.id.tvCartQuantity);
            btnDecrease = layoutFront.findViewById(R.id.btnDecreaseQuantity);
            btnIncrease = layoutFront.findViewById(R.id.btnIncreaseQuantity);
            btnWishlist = layoutFront.findViewById(R.id.btnCartWishlist);
            layoutVariant = layoutFront.findViewById(R.id.layoutCartVariant);

            tvActionSimilar = layoutAction.findViewById(R.id.tvActionSimilar);
            tvActionDelete = layoutAction.findViewById(R.id.tvActionDelete);
        }

        public void bind(CartItem item) {
            tvName.setText(item.getProduct().getName());
            tvVariant.setText(item.getVariant());
            tvPrice.setText(item.getProduct().getPrice());
            tvQuantity.setText(String.valueOf(item.getQuantity()));
            ivProduct.setImageResource(item.getProduct().getImageResource());
            btnWishlist.setSelected(item.isWishlisted());
            
            // Reset translation in case view is reused
            layoutFront.setTranslationX(0f);
            
            cbSelected.setOnCheckedChangeListener(null);
            cbSelected.setChecked(item.isSelected());
            cbSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setSelected(isChecked);
                if (listener != null) listener.onItemSelectedChanged();
            });

            btnDecrease.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    tvQuantity.setText(String.valueOf(item.getQuantity()));
                    if (listener != null) listener.onQuantityChanged();
                }
            });

            btnIncrease.setOnClickListener(v -> {
                item.setQuantity(item.getQuantity() + 1);
                tvQuantity.setText(String.valueOf(item.getQuantity()));
                if (listener != null) listener.onQuantityChanged();
            });

            btnWishlist.setOnClickListener(v -> {
                boolean newState = !item.isWishlisted();
                item.setWishlisted(newState);
                btnWishlist.setSelected(newState);
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
                if (pos != RecyclerView.NO_POSITION) {
                    items.remove(pos);
                    notifyItemRemoved(pos);
                    if (listener != null) listener.onItemSelectedChanged();
                }
            });

            tvActionSimilar.setOnClickListener(v -> {
                // Feature logic for similar products
            });
        }
    }
}
