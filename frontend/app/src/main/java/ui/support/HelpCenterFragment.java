package ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import ui.common.FragmentNavigationHelper;

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

        view.findViewById(R.id.catOrders).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Đơn hàng"));
        });

        view.findViewById(R.id.catReturns).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Đổi trả"));
        });

        view.findViewById(R.id.catPayment).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Thanh toán"));
        });

        view.findViewById(R.id.catAccount).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Tài khoản"));
        });

        view.findViewById(R.id.catProduct).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Sản phẩm"));
        });

        view.findViewById(R.id.catPromotion).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Khuyến mãi"));
        });

        view.findViewById(R.id.chatNowFooter).setOnClickListener(v -> {
            replaceFragment(new ChatConversationFragment());
        });

        // "Xem tất cả" FAQ button
        view.findViewById(R.id.btnViewAllFaqs).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Tất cả câu hỏi"));
        });

        // FAQ manual items (Example: FAQ 1)
        view.findViewById(R.id.faq1).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Đơn hàng"));
        });

        // More Info Section
        view.findViewById(R.id.itemPrioritySupport).setOnClickListener(v -> {
            replaceFragment(new ChatConversationFragment());
        });

        view.findViewById(R.id.itemCallSupport).setOnClickListener(v -> {
            // Action for calling
            try {
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_DIAL);
                intent.setData(android.net.Uri.parse("tel:19001234"));
                startActivity(intent);
            } catch (Exception e) {
                android.widget.Toast.makeText(requireContext(), "Không thể thực hiện cuộc gọi", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentNavigationHelper.replaceFragment(requireActivity(), fragment);
    }
}
