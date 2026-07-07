package com.example.frontend.feature.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.frontend.R;
import com.example.frontend.core.auth.AuthNavigationHelper;
import com.example.frontend.core.auth.PendingAuthAction;
import com.example.frontend.databinding.BottomSheetGuestPromptBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class GuestPromptBottomSheet extends BottomSheetDialogFragment {
    private static final String ARG_ACTION_TYPE = "action_type";
    private BottomSheetGuestPromptBinding binding;

    public static GuestPromptBottomSheet newInstance(PendingAuthAction.ActionType actionType) {
        GuestPromptBottomSheet fragment = new GuestPromptBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_ACTION_TYPE, actionType.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetGuestPromptBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Hide floating chatbot if exists in parent activity
        if (getActivity() != null) {
            View chatbot = getActivity().findViewById(R.id.ivChatbot);
            if (chatbot != null) chatbot.setVisibility(View.GONE);
        }

        setupContent();

        binding.btnLogin.setOnClickListener(v -> {
            AuthNavigationHelper.navigateToLogin(requireActivity());
            dismiss();
        });

        binding.btnRegister.setOnClickListener(v -> {
            AuthNavigationHelper.navigateToRegister(requireActivity());
            dismiss();
        });

        binding.btnLater.setOnClickListener(v -> dismiss());
    }

    private void setupContent() {
        if (getArguments() == null) return;
        String typeStr = getArguments().getString(ARG_ACTION_TYPE);
        if (typeStr == null) return;
        
        PendingAuthAction.ActionType type = PendingAuthAction.ActionType.valueOf(typeStr);
        
        String subtitle;
        switch (type) {
            case ADD_TO_WISHLIST:
            case OPEN_WISHLIST:
                subtitle = "Đăng nhập để lưu sản phẩm yêu thích và xem lại bất cứ lúc nào.";
                break;
            case START_CHECKOUT:
                subtitle = "Đăng nhập để tiếp tục thanh toán, lưu địa chỉ và theo dõi đơn hàng.";
                break;
            case SAVE_BEAUTY_PROFILE:
                subtitle = "Đăng nhập để lưu hồ sơ làm đẹp và nhận gợi ý cá nhân hóa.";
                break;
            case CREATE_COMMUNITY_POST:
                subtitle = "Đăng nhập để đăng bài, bình luận và nhận thưởng tương tác.";
                break;
            case SAVE_COUPON:
            case OPEN_VOUCHER_WALLET:
                subtitle = "Đăng nhập để lưu voucher và sử dụng khi thanh toán.";
                break;
            default:
                subtitle = "Đăng nhập để cá nhân hóa trải nghiệm Kanila của bạn.";
                break;
        }
        binding.tvSubtitle.setText(subtitle);
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
