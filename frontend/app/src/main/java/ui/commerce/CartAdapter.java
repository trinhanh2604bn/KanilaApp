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
        ImageButton btnDecrease, btnIncrease, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_cart_swipeable has layoutCartFront which is item_cart
            View front = itemView.findViewById(R.id.layoutCartFront);
            cbSelected = front.findViewById(R.id.cbCartSelected);
            ivProduct = front.findViewById(R.id.ivCartProductImage);
            tvName = front.findViewById(R.id.tvCartProductName);
            tvVariant = front.findViewById(R.id.tvCartProductVariant);
            tvPrice = front.findViewById(R.id.tvCartPrice);
            tvQuantity = front.findViewById(R.id.tvCartQuantity);
            btnDecrease = front.findViewById(R.id.btnDecreaseQuantity);
            btnIncrease = front.findViewById(R.id.btnIncreaseQuantity);
        }

        public void bind(CartItem item) {
            tvName.setText(item.getProduct().getName());
            tvVariant.setText(item.getVariant());
            tvPrice.setText(item.getProduct().getPrice());
            tvQuantity.setText(String.valueOf(item.getQuantity()));
            ivProduct.setImageResource(item.getProduct().getImageResource());
            
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

            if (btnRemove != null) {
                btnRemove.setOnClickListener(v -> {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        items.remove(pos);
                        notifyItemRemoved(pos);
                        if (listener != null) listener.onItemSelectedChanged();
                    }
                });
            }
        }
    }
}
