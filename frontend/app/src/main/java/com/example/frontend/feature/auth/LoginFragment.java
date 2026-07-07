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
import com.example.frontend.databinding.FragmentLoginBinding;

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
        // Combined Email/Phone Input
        binding.inputEmail.setLabelText(null);
        binding.inputEmail.getEditText().setHint("Email / Số điện thoại");
        binding.inputEmail.setLeadingIcon(R.drawable.ic_mail);

        // Password Input
        binding.inputPassword.setLabelText(null);
        binding.inputPassword.getEditText().setHint("Mật khẩu");
        binding.inputPassword.setLeadingIcon(R.drawable.ic_lock);

        // Style GoToRegister footer
        String footerText = getString(R.string.auth_footer_register);
        SpannableString footerSpannable = new SpannableString(footerText);
        int registerStart = footerText.indexOf("Đăng ký");
        if (registerStart != -1) {
            footerSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.button)),
                    registerStart, registerStart + "Đăng ký".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            footerSpannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    registerStart, registerStart + "Đăng ký".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        binding.tvGoToRegister.setText(footerSpannable);
    }

    private void setupActions() {
        binding.btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        binding.btnLogin.setOnClickListener(v -> {
            String input = binding.inputEmail.getText().trim();
            if (input.isEmpty()) {
                binding.inputEmail.setErrorState(getString(R.string.error_required_field));
                return;
            }

            String identifier;
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                selectedChannel = "email";
                identifier = input.toLowerCase();
            } else if (input.replaceAll("[^\\d]", "").length() >= 9) {
                selectedChannel = "phone";
                identifier = normalizePhone(input);
            } else {
                binding.inputEmail.setErrorState("Email hoặc số điện thoại không hợp lệ");
                return;
            }
            binding.inputEmail.clearMessage();

            String password = binding.inputPassword.getText().trim();
            if (password.isEmpty()) {
                binding.inputPassword.setErrorState(getString(R.string.error_required_field));
                return;
            }
            binding.inputPassword.clearMessage();

            viewModel.login(selectedChannel, identifier, password);
        });

        binding.tvGoToRegister.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.main, new RegisterFragment())
                .addToBackStack(null)
                .commit());

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
                            String input = binding.inputEmail.getText().trim();
                            String identifier = android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches() ? 
                                    input.toLowerCase() : normalizePhone(input);
                            
                            navigateToOtp(identifier);
                        } else {
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
        // Show floating chatbot back when leaving auth screens
        if (getActivity() != null) {
            View chatbot = getActivity().findViewById(R.id.ivChatbot);
            if (chatbot != null) chatbot.setVisibility(View.VISIBLE);
        }
        super.onDestroyView();
        binding = null;
    }
}
