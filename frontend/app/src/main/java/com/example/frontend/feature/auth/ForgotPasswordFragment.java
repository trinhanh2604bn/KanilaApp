package com.example.frontend.feature.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.frontend.R;
import com.example.frontend.databinding.FragmentForgotPasswordBinding;
import com.example.frontend.utils.ToastHelper;

public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;
    private AuthViewModel viewModel;
    private String selectedChannel;
    private String selectedIdentifier;

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
            String input = binding.inputEmail.getText().trim();
            if (input.isEmpty()) {
                binding.inputEmail.setErrorState(getString(R.string.error_required_field));
                return;
            }

            if (android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                selectedChannel = "email";
                selectedIdentifier = input.toLowerCase();
            } else if (input.replaceAll("[^\\d]", "").length() >= 9) {
                selectedChannel = "phone";
                selectedIdentifier = normalizePhone(input);
            } else {
                binding.inputEmail.setErrorState("Email hoặc số điện thoại không hợp lệ");
                return;
            }
            binding.inputEmail.clearMessage();

            viewModel.forgotPassword(selectedChannel, selectedIdentifier);
        });

        binding.tvContactSupport.setOnClickListener(v -> {
            // Logic to contact support/assistant
            ToastHelper.showShort(getContext(), "Đang kết nối với Kanila Assistant...");
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
                    
                    navigateToOtp(selectedChannel, selectedIdentifier);
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmit.setEnabled(true);
                    ToastHelper.showShort(getContext(), result.message);
                    break;
                case NO_INTERNET:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmit.setEnabled(true);
                    ToastHelper.showShort(getContext(), getString(R.string.error_no_internet));
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
