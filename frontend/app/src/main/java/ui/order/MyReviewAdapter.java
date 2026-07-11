package ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.review.MyReviewDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyReviewAdapter extends RecyclerView.Adapter<MyReviewAdapter.ViewHolder> {

    public interface OnReviewClickListener {
        void onReviewClick(MyReviewDto review);
    }

    private final List<MyReviewDto> reviews = new ArrayList<>();
    private final OnReviewClickListener listener;

    public MyReviewAdapter(OnReviewClickListener listener) {
        this.listener = listener;
    }

    public void setReviews(List<MyReviewDto> newReviews) {
        reviews.clear();
        if (newReviews != null) {
            reviews.addAll(newReviews);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(reviews.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvVariantName, tvReviewDate, tvReviewContent, tvMediaCount;
        RatingBar rbRating;
        View btnViewDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvVariantName = itemView.findViewById(R.id.tvVariantName);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            tvReviewContent = itemView.findViewById(R.id.tvReviewContent);
            tvMediaCount = itemView.findViewById(R.id.tvMediaCount);
            rbRating = itemView.findViewById(R.id.rbRating);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
        }

        public void bind(MyReviewDto review, OnReviewClickListener listener) {
            if (review.getProduct() != null) {
                tvProductName.setText(review.getProduct().getProductName());
                tvVariantName.setText(review.getProduct().getVariantName());
                Glide.with(itemView.getContext())
                        .load(review.getProduct().getImageUrl())
                        .placeholder(R.drawable.ic_product)
                        .error(R.drawable.ic_product)
                        .into(ivProductImage);
            }

            // Date formatting
            if (review.getCreatedAt() != null && review.getCreatedAt().contains("T")) {
                String dateStr = review.getCreatedAt().split("T")[0];
                String[] parts = dateStr.split("-");
                if (parts.length == 3) {
                    tvReviewDate.setText(parts[2] + "/" + parts[1] + "/" + parts[0]);
                } else {
                    tvReviewDate.setText(dateStr);
                }
            } else {
                tvReviewDate.setText(review.getCreatedAt());
            }

            rbRating.setRating(review.getRating());
            tvReviewContent.setText(review.getReviewContent());

            int mediaSize = review.getMedia() != null ? review.getMedia().size() : 0;
            if (mediaSize > 0) {
                tvMediaCount.setText(String.format(Locale.getDefault(), "%d ảnh", mediaSize));
                itemView.findViewById(R.id.layoutMediaInfo).setVisibility(View.VISIBLE);
            } else {
                itemView.findViewById(R.id.layoutMediaInfo).setVisibility(View.GONE);
            }

            btnViewDetail.setOnClickListener(v -> {
                if (listener != null) listener.onReviewClick(review);
            });
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onReviewClick(review);
            });
        }
    }
}
