package ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShipperChatFragment extends Fragment {

    private LinearLayout chatContainer;
    private EditText edtMessage;
    private NestedScrollView chatScrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shipper_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        chatContainer = view.findViewById(R.id.chatContainer);
        edtMessage = view.findViewById(R.id.edtMessage);
        chatScrollView = view.findViewById(R.id.chatScrollView);
        if (chatScrollView == null) {
            // Check ID from fragment_shipper_chat.xml
            chatScrollView = view.findViewById(R.id.chatScrollView);
        }

        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });

        view.findViewById(R.id.btnCallHeader).setOnClickListener(v -> {
            replaceFragment(new InAppCallFragment());
        });

        view.findViewById(R.id.btnSendMessage).setOnClickListener(v -> {
            sendMessage();
        });
    }

    private void sendMessage() {
        String text = edtMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        // Add user message
        addUserMessage(text);
        edtMessage.setText("");

        // Scroll to bottom
        scrollToBottom();

        // Simulate shipper reply
        edtMessage.postDelayed(() -> {
            if (isAdded()) {
                addShipperMessage("Dạ vâng ạ, em sẽ gọi anh khi tới nơi.");
                scrollToBottom();
            }
        }, 1500);
    }

    private void addUserMessage(String message) {
        View view = getLayoutInflater().inflate(R.layout.item_chat_user, chatContainer, false);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        TextView tvTime = view.findViewById(R.id.tvTime);
        
        tvMessage.setText(message);
        tvTime.setText(getCurrentTime());
        
        chatContainer.addView(view);
    }

    private void addShipperMessage(String message) {
        View view = getLayoutInflater().inflate(R.layout.item_chat_bot, chatContainer, false);
        TextView tvName = view.findViewById(R.id.tvBotName);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        TextView tvTime = view.findViewById(R.id.tvTime);
        
        tvName.setText("Shipper Nam");
        tvMessage.setText(message);
        tvTime.setText(getCurrentTime());
        
        chatContainer.addView(view);
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    private void scrollToBottom() {
        if (chatScrollView != null) {
            chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
        }
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
