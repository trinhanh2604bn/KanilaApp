package com.example.frontend.feature.chatbot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.feature.chatbot.model.ChatCartActionUiModel;
import com.example.frontend.feature.chatbot.model.ChatCartSummaryUiModel;
import com.example.frontend.feature.chatbot.model.ChatMessageUiModel;
import com.example.frontend.feature.chatbot.model.ChatOrderTimelineUiModel;
import com.example.frontend.feature.chatbot.model.ChatOrderUiModel;
import com.example.frontend.feature.chatbot.model.ChatProductUiModel;
import com.example.frontend.feature.chatbot.model.ChatTicketUiModel;
import com.example.frontend.feature.chatbot.model.ComparisonUiModel;
import com.example.frontend.feature.chatbot.model.IngredientUiModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    public interface OnPreferenceClickListener {
        void onPreferenceClick(String option);
    }

    public interface OnAddComboClickListener {
        void onAddComboClick(ChatMessageUiModel message);
        void onViewCartClick();
    }

    private final List<ChatMessageUiModel> messages = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final ChatProductAdapter.OnProductClickListener productClickListener;
    private final ChatProductAdapter.OnAddToCartClickListener addToCartClickListener;
    private final OnOrderClickListener orderClickListener;
    private final OnTicketClickListener ticketClickListener;
    private final OnPreferenceClickListener preferenceClickListener;
    private final OnAddComboClickListener addComboClickListener;

    public ChatMessageAdapter(ChatProductAdapter.OnProductClickListener productClickListener,
                             ChatProductAdapter.OnAddToCartClickListener addToCartClickListener,
                             OnOrderClickListener orderClickListener,
                             OnTicketClickListener ticketClickListener,
                             OnPreferenceClickListener preferenceClickListener,
                             OnAddComboClickListener addComboClickListener) {
        this.productClickListener = productClickListener;
        this.addToCartClickListener = addToCartClickListener;
        this.orderClickListener = orderClickListener;
        this.ticketClickListener = ticketClickListener;
        this.preferenceClickListener = preferenceClickListener;
        this.addComboClickListener = addComboClickListener;
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
        TextView tvMessage, tvTime, tvPersonalizedBadge;
        RecyclerView rvProducts, rvPreferenceOptions, rvUpsellProducts;
        ChatProductAdapter productAdapter, upsellAdapter;
        QuickReplyAdapter preferenceAdapter;
        View layoutUpsell;
        
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

        // Cart summary views
        View layoutCartSummary;
        TextView tvCartItemsCount, tvCartSubtotal, tvCartDiscount, tvCartTotal;
        View layoutCartDiscountRow;
        View btnAddCombo, btnViewCart;

        // Comparison card views
        View layoutComparisonCard;
        ViewGroup layoutComparisonProducts, layoutDifferences;
        View layoutRecommendation;
        TextView tvRecommendationText;

        // Ingredient card views
        View layoutIngredientCard;
        TextView tvIngredientName, tvCompatibilityBadge, tvCompatibilityReason, tvBenefits, tvSkinTypes, tvWarnings;
        View layoutCompatibility, layoutWarnings;

        BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPersonalizedBadge = itemView.findViewById(R.id.tvPersonalizedBadge);
            rvProducts = itemView.findViewById(R.id.rvProducts);
            rvPreferenceOptions = itemView.findViewById(R.id.rvPreferenceOptions);
            
            productAdapter = new ChatProductAdapter(productClickListener);
            productAdapter.setOnAddToCartClickListener(addToCartClickListener);
            rvProducts.setAdapter(productAdapter);

            rvUpsellProducts = itemView.findViewById(R.id.rvUpsellProducts);
            layoutUpsell = itemView.findViewById(R.id.layoutUpsell);
            upsellAdapter = new ChatProductAdapter(productClickListener);
            upsellAdapter.setOnAddToCartClickListener(addToCartClickListener);
            if (rvUpsellProducts != null) rvUpsellProducts.setAdapter(upsellAdapter);

            preferenceAdapter = new QuickReplyAdapter();
            preferenceAdapter.setOnItemClickListener(option -> {
                if (preferenceClickListener != null) {
                    preferenceClickListener.onPreferenceClick(option);
                }
            });
            rvPreferenceOptions.setAdapter(preferenceAdapter);

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

            // Init Cart summary views
            layoutCartSummary = itemView.findViewById(R.id.layoutCartSummaryContainer);
            if (layoutCartSummary != null) {
                tvCartItemsCount = layoutCartSummary.findViewById(R.id.tvItemsCount);
                tvCartSubtotal = layoutCartSummary.findViewById(R.id.tvSubtotal);
                tvCartDiscount = layoutCartSummary.findViewById(R.id.tvDiscount);
                tvCartTotal = layoutCartSummary.findViewById(R.id.tvTotal);
                layoutCartDiscountRow = layoutCartSummary.findViewById(R.id.layoutDiscount);
                btnAddCombo = layoutCartSummary.findViewById(R.id.btnAddCombo);
                btnViewCart = layoutCartSummary.findViewById(R.id.btnViewCart);
            }

            // Init Comparison views
            layoutComparisonCard = itemView.findViewById(R.id.layoutComparisonCardContainer);
            if (layoutComparisonCard != null) {
                layoutComparisonProducts = layoutComparisonCard.findViewById(R.id.layoutComparisonProducts);
                layoutDifferences = layoutComparisonCard.findViewById(R.id.layoutDifferences);
                layoutRecommendation = layoutComparisonCard.findViewById(R.id.layoutRecommendation);
                tvRecommendationText = layoutComparisonCard.findViewById(R.id.tvRecommendationText);
            }

            // Init Ingredient views
            layoutIngredientCard = itemView.findViewById(R.id.layoutIngredientCardContainer);
            if (layoutIngredientCard != null) {
                tvIngredientName = layoutIngredientCard.findViewById(R.id.tvIngredientName);
                layoutCompatibility = layoutIngredientCard.findViewById(R.id.layoutCompatibility);
                tvCompatibilityBadge = layoutIngredientCard.findViewById(R.id.tvCompatibilityBadge);
                tvCompatibilityReason = layoutIngredientCard.findViewById(R.id.tvCompatibilityReason);
                tvBenefits = layoutIngredientCard.findViewById(R.id.tvBenefits);
                tvSkinTypes = layoutIngredientCard.findViewById(R.id.tvSkinTypes);
                layoutWarnings = layoutIngredientCard.findViewById(R.id.layoutWarnings);
                tvWarnings = layoutIngredientCard.findViewById(R.id.tvWarnings);
            }
        }

        void bind(ChatMessageUiModel message) {
            tvMessage.setText(message.getContent());
            tvTime.setText(timeFormat.format(new Date(message.getTimestamp())));

            // Personalized badge
            if (message.isCustomerContextUsed() && tvPersonalizedBadge != null) {
                if ("ingredient_analysis".equals(message.getReplyType())) {
                    tvPersonalizedBadge.setText(R.string.chat_personalized_skin_profile);
                } else {
                    tvPersonalizedBadge.setText(R.string.chat_personalized_badge);
                }
                tvPersonalizedBadge.setVisibility(View.VISIBLE);
            } else if (tvPersonalizedBadge != null) {
                tvPersonalizedBadge.setVisibility(View.GONE);
            }

            // Products
            if (message.getProducts() != null && !message.getProducts().isEmpty()) {
                productAdapter.setProducts(message.getProducts(), message.isCustomerContextUsed());
                rvProducts.setVisibility(View.VISIBLE);
            } else {
                rvProducts.setVisibility(View.GONE);
            }

            // Upsell Products
            if (message.getUpsellProducts() != null && !message.getUpsellProducts().isEmpty() && layoutUpsell != null) {
                upsellAdapter.setProducts(message.getUpsellProducts(), false);
                layoutUpsell.setVisibility(View.VISIBLE);
            } else if (layoutUpsell != null) {
                layoutUpsell.setVisibility(View.GONE);
            }

            // Preference question
            if (message.getPreferenceQuestion() != null && rvPreferenceOptions != null 
                    && message.getPreferenceQuestion().getOptions() != null 
                    && !message.getPreferenceQuestion().getOptions().isEmpty()) {
                preferenceAdapter.setItems(message.getPreferenceQuestion().getOptions());
                rvPreferenceOptions.setVisibility(View.VISIBLE);
            } else if (rvPreferenceOptions != null) {
                rvPreferenceOptions.setVisibility(View.GONE);
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

            // Cart Summary
            if (message.getCartSummary() != null && layoutCartSummary != null) {
                bindCartSummary(message);
                layoutCartSummary.setVisibility(View.VISIBLE);
            } else if (layoutCartSummary != null) {
                layoutCartSummary.setVisibility(View.GONE);
            }

            // Comparison
            if (message.getComparison() != null && layoutComparisonCard != null) {
                bindComparisonCard(message.getComparison(), message.isCustomerContextUsed());
                layoutComparisonCard.setVisibility(View.VISIBLE);
            } else if (layoutComparisonCard != null) {
                layoutComparisonCard.setVisibility(View.GONE);
            }

            // Ingredient
            if (message.getIngredientData() != null && layoutIngredientCard != null) {
                bindIngredientCard(message.getIngredientData());
                layoutIngredientCard.setVisibility(View.VISIBLE);
            } else if (layoutIngredientCard != null) {
                layoutIngredientCard.setVisibility(View.GONE);
            }
        }

        private void bindCartSummary(ChatMessageUiModel message) {
            ChatCartSummaryUiModel summary = message.getCartSummary();
            ChatCartActionUiModel action = message.getCartAction();

            if (action != null && action.isSuccess() && action.getCartCount() != null) {
                tvCartItemsCount.setText(itemView.getContext().getString(R.string.chat_cart_count_format, action.getCartCount()));
            } else {
                tvCartItemsCount.setText(itemView.getContext().getString(R.string.chat_cart_items_count, summary.getItemsCount()));
            }

            tvCartSubtotal.setText(summary.getSubtotal());
            tvCartTotal.setText(summary.getTotal());

            if (summary.isHasDiscount()) {
                tvCartDiscount.setText(summary.getDiscount());
                layoutCartDiscountRow.setVisibility(View.VISIBLE);
            } else {
                layoutCartDiscountRow.setVisibility(View.GONE);
            }

            if (action != null && action.isRequiresConfirmation()) {
                btnAddCombo.setVisibility(View.VISIBLE);
                btnAddCombo.setOnClickListener(v -> {
                    if (addComboClickListener != null) addComboClickListener.onAddComboClick(message);
                });
            } else {
                btnAddCombo.setVisibility(View.GONE);
            }

            if (action != null && action.isSuccess() && "added".equals(action.getAction())) {
                btnViewCart.setVisibility(View.VISIBLE);
                btnViewCart.setOnClickListener(v -> {
                    if (addComboClickListener != null) addComboClickListener.onViewCartClick();
                });
            } else {
                btnViewCart.setVisibility(View.GONE);
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

        private void bindComparisonCard(ComparisonUiModel comparison, boolean customerContextUsed) {
            // Products
            layoutComparisonProducts.removeAllViews();
            if (comparison.getProducts() != null) {
                for (ChatProductUiModel p : comparison.getProducts()) {
                    View productView = LayoutInflater.from(itemView.getContext())
                            .inflate(R.layout.item_chat_product_card, layoutComparisonProducts, false);
                    
                    ChatProductAdapter.ProductViewHolder holder = new ChatProductAdapter.ProductViewHolder(productView, productClickListener);
                    holder.bind(p, customerContextUsed, null);
                    
                    layoutComparisonProducts.addView(productView);
                }
            }

            // Differences
            layoutDifferences.removeAllViews();
            if (comparison.getDifferences() != null) {
                for (Map.Entry<String, String> entry : comparison.getDifferences().entrySet()) {
                    View diffView = LayoutInflater.from(itemView.getContext())
                            .inflate(R.layout.item_comparison_difference_row, layoutDifferences, false);
                    
                    TextView tvTitle = diffView.findViewById(R.id.tvDifferenceTitle);
                    TextView tvValue = diffView.findViewById(R.id.tvDifferenceValue);
                    
                    tvTitle.setText(entry.getKey());
                    tvValue.setText(entry.getValue());
                    
                    layoutDifferences.addView(diffView);
                }
            }

            // Recommendation
            if (comparison.getRecommendation() != null && !comparison.getRecommendation().isEmpty()) {
                tvRecommendationText.setText(comparison.getRecommendation());
                layoutRecommendation.setVisibility(View.VISIBLE);
            } else {
                layoutRecommendation.setVisibility(View.GONE);
            }
        }

        private void bindIngredientCard(IngredientUiModel ingredient) {
            tvIngredientName.setText("🧪 " + ingredient.getIngredientName());

            // Compatibility
            if (ingredient.getCompatibilityLevel() != null && !ingredient.getCompatibilityLevel().isEmpty()) {
                layoutCompatibility.setVisibility(View.VISIBLE);
                
                String level = ingredient.getCompatibilityLevel().toLowerCase();
                int color;
                int bgColor;
                String label;

                if ("safe".equals(level)) {
                    color = ContextCompat.getColor(itemView.getContext(), R.color.success);
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.status_success_bg);
                    label = "🟢 Phù hợp";
                } else if ("warning".equals(level)) {
                    color = ContextCompat.getColor(itemView.getContext(), R.color.status_pending_text);
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.status_pending_bg);
                    label = "🟡 Cần lưu ý";
                } else if ("avoid".equals(level)) {
                    color = ContextCompat.getColor(itemView.getContext(), R.color.error);
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.status_payment_failed_bg);
                    label = "🔴 Không nên kết hợp";
                } else {
                    color = ContextCompat.getColor(itemView.getContext(), R.color.text_tertiary);
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.border_divider);
                    label = ingredient.getCompatibilityLevel();
                }

                tvCompatibilityBadge.setText(label);
                tvCompatibilityBadge.setTextColor(color);
                layoutCompatibility.setBackgroundTintList(android.content.res.ColorStateList.valueOf(bgColor));

                if (ingredient.getCompatibilityReason() != null && !ingredient.getCompatibilityReason().isEmpty()) {
                    tvCompatibilityReason.setText(ingredient.getCompatibilityReason());
                    tvCompatibilityReason.setVisibility(View.VISIBLE);
                } else {
                    tvCompatibilityReason.setVisibility(View.GONE);
                }
            } else {
                layoutCompatibility.setVisibility(View.GONE);
                tvCompatibilityReason.setVisibility(View.GONE);
            }

            // Benefits
            if (ingredient.getBenefits() != null && !ingredient.getBenefits().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String b : ingredient.getBenefits()) {
                    sb.append("• ").append(b).append("\n");
                }
                tvBenefits.setText(sb.toString().trim());
            } else {
                tvBenefits.setText("Không có thông tin");
            }

            // Skin Types
            if (ingredient.getSuitableSkinTypes() != null && !ingredient.getSuitableSkinTypes().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String s : ingredient.getSuitableSkinTypes()) {
                    sb.append("• ").append(s).append("\n");
                }
                tvSkinTypes.setText(sb.toString().trim());
            } else {
                tvSkinTypes.setText("Không có thông tin");
            }

            // Warnings
            if (ingredient.getWarnings() != null && !ingredient.getWarnings().isEmpty()) {
                layoutWarnings.setVisibility(View.VISIBLE);
                StringBuilder sb = new StringBuilder();
                for (String w : ingredient.getWarnings()) {
                    sb.append("• ").append(w).append("\n");
                }
                tvWarnings.setText(sb.toString().trim());
            } else {
                layoutWarnings.setVisibility(View.GONE);
            }
        }
    }

    static class TypingViewHolder extends RecyclerView.ViewHolder {
        TypingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
