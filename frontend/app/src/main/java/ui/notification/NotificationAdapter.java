package ui.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter trộn 4 loại thông báo trong 1 danh sách bằng getItemViewType.
 * Icon + màu icon đã bake sẵn trong từng item_notification_*.xml nên KHÔNG set
 * từ code; chỉ bind title/content/time và visibility của tag "Mới" + chấm chưa đọc.
 */
public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem item);
    }

    private final List<NotificationItem> items = new ArrayList<>();
    private OnNotificationClickListener listener;

    public void setItems(List<NotificationItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType().ordinal();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        NotificationType type = NotificationType.values()[viewType];
        switch (type) {
            case OFFER:
                return new OfferViewHolder(inflater.inflate(R.layout.item_notification_offer, parent, false));
            case COMMUNITY:
                return new CommunityViewHolder(inflater.inflate(R.layout.item_notification_community, parent, false));
            case PERSONAL:
                return new PersonalViewHolder(inflater.inflate(R.layout.item_notification_personal, parent, false));
            case ORDER:
            default:
                return new OrderViewHolder(inflater.inflate(R.layout.item_notification_order, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NotificationItem item = items.get(position);
        ((Binder) holder).bind(item);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** Hợp đồng chung để onBindViewHolder gọi bind mà không cần biết loại cụ thể. */
    private interface Binder {
        void bind(NotificationItem item);
    }

    /** Ẩn/hiện tag "Mới" + chấm chưa đọc theo trạng thái đã đọc. */
    private static void bindReadState(NotificationItem item, View newTag, View unreadDot) {
        int visibility = item.isRead() ? View.GONE : View.VISIBLE;
        newTag.setVisibility(visibility);
        unreadDot.setVisibility(visibility);
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder implements Binder {
        private final TextView tvTitle;
        private final TextView tvContent;
        private final TextView tvTime;
        private final View newTag;
        private final View unreadDot;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotifTitleOrder);
            tvContent = itemView.findViewById(R.id.tvNotifContentOrder);
            tvTime = itemView.findViewById(R.id.tvNotifTimeOrder);
            newTag = itemView.findViewById(R.id.tvNewTagOrder);
            unreadDot = itemView.findViewById(R.id.viewUnreadDotOrder);
        }

        @Override
        public void bind(NotificationItem item) {
            tvTitle.setText(item.getTitle());
            tvContent.setText(item.getContent());
            tvTime.setText(item.getTime());
            bindReadState(item, newTag, unreadDot);
        }
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder implements Binder {
        private final TextView tvTitle;
        private final TextView tvContent;
        private final TextView tvTime;
        private final View newTag;
        private final View unreadDot;

        OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotifTitleOffer);
            tvContent = itemView.findViewById(R.id.tvNotifContentOffer);
            tvTime = itemView.findViewById(R.id.tvNotifTimeOffer);
            newTag = itemView.findViewById(R.id.tvNewTagOffer);
            unreadDot = itemView.findViewById(R.id.viewUnreadDotOffer);
        }

        @Override
        public void bind(NotificationItem item) {
            tvTitle.setText(item.getTitle());
            tvContent.setText(item.getContent());
            tvTime.setText(item.getTime());
            bindReadState(item, newTag, unreadDot);
        }
    }

    static class CommunityViewHolder extends RecyclerView.ViewHolder implements Binder {
        private final TextView tvTitle;
        private final TextView tvContent;
        private final TextView tvTime;
        private final View newTag;
        private final View unreadDot;

        CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotifTitleCommunity);
            tvContent = itemView.findViewById(R.id.tvNotifContentCommunity);
            tvTime = itemView.findViewById(R.id.tvNotifTimeCommunity);
            newTag = itemView.findViewById(R.id.tvNewTagCommunity);
            unreadDot = itemView.findViewById(R.id.viewUnreadDotCommunity);
        }

        @Override
        public void bind(NotificationItem item) {
            tvTitle.setText(item.getTitle());
            tvContent.setText(item.getContent());
            tvTime.setText(item.getTime());
            bindReadState(item, newTag, unreadDot);
        }
    }

    static class PersonalViewHolder extends RecyclerView.ViewHolder implements Binder {
        private final TextView tvTitle;
        private final TextView tvContent;
        private final TextView tvTime;
        private final View newTag;
        private final View unreadDot;

        PersonalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotifTitlePersonal);
            tvContent = itemView.findViewById(R.id.tvNotifContentPersonal);
            tvTime = itemView.findViewById(R.id.tvNotifTimePersonal);
            newTag = itemView.findViewById(R.id.tvNewTagPersonal);
            unreadDot = itemView.findViewById(R.id.viewUnreadDotPersonal);
        }

        @Override
        public void bind(NotificationItem item) {
            tvTitle.setText(item.getTitle());
            tvContent.setText(item.getContent());
            tvTime.setText(item.getTime());
            bindReadState(item, newTag, unreadDot);
        }
    }
}
