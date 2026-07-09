package ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import com.google.android.material.tabs.TabLayout;
import ui.common.FragmentNavigationHelper;

public class SupportHistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_support_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupEvents(view);
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
            replaceFragment(new ChatSupportFragment());
        });

        view.findViewById(R.id.itemTicket1).setOnClickListener(v -> {
            replaceFragment(new TicketDetailFragment());
        });

        view.findViewById(R.id.itemTicket2).setOnClickListener(v -> {
            replaceFragment(new TicketDetailFragment());
        });

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Logic to filter list based on status
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentNavigationHelper.replaceFragment(requireActivity(), fragment);
    }
}
