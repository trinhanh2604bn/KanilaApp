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
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.account.AccountViewModel;
import com.example.frontend.feature.checkout.CheckoutViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import ui.account.AccountAddressAdapter;

public class CheckoutAddressFragment extends Fragment {

    private RecyclerView rvAddressList;
    private AccountAddressAdapter adapter;
    private AccountViewModel accountViewModel;
    private CheckoutViewModel checkoutViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_checkout_address, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        checkoutViewModel = new ViewModelProvider(requireActivity()).get(CheckoutViewModel.class);

        // Quan trọng: Reset trạng thái cũ để tránh bị tự động pop khi vào lại
        accountViewModel.resetSetDefaultAccountAddressResult();

        setupHeader(view);
        setupAddressList(view);
        setupFooter(view);
        
        observeViewModel();
        accountViewModel.loadAccountAddresses();
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutTopBar);
        if (header == null) return;

        TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText(R.string.checkout_address_title);

        View btnBack = header.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
        
        View btnSearch = header.findViewById(R.id.btnTopBarSearch);
        if (btnSearch != null) btnSearch.setVisibility(View.GONE);
    }

    private void setupAddressList(View view) {
        rvAddressList = view.findViewById(R.id.rvCheckoutAddressList);
        if (rvAddressList != null) {
            rvAddressList.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new AccountAddressAdapter(new AccountAddressAdapter.OnAddressActionListener() {
                @Override
                public void onSetDefault(AddressDto address) {
                    accountViewModel.setDefaultAccountAddress(address.getId());
                }

                @Override
                public void onEdit(AddressDto address) {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.main, ui.account.AccountAddressAddFragment.newInstance(address))
                            .addToBackStack(null)
                            .commit();
                }

                @Override
                public void onAddressSelected(AddressDto address) {
                    // Không làm gì cả, user chọn bằng cách nhấn "Thiết lập mặc định"
                }
            });
            // Tắt chế độ chọn (radio button) để giống trang Account
            adapter.setSelectionMode(false);

            rvAddressList.setAdapter(adapter);
        }
    }

    private void setupFooter(View view) {
        MaterialButton btnAdd = view.findViewById(R.id.btnAddNewAddress);
        if (btnAdd != null) {
            btnAdd.setText(R.string.address_book_add_new);
            btnAdd.setOnClickListener(v -> 
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main, new ui.account.AccountAddressAddFragment())
                        .addToBackStack(null)
                        .commit()
            );
        }
    }

    private void observeViewModel() {
        accountViewModel.getAccountAddressesResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case SUCCESS:
                    if (result.data != null) {
                        adapter.setAddresses(result.data);
                    }
                    break;
                case EMPTY:
                    adapter.setAddresses(new ArrayList<>());
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        accountViewModel.getSetDefaultAccountAddressResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                if (result.data != null) {
                    // Cập nhật địa chỉ được chọn cho màn hình Checkout
                    checkoutViewModel.setSelectedAddress(result.data);
                    
                    // Sau khi chọn thành công thì quay lại Checkout
                    Toast.makeText(getContext(), "Đã chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                }
                
                // Reset kết quả để không bị lặp lại logic khi vào lại fragment
                accountViewModel.resetSetDefaultAccountAddressResult();
                accountViewModel.loadAccountAddresses();
            }
        });
    }
}
