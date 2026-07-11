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
import java.util.ArrayList;
import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {
    private List<BlogPost> blogs = new ArrayList<>();
    private final OnBlogClickListener listener;

    public interface OnBlogClickListener {
        void onBlogClick(BlogPost blog);
        void onSaveClick(BlogPost blog);
        void onLikeClick(BlogPost blog);
        void onShareClick(BlogPost blog);
    }

    public BlogAdapter(OnBlogClickListener listener) {
        this.listener = listener;
    }

    public void setBlogs(List<BlogPost> blogs) {
        this.blogs = blogs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blog, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BlogPost blog = blogs.get(position);
        holder.bind(blog, listener);
    }

    @Override
    public int getItemCount() {
        return blogs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail, ivAuthorAvatar, ivVerified, btnSave, ivLike, ivShare;
        TextView tvCategory, tvTime, tvTitle, tvExcerpt, tvAuthor;
        TextView tvMeta, tvLikeCount, tvCommentCount, tvShareCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivBlogThumbnail);
            tvCategory = itemView.findViewById(R.id.tvBlogCategory);
            tvTime = itemView.findViewById(R.id.tvBlogTime);
            tvTitle = itemView.findViewById(R.id.tvBlogTitle);
            tvExcerpt = itemView.findViewById(R.id.tvBlogExcerpt);
            tvAuthor = itemView.findViewById(R.id.tvBlogAuthor);
            ivVerified = itemView.findViewById(R.id.ivVerified);
            btnSave = itemView.findViewById(R.id.btnSave);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivShare = itemView.findViewById(R.id.ivShare);
            tvMeta = itemView.findViewById(R.id.tvBlogMeta);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            tvShareCount = itemView.findViewById(R.id.tvShareCount);
        }

        void bind(BlogPost blog, OnBlogClickListener listener) {
            tvTitle.setText(blog.getTitle());
            tvExcerpt.setText(blog.getExcerpt());
            // tvCategory and tvTime are gone but kept for safety in XML
            
            if (tvMeta != null) {
                tvMeta.setText(String.format("%s • %s", blog.getCreatedAt(), blog.getCategory()));
            }
            
            tvAuthor.setText(blog.getAuthorName());
            
            if (tvLikeCount != null) tvLikeCount.setText(formatCount(blog.getLikeCount()));
            if (tvCommentCount != null) tvCommentCount.setText(formatCount(blog.getCommentCount()));
            if (tvShareCount != null) tvShareCount.setText(formatCount(blog.getShareCount()));

            ivVerified.setVisibility(blog.isAuthorVerified() ? View.VISIBLE : View.GONE);
            
            if (blog.getImageResId() != 0) {
                ivThumbnail.setImageResource(blog.getImageResId());
            } else if (blog.getThumbnailUrl() != null) {
                Glide.with(itemView.getContext()).load(blog.getThumbnailUrl()).placeholder(R.drawable.bg_slide_3).into(ivThumbnail);
            } else {
                ivThumbnail.setImageResource(R.drawable.bg_slide_3);
            }

            // Render Like state
            if (ivLike != null) {
                ivLike.setImageResource(blog.isLiked() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
                ivLike.setColorFilter(itemView.getContext().getColor(blog.isLiked() ? R.color.button : R.color.accent_dark));
                ivLike.setOnClickListener(v -> listener.onLikeClick(blog));
            }

            // Render Save state
            if (btnSave != null) {
                btnSave.setImageResource(blog.isSaved() ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_outline);
                btnSave.setColorFilter(itemView.getContext().getColor(blog.isSaved() ? R.color.button : R.color.accent_dark));
                btnSave.setOnClickListener(v -> listener.onSaveClick(blog));
            }

            // Share listener
            if (ivShare != null) {
                ivShare.setOnClickListener(v -> listener.onShareClick(blog));
            }

            itemView.setOnClickListener(v -> listener.onBlogClick(blog));
        }

        private String formatCount(int count) {
            if (count >= 1000) {
                return String.format(java.util.Locale.US, "%.1fK", count / 1000.0);
            }
            return String.valueOf(count);
        }
    }
}
