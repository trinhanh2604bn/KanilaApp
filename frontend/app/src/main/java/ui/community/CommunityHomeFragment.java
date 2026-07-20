package ui.community;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.frontend.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CommunityHomeFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton btnSearch;
    private ImageButton btnProfile;
    private View layoutSearchBarContainer;
    private EditText edtSearch;
    private View layoutNotification;
    private TextView tvNotificationBadge;
    private CommunityViewModel viewModel;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    public static CommunityHomeFragment newInstance(int initialTab) {
        CommunityHomeFragment fragment = new CommunityHomeFragment();
        Bundle args = new Bundle();
        args.putInt("initial_tab", initialTab);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_home, container, false);
        initViews(view);
        setupViewModel();
        setupTabs();
        setupSearch();
        setupNotifications();
        setupHeaderActions();

        if (getArguments() != null && getArguments().containsKey("initial_tab")) {
            int initialTab = getArguments().getInt("initial_tab");
            viewPager.post(() -> viewPager.setCurrentItem(initialTab, false));
        }

        return view;
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        btnSearch = view.findViewById(R.id.btnSearch);
        layoutSearchBarContainer = view.findViewById(R.id.layoutSearchBarContainer);
        edtSearch = view.findViewById(R.id.edtSearch);
        btnProfile = view.findViewById(R.id.btnProfile);
        layoutNotification = view.findViewById(R.id.layoutNotification);
        tvNotificationBadge = view.findViewById(R.id.tvNotificationBadge);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CommunityViewModel.class);
        viewModel.getUnreadCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null && count > 0) {
                tvNotificationBadge.setVisibility(View.VISIBLE);
                tvNotificationBadge.setText(String.valueOf(count));
            } else {
                tvNotificationBadge.setVisibility(View.GONE);
            }
        });
    }

    private void setupTabs() {
        viewPager.setAdapter(new CommunityPagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            View customView = LayoutInflater.from(getContext()).inflate(R.layout.item_community_segment_tab, tabLayout, false);
            TextView tvTabTitle = customView.findViewById(R.id.tvTabTitle);
            switch (position) {
                case 0: tvTabTitle.setText(R.string.tab_feed); break;
                case 1: tvTabTitle.setText(R.string.tab_challenge); break;
                case 2: tvTabTitle.setText(R.string.tab_blog); break;
            }
            tab.setCustomView(customView);
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabStyle(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabStyle(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Initialize first tab style
        tabLayout.post(() -> {
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    updateTabStyle(tab, i == tabLayout.getSelectedTabPosition());
                }
            }
        });
    }

    private void updateTabStyle(TabLayout.Tab tab, boolean selected) {
        View view = tab.getCustomView();
        if (view != null && getContext() != null) {
            TextView tvTabTitle = view.findViewById(R.id.tvTabTitle);
            if (selected) {
                tvTabTitle.setBackgroundResource(R.drawable.bg_community_segment_selected);
                tvTabTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            } else {
                tvTabTitle.setBackground(null);
                tvTabTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_main));
            }
        }
    }

    private void setupSearch() {
        btnSearch.setOnClickListener(v -> {
            if (layoutSearchBarContainer.getVisibility() == View.VISIBLE) {
                layoutSearchBarContainer.setVisibility(View.GONE);
                hideKeyboard();
            } else {
                layoutSearchBarContainer.setVisibility(View.VISIBLE);
                edtSearch.requestFocus();
                showKeyboard();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> viewModel.setSearchQuery(s.toString());
                searchHandler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupNotifications() {
        layoutNotification.setOnClickListener(v -> {
            if (com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
                CommunityNotificationBottomSheet bottomSheet = new CommunityNotificationBottomSheet();
                bottomSheet.show(getChildFragmentManager(), "CommunityNotificationBottomSheet");
            } else {
                com.example.frontend.feature.auth.GuestPromptBottomSheet.newInstance(
                        com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION
                ).show(getChildFragmentManager(), "GuestPromptBottomSheet");
            }
        });
    }

    private void setupHeaderActions() {
        btnProfile.setOnClickListener(v -> {
            if (ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.OPEN_ACCOUNT)) {
                if (getActivity() instanceof com.example.frontend.MainActivity) {
                    ((com.example.frontend.MainActivity) getActivity()).loadFragment(new CommunityProfileFragment());
                }
            }
        });
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
        }
    }

    private static class CommunityPagerAdapter extends FragmentStateAdapter {
        public CommunityPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new CommunityFeedFragment();
                case 1: return new CommunityChallengeFragment();
                case 2: return new CommunityBlogFragment();
                default: return new PlaceholderFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
