package ui.commerce;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.frontend.R;
import com.example.frontend.data.model.address.AddressDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.checkout.CheckoutAddressViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutAddressAddFragment extends Fragment {

    private EditText edtFullName, edtPhone, edtDetail;
    private TextView tvProvince, tvDistrict, tvWard, tvCounter;
    private ChipGroup chipGroupTag;
    private SwitchMaterial switchDefault;
    private View btnSave;

    private String selectedProvince;
    private String selectedDistrict;
    private String selectedWard;
    private String addressId;
    private Map<String, Map<String, List<String>>> locationMap;
    private CheckoutAddressViewModel viewModel;
    private com.example.frontend.feature.checkout.CheckoutViewModel checkoutViewModel;
    private boolean isGuest = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_checkout_address_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CheckoutAddressViewModel.class);
        checkoutViewModel = new ViewModelProvider(requireActivity()).get(com.example.frontend.feature.checkout.CheckoutViewModel.class);
        
        if (getArguments() != null) {
            addressId = getArguments().getString("address_id");
            isGuest = getArguments().getBoolean("is_guest", false);
            
            // Handle guest edit
            if (isGuest && getArguments().containsKey("guest_address")) {
                AddressDto guestAddr = BundleCompat.getSerializable(getArguments(), "guest_address", AddressDto.class);
                if (guestAddr != null) {
                    populateViews(guestAddr);
                }
            }
        }

        initData();
        initViews(view);
        setupHeader(view);
        setupTexts(view);
        setupListeners(view);
        observeViewModel();
        
        if (addressId != null) {
            loadExistingAddress();
        }

        if (isGuest) {
            if (switchDefault != null) switchDefault.setVisibility(View.GONE);
            View layoutDefault = view.findViewById(R.id.layoutAddressDefault);
            if (layoutDefault != null) layoutDefault.setVisibility(View.GONE);
        }
    }

    private void loadExistingAddress() {
        // Find the address in the already loaded list from ViewModel
        NetworkResult<List<AddressDto>> result = viewModel.getAddressResult().getValue();
        if (result != null && result.data != null) {
            for (AddressDto address : result.data) {
                if (address.getId() != null && address.getId().equals(addressId)) {
                    populateViews(address);
                    break;
                }
            }
        }
    }

    private void populateViews(AddressDto address) {
        if (edtFullName != null) edtFullName.setText(address.getRecipientName());
        if (edtPhone != null) edtPhone.setText(address.getPhone());
        if (edtDetail != null) edtDetail.setText(address.getAddressLine1());
        
        selectedProvince = address.getCity();
        selectedDistrict = address.getDistrict();
        selectedWard = address.getWard();
        
        if (tvProvince != null) tvProvince.setText(selectedProvince != null ? selectedProvince : "Chọn tỉnh/thành phố");
        if (tvDistrict != null) tvDistrict.setText(selectedDistrict != null ? selectedDistrict : "Chọn quận/huyện");
        if (tvWard != null) tvWard.setText(selectedWard != null ? selectedWard : "Chọn phường/xã");
        
        if (switchDefault != null) switchDefault.setChecked(address.isDefaultShipping());
        
        // Handle tags
        if (chipGroupTag != null && address.getAddressType() != null) {
            String type = address.getAddressType().toLowerCase();
            if (type.contains("home")) {
                chipGroupTag.check(R.id.chipAddressHome);
            } else if (type.contains("office")) {
                chipGroupTag.check(R.id.chipAddressOffice);
            } else {
                chipGroupTag.check(R.id.chipAddressOther);
            }
        }
    }

    private void observeViewModel() {
        viewModel.getSaveResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    btnSave.setEnabled(false);
                    break;
                case SUCCESS:
                    Toast.makeText(getContext(), "Địa chỉ đã được lưu", Toast.LENGTH_SHORT).show();
                    
                    // If saving from checkout, update the selected address
                    if (result.data != null) {
                        checkoutViewModel.setSelectedAddress(result.data);
                    }
                    
                    viewModel.loadCustomerAddresses(); // Refresh list
                    viewModel.clearSaveResult();
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                    break;
                case ERROR:
                    btnSave.setEnabled(true);
                    showError(result.message);
                    break;
            }
        });
    }

    private void initData() {
        locationMap = new HashMap<>();
        
        Map<String, List<String>> hcmDistricts = new HashMap<>();
        hcmDistricts.put("Quận 1", Arrays.asList("Phường Bến Thành", "Phường Đa Kao", "Phường Tân Định"));
        hcmDistricts.put("Quận 7", Arrays.asList("Phường Tân Phong", "Phường Tân Kiểng", "Phường Phú Mỹ"));
        hcmDistricts.put("Thành phố Thủ Đức", Arrays.asList("Phường Linh Trung", "Phường Linh Tây", "Phường Hiệp Phú"));
        locationMap.put("TP. Hồ Chí Minh", hcmDistricts);

        Map<String, List<String>> hnDistricts = new HashMap<>();
        hnDistricts.put("Quận Hoàn Kiếm", Arrays.asList("Phường Hàng Đào", "Phường Tràng Tiền", "Phường Lý Thái Tổ"));
        hnDistricts.put("Quận Cầu Giấy", Arrays.asList("Phường Dịch Vọng", "Phường Yên Hòa", "Phường Quan Hoa"));
        locationMap.put("Hà Nội", hnDistricts);
        
        Map<String, List<String>> dnDistricts = new HashMap<>();
        dnDistricts.put("Quận Hải Châu", Arrays.asList("Phường Hòa Thuận Đông", "Phường Phước Ninh"));
        locationMap.put("Đà Nẵng", dnDistricts);
    }

    private void initViews(View view) {
        edtFullName = view.findViewById(R.id.edtAddressFullName);
        edtPhone = view.findViewById(R.id.edtAddressPhone);
        edtDetail = view.findViewById(R.id.edtAddressDetail);
        tvProvince = view.findViewById(R.id.edtAddressProvince);
        tvDistrict = view.findViewById(R.id.edtAddressDistrict);
        tvWard = view.findViewById(R.id.edtAddressWard);
        tvCounter = view.findViewById(R.id.tvAddressCounter);
        chipGroupTag = view.findViewById(R.id.chipGroupAddressTag);
        switchDefault = view.findViewById(R.id.switchAddressDefault);
        btnSave = view.findViewById(R.id.btnSaveAddress);
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutTopBar);
        if (header == null) return;

        TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) {
            if (addressId != null) {
                tvTitle.setText("Sửa địa chỉ");
            } else {
                tvTitle.setText(R.string.address_book_add_new);
            }
        }

        View btnSearch = header.findViewById(R.id.btnTopBarSearch);
        if (btnSearch != null) {
            btnSearch.setVisibility(View.GONE);
        }

        View btnBack = header.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }
    }

    private void setupTexts(View view) {
        TextView tvLabelFullName = view.findViewById(R.id.tvLabelFullName);
        if (tvLabelFullName != null) tvLabelFullName.setText(R.string.auth_full_name_label);
        if (edtFullName != null) edtFullName.setHint(R.string.auth_full_name_hint);

        TextView tvLabelPhone = view.findViewById(R.id.tvLabelPhone);
        if (tvLabelPhone != null) tvLabelPhone.setText(R.string.auth_phone_label);
        if (edtPhone != null) edtPhone.setHint(R.string.auth_phone_hint);

        TextView tvLabelProvince = view.findViewById(R.id.tvLabelProvince);
        if (tvLabelProvince != null) tvLabelProvince.setText("Tỉnh/Thành phố");
        
        TextView tvLabelDistrict = view.findViewById(R.id.tvLabelDistrict);
        if (tvLabelDistrict != null) tvLabelDistrict.setText("Quận/Huyện");

        TextView tvLabelWard = view.findViewById(R.id.tvLabelWard);
        if (tvLabelWard != null) tvLabelWard.setText("Phường/Xã");

        TextView tvLabelDetail = view.findViewById(R.id.tvLabelDetail);
        if (tvLabelDetail != null) tvLabelDetail.setText("Địa chỉ chi tiết");

        TextView tvUseCurrentLocation = view.findViewById(R.id.tvUseCurrentLocation);
        if (tvUseCurrentLocation != null) tvUseCurrentLocation.setText("Dùng vị trí hiện tại");

        TextView tvLabelTag = view.findViewById(R.id.tvLabelTag);
        if (tvLabelTag != null) tvLabelTag.setText("Gắn thẻ địa chỉ");

        Chip chipHome = view.findViewById(R.id.chipAddressHome);
        if (chipHome != null) chipHome.setText("Nhà riêng");

        Chip chipOffice = view.findViewById(R.id.chipAddressOffice);
        if (chipOffice != null) chipOffice.setText("Văn phòng");

        Chip chipOther = view.findViewById(R.id.chipAddressOther);
        if (chipOther != null) chipOther.setText("Khác");

        TextView tvLabelDefault = view.findViewById(R.id.tvLabelDefault);
        if (tvLabelDefault != null) tvLabelDefault.setText("Đặt làm mặc định");

        if (btnSave instanceof MaterialButton) {
            ((MaterialButton) btnSave).setText("Lưu địa chỉ");
        }
    }

    private void setupListeners(View view) {
        if (edtDetail != null) {
            edtDetail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (tvCounter != null) tvCounter.setText(s.length() + "/255");
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        View layoutProvince = view.findViewById(R.id.layoutAddressProvince);
        if (layoutProvince != null) {
            layoutProvince.setOnClickListener(v -> {
                List<String> provinces = new ArrayList<>(locationMap.keySet());
                AddressPickerBottomSheet picker = new AddressPickerBottomSheet(getContext(), "Chọn tỉnh/thành phố", provinces, item -> {
                    selectedProvince = item;
                    selectedDistrict = null;
                    selectedWard = null;
                    if (tvProvince != null) tvProvince.setText(selectedProvince);
                    if (tvDistrict != null) tvDistrict.setText("Chọn quận/huyện");
                    if (tvWard != null) tvWard.setText("Chọn phường/xã");
                });
                picker.show();
            });
        }

        View layoutDistrict = view.findViewById(R.id.layoutAddressDistrict);
        if (layoutDistrict != null) {
            layoutDistrict.setOnClickListener(v -> {
                if (selectedProvince == null) {
                    showError("Vui lòng chọn tỉnh/thành phố trước");
                    return;
                }
                Map<String, List<String>> districts = locationMap.get(selectedProvince);
                if (districts != null) {
                    List<String> districtNames = new ArrayList<>(districts.keySet());
                    AddressPickerBottomSheet picker = new AddressPickerBottomSheet(getContext(), "Chọn quận/huyện", districtNames, item -> {
                        selectedDistrict = item;
                        selectedWard = null;
                        if (tvDistrict != null) tvDistrict.setText(selectedDistrict);
                        if (tvWard != null) tvWard.setText("Chọn phường/xã");
                    });
                    picker.show();
                }
            });
        }

        View layoutWard = view.findViewById(R.id.layoutAddressWard);
        if (layoutWard != null) {
            layoutWard.setOnClickListener(v -> {
                if (selectedProvince == null || selectedDistrict == null) {
                    showError("Vui lòng chọn quận/huyện trước");
                    return;
                }
                Map<String, List<String>> districts = locationMap.get(selectedProvince);
                if (districts != null) {
                    List<String> wards = districts.get(selectedDistrict);
                    if (wards != null) {
                        AddressPickerBottomSheet picker = new AddressPickerBottomSheet(getContext(), "Chọn phường/xã", wards, item -> {
                            selectedWard = item;
                            if (tvWard != null) tvWard.setText(selectedWard);
                        });
                        picker.show();
                    }
                }
            });
        }

        View layoutLocation = view.findViewById(R.id.layoutUseCurrentLocation);
        if (layoutLocation != null) {
            layoutLocation.setOnClickListener(v -> {
                // TODO: Use current location logic
            });
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                if (validateInputs()) {
                    if (isGuest) {
                        saveGuestAddress();
                    } else {
                        saveAddress();
                    }
                }
            });
        }
    }

    private void saveGuestAddress() {
        AddressDto address = new AddressDto();
        address.setRecipientName(edtFullName.getText().toString().trim());
        address.setPhone(edtPhone.getText().toString().trim());
        address.setCity(selectedProvince);
        address.setDistrict(selectedDistrict);
        address.setWard(selectedWard);
        address.setAddressLine1(edtDetail.getText().toString().trim());
        
        checkoutViewModel.setSelectedAddress(address);
        Toast.makeText(getContext(), "Địa chỉ đã được cập nhật", Toast.LENGTH_SHORT).show();
        
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private boolean validateInputs() {
        if (edtFullName == null || edtFullName.getText().toString().trim().isEmpty()) {
            showError("Vui lòng nhập họ và tên");
            return false;
        }
        if (edtPhone == null || edtPhone.getText().toString().trim().isEmpty()) {
            showError("Vui lòng nhập số điện thoại");
            return false;
        }
        if (selectedProvince == null || selectedProvince.isEmpty()) {
            showError("Vui lòng chọn tỉnh/thành phố");
            return false;
        }
        if (selectedDistrict == null || selectedDistrict.isEmpty()) {
            showError("Vui lòng chọn quận/huyện");
            return false;
        }
        if (selectedWard == null || selectedWard.isEmpty()) {
            showError("Vui lòng chọn phường/xã");
            return false;
        }
        if (edtDetail == null || edtDetail.getText().toString().trim().isEmpty()) {
            showError("Vui lòng nhập địa chỉ chi tiết");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void saveAddress() {
        Map<String, Object> data = new HashMap<>();
        data.put("recipient_name", edtFullName.getText().toString().trim());
        data.put("phone", edtPhone.getText().toString().trim());
        data.put("city", selectedProvince);
        data.put("district", selectedDistrict);
        data.put("ward", selectedWard);
        data.put("address_line_1", edtDetail.getText().toString().trim());
        data.put("is_default_shipping", switchDefault.isChecked());
        
        if (chipGroupTag != null) {
            int checkedChipId = chipGroupTag.getCheckedChipId();
            if (checkedChipId == R.id.chipAddressHome) {
                data.put("address_type", "home");
            } else if (checkedChipId == R.id.chipAddressOffice) {
                data.put("address_type", "office");
            } else if (checkedChipId == R.id.chipAddressOther) {
                data.put("address_type", "other");
            }
        }

        if (addressId != null) {
            viewModel.updateAddress(addressId, data);
        } else {
            viewModel.addAddress(data);
        }
    }
}
