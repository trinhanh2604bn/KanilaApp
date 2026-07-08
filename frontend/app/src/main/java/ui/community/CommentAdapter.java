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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments = new ArrayList<>();
    private OnCommentActionListener listener;

    public interface OnCommentActionListener {
        void onReplyClick(Comment comment);
    }

    public void setOnCommentActionListener(OnCommentActionListener listener) {
        this.listener = listener;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public List<Comment> getComments() {
        return comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUserName, tvAuthorBadge, tvContent, tvTime, tvReply, tvLikeCount;
        ImageButton btnLike;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivCommentAvatar);
            tvUserName = itemView.findViewById(R.id.tvCommentUserName);
            tvAuthorBadge = itemView.findViewById(R.id.tvCommentAuthorBadge);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
            tvTime = itemView.findViewById(R.id.tvCommentTime);
            tvReply = itemView.findViewById(R.id.tvCommentReply);
            tvLikeCount = itemView.findViewById(R.id.tvCommentLikeCount);
            btnLike = itemView.findViewById(R.id.btnCommentLike);
        }

        public void bind(Comment comment) {
            // Apply reply indent
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            if (params != null) {
                int indent = comment.isReply() ? dp(36) : 0;
                params.setMarginStart(indent);
                params.setMarginEnd(0);
                itemView.setLayoutParams(params);
            }

            tvUserName.setText(comment.getUserName());
            tvContent.setText(comment.getContent());
            tvTime.setText(comment.getTime());
            tvLikeCount.setText(String.valueOf(comment.getLikeCount()));
            tvAuthorBadge.setVisibility(comment.isAuthor() ? View.VISIBLE : View.GONE);

            if (comment.getUserAvatar() != null) {
                Glide.with(itemView.getContext()).load(comment.getUserAvatar()).into(ivAvatar);
            } else {
                ivAvatar.setImageResource(R.drawable.ic_account);
            }

            btnLike.setImageResource(comment.isLiked() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
            btnLike.setSelected(comment.isLiked());
            tvLikeCount.setText(String.valueOf(comment.getLikeCount()));
            
            tvReply.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReplyClick(comment);
                }
            });
            
            btnLike.setOnClickListener(v -> {
                boolean newLikedState = !comment.isLiked();
                comment.setLiked(newLikedState);
                
                int currentCount = comment.getLikeCount();
                if (newLikedState) {
                    comment.setLikeCount(currentCount + 1);
                } else {
                    comment.setLikeCount(Math.max(0, currentCount - 1));
                }
                
                btnLike.setImageResource(newLikedState ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
                btnLike.setSelected(newLikedState);
                tvLikeCount.setText(String.valueOf(comment.getLikeCount()));
            });
        }

        private int dp(int value) {
            return (int) (value * itemView.getContext().getResources().getDisplayMetrics().density);
        }
    }
}
