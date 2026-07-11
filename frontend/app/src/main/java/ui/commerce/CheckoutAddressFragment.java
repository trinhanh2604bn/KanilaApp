package ui.commerce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.data.model.address.AddressDto;
import com.example.frontend.feature.checkout.CheckoutAddressViewModel;
import com.example.frontend.feature.checkout.CheckoutViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CheckoutAddressFragment extends Fragment {

    private RecyclerView rvAddressList;
    private CheckoutAddressAdapter adapter;
    private CheckoutAddressViewModel viewModel;
    private CheckoutViewModel checkoutViewModel;

    private static final boolean USE_MOCK_ADDRESS_WHEN_UNAUTHORIZED = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_checkout_address, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CheckoutAddressViewModel.class);
        checkoutViewModel = new ViewModelProvider(requireActivity()).get(CheckoutViewModel.class);

        setupHeader(view);
        setupAddressList(view);
        setupFooter(view);
        
        observeViewModel();
        viewModel.loadCustomerAddresses();
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutTopBar);
        if (header == null) return;

        TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText(R.string.checkout_address_title);

        View btnBack = header.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
            });
        }
        
        View btnSearch = header.findViewById(R.id.btnTopBarSearch);
        if (btnSearch != null) btnSearch.setVisibility(View.GONE);
    }

    private void setupAddressList(View view) {
        rvAddressList = view.findViewById(R.id.rvCheckoutAddressList);
        if (rvAddressList != null) {
            rvAddressList.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new CheckoutAddressAdapter(new CheckoutAddressAdapter.OnAddressClickListener() {
                @Override
                public void onAddressSelected(AddressDto address, int position) {
                    viewModel.selectAddress(address);
                    checkoutViewModel.setSelectedAddress(address);
                    
                    // Return to checkout after selection
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }

                @Override
                public void onAddressEdit(AddressDto address, int position) {
                    if (getActivity() != null) {
                        CheckoutAddressAddFragment editFragment = new CheckoutAddressAddFragment();
                        Bundle args = new Bundle();
                        args.putString("address_id", address.getId());
                        editFragment.setArguments(args);
                        
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main, editFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }

                @Override
                public void onSetDefault(AddressDto address, int position) {
                    viewModel.setDefaultAddress(address.getId());
                }
            });
            rvAddressList.setAdapter(adapter);
        }
    }

    private void setupFooter(View view) {
        MaterialButton btnAdd = view.findViewById(R.id.btnAddNewAddress);
        if (btnAdd != null) {
            btnAdd.setText(R.string.address_book_add_new);
            btnAdd.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main, new CheckoutAddressAddFragment())
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
        
        TextView tvTitleList = view.findViewById(R.id.tvAddressListTitle);
        if (tvTitleList != null) {
            tvTitleList.setText(R.string.address_book_list_header);
        }
    }

    private void observeViewModel() {
        viewModel.getSaveResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    Toast.makeText(getContext(), "Đã thiết lập địa chỉ mặc định", Toast.LENGTH_SHORT).show();
                    viewModel.loadCustomerAddresses(); // Refresh list
                    viewModel.clearSaveResult();
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        viewModel.getAddressResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    // Optionally show loading state
                    break;
                case SUCCESS:
                    if (result.data != null) {
                        handleInitialSelection(result.data);
                        adapter.setAddresses(result.data);
                        // Make sure to apply selection UI state
                        AddressDto selected = checkoutViewModel.getSelectedAddress().getValue();
                        if (selected != null) {
                            adapter.setSelectedAddressId(selected.getId());
                        }
                    }
                    break;
                case EMPTY:
                    adapter.setAddresses(new ArrayList<>());
                    break;
                case ERROR:
                    if (result.message != null) {
                        Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case UNAUTHORIZED:
                    if (USE_MOCK_ADDRESS_WHEN_UNAUTHORIZED) {
                        List<AddressDto> mockAddresses = getMockAddresses();
                        handleInitialSelection(mockAddresses);
                        adapter.setAddresses(mockAddresses);
                    } else {
                        Toast.makeText(getContext(), "Vui lòng đăng nhập để xem địa chỉ", Toast.LENGTH_SHORT).show();
                        // Navigate to login if possible
                    }
                    break;
            }
        });
    }

    private List<AddressDto> getMockAddresses() {
        List<AddressDto> mockList = new ArrayList<>();

        AddressDto addr1 = new AddressDto();
        addr1.setId("mock_1");
        addr1.setRecipientName("Nguyễn Thị Mai (Demo)");
        addr1.setPhone("0987654321");
        addr1.setCity("TP. Hồ Chí Minh");
        addr1.setDistrict("Thủ Đức");
        addr1.setWard("Linh Trung");
        addr1.setAddressLine1("Khu phố 6");
        addr1.setDefaultShipping(true);
        mockList.add(addr1);

        AddressDto addr2 = new AddressDto();
        addr2.setId("mock_2");
        addr2.setRecipientName("Trần Văn An (Demo)");
        addr2.setPhone("0123456789");
        addr2.setCity("Hà Nội");
        addr2.setDistrict("Cầu Giấy");
        addr2.setWard("Dịch Vọng");
        addr2.setAddressLine1("Số 123 Cầu Giấy");
        addr2.setDefaultShipping(false);
        mockList.add(addr2);

        return mockList;
    }

    private void handleInitialSelection(List<AddressDto> addresses) {
        if (addresses == null || addresses.isEmpty()) return;
        
        // If already has selection in checkoutViewModel, keep it
        if (checkoutViewModel.getSelectedAddress().getValue() != null) {
            return;
        }
        
        // Else find default shipping
        for (AddressDto address : addresses) {
            if (address.isDefaultShipping()) {
                address.setSelected(true);
                viewModel.selectAddress(address);
                checkoutViewModel.setSelectedAddress(address);
                return;
            }
        }
        
        // Else select first
        AddressDto first = addresses.get(0);
        first.setSelected(true);
        viewModel.selectAddress(first);
        checkoutViewModel.setSelectedAddress(first);
    }
}
