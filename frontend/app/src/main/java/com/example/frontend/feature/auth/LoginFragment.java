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

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;

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
        viewModel.resetStates();

        setupInputs();
        setupActions();
        observeViewModel();
    }

    private void setupInputs() {
        binding.inputIdentifier.setHintText(getString(R.string.auth_login_input_hint));
        binding.inputIdentifier.setLeadingIcon(R.drawable.ic_mail);

        binding.inputPassword.setHintText("••••••••");
        binding.inputPassword.setLeadingIcon(R.drawable.ic_lock);
    }

    private void setupActions() {
        binding.btnMenu.setOnClickListener(v -> {
            // Hamburger menu action or back action
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.btnLogin.setOnClickListener(v -> {
            String rawIdentifier = binding.inputIdentifier.getText().trim();
            String password = binding.inputPassword.getText().trim();

            if (rawIdentifier.isEmpty()) {
                binding.inputIdentifier.setErrorState(getString(R.string.error_required_field));
                return;
            }
            binding.inputIdentifier.setDefaultState();

            if (password.isEmpty()) {
                binding.inputPassword.setErrorState(getString(R.string.error_required_field));
                return;
            }
            binding.inputPassword.setDefaultState();

            String channel = detectChannel(rawIdentifier);
            String normalizedIdentifier = channel.equals("phone") ? normalizePhone(rawIdentifier) : rawIdentifier.toLowerCase();
            
            viewModel.login(channel, normalizedIdentifier, password);
        });

        binding.tvForgotPassword.setOnClickListener(v -> ui.common.FragmentNavigationHelper.replaceFragment(requireActivity(), new ForgotPasswordFragment()));

        binding.btnGoogle.setOnClickListener(v -> Toast.makeText(getContext(), "Tiếp tục với Google", Toast.LENGTH_SHORT).show());

        binding.btnFacebook.setOnClickListener(v -> Toast.makeText(getContext(), "Tiếp tục với Facebook", Toast.LENGTH_SHORT).show());
        
        if (binding.tvGoToRegister != null) {
            binding.tvGoToRegister.setOnClickListener(v -> ui.common.FragmentNavigationHelper.replaceFragment(requireActivity(), new RegisterFragment()));
        }
    }

    private String detectChannel(String identifier) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
            return "email";
        }
        // Basic phone detection: starts with 0 or + and has digits
        if (identifier.matches("^[0-9+]{9,15}$") || (identifier.startsWith("0") && identifier.length() >= 9)) {
            return "phone";
        }
        return "email"; // default to email for generic input
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
        viewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
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
                        Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        com.example.frontend.core.auth.AuthResultHandler.handleSuccess(requireActivity());
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

    private void navigateToOtp(String channel, String identifier) {
        OtpVerificationFragment fragment = OtpVerificationFragment.newInstance(
                channel, identifier, "login"
        );
        ui.common.FragmentNavigationHelper.replaceFragment(requireActivity(), fragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
