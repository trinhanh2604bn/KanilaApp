package ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import com.example.frontend.feature.chatbot.ChatConversationFragment;
import ui.common.FragmentNavigationHelper;

public class ProductSupportFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_support, container, false);
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
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        view.findViewById(R.id.cardConsultation).setOnClickListener(v -> {
            replaceFragment(ChatConversationFragment.newInstance(""));
        });

        view.findViewById(R.id.btnScanQR).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng Quét mã QR đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btnIngredientLookup).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chuyển tới Tra cứu thành phần", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btnIrritationGuide).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Hướng dẫn xử lý kích ứng", Toast.LENGTH_SHORT).show();
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentNavigationHelper.replaceFragment(requireActivity(), fragment);
    }
}
