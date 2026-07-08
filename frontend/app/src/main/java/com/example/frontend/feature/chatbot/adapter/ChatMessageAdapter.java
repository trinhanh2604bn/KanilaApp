package com.example.frontend.feature.chatbot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.feature.chatbot.model.ChatMessageUiModel;
import com.example.frontend.feature.chatbot.model.ChatOrderTimelineUiModel;
import com.example.frontend.feature.chatbot.model.ChatOrderUiModel;
import com.example.frontend.feature.chatbot.model.ChatProductUiModel;
import com.example.frontend.feature.chatbot.model.ChatTicketUiModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;
    private static final int VIEW_TYPE_TYPING = 3;

    public interface OnOrderClickListener {
        void onOrderClick(ChatOrderUiModel order);
    }

    public interface OnTicketClickListener {
        void onTicketClick(ChatTicketUiModel ticket);
    }

    private final List<ChatMessageUiModel> messages = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final ChatProductAdapter.OnProductClickListener productClickListener;
    private final OnOrderClickListener orderClickListener;
    private final OnTicketClickListener ticketClickListener;

    public ChatMessageAdapter(ChatProductAdapter.OnProductClickListener productClickListener,
                             OnOrderClickListener orderClickListener,
                             OnTicketClickListener ticketClickListener) {
        this.productClickListener = productClickListener;
        this.orderClickListener = orderClickListener;
        this.ticketClickListener = ticketClickListener;
    }

    public void setMessages(List<ChatMessageUiModel> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageUiModel message = messages.get(position);
        if (message.isTyping()) return VIEW_TYPE_TYPING;
        return message.isUser() ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_USER) {
            return new UserMessageViewHolder(inflater.inflate(R.layout.item_chat_user, parent, false));
        } else if (viewType == VIEW_TYPE_TYPING) {
            return new TypingViewHolder(inflater.inflate(R.layout.item_chat_typing, parent, false));
        } else {
            return new BotMessageViewHolder(inflater.inflate(R.layout.item_chat_bot, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessageUiModel message = messages.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof BotMessageViewHolder) {
            ((BotMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;

        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(ChatMessageUiModel message) {
            tvMessage.setText(message.getContent());
            tvTime.setText(timeFormat.format(new Date(message.getTimestamp())));
        }
    }

    class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        RecyclerView rvProducts;
        ChatProductAdapter productAdapter;
        
        // Order card views
        View layoutOrderCard;
        TextView tvOrderCode, tvOrderStatus, tvPaymentStatus, tvTotalAmount, tvOrderDate, tvEstimatedDelivery, tvItemsCount;
        View rowEstimatedDelivery;
        ViewGroup layoutTimeline;
        View btnViewOrderDetail;

        // Ticket card views
        View layoutTicketCard;
        TextView tvTicketCode, tvTicketStatus, tvTicketCategory, tvTicketCreatedAt, tvTicketMessage;
        View btnViewTicketDetail;

        BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            rvProducts = itemView.findViewById(R.id.rvProducts);
            
            productAdapter = new ChatProductAdapter(productClickListener);
            rvProducts.setAdapter(productAdapter);

            // Init Order views
            layoutOrderCard = itemView.findViewById(R.id.layoutOrderCardContainer);
            if (layoutOrderCard != null) {
                tvOrderCode = layoutOrderCard.findViewById(R.id.tvOrderCode);
                tvOrderStatus = layoutOrderCard.findViewById(R.id.tvOrderStatus);
                tvPaymentStatus = layoutOrderCard.findViewById(R.id.tvPaymentStatus);
                tvTotalAmount = layoutOrderCard.findViewById(R.id.tvTotalAmount);
                tvOrderDate = layoutOrderCard.findViewById(R.id.tvOrderDate);
                tvEstimatedDelivery = layoutOrderCard.findViewById(R.id.tvEstimatedDelivery);
                tvItemsCount = layoutOrderCard.findViewById(R.id.tvItemsCount);
                rowEstimatedDelivery = layoutOrderCard.findViewById(R.id.rowEstimatedDelivery);
                layoutTimeline = layoutOrderCard.findViewById(R.id.layoutTimeline);
                btnViewOrderDetail = layoutOrderCard.findViewById(R.id.btnViewOrderDetail);
            }

            // Init Ticket views
            layoutTicketCard = itemView.findViewById(R.id.layoutTicketCardContainer);
            if (layoutTicketCard != null) {
                tvTicketCode = layoutTicketCard.findViewById(R.id.tvTicketCode);
                tvTicketStatus = layoutTicketCard.findViewById(R.id.tvTicketStatus);
                tvTicketCategory = layoutTicketCard.findViewById(R.id.tvTicketCategory);
                tvTicketCreatedAt = layoutTicketCard.findViewById(R.id.tvTicketCreatedAt);
                tvTicketMessage = layoutTicketCard.findViewById(R.id.tvTicketMessage);
                btnViewTicketDetail = layoutTicketCard.findViewById(R.id.btnViewTicketDetail);
            }
        }

        void bind(ChatMessageUiModel message) {
            tvMessage.setText(message.getContent());
            tvTime.setText(timeFormat.format(new Date(message.getTimestamp())));

            // Products
            if (message.getProducts() != null && !message.getProducts().isEmpty()) {
                productAdapter.setProducts(message.getProducts());
                rvProducts.setVisibility(View.VISIBLE);
            } else {
                rvProducts.setVisibility(View.GONE);
            }

            // Order
            if (message.getOrder() != null && layoutOrderCard != null) {
                bindOrderCard(message.getOrder());
                layoutOrderCard.setVisibility(View.VISIBLE);
            } else if (layoutOrderCard != null) {
                layoutOrderCard.setVisibility(View.GONE);
            }

            // Ticket
            if (message.getTicket() != null && layoutTicketCard != null) {
                bindTicketCard(message.getTicket());
                layoutTicketCard.setVisibility(View.VISIBLE);
            } else if (layoutTicketCard != null) {
                layoutTicketCard.setVisibility(View.GONE);
            }
        }

        private void bindOrderCard(ChatOrderUiModel order) {
            tvOrderCode.setText(order.getOrderCode());
            tvOrderStatus.setText(order.getStatusLabel());
            tvPaymentStatus.setText(order.getPaymentStatusLabel());
            tvTotalAmount.setText(String.format(Locale.US, "%,dđ", order.getTotalAmount()).replace(",", "."));
            tvOrderDate.setText(order.getCreatedAt());
            tvItemsCount.setText(String.valueOf(order.getItemsCount()));

            if (order.getEstimatedDelivery() != null && !order.getEstimatedDelivery().isEmpty()) {
                tvEstimatedDelivery.setText(order.getEstimatedDelivery());
                rowEstimatedDelivery.setVisibility(View.VISIBLE);
            } else {
                rowEstimatedDelivery.setVisibility(View.GONE);
            }

            // Timeline
            layoutTimeline.removeAllViews();
            if (order.getTimeline() != null) {
                for (int i = 0; i < order.getTimeline().size(); i++) {
                    ChatOrderTimelineUiModel t = order.getTimeline().get(i);
                    View timelineView = LayoutInflater.from(itemView.getContext())
                            .inflate(R.layout.item_chat_order_timeline, layoutTimeline, false);
                    
                    TextView tvLabel = timelineView.findViewById(R.id.tvTimelineLabel);
                    TextView tvTime = timelineView.findViewById(R.id.tvTimelineTime);
                    TextView tvDesc = timelineView.findViewById(R.id.tvTimelineDescription);
                    View indicator = timelineView.findViewById(R.id.viewStatusIndicator);
                    View line = timelineView.findViewById(R.id.viewLine);

                    tvLabel.setText(t.getLabel());
                    tvTime.setText(t.getTime());
                    tvDesc.setText(t.getDescription());

                    // Highlight latest (usually the first in the list if sorted desc, or last if sorted asc)
                    // Assuming last in list is latest for now or checking index 0. 
                    // Requirements say "highlight current/latest status".
                    if (i == 0) {
                        indicator.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                                ContextCompat.getColor(itemView.getContext(), R.color.button)));
                        tvLabel.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.button));
                    }

                    if (i == order.getTimeline().size() - 1) {
                        line.setVisibility(View.GONE);
                    }

                    layoutTimeline.addView(timelineView);
                }
            }

            btnViewOrderDetail.setOnClickListener(v -> {
                if (orderClickListener != null) orderClickListener.onOrderClick(order);
            });
        }

        private void bindTicketCard(ChatTicketUiModel ticket) {
            tvTicketCode.setText(ticket.getTicketCode());
            tvTicketStatus.setText(ticket.getStatusLabel());
            tvTicketCategory.setText(ticket.getCategoryLabel());
            tvTicketCreatedAt.setText(ticket.getCreatedAt());
            tvTicketMessage.setText(ticket.getMessage());

            btnViewTicketDetail.setOnClickListener(v -> {
                if (ticketClickListener != null) ticketClickListener.onTicketClick(ticket);
            });
        }
    }

    static class TypingViewHolder extends RecyclerView.ViewHolder {
        TypingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
