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
import com.example.frontend.databinding.FragmentLoginBinding;
import com.google.android.material.tabs.TabLayout;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;
    private String selectedChannel = "email";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
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
                    binding.btnLogin.setText(R.string.auth_btn_send_otp_email);
                } else {
                    selectedChannel = "phone";
                    binding.inputEmail.setVisibility(View.GONE);
                    binding.inputPhone.setVisibility(View.VISIBLE);
                    binding.btnLogin.setText(R.string.auth_btn_send_otp_phone);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupInputs() {
        // Email Input
        binding.inputEmail.setLabelText(getString(R.string.auth_email_label));
        binding.inputEmail.getEditText().setHint(R.string.auth_email_hint);
        binding.inputEmail.setLeadingIcon(R.drawable.ic_mail);

        // Phone Input
        binding.inputPhone.setLabelText(getString(R.string.auth_phone_label));
        binding.inputPhone.getEditText().setHint(R.string.auth_phone_hint);
        binding.inputPhone.setLeadingIcon(R.drawable.ic_account); 
        binding.inputPhone.getEditText().setInputType(android.text.InputType.TYPE_CLASS_PHONE);
    }

    private void setupActions() {
        binding.btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        binding.btnLogin.setOnClickListener(v -> {
            String identifier;
            if (selectedChannel.equals("email")) {
                identifier = binding.inputEmail.getText().trim().toLowerCase();
                if (identifier.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
                    binding.inputEmail.setErrorState(getString(R.string.error_invalid_email));
                    return;
                }
                binding.inputEmail.clearMessage();
            } else {
                String rawPhone = binding.inputPhone.getText().trim();
                if (rawPhone.isEmpty() || rawPhone.length() < 9) {
                    binding.inputPhone.setErrorState(getString(R.string.error_invalid_phone));
                    return;
                }
                identifier = normalizePhone(rawPhone);
                binding.inputPhone.clearMessage();
            }

            viewModel.login(selectedChannel, identifier);
        });

        binding.tvGoToRegister.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.main, new RegisterFragment())
                .addToBackStack(null)
                .commit());

        // Need to add this to layout first, then binding will have it
        // For now I'll just assume it's there or I'll add it to layout later
        if (binding.tvForgotPassword != null) {
            binding.tvForgotPassword.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                    .replace(R.id.main, new ForgotPasswordFragment())
                    .addToBackStack(null)
                    .commit());
        }
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

    private void observeViewModel() {
        viewModel.getAuthResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnLogin.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnLogin.setEnabled(true);
                    if (result.data != null) {
                        if (result.data.isVerificationRequired()) {
                            String email = binding.inputEmail.getText().trim().toLowerCase();
                            String rawPhone = binding.inputPhone.getText().trim();
                            String identifier = selectedChannel.equals("email") ? email : normalizePhone(rawPhone);
                            
                            navigateToOtp(identifier);
                        } else {
                            // Direct success (e.g. if password was used or already authenticated)
                            Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            com.example.frontend.core.auth.AuthResultHandler.handleSuccess(requireActivity());
                        }
                    }
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnLogin.setEnabled(true);
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void navigateToOtp(String identifier) {
        OtpVerificationFragment fragment = OtpVerificationFragment.newInstance(
                selectedChannel, identifier, "login"
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
