package ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;
import ui.common.helper.ChatbotLoadingAnimator;

public class ChatSupportFragment extends Fragment {

    private ChatbotLoadingAnimator chatbotLoadingAnimator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_support, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatbotLoadingAnimator = new ChatbotLoadingAnimator(view);
        chatbotLoadingAnimator.start();
    }

    @Override
    public void onDestroyView() {
        if (chatbotLoadingAnimator != null) {
            chatbotLoadingAnimator.stop();
            chatbotLoadingAnimator = null;
        }

        super.onDestroyView();
    }
}