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

        if (product.getVariantId() != null && !product.getVariantId().isEmpty()) {
            viewModel.addToCart(product.getProductId(), product.getVariantId());
        } else {
            onProductClick(product);
        }
    }

    private void onWhyRecommendClick(ChatProductUiModel product) {
        if (product == null || getContext() == null) return;
        showProductReasonDialog(product);
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
                ui.common.FragmentNavigationHelper.loadFragment(getActivity(), ProductDetailFragment.newInstance(product.getProductId()));
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
        String info = getString(R.string.chat_cart_confirm_info, 
                message.getCartSummary().getItemsCount(), 
                message.getCartSummary().getTotal());

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.chat_cart_confirm_title)
                .setMessage(getString(R.string.chat_cart_confirm_msg) + "\n\n" + info)
                .setNegativeButton(R.string.chat_new_chat_confirm_btn_cancel, null)
                .setPositiveButton(R.string.chat_cart_confirm_btn_add, (dialog, which) -> {
                    if (message.getCartAction() != null) {
                        viewModel.confirmAddCombo(message.getCartAction().getAction());
                    }
                })
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

    private void showProductReasonDialog(ChatProductUiModel product) {
        if (getContext() == null) return;
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_chat_product_reason, null);

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
                holder.bind(p, message.isCustomerContextUsed(), null, null);
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
            if ("safe".equals(level)) {
                badgeColor = ContextCompat.getColor(requireContext(), R.color.success);
                bgColor = ContextCompat.getColor(requireContext(), R.color.status_success_bg);
                label = "🟢 Phù hợp với da của bạn";
            } else if ("warning".equals(level)) {
                badgeColor = ContextCompat.getColor(requireContext(), R.color.status_pending_text);
                bgColor = ContextCompat.getColor(requireContext(), R.color.status_pending_bg);
                label = "🟡 Cần lưu ý";
            } else if ("avoid".equals(level)) {
                badgeColor = ContextCompat.getColor(requireContext(), R.color.error);
                bgColor = ContextCompat.getColor(requireContext(), R.color.status_payment_failed_bg);
                label = "🔴 Không nên kết hợp";
            } else {
                badgeColor = ContextCompat.getColor(requireContext(), R.color.text_tertiary);
                bgColor = ContextCompat.getColor(requireContext(), R.color.border_divider);
                label = ingredient.getCompatibilityLevel();
            }
            if (tvBadge != null) {
                tvBadge.setText(label);
                tvBadge.setTextColor(badgeColor);
            }
            if (layoutCompat != null) {
                layoutCompat.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(bgColor));
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

