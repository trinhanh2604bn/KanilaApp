package com.example.frontend.feature.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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

        setupInputs();
        setupActions();
        observeViewModel();
    }

    private void setupInputs() {
        binding.inputAuth.setHintText(getString(R.string.auth_forgot_password_hint));
        binding.inputAuth.setLeadingIcon(R.drawable.ic_mail);
        binding.inputAuth.setTrailingIcon(R.drawable.ic_eye);
        binding.inputAuth.setTrailingIconTint(ContextCompat.getColor(requireContext(), R.color.button));
    }

    private void setupActions() {
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnSubmit.setOnClickListener(v -> {
            String identifier = binding.inputAuth.getText().trim();
            if (identifier.isEmpty()) {
                binding.inputAuth.setErrorState(getString(R.string.error_required_field));
                return;
            }

            String channel;
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
                channel = "email";
            } else if (isValidPhone(identifier)) {
                channel = "phone";
                identifier = normalizePhone(identifier);
            } else {
                binding.inputAuth.setErrorState(getString(R.string.error_invalid_email));
                return;
            }

            binding.inputAuth.clearMessage();
            viewModel.forgotPassword(channel, identifier);
        });
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("(\\+84|0)\\d{9,10}");
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
                    String identifier = binding.inputAuth.getText().trim();
                    String channel = android.util.Patterns.EMAIL_ADDRESS.matcher(identifier).matches() ? "email" : "phone";
                    if (channel.equals("phone")) {
                        identifier = normalizePhone(identifier);
                    }
                    navigateToOtp(channel, identifier);
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
                .replace(R.id.main_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
