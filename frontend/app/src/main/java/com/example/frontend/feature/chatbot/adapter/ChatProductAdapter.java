package com.example.frontend.feature.chatbot.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.feature.chatbot.model.ChatProductUiModel;

import java.util.ArrayList;
import java.util.List;

public class ChatProductAdapter extends RecyclerView.Adapter<ChatProductAdapter.ProductViewHolder> {

    private final List<ChatProductUiModel> products = new ArrayList<>();
    private final OnProductClickListener listener;
    private OnAddToCartClickListener addToCartListener;
    private OnWhyRecommendClickListener whyRecommendListener;
    private boolean customerContextUsed = false;

    public interface OnProductClickListener {
        void onProductClick(ChatProductUiModel product);
    }

    public interface OnAddToCartClickListener {
        void onAddToCartClick(ChatProductUiModel product);
    }

    public interface OnWhyRecommendClickListener {
        void onWhyRecommendClick(ChatProductUiModel product);
    }

    public ChatProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setOnAddToCartClickListener(OnAddToCartClickListener addToCartListener) {
        this.addToCartListener = addToCartListener;
    }

    public void setOnWhyRecommendClickListener(OnWhyRecommendClickListener whyRecommendListener) {
        this.whyRecommendListener = whyRecommendListener;
    }

    public void setProducts(List<ChatProductUiModel> newProducts) {
        setProducts(newProducts, false);
    }

    public void setProducts(List<ChatProductUiModel> newProducts, boolean customerContextUsed) {
        this.customerContextUsed = customerContextUsed;
        products.clear();
        if (newProducts != null) {
            products.addAll(newProducts);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_product_card, parent, false);
        return new ProductViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position), customerContextUsed, addToCartListener, whyRecommendListener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvBrandName, tvProductName, tvCategory, tvSalePrice, tvOriginalPrice, tvMatchedReason, tvRecommendationBadge, btnViewDetail, btnAddToCart, btnWhyRecommend;
        private final OnProductClickListener listener;

        public ProductViewHolder(@NonNull View itemView, OnProductClickListener listener) {
            super(itemView);
            this.listener = listener;
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvBrandName = itemView.findViewById(R.id.tvProductBrand);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvCategory = itemView.findViewById(R.id.tvProductCategory);
            tvSalePrice = itemView.findViewById(R.id.tvProductPrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvProductOriginalPrice);
            tvMatchedReason = itemView.findViewById(R.id.tvMatchedReason);
            tvRecommendationBadge = itemView.findViewById(R.id.tvProductBadge);
            btnViewDetail = itemView.findViewById(R.id.btnChatViewDetail);
            btnAddToCart = itemView.findViewById(R.id.btnChatAddToCart);
            btnWhyRecommend = itemView.findViewById(R.id.btnWhyRecommend);
        }

        public void bind(ChatProductUiModel product, boolean customerContextUsed, OnAddToCartClickListener addToCartListener, OnWhyRecommendClickListener whyRecommendListener) {
            tvBrandName.setText(product.getBrandName());
            tvProductName.setText(product.getName());
            
            if (tvCategory != null) {
                tvCategory.setText(product.getCategoryName());
                tvCategory.setVisibility(View.VISIBLE);
            }
            
            // Handle prices
            String displayPrice = product.getFinalPriceText() != null ? product.getFinalPriceText() : product.getPriceText();
            String strikePrice = product.getCompareAtPriceText() != null ? product.getCompareAtPriceText() : 
                                (product.getFinalPriceText() != null ? product.getPriceText() : null);
            
            tvSalePrice.setText(displayPrice);
            
            if (strikePrice != null && !strikePrice.isEmpty() && !strikePrice.equals(displayPrice)) {
                tvOriginalPrice.setText(strikePrice);
                tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvOriginalPrice.setVisibility(View.VISIBLE);
            } else {
                tvOriginalPrice.setVisibility(View.GONE);
            }

            // Hide inline reason — shown via popup instead
            if (tvMatchedReason != null) {
                tvMatchedReason.setVisibility(View.GONE);
            }
            
            if (tvRecommendationBadge != null) {
                if (product.getSuggestedUse() != null && !product.getSuggestedUse().isEmpty()) {
                    tvRecommendationBadge.setText(product.getSuggestedUse());
                    View parentBadge = (View) tvRecommendationBadge.getParent();
                    if (parentBadge != null) parentBadge.setVisibility(View.VISIBLE);
                } else {
                    View parentBadge = (View) tvRecommendationBadge.getParent();
                    if (parentBadge != null) parentBadge.setVisibility(View.GONE);
                }
            }

            // Show chat actions if available
            View chatActions = itemView.findViewById(R.id.layoutChatActions);
            if (chatActions != null) {
                chatActions.setVisibility(View.VISIBLE);
            }

            // Hide default add to cart button if we have chat actions
            View defaultAddToCart = itemView.findViewById(R.id.btnAddToCart);
            if (defaultAddToCart != null) {
                defaultAddToCart.setVisibility(View.GONE);
            }

            // "Why recommended?" button — show only when reason exists
            if (btnWhyRecommend != null) {
                boolean hasReason = product.getReason() != null && !product.getReason().isEmpty();
                btnWhyRecommend.setVisibility(hasReason ? View.VISIBLE : View.GONE);
                if (hasReason) {
                    btnWhyRecommend.setOnClickListener(v -> {
                        if (whyRecommendListener != null) whyRecommendListener.onWhyRecommendClick(product);
                    });
                }
            }

            Glide.with(ivProductImage.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(ivProductImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onProductClick(product);
            });

            btnViewDetail.setOnClickListener(v -> {
                if (listener != null) listener.onProductClick(product);
            });

            if (btnAddToCart != null) {
                btnAddToCart.setOnClickListener(v -> {
                    if (addToCartListener != null) addToCartListener.onAddToCartClick(product);
                });
            }
        }
    }
}
