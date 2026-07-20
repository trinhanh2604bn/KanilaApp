package com.example.frontend.feature.product.adapter;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.review.ReviewCommentDto;
import com.example.frontend.data.model.review.ReviewDto;
import com.example.frontend.data.model.review.ReviewMediaDto;
import com.example.frontend.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<ReviewDto> reviews = new ArrayList<>();
    private OnReviewLikeListener likeListener;
    private OnReviewClickListener clickListener;
    private OnReviewReplyListener replyListener;

    public interface OnReviewLikeListener {
        void onLikeClick(ReviewDto review);
    }

    public interface OnReviewClickListener {
        void onReviewClick(ReviewDto review);
    }

    public interface OnReviewReplyListener {
        void onReplyClick(ReviewDto review);
    }

    public void setOnReviewLikeListener(OnReviewLikeListener listener) {
        this.likeListener = listener;
    }

    public void setOnReviewClickListener(OnReviewClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnReviewReplyListener(OnReviewReplyListener listener) {
        this.replyListener = listener;
    }

    public void setReviews(List<ReviewDto> reviews) {
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void submitList(List<ReviewDto> reviews) {
        setReviews(reviews);
    }

    public void updateReviewVoteState(String reviewId, boolean liked, int helpfulCount) {
        if (reviewId == null) return;
        for (int i = 0; i < reviews.size(); i++) {
            ReviewDto item = reviews.get(i);
            if (item != null && reviewId.equals(item.getId())) {
                item.setLikedByMe(liked);
                item.setHelpfulCount(Math.max(0, helpfulCount));
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void addCommentToReview(ReviewCommentDto comment) {
        if (comment == null || comment.getReviewId() == null) return;
        for (int i = 0; i < reviews.size(); i++) {
            ReviewDto item = reviews.get(i);
            if (item != null && comment.getReviewId().equals(item.getId())) {
                item.addComment(comment);
                notifyItemChanged(i);
                return;
            }
        }
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

        if (review.getCustomer() != null) {
            holder.tvUserName.setText(review.getCustomer().getFullName());
            String avatarUrl = UrlUtils.getFullUrl(review.getCustomer().getAvatarUrl());
            if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                Glide.with(holder.ivAvatar.getContext())
                        .load(avatarUrl)
                        .placeholder(R.drawable.bg_avatar_circle)
                        .error(R.drawable.bg_avatar_circle)
                        .circleCrop()
                        .into(holder.ivAvatar);
            } else {
                holder.ivAvatar.setImageResource(R.drawable.bg_avatar_circle);
            }
        } else {
            holder.tvUserName.setText("Người dùng");
            holder.ivAvatar.setImageResource(R.drawable.bg_avatar_circle);
        }

        String content = review.getReviewContent();
        if (content == null || content.isEmpty()) {
            content = review.getContent();
        }
        holder.tvContent.setText(content != null ? content : "");
        holder.rbStars.setRating((float) review.getRating());

        // Date
        String date = review.getCreatedAt();
        if (date != null && date.contains("T")) {
            holder.tvDate.setText(date.split("T")[0]);
        } else {
            holder.tvDate.setText(date != null ? date : "");
        }

        // Verified purchase
        if (holder.tvVerified != null) {
            holder.tvVerified.setVisibility(review.isVerifiedPurchase() ? View.VISIBLE : View.GONE);
        }

        // Helpful count & Like state
        bindLikeButton(holder, review);

        // Reply button
        if (holder.btnReply != null) {
            holder.btnReply.setOnClickListener(v -> {
                if (replyListener != null) {
                    replyListener.onReplyClick(review);
                }
            });
        }

        // Media
        if (holder.rvMedia != null) {
            if (review.getMedia() != null && !review.getMedia().isEmpty()) {
                holder.rvMedia.setVisibility(View.VISIBLE);
                ReviewMediaAdapter mediaAdapter = new ReviewMediaAdapter();
                List<ReviewMediaDto> validMedia = new ArrayList<>();
                for (ReviewMediaDto media : review.getMedia()) {
                    String url = media.getMediaUrl();
                    if (url != null && url.startsWith("content://")) {
                        Log.e("ReviewMedia", "Invalid backend mediaUrl. Must be HTTP/HTTPS: " + url);
                        continue;
                    }
                    // The ReviewMediaAdapter will handle prepending the base URL
                    validMedia.add(media);
                }
                mediaAdapter.submitList(validMedia);
                holder.rvMedia.setAdapter(mediaAdapter);
            } else {
                holder.rvMedia.setVisibility(View.GONE);
            }
        }

        // Comments
        bindComments(holder, review);
    }

    private void bindLikeButton(ViewHolder holder, ReviewDto review) {
        if (holder.btnHelpful == null || review == null) return;

        int count = Math.max(0, review.getHelpfulCount());
        boolean liked = review.isLikedByMe();

        holder.btnHelpful.setText(
                String.format(Locale.getDefault(), "Yêu thích (%d)", count)
        );

        int color = ContextCompat.getColor(
                holder.itemView.getContext(),
                liked ? R.color.button : R.color.text_tertiary
        );

        holder.btnHelpful.setTextColor(color);
        holder.btnHelpful.setSelected(liked);

        // Sử dụng icon phù hợp với trạng thái
        int iconRes = liked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline;
        Drawable heart = ContextCompat.getDrawable(holder.itemView.getContext(), iconRes);

        if (heart != null) {
            heart = DrawableCompat.wrap(heart.mutate());
            DrawableCompat.setTint(heart, color);
            holder.btnHelpful.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    heart,
                    null,
                    null,
                    null
            );
            // Đảm bảo tint list của TextView cũng được cập nhật đồng bộ
            TextViewCompat.setCompoundDrawableTintList(
                    holder.btnHelpful,
                    ColorStateList.valueOf(color)
            );
        }

        holder.btnHelpful.setOnClickListener(v -> {
            if (likeListener != null) {
                likeListener.onLikeClick(review);
            }
        });
    }

    private void bindComments(ViewHolder holder, ReviewDto review) {
        if (holder.layoutComments == null) return;

        holder.layoutComments.removeAllViews();
        List<ReviewCommentDto> comments = review.getComments();

        if (comments == null || comments.isEmpty()) {
            holder.layoutComments.setVisibility(View.GONE);
            return;
        }

        holder.layoutComments.setVisibility(View.VISIBLE);
        int maxToShow = Math.min(comments.size(), 3);

        for (int i = 0; i < maxToShow; i++) {
            ReviewCommentDto comment = comments.get(i);
            if (comment == null) continue;

            LinearLayout row = new LinearLayout(holder.itemView.getContext());
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(12, 8, 12, 8);
            row.setBackgroundResource(R.drawable.bg_create_post_input);

            TextView name = new TextView(holder.itemView.getContext());
            name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_main));
            name.setTextSize(13);
            name.setTypeface(name.getTypeface(), android.graphics.Typeface.BOLD);

            String fullName = "Người dùng";
            if (comment.getCustomer() != null
                    && comment.getCustomer().getFullName() != null
                    && !comment.getCustomer().getFullName().trim().isEmpty()) {
                fullName = comment.getCustomer().getFullName();
            }
            name.setText(fullName);

            TextView content = new TextView(holder.itemView.getContext());
            content.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary));
            content.setTextSize(13);
            content.setText(comment.getCommentContent() != null ? comment.getCommentContent() : "");

            row.addView(name);
            row.addView(content);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, i == 0 ? 0 : 8, 0, 0);

            holder.layoutComments.addView(row, params);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvContent, tvDate, tvVerified, btnHelpful, btnReply;
        RatingBar rbStars;
        ImageView ivAvatar;
        RecyclerView rvMedia;
        LinearLayout layoutComments;

        ViewHolder(View view) {
            super(view);
            tvUserName = view.findViewById(R.id.tvReviewUserName);
            tvContent = view.findViewById(R.id.tvReviewContent);
            rbStars = view.findViewById(R.id.rbReviewStars);
            ivAvatar = view.findViewById(R.id.ivReviewAvatar);
            tvDate = view.findViewById(R.id.tvReviewDate);
            tvVerified = view.findViewById(R.id.tvVerifiedPurchase);
            btnHelpful = view.findViewById(R.id.btnHelpful);
            btnReply = view.findViewById(R.id.btnReply);
            rvMedia = view.findViewById(R.id.rvReviewMedia);
            layoutComments = view.findViewById(R.id.layoutReviewComments);

            if (rvMedia != null) {
                rvMedia.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
            }
        }
    }
}
