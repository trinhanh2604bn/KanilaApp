package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CommunityNotificationBottomSheet extends BottomSheetDialogFragment {

    private CommunityViewModel viewModel;
    private CommunityNotificationAdapter adapter;
    private RecyclerView rvNotifications;
    private View layoutEmpty;
    private TextView btnMarkAllRead;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_community_notifications, container, false);
        initViews(view);
        setupViewModel();
        return view;
    }

    private void initViews(View view) {
        rvNotifications = view.findViewById(R.id.rvNotifications);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        btnMarkAllRead = view.findViewById(R.id.btnMarkAllRead);
        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommunityNotificationAdapter(notification -> {
            viewModel.markNotificationAsRead(notification.getId());
            // TODO: Navigate to target based on targetType and targetId
        });
        rvNotifications.setAdapter(adapter);

        btnMarkAllRead.setOnClickListener(v -> {
            viewModel.markAllNotificationsAsRead();
            
            // Navigate to NotificationCenterFragment with COMMUNITY filter
            dismiss();
            if (getActivity() != null) {
                ui.common.FragmentNavigationHelper.loadFragment(
                        getActivity(), 
                        ui.notification.NotificationCenterFragment.newInstance(ui.notification.NotificationType.COMMUNITY)
                );
            }
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CommunityViewModel.class);
        viewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications == null || notifications.isEmpty()) {
                layoutEmpty.setVisibility(View.VISIBLE);
                rvNotifications.setVisibility(View.GONE);
                btnMarkAllRead.setVisibility(View.GONE);
            } else {
                layoutEmpty.setVisibility(View.GONE);
                rvNotifications.setVisibility(View.VISIBLE);
                btnMarkAllRead.setVisibility(View.VISIBLE);
                adapter.setNotifications(notifications);
            }
        });
    }
}
