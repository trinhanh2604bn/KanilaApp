package ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.model.Product;
import java.util.ArrayList;
import java.util.List;

public class ChallengeSelectedProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ADD = 1;
    private static final int VIEW_TYPE_PRODUCT = 2;

    private List<Product> selectedProducts = new ArrayList<>();
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onAddClick();
        void onRemoveClick(Product product);
    }

    public ChallengeSelectedProductAdapter(OnProductActionListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.selectedProducts = products;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == selectedProducts.size()) return VIEW_TYPE_ADD;
        return VIEW_TYPE_PRODUCT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ADD) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge_add_product, parent, false);
            return new AddViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_create_post_product_used, parent, false);
            return new ProductViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AddViewHolder) {
            ((AddViewHolder) holder).bind();
        } else if (holder instanceof ProductViewHolder) {
            ((ProductViewHolder) holder).bind(selectedProducts.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return selectedProducts.size() + 1;
    }

    class AddViewHolder extends RecyclerView.ViewHolder {
        public AddViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public void bind() {
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onAddClick();
            });
        }
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductSubtitle;
        View btnRemove;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductSubtitle = itemView.findViewById(R.id.tvProductSubtitle);
            btnRemove = itemView.findViewById(R.id.btnRemoveProduct);
        }

        public void bind(Product product) {
            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product)
                    .into(ivProductImage);
            
            if (tvProductName != null) tvProductName.setText(product.getName());
            if (tvProductSubtitle != null) tvProductSubtitle.setText(product.getBrand()); // Or category

            btnRemove.setOnClickListener(v -> {
                if (listener != null) listener.onRemoveClick(product);
            });
        }
    }
}
