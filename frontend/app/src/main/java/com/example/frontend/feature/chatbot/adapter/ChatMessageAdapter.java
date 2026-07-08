package com.example.frontend.feature.chatbot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.feature.chatbot.model.ChatMessageUiModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;
    private static final int VIEW_TYPE_TYPING = 3;

    private final List<ChatMessageUiModel> messages = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

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

        BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(ChatMessageUiModel message) {
            tvMessage.setText(message.getContent());
            tvTime.setText(timeFormat.format(new Date(message.getTimestamp())));
        }
    }

    static class TypingViewHolder extends RecyclerView.ViewHolder {
        TypingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
