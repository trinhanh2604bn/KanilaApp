package ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;

import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ArrangeBottomSheetDialog extends BottomSheetDialogFragment {

    public enum SortOption {
        BEST_MATCH,
        BEST_SELLER,
        PRICE_LOW_TO_HIGH,
        PRICE_HIGH_TO_LOW,
        NEWEST
    }

    private OnSortOptionSelectedListener listener;
    private SortOption selectedOption = SortOption.BEST_MATCH;

    private RadioButton rbSortBestMatch, rbSortBestSeller, rbSortPriceLowToHigh, rbSortPriceHighToLow, rbSortNewest;
    private TextView tvSortBestMatchTitle, tvSortBestSellerTitle, tvSortPriceLowToHighTitle, tvSortPriceHighToLowTitle, tvSortNewestTitle;

    public interface OnSortOptionSelectedListener {
        void onSortOptionSelected(SortOption option);
    }

    public void setOnSortOptionSelectedListener(OnSortOptionSelectedListener listener) {
        this.listener = listener;
    }

    public void setSelectedOption(SortOption option) {
        this.selectedOption = option;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_arrange, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupListeners(view);
        updateSelection(selectedOption);
    }

    private void initViews(View root) {
        rbSortBestMatch = root.findViewById(R.id.rbSortBestMatch);
        rbSortBestSeller = root.findViewById(R.id.rbSortBestSeller);
        rbSortPriceLowToHigh = root.findViewById(R.id.rbSortPriceLowToHigh);
        rbSortPriceHighToLow = root.findViewById(R.id.rbSortPriceHighToLow);
        rbSortNewest = root.findViewById(R.id.rbSortNewest);

        tvSortBestMatchTitle = root.findViewById(R.id.tvSortBestMatchTitle);
        tvSortBestSellerTitle = root.findViewById(R.id.tvSortBestSellerTitle);
        tvSortPriceLowToHighTitle = root.findViewById(R.id.tvSortPriceLowToHighTitle);
        tvSortPriceHighToLowTitle = root.findViewById(R.id.tvSortPriceHighToLowTitle);
        tvSortNewestTitle = root.findViewById(R.id.tvSortNewestTitle);
    }

    private void setupListeners(View root) {
        root.findViewById(R.id.btnCloseArrange).setOnClickListener(v -> dismiss());

        root.findViewById(R.id.layoutSortBestMatch).setOnClickListener(v -> updateSelection(SortOption.BEST_MATCH));
        root.findViewById(R.id.layoutSortBestSeller).setOnClickListener(v -> updateSelection(SortOption.BEST_SELLER));
        root.findViewById(R.id.layoutSortPriceLowToHigh).setOnClickListener(v -> updateSelection(SortOption.PRICE_LOW_TO_HIGH));
        root.findViewById(R.id.layoutSortPriceHighToLow).setOnClickListener(v -> updateSelection(SortOption.PRICE_HIGH_TO_LOW));
        root.findViewById(R.id.layoutSortNewest).setOnClickListener(v -> updateSelection(SortOption.NEWEST));

        root.findViewById(R.id.btnApplyArrange).setOnClickListener(v -> {
            if (listener != null) {
                listener.onSortOptionSelected(selectedOption);
            }
            dismiss();
        });
    }

    private void updateSelection(SortOption option) {
        this.selectedOption = option;

        rbSortBestMatch.setChecked(option == SortOption.BEST_MATCH);
        rbSortBestSeller.setChecked(option == SortOption.BEST_SELLER);
        rbSortPriceLowToHigh.setChecked(option == SortOption.PRICE_LOW_TO_HIGH);
        rbSortPriceHighToLow.setChecked(option == SortOption.PRICE_HIGH_TO_LOW);
        rbSortNewest.setChecked(option == SortOption.NEWEST);

        if (getContext() == null) return;

        int accentColor = ContextCompat.getColor(getContext(), R.color.accent_dark);
        int mainTextColor = ContextCompat.getColor(getContext(), R.color.text_main);

        tvSortBestMatchTitle.setTextColor(option == SortOption.BEST_MATCH ? accentColor : mainTextColor);
        tvSortBestSellerTitle.setTextColor(option == SortOption.BEST_SELLER ? accentColor : mainTextColor);
        tvSortPriceLowToHighTitle.setTextColor(option == SortOption.PRICE_LOW_TO_HIGH ? accentColor : mainTextColor);
        tvSortPriceHighToLowTitle.setTextColor(option == SortOption.PRICE_HIGH_TO_LOW ? accentColor : mainTextColor);
        tvSortNewestTitle.setTextColor(option == SortOption.NEWEST ? accentColor : mainTextColor);
    }
}
