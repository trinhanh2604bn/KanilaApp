package ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class ChallengeParticipantAdapter extends RecyclerView.Adapter<ChallengeParticipantAdapter.ViewHolder> {

    private List<ChallengeParticipant> participants = new ArrayList<>();
    private OnParticipantClickListener listener;

    public interface OnParticipantClickListener {
        void onParticipantClick(ChallengeParticipant participant);
    }

    public ChallengeParticipantAdapter(OnParticipantClickListener listener) {
        this.listener = listener;
    }

    public void setParticipants(List<ChallengeParticipant> participants) {
        this.participants = participants;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge_participant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChallengeParticipant participant = participants.get(position);
        holder.bind(participant, listener);
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName, tvCount, tvMorePosts;
        LinearLayout layoutPreviews;

        ViewHolder(View view) {
            super(view);
            ivAvatar = view.findViewById(R.id.ivParticipantAvatar);
            tvName = view.findViewById(R.id.tvParticipantName);
            tvCount = view.findViewById(R.id.tvProgressCount);
            tvMorePosts = view.findViewById(R.id.tvMorePosts);
            layoutPreviews = view.findViewById(R.id.layoutPreviews);
        }

        void bind(ChallengeParticipant participant, OnParticipantClickListener listener) {
            tvName.setText(participant.getUserName());
            
            // Mock total days as 7 for display
            tvCount.setText(itemView.getContext().getString(R.string.challenge_progress_summary_full, 
                    String.valueOf(participant.getProgressCount()), "7"));

            Glide.with(itemView.getContext())
                    .load(participant.getUserAvatar())
                    .placeholder(R.drawable.ic_account)
                    .error(R.drawable.ic_account)
                    .into(ivAvatar);

            // Bind previews (latest 2)
            layoutPreviews.removeAllViews();
            List<Post> posts = participant.getProgressPosts();
            if (posts != null && !posts.isEmpty()) {
                int previewCount = Math.min(posts.size(), 2);
                for (int i = 0; i < previewCount; i++) {
                    Post post = posts.get(posts.size() - 1 - i); // Latest first
                    View previewView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_participant_progress_preview_simple, layoutPreviews, false);
                    
                    TextView tvDay = previewView.findViewById(R.id.tvDayLabel);
                    TextView tvTime = previewView.findViewById(R.id.tvTime);
                    TextView tvContent = previewView.findViewById(R.id.tvContentPreview);
                    ImageView ivContent = previewView.findViewById(R.id.ivContentImage);

                    tvDay.setText(itemView.getContext().getString(R.string.challenge_day_label_format, posts.size() - i));
                    tvTime.setText(post.getTime());
                    tvContent.setText(post.getContent());

                    if (post.getImages() != null && !post.getImages().isEmpty()) {
                        ivContent.setVisibility(View.VISIBLE);
                        Glide.with(itemView.getContext()).load(post.getImages().get(0)).into(ivContent);
                    } else {
                        ivContent.setVisibility(View.GONE);
                    }
                    
                    layoutPreviews.addView(previewView);
                }

                if (posts.size() > 2) {
                    tvMorePosts.setVisibility(View.VISIBLE);
                    tvMorePosts.setText(itemView.getContext().getString(R.string.challenge_more_posts_format, posts.size() - 2));
                } else {
                    tvMorePosts.setVisibility(View.GONE);
                }
                layoutPreviews.setVisibility(View.VISIBLE);
            } else {
                layoutPreviews.setVisibility(View.GONE);
                tvMorePosts.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onParticipantClick(participant);
            });
        }
    }
}
