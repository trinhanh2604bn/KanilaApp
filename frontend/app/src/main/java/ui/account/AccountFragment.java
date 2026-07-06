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

import ui.commerce.PaymentMethodFragment;
import ui.common.BottomNavigationHelper;

public class AccountFragment extends Fragment {

    private AccountViewModel viewModel;
    
    private View layoutLoading, layoutError, layoutContent;
    private ImageView ivAvatar;
    private TextView tvName, tvRankName, tvPointsHeader;
    
    private View itemOrders, itemVouchers, itemPoints, itemSaved;
    private View menuBeautyProfile, menuSkinJourney, menuAddress, menuPayment, menuSettings, menuSupport;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_account_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        
        initViews(view);
        setupStatCards();
        setupMenuItems();
        setupHeader(view);
        setupBottomNavigation(view);
        observeViewModel();
        
        loadData();
    }

    private void initViews(View view) {
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutError = view.findViewById(R.id.layoutError);
        layoutContent = view.findViewById(R.id.layoutContent);

        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvName = view.findViewById(R.id.tvName);
        tvRankName = view.findViewById(R.id.tvRankName);
        tvPointsHeader = view.findViewById(R.id.tvPointsHeader);
        
        itemOrders = view.findViewById(R.id.itemOrders);
        itemVouchers = view.findViewById(R.id.itemVouchers);
        itemPoints = view.findViewById(R.id.itemPoints);
        itemSaved = view.findViewById(R.id.itemSaved);

        menuBeautyProfile = view.findViewById(R.id.menuBeautyProfile);
        menuSkinJourney = view.findViewById(R.id.menuSkinJourney);
        menuAddress = view.findViewById(R.id.menuAddress);
        menuPayment = view.findViewById(R.id.menuPayment);
        menuSettings = view.findViewById(R.id.menuSettings);
        menuSupport = view.findViewById(R.id.menuSupport);

        View btnErrorRetry = layoutError.findViewById(R.id.btnErrorRetry);
        if (btnErrorRetry != null) {
            btnErrorRetry.setOnClickListener(v -> loadData());
        }

        view.findViewById(R.id.btnEdit).setOnClickListener(v -> {
            // Navigate to Edit Profile
        });
    }

    private void setupStatCards() {
        setupStatCard(itemOrders, "Đơn hàng", R.drawable.ic_cart);
        setupStatCard(itemVouchers, "Ví voucher", R.drawable.ic_coupon);
        setupStatCard(itemPoints, "Điểm thưởng", R.drawable.ic_star);
        setupStatCard(itemSaved, "Đã lưu", R.drawable.ic_heart);
    }

    private void setupMenuItems() {
        setupMenuItem(menuBeautyProfile, "Beauty Profile", R.drawable.ic_account);
        setupMenuItem(menuSkinJourney, "Skin Journey", R.drawable.ic_drops);
        setupMenuItem(menuAddress, "Địa chỉ giao hàng", R.drawable.ic_location);
        setupMenuItem(menuPayment, "Phương thức thanh toán", R.drawable.ic_paymeny_card);
        setupMenuItem(menuSettings, "Cài đặt", R.drawable.ic_settings);
        setupMenuItem(menuSupport, "Trung tâm hỗ trợ", R.drawable.ic_support);
    }

    private void setupStatCard(View card, String title, int iconRes) {
        if (card == null) return;
        TextView tvTitle = card.findViewById(R.id.tvStatTitle);
        ImageView ivIcon = card.findViewById(R.id.ivStatIcon);
        if (tvTitle != null) tvTitle.setText(title);
        if (ivIcon != null) ivIcon.setImageResource(iconRes);
    }

    private void setupMenuItem(View item, String title, int iconRes) {
        if (item == null) return;
        TextView tvTitle = item.findViewById(R.id.tvMenuTitle);
        ImageView ivIcon = item.findViewById(R.id.ivMenuIcon);
        if (tvTitle != null) tvTitle.setText(title);
        if (ivIcon != null) ivIcon.setImageResource(iconRes);
    }

    private void setupHeader(View view) {
        View btnNotification = view.findViewById(R.id.btnNotification);
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                    .replace(R.id.main, new ui.notification.NotificationCenterFragment())
                    .addToBackStack(null)
                    .commit());
        }
    }

    private void setupBottomNavigation(View view) {
        BottomNavigationHelper.setup(view, tabIndex -> {
            if (tabIndex == BottomNavigationHelper.TAB_HOME) {
                if (getActivity() != null) getActivity().onBackPressed();
            } else if (tabIndex == BottomNavigationHelper.TAB_CATEGORY) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main, new ui.category.ProductCategoryFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
        BottomNavigationHelper.setSelectedTab(view, BottomNavigationHelper.TAB_ACCOUNT);
    }

    private void loadData() {
        viewModel.loadProfileHub();
    }

    private void observeViewModel() {
        viewModel.getProfileHubResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    showLoading();
                    break;
                case SUCCESS:
                    showContent();
                    bindData(result.data);
                    break;
                case ERROR:
                    showError(result.message);
                    break;
            }
        });
    }

    private void showLoading() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        layoutContent.setVisibility(View.GONE);
    }

    private void showContent() {
        layoutLoading.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        layoutContent.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        layoutLoading.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.GONE);
        
        TextView tvErrorTitle = layoutError.findViewById(R.id.tvErrorTitle);
        if (tvErrorTitle != null) tvErrorTitle.setText(message);
    }

    private void bindData(ProfileHubDto data) {
        if (data == null) return;

        ProfileHubDto.AccountInfo profile = data.getProfile();
        if (profile != null) {
            tvName.setText(profile.getFullName());
            Glide.with(this)
                    .load(profile.getAvatarUrl())
                    .placeholder(R.drawable.ic_account)
                    .error(R.drawable.ic_account)
                    .into(ivAvatar);
        } else {
            tvName.setText("Người dùng Kanila");
        }

        ProfileHubDto.LoyaltyInfo loyalty = data.getLoyalty();
        if (loyalty != null) {
            String pointsStr = String.format(Locale.US, "%,d", loyalty.getPointsBalance());
            tvPointsHeader.setText(pointsStr);
            tvRankName.setText(loyalty.getTierName() != null ? loyalty.getTierName() : "Thành viên");
            
            setStatValue(itemPoints, pointsStr);
        }

        ProfileHubDto.StatsInfo stats = data.getStats();
        if (stats != null) {
            setStatValue(itemOrders, String.valueOf(stats.getOrderCount()));
            setStatValue(itemVouchers, String.valueOf(stats.getVoucherCount()));
            setStatValue(itemSaved, String.valueOf(stats.getWishlistCount()));
        }

        setupClickListeners(data);
    }

    private void setStatValue(View card, String value) {
        if (card == null) return;
        TextView tvValue = card.findViewById(R.id.tvStatValue);
        if (tvValue != null) tvValue.setText(value);
    }

    private void setupClickListeners(ProfileHubDto data) {
        if (menuBeautyProfile != null) {
            menuBeautyProfile.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                    .replace(R.id.main, new BeautyProfileOverviewFragment())
                    .addToBackStack(null)
                    .commit());
        }

        if (menuPayment != null) {
            menuPayment.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                    .replace(R.id.main, new PaymentMethodFragment())
                    .addToBackStack(null)
                    .commit());
        }

        if (itemSaved != null) {
            itemSaved.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                    .replace(R.id.main, new com.example.frontend.feature.wishlist.WishlistFragment())
                    .addToBackStack(null)
                    .commit());
        }

        // Additional click listeners for orders, vouchers etc. can be added here
    }
}
