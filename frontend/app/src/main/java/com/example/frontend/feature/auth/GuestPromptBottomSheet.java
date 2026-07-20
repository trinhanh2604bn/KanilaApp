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

        String title = getString(R.string.auth_guest_prompt_title);
        String subtitle;
        switch (type) {
            case ADD_TO_WISHLIST:
            case OPEN_WISHLIST:
                subtitle = "Đăng nhập để lưu sản phẩm yêu thích và xem lại bất cứ lúc nào.";
                break;
            case START_CHECKOUT:
            case ADD_TO_CART:
                title = "Đăng nhập để mua hàng";
                subtitle = "Đăng nhập để tiếp tục thanh toán, lưu địa chỉ và theo dõi đơn hàng.";
                break;
            case SAVE_BEAUTY_PROFILE:
                subtitle = "Đăng nhập để lưu hồ sơ làm đẹp và nhận gợi ý cá nhân hóa.";
                break;
            case CREATE_COMMUNITY_POST:
                subtitle = "Đăng nhập để chia sẻ trải nghiệm và nhận thưởng tương tác.";
                break;
            case JOIN_CHALLENGE:
                subtitle = "Đăng nhập để tham gia thử thách, theo dõi tiến trình và nhận quà tặng.";
                break;
            case COMMUNITY_INTERACTION:
                subtitle = "Đăng nhập để yêu thích, lưu bài viết và thảo luận cùng cộng đồng.";
                break;
            case SAVE_COUPON:
            case OPEN_VOUCHER_WALLET:
                subtitle = "Đăng nhập để lưu voucher và sử dụng khi thanh toán.";
                break;
            default:
                subtitle = "Đăng nhập để cá nhân hóa trải nghiệm Kanila của bạn.";
                break;
        }
        binding.tvTitle.setText(title);
        binding.tvSubtitle.setText(subtitle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
