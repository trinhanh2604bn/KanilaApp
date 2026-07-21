package com.example.frontend.feature.ar.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.feature.ar.data.ArShade;

public class ArShadeAdapter extends ListAdapter<ArShade, ArShadeAdapter.ShadeViewHolder> {

    private final OnShadeClickListener listener;
    private ArShade selectedShade;

    public interface OnShadeClickListener {
        void onShadeClick(ArShade shade);
    }

    public ArShadeAdapter(OnShadeClickListener listener) {
        super(new DiffUtil.ItemCallback<ArShade>() {
            @Override
            public boolean areItemsTheSame(@NonNull ArShade oldItem, @NonNull ArShade newItem) {
                return oldItem.getVariantId().equals(newItem.getVariantId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ArShade oldItem, @NonNull ArShade newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener;
    }

    public void setSelectedShade(ArShade shade) {
        this.selectedShade = shade;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShadeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ar_shade, parent, false);
        return new ShadeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShadeViewHolder holder, int position) {
        ArShade shade = getItem(position);
        boolean isSelected = selectedShade != null && selectedShade.getVariantId().equals(shade.getVariantId());
        holder.bind(shade, isSelected, listener);
    }

    static class ShadeViewHolder extends RecyclerView.ViewHolder {
        private final View colorSwatch;
        private final View selectedRing;
        private final View outOfStockOverlay;

        public ShadeViewHolder(@NonNull View itemView) {
            super(itemView);
            colorSwatch = itemView.findViewById(R.id.colorSwatch);
            selectedRing = itemView.findViewById(R.id.selectedRing);
            outOfStockOverlay = itemView.findViewById(R.id.outOfStockOverlay);
        }

        public void bind(ArShade shade, boolean isSelected, OnShadeClickListener listener) {
            int parsedColor = ShadeColorParser.parseOrFallback(shade.getShadeHex());
            colorSwatch.setBackgroundTintList(ColorStateList.valueOf(parsedColor));

            selectedRing.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            outOfStockOverlay.setVisibility(shade.getInStock() ? View.GONE : View.VISIBLE);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onShadeClick(shade);
                }
            });
            
            String desc = shade.getVariantName();
            if (!shade.getInStock()) {
                desc += " (Hết hàng)";
            }
            itemView.setContentDescription(desc);
        }
    }
}
