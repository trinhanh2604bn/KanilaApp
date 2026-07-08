package ui.support;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HumanHandoffFragment extends Fragment {

    private LinearLayout chatContainer;
    private EditText edtMessage;
    private NestedScrollView chatScrollView;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_human_handoff, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        chatContainer = view.findViewById(R.id.chatContainer);
        edtMessage = view.findViewById(R.id.edtMessage);
        chatScrollView = view.findViewById(R.id.chatScrollView);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });

        view.findViewById(R.id.btnSendMessage).setOnClickListener(v -> sendMessage());

        view.findViewById(R.id.btnMore).setOnClickListener(this::showMoreMenu);

        // Suggestion actions
        view.findViewById(R.id.actionViewPolicy).setOnClickListener(v -> 
            ToastMessage("Đang tải chính sách đổi trả..."));
            
        view.findViewById(R.id.actionCreateReturn).setOnClickListener(v -> 
            replaceFragment(new ReturnAssistantFragment()));
    }

    private void showMoreMenu(View v) {
        PopupMenu popup = new PopupMenu(requireContext(), v);
        popup.getMenu().add(0, 1, 0, getString(R.string.menu_clear_chat));
        popup.getMenu().add(0, 2, 1, getString(R.string.btn_close_ticket));
        popup.getMenu().add(0, 3, 2, getString(R.string.btn_rate_support));
        popup.getMenu().add(0, 4, 3, getString(R.string.action_view_policy));

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    chatContainer.removeAllViews();
                    ToastMessage("Đã xóa lịch sử trò chuyện");
                    return true;
                case 2:
                    ToastMessage("Yêu cầu đã được đóng");
                    getParentFragmentManager().popBackStack();
                    return true;
                case 3:
                    // Navigate to rating or show rating dialog
                    ToastMessage("Cảm ơn bạn đã quan tâm đánh giá");
                    return true;
                case 4:
                    ToastMessage("Đang tải chính sách...");
                    return true;
            }
            return false;
        });
        popup.show();
    }

    private void sendMessage() {
        String message = edtMessage.getText().toString().trim();
        if (message.isEmpty()) return;

        // Add user message to UI
        addUserMessage(message);
        edtMessage.setText("");
        scrollToBottom();

        // Simulate advisor response after a short delay
        handler.postDelayed(() -> {
            if (isAdded()) {
                addAdvisorMessage("Cảm ơn bạn đã cung cấp thông tin. Nhân viên CSKH sẽ phản hồi bạn ngay lập tức ạ.");
                scrollToBottom();
            }
        }, 1500);
    }

    private void addUserMessage(String message) {
        View view = getLayoutInflater().inflate(R.layout.item_chat_user, chatContainer, false);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        TextView tvTime = view.findViewById(R.id.tvTime);
        TextView tvStatus = view.findViewById(R.id.tvStatus);

        tvMessage.setText(message);
        tvTime.setText(getCurrentTime());
        tvStatus.setText("✓"); // Sent status
        
        chatContainer.addView(view);
    }

    private void addAdvisorMessage(String message) {
        View view = getLayoutInflater().inflate(R.layout.item_chat_bot, chatContainer, false);
        TextView tvName = view.findViewById(R.id.tvBotName);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        TextView tvTime = view.findViewById(R.id.tvTime);

        tvName.setText("Linh – CSKH Kanila");
        tvMessage.setText(message);
        tvTime.setText(getCurrentTime());
        
        chatContainer.addView(view);
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    private void scrollToBottom() {
        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void ToastMessage(String msg) {
        android.widget.Toast.makeText(getContext(), msg, android.widget.Toast.LENGTH_SHORT).show();
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
