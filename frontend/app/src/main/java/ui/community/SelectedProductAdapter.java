package ui.community;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.databinding.ItemCreatePostProductUsedBinding;
import com.example.frontend.model.Product;
import java.util.ArrayList;
import java.util.List;

public class SelectedProductAdapter extends RecyclerView.Adapter<SelectedProductAdapter.ViewHolder> {

    private final List<Product> selectedProducts = new ArrayList<>();
    private final OnProductActionListener listener;

    public interface OnProductActionListener {
        void onProductClick(Product product);
        void onProductRemove(Product product);
    }

    public SelectedProductAdapter(OnProductActionListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.selectedProducts.clear();
        if (products != null) {
            this.selectedProducts.addAll(products);
        }
        notifyDataSetChanged();
    }

    public void addProduct(Product product) {
        // Check if product is already added
        for (Product p : selectedProducts) {
            if (p.getId().equals(product.getId())) {
                return;
            }
        }
        selectedProducts.add(product);
        notifyItemInserted(selectedProducts.size() - 1);
    }

    public void removeProduct(Product product) {
        int index = selectedProducts.indexOf(product);
        if (index != -1) {
            selectedProducts.remove(index);
            notifyItemRemoved(index);
        }
    }

    public List<Product> getSelectedProducts() {
        return selectedProducts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCreatePostProductUsedBinding binding = ItemCreatePostProductUsedBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = selectedProducts.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return selectedProducts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCreatePostProductUsedBinding binding;

        ViewHolder(ItemCreatePostProductUsedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Product product) {
            binding.tvProductName.setText(product.getName());
            binding.tvProductSubtitle.setText(product.getBrand());

            Glide.with(binding.ivProductImage.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(binding.ivProductImage);

            binding.layoutProductCard.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });

            binding.btnRemoveProduct.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductRemove(product);
                }
            });
        }
    }
}
