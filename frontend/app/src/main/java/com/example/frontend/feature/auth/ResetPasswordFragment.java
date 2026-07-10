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
import com.example.frontend.databinding.FragmentResetPasswordBinding;

public class ResetPasswordFragment extends Fragment {
    private static final String ARG_TOKEN = "reset_token";
    private FragmentResetPasswordBinding binding;
    private AuthViewModel viewModel;
    private String resetToken;

    public static ResetPasswordFragment newInstance(String token) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            resetToken = getArguments().getString(ARG_TOKEN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
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
        binding.inputNewPassword.setLabelText(getString(R.string.auth_reset_password_new_label));
        binding.inputNewPassword.setLeadingIcon(R.drawable.ic_lock);
        
        binding.inputConfirmPassword.setLabelText(getString(R.string.auth_reset_password_confirm_label));
        binding.inputConfirmPassword.setLeadingIcon(R.drawable.ic_lock);
    }

    private void setupActions() {
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnReset.setOnClickListener(v -> {
            String newPass = binding.inputNewPassword.getText().trim();
            String confirmPass = binding.inputConfirmPassword.getText().trim();

            if (newPass.length() < 8) {
                binding.inputNewPassword.setErrorState(getString(R.string.error_password_weak));
                return;
            }

            if (!newPass.equals(confirmPass)) {
                binding.inputConfirmPassword.setErrorState(getString(R.string.error_password_mismatch));
                return;
            }

            viewModel.resetPassword(resetToken, newPass, confirmPass);
        });
    }

    private void observeViewModel() {
        viewModel.getResetPasswordResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnReset.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnReset.setEnabled(true);
                    Toast.makeText(getContext(), R.string.auth_reset_success, Toast.LENGTH_SHORT).show();
                    // Go back to login
                    getParentFragmentManager().popBackStack(); // Pop Reset
                    getParentFragmentManager().popBackStack(); // Pop Otp
                    getParentFragmentManager().popBackStack(); // Pop Forgot
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnReset.setEnabled(true);
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
