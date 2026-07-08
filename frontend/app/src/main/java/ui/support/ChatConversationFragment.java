package ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;

public class ChatConversationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_conversation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupEvents(view);
    }

    private void setupEvents(View view) {
        EditText edtMessage = view.findViewById(R.id.edtMessage);

        view.findViewById(R.id.btnMenu).setOnClickListener(v -> {
            ChatbotQuickMenuBottomSheet.newInstance().show(getParentFragmentManager(), "ChatbotQuickMenu");
        });

        view.findViewById(R.id.btnHistory).setOnClickListener(v -> {
            replaceFragment(new SupportHistoryFragment());
        });

        view.findViewById(R.id.btnSend).setOnClickListener(v -> {
            String message = edtMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                Toast.makeText(getContext(), "Đã gửi: " + message, Toast.LENGTH_SHORT).show();
                edtMessage.setText("");
            }
        });

        view.findViewById(R.id.suggestOilySkin).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tư vấn da dầu...", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.suggestOrderStatus).setOnClickListener(v -> {
            replaceFragment(new OrderTrackingResultFragment());
        });

        view.findViewById(R.id.suggestReturnPolicy).setOnClickListener(v -> {
            replaceFragment(new ReturnAssistantFragment());
        });
    }

    private void replaceFragment(Fragment fragment) {
        if (getActivity() == null) return;
        int containerId = R.id.main_fragment_container;
        getParentFragmentManager().beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }
}
