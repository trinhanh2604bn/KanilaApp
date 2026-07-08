package ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class CommunityNotificationAdapter extends RecyclerView.Adapter<CommunityNotificationAdapter.ViewHolder> {

    private final List<CommunityNotification> notifications = new ArrayList<>();
    private final OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(CommunityNotification notification);
    }

    public CommunityNotificationAdapter(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    public void setNotifications(List<CommunityNotification> newList) {
        notifications.clear();
        notifications.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIcon;
        private final TextView tvTitle;
        private final TextView tvContent;
        private final TextView tvTime;
        private final View viewUnread;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
            viewUnread = itemView.findViewById(R.id.viewUnread);
        }

        public void bind(CommunityNotification notification) {
            tvTitle.setText(notification.getTitle());
            tvContent.setText(notification.getContent());
            tvTime.setText(notification.getCreatedAt());

            switch (notification.getType()) {
                case "REWARD":
                    ivIcon.setImageResource(R.drawable.ic_gift);
                    ivIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.button));
                    break;
                case "CHALLENGE_EXPIRING":
                case "CHALLENGE_REMINDER":
                    ivIcon.setImageResource(R.drawable.ic_time);
                    ivIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.button));
                    break;
                case "LIKE":
                    ivIcon.setImageResource(R.drawable.ic_heart_filled);
                    ivIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.button));
                    break;
                case "COMMENT":
                    ivIcon.setImageResource(R.drawable.ic_comment);
                    ivIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.button));
                    break;
                default:
                    ivIcon.setImageResource(R.drawable.ic_notification);
                    ivIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.text_main));
                    break;
            }

            viewUnread.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);
            itemView.setAlpha(notification.isRead() ? 0.7f : 1.0f);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationClick(notification);
                }
            });
        }
    }
}
