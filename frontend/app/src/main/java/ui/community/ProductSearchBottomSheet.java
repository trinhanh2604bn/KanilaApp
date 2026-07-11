package ui.community;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.databinding.BottomSheetProductSearchBinding;
import com.example.frontend.model.Product;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProductSearchBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetProductSearchBinding binding;
    private ProductSearchAdapter adapter;
    private ProductRepository productRepository;
    private OnProductSelectedListener listener;

    public interface OnProductSelectedListener {
        void onProductSelected(Product product);
    }

    public static ProductSearchBottomSheet newInstance(OnProductSelectedListener listener) {
        ProductSearchBottomSheet fragment = new ProductSearchBottomSheet();
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetProductSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productRepository = new ProductRepository(requireContext());
        setupRecyclerView();
        setupSearch();
        setupListeners();

        // Load suggestions initially
        searchProducts("");
    }

    private void setupRecyclerView() {
        adapter = new ProductSearchAdapter(product -> {
            if (listener != null) {
                listener.onProductSelected(product);
            }
            dismiss();
        });
        binding.rvProducts.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchProducts(binding.edtSearch.getText().toString());
                return true;
            }
            return false;
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    searchProducts(s.toString());
                } else if (s.length() == 0) {
                    searchProducts("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupListeners() {
        binding.btnClose.setOnClickListener(v -> dismiss());
    }

    private void searchProducts(String query) {
        productRepository.getProducts(query, null, null).observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    // Optionally show a loading indicator in rvProducts or elsewhere
                    break;
                case SUCCESS:
                    if (result.data != null) {
                        adapter.setProducts(result.data);
                    }
                    break;
                case ERROR:
                    android.util.Log.e("ProductSearch", "Search error: " + result.message);
                    break;
                case EMPTY:
                    adapter.setProducts(new java.util.ArrayList<>());
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
