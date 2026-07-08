package ui.support;




import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ContactShipperBottomSheet extends BottomSheetDialogFragment {

    public static ContactShipperBottomSheet newInstance() {
        return new ContactShipperBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_contact_shipper, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.optionCall).setOnClickListener(v -> {
            replaceFragment(new InAppCallFragment());
            dismiss();
        });

        view.findViewById(R.id.optionMessage).setOnClickListener(v -> {
            replaceFragment(new ShipperChatFragment());
            dismiss();
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
