package com.example.frontend.ui.category;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.frontend.R;
import com.example.frontend.data.model.product.ProductFilterParams;
import com.example.frontend.data.repository.BrandRepository;
import com.example.frontend.model.Brand;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

public class FilterBottomSheetDialog extends BottomSheetDialogFragment {

    private OnFilterAppliedListener listener;
    private ProductFilterParams initialFilterParams;
    private BrandRepository brandRepository;

    public interface OnFilterAppliedListener {
        void onFilterApplied(ProductFilterParams filterParams);
    }

    public void setOnFilterAppliedListener(OnFilterAppliedListener listener) {
        this.listener = listener;
    }

    public void setInitialFilterParams(ProductFilterParams params) {
        this.initialFilterParams = params;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        brandRepository = new BrandRepository(requireContext());

        setupHeader(view);
        setupSkinTypeChips(view);
        setupToneChips(view);
        setupPriceQuickChips(view);
        setupActionButtons(view);
        setupIngredientTags(view);
        loadBrands(view);

        if (initialFilterParams != null) {
            applyInitialParams(view);
        }
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

        ChipGroup priceGroup = root.findViewById(R.id.chipGroupPrice);
        if (priceGroup != null) {
            priceGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.chipPrice0To100) {
                    edtMin.setText("0");
                    edtMax.setText("100000");
                } else if (checkedId == R.id.chipPrice100To300) {
                    edtMin.setText("100000");
                    edtMax.setText("300000");
                } else if (checkedId == R.id.chipPrice300To500) {
                    edtMin.setText("300000");
                    edtMax.setText("500000");
                }
            });
        }
    }

    private void setupIngredientTags(View root) {
        View chipGlycerin = root.findViewById(R.id.chipIngGlycerin);
        if (chipGlycerin != null) chipGlycerin.setTag("Glycerin");

        View chipTocopherol = root.findViewById(R.id.chipIngTocopherol);
        if (chipTocopherol != null) chipTocopherol.setTag("Tocopherol");

        View chipMica = root.findViewById(R.id.chipIngMica);
        if (chipMica != null) chipMica.setTag("Mica");
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

    private void loadBrands(View root) {
        ChipGroup brandGroup = root.findViewById(R.id.chipGroupBrands);
        if (brandGroup == null) return;

        brandRepository.getBrands().observe(getViewLifecycleOwner(), result -> {
            if (result == null || result.status != com.example.frontend.data.remote.NetworkResult.Status.SUCCESS) return;

            List<Brand> brands = result.data;
            if (brands == null || brands.isEmpty()) return;

            brandGroup.removeAllViews();
            for (Brand brand : brands) {
                Chip chip = (Chip) LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_filter_chip, brandGroup, false);
                chip.setText(brand.getBrandName());
                chip.setTag(brand.getId());
                chip.setCheckable(true);
                
                if (initialFilterParams != null && initialFilterParams.brandIds.contains(brand.getId())) {
                    chip.setChecked(true);
                }
                
                brandGroup.addView(chip);
            }
        });
    }

    private void applyInitialParams(View root) {
        EditText edtMin = root.findViewById(R.id.edtMinPrice);
        EditText edtMax = root.findViewById(R.id.edtMaxPrice);
        if (edtMin != null) edtMin.setText(initialFilterParams.minPrice);
        if (edtMax != null) edtMax.setText(initialFilterParams.maxPrice);

        SwitchMaterial switchAr = root.findViewById(R.id.switchArOnly);
        if (switchAr != null) switchAr.setChecked(initialFilterParams.arOnly);

        SwitchMaterial switchSensitive = root.findViewById(R.id.switchSensitiveOnly);
        if (switchSensitive != null) switchSensitive.setChecked(initialFilterParams.sensitiveOnly);

        SwitchMaterial switchBestSeller = root.findViewById(R.id.switchBestSellerOnly);
        if (switchBestSeller != null) switchBestSeller.setChecked(initialFilterParams.bestSellerOnly);

        // Skin types
        if (initialFilterParams.skinTypes.contains("oily")) {
            View v = root.findViewById(R.id.chipSkinOily);
            if (v != null) v.setSelected(true);
        }
        if (initialFilterParams.skinTypes.contains("dry")) {
            View v = root.findViewById(R.id.chipSkinDry);
            if (v != null) v.setSelected(true);
        }
        if (initialFilterParams.skinTypes.contains("normal")) {
            View v = root.findViewById(R.id.chipSkinNormal);
            if (v != null) v.setSelected(true);
        }
        if (initialFilterParams.skinTypes.contains("sensitive")) {
            View v = root.findViewById(R.id.chipSkinSensitive);
            if (v != null) v.setSelected(true);
        }

        // Tones
        if (initialFilterParams.tones.contains("light")) {
            View v = root.findViewById(R.id.chipToneLight);
            if (v != null) v.setSelected(true);
        }
        if (initialFilterParams.tones.contains("medium")) {
            View v = root.findViewById(R.id.chipToneMedium);
            if (v != null) v.setSelected(true);
        }
        if (initialFilterParams.tones.contains("dark")) {
            View v = root.findViewById(R.id.chipToneDark);
            if (v != null) v.setSelected(true);
        }

        // Rating
        if (!TextUtils.isEmpty(initialFilterParams.minRating)) {
            ChipGroup ratingGroup = root.findViewById(R.id.chipGroupRating);
            if (ratingGroup != null) {
                int rating = Integer.parseInt(initialFilterParams.minRating);
                if (rating == 5) ratingGroup.check(R.id.chipRating5);
                else if (rating == 4) ratingGroup.check(R.id.chipRating4Plus);
                else if (rating == 3) ratingGroup.check(R.id.chipRating3Plus);
                else if (rating == 2) ratingGroup.check(R.id.chipRating2Plus);
                else if (rating == 1) ratingGroup.check(R.id.chipRating1Plus);
            }
        }

        // Ingredients
        ChipGroup ingGroup = root.findViewById(R.id.chipGroupIngredients);
        if (ingGroup != null) {
            for (int i = 0; i < ingGroup.getChildCount(); i++) {
                View v = ingGroup.getChildAt(i);
                if (v instanceof Chip) {
                    Chip chip = (Chip) v;
                    if (chip.getTag() != null && initialFilterParams.ingredients.contains(chip.getTag().toString())) {
                        chip.setChecked(true);
                    }
                }
            }
        }

        // Concerns
        ChipGroup concernGroup = root.findViewById(R.id.chipGroupConcerns);
        if (concernGroup != null) {
            for (int i = 0; i < concernGroup.getChildCount(); i++) {
                View v = concernGroup.getChildAt(i);
                if (v instanceof Chip) {
                    Chip chip = (Chip) v;
                    String val = getConcernValue(chip.getId());
                    if (initialFilterParams.concerns.contains(val)) {
                        chip.setChecked(true);
                    }
                }
            }
        }
    }

    private void resetFilters(View root) {
        EditText edtMin = root.findViewById(R.id.edtMinPrice);
        EditText edtMax = root.findViewById(R.id.edtMaxPrice);
        if (edtMin != null) edtMin.setText("");
        if (edtMax != null) edtMax.setText("");

        SwitchMaterial switchAr = root.findViewById(R.id.switchArOnly);
        if (switchAr != null) switchAr.setChecked(false);
        
        SwitchMaterial switchSensitive = root.findViewById(R.id.switchSensitiveOnly);
        if (switchSensitive != null) switchSensitive.setChecked(false);

        SwitchMaterial switchBestSeller = root.findViewById(R.id.switchBestSellerOnly);
        if (switchBestSeller != null) switchBestSeller.setChecked(false);

        int[] customChipIds = {
                R.id.chipSkinOily, R.id.chipSkinDry, R.id.chipSkinNormal, R.id.chipSkinSensitive,
                R.id.chipToneLight, R.id.chipToneMedium, R.id.chipToneDark
        };
        for (int id : customChipIds) {
            View v = root.findViewById(id);
            if (v != null) v.setSelected(false);
        }

        int[] groupIds = {R.id.chipGroupPrice, R.id.chipGroupRating, R.id.chipGroupBrands, R.id.chipGroupIngredients, R.id.chipGroupConcerns};
        for (int id : groupIds) {
            ChipGroup group = root.findViewById(id);
            if (group != null) group.clearCheck();
        }
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
                    Toast.makeText(getContext(), "Giá tối thiểu không thể lớn hơn giá tối đa", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException ignored) {}
        }

        ProductFilterParams params = new ProductFilterParams();
        params.minPrice = minStr;
        params.maxPrice = maxStr;

        SwitchMaterial switchAr = root.findViewById(R.id.switchArOnly);
        params.arOnly = switchAr != null && switchAr.isChecked();

        SwitchMaterial switchSensitive = root.findViewById(R.id.switchSensitiveOnly);
        params.sensitiveOnly = switchSensitive != null && switchSensitive.isChecked();

        SwitchMaterial switchBestSeller = root.findViewById(R.id.switchBestSellerOnly);
        params.bestSellerOnly = switchBestSeller != null && switchBestSeller.isChecked();

        // Skin Types
        if (root.findViewById(R.id.chipSkinOily).isSelected()) params.skinTypes.add("oily");
        if (root.findViewById(R.id.chipSkinDry).isSelected()) params.skinTypes.add("dry");
        if (root.findViewById(R.id.chipSkinNormal).isSelected()) params.skinTypes.add("normal");
        if (root.findViewById(R.id.chipSkinSensitive).isSelected()) params.skinTypes.add("sensitive");

        // Tones
        if (root.findViewById(R.id.chipToneLight).isSelected()) params.tones.add("light");
        if (root.findViewById(R.id.chipToneMedium).isSelected()) params.tones.add("medium");
        if (root.findViewById(R.id.chipToneDark).isSelected()) params.tones.add("dark");

        // Rating
        ChipGroup ratingGroup = root.findViewById(R.id.chipGroupRating);
        if (ratingGroup != null) {
            int checkedId = ratingGroup.getCheckedChipId();
            if (checkedId == R.id.chipRating5) params.minRating = "5";
            else if (checkedId == R.id.chipRating4Plus) params.minRating = "4";
            else if (checkedId == R.id.chipRating3Plus) params.minRating = "3";
            else if (checkedId == R.id.chipRating2Plus) params.minRating = "2";
            else if (checkedId == R.id.chipRating1Plus) params.minRating = "1";
        }

        // Brands
        ChipGroup brandGroup = root.findViewById(R.id.chipGroupBrands);
        if (brandGroup != null) {
            List<Integer> checkedIds = brandGroup.getCheckedChipIds();
            for (Integer id : checkedIds) {
                Chip chip = brandGroup.findViewById(id);
                if (chip != null && chip.getTag() != null) {
                    params.brandIds.add(chip.getTag().toString());
                }
            }
        }

        // Ingredients
        ChipGroup ingGroup = root.findViewById(R.id.chipGroupIngredients);
        if (ingGroup != null) {
            List<Integer> checkedIds = ingGroup.getCheckedChipIds();
            for (Integer id : checkedIds) {
                Chip chip = ingGroup.findViewById(id);
                if (chip != null && chip.getTag() != null) {
                    params.ingredients.add(chip.getTag().toString());
                }
            }
        }

        // Concerns
        ChipGroup concernGroup = root.findViewById(R.id.chipGroupConcerns);
        if (concernGroup != null) {
            List<Integer> checkedIds = concernGroup.getCheckedChipIds();
            for (Integer id : checkedIds) {
                params.concerns.add(getConcernValue(id));
            }
        }

        if (listener != null) {
            listener.onFilterApplied(params);
        }
        dismiss();
    }

    private String getConcernValue(int id) {
        if (id == R.id.chipConcernLongWear) return "long_wear";
        if (id == R.id.chipConcernSmoothFinish) return "smooth_finish";
        if (id == R.id.chipConcernPhotoReady) return "photo_ready";
        return "";
    }
}
