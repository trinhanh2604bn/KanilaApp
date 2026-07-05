package com.example.frontend.feature.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.order.OrderDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
    private List<OrderDto> orders = new ArrayList<>();
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderDto order);
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<OrderDto> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDto order = orders.get(position);
        holder.tvOrderNumber.setText(order.getOrderNumber());
        holder.tvOrderStatus.setText(order.getOrderStatus());
        holder.tvOrderTotalAmount.setText(formatPrice(order.getTotalAmount()));

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            OrderDto.OrderItemDto firstItem = order.getItems().get(0);
            holder.tvOrderFirstProductName.setText(firstItem.getProductName());
            Glide.with(holder.ivOrderFirstProduct.getContext())
                    .load(firstItem.getImageUrl())
                    .placeholder(R.drawable.ic_product)
                    .into(holder.ivOrderFirstProduct);

            if (order.getItems().size() > 1) {
                holder.tvOrderItemsCount.setText("và " + (order.getItems().size() - 1) + " sản phẩm khác");
                holder.tvOrderItemsCount.setVisibility(View.VISIBLE);
            } else {
                holder.tvOrderItemsCount.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNumber, tvOrderStatus, tvOrderFirstProductName, tvOrderItemsCount, tvOrderTotalAmount;
        ImageView ivOrderFirstProduct;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderFirstProductName = itemView.findViewById(R.id.tvOrderFirstProductName);
            tvOrderItemsCount = itemView.findViewById(R.id.tvOrderItemsCount);
            tvOrderTotalAmount = itemView.findViewById(R.id.tvOrderTotalAmount);
            ivOrderFirstProduct = itemView.findViewById(R.id.ivOrderFirstProduct);
        }
    }
}
