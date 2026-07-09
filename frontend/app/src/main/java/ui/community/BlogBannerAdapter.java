package ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class BlogBannerAdapter extends RecyclerView.Adapter<BlogBannerAdapter.BannerViewHolder> {

    private List<BlogPost> items = new ArrayList<>();
    private OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(BlogPost blog);
    }

    public void setItems(List<BlogPost> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blog_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivBannerImage;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBannerImage = itemView.findViewById(R.id.ivBannerImage);
        }

        public void bind(BlogPost blog) {
            if (blog.getImageResId() != 0) {
                ivBannerImage.setImageResource(blog.getImageResId());
            } else if (blog.getThumbnailUrl() != null) {
                Glide.with(ivBannerImage.getContext())
                        .load(blog.getThumbnailUrl())
                        .placeholder(R.drawable.bg_slide_2)
                        .into(ivBannerImage);
            } else {
                ivBannerImage.setImageResource(R.drawable.img_blog1);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBannerClick(blog);
                }
            });
        }
    }
}
