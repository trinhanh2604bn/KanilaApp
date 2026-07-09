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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.ArrayList;
import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {
    private List<Challenge> challenges = new ArrayList<>();
    private final OnChallengeClickListener listener;

    public interface OnChallengeClickListener {
        void onChallengeClick(Challenge challenge);
        void onActionClick(Challenge challenge);
    }

    public ChallengeAdapter(OnChallengeClickListener listener) {
        this.listener = listener;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Challenge challenge = challenges.get(position);
        holder.bind(challenge, listener);
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner;
        TextView tvTitle, tvParticipants, tvRewardPoints, tvProgressText, tvBadge;
        LinearProgressIndicator progressIndicator;
        MaterialButton btnAction;
        View layoutProgress;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivChallengeBanner);
            tvTitle = itemView.findViewById(R.id.tvChallengeTitle);
            tvParticipants = itemView.findViewById(R.id.tvParticipants);
            tvRewardPoints = itemView.findViewById(R.id.tvRewardPoints);
            tvProgressText = itemView.findViewById(R.id.tvProgressText);
            tvBadge = itemView.findViewById(R.id.tvChallengeBadge);
            progressIndicator = itemView.findViewById(R.id.progressIndicator);
            btnAction = itemView.findViewById(R.id.btnAction);
            layoutProgress = itemView.findViewById(R.id.layoutProgress);
        }

        void bind(Challenge challenge, OnChallengeClickListener listener) {
            tvTitle.setText(challenge.getTitle());
            tvParticipants.setText(itemView.getContext().getString(R.string.challenge_participants, String.valueOf(challenge.getParticipantCount())));
            tvRewardPoints.setText(itemView.getContext().getString(R.string.challenge_reward_points, String.valueOf(challenge.getRewardPoints())));
            
            if (challenge.getBannerUrl() != null) {
                Glide.with(itemView.getContext()).load(challenge.getBannerUrl()).placeholder(R.drawable.bg_slide_1).into(ivBanner);
            } else {
                ivBanner.setImageResource(R.drawable.bg_slide_1);
            }

            if (challenge.isJoined()) {
                layoutProgress.setVisibility(View.VISIBLE);
                tvProgressText.setText(challenge.getCurrentProgress() + "/" + challenge.getDurationDays() + " ngày");
                progressIndicator.setProgress((int) ((challenge.getCurrentProgress() / (float) challenge.getDurationDays()) * 100));
                btnAction.setText(R.string.challenge_action_continue);
            } else {
                layoutProgress.setVisibility(View.GONE);
                btnAction.setText(R.string.challenge_action_join);
            }

            if (challenge.isHot()) {
                tvBadge.setVisibility(View.VISIBLE);
                tvBadge.setText(R.string.home_social_challenge_hot);
                tvBadge.setBackgroundResource(R.drawable.bg_badge_hot);
            } else if (challenge.isNew()) {
                tvBadge.setVisibility(View.VISIBLE);
                tvBadge.setText(R.string.category_new);
                tvBadge.setBackgroundResource(R.drawable.bg_badge_hot); // reuse for now
            } else {
                tvBadge.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> listener.onChallengeClick(challenge));
            btnAction.setOnClickListener(v -> listener.onActionClick(challenge));
        }
    }
}
