package ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts = new ArrayList<>();
    private OnPostClickListener listener;

    public interface OnPostClickListener {
        void onPostClick(Post post);
        void onSaveClick(Post post);
        void onMoreClick(Post post, View view);
        void onLikeClick(Post post);
        void onCommentClick(Post post);
        void onShareClick(Post post);
    }

    public void setOnPostClickListener(OnPostClickListener listener) {
        this.listener = listener;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    public List<Post> getPosts() {
        return posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivVerified, ivImage1, ivImage2, ivImage3;
        TextView tvUserName, tvTime, tvTitle, tvContent, tvLikeCount, tvCommentCount, tvShareCount, tvVerifiedPurchase;
        View layoutVerifiedPurchase, layoutImages;
        ImageButton btnSave, btnMore;
        View layoutLike, layoutComment, layoutShare;
        ImageView ivLikeIcon;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivPostAvatar);
            ivVerified = itemView.findViewById(R.id.ivPostVerified);
            tvUserName = itemView.findViewById(R.id.tvPostUserName);
            tvTime = itemView.findViewById(R.id.tvPostTime);
            tvTitle = itemView.findViewById(R.id.tvPostTitle);
            tvContent = itemView.findViewById(R.id.tvPostContent);
            layoutVerifiedPurchase = itemView.findViewById(R.id.layoutVerifiedPurchase);
            tvVerifiedPurchase = itemView.findViewById(R.id.tvVerifiedPurchase);
            ivImage1 = itemView.findViewById(R.id.ivPostImage1);
            ivImage2 = itemView.findViewById(R.id.ivPostImage2);
            ivImage3 = itemView.findViewById(R.id.ivPostImage3);
            layoutImages = itemView.findViewById(R.id.layoutPostImages);
            tvLikeCount = itemView.findViewById(R.id.tvPostLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvPostCommentCount);
            tvShareCount = itemView.findViewById(R.id.tvPostShareCount);
            btnSave = itemView.findViewById(R.id.btnPostSave);
            btnMore = itemView.findViewById(R.id.btnPostMore);
            layoutLike = itemView.findViewById(R.id.layoutPostLike);
            layoutComment = itemView.findViewById(R.id.layoutPostComment);
            layoutShare = itemView.findViewById(R.id.layoutPostShare);
            ivLikeIcon = itemView.findViewById(R.id.ivPostLikeIcon);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onPostClick(posts.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Post post) {
            tvUserName.setText(post.getUserName());
            tvTime.setText(post.getTime());
            tvTitle.setText(post.getTitle());
            tvContent.setText(post.getContent());
            tvLikeCount.setText(String.valueOf(post.getLikeCount()));
            tvCommentCount.setText(String.valueOf(post.getCommentCount()));
            tvShareCount.setText(String.valueOf(post.getShareCount()));

            ivVerified.setVisibility(post.isVerified() ? View.VISIBLE : View.GONE);
            layoutVerifiedPurchase.setVisibility(post.isPurchased() ? View.VISIBLE : View.GONE);

            if (post.getUserAvatar() != null) {
                Glide.with(itemView.getContext())
                        .load(post.getUserAvatar())
                        .placeholder(R.drawable.ic_account)
                        .error(R.drawable.ic_account)
                        .circleCrop()
                        .override(120, 120)
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageResource(R.drawable.ic_account);
            }

            List<String> images = post.getImages();
            if (images != null && !images.isEmpty()) {
                layoutImages.setVisibility(View.VISIBLE);
                ImageView[] ivs = {ivImage1, ivImage2, ivImage3};
                for (int i = 0; i < ivs.length; i++) {
                    if (i < images.size()) {
                        ivs[i].setVisibility(View.VISIBLE);
                        Glide.with(itemView.getContext())
                                .load(images.get(i))
                                .thumbnail(0.1f)
                                .placeholder(R.drawable.bg_skeleton_placeholder)
                                .centerCrop()
                                .override(400, 400)
                                .into(ivs[i]);
                    } else {
                        ivs[i].setVisibility(View.GONE);
                        Glide.with(itemView.getContext()).clear(ivs[i]);
                    }
                }
            } else {
                layoutImages.setVisibility(View.GONE);
            }

            ivLikeIcon.setImageResource(post.isLiked() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
            layoutLike.setSelected(post.isLiked());
            tvLikeCount.setText(String.valueOf(post.getLikeCount()));
            
            btnSave.setImageResource(post.isSaved() ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_outline);
            btnSave.setSelected(post.isSaved());
            
            layoutShare.setSelected(post.isShared());
            tvShareCount.setText(String.valueOf(post.getShareCount()));
            
            layoutLike.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLikeClick(post);
                }
            });

            layoutComment.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCommentClick(post);
                }
            });

            layoutShare.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onShareClick(post);
                }
            });

            btnSave.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSaveClick(post);
                }
            });

            btnMore.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMoreClick(post, v);
                }
            });
        }
    }
}
