package com.example.frontend.feature.auth;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
import com.example.frontend.databinding.FragmentRegisterBinding;

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

        setupModeToggle();
        setupInputs();
        setupTerms();
        setupActions();
        observeViewModel();
        
        updateUiForMode();
    }

    private void setupModeToggle() {
        binding.tvRegisterModeToggle.setOnClickListener(v -> {
            if (selectedChannel.equals("email")) {
                selectedChannel = "phone";
            } else {
                selectedChannel = "email";
            }
            updateUiForMode();
        });
    }

    private void updateUiForMode() {
        if (selectedChannel.equals("email")) {
            binding.inputAuth.setLabelText(getString(R.string.auth_email_label));
            binding.inputAuth.getEditText().setHint(R.string.auth_email_hint);
            binding.inputAuth.setLeadingIcon(R.drawable.ic_mail);
            binding.inputAuth.getEditText().setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            binding.tvRegisterModeToggle.setText(R.string.auth_tab_phone);
        } else {
            binding.inputAuth.setLabelText(getString(R.string.auth_phone_label));
            binding.inputAuth.getEditText().setHint(R.string.auth_phone_hint);
            binding.inputAuth.setLeadingIcon(R.drawable.ic_account);
            binding.inputAuth.getEditText().setInputType(android.text.InputType.TYPE_CLASS_PHONE);
            binding.tvRegisterModeToggle.setText(R.string.auth_tab_email);
        }
        binding.inputAuth.setTextValue("");
        binding.inputAuth.clearMessage();
    }

    private void setupInputs() {
        // Full Name Input
        binding.inputUsername.setLabelText(getString(R.string.auth_full_name_label));
        binding.inputUsername.getEditText().setHint(R.string.auth_full_name_hint);
        binding.inputUsername.setLeadingIcon(R.drawable.ic_account);

        // Password Input
        binding.inputPassword.setLabelText(getString(R.string.auth_password_label));
        binding.inputPassword.getEditText().setHint("********");
        binding.inputPassword.setLeadingIcon(R.drawable.ic_lock);

        // Confirm Password Input
        binding.inputConfirmPassword.setLabelText(getString(R.string.auth_confirm_password_label));
        binding.inputConfirmPassword.getEditText().setHint("********");
        binding.inputConfirmPassword.setLeadingIcon(R.drawable.ic_lock);

        binding.btnRegister.setText(R.string.auth_btn_register);
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
        
        binding.tvTerms.setText(spannable);
    }

    private void setupActions() {
        binding.btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        binding.btnRegister.setOnClickListener(v -> {
            String fullName = binding.inputUsername.getText().trim();
            String identifier = binding.inputAuth.getText().trim();
            String password = binding.inputPassword.getText().trim();
            String confirmPassword = binding.inputConfirmPassword.getText().trim();

            if (fullName.isEmpty()) {
                binding.inputUsername.setErrorState(getString(R.string.error_required_field));
                return;
            }
            binding.inputUsername.clearMessage();

            if (identifier.isEmpty()) {
                binding.inputAuth.setErrorState(getString(R.string.error_required_field));
                return;
            }
            binding.inputAuth.clearMessage();

            if (selectedChannel.equals("email")) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
                    binding.inputAuth.setErrorState(getString(R.string.error_invalid_email));
                    return;
                }
            } else {
                if (identifier.length() < 9) {
                    binding.inputAuth.setErrorState(getString(R.string.error_invalid_phone));
                    return;
                }
            }

            if (password.isEmpty()) {
                binding.inputPassword.setErrorState(getString(R.string.error_required_field));
                return;
            }
            if (password.length() < 6) {
                binding.inputPassword.setErrorState(getString(R.string.error_password_short));
                return;
            }
            binding.inputPassword.clearMessage();

            if (!password.equals(confirmPassword)) {
                binding.inputConfirmPassword.setErrorState(getString(R.string.error_password_mismatch));
                return;
            }
            binding.inputConfirmPassword.clearMessage();

            if (!binding.cbTerms.isChecked()) {
                Toast.makeText(getContext(), "Vui lòng đồng ý với điều khoản", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = selectedChannel.equals("email") ? identifier.toLowerCase() : null;
            String phone = selectedChannel.equals("phone") ? normalizePhone(identifier) : null;

            viewModel.register(selectedChannel, fullName, email, phone, password, true, binding.cbMarketing.isChecked());
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

    private void observeViewModel() {
        viewModel.getRegisterResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnRegister.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnRegister.setEnabled(true);
                    if (result.data != null && result.data.isVerificationRequired()) {
                        String rawIdentifier = binding.inputAuth.getText().trim();
                        String identifier = selectedChannel.equals("phone") ? normalizePhone(rawIdentifier) : rawIdentifier;
                        navigateToOtp(identifier);
                    }
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnRegister.setEnabled(true);
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void navigateToOtp(String identifier) {
        OtpVerificationFragment fragment = OtpVerificationFragment.newInstance(
                selectedChannel, identifier, "register"
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
