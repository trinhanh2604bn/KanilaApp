package ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
        private final com.example.frontend.feature.account.AccountViewModel accountViewModel;
        ImageView ivAvatar, ivVerified, ivImage1, ivImage2, ivImage3;
        TextView tvUserName, tvTime, tvTitle, tvContent, tvLikeCount, tvCommentCount, tvShareCount, tvVerifiedPurchase, tvSkinType, tvPostType;
        View layoutVerifiedPurchase, layoutImages, hsvProducts;
        ViewGroup layoutProducts;
        ImageButton btnSave, btnMore;
        View layoutLike, layoutComment, layoutShare;
        ImageView ivLikeIcon;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            accountViewModel = new androidx.lifecycle.ViewModelProvider((androidx.activity.ComponentActivity) itemView.getContext()).get(com.example.frontend.feature.account.AccountViewModel.class);
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
            
            tvPostType = itemView.findViewById(R.id.tvPostType);
            tvSkinType = itemView.findViewById(R.id.tvPostSkinType);
            hsvProducts = itemView.findViewById(R.id.hsvPostProducts);
            layoutProducts = itemView.findViewById(R.id.layoutPostProducts);
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

            // Sync avatar with real account if it's the current user's post
            String userAvatar = post.getUserAvatar();
            com.example.frontend.data.remote.NetworkResult<com.example.frontend.data.model.account.ProfileHubDto> userResult = accountViewModel.getProfileHubResult().getValue();
            if (userResult != null && userResult.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS && userResult.data != null) {
                if (userResult.data.getProfile() != null && userResult.data.getProfile().getFullName().equals(post.getUserName())) {
                    userAvatar = userResult.data.getProfile().getAvatarUrl();
                }
            }

            if (userAvatar != null) {
                Glide.with(itemView.getContext())
                        .load(userAvatar)
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

            // Post type badge with different colors
            String type = post.getTitle(); // In this mock, title is used for post type
            if (type != null) {
                tvPostType.setVisibility(View.VISIBLE);
                tvPostType.setText(type.toUpperCase());
                
                int bgColor, textColor;
                switch (type) {
                    case "Review":
                        bgColor = ContextCompat.getColor(itemView.getContext(), R.color.post_type_review_bg);
                        textColor = ContextCompat.getColor(itemView.getContext(), R.color.post_type_review_text);
                        break;
                    case "Routine":
                        bgColor = ContextCompat.getColor(itemView.getContext(), R.color.post_type_routine_bg);
                        textColor = ContextCompat.getColor(itemView.getContext(), R.color.post_type_routine_text);
                        break;
                    case "Before/After":
                        bgColor = ContextCompat.getColor(itemView.getContext(), R.color.post_type_before_after_bg);
                        textColor = ContextCompat.getColor(itemView.getContext(), R.color.post_type_before_after_text);
                        break;
                    case "Hỏi đáp":
                        bgColor = ContextCompat.getColor(itemView.getContext(), R.color.post_type_qa_bg);
                        textColor = ContextCompat.getColor(itemView.getContext(), R.color.post_type_qa_text);
                        break;
                    default:
                        bgColor = ContextCompat.getColor(itemView.getContext(), R.color.border_divider);
                        textColor = ContextCompat.getColor(itemView.getContext(), R.color.text_tertiary);
                        break;
                }
                tvPostType.getBackground().setTint(bgColor);
                tvPostType.setTextColor(textColor);
            } else {
                tvPostType.setVisibility(View.GONE);
            }

            // Skin type display
            if (post.getSkinType() != null && !post.getSkinType().isEmpty()) {
                tvSkinType.setVisibility(View.VISIBLE);
                tvSkinType.setText(post.getSkinType());
            } else {
                tvSkinType.setVisibility(View.GONE);
            }

            // Products used display
            List<com.example.frontend.model.Product> products = post.getProducts();
            if (products != null && !products.isEmpty()) {
                hsvProducts.setVisibility(View.VISIBLE);
                layoutProducts.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
                for (com.example.frontend.model.Product product : products) {
                    View productTag = inflater.inflate(R.layout.item_post_product_tag, layoutProducts, false);
                    ImageView ivProd = productTag.findViewById(R.id.ivProductImage);
                    TextView tvProd = productTag.findViewById(R.id.tvProductName);
                    
                    tvProd.setText(product.getName());
                    Glide.with(itemView.getContext())
                            .load(product.getImageUrl())
                            .placeholder(R.drawable.ic_product)
                            .into(ivProd);
                            
                    productTag.setOnClickListener(v -> {
                        // Navigate to product detail (re-using MainActivity fragment loading if available)
                        if (itemView.getContext() instanceof com.example.frontend.MainActivity) {
                            ((com.example.frontend.MainActivity) itemView.getContext())
                                    .loadFragment(com.example.frontend.feature.product.ProductDetailFragment.newInstance(product.getId()));
                        }
                    });
                    
                    layoutProducts.addView(productTag);
                }
            } else {
                hsvProducts.setVisibility(View.GONE);
            }
            
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

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPostClick(post);
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
