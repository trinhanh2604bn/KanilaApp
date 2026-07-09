package ui.support;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import ui.common.FragmentNavigationHelper;

public class ReturnAssistantFragment extends Fragment {

    private TextView stepCircle1, stepCircle2, stepCircle3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_return_assistant, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupEvents(view);
    }

    private void initViews(View view) {
        stepCircle1 = view.findViewById(R.id.stepCircle1);
        stepCircle2 = view.findViewById(R.id.stepCircle2);
        stepCircle3 = view.findViewById(R.id.stepCircle3);
    }

    private void setupEvents(View view) {
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });

        // Step 1: Select Product
        view.findViewById(R.id.step1).setOnClickListener(v -> {
            markStepComplete(stepCircle1);
            Toast.makeText(getContext(), "Đã chọn sản phẩm cần đổi trả", Toast.LENGTH_SHORT).show();
        });

        // Step 2: Return Reason
        view.findViewById(R.id.step2).setOnClickListener(v -> {
            markStepComplete(stepCircle2);
            Toast.makeText(getContext(), "Đã chọn lý do đổi trả", Toast.LENGTH_SHORT).show();
        });

        // Step 3: Upload Media
        view.findViewById(R.id.step3).setOnClickListener(v -> {
            markStepComplete(stepCircle3);
            Toast.makeText(getContext(), "Đã tải lên hình ảnh minh chứng", Toast.LENGTH_SHORT).show();
        });

        // Step 4: Create Request - CHUYỂN SANG MÀN HÌNH TẠO TICKET
        view.findViewById(R.id.btnCreateRequest).setOnClickListener(v -> {
            navigateToCreateTicket();
        });

        view.findViewById(R.id.tvViewDetail).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Xem chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
        });

        // Kích hoạt nút thay đổi phương thức hoàn tiền
        view.findViewById(R.id.tvChangeRefundMethod).setOnClickListener(v -> {
            replaceFragment(new LinkedWalletsFragment());
        });
    }

    private void markStepComplete(TextView stepView) {
        stepView.setBackgroundResource(R.drawable.bg_circle);
        stepView.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.success));
        stepView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        stepView.setText("✓");
    }

    private void navigateToCreateTicket() {
        FragmentNavigationHelper.replaceFragment(requireActivity(), new CreateTicketFragment());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentNavigationHelper.replaceFragment(requireActivity(), fragment);
    }
}
