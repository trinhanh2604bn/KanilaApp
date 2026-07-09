package com.example.frontend.feature.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.frontend.R;
import com.example.frontend.core.auth.AuthResultHandler;
import com.example.frontend.databinding.FragmentOtpVerificationBinding;
import java.util.ArrayList;
import java.util.List;

public class OtpVerificationFragment extends Fragment {
    private static final String ARG_TYPE = "target_type";
    private static final String ARG_VALUE = "target_value";
    private static final String ARG_PURPOSE = "purpose";

    private FragmentOtpVerificationBinding binding;
    private AuthViewModel viewModel;
    private String targetType;
    private String targetValue;
    private String purpose;
    private final List<EditText> otpBoxes = new ArrayList<>();
    private CountDownTimer countDownTimer;

    public static OtpVerificationFragment newInstance(String type, String value, String purpose) {
        OtpVerificationFragment fragment = new OtpVerificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putString(ARG_VALUE, value);
        args.putString(ARG_PURPOSE, purpose);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            targetType = getArguments().getString(ARG_TYPE);
            targetValue = getArguments().getString(ARG_VALUE);
            purpose = getArguments().getString(ARG_PURPOSE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOtpVerificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        setupUI();
        setupOtpBoxes();
        setupActions();
        startResendTimer();
        observeViewModel();
    }

    private void setupUI() {
        if (targetType.equals("email")) {
            binding.tvSubtitle.setText(getString(R.string.auth_otp_verify_email_subtitle, maskEmail(targetValue)));
            binding.btnChangeTarget.setText(R.string.auth_otp_change_email);
            binding.tvSupportNote.setText(R.string.auth_otp_no_code_email);
        } else {
            binding.tvSubtitle.setText(getString(R.string.auth_otp_verify_phone_subtitle, maskPhone(targetValue)));
            binding.btnChangeTarget.setText(R.string.auth_otp_change_phone);
            binding.tvSupportNote.setText(R.string.auth_otp_no_code_phone);
        }
    }

    private void setupOtpBoxes() {
        ViewGroup container = binding.layoutOtpInputs;
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof EditText) {
                EditText et = (EditText) child;
                otpBoxes.add(et);
                final int index = i;
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 1 && index < otpBoxes.size() - 1) {
                            otpBoxes.get(index + 1).requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
                
                et.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == android.view.KeyEvent.KEYCODE_DEL 
                            && et.getText().length() == 0 
                            && index > 0 
                            && event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                        otpBoxes.get(index - 1).requestFocus();
                        return true;
                    }
                    return false;
                });
            }
        }
    }

    private void setupActions() {
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.btnChangeTarget.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnVerify.setOnClickListener(v -> {
            StringBuilder otp = new StringBuilder();
            for (EditText et : otpBoxes) {
                otp.append(et.getText().toString());
            }

            if (otp.length() < 6) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ 6 số", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.verifyOtp(targetType, targetValue, otp.toString(), purpose);
        });

        binding.btnResend.setOnClickListener(v -> {
            if (purpose.equals("register")) {
                viewModel.login(targetType, targetValue);
            } else if (purpose.equals("forgot_password")) {
                viewModel.forgotPassword(targetType, targetValue);
            }
            startResendTimer();
            binding.btnResend.setVisibility(View.GONE);
            binding.tvResendCountdown.setVisibility(View.VISIBLE);
        });
    }

    private void startResendTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                binding.tvResendCountdown.setText(getString(R.string.auth_otp_resend_countdown, 0, seconds));
            }

            @Override
            public void onFinish() {
                binding.tvResendCountdown.setVisibility(View.GONE);
                binding.btnResend.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void observeViewModel() {
        viewModel.getVerifyResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnVerify.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnVerify.setEnabled(true);

                    if (purpose.equals("forgot_password")) {
                        if (result.data != null && result.data.getResetToken() != null) {
                            ResetPasswordFragment fragment = ResetPasswordFragment.newInstance(result.data.getResetToken());
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    } else if (purpose.equals("register")) {
                        Toast.makeText(getContext(), "Xác minh thành công", Toast.LENGTH_SHORT).show();
                        AuthResultHandler.handleSuccess(requireActivity());
                    }
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnVerify.setEnabled(true);
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private String maskEmail(String email) {
        String[] parts = email.split("@");
        if (parts.length < 2) return email;
        String user = parts[0];
        if (user.length() <= 2) return email;
        return user.substring(0, 2) + "***@" + parts[1];
    }

    private String maskPhone(String phone) {
        if (phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 3);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
        binding = null;
    }
}
