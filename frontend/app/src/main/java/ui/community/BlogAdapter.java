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
        ImageView ivThumbnail, ivAuthorAvatar, ivVerified, btnSave;
        TextView tvCategory, tvTime, tvTitle, tvExcerpt, tvAuthor;

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
        }

        void bind(BlogPost blog, OnBlogClickListener listener) {
            tvTitle.setText(blog.getTitle());
            tvExcerpt.setText(blog.getExcerpt());
            tvCategory.setText(blog.getCategory().toUpperCase());
            tvTime.setText(blog.getCreatedAt());
            tvAuthor.setText(blog.getAuthorName());
            
            ivVerified.setVisibility(blog.isAuthorVerified() ? View.VISIBLE : View.GONE);
            
            if (blog.getThumbnailUrl() != null) {
                Glide.with(itemView.getContext()).load(blog.getThumbnailUrl()).placeholder(R.drawable.bg_slide_3).into(ivThumbnail);
            } else {
                ivThumbnail.setImageResource(R.drawable.bg_slide_3);
            }

            btnSave.setImageResource(blog.isSaved() ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_outline);
            btnSave.setOnClickListener(v -> listener.onSaveClick(blog));
            itemView.setOnClickListener(v -> listener.onBlogClick(blog));
        }
    }
}
