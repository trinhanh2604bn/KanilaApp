package ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.account.ProfileHubDto;
import com.example.frontend.feature.account.AccountViewModel;

import java.util.Locale;

import ui.common.FragmentNavigationHelper;

public class AccountFragment extends Fragment {

    private AccountViewModel viewModel;
    
    private View scrollAccountContent, layoutGuestState;
    private ImageView ivAvatar;
    private TextView tvName, tvRankName, tvPointsHeader, tvPointsVal, tvOrderCount, tvVoucherCount, tvSavedCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_account_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        
        initViews(view);

        checkLoginStatus();
    }

    private void checkLoginStatus() {
        if (com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
            scrollAccountContent.setVisibility(View.VISIBLE);
            layoutGuestState.setVisibility(View.GONE);
            observeViewModel();
            
            // Bước 1: Hiển thị ngay dữ liệu cũ nếu đã có (Tránh nháy UI)
            com.example.frontend.data.remote.NetworkResult<ProfileHubDto> currentResult = viewModel.getProfileHubResult().getValue();
            if (currentResult != null && currentResult.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS) {
                bindData(currentResult.data);
            }
            
            // Bước 2: Tải dữ liệu mới nhất từ Server
            viewModel.loadProfileHub();
        } else {
            scrollAccountContent.setVisibility(View.GONE);
            layoutGuestState.setVisibility(View.VISIBLE);
            setupGuestState();
        }
    }

    private void setupGuestState() {
        layoutGuestState.findViewById(R.id.btnLoginNow).setOnClickListener(v -> {
            FragmentNavigationHelper.replaceFragment(requireActivity(), new com.example.frontend.feature.auth.LoginFragment());
        });
        
        layoutGuestState.findViewById(R.id.tvCreateAccount).setOnClickListener(v -> {
            FragmentNavigationHelper.replaceFragment(requireActivity(), new com.example.frontend.feature.auth.RegisterFragment());
        });
    }

    private void initViews(View view) {
        scrollAccountContent = view.findViewById(R.id.scrollAccountContent);
        layoutGuestState = view.findViewById(R.id.layoutGuestState);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvName = view.findViewById(R.id.tvName);
        tvRankName = view.findViewById(R.id.tvRankName);
        tvPointsHeader = view.findViewById(R.id.tvPointsHeader);
        tvPointsVal = view.findViewById(R.id.tvPointsVal);
        
        tvOrderCount = view.findViewById(R.id.tvOrderCount);
        tvVoucherCount = view.findViewById(R.id.tvVoucherCount);
        tvSavedCount = view.findViewById(R.id.tvSavedCount);
        
        view.findViewById(R.id.btnEdit).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main, new ProfileOverviewFragment())
                    .addToBackStack(null)
                    .commit();
        });
        
        // Rank Chip
        View layoutRank = view.findViewById(R.id.layoutRank);
        if (layoutRank != null) {
            layoutRank.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main, new ui.loyalty.LoyaltyFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        // Menu item clicks
        View menuBeautyProfile = view.findViewById(R.id.ivMenu1).getParent() instanceof View ? (View) view.findViewById(R.id.ivMenu1).getParent() : view.findViewById(R.id.ivMenu1);
        menuBeautyProfile.setOnClickListener(v -> {
            FragmentNavigationHelper.replaceFragment(requireActivity(), new BeautyProfileOverviewFragment());
        });

        view.findViewById(R.id.btnAccountOrders).setOnClickListener(v -> {
            FragmentNavigationHelper.replaceFragment(requireActivity(), new com.example.frontend.feature.order.OrderListFragment());
        });

        view.findViewById(R.id.btnAccountVouchers).setOnClickListener(v -> {
            FragmentNavigationHelper.replaceFragment(requireActivity(), new com.example.frontend.feature.voucher.VoucherListFragment());
        });

        view.findViewById(R.id.btnAccountSaved).setOnClickListener(v -> {
            FragmentNavigationHelper.replaceFragment(requireActivity(), new com.example.frontend.feature.wishlist.WishlistFragment());
        });

        View menuPaymentMethod = view.findViewById(R.id.menuPaymentMethod);
        if (menuPaymentMethod != null) {
            menuPaymentMethod.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main, new PaymentMethodAccountFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        View menuSettings = view.findViewById(R.id.menuSettings);
        if (menuSettings != null) {
            menuSettings.setOnClickListener(v -> {
                FragmentNavigationHelper.replaceFragment(requireActivity(), new AccountSettingsFragment());
            });
        }

        View menuSupportCenter = view.findViewById(R.id.menuSupportCenter);
        if (menuSupportCenter != null) {
            menuSupportCenter.setOnClickListener(v -> {
                FragmentNavigationHelper.replaceFragment(requireActivity(), new ui.support.HelpCenterFragment());
            });
        }
    }



    private void observeViewModel() {
        viewModel.getProfileHubResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case SUCCESS:
                    bindData(result.data);
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void bindData(ProfileHubDto data) {
        if (data == null) return;

        if (data.getProfile() != null) {
            String name = data.getProfile().getFullName();
            tvName.setText(name == null || name.trim().isEmpty() ? "Khách hàng Kanila" : name);
            Glide.with(this)
                    .load(data.getProfile().getAvatarUrl())
                    .placeholder(R.drawable.ic_account)
                    .error(R.drawable.ic_account)
                    .into(ivAvatar);
        }

        if (data.getLoyalty() != null) {
            String points = String.format(Locale.US, "%,d", data.getLoyalty().getPointsBalance());
            tvPointsHeader.setText(points);
            tvPointsVal.setText(points);
            tvRankName.setText(data.getLoyalty().getTierName() == null ? "Thành viên" : data.getLoyalty().getTierName());
        }

        if (data.getStats() != null) {
            tvOrderCount.setText(String.valueOf(data.getStats().getOrderCount()));
            tvVoucherCount.setText(String.valueOf(data.getStats().getVoucherCount()));
            tvSavedCount.setText(String.valueOf(data.getStats().getWishlistCount()));
        }
    }
}
