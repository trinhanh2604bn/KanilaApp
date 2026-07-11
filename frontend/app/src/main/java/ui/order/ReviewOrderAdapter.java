package ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.model.order.ReviewOrderItemsDto;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReviewOrderAdapter extends RecyclerView.Adapter<ReviewOrderAdapter.ViewHolder> {

    public interface OnReviewClickListener {
        void onReviewClick(ReviewOrderItemsDto.ReviewItemDto item);
    }

    private final List<ReviewOrderItemsDto.ReviewItemDto> items = new ArrayList<>();
    private final OnReviewClickListener listener;

    public ReviewOrderAdapter(OnReviewClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<ReviewOrderItemsDto.ReviewItemDto> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_order_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvVariantName, tvPrice;
        View btnReviewNow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvVariantName = itemView.findViewById(R.id.tvVariantName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnReviewNow = itemView.findViewById(R.id.btnReviewNow);
        }

        public void bind(ReviewOrderItemsDto.ReviewItemDto item, OnReviewClickListener listener) {
            String productName = item.getProductName() != null ? item.getProductName().trim() : "";
            tvProductName.setText(productName);

            // Clean up variant name: (Product Name - Variant) -> Variant
            String variantDisplay = item.getVariantName() != null ? item.getVariantName().trim() : "";
            if (!variantDisplay.isEmpty() && !productName.isEmpty()) {
                // Remove product name if it's a prefix
                if (variantDisplay.toLowerCase().startsWith(productName.toLowerCase())) {
                    String potential = variantDisplay.substring(productName.length()).trim();
                    if (potential.startsWith("-") || potential.startsWith(":") || potential.startsWith("•")) {
                        variantDisplay = potential.substring(1).trim();
                    } else if (!potential.isEmpty()) {
                        variantDisplay = potential;
                    }
                }
                
                // Also check for "productName - variant" pattern specifically
                String pattern = productName + " - ";
                if (variantDisplay.toLowerCase().contains(pattern.toLowerCase())) {
                    // Find the index regardless of case
                    int index = variantDisplay.toLowerCase().indexOf(pattern.toLowerCase());
                    variantDisplay = variantDisplay.substring(index + pattern.length()).trim();
                }
            }

            if (variantDisplay == null || variantDisplay.isEmpty() || variantDisplay.equalsIgnoreCase(productName)) {
                variantDisplay = "Mặc định";
            }
            tvVariantName.setText(variantDisplay);

            tvPrice.setText(String.format(Locale.getDefault(), "%,.0fđ", item.getUnitPrice()).replace(",", "."));

            Glide.with(itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .centerCrop()
                    .into(ivProductImage);

            if ("reviewed".equals(item.getReviewStatus())) {
                btnReviewNow.setEnabled(false);
                if (btnReviewNow instanceof TextView) {
                    ((TextView) btnReviewNow).setText("Đã đánh giá");
                }
            } else {
                btnReviewNow.setEnabled(true);
                if (btnReviewNow instanceof TextView) {
                    ((TextView) btnReviewNow).setText("Đánh giá ngay");
                }
                btnReviewNow.setOnClickListener(v -> {
                    if (listener != null) listener.onReviewClick(item);
                });
            }
        }
    }
}
