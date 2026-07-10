package ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import com.example.frontend.feature.chatbot.ChatConversationFragment;
import com.google.android.material.tabs.TabLayout;
import ui.common.FragmentNavigationHelper;

public class SupportHistoryFragment extends Fragment {

    private TabLayout tabLayout;
    private LinearLayout layoutTicketsContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_support_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupEvents(view);
        filterTickets(0); 
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        layoutTicketsContainer = view.findViewById(R.id.layoutTicketsContainer);
    }

    private void setupEvents(View view) {
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });

        view.findViewById(R.id.btnSupportIcon).setOnClickListener(v -> {
            replaceFragment(new ChatConversationFragment());
        });

        // Điều hướng chi tiết khi click vào từng item
        view.findViewById(R.id.itemTicket1).setOnClickListener(v -> 
            navigateToDetail("#SR24060031", "Đổi trả sản phẩm bị lỗi")
        );
        view.findViewById(R.id.itemTicket2).setOnClickListener(v -> 
            navigateToDetail("#SR24060012", "Hỏi về đơn hàng")
        );
        view.findViewById(R.id.itemTicket3).setOnClickListener(v -> 
            navigateToDetail("#SR24059980", "Tư vấn sản phẩm")
        );
        view.findViewById(R.id.itemTicket4).setOnClickListener(v -> 
            navigateToDetail("#SR24059876", "Vấn đề thanh toán")
        );

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterTickets(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void navigateToDetail(String id, String type) {
        replaceFragment(TicketDetailFragment.newInstance(id, type));
    }

    private void filterTickets(int position) {
        if (layoutTicketsContainer == null) return;

        View t1 = layoutTicketsContainer.findViewById(R.id.itemTicket1);
        View t2 = layoutTicketsContainer.findViewById(R.id.itemTicket2);
        View t3 = layoutTicketsContainer.findViewById(R.id.itemTicket3);
        View t4 = layoutTicketsContainer.findViewById(R.id.itemTicket4);

        int openCount = 0;
        int completedCount = 0;
        if (t1 != null) openCount++;
        if (t2 != null) openCount++;
        if (t3 != null) completedCount++;
        if (t4 != null) completedCount++;
        
        if (tabLayout.getTabAt(0) != null) tabLayout.getTabAt(0).setText("Đang mở (" + openCount + ")");
        if (tabLayout.getTabAt(1) != null) tabLayout.getTabAt(1).setText("Đã hoàn tất (" + completedCount + ")");

        if (position == 0) {
            if (t1 != null) t1.setVisibility(View.VISIBLE);
            if (t2 != null) t2.setVisibility(View.VISIBLE);
            if (t3 != null) t3.setVisibility(View.GONE);
            if (t4 != null) t4.setVisibility(View.GONE);
        } else {
            if (t1 != null) t1.setVisibility(View.GONE);
            if (t2 != null) t2.setVisibility(View.GONE);
            if (t3 != null) t3.setVisibility(View.VISIBLE);
            if (t4 != null) t4.setVisibility(View.VISIBLE);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentNavigationHelper.replaceFragment(requireActivity(), fragment);
    }
}
