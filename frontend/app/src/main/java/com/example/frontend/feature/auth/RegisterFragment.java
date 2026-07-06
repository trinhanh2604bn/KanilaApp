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
import com.google.android.material.tabs.TabLayout;

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

        setupTabs();
        setupInputs();
        setupTerms();
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
                    binding.btnRegister.setText(R.string.auth_btn_register_email);
                } else {
                    selectedChannel = "phone";
                    binding.inputEmail.setVisibility(View.GONE);
                    binding.inputPhone.setVisibility(View.VISIBLE);
                    binding.btnRegister.setText(R.string.auth_btn_register_phone);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupInputs() {
        // Full Name Input
        binding.inputFullName.setLabelText(getString(R.string.auth_full_name_label));
        binding.inputFullName.getEditText().setHint(R.string.auth_full_name_hint);
        binding.inputFullName.setLeadingIcon(R.drawable.ic_account);

        // Email Input
        binding.inputEmail.getEditText().setHint(R.string.auth_email_hint);
        binding.inputEmail.setLeadingIcon(R.drawable.ic_mail);

        // Phone Input
        binding.inputPhone.getEditText().setHint(R.string.auth_phone_hint);
        binding.inputPhone.setLeadingIcon(R.drawable.ic_account);
        binding.inputPhone.getEditText().setInputType(android.text.InputType.TYPE_CLASS_PHONE);
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

        binding.btnRegister.setOnClickListener(v -> {
            String fullName = binding.inputFullName.getText().trim();
            if (fullName.isEmpty()) {
                binding.inputFullName.setErrorState(getString(R.string.error_required_field));
                return;
            }
            binding.inputFullName.clearMessage();

            String email = null;
            String phone = null;

            if (selectedChannel.equals("email")) {
                email = binding.inputEmail.getText().trim().toLowerCase();
                if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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
                phone = normalizePhone(rawPhone);
                binding.inputPhone.clearMessage();
            }

            if (!binding.cbTerms.isChecked()) {
                Toast.makeText(getContext(), "Vui lòng đồng ý với điều khoản", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.register(selectedChannel, fullName, email, phone, true, binding.cbMarketing.isChecked());
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
                    if (result.data != null && result.data.isVerificationRequired()) {
                        String email = binding.inputEmail.getText().trim().toLowerCase();
                        String rawPhone = binding.inputPhone.getText().trim();
                        String identifier = selectedChannel.equals("email") ? email : normalizePhone(rawPhone);
                        
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
