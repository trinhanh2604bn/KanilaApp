package ui.account;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;
import com.example.frontend.data.remote.TokenManager;
import ui.commerce.PaymentMethodFragment;
import ui.common.FragmentNavigationHelper;

public class AccountSettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_account_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHeader(view);
        setupMenuItems(view);
        setupLogout(view);
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutHeader);
        TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText("Thiết lập tài khoản");

        View btnBack = header.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        View rightAction = header.findViewById(R.id.layoutTopBarRightAction);
        if (rightAction != null) rightAction.setVisibility(View.GONE);
    }

    private void setupMenuItems(View view) {
        // Nhóm: Tài khoản của tôi
        setupMenuItem(view.findViewById(R.id.menuSecurity), "Tài khoản & Bảo mật", v -> {
            // TODO: Navigate to Security settings
        });

        setupMenuItem(view.findViewById(R.id.menuAddress), "Địa chỉ", v -> {
            if (getActivity() instanceof com.example.frontend.MainActivity) {
                ((com.example.frontend.MainActivity) getActivity()).loadFragment(new AccountAddressFragment());
            } else {
                ui.common.FragmentNavigationHelper.replaceFragment(requireActivity(), new AccountAddressFragment());
            }
        });

        setupMenuItem(view.findViewById(R.id.menuPayment), "Tài khoản / Thẻ ngân hàng", v -> {
            FragmentNavigationHelper.replaceFragment(requireActivity(), new PaymentMethodFragment());
        });

        // Nhóm: Cài đặt
        setupMenuItem(view.findViewById(R.id.menuChat), "Cài đặt Chat", v -> {
            // TODO: Chat settings
        });

        setupMenuItem(view.findViewById(R.id.menuNotification), "Cài đặt Thông báo", v -> {
            // TODO: Notification settings
        });

        View menuLanguage = view.findViewById(R.id.menuLanguage);
        setupMenuItem(menuLanguage, "Ngôn ngữ / Language", v -> {
            // TODO: Language settings
        });
        TextView tvLangSubtitle = menuLanguage.findViewById(R.id.tvSubtitle);
        if (tvLangSubtitle != null) {
            tvLangSubtitle.setText("Tiếng Việt");
            tvLangSubtitle.setVisibility(View.VISIBLE);
        }

        // Nhóm: Hỗ trợ
        setupMenuItem(view.findViewById(R.id.menuHelpCenter), "Trung tâm hỗ trợ", v -> {
            // TODO: Help Center
        });

        setupMenuItem(view.findViewById(R.id.menuCommunityStandard), "Tiêu chuẩn cộng đồng", v -> {
            // TODO: Community Standards
        });

        setupMenuItem(view.findViewById(R.id.menuTerms), "Điều khoản Kanila", v -> {
            // TODO: Terms
        });

        setupMenuItem(view.findViewById(R.id.menuRate), "Hài lòng với Kanila? Hãy đánh giá ngay!", v -> {
            // TODO: App rating
        });

        setupMenuItem(view.findViewById(R.id.menuAbout), "Giới thiệu", v -> {
            // TODO: About app
        });

        setupMenuItem(view.findViewById(R.id.menuRequestDelete), "Yêu cầu hủy tài khoản", v -> {
            // TODO: Request delete
        });
    }

    private void setupMenuItem(View menuView, String title, View.OnClickListener listener) {
        if (menuView == null) return;
        TextView tvTitle = menuView.findViewById(R.id.tvTitle);
        if (tvTitle != null) tvTitle.setText(title);
        menuView.setOnClickListener(listener);
    }

    private void setupLogout(View view) {
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void showLogoutConfirmationDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_logout);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.5f;
            window.setAttributes(params);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        dialog.findViewById(R.id.btnDialogClose).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnDialogCancel).setOnClickListener(v -> dialog.dismiss());
        
        dialog.findViewById(R.id.btnDialogConfirm).setOnClickListener(v -> {
            dialog.dismiss();
            performLogout();
        });

        dialog.show();
    }

    private void performLogout() {
        // 1. Clear token locally
        TokenManager.getInstance(requireContext()).clearToken();

        // 2. Reset ViewModels state
        com.example.frontend.feature.auth.AuthViewModel authViewModel = 
                new androidx.lifecycle.ViewModelProvider(requireActivity()).get(com.example.frontend.feature.auth.AuthViewModel.class);
        authViewModel.resetStates();
        
        com.example.frontend.feature.recommendation.RecommendationViewModel recommendationViewModel =
                new androidx.lifecycle.ViewModelProvider(requireActivity()).get(com.example.frontend.feature.recommendation.RecommendationViewModel.class);
        recommendationViewModel.clearData();

        // Refresh cart to update badge count immediately after logout
        com.example.frontend.feature.cart.CartViewModel cartViewModel =
                new androidx.lifecycle.ViewModelProvider(requireActivity()).get(com.example.frontend.feature.cart.CartViewModel.class);
        cartViewModel.loadCart();

        com.example.frontend.core.auth.AuthRequiredManager.getInstance().clearPendingAction();
        
        // 3. Clear all Fragment BackStack to prevent going back to restricted screens
        getParentFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // 4. Navigate back to AccountFragment which will now show Guest State
        FragmentNavigationHelper.replaceFragment(requireActivity(), new AccountFragment());

        Toast.makeText(getContext(), "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
    }
}
