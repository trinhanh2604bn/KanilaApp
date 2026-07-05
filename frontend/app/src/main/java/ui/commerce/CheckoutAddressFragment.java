package ui.commerce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CheckoutAddressFragment extends Fragment {

    private RecyclerView rvAddressList;
    private AddressAdapter adapter;
    private final List<AddressAdapter.Address> addressList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_checkout_address, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHeader(view);
        setupAddressList(view);
        setupFooter(view);
        loadAddressData();
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutTopBar);
        if (header == null) return;

        TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) {
            tvTitle.setText(R.string.checkout_address_title);
        }

        View btnSearch = header.findViewById(R.id.btnTopBarSearch);
        if (btnSearch != null) {
            btnSearch.setVisibility(View.GONE);
        }

        View btnBack = header.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }
    }

    private void setupAddressList(View view) {
        TextView tvSectionTitle = view.findViewById(R.id.tvAddressListTitle);
        if (tvSectionTitle != null) {
            // Using hardcoded text as per instruction if string resource doesn't exist
            // instructing that text should be set from Java
            tvSectionTitle.setText("Danh sách địa chỉ");
        }

        rvAddressList = view.findViewById(R.id.rvCheckoutAddressList);
        if (rvAddressList != null) {
            rvAddressList.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new AddressAdapter(addressList, new AddressAdapter.OnAddressClickListener() {
                @Override
                public void onAddressClick(AddressAdapter.Address address, int position) {
                    // TODO: Return selected address to checkout page if existing flow requires it
                }

                @Override
                public void onEditClick(AddressAdapter.Address address) {
                    // TODO: Open existing edit address flow if available
                }
            });
            rvAddressList.setAdapter(adapter);
        }
    }

    private void setupFooter(View view) {
        MaterialButton btnAdd = view.findViewById(R.id.btnAddNewAddress);
        if (btnAdd != null) {
            btnAdd.setText("Thêm địa chỉ mới");
            btnAdd.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main, new CheckoutAddressAddFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }
    }

    private void loadAddressData() {
        // Sample data matching the reference image for demonstration
        addressList.clear();
        addressList.add(new AddressAdapter.Address("Nguyễn Thị Mai", "0987654321", "Thủ Đức, TP.Hồ Chí Minh", true, true));
        addressList.add(new AddressAdapter.Address("Nguyễn Thị Mai", "0987654321", "Thủ Đức, TP.Hồ Chí Minh", false, false));
        addressList.add(new AddressAdapter.Address("Nguyễn Thị Mai", "0987654321", "Thủ Đức, TP.Hồ Chí Minh", false, false));
        addressList.add(new AddressAdapter.Address("Nguyễn Thị Mai", "0987654321", "Thủ Đức, TP.Hồ Chí Minh", false, false));
        
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
