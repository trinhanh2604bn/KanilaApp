package com.example.frontend.feature.chatbot;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.feature.chatbot.adapter.ChatMessageAdapter;
import com.example.frontend.feature.chatbot.adapter.ChatProductAdapter;
import com.example.frontend.feature.chatbot.adapter.QuickReplyAdapter;
import com.example.frontend.feature.chatbot.model.ChatOrderUiModel;
import com.example.frontend.feature.chatbot.model.ChatProductUiModel;
import com.example.frontend.feature.chatbot.model.ChatTicketUiModel;
import com.example.frontend.feature.product.ProductDetailFragment;

import java.util.Arrays;
import java.util.List;

public class ChatConversationFragment extends Fragment {

    private static final String ARG_STARTER_MESSAGE = "starter_message";

    private ChatbotViewModel viewModel;
    private ChatMessageAdapter chatAdapter;
    private QuickReplyAdapter quickReplyAdapter;
    private EditText edtMessage;
    private View btnSend;
    private View layoutWelcome;
    private RecyclerView rvChat;

    public static ChatConversationFragment newInstance(String starterMessage) {
        ChatConversationFragment fragment = new ChatConversationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STARTER_MESSAGE, starterMessage);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_conversation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChatbotViewModel.class);

        initViews(view);
        setupRecyclerViews(view);
        setupInputArea();
        observeViewModel();

        if (getArguments() != null) {
            String starterMessage = getArguments().getString(ARG_STARTER_MESSAGE);
            if (starterMessage != null && !starterMessage.isEmpty()) {
                edtMessage.setText(starterMessage);
            }
        }
    }

    private void initViews(View view) {
        rvChat = view.findViewById(R.id.rvChat);
        layoutWelcome = view.findViewById(R.id.layoutWelcome);
        edtMessage = view.findViewById(R.id.edtMessage);
        btnSend = view.findViewById(R.id.btnSend);
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        ImageButton btnMenu = view.findViewById(R.id.btnMenu);

        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        btnMenu.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Menu", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerViews(View view) {
        chatAdapter = new ChatMessageAdapter(
                this::onProductClick,
                this::onOrderClick,
                this::onTicketClick,
                this::onPreferenceClick
        );
        rvChat.setAdapter(chatAdapter);

        RecyclerView rvQuickReplies = view.findViewById(R.id.rvQuickReplies);
        quickReplyAdapter = new QuickReplyAdapter();
        rvQuickReplies.setAdapter(quickReplyAdapter);

        List<String> quickReplies = Arrays.asList(
                getString(R.string.menu_product_consult),
                getString(R.string.menu_create_routine),
                getString(R.string.menu_ingredients_check),
                getString(R.string.menu_track_order),
                getString(R.string.menu_get_support)
        );
        quickReplyAdapter.setItems(quickReplies);

        quickReplyAdapter.setOnItemClickListener(text -> {
            edtMessage.setText(text);
            edtMessage.setSelection(text.length());
        });
    }

    private void setupInputArea() {
        btnSend.setEnabled(false);
        btnSend.setAlpha(0.5f);

        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = s != null && s.toString().trim().length() > 0;
                boolean isLoading = viewModel.getUiState().getValue() != null && viewModel.getUiState().getValue().isLoading();
                btnSend.setEnabled(hasText && !isLoading);
                btnSend.setAlpha(isLoading ? 0.5f : (hasText ? 1.0f : 0.5f));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSend.setOnClickListener(v -> sendMessage());

        edtMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String content = edtMessage.getText().toString().trim();
        if (!content.isEmpty()) {
            viewModel.sendMessage(content);
            edtMessage.setText("");
        }
    }

    private void onProductClick(ChatProductUiModel product) {
        if (product == null || product.getProductId() == null) {
            Toast.makeText(getContext(), R.string.chat_product_detail_next_phase, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, ProductDetailFragment.newInstance(product.getProductId()))
                        .addToBackStack(null)
                        .commit();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.chat_product_detail_next_phase, Toast.LENGTH_SHORT).show();
        }
    }

    private void onOrderClick(ChatOrderUiModel order) {
        // Feature connects in next phase as OrderDetailFragment is not found
        Toast.makeText(getContext(), "Tính năng xem chi tiết đơn hàng sẽ được kết nối ở bước tiếp theo.", Toast.LENGTH_SHORT).show();
    }

    private void onTicketClick(ChatTicketUiModel ticket) {
        // Feature connects in next phase as SupportTicketDetailFragment is not found
        Toast.makeText(getContext(), "Tính năng theo dõi yêu cầu hỗ trợ sẽ được kết nối ở bước tiếp theo.", Toast.LENGTH_SHORT).show();
    }

    private void onPreferenceClick(String option) {
        if (option != null && !option.isEmpty()) {
            viewModel.sendMessage(option);
        }
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            chatAdapter.setMessages(state.getMessages());
            if (!state.getMessages().isEmpty()) {
                rvChat.scrollToPosition(state.getMessages().size() - 1);
            }

            layoutWelcome.setVisibility(state.isWelcomeVisible() ? View.VISIBLE : View.GONE);
            rvChat.setVisibility(state.isWelcomeVisible() ? View.GONE : View.VISIBLE);

            // Update quick replies from state
            if (state.getQuickReplies() != null) {
                quickReplyAdapter.setItems(state.getQuickReplies());
            }

            // Disable input while loading
            btnSend.setEnabled(!state.isLoading() && edtMessage.getText().toString().trim().length() > 0);
            btnSend.setAlpha(state.isLoading() ? 0.5f : (edtMessage.getText().toString().trim().length() > 0 ? 1.0f : 0.5f));
            edtMessage.setEnabled(!state.isLoading());
        });

        viewModel.getLastResponse().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                viewModel.handleBotResponse(result);
            }
        });

        viewModel.getHistoryResponse().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                viewModel.handleHistoryResponse(result);
            }
        });
    }
}
