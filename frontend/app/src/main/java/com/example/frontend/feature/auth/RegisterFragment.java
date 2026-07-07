package com.example.frontend.feature.auth;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.frontend.R;
import com.example.frontend.databinding.FragmentRegisterBinding;
import com.example.frontend.utils.ToastHelper;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private AuthViewModel viewModel;
    private String selectedChannel = "email";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
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
        setupTerms();
        setupActions();
        observeViewModel();
    }

    private void switchRegistrationMode(String mode) {
        selectedChannel = mode;
        if (mode.equals("email")) {
            binding.tvSwitchMode.setText("Số điện thoại");
            binding.inputEmail.setVisibility(View.VISIBLE);
            binding.inputPhone.setVisibility(View.GONE);
        } else {
            binding.tvSwitchMode.setText("Email");
            binding.inputEmail.setVisibility(View.GONE);
            binding.inputPhone.setVisibility(View.VISIBLE);
        }
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

        // Password Input
        binding.inputPassword.setLabelText("Mật khẩu");
        binding.inputPassword.getEditText().setHint("••••••••");
        binding.inputPassword.setLeadingIcon(R.drawable.ic_lock);
        binding.inputPassword.showMessage("Ít nhất 8 ký tự");

        // Confirm Password Input
        binding.inputConfirmPassword.setLabelText("Xác nhận mật khẩu");
        binding.inputConfirmPassword.getEditText().setHint("••••••••");
        binding.inputConfirmPassword.setLeadingIcon(R.drawable.ic_lock);

        switchRegistrationMode("email");

        binding.btnRegister.setText(R.string.auth_register_title);

        // Style GoToLogin footer
        String footerText = getString(R.string.auth_footer_login);
        SpannableString footerSpannable = new SpannableString(footerText);
        int loginStart = footerText.indexOf("Đăng nhập");
        if (loginStart != -1) {
            footerSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.button)),
                    loginStart, loginStart + "Đăng nhập".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            footerSpannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    loginStart, loginStart + "Đăng nhập".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        binding.tvGoToLogin.setText(footerSpannable);
    }

    private void setupTerms() {
        String prefix = getString(R.string.auth_terms_prefix);
        String terms = getString(R.string.auth_terms_link);
        String and = " và ";
        String privacy = getString(R.string.auth_privacy_link);
        
        SpannableString spannable = new SpannableString(prefix + terms + and + privacy);
        int color = ContextCompat.getColor(requireContext(), R.color.button);
        
        int startTerms = prefix.length();
        int endTerms = startTerms + terms.length();
        spannable.setSpan(new ForegroundColorSpan(color), startTerms, endTerms, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        int startPrivacy = endTerms + and.length();
        int endPrivacy = startPrivacy + privacy.length();
        spannable.setSpan(new ForegroundColorSpan(color), startPrivacy, endPrivacy, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        binding.cbTerms.setText(spannable);
    }

    private void setupActions() {
        binding.btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        binding.tvSwitchMode.setOnClickListener(v -> {
            if (selectedChannel.equals("email")) {
                switchRegistrationMode("phone");
            } else {
                switchRegistrationMode("email");
            }
        });

        binding.btnRegister.setOnClickListener(v -> {
            String password = binding.inputPassword.getText().trim();
            String confirmPassword = binding.inputConfirmPassword.getText().trim();

            if (password.length() < 8) {
                binding.inputPassword.setErrorState(getString(R.string.error_password_weak));
                return;
            }
            binding.inputPassword.clearMessage();

            if (!password.equals(confirmPassword)) {
                binding.inputConfirmPassword.setErrorState(getString(R.string.error_password_mismatch));
                return;
            }
            binding.inputConfirmPassword.clearMessage();

            String email = binding.inputEmail.getText().trim().toLowerCase();
            String rawPhone = binding.inputPhone.getText().trim();
            String phone = rawPhone.isEmpty() ? null : normalizePhone(rawPhone);

            // Auto generate username and fullName since they are hidden
            String identifier = selectedChannel.equals("email") ? email : phone;
            String base = (identifier != null && identifier.contains("@")) ? identifier.split("@")[0] : identifier;
            if (base != null) base = base.replaceAll("[^a-zA-Z0-9]", "");
            
            String username = "user_" + (base != null ? base : "") + "_" + (System.currentTimeMillis() % 1000);
            String fullName = base != null ? base : "Kanila User";

            if (selectedChannel.equals("email")) {
                if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.inputEmail.setErrorState(getString(R.string.error_invalid_email));
                    return;
                }
            } else {
                if (rawPhone.isEmpty() || rawPhone.length() < 9) {
                    binding.inputPhone.setErrorState(getString(R.string.error_invalid_phone));
                    return;
                }
            }
            binding.inputEmail.clearMessage();
            binding.inputPhone.clearMessage();

            if (!binding.cbTerms.isChecked()) {
                ToastHelper.showShort(getContext(), "Vui lòng đồng ý với điều khoản");
                return;
            }

            viewModel.register(selectedChannel, fullName, email.isEmpty() ? null : email, phone, username, password, true, binding.cbMarketing.isChecked());
        });

        binding.tvGoToLogin.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
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
                    binding.btnRegister.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnRegister.setEnabled(true);
                    if (result.data != null) {
                        if (result.data.isVerificationRequired()) {
                            String email = binding.inputEmail.getText().trim().toLowerCase();
                            String rawPhone = binding.inputPhone.getText().trim();
                            String identifier = selectedChannel.equals("email") ? email : normalizePhone(rawPhone);

                            navigateToOtp(identifier);
                        } else {
                            ToastHelper.showShort(getContext(), "Đăng ký thành công");
                            com.example.frontend.core.auth.AuthResultHandler.handleSuccess(requireActivity());
                        }
                    }
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnRegister.setEnabled(true);
                    ToastHelper.showShort(getContext(), result.message);
                    break;
            }
        });
    }

    private void navigateToOtp(String identifier) {
        OtpVerificationFragment fragment = OtpVerificationFragment.newInstance(
                selectedChannel, identifier, "register"
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
