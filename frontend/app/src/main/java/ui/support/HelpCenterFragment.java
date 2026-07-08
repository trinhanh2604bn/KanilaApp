package ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;

public class HelpCenterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_center, container, false);
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
            replaceFragment(new SupportHistoryFragment());
        });

        view.findViewById(R.id.catReturns).setOnClickListener(v -> {
            replaceFragment(new ReturnAssistantFragment());
        });

        view.findViewById(R.id.catOrders).setOnClickListener(v -> {
            replaceFragment(new OrderTrackingResultFragment());
        });

        view.findViewById(R.id.catPayment).setOnClickListener(v -> {
            replaceFragment(new PaymentSupportFragment());
        });

        view.findViewById(R.id.catPromotion).setOnClickListener(v -> {
            replaceFragment(new com.example.frontend.feature.voucher.VoucherListFragment());
        });

        view.findViewById(R.id.catProduct).setOnClickListener(v -> {
            replaceFragment(new ProductSupportFragment());
        });

        view.findViewById(R.id.chatNowFooter).setOnClickListener(v -> {
            replaceFragment(new ChatConversationFragment());
        });

        // FAQ 1
        view.findViewById(R.id.faq1).setOnClickListener(v -> {
            // Navigate to FAQ detail if available
        });
    }

    private void replaceFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.container7, fragment)
                .addToBackStack(null)
                .commit();
    }
}
