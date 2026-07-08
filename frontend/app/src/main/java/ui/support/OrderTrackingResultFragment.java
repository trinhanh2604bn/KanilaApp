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

public class OrderTrackingResultFragment extends Fragment {

    public static OrderTrackingResultFragment newInstance() {
        return new OrderTrackingResultFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_tracking_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        view.findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().onBackPressed());

        view.findViewById(R.id.actionViewOrderDetail).setOnClickListener(v -> 
            replaceFragment(new OrderDetailFragment())
        );

        view.findViewById(R.id.actionCreateSupport).setOnClickListener(v -> 
            replaceFragment(new CreateTicketFragment())
        );
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
