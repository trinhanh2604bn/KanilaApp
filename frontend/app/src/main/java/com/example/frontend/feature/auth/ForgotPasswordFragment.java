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
import com.google.android.material.tabs.TabLayout;

public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;
    private AuthViewModel viewModel;
    private String selectedChannel = "email";

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

        setupTabs();
        setupInputs();
        setupActions();
        observeViewModel();
    }

    private void setupTabs() {
        binding.tabLayoutAuth.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    selectedChannel = "email";
                    binding.inputEmail.setVisibility(View.VISIBLE);
                    binding.inputPhone.setVisibility(View.GONE);
                    binding.btnSubmit.setText(R.string.auth_btn_send_otp_email);
                } else {
                    selectedChannel = "phone";
                    binding.inputEmail.setVisibility(View.GONE);
                    binding.inputPhone.setVisibility(View.VISIBLE);
                    binding.btnSubmit.setText(R.string.auth_btn_send_otp_phone);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupInputs() {
        binding.inputEmail.setLabelText(getString(R.string.auth_email_label));
        binding.inputEmail.getEditText().setHint(R.string.auth_email_hint);
        binding.inputEmail.setLeadingIcon(R.drawable.ic_mail);

        binding.inputPhone.setLabelText(getString(R.string.auth_phone_label));
        binding.inputPhone.getEditText().setHint(R.string.auth_phone_hint);
        binding.inputPhone.setLeadingIcon(R.drawable.ic_account);
        binding.inputPhone.getEditText().setInputType(android.text.InputType.TYPE_CLASS_PHONE);
    }

    private void setupActions() {
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnSubmit.setOnClickListener(v -> {
            String identifier;
            if (selectedChannel.equals("email")) {
                identifier = binding.inputEmail.getText().trim().toLowerCase();
                if (identifier.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
                    binding.inputEmail.setErrorState(getString(R.string.error_invalid_email));
                    return;
                }
            } else {
                identifier = binding.inputPhone.getText().trim();
                if (identifier.isEmpty() || identifier.length() < 9) {
                    binding.inputPhone.setErrorState(getString(R.string.error_invalid_phone));
                    return;
                }
            }
            // Use forgot-password endpoint to issue OTP with "reset_password" purpose
            viewModel.forgotPassword(selectedChannel, identifier);
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
                    String email = binding.inputEmail.getText().trim().toLowerCase();
                    String rawPhone = binding.inputPhone.getText().trim();
                    String identifier = selectedChannel.equals("email") ? email : normalizePhone(rawPhone);
                    navigateToOtp(identifier);
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

    private void navigateToOtp(String identifier) {
        OtpVerificationFragment fragment = OtpVerificationFragment.newInstance(
                selectedChannel, identifier, "reset_password"
        );
        getParentFragmentManager().beginTransaction()
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
