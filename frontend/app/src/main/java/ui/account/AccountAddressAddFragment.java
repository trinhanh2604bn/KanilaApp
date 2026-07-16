package ui.account;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.frontend.R;
import com.example.frontend.data.model.address.AddressDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.account.AccountViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ui.commerce.AddressPickerBottomSheet;

public class AccountAddressAddFragment extends Fragment {

    private static final String ARG_ADDRESS = "arg_address";

    private AccountViewModel viewModel;
    private AddressDto editingAddress; // null = add mode, non-null = edit mode

    private EditText edtFullName, edtPhone, edtDetail;
    private TextView tvProvince, tvDistrict, tvWard, tvCounter;
    private ChipGroup chipGroupTag;
    private SwitchMaterial switchDefault;
    private View btnSave, btnDelete;

    private String selectedProvince;
    private String selectedDistrict;
    private String selectedWard;
    private Map<String, Map<String, List<String>>> locationMap;

    public static AccountAddressAddFragment newInstance(AddressDto address) {
        AccountAddressAddFragment fragment = new AccountAddressAddFragment();
        if (address != null) {
            Bundle args = new Bundle();
            args.putString("address_id", address.getId());
            args.putString("address_full_name", address.getFullName());
            args.putString("address_phone", address.getPhone());
            args.putString("address_line", address.getAddressLine());
            args.putBoolean("address_is_default", address.isDefaultShipping());
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_checkout_address_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        
        // Reset stale results so form doesn't auto-close from previous success
        viewModel.resetAddAccountAddressResult();
        viewModel.resetUpdateAccountAddressResult();
        viewModel.resetDeleteAccountAddressResult();

        // Check if edit mode
        Bundle args = getArguments();
        if (args != null && args.containsKey("address_id")) {
            editingAddress = new AddressDto();
            // We'll use args directly
        }

        initData();
        initViews(view);
        setupHeader(view);
        setupTexts(view);
        setupListeners(view);
        prefillIfEditing(view, args);
        observeViewModel();
    }

    private void prefillIfEditing(View view, Bundle args) {
        if (args == null || !args.containsKey("address_id")) {
            // Add mode: hide delete button
            if (btnDelete != null) btnDelete.setVisibility(View.GONE);
            return;
        }
        // Edit mode: show delete button, prefill fields
        if (btnDelete != null) btnDelete.setVisibility(View.VISIBLE);
        String fullName = args.getString("address_full_name", "");
        String phone = args.getString("address_phone", "");
        String addressLine = args.getString("address_line", "");
        
        if (edtFullName != null) edtFullName.setText(fullName);
        if (edtPhone != null) edtPhone.setText(phone);
        if (edtDetail != null) edtDetail.setText(addressLine);
        if (switchDefault != null) switchDefault.setChecked(args.getBoolean("address_is_default", false));

        // Try to identify province, district and ward from addressLine
        outerLoop:
        for (String province : locationMap.keySet()) {
            if (addressLine.contains(province)) {
                selectedProvince = province;
                if (tvProvince != null) tvProvince.setText(province);
                
                Map<String, List<String>> districts = locationMap.get(province);
                if (districts != null) {
                    for (String district : districts.keySet()) {
                        if (addressLine.contains(district)) {
                            selectedDistrict = district;
                            if (tvDistrict != null) tvDistrict.setText(district);
                            
                            List<String> wards = districts.get(district);
                            if (wards != null) {
                                for (String ward : wards) {
                                    if (addressLine.contains(ward)) {
                                        selectedWard = ward;
                                        if (tvWard != null) tvWard.setText(ward);
                                        
                                        // Cleanup Detail field
                                        String cleanedDetail = addressLine.replace(province, "")
                                                .replace(district, "")
                                                .replace(ward, "")
                                                .replace(", ,", ",")
                                                .replaceAll("^[,\\s]+", "")
                                                .replaceAll("[,\\s]+$", "");
                                        if (edtDetail != null) edtDetail.setText(cleanedDetail);
                                        break outerLoop;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Update header title
        View header = view.findViewById(R.id.layoutTopBar);
        if (header != null) {
            TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
            if (tvTitle != null) tvTitle.setText("Sửa địa chỉ");
        }
    }

    private void initData() {
        locationMap = new HashMap<>();
        
        // --- TP. Hồ Chí Minh ---
        Map<String, List<String>> hcmDistricts = new HashMap<>();
        hcmDistricts.put("Quận 1", Arrays.asList("Phường Bến Thành", "Phường Đa Kao", "Phường Tân Định"));
        hcmDistricts.put("Quận 7", Arrays.asList("Phường Tân Phong", "Phường Tân Kiểng", "Phường Phú Mỹ"));
        hcmDistricts.put("Thành phố Thủ Đức", Arrays.asList("Phường Linh Trung", "Phường Linh Tây", "Phường Hiệp Phú"));
        locationMap.put("TP. Hồ Chí Minh", hcmDistricts);

        // --- Hà Nội ---
        Map<String, List<String>> hnDistricts = new HashMap<>();
        hnDistricts.put("Quận Hoàn Kiếm", Arrays.asList("Phường Hàng Đào", "Phường Tràng Tiền", "Phường Lý Thái Tổ"));
        hnDistricts.put("Quận Cầu Giấy", Arrays.asList("Phường Dịch Vọng", "Phường Yên Hòa", "Phường Quan Hoa"));
        locationMap.put("Hà Nội", hnDistricts);
        
        // --- Đà Nẵng ---
        Map<String, List<String>> dnDistricts = new HashMap<>();
        dnDistricts.put("Quận Hải Châu", Arrays.asList("Phường Hòa Thuận Đông", "Phường Phước Ninh"));
        dnDistricts.put("Quận Thanh Khê", Arrays.asList("Phường Chính Gián", "Phường Thạc Gián"));
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
        btnDelete = view.findViewById(R.id.btnDeleteAddress);
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutTopBar);
        if (header == null) return;
        TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText("Thêm địa chỉ mới");
        View btnBack = header.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
        View btnSearch = header.findViewById(R.id.btnTopBarSearch);
        if (btnSearch != null) btnSearch.setVisibility(View.GONE);
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
        if (edtDetail != null) edtDetail.setHint("Số nhà, tên đường, tòa nhà, căn hộ...");

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

        if (btnSave instanceof com.google.android.material.button.MaterialButton) {
            ((com.google.android.material.button.MaterialButton) btnSave).setText("Lưu địa chỉ");
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
                AddressPickerBottomSheet picker = new AddressPickerBottomSheet(getContext(),
                        "Chọn tỉnh/thành phố", provinces, item -> {
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
                    Toast.makeText(getContext(), "Vui lòng chọn tỉnh/thành phố trước", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, List<String>> districts = locationMap.get(selectedProvince);
                if (districts != null) {
                    List<String> districtNames = new ArrayList<>(districts.keySet());
                    AddressPickerBottomSheet picker = new AddressPickerBottomSheet(getContext(),
                            "Chọn quận/huyện", districtNames, item -> {
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
                    Toast.makeText(getContext(), "Vui lòng chọn quận/huyện trước", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, List<String>> districts = locationMap.get(selectedProvince);
                if (districts != null) {
                    List<String> wards = districts.get(selectedDistrict);
                    if (wards != null) {
                        AddressPickerBottomSheet picker = new AddressPickerBottomSheet(getContext(),
                                "Chọn phường/xã", wards, item -> {
                            selectedWard = item;
                            if (tvWard != null) tvWard.setText(selectedWard);
                        });
                        picker.show();
                    }
                }
            });
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                if (!validateInputs()) return;
                String fullName = edtFullName.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String detail = edtDetail.getText().toString().trim();
                
                // Combine address_line: [Detail], [Ward], [District], [Province]
                StringBuilder sb = new StringBuilder(detail);
                if (selectedWard != null) sb.append(", ").append(selectedWard);
                if (selectedDistrict != null) sb.append(", ").append(selectedDistrict);
                if (selectedProvince != null) sb.append(", ").append(selectedProvince);
                String addressLine = sb.toString().trim();

                boolean isDefault = switchDefault != null && switchDefault.isChecked();

                Map<String, Object> body = new HashMap<>();
                body.put("full_name", fullName);
                body.put("phone", phone);
                body.put("address_line", addressLine);
                body.put("is_default", isDefault);

                Bundle args = getArguments();
                if (args != null && args.containsKey("address_id")) {
                    viewModel.updateAccountAddress(args.getString("address_id"), body);
                } else {
                    viewModel.addAccountAddress(body);
                }
            });
        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                Bundle args = getArguments();
                if (args != null && args.containsKey("address_id")) {
                    String addressId = args.getString("address_id");
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Xác nhận xóa")
                            .setMessage("Bạn có chắc chắn muốn xóa địa chỉ này không?")
                            .setPositiveButton("Xóa", (dialog, which) -> {
                                viewModel.deleteAccountAddress(addressId);
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                }
            });
        }
    }

    private boolean validateInputs() {
        if (edtFullName == null || edtFullName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtPhone == null || edtPhone.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtDetail == null || edtDetail.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập địa chỉ chi tiết", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void observeViewModel() {
        viewModel.getAddAccountAddressResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                Toast.makeText(getContext(), "Đã thêm địa chỉ", Toast.LENGTH_SHORT).show();
                viewModel.resetAddAccountAddressResult();
                getParentFragmentManager().popBackStack();
            } else if (result != null && result.status == NetworkResult.Status.ERROR) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getUpdateAccountAddressResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                Toast.makeText(getContext(), "Đã cập nhật địa chỉ", Toast.LENGTH_SHORT).show();
                viewModel.resetUpdateAccountAddressResult();
                getParentFragmentManager().popBackStack();
            } else if (result != null && result.status == NetworkResult.Status.ERROR) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getDeleteAccountAddressResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                Toast.makeText(getContext(), "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                viewModel.resetDeleteAccountAddressResult();
                getParentFragmentManager().popBackStack();
            }
        });
    }
}
