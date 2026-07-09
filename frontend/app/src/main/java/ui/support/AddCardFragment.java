package ui.support;

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

public class AddCardFragment extends Fragment {

    private TextView tvPreviewNumber, tvPreviewHolder, tvPreviewExpiry;
    private EditText edtCardNumber, edtExpiry, edtCardHolder, edtCvv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupLivePreview();
        
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        view.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            if (validateForm()) {
                SuccessDialog dialog = new SuccessDialog(requireContext(), 
                        "Thành công!", 
                        "Thẻ ngân hàng của bạn đã được liên kết thành công.", 
                        "Đóng");
                dialog.setOnConfirmListener(() -> getParentFragmentManager().popBackStack());
                dialog.show();
            } else {
                Toast.makeText(getContext(), "Vui lòng kiểm tra lại thông tin nhập liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews(View view) {
        tvPreviewNumber = view.findViewById(R.id.tvPreviewNumber);
        tvPreviewHolder = view.findViewById(R.id.tvPreviewHolder);
        tvPreviewExpiry = view.findViewById(R.id.tvPreviewExpiry);
        
        edtCardNumber = view.findViewById(R.id.edtCardNumber);
        edtExpiry = view.findViewById(R.id.edtExpiry);
        edtCardHolder = view.findViewById(R.id.edtCardHolder);
        edtCvv = view.findViewById(R.id.edtCvv);
    }

    private void setupLivePreview() {
        // Card Number Formatter and Preview
        edtCardNumber.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;
                isUpdating = true;
                
                String original = s.toString().replaceAll(" ", "");
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < original.length(); i++) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ");
                    formatted.append(original.charAt(i));
                }
                
                String result = formatted.toString();
                edtCardNumber.setText(result);
                edtCardNumber.setSelection(result.length());
                tvPreviewNumber.setText(!result.isEmpty() ? result : "**** **** **** ****");
                isUpdating = false;
            }
        });

        // Expiry Formatter and Preview
        edtExpiry.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;
                isUpdating = true;
                
                String original = s.toString().replaceAll("/", "");
                if (original.length() > 2) {
                    String formatted = original.substring(0, 2) + "/" + original.substring(2);
                    edtExpiry.setText(formatted);
                    edtExpiry.setSelection(formatted.length());
                    tvPreviewExpiry.setText(formatted);
                } else {
                    tvPreviewExpiry.setText(!original.isEmpty() ? original : "MM/YY");
                }
                isUpdating = false;
            }
        });

        // Holder Name Preview
        edtCardHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                tvPreviewHolder.setText(!s.toString().isEmpty() ? s.toString().toUpperCase() : "CHỦ THẺ");
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;
        
        String num = edtCardNumber.getText().toString().replaceAll(" ", "");
        if (num.length() < 16) {
            edtCardNumber.setError("Số thẻ phải đủ 16 chữ số");
            isValid = false;
        }

        String exp = edtExpiry.getText().toString();
        if (exp.length() < 5) {
            edtExpiry.setError("Ngày hết hạn không hợp lệ (MM/YY)");
            isValid = false;
        } else {
            try {
                int month = Integer.parseInt(exp.substring(0, 2));
                if (month < 1 || month > 12) {
                    edtExpiry.setError("Tháng không hợp lệ (01-12)");
                    isValid = false;
                }
            } catch (Exception e) {
                isValid = false;
            }
        }

        String cvv = edtCvv.getText().toString();
        if (cvv.length() < 3) {
            edtCvv.setError("Mã CVV phải có 3 chữ số");
            isValid = false;
        }

        if (edtCardHolder.getText().toString().trim().isEmpty()) {
            edtCardHolder.setError("Vui lòng nhập tên chủ thẻ");
            isValid = false;
        }

        return isValid;
    }
}
