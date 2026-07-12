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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import com.example.frontend.R;
import com.example.frontend.data.model.review.ReviewDto;
import java.util.ArrayList;
import java.util.List;
import android.graphics.drawable.Drawable;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<ReviewDto> reviews = new ArrayList<>();
    private OnReviewLikeListener likeListener;
    private OnReviewClickListener clickListener;

    public interface OnReviewLikeListener {
        void onLikeClick(ReviewDto review);
    }

    public interface OnReviewClickListener {
        void onReviewClick(ReviewDto review);
    }

    public void setOnReviewLikeListener(OnReviewLikeListener listener) {
        this.likeListener = listener;
    }

    public void setOnReviewClickListener(OnReviewClickListener listener) {
        this.clickListener = listener;
    }

    public void setReviews(List<ReviewDto> reviews) {
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void submitList(List<ReviewDto> reviews) {
        setReviews(reviews);
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
        
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onReviewClick(review);
            }
        });

        String fullName = review.getCustomer() != null ? review.getCustomer().getFullName() : "Người dùng";
        holder.tvUserName.setText(fullName);
        holder.tvContent.setText(review.getReviewContent() != null ? review.getReviewContent() : "");
        holder.rbStars.setRating((float) review.getRating());
        
        // Format date if needed, or use raw string from backend
        holder.tvDate.setText(review.getCreatedAt());
        
        if (holder.tvVerified != null) {
            holder.tvVerified.setVisibility(review.isVerifiedPurchase() ? View.VISIBLE : View.GONE);
        }

        if (holder.btnHelpful != null) {
            holder.btnHelpful.setText("Yêu thích (" + review.getHelpfulCount() + ")");
            int colorRes = review.isLikedByMe() ? R.color.button : R.color.text_tertiary;
            int color = ContextCompat.getColor(holder.itemView.getContext(), colorRes);
            holder.btnHelpful.setTextColor(color);
            
            Drawable[] drawables = holder.btnHelpful.getCompoundDrawablesRelative();
            if (drawables[0] != null) {
                Drawable wrapped = DrawableCompat.wrap(drawables[0].mutate());
                DrawableCompat.setTint(wrapped, color);
                holder.btnHelpful.setCompoundDrawablesRelativeWithIntrinsicBounds(wrapped, drawables[1], drawables[2], drawables[3]);
            }

            holder.btnHelpful.setOnClickListener(v -> {
                if (likeListener != null) {
                    likeListener.onLikeClick(review);
                }
            });
        }

        if (holder.ivAvatar != null) {
            String avatarUrl = review.getCustomer() != null ? review.getCustomer().getAvatarUrl() : null;
            Glide.with(holder.ivAvatar.getContext())
                    .load(avatarUrl)
                    .placeholder(R.drawable.bg_avatar_circle)
                    .error(R.drawable.bg_avatar_circle)
                    .circleCrop()
                    .into(holder.ivAvatar);
        }

        if (holder.rvMedia != null && review.getMedia() != null && !review.getMedia().isEmpty()) {
            holder.rvMedia.setVisibility(View.VISIBLE);
            ReviewMediaAdapter mediaAdapter = new ReviewMediaAdapter();
            List<String> mediaUrls = new ArrayList<>();
            for (ReviewDto.MediaInfo media : review.getMedia()) {
                mediaUrls.add(media.getMediaUrl());
            }
            mediaAdapter.setMediaUrls(mediaUrls);
            holder.rvMedia.setAdapter(mediaAdapter);
        } else if (holder.rvMedia != null) {
            holder.rvMedia.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvContent, tvDate, tvVerified, btnHelpful;
        RatingBar rbStars;
        ImageView ivAvatar;
        RecyclerView rvMedia;
        
        ViewHolder(View view) {
            super(view);
            tvUserName = view.findViewById(R.id.tvReviewUserName);
            tvContent = view.findViewById(R.id.tvReviewContent);
            rbStars = view.findViewById(R.id.rbReviewStars);
            ivAvatar = view.findViewById(R.id.ivReviewAvatar);
            tvDate = view.findViewById(R.id.tvReviewDate);
            tvVerified = view.findViewById(R.id.tvVerifiedPurchase);
            btnHelpful = view.findViewById(R.id.btnHelpful);
            rvMedia = view.findViewById(R.id.rvReviewMedia);
            
            if (rvMedia != null) {
                rvMedia.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
            }
        }
    }
}
