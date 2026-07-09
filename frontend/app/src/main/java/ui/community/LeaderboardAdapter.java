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

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private List<LeaderboardUser> users = new ArrayList<>();

    public void setUsers(List<LeaderboardUser> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardUser user = users.get(position);
        holder.tvRank.setText(String.valueOf(user.getRank()));
        holder.tvName.setText(user.getName());
        holder.tvPoints.setText(holder.itemView.getContext().getString(R.string.leaderboard_points_suffix, user.getPoints()));
        
        if (user.getAvatarUrl() != null) {
            Glide.with(holder.itemView.getContext()).load(user.getAvatarUrl()).placeholder(R.drawable.ic_account).into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(R.drawable.ic_account);
        }

        // Reset rank text style
        holder.tvRank.setBackground(null);
        holder.tvRank.setTextColor(holder.itemView.getContext().getColor(R.color.text_main));
        holder.tvRank.setPadding(0, 0, 0, 0);

        // Highlight top 3
        if (user.getRank() == 1) {
            holder.tvRank.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_crown, 0, 0, 0);
            holder.tvRank.setText("");
            holder.tvPoints.setTextColor(holder.itemView.getContext().getColor(R.color.icon_pink));
        } else if (user.getRank() == 2) {
            holder.tvRank.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.tvRank.setText("2");
            holder.tvRank.setBackgroundResource(R.drawable.bg_circle_dark);
            holder.tvRank.setTextColor(holder.itemView.getContext().getColor(R.color.white));
            holder.tvPoints.setTextColor(holder.itemView.getContext().getColor(R.color.icon_pink));
        } else if (user.getRank() == 3) {
            holder.tvRank.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.tvRank.setText("3");
            holder.tvRank.setBackgroundResource(R.drawable.bg_circle_pink);
            holder.tvRank.setTextColor(holder.itemView.getContext().getColor(R.color.white));
            holder.tvPoints.setTextColor(holder.itemView.getContext().getColor(R.color.icon_pink));
        } else {
            holder.tvRank.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.tvRank.setText(String.valueOf(user.getRank()));
            holder.tvPoints.setTextColor(holder.itemView.getContext().getColor(R.color.text_secondary));
        }

        if (user.isCurrentUser()) {
            holder.itemView.setBackgroundResource(R.drawable.bg_selection_selected);
        } else {
            holder.itemView.setBackground(null);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvPoints;
        ImageView ivAvatar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }
    }
}
