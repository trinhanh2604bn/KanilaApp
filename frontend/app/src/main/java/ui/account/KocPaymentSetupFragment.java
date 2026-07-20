package ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;

public class KocPaymentSetupFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_koc_payment_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        view.findViewById(R.id.btnSave).setOnClickListener(v -> {
            // Lưu trạng thái đã thiết lập ngân hàng
            com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).saveBankSetupStatus(true);

            Toast.makeText(requireContext(), "Thông tin thanh toán đã được cập nhật. Yêu cầu rút tiền đang được xử lý.", Toast.LENGTH_LONG).show();
            getParentFragmentManager().popBackStack();
        });
    }
}
