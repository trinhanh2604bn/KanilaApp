package ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;

public class KocRegistrationFragment extends Fragment {

    private int currentStep = 1;
    private FrameLayout containerSteps;
    private ProgressBar pbRegistration;
    private TextView tvStep1, tvStep2;
    private Button btnNext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_koc_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadStep(1);
    }

    private void initViews(View view) {
        containerSteps = view.findViewById(R.id.containerSteps);
        pbRegistration = view.findViewById(R.id.pbRegistration);
        tvStep1 = view.findViewById(R.id.tvStepIndicator1);
        tvStep2 = view.findViewById(R.id.tvStepIndicator2);
        btnNext = view.findViewById(R.id.btnNext);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (currentStep > 1) {
                loadStep(currentStep - 1);
            } else {
                getParentFragmentManager().popBackStack();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentStep < 2) {
                loadStep(currentStep + 1);
            } else {
                completeRegistration();
            }
        });
    }

    private void loadStep(int step) {
        currentStep = step;
        containerSteps.removeAllViews();
        
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        int layoutId;
        
        switch (step) {
            case 1:
                layoutId = R.layout.layout_koc_step_1;
                updateIndicators(1);
                btnNext.setText("Tiếp tục");
                break;
            case 2:
                layoutId = R.layout.layout_koc_step_2;
                updateIndicators(2);
                btnNext.setText("Hoàn tất đăng ký");
                break;
            default:
                return;
        }
        
        inflater.inflate(layoutId, containerSteps, true);
        pbRegistration.setProgress(step);
    }

    private void updateIndicators(int step) {
        int activeColor = ContextCompat.getColor(requireContext(), R.color.pink_primary);
        int inactiveColor = ContextCompat.getColor(requireContext(), R.color.text_tertiary);

        tvStep1.setTextColor(step >= 1 ? activeColor : inactiveColor);
        tvStep2.setTextColor(step >= 2 ? activeColor : inactiveColor);
    }

    private void completeRegistration() {
        // Lưu trạng thái KOC vào SharedPreferences
        com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).saveKocStatus(true);
        
        Toast.makeText(requireContext(), "Đăng ký KOC thành công! Hồ sơ của bạn đã được lưu.", Toast.LENGTH_LONG).show();
        getParentFragmentManager().popBackStack();
        
        // Mở luôn Dashboard sau khi đăng ký thành công
        Fragment dashboard = new KocDashboardFragment();
        int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                ? R.id.main_fragment_container : R.id.main;
        getParentFragmentManager().beginTransaction()
                .replace(containerId, dashboard)
                .commit();
    }
}
