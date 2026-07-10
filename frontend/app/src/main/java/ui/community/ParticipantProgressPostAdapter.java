package ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.model.Product;
import java.util.ArrayList;
import java.util.List;

public class ParticipantProgressPostAdapter extends RecyclerView.Adapter<ParticipantProgressPostAdapter.ViewHolder> {

    private List<Post> posts = new ArrayList<>();

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participant_progress_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post, position);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayLabel, tvDate, tvContent;
        RecyclerView rvMedia;
        ViewGroup layoutProducts;

        ViewHolder(View view) {
            super(view);
            tvDayLabel = view.findViewById(R.id.tvDayLabel);
            tvDate = view.findViewById(R.id.tvPostDate);
            tvContent = view.findViewById(R.id.tvPostContent);
            rvMedia = view.findViewById(R.id.rvPostMedia);
            layoutProducts = view.findViewById(R.id.layoutProducts);
        }

        void bind(Post post, int position) {
            tvDayLabel.setText(itemView.getContext().getString(R.string.challenge_day_label_format, position + 1));
            tvDate.setText(post.getTime());
            tvContent.setText(post.getContent());

            if (post.getImages() != null && !post.getImages().isEmpty()) {
                rvMedia.setVisibility(View.VISIBLE);
                ProductThumbnailAdapter mediaAdapter = new ProductThumbnailAdapter();
                rvMedia.setAdapter(mediaAdapter);
                mediaAdapter.setImageUrls(post.getImages());
            } else {
                rvMedia.setVisibility(View.GONE);
            }

            layoutProducts.removeAllViews();
            if (post.getProducts() != null && !post.getProducts().isEmpty()) {
                for (Product product : post.getProducts()) {
                    View tagView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_post_product_tag, layoutProducts, false);
                    TextView tvName = tagView.findViewById(R.id.tvProductName);
                    tvName.setText(product.getName());
                    layoutProducts.addView(tagView);
                }
            }
        }
    }
}
