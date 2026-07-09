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

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.ViewHolder> {
    private List<RewardItem> rewards = new ArrayList<>();

    public void setRewards(List<RewardItem> rewards) {
        this.rewards = rewards;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RewardItem reward = rewards.get(position);
        holder.tvTitle.setText(reward.getTitle());
        holder.tvPoints.setText(holder.itemView.getContext().getString(R.string.challenge_reward_points, "" + reward.getPointCost()));
        
        if (reward.getImageUrl() != null) {
            Glide.with(holder.itemView.getContext()).load(reward.getImageUrl()).placeholder(R.drawable.ic_gift).into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_gift);
        }
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvPoints;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivRewardImage);
            tvTitle = itemView.findViewById(R.id.tvRewardTitle);
            tvPoints = itemView.findViewById(R.id.tvRewardPoints);
        }
    }
}
