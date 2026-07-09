package ui.support;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;

public class InAppCallFragment extends Fragment {

    private TextView tvCallStatus;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int seconds = 0;
    private boolean isConnected = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_in_app_call, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        tvCallStatus = view.findViewById(R.id.tvCallStatus);
        
        view.findViewById(R.id.btnEndCall).setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });

        // Simulate connection
        handler.postDelayed(() -> {
            isConnected = true;
            startTimer();
        }, 2000);
    }

    private void startTimer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isConnected) {
                    int mins = seconds / 60;
                    int secs = seconds % 60;
                    String time = String.format("%02d:%02d", mins, secs);
                    tvCallStatus.setText(time);
                    seconds++;
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        isConnected = false;
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}
