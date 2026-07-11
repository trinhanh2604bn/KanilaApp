package ui.loyalty;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.remote.TokenManager;
import java.util.Locale;

public class LoyaltyFragment extends Fragment {

    private LoyaltyViewModel viewModel;
    private LoyaltyVoucherAdapter adapter;
    private View layoutLoading, layoutError, scrollLoyalty, layoutTierBanner;
    private TextView tvTierName, tvPointsBalance, tvOrderProgress, tvSpentProgress, tvOrderTarget, tvSpentTarget;
    private android.widget.ProgressBar progressOrders, progressSpent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_loyalty, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoyaltyViewModel.class);

        initViews(view);
        setupRecyclerView(view);
        setupMenuRows(view);
        observeViewModel(view);

        viewModel.loadAll();
    }

    private void initViews(View view) {
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutError = view.findViewById(R.id.layoutError);
        scrollLoyalty = view.findViewById(R.id.scrollLoyalty);
        layoutTierBanner = view.findViewById(R.id.layoutTierBanner);
        tvTierName = view.findViewById(R.id.tvTierName);
        tvPointsBalance = view.findViewById(R.id.tvPointsBalance);
        
        tvOrderProgress = view.findViewById(R.id.tvOrderProgress);
        tvSpentProgress = view.findViewById(R.id.tvSpentProgress);
        tvOrderTarget = view.findViewById(R.id.tvOrderTarget);
        tvSpentTarget = view.findViewById(R.id.tvSpentTarget);
        progressOrders = view.findViewById(R.id.progressOrders);
        progressSpent = view.findViewById(R.id.progressSpent);

        TextView tvUpdateInfo = view.findViewById(R.id.tvUpdateInfo);

        // Header
        View header = view.findViewById(R.id.layoutHeader);
        if (header != null) {
            TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
            if (tvTitle != null) tvTitle.setText(R.string.loyalty_title);
            View btnBack = header.findViewById(R.id.btnTopBarBack);
            if (btnBack != null) btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        // Tier Perks Link
        View btnTierPerks = view.findViewById(R.id.tvTierPerksLink);
        if (btnTierPerks != null) {
            btnTierPerks.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main, new TierPerksFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }
        
        // Default Info
        if (tvUpdateInfo != null) tvUpdateInfo.setText(getString(R.string.loyalty_update_date, "31.12.2026"));
    }

    private void setupRecyclerView(View view) {
        RecyclerView rv = view.findViewById(R.id.rvLoyaltyVouchers);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LoyaltyVoucherAdapter(voucher -> {
            if (TokenManager.getInstance(requireContext()).isLoggedIn()) {
                viewModel.saveCoupon(voucher.getId());
            } else {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để lưu ưu đãi", Toast.LENGTH_SHORT).show();
            }
        });
        rv.setAdapter(adapter);
    }

    private void setupMenuRows(View view) {
        View rowTier = view.findViewById(R.id.menuTierPerks);
        if (rowTier != null) {
            ((TextView) rowTier.findViewById(R.id.tvMenuTitle)).setText(getString(R.string.loyalty_exclusive_tier, "Thành Viên"));
            TextView tvDesc = rowTier.findViewById(R.id.tvMenuDescription);
            tvDesc.setText(R.string.loyalty_exclusive_desc_tier);
            tvDesc.setVisibility(View.VISIBLE);
            ((ImageView) rowTier.findViewById(R.id.ivMenuIcon)).setImageResource(R.drawable.ic_shortcut_royalty);
            
            rowTier.setOnClickListener(v -> openLoyaltyInfo());
        }

        View rowKanila = view.findViewById(R.id.menuKanilaPerks);
        if (rowKanila != null) {
            ((TextView) rowKanila.findViewById(R.id.tvMenuTitle)).setText(R.string.loyalty_exclusive_kanila);
            TextView tvDesc = rowKanila.findViewById(R.id.tvMenuDescription);
            tvDesc.setText(R.string.loyalty_exclusive_desc_kanila);
            tvDesc.setVisibility(View.VISIBLE);
            ((ImageView) rowKanila.findViewById(R.id.ivMenuIcon)).setImageResource(R.drawable.ic_gift);
            
            rowKanila.setOnClickListener(v -> openLoyaltyInfo());
        }
    }

    private void openLoyaltyInfo() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.main, new LoyaltyInfoFragment())
                .addToBackStack(null)
                .commit();
    }

    private void observeViewModel(View view) {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            layoutLoading.setVisibility(state.loading ? View.VISIBLE : View.GONE);
            layoutError.setVisibility(state.error != null ? View.VISIBLE : View.GONE);
            scrollLoyalty.setVisibility(state.error == null ? View.VISIBLE : View.GONE);

            if (state.loyalty != null) {
                // Rules: Bronze (0/0), Silver (3/1M), Gold (20/5M), Diamond (75/15M)
                int orders = state.loyalty.getOrderCount();
                double spent = state.loyalty.getSpentAmount();
                
                String tierName;
                int bannerBg;
                
                if (orders >= 75 && spent >= 15000000) {
                    tierName = "Kim Cương";
                    bannerBg = R.drawable.bg_banner_diamond;
                } else if (orders >= 20 && spent >= 5000000) {
                    tierName = "Vàng";
                    bannerBg = R.drawable.bg_banner_gold;
                } else if (orders >= 3 && spent >= 1000000) {
                    tierName = "Bạc";
                    bannerBg = R.drawable.bg_banner_silver;
                } else {
                    tierName = "Đồng";
                    bannerBg = R.drawable.bg_banner_bronze;
                }

                tvTierName.setText(tierName);
                if (layoutTierBanner != null) {
                    layoutTierBanner.setBackgroundResource(bannerBg);
                }
                tvPointsBalance.setText(getString(R.string.loyalty_points_balance, String.valueOf(state.loyalty.getPointsBalance())));
                
                // Progress stats
                if (tvOrderProgress != null) tvOrderProgress.setText(String.valueOf(orders));
                if (tvSpentProgress != null) tvSpentProgress.setText(formatPrice(spent));
                
                // Calculate Progress Bar (based on next tier)
                updateProgressBars(orders, spent);
                
                // Update menu row title with real tier name
                View rowTier = view.findViewById(R.id.menuTierPerks);
                if (rowTier != null) {
                    ((TextView) rowTier.findViewById(R.id.tvMenuTitle)).setText(getString(R.string.loyalty_exclusive_tier, tierName));
                }
            }

            if (state.coupons != null) {
                adapter.setVouchers(state.coupons);
            }

            if ("Success".equals(state.saveCouponResult)) {
                Toast.makeText(getContext(), "Đã lưu ưu đãi thành công!", Toast.LENGTH_SHORT).show();
            }

            if (state.error != null) {
                TextView tvErr = layoutError.findViewById(R.id.tvErrorTitle);
                if (tvErr != null) tvErr.setText(state.error);
                View btnRetry = layoutError.findViewById(R.id.btnErrorRetry);
                if (btnRetry != null) btnRetry.setOnClickListener(v -> viewModel.loadAll());
            }
        });
    }

    private void updateProgressBars(int orders, double spent) {
        if (progressOrders == null || progressSpent == null) return;
        
        int nextOrderTarget;
        double nextSpentTarget;
        
        // Tier thresholds
        // Silver: 3 orders / 1M
        // Gold: 20 orders / 5M
        // Diamond: 75 orders / 15M

        if (orders >= 75 || spent >= 15000000) {
            // Already Diamond or reached Diamond spend
            nextOrderTarget = 75;
            nextSpentTarget = 15000000;
        } else if (orders >= 20 || spent >= 5000000) {
            // Gold -> Diamond
            nextOrderTarget = 75;
            nextSpentTarget = 15000000;
        } else if (orders >= 3 || spent >= 1000000) {
            // Silver -> Gold
            nextOrderTarget = 20;
            nextSpentTarget = 5000000;
        } else {
            // Bronze -> Silver
            nextOrderTarget = 3;
            nextSpentTarget = 1000000;
        }
        
        if (tvOrderTarget != null) tvOrderTarget.setText("/" + nextOrderTarget);
        if (tvSpentTarget != null) tvSpentTarget.setText("/" + formatPrice(nextSpentTarget));

        progressOrders.setMax(nextOrderTarget);
        progressOrders.setProgress(Math.min(orders, nextOrderTarget));
        
        progressSpent.setMax(100);
        int spentPercent = (int) ((spent / nextSpentTarget) * 100);
        progressSpent.setProgress(Math.min(spentPercent, 100));
    }

    private String formatPrice(double price) {
        return java.text.NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(price)
                .replace("₫", "đ").replace(",00", "").trim();
    }
}
