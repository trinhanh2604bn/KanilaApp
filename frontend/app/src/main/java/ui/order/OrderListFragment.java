package ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment {

    private ViewPager2 vpOrderList;
    private HorizontalScrollView scrollTabs;
    private LinearLayout layoutTabs;
    
    private final List<TextView> tabViews = new ArrayList<>();
    private final String[] statusCodes = {null, "pending", "confirmed", "processing", "completed", "returned", "cancelled"};

    public static OrderListFragment newInstance(String initialStatus) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString("initial_status", initialStatus);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupHeader(view);
        setupTabs(view);
        setupViewPager();
        
        if (getArguments() != null && getArguments().containsKey("initial_status")) {
            String initialStatus = getArguments().getString("initial_status");
            int index = getTabIndexForStatus(initialStatus);
            vpOrderList.setCurrentItem(index, false);
            updateTabSelection(index);
        } else {
            updateTabSelection(0);
        }
    }

    private int getTabIndexForStatus(String status) {
        if (status == null) return 0;
        for (int i = 0; i < statusCodes.length; i++) {
            if (status.equals(statusCodes[i])) return i;
        }
        return 0;
    }

    private void initViews(View view) {
        vpOrderList = view.findViewById(R.id.vpOrderList);
        scrollTabs = (HorizontalScrollView) view.findViewById(R.id.layoutOrderTabs).getParent();
        layoutTabs = view.findViewById(R.id.layoutOrderTabs);
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutHeader);
        TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText("Đơn mua");

        ImageButton btnBack = header.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
        
        View rightAction = header.findViewById(R.id.layoutTopBarRightAction);
        if (rightAction != null) rightAction.setVisibility(View.GONE);
    }

    private void setupTabs(View view) {
        tabViews.clear();
        tabViews.add(view.findViewById(R.id.tabAll));
        tabViews.add(view.findViewById(R.id.tabPending));
        tabViews.add(view.findViewById(R.id.tabConfirmed));
        tabViews.add(view.findViewById(R.id.tabProcessing));
        tabViews.add(view.findViewById(R.id.tabCompleted));
        tabViews.add(view.findViewById(R.id.tabReturned));
        tabViews.add(view.findViewById(R.id.tabCancelled));

        for (int i = 0; i < tabViews.size(); i++) {
            final int index = i;
            TextView tab = tabViews.get(i);
            tab.setOnClickListener(v -> vpOrderList.setCurrentItem(index));
        }
    }

    private void setupViewPager() {
        vpOrderList.setAdapter(new OrderPagerAdapter(this));
        vpOrderList.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabSelection(position);
                scrollToTab(position);
            }
        });
    }

    private void updateTabSelection(int index) {
        for (int i = 0; i < tabViews.size(); i++) {
            TextView tab = tabViews.get(i);
            if (i == index) {
                tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.button));
            } else {
                tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_main));
            }
        }
    }

    private void scrollToTab(int index) {
        if (index < 0 || index >= tabViews.size()) return;
        View tabView = tabViews.get(index);
        
        int scrollX = (tabView.getLeft() - (scrollTabs.getWidth() / 2)) + (tabView.getWidth() / 2);
        scrollTabs.smoothScrollTo(scrollX, 0);
    }

    private class OrderPagerAdapter extends FragmentStateAdapter {
        public OrderPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return OrderTabContentFragment.newInstance(statusCodes[position]);
        }

        @Override
        public int getItemCount() {
            return statusCodes.length;
        }
    }
}
