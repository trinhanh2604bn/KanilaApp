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
import androidx.fragment.app.Fragment;

import com.example.frontend.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class CheckoutAddressAddFragment extends Fragment {

    private EditText edtFullName, edtPhone, edtDetail;
    private TextView tvProvince, tvWard, tvCounter;
    private ChipGroup chipGroupTag;
    private SwitchMaterial switchDefault;
    private View btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_checkout_address_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupHeader(view);
        setupTexts(view);
        setupListeners(view);
    }

    private void initViews(View view) {
        edtFullName = view.findViewById(R.id.edtAddressFullName);
        edtPhone = view.findViewById(R.id.edtAddressPhone);
        edtDetail = view.findViewById(R.id.edtAddressDetail);
        tvProvince = view.findViewById(R.id.edtAddressProvince);
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
            tvTitle.setText("Thêm địa chỉ mới");
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
        if (tvLabelFullName != null) tvLabelFullName.setText("Họ và tên");
        if (edtFullName != null) edtFullName.setHint("Nhập họ và tên");

        TextView tvLabelPhone = view.findViewById(R.id.tvLabelPhone);
        if (tvLabelPhone != null) tvLabelPhone.setText("Số điện thoại");
        if (edtPhone != null) edtPhone.setHint("Nhập số điện thoại");

        TextView tvLabelProvince = view.findViewById(R.id.tvLabelProvince);
        if (tvLabelProvince != null) tvLabelProvince.setText("Tỉnh/Thành phố");
        if (tvProvince != null) tvProvince.setText("Chọn tỉnh/thành phố");

        TextView tvLabelWard = view.findViewById(R.id.tvLabelWard);
        if (tvLabelWard != null) tvLabelWard.setText("Thôn/Xóm");
        if (tvWard != null) tvWard.setText("Chọn phường/xã");

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

        TextView tvLabelDefaultDesc = view.findViewById(R.id.tvLabelDefaultDesc);
        if (tvLabelDefaultDesc != null) tvLabelDefaultDesc.setText("Địa chỉ mặc định sẽ được chọn ở lần đặt hàng sau");

        if (btnSave instanceof TextView) {
            ((TextView) btnSave).setText("Lưu địa chỉ");
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
                // TODO: Open province selector
            });
        }

        View layoutWard = view.findViewById(R.id.layoutAddressWard);
        if (layoutWard != null) {
            layoutWard.setOnClickListener(v -> {
                // TODO: Open ward selector
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
                    saveAddress();
                }
            });
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
        if (tvProvince == null || tvProvince.getText().toString().equals("Chọn tỉnh/thành phố")) {
            showError("Vui lòng chọn tỉnh/thành phố");
            return false;
        }
        if (tvWard == null || tvWard.getText().toString().equals("Chọn phường/xã")) {
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
        // TODO: Implement save logic through repository
        Toast.makeText(getContext(), "Địa chỉ đã được lưu", Toast.LENGTH_SHORT).show();
        if (getActivity() != null) {
            getActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    }
}
