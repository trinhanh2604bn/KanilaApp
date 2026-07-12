package com.example.frontend.feature.product.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.RatingBar;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

import com.example.frontend.data.model.review.ReviewDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<ReviewDto> reviews = new ArrayList<>();

    public void setReviews(List<ReviewDto> reviews) {
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewDto review = reviews.get(position);
        
        if (review.getCustomer() != null) {
            holder.tvUserName.setText(review.getCustomer().getFullName());
            Glide.with(holder.ivAvatar.getContext())
                    .load(review.getCustomer().getAvatarUrl())
                    .placeholder(R.drawable.bg_avatar_circle)
                    .error(R.drawable.bg_avatar_circle)
                    .circleCrop()
                    .into(holder.ivAvatar);
        }

        holder.tvContent.setText(review.getContent());
        holder.rbStars.setRating(review.getRating());
        
        // Date
        if (review.getCreatedAt() != null && review.getCreatedAt().contains("T")) {
             holder.tvDate.setText(review.getCreatedAt().split("T")[0]);
        } else {
             holder.tvDate.setText(review.getCreatedAt());
        }

        // Verified purchase
        holder.tvVerified.setVisibility(review.isVerifiedPurchase() ? View.VISIBLE : View.GONE);

        // Media
        if (review.getMedia() != null && !review.getMedia().isEmpty()) {
            holder.rvMedia.setVisibility(View.VISIBLE);
            ReviewMediaAdapter mediaAdapter = new ReviewMediaAdapter();
            holder.rvMedia.setAdapter(mediaAdapter);
            List<String> urls = new ArrayList<>();
            for (com.example.frontend.data.model.review.MyReviewDto.ReviewMediaDto m : review.getMedia()) {
                urls.add(m.getMediaUrl());
            }
            mediaAdapter.setMediaUrls(urls);
        } else {
            holder.rvMedia.setVisibility(View.GONE);
        }

        // Helpful count
        holder.btnHelpful.setText(String.format(Locale.getDefault(), "Hữu ích (%d)", review.getHelpfulCount()));
        
        // Read only: disable click actions if they were interactive
        holder.btnHelpful.setClickable(false);
        holder.btnReply.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvContent, tvDate, tvVerified;
        RatingBar rbStars;
        ImageView ivAvatar;
        RecyclerView rvMedia;
        TextView btnHelpful, btnReply;
        
        ViewHolder(View view) {
            super(view);
            tvUserName = view.findViewById(R.id.tvReviewUserName);
            tvContent = view.findViewById(R.id.tvReviewContent);
            rbStars = view.findViewById(R.id.rbReviewStars);
            ivAvatar = view.findViewById(R.id.ivReviewAvatar);
            tvDate = view.findViewById(R.id.tvReviewDate);
            tvVerified = view.findViewById(R.id.tvVerifiedPurchase);
            rvMedia = view.findViewById(R.id.rvReviewMedia);
            btnHelpful = view.findViewById(R.id.btnHelpful);
            btnReply = view.findViewById(R.id.btnReply);
        }
    }
}
