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

public class TicketDetailFragment extends Fragment {

    public static TicketDetailFragment newInstance() {
        return new TicketDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ticket_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        view.findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().onBackPressed());

        view.findViewById(R.id.btnAddInfo).setOnClickListener(v -> 
            Toast.makeText(getContext(), "Thêm thông tin", Toast.LENGTH_SHORT).show()
        );

        view.findViewById(R.id.btnCloseTicket).setOnClickListener(v -> 
            Toast.makeText(getContext(), "Đóng yêu cầu", Toast.LENGTH_SHORT).show()
        );

        view.findViewById(R.id.btnRate).setOnClickListener(v -> 
            Toast.makeText(getContext(), "Đánh giá hỗ trợ", Toast.LENGTH_SHORT).show()
        );
    }
}
