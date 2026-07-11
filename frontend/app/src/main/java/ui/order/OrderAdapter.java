package ui.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.model.order.OrderSummaryDto;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(OrderSummaryDto order);
        default void onActionClick(OrderSummaryDto order, String action) {}
    }

    private final List<OrderSummaryDto> orders = new ArrayList<>();
    private OnOrderClickListener listener;

    public OrderAdapter() {
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOrders(List<OrderSummaryDto> newOrders) {
        orders.clear();
        if (newOrders != null) {
            orders.addAll(newOrders);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_card, parent, false);
        return new OrderViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderSummaryDto order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvBrandName, tvStatus, tvPaymentStatus, tvOrderNumber, tvOrderDate;
        TextView tvProductName, tvVariant, tvQuantity, tvPrice, tvTotalSummary, tvGrandTotal, tvDisclaimer;
        ImageView ivProduct;
        MaterialButton btnAction, btnReturn;
        LinearLayout layoutActionArea;
        private final OnOrderClickListener listener;

        public OrderViewHolder(@NonNull View itemView, OnOrderClickListener listener) {
            super(itemView);
            this.listener = listener;
            tvBrandName = itemView.findViewById(R.id.tvOrderBrandName);
            tvStatus = itemView.findViewById(R.id.tvOrderHeaderStatus);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            
            // Views inside item_cart_selected
            tvProductName = itemView.findViewById(R.id.tvSelectedCartProductName);
            tvVariant = itemView.findViewById(R.id.tvSelectedCartVariant);
            tvQuantity = itemView.findViewById(R.id.tvSelectedCartQuantity);
            tvPrice = itemView.findViewById(R.id.tvSelectedCartPrice);
            ivProduct = itemView.findViewById(R.id.ivSelectedCartProductImage);

            tvTotalSummary = itemView.findViewById(R.id.tvOrderTotalSummary);
            tvGrandTotal = itemView.findViewById(R.id.tvOrderGrandTotal);
            tvDisclaimer = itemView.findViewById(R.id.tvOrderProcessingDisclaimer);
            btnAction = itemView.findViewById(R.id.btnOrderAction);
            btnReturn = itemView.findViewById(R.id.btnOrderReturn);
            layoutActionArea = itemView.findViewById(R.id.layoutOrderActionArea);
        }

        public void bind(OrderSummaryDto order) {
            Context context = itemView.getContext();
            // Brand
            tvBrandName.setText("Kanila Official");

            // Status mapping
            tvStatus.setText(getStatusText(order.getOrderStatus()));
            tvPaymentStatus.setText(getPaymentStatusText(order.getPaymentStatus()));
            
            // Order Info
            String orderNumberLabel = context.getString(R.string.order_detail_number_label);
            tvOrderNumber.setText(orderNumberLabel + ": " + order.getOrderNumber());
            tvOrderDate.setText(order.getPlacedAt());
            
            // Product info
            String productName = order.getFirstItemName();
            tvProductName.setText(productName);
            
            // Clean up variant name: (Product Name - Variant) -> Variant
            String variantDisplay = order.getFirstItemVariant();
            if (variantDisplay != null && productName != null) {
                if (variantDisplay.contains(productName + " - ")) {
                    variantDisplay = variantDisplay.replace(productName + " - ", "");
                } else if (variantDisplay.startsWith(productName)) {
                    String potential = variantDisplay.substring(productName.length()).trim();
                    if (!potential.isEmpty()) {
                        if (potential.startsWith("-") || potential.startsWith(":") || potential.startsWith("•")) {
                            variantDisplay = potential.substring(1).trim();
                        } else {
                            variantDisplay = potential;
                        }
                    }
                }
            }
            
            // Try to get cleaner variant from ItemPreview if available
            if (order.getItemPreviews() != null && !order.getItemPreviews().isEmpty()) {
                String previewVariant = order.getItemPreviews().get(0).getVariantName();
                if (previewVariant != null && !previewVariant.isEmpty()) {
                    if (productName != null && previewVariant.contains(productName + " - ")) {
                        previewVariant = previewVariant.replace(productName + " - ", "");
                    }
                    variantDisplay = previewVariant;
                }
            }

            if (variantDisplay == null || variantDisplay.isEmpty() || variantDisplay.equalsIgnoreCase(productName)) {
                variantDisplay = "Mặc định";
            }
            tvVariant.setText(variantDisplay);
            
            String qtyLabel = "Số lượng: x" + order.getTotalQuantity();
            tvQuantity.setText(qtyLabel);
            tvPrice.setText(formatPrice(order.getGrandTotalAmount()));
            
            // Load image using Glide
            String imageUrl = order.getFirstItemImageUrl();
            
            // If image is missing at root level, try to find it in itemPreviews matching the first item name
            if ((imageUrl == null || imageUrl.isEmpty()) && order.getItemPreviews() != null && !order.getItemPreviews().isEmpty()) {
                if (productName != null) {
                    for (OrderSummaryDto.ItemPreview preview : order.getItemPreviews()) {
                        if (productName.equals(preview.getProductName())) {
                            imageUrl = preview.getImageUrl();
                            break;
                        }
                    }
                }
                // Fallback to first preview if still null
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = order.getItemPreviews().get(0).getImageUrl();
                }
            }
            
            com.bumptech.glide.Glide.with(context)
                .load(imageUrl != null && !imageUrl.isEmpty() ? imageUrl : "")
                .placeholder(R.drawable.ic_product)
                .error(R.drawable.ic_product)
                .centerCrop()
                .into(ivProduct);

            // Summary
            String summaryLabel = String.format(Locale.getDefault(), "Tổng số tiền (%d sản phẩm): ", order.getTotalQuantity());
            tvTotalSummary.setText(summaryLabel);
            tvGrandTotal.setText(formatPrice(order.getGrandTotalAmount()));

            // Action Button & Disclaimer
            setupActionArea(order.getOrderStatus());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(order);
                }
            });

            btnAction.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onActionClick(order, btnAction.getText().toString());
                }
            });
        }

        private void setupActionArea(String status) {
            tvDisclaimer.setVisibility(View.GONE);
            btnAction.setEnabled(true);
            btnAction.setAlpha(1.0f);
            btnAction.setVisibility(View.VISIBLE);
            btnReturn.setVisibility(View.GONE);

            if (status == null) {
                btnAction.setVisibility(View.GONE);
                return;
            }

            switch (status) {
                case "pending":
                case "confirmed":
                    btnAction.setText("Liên hệ shop");
                    break;
                case "processing":
                    btnAction.setText("Đã nhận được hàng");
                    btnAction.setEnabled(false);
                    btnAction.setAlpha(0.5f);
                    tvDisclaimer.setVisibility(View.VISIBLE);
                    btnReturn.setVisibility(View.VISIBLE);
                    btnReturn.setText("Trả hàng/Hoàn tiền");
                    break;
                case "completed":
                    btnAction.setText("Đánh giá");
                    btnReturn.setVisibility(View.VISIBLE);
                    btnReturn.setText("Trả hàng/Hoàn tiền");
                    break;
                case "cancelled":
                case "returned":
                    btnAction.setText("Mua lại");
                    break;
                default:
                    btnAction.setVisibility(View.GONE);
                    break;
            }
        }

        private String getStatusText(String status) {
            if (status == null) return "";
            switch (status) {
                case "pending": return "Chờ xác nhận";
                case "confirmed": return "Chờ lấy hàng";
                case "processing": return "Chờ giao hàng";
                case "completed": return "Đã giao";
                case "cancelled": return "Đã hủy";
                case "returned": return "Trả hàng";
                default: return status;
            }
        }

        private String getPaymentStatusText(String status) {
            if (status == null) return "Chưa thanh toán";
            switch (status) {
                case "paid": return "Đã thanh toán";
                case "unpaid": return "Chưa thanh toán";
                case "pending": return "Chờ thanh toán";
                case "refunded": return "Đã hoàn tiền";
                default: return status;
            }
        }

        private String formatPrice(double price) {
            return String.format(Locale.getDefault(), "%,.0fđ", price).replace(",", ".");
        }
    }
}
