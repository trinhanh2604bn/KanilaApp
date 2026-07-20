package com.example.frontend.feature.chatbot;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.feature.chatbot.adapter.ChatMessageAdapter;
import com.example.frontend.feature.chatbot.adapter.QuickReplyAdapter;
import com.example.frontend.feature.chatbot.model.ChatMessageUiModel;
import com.example.frontend.feature.chatbot.model.ChatOrderUiModel;
import com.example.frontend.feature.chatbot.model.ChatProductUiModel;
import com.example.frontend.feature.chatbot.model.ChatTicketUiModel;
import com.example.frontend.feature.chatbot.model.ComparisonUiModel;
import com.example.frontend.feature.chatbot.model.IngredientUiModel;
import com.example.frontend.feature.product.ProductDetailFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Arrays;
import java.util.List;

public class ChatConversationFragment extends Fragment {

    private static final String ARG_STARTER_MESSAGE = "starter_message";

    private ChatbotViewModel viewModel;
    private com.example.frontend.feature.cart.CartViewModel cartViewModel;
    private ChatMessageAdapter chatAdapter;
    private QuickReplyAdapter quickReplyAdapter;
    private EditText edtMessage;
    private View btnSend;
    private View btnNewChat;
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
        cartViewModel = new ViewModelProvider(requireActivity()).get(com.example.frontend.feature.cart.CartViewModel.class);

        initViews(view);
        setupRecyclerViews(view);
        setupInputArea();
        setupKeyboardHandling(view);
        observeViewModel();

        if (getArguments() != null) {
            String starterMessage = getArguments().getString(ARG_STARTER_MESSAGE);
            if (starterMessage != null && !starterMessage.isEmpty()) {
                edtMessage.setText(starterMessage);
            }
        }
    }

    private void setupKeyboardHandling(View view) {
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            androidx.core.graphics.Insets imeInsets = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.ime());
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            
            View inputArea = v.findViewById(R.id.layoutInputArea);
            if (inputArea != null) {
                // Adjust input area padding to avoid being covered by keyboard
                // If keyboard is shown, imeInsets.bottom will be > 0
                int bottomPadding = Math.max(imeInsets.bottom, systemBars.bottom);
                inputArea.setPadding(
                    inputArea.getPaddingLeft(),
                    inputArea.getPaddingTop(),
                    inputArea.getPaddingRight(),
                    imeInsets.bottom > 0 ? (int) getResources().getDimension(R.dimen.spacing_m) : (int) getResources().getDimension(R.dimen.spacing_xl)
                );
                
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) inputArea.getLayoutParams();
                lp.bottomMargin = imeInsets.bottom > 0 ? imeInsets.bottom : 0;
                inputArea.setLayoutParams(lp);
            }
            return insets;
        });
    }

    private void initViews(View view) {
        rvChat = view.findViewById(R.id.rvChat);
        layoutWelcome = view.findViewById(R.id.layoutWelcome);
        edtMessage = view.findViewById(R.id.edtMessage);
        btnSend = view.findViewById(R.id.btnSend);
        btnNewChat = view.findViewById(R.id.btnNewChat);
        ImageButton btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        btnNewChat.setOnClickListener(v -> showNewChatConfirmation());
    }

    private void showNewChatConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.chat_new_chat_confirm_title)
                .setMessage(R.string.chat_new_chat_confirm_message)
                .setNegativeButton(R.string.chat_new_chat_confirm_btn_cancel, null)
                .setPositiveButton(R.string.chat_new_chat_confirm_btn_confirm, (dialog, which) -> {
                    viewModel.startNewChat();
                    edtMessage.setText("");
                    Toast.makeText(getContext(), "Đã bắt đầu cuộc trò chuyện mới", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void setupRecyclerViews(View view) {
        chatAdapter = new ChatMessageAdapter(
                this::onProductClick,
                this::onAddToCartClick,
                this::onWhyRecommendClick,
                this::onOrderClick,
                this::onTicketClick,
                this::onPreferenceClick,
                new ChatMessageAdapter.OnAddComboClickListener() {
                    @Override
                    public void onAddComboClick(ChatMessageUiModel message) {
                        ChatConversationFragment.this.onAddComboClick(message);
                    }

                    @Override
                    public void onViewCartClick() {
                        ChatConversationFragment.this.onViewCartClick();
                    }
                },
                this::onComparisonDetailClick,
                this::onIngredientDetailClick
        );
        rvChat.setAdapter(chatAdapter);

        // Scroll to bottom when keyboard appears
        rvChat.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                rvChat.postDelayed(() -> {
                    if (chatAdapter.getItemCount() > 0) {
                        rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                }, 100);
            }
        });

        RecyclerView rvQuickReplies = view.findViewById(R.id.rvQuickReplies);
        quickReplyAdapter = new QuickReplyAdapter();
        rvQuickReplies.setAdapter(quickReplyAdapter);

        List<String> quickReplies = Arrays.asList(
                getString(R.string.chat_suggest_oily),
                getString(R.string.chat_suggest_order),
                getString(R.string.chat_suggest_sensitive),
                getString(R.string.chat_suggest_return),
                getString(R.string.chat_suggest_mascara),
                getString(R.string.chat_suggest_voucher)
        );
        quickReplyAdapter.setItems(quickReplies);

        quickReplyAdapter.setOnItemClickListener(text -> {
            edtMessage.setText(text);
            edtMessage.setSelection(text.length());
            sendMessage();
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

    private void onAddToCartClick(ChatProductUiModel product) {
        if (product == null) return;
        String varId = (product.getVariantId() != null && !product.getVariantId().isEmpty()) ? product.getVariantId() : "";
        viewModel.addToCart(product.getProductId(), varId);
    }

    private void onWhyRecommendClick(ChatProductUiModel product, boolean customerContextUsed) {
        showProductReasonDialog(product, customerContextUsed);
    }

    private void onComparisonDetailClick(ChatMessageUiModel message) {
        if (message == null || message.getComparison() == null || getContext() == null) return;
        showComparisonDetailDialog(message);
    }

    private void onIngredientDetailClick(ChatMessageUiModel message) {
        if (message == null || message.getIngredientData() == null || getContext() == null) return;
        showIngredientDetailDialog(message);
    }

    private void onProductClick(ChatProductUiModel product) {
        if (product == null || product.getProductId() == null) {
            Toast.makeText(getContext(), R.string.chat_product_detail_next_phase, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (getActivity() != null) {
                Fragment parent = getParentFragment();
                while (parent != null) {
                    if (parent instanceof com.google.android.material.bottomsheet.BottomSheetDialogFragment) {
                        ((com.google.android.material.bottomsheet.BottomSheetDialogFragment) parent).dismiss();
                        break;
                    }
                    parent = parent.getParentFragment();
                }
                ui.common.FragmentNavigationHelper.loadFragment(getActivity(), ProductDetailFragment.newInstance(product.getProductId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), R.string.chat_product_detail_next_phase, Toast.LENGTH_SHORT).show();
        }
    }

    private void onOrderClick(ChatOrderUiModel order) {
        if (getContext() == null || order == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_chat_order_detail, null);
                
        TextView tvOrderCode = dialogView.findViewById(R.id.tvOrderCode);
        TextView tvOrderStatus = dialogView.findViewById(R.id.tvOrderStatus);
        TextView tvPaymentStatus = dialogView.findViewById(R.id.tvPaymentStatus);
        TextView tvTotalAmount = dialogView.findViewById(R.id.tvTotalAmount);
        TextView tvItemsCount = dialogView.findViewById(R.id.tvItemsCount);
        LinearLayout llTimeline = dialogView.findViewById(R.id.llTimeline);
        android.widget.Button btnNextAction = dialogView.findViewById(R.id.btnNextAction);
        
        if (tvOrderCode != null) tvOrderCode.setText(order.getOrderCode());
        if (tvOrderStatus != null) {
            tvOrderStatus.setText(order.getStatusLabel());
            String status = order.getStatus() != null ? order.getStatus().toLowerCase() : "";
            int bgColor;
            if (status.contains("completed") || status.contains("delivered")) {
                bgColor = ContextCompat.getColor(requireContext(), R.color.success);
            } else if (status.contains("cancelled") || status.contains("failed")) {
                bgColor = ContextCompat.getColor(requireContext(), R.color.error);
            } else if (status.contains("shipping") || status.contains("delivering")) {
                bgColor = ContextCompat.getColor(requireContext(), R.color.primary);
            } else {
                bgColor = ContextCompat.getColor(requireContext(), R.color.status_pending_text);
            }
            tvOrderStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(bgColor));
        }
        
        if (tvPaymentStatus != null) tvPaymentStatus.setText("Thanh toán: " + (order.getPaymentStatusLabel() != null ? order.getPaymentStatusLabel() : order.getPaymentStatus()));
        if (tvTotalAmount != null) tvTotalAmount.setText("Tổng tiền: " + java.text.NumberFormat.getInstance().format(order.getTotalAmount()) + " đ");
        if (tvItemsCount != null) tvItemsCount.setText("Số lượng sản phẩm: " + order.getItemsCount());
        if (llTimeline != null && order.getTimeline() != null) {
            for (int i = 0; i < order.getTimeline().size(); i++) {
                com.example.frontend.feature.chatbot.model.ChatOrderTimelineUiModel item = order.getTimeline().get(i);
                TextView tv = new TextView(requireContext());
                tv.setText("• " + item.getLabel() + " (" + item.getTime() + ")");
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
                tv.setTextSize(13f);
                tv.setPadding(0, 0, 0, 8);
                llTimeline.addView(tv);
            }
        }
        
        if (btnNextAction != null) {
            if (order.getNextAction() != null && !order.getNextAction().isEmpty()) {
                btnNextAction.setText(order.getNextAction());
                btnNextAction.setOnClickListener(v -> {
                    dialog.dismiss();
                    viewModel.sendMessage(order.getNextAction());
                });
            } else {
                btnNextAction.setVisibility(View.GONE);
            }
        }
        
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void onTicketClick(ChatTicketUiModel ticket) {
        if (getContext() == null || ticket == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_chat_ticket_detail, null);

        TextView tvTicketCode = dialogView.findViewById(R.id.tvTicketCode);
        TextView tvTicketStatus = dialogView.findViewById(R.id.tvTicketStatus);
        TextView tvTicketCategory = dialogView.findViewById(R.id.tvTicketCategory);
        TextView tvTicketPriority = dialogView.findViewById(R.id.tvTicketPriority);
        TextView tvTicketDate = dialogView.findViewById(R.id.tvTicketDate);
        TextView tvTicketDescription = dialogView.findViewById(R.id.tvTicketDescription);
        android.widget.Button btnContactCS = dialogView.findViewById(R.id.btnContactCS);

        if (tvTicketCode != null) tvTicketCode.setText(ticket.getTicketCode());
        if (tvTicketStatus != null) {
            tvTicketStatus.setText(ticket.getStatusLabel());
            String status = ticket.getStatus() != null ? ticket.getStatus().toLowerCase() : "";
            int bgColor;
            if (status.contains("resolved") || status.contains("closed")) {
                bgColor = ContextCompat.getColor(requireContext(), R.color.success);
            } else if (status.contains("open") || status.contains("pending")) {
                bgColor = ContextCompat.getColor(requireContext(), R.color.status_pending_text);
            } else {
                bgColor = ContextCompat.getColor(requireContext(), R.color.primary);
            }
            tvTicketStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(bgColor));
        }

        if (tvTicketCategory != null) tvTicketCategory.setText("Phân loại: " + ticket.getCategoryLabel());
        if (tvTicketPriority != null) tvTicketPriority.setVisibility(View.GONE); // No priority in model
        if (tvTicketDate != null) tvTicketDate.setText("Ngày tạo: " + ticket.getCreatedAt());
        if (tvTicketDescription != null) tvTicketDescription.setText(ticket.getMessage());

        if (btnContactCS != null) {
            String status = ticket.getStatus() != null ? ticket.getStatus().toLowerCase() : "";
            if (status.contains("resolved") || status.contains("closed")) {
                btnContactCS.setVisibility(View.GONE);
            } else {
                btnContactCS.setOnClickListener(v -> {
                    dialog.dismiss();
                    viewModel.sendMessage("Tôi cần hỗ trợ thêm về yêu cầu " + ticket.getTicketCode());
                });
            }
        }

        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void onPreferenceClick(String option) {
        if (option != null && !option.isEmpty()) {
            viewModel.sendMessage(option);
        }
    }

    private void onAddComboClick(ChatMessageUiModel message) {
        if (message.getCartAction() != null && "require_login".equals(message.getCartAction().getReason())) {
            showLoginPrompt();
            return;
        }

        if (message.getCartSummary() != null) {
            showAddComboConfirmation(message);
        }
    }

    private void onViewCartClick() {
        if (getActivity() instanceof com.example.frontend.MainActivity) {
            ((com.example.frontend.MainActivity) getActivity()).navigateToCart();
        }
    }

    private void showLoginPrompt() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.chat_cart_require_login)
                .setNegativeButton(R.string.chat_new_chat_confirm_btn_cancel, null)
                .setPositiveButton(R.string.chat_cart_login_now, (dialog, which) -> {
                    if (getActivity() != null) {
                        ui.common.FragmentNavigationHelper.loadFragment(getActivity(), new com.example.frontend.feature.auth.LoginFragment());
                    }
                })
                .show();
    }

    private void showAddComboConfirmation(ChatMessageUiModel message) {
        if (getContext() == null || message.getCartAction() == null) return;
        
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Thêm Combo")
                .setMessage("Bạn có chắc chắn muốn thêm toàn bộ sản phẩm này vào giỏ hàng?")
                .setPositiveButton("Thêm", (dialog, which) -> {
                    java.util.List<com.example.frontend.feature.chatbot.model.ChatProductUiModel> comboProducts = new java.util.ArrayList<>();
                    if (message.getUpsellProducts() != null && !message.getUpsellProducts().isEmpty()) {
                        comboProducts.addAll(message.getUpsellProducts());
                    } else if (message.getProducts() != null && !message.getProducts().isEmpty()) {
                        comboProducts.addAll(message.getProducts());
                    }
                    
                    if (comboProducts.isEmpty()) {
                        Toast.makeText(getContext(), "Không có sản phẩm nào trong combo", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    for (com.example.frontend.feature.chatbot.model.ChatProductUiModel p : comboProducts) {
                        String varId = (p.getVariantId() != null && !p.getVariantId().isEmpty()) ? p.getVariantId() : "";
                        viewModel.addToCart(p.getProductId(), varId);
                    }
                    
                    // Inform backend for flexible AI response
                    viewModel.confirmAddCombo(message.getCartAction().getAction());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            // Requirement 1: Log UI state changes
            android.util.Log.d("CHATBOT_DEBUG", "Fragment UI State: messages=" + state.getMessages().size() + 
                ", isLoading=" + state.isLoading() + ", error=" + state.getError());

            chatAdapter.setMessages(state.getMessages());
            if (!state.getMessages().isEmpty()) {
                rvChat.scrollToPosition(state.getMessages().size() - 1);
            }

            // Update state views
            View root = getView();
            if (root == null) return;
            
            View layoutStateContainer = root.findViewById(R.id.layoutStateContainer);
            View viewLoading = root.findViewById(R.id.viewLoading);
            View viewError = root.findViewById(R.id.viewError);
            View viewNoInternet = root.findViewById(R.id.viewNoInternet);

            if (state.isLoading() && state.getMessages().isEmpty()) {
                // Show full screen loading only for initial load or when no messages
                layoutStateContainer.setVisibility(View.VISIBLE);
                viewLoading.setVisibility(View.VISIBLE);
                viewError.setVisibility(View.GONE);
                viewNoInternet.setVisibility(View.GONE);
                layoutWelcome.setVisibility(View.GONE);
                rvChat.setVisibility(View.GONE);
            } else if (state.getError() != null && state.getMessages().isEmpty()) {
                layoutStateContainer.setVisibility(View.VISIBLE);
                viewLoading.setVisibility(View.GONE);
                
                if ("no_internet".equals(state.getError())) {
                    viewNoInternet.setVisibility(View.VISIBLE);
                    viewError.setVisibility(View.GONE);
                } else {
                    viewError.setVisibility(View.VISIBLE);
                    viewNoInternet.setVisibility(View.GONE);
                    
                    TextView tvErrorMsg = viewError.findViewById(R.id.tvErrorDescription);
                    if (tvErrorMsg != null) tvErrorMsg.setText(state.getError());
                }
                
                layoutWelcome.setVisibility(View.GONE);
                rvChat.setVisibility(View.GONE);
            } else {
                layoutStateContainer.setVisibility(View.GONE);
                layoutWelcome.setVisibility(state.isWelcomeVisible() ? View.VISIBLE : View.GONE);
                rvChat.setVisibility(state.isWelcomeVisible() ? View.GONE : View.VISIBLE);
            }

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

        viewModel.getAddToCartResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case SUCCESS:
                    Toast.makeText(getContext(), R.string.chat_cart_add_success, Toast.LENGTH_SHORT).show();
                    // Sync badge count by reloading cart in shared ViewModel
                    if (cartViewModel != null) {
                        cartViewModel.loadCart();
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    // ─── Progressive Disclosure: Detail Popup Dialogs ───────────────────────────

    private void showProductReasonDialog(ChatProductUiModel product, boolean customerContextUsed) {
        if (getContext() == null) return;
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_chat_product_reason, null);

        // Update UI based on Kanila Beauty Context
        TextView tvReasonSubtitle = dialogView.findViewById(R.id.tvReasonSubtitle);
        ImageView ivReasonAiIcon = dialogView.findViewById(R.id.ivReasonAiIcon);
        if (tvReasonSubtitle != null && ivReasonAiIcon != null) {
            if (customerContextUsed) {
                tvReasonSubtitle.setText("Dựa trên hồ sơ Kanila Beauty của bạn");
                tvReasonSubtitle.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary));
                ivReasonAiIcon.setImageResource(R.drawable.ic_shortcut_kanila_beauty);
                ivReasonAiIcon.setColorFilter(null); // Keep original colors if it's a colorful icon
            } else {
                tvReasonSubtitle.setText("Phân tích từ Kanila AI");
                tvReasonSubtitle.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_tertiary));
                ivReasonAiIcon.setImageResource(R.drawable.ic_chat_placeholder);
            }
        }

        // Product mini info
        ImageView ivImage = dialogView.findViewById(R.id.ivReasonProductImage);
        TextView tvBrand = dialogView.findViewById(R.id.tvReasonProductBrand);
        TextView tvName = dialogView.findViewById(R.id.tvReasonProductName);
        TextView tvPrice = dialogView.findViewById(R.id.tvReasonProductPrice);
        TextView tvReason = dialogView.findViewById(R.id.tvReasonContent);

        if (tvBrand != null) tvBrand.setText(product.getBrandName());
        if (tvName != null) tvName.setText(product.getName());

        String displayPrice = product.getFinalPriceText() != null ? product.getFinalPriceText() : product.getPriceText();
        if (tvPrice != null) tvPrice.setText(displayPrice);

        if (ivImage != null) {
            Glide.with(requireContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(ivImage);
        }

        if (tvReason != null) {
            tvReason.setText(product.getReason() != null ? product.getReason() : "");
        }

        View layoutMatchScoreDialog = dialogView.findViewById(R.id.layoutMatchScoreDialog);
        TextView tvMatchScorePercentage = dialogView.findViewById(R.id.tvMatchScorePercentage);
        android.widget.ProgressBar pbMatchScore = dialogView.findViewById(R.id.pbMatchScore);

        if (product.getMatchScore() > 0) {
            int score = product.getMatchScore();
            if (score > 100) score = score / 100;
            if (layoutMatchScoreDialog != null) layoutMatchScoreDialog.setVisibility(View.VISIBLE);
            if (tvMatchScorePercentage != null) tvMatchScorePercentage.setText(score + "%");
            if (pbMatchScore != null) pbMatchScore.setProgress(score);
        } else {
            if (layoutMatchScoreDialog != null) layoutMatchScoreDialog.setVisibility(View.GONE);
        }

        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void showComparisonDetailDialog(ChatMessageUiModel message) {
        if (getContext() == null || message.getComparison() == null) return;
        ComparisonUiModel comparison = message.getComparison();

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_chat_comparison_detail, null);

        // Products section
        LinearLayout layoutProducts = dialogView.findViewById(R.id.layoutDialogComparisonProducts);
        if (layoutProducts != null && comparison.getProducts() != null) {
            for (com.example.frontend.feature.chatbot.model.ChatProductUiModel p : comparison.getProducts()) {
                View productView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_chat_product_card, layoutProducts, false);
                com.example.frontend.feature.chatbot.adapter.ChatProductAdapter.ProductViewHolder holder =
                        new com.example.frontend.feature.chatbot.adapter.ChatProductAdapter.ProductViewHolder(productView, this::onProductClick);
                holder.bind(p, message.isCustomerContextUsed(), this::onAddToCartClick, this::onWhyRecommendClick);
                layoutProducts.addView(productView);
            }
        }

        // Differences section
        LinearLayout layoutDiffs = dialogView.findViewById(R.id.layoutDialogDifferences);
        if (layoutDiffs != null && comparison.getDifferences() != null) {
            for (java.util.Map.Entry<String, String> entry : comparison.getDifferences().entrySet()) {
                LinearLayout rowLayout = new LinearLayout(requireContext());
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setPadding(0, 0, 0, (int) (8 * getResources().getDisplayMetrics().density));

                TextView tvLabel = new TextView(requireContext());
                tvLabel.setText(entry.getKey() + ": ");
                tvLabel.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary));
                tvLabel.setTextSize(13f);
                tvLabel.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                TextView tvValue = new TextView(requireContext());
                tvValue.setText(entry.getValue());
                tvValue.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_main));
                tvValue.setTextSize(13f);
                LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                tvValue.setLayoutParams(valueParams);

                rowLayout.addView(tvLabel);
                rowLayout.addView(tvValue);
                layoutDiffs.addView(rowLayout);
            }
        }

        // Pros Cons Section
        View layoutProsCons = dialogView.findViewById(R.id.layoutDialogProsCons);
        LinearLayout layoutProsConsContent = dialogView.findViewById(R.id.layoutDialogProsConsContent);
        if (comparison.getProsCons() != null && !comparison.getProsCons().isEmpty()) {
            if (layoutProsCons != null) layoutProsCons.setVisibility(View.VISIBLE);
            if (layoutProsConsContent != null) {
                for (java.util.Map.Entry<String, ComparisonUiModel.ProsConsUi> entry : comparison.getProsCons().entrySet()) {
                    LinearLayout cardLayout = new LinearLayout(requireContext());
                    cardLayout.setOrientation(LinearLayout.VERTICAL);
                    cardLayout.setBackgroundResource(R.drawable.bg_card);
                    cardLayout.setPadding((int)(12 * getResources().getDisplayMetrics().density), (int)(12 * getResources().getDisplayMetrics().density), (int)(12 * getResources().getDisplayMetrics().density), (int)(12 * getResources().getDisplayMetrics().density));
                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    cardParams.setMargins(0, 0, 0, (int)(12 * getResources().getDisplayMetrics().density));
                    cardLayout.setLayoutParams(cardParams);

                    TextView tvTitle = new TextView(requireContext());
                    tvTitle.setText(entry.getKey());
                    tvTitle.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_main));
                    tvTitle.setTextSize(14f);
                    tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
                    tvTitle.setPadding(0, 0, 0, (int)(8 * getResources().getDisplayMetrics().density));
                    cardLayout.addView(tvTitle);

                    ComparisonUiModel.ProsConsUi pc = entry.getValue();
                    if (pc.getPros() != null && !pc.getPros().isEmpty()) {
                        TextView tvProsHeader = new TextView(requireContext());
                        tvProsHeader.setText("✅ Ưu điểm:");
                        tvProsHeader.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.success));
                        tvProsHeader.setTextSize(13f);
                        cardLayout.addView(tvProsHeader);

                        for (String pro : pc.getPros()) {
                            TextView tvPro = new TextView(requireContext());
                            tvPro.setText("• " + pro);
                            tvPro.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary));
                            tvPro.setTextSize(13f);
                            cardLayout.addView(tvPro);
                        }
                    }

                    if (pc.getCons() != null && !pc.getCons().isEmpty()) {
                        TextView tvConsHeader = new TextView(requireContext());
                        tvConsHeader.setText("⚠️ Nhược điểm:");
                        tvConsHeader.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.error));
                        tvConsHeader.setTextSize(13f);
                        tvConsHeader.setPadding(0, (int)(8 * getResources().getDisplayMetrics().density), 0, 0);
                        cardLayout.addView(tvConsHeader);

                        for (String con : pc.getCons()) {
                            TextView tvCon = new TextView(requireContext());
                            tvCon.setText("• " + con);
                            tvCon.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary));
                            tvCon.setTextSize(13f);
                            cardLayout.addView(tvCon);
                        }
                    }
                    layoutProsConsContent.addView(cardLayout);
                }
            }
        } else {
            if (layoutProsCons != null) layoutProsCons.setVisibility(View.GONE);
        }

        // Recommendation section
        View layoutRec = dialogView.findViewById(R.id.layoutDialogRecommendation);
        TextView tvRec = dialogView.findViewById(R.id.tvDialogRecommendationText);
        if (comparison.getRecommendation() != null && !comparison.getRecommendation().isEmpty()) {
            if (tvRec != null) tvRec.setText(comparison.getRecommendation());
            if (layoutRec != null) layoutRec.setVisibility(View.VISIBLE);
        } else {
            if (layoutRec != null) layoutRec.setVisibility(View.GONE);
        }

        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void showIngredientDetailDialog(ChatMessageUiModel message) {
        if (getContext() == null || message.getIngredientData() == null) return;
        IngredientUiModel ingredient = message.getIngredientData();

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_chat_ingredient_detail, null);

        // Title
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogIngredientTitle);
        if (tvTitle != null) {
            tvTitle.setText("🧪 " + ingredient.getIngredientName());
        }

        // Compatibility
        View layoutCompat = dialogView.findViewById(R.id.layoutDialogCompatibility);
        TextView tvBadge = dialogView.findViewById(R.id.tvDialogCompatibilityBadge);
        TextView tvReason = dialogView.findViewById(R.id.tvDialogCompatibilityReason);

        if (ingredient.getCompatibilityLevel() != null && !ingredient.getCompatibilityLevel().isEmpty()) {
            if (layoutCompat != null) layoutCompat.setVisibility(View.VISIBLE);
            String level = ingredient.getCompatibilityLevel().toLowerCase();
            int badgeColor;
            int bgColor;
            String label;
            int progress;
            if ("safe".equals(level)) {
                badgeColor = ContextCompat.getColor(requireContext(), R.color.success);
                bgColor = ContextCompat.getColor(requireContext(), R.color.status_success_bg);
                label = "🟢 Phù hợp với da của bạn";
                progress = 95;
            } else if ("warning".equals(level)) {
                badgeColor = ContextCompat.getColor(requireContext(), R.color.status_pending_text);
                bgColor = ContextCompat.getColor(requireContext(), R.color.status_pending_bg);
                label = "🟡 Cần lưu ý";
                progress = 65;
            } else if ("avoid".equals(level)) {
                badgeColor = ContextCompat.getColor(requireContext(), R.color.error);
                bgColor = ContextCompat.getColor(requireContext(), R.color.status_payment_failed_bg);
                label = "🔴 Không nên kết hợp";
                progress = 30;
            } else {
                badgeColor = ContextCompat.getColor(requireContext(), R.color.text_tertiary);
                bgColor = ContextCompat.getColor(requireContext(), R.color.border_divider);
                label = ingredient.getCompatibilityLevel();
                progress = 50;
            }
            if (tvBadge != null) {
                tvBadge.setText(label);
                tvBadge.setTextColor(badgeColor);
                tvBadge.getBackground().setTint(badgeColor & 0x33FFFFFF | (badgeColor & 0x00FFFFFF)); // Approximate 20% alpha background
            }
            if (layoutCompat != null) {
                layoutCompat.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(bgColor));
            }
            
            TextView tvMatchScore = dialogView.findViewById(R.id.tvIngredientMatchScore);
            android.widget.ProgressBar pbMatchScore = dialogView.findViewById(R.id.pbIngredientMatchScore);
            if (tvMatchScore != null) {
                tvMatchScore.setText(progress + "%");
                tvMatchScore.setTextColor(badgeColor);
            }
            if (pbMatchScore != null) {
                pbMatchScore.setProgress(progress);
                pbMatchScore.setProgressTintList(android.content.res.ColorStateList.valueOf(badgeColor));
            }
            if (tvReason != null && ingredient.getCompatibilityReason() != null && !ingredient.getCompatibilityReason().isEmpty()) {
                tvReason.setText(ingredient.getCompatibilityReason());
                tvReason.setVisibility(View.VISIBLE);
            }
        } else {
            if (layoutCompat != null) layoutCompat.setVisibility(View.GONE);
        }

        // Benefits
        TextView tvBenefits = dialogView.findViewById(R.id.tvDialogBenefits);
        if (tvBenefits != null && ingredient.getBenefits() != null && !ingredient.getBenefits().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String b : ingredient.getBenefits()) sb.append("• ").append(b).append("\n");
            tvBenefits.setText(sb.toString().trim());
        } else if (tvBenefits != null) {
            tvBenefits.setText("Không có thông tin");
        }

        // Skin types
        TextView tvSkinTypes = dialogView.findViewById(R.id.tvDialogSkinTypes);
        if (tvSkinTypes != null && ingredient.getSuitableSkinTypes() != null && !ingredient.getSuitableSkinTypes().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String s : ingredient.getSuitableSkinTypes()) sb.append("• ").append(s).append("\n");
            tvSkinTypes.setText(sb.toString().trim());
        } else if (tvSkinTypes != null) {
            tvSkinTypes.setText("Không có thông tin");
        }

        // Warnings
        View layoutWarnings = dialogView.findViewById(R.id.layoutDialogWarnings);
        TextView tvWarnings = dialogView.findViewById(R.id.tvDialogWarnings);
        if (ingredient.getWarnings() != null && !ingredient.getWarnings().isEmpty()) {
            if (layoutWarnings != null) layoutWarnings.setVisibility(View.VISIBLE);
            if (tvWarnings != null) {
                StringBuilder sb = new StringBuilder();
                for (String w : ingredient.getWarnings()) sb.append("• ").append(w).append("\n");
                tvWarnings.setText(sb.toString().trim());
            }
        } else {
            if (layoutWarnings != null) layoutWarnings.setVisibility(View.GONE);
        }

        dialog.setContentView(dialogView);
        dialog.show();
    }
}

