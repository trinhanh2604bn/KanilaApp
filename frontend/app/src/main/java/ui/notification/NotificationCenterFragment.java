package ui.notification;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.MainActivity;
import ui.common.FragmentNavigationHelper;

import java.util.ArrayList;
import java.util.List;

public class NotificationCenterFragment extends Fragment {

    private TextView tvFilterAll, tvFilterOrders, tvFilterOffers, tvFilterCommunity, tvFilterPersonal;
    private final List<TextView> filterTabs = new ArrayList<>();

    private RecyclerView rvNotifications;
    private View layoutNotifEmpty;
    private NotificationAdapter adapter;
    private NotificationViewModel viewModel;

    // Header views
    private ImageButton btnBack;
    private TextView tvTitle;
    private ImageButton btnCart;

    private int colorActiveText, colorActiveBg;
    private int colorInactiveText, colorInactiveBg;

    public static NotificationCenterFragment newInstance(@Nullable NotificationType initialFilter) {
        NotificationCenterFragment fragment = new NotificationCenterFragment();
        Bundle args = new Bundle();
        if (initialFilter != null) {
            args.putString("initial_filter", initialFilter.name());
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_notification_center, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initColors();
        initViews(view);
        setupHeader();
        setupRecyclerView();
        setupViewModel();
        setupListeners();
    }

    private void initColors() {
        colorActiveText = ContextCompat.getColor(requireContext(), R.color.background_main);
        colorActiveBg = ContextCompat.getColor(requireContext(), R.color.accent_dark);
        colorInactiveText = ContextCompat.getColor(requireContext(), R.color.text_main);
        colorInactiveBg = ContextCompat.getColor(requireContext(), R.color.background_sub);
    }

    private void initViews(View view) {
        // Toolbar views
        btnBack = view.findViewById(R.id.btnTopBarBack);
        tvTitle = view.findViewById(R.id.tvTopBarTitle);
        btnCart = view.findViewById(R.id.btnTopBarSearch);

        // Filters
        tvFilterAll = view.findViewById(R.id.tvFilterAll);
        tvFilterOrders = view.findViewById(R.id.tvFilterOrders);
        tvFilterOffers = view.findViewById(R.id.tvFilterOffers);
        tvFilterCommunity = view.findViewById(R.id.tvFilterCommunity);
        tvFilterPersonal = view.findViewById(R.id.tvFilterPersonal);

        filterTabs.add(tvFilterAll);
        filterTabs.add(tvFilterOrders);
        filterTabs.add(tvFilterOffers);
        filterTabs.add(tvFilterCommunity);
        filterTabs.add(tvFilterPersonal);

        rvNotifications = view.findViewById(R.id.rvNotifications);
        layoutNotifEmpty = view.findViewById(R.id.layoutNotifEmpty);
    }

    private void setupHeader() {
        if (tvTitle != null) {
            tvTitle.setText(R.string.notification_title);
            // Cài đặt title không click được và không có hiệu ứng hover
            tvTitle.setClickable(false);
            tvTitle.setFocusable(false);
            tvTitle.setOnClickListener(null);
        }

        if (btnCart != null) {
            // Thay icon kính lúp bằng icon giỏ hàng theo yêu cầu
            btnCart.setImageResource(R.drawable.ic_cart);
            btnCart.setContentDescription(getString(R.string.cart));
            btnCart.setVisibility(View.VISIBLE);
        }
        
        // Nút back kế thừa từ view_app_top_bar đã có icon ic_back
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter();
        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNotifications.setAdapter(adapter);
        adapter.setOnNotificationClickListener(this::openDetail);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        
        // Handle initial filter from arguments
        if (getArguments() != null && getArguments().containsKey("initial_filter")) {
            String filterName = getArguments().getString("initial_filter");
            try {
                NotificationType type = NotificationType.valueOf(filterName);
                viewModel.setFilter(type);
                updateTabsForType(type);
            } catch (IllegalArgumentException ignored) {}
        }

        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            adapter.setItems(state.items);
            boolean showEmpty = state.empty;
            layoutNotifEmpty.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
            rvNotifications.setVisibility(showEmpty ? View.GONE : View.VISIBLE);
        });
    }

    private void updateTabsForType(@Nullable NotificationType type) {
        TextView targetTab = tvFilterAll;
        if (type == NotificationType.ORDER) targetTab = tvFilterOrders;
        else if (type == NotificationType.OFFER) targetTab = tvFilterOffers;
        else if (type == NotificationType.COMMUNITY) targetTab = tvFilterCommunity;
        else if (type == NotificationType.PERSONAL) targetTab = tvFilterPersonal;
        
        updateTabs(targetTab);
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }

        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                FragmentNavigationHelper.loadFragment(getActivity(), new ui.commerce.CartFragment());
            });
        }

        for (TextView tab : filterTabs) {
            tab.setOnClickListener(v -> {
                updateTabs(tab);
                viewModel.setFilter(filterForTab(tab));
            });
        }
    }

    @Nullable
    private NotificationType filterForTab(TextView tab) {
        if (tab == tvFilterOrders) return NotificationType.ORDER;
        if (tab == tvFilterOffers) return NotificationType.OFFER;
        if (tab == tvFilterCommunity) return NotificationType.COMMUNITY;
        if (tab == tvFilterPersonal) return NotificationType.PERSONAL;
        return null; // tvFilterAll -> Tất cả
    }

    private void openDetail(NotificationItem item) {
        Intent intent = new Intent(requireContext(), NotificationDetailActivity.class);
        intent.putExtra(NotificationDetailActivity.EXTRA_NOTIF_TYPE, item.getType().name());
        intent.putExtra(NotificationDetailActivity.EXTRA_NOTIF_TITLE, item.getTitle());
        intent.putExtra(NotificationDetailActivity.EXTRA_NOTIF_CONTENT, item.getContent());
        intent.putExtra(NotificationDetailActivity.EXTRA_NOTIF_REF_ID, item.getRefId());
        startActivity(intent);
    }

    private void updateTabs(TextView selectedTab) {
        for (TextView tab : filterTabs) {
            if (tab == selectedTab) {
                tab.setBackgroundTintList(ColorStateList.valueOf(colorActiveBg));
                tab.setTextColor(colorActiveText);
            } else {
                tab.setBackgroundTintList(ColorStateList.valueOf(colorInactiveBg));
                tab.setTextColor(colorInactiveText);
            }
        }
    }
}
