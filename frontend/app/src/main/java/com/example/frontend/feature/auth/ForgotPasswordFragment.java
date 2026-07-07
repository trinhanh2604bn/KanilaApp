package com.example.frontend.feature.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.frontend.R;
import com.example.frontend.databinding.FragmentForgotPasswordBinding;

public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // Hide floating chatbot if exists in parent activity
        if (getActivity() != null) {
            View chatbot = getActivity().findViewById(R.id.ivChatbot);
            if (chatbot != null) chatbot.setVisibility(View.GONE);
        }

        setupInputs();
        setupActions();
        observeViewModel();
    }

    private void setupInputs() {
        binding.inputEmail.getEditText().setHint("Email / Số điện thoại");
        binding.inputEmail.setLeadingIcon(R.drawable.ic_mail);
    }

    private void setupActions() {
        binding.btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        binding.btnBackToLogin.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        binding.btnSubmit.setOnClickListener(v -> {
            String identifier = binding.inputEmail.getText().trim().toLowerCase();
            if (identifier.isEmpty()) {
                binding.inputEmail.setErrorState(getString(R.string.error_required_field));
                return;
            }

            String channel = "email";
            if (android.util.Patterns.PHONE.matcher(identifier).matches()) {
                channel = "phone";
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
                binding.inputEmail.setErrorState(getString(R.string.error_invalid_email));
                return;
            }

            viewModel.forgotPassword(channel, identifier);
        });

        binding.tvContactSupport.setOnClickListener(v -> {
            // Logic to contact support/assistant
            Toast.makeText(getContext(), "Đang kết nối với Kanila Assistant...", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeViewModel() {
        viewModel.getAuthResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnSubmit.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmit.setEnabled(true);
                    
                    String identifier = binding.inputEmail.getText().trim().toLowerCase();
                    String channel = android.util.Patterns.PHONE.matcher(identifier).matches() ? "phone" : "email";
                    
                    navigateToOtp(channel, channel.equals("phone") ? normalizePhone(identifier) : identifier);
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmit.setEnabled(true);
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private String normalizePhone(String phone) {
        if (phone == null) return null;
        String digits = phone.replaceAll("[^\\d+]", "");
        if (digits.startsWith("0")) {
            return "+84" + digits.substring(1);
        } else if (!digits.startsWith("+")) {
            return "+84" + digits;
        }
        return digits;
    }

    private void navigateToOtp(String channel, String identifier) {
        OtpVerificationFragment fragment = OtpVerificationFragment.newInstance(
                channel, identifier, "reset_password"
        );
        getParentFragmentManager().beginTransaction()
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        // Show floating chatbot back
        if (getActivity() != null) {
            View chatbot = getActivity().findViewById(R.id.ivChatbot);
            if (chatbot != null) chatbot.setVisibility(View.VISIBLE);
        }
        super.onDestroyView();
        binding = null;
    }
}
