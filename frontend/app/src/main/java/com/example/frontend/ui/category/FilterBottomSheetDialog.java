package com.example.frontend.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.frontend.R;
import com.example.frontend.utils.ToastHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class FilterBottomSheetDialog extends BottomSheetDialogFragment {

    private OnFilterAppliedListener listener;

    public interface OnFilterAppliedListener {
        void onFilterApplied(FilterState filterState);
    }

    public static class FilterState {
        public List<Integer> selectedSkinTypes = new ArrayList<>();
        public List<Integer> selectedTones = new ArrayList<>();
        public String minPrice = "";
        public String maxPrice = "";
        public int selectedRating = -1;
        public List<Integer> selectedBrands = new ArrayList<>();
        public List<Integer> selectedIngredients = new ArrayList<>();
        public boolean arOnly = false;
    }

    public void setOnFilterAppliedListener(OnFilterAppliedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHeader(view);
        setupSkinTypeChips(view);
        setupToneChips(view);
        setupPriceQuickChips(view);
        setupActionButtons(view);
    }

    private void setupHeader(View root) {
        View btnClose = root.findViewById(R.id.btnCloseFilter);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dismiss());
        }
    }

    private void setupSkinTypeChips(View root) {
        int[] ids = {R.id.chipSkinOily, R.id.chipSkinDry, R.id.chipSkinNormal, R.id.chipSkinSensitive};
        for (int id : ids) {
            View chip = root.findViewById(id);
            if (chip != null) {
                chip.setOnClickListener(v -> v.setSelected(!v.isSelected()));
            }
        }
    }

    private void setupToneChips(View root) {
        int[] ids = {R.id.chipToneLight, R.id.chipToneMedium, R.id.chipToneDark};
        for (int id : ids) {
            View chip = root.findViewById(id);
            if (chip != null) {
                chip.setOnClickListener(v -> v.setSelected(!v.isSelected()));
            }
        }
    }

    private void setupPriceQuickChips(View root) {
        EditText edtMin = root.findViewById(R.id.edtMinPrice);
        EditText edtMax = root.findViewById(R.id.edtMaxPrice);

        View chip0To100 = root.findViewById(R.id.chipPrice0To100);
        if (chip0To100 != null) {
            chip0To100.setOnClickListener(v -> {
                edtMin.setText("0");
                edtMax.setText("100000");
            });
        }

        View chip100To300 = root.findViewById(R.id.chipPrice100To300);
        if (chip100To300 != null) {
            chip100To300.setOnClickListener(v -> {
                edtMin.setText("100000");
                edtMax.setText("300000");
            });
        }

        View chip300To500 = root.findViewById(R.id.chipPrice300To500);
        if (chip300To500 != null) {
            chip300To500.setOnClickListener(v -> {
                edtMin.setText("300000");
                edtMax.setText("500000");
            });
        }
    }

    private void setupActionButtons(View root) {
        View btnReset = root.findViewById(R.id.btnResetFilter);
        if (btnReset != null) {
            btnReset.setOnClickListener(v -> resetFilters(root));
        }

        View btnApply = root.findViewById(R.id.btnApplyFilter);
        if (btnApply != null) {
            btnApply.setOnClickListener(v -> applyFilters(root));
        }
    }

    private void resetFilters(View root) {
        EditText edtMin = root.findViewById(R.id.edtMinPrice);
        EditText edtMax = root.findViewById(R.id.edtMaxPrice);
        if (edtMin != null) edtMin.setText("");
        if (edtMax != null) edtMax.setText("");

        SwitchMaterial switchAr = root.findViewById(R.id.switchArOnly);
        if (switchAr != null) switchAr.setChecked(false);

        // Reset custom chips
        int[] customChipIds = {
                R.id.chipSkinOily, R.id.chipSkinDry, R.id.chipSkinNormal, R.id.chipSkinSensitive,
                R.id.chipToneLight, R.id.chipToneMedium, R.id.chipToneDark
        };
        for (int id : customChipIds) {
            View v = root.findViewById(id);
            if (v != null) v.setSelected(false);
        }

        ViewGroup content = root.findViewById(R.id.layoutFilterContent);
        if (content != null) {
            for (int i = 0; i < content.getChildCount(); i++) {
                View child = content.getChildAt(i);
                if (child instanceof ChipGroup) {
                    ((ChipGroup) child).clearCheck();
                }
            }
        }
        
        ToastHelper.showShort(getContext(), R.string.filter_reset);
    }

    private void applyFilters(View root) {
        EditText edtMin = root.findViewById(R.id.edtMinPrice);
        EditText edtMax = root.findViewById(R.id.edtMaxPrice);

        String minStr = edtMin != null ? edtMin.getText().toString().trim() : "";
        String maxStr = edtMax != null ? edtMax.getText().toString().trim() : "";

        if (!minStr.isEmpty() && !maxStr.isEmpty()) {
            try {
                long min = Long.parseLong(minStr);
                long max = Long.parseLong(maxStr);
                if (min > max) {
                    ToastHelper.showShort(getContext(), "Giá tối thiểu không thể lớn hơn giá tối đa");
                    return;
                }
            } catch (NumberFormatException ignored) {}
        }

        if (listener != null) {
            FilterState state = new FilterState();
            state.minPrice = minStr;
            state.maxPrice = maxStr;
            
            SwitchMaterial switchAr = root.findViewById(R.id.switchArOnly);
            state.arOnly = switchAr != null && switchAr.isChecked();
            
            // Note: In a real app, we would collect IDs from all ChipGroups here
            // Example: state.selectedBrands = chipGroupBrands.getCheckedChipIds();
            
            listener.onFilterApplied(state);
        }
        dismiss();
    }
}
