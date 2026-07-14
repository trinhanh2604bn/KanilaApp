package ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;

public class KocFreeSamplesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_koc_free_samples, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        view.findViewById(R.id.btnExplore).setOnClickListener(v -> {
            // Chuyển tới danh sách sản phẩm Hot
            Fragment hotProductsFragment = com.example.frontend.ui.category.ProductListingFragment.newCollectionInstance("hot", "Sản phẩm Hot");
            
            int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                    ? R.id.main_fragment_container : R.id.main;
            
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(containerId, hotProductsFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
}
