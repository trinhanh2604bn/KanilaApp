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
import com.example.frontend.feature.checkout.CheckoutAddressViewModel;
import com.example.frontend.feature.checkout.CheckoutViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class CheckoutAddressFragment extends Fragment {

    private RecyclerView rvAddressList;
    private CheckoutAddressAdapter adapter;
    private CheckoutAddressViewModel viewModel;
    private CheckoutViewModel checkoutViewModel;
    private com.example.frontend.feature.account.AccountViewModel accountViewModel;

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
        accountViewModel = new ViewModelProvider(requireActivity()).get(com.example.frontend.feature.account.AccountViewModel.class);

        setupHeader(view);
        setupAddressList(view);
        setupFooter(view);
        
        observeViewModel();
        viewModel.loadCustomerAddresses();
        
        if (com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
            accountViewModel.loadAccountAddresses();
        }
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
                    showAddressConfirmationDialog(address);
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

                @Override
                public void onSaveToAccount(AddressDto address, int position) {
                    saveAddressToAccount(address);
                }
            });
            rvAddressList.setAdapter(adapter);
            setupSwipeToDelete();
        }
    }

    private void setupSwipeToDelete() {
        androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback swipeCallback = new androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(0, androidx.recyclerview.widget.ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                AddressDto address = adapter.getAddressAt(position);
                
                if (address != null) {
                    showDeleteConfirmationDialog(address, position);
                }
            }

            @Override
            public void onChildDraw(@NonNull android.graphics.Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // Thêm hiệu ứng màu đỏ khi trượt
                new ui.common.SwipeToDeleteDecorator(requireContext()).onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new androidx.recyclerview.widget.ItemTouchHelper(swipeCallback).attachToRecyclerView(rvAddressList);
    }

    private void showDeleteConfirmationDialog(AddressDto address, int position) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa địa chỉ này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteAddress(address.getId());
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    adapter.notifyItemChanged(position); // Khôi phục lại item nếu không xóa
                })
                .setOnCancelListener(dialog -> {
                    adapter.notifyItemChanged(position);
                })
                .show();
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
        viewModel.getDeleteResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    Toast.makeText(getContext(), "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                    viewModel.loadCustomerAddresses();
                    viewModel.clearDeleteResult();
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    viewModel.clearDeleteResult();
                    break;
            }
        });

        viewModel.getSetDefaultResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    Toast.makeText(getContext(), "Đã thiết lập địa chỉ mặc định", Toast.LENGTH_SHORT).show();
                    viewModel.loadCustomerAddresses(); // Refresh list
                    viewModel.clearSetDefaultResult();
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    viewModel.clearSetDefaultResult();
                    break;
            }
        });

        viewModel.getSaveResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.status == NetworkResult.Status.SUCCESS) {
                // Just refresh list when an address is saved/updated elsewhere
                viewModel.loadCustomerAddresses();
                if (com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
                    accountViewModel.loadAccountAddresses();
                }
            }
        });

        accountViewModel.getAccountAddressesResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                if (adapter != null) {
                    adapter.setAccountAddresses(result.data);
                }
            }
        });

        accountViewModel.getAddAccountAddressResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                Toast.makeText(getContext(), "Đã lưu vào sổ địa chỉ", Toast.LENGTH_SHORT).show();
                accountViewModel.loadAccountAddresses(); // Refresh the list to hide buttons
                accountViewModel.resetAddAccountAddressResult();
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

    private void showAddressConfirmationDialog(AddressDto address) {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_address, null);
        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Bind data
        TextView tvMessage = dialogView.findViewById(R.id.tvDialogMessage);
        if (tvMessage != null) {
            tvMessage.setText("Bạn có muốn đổi địa chỉ giao hàng thành:\n" + address.getRecipientName() + " - " + address.getPhone() + "?");
        }

        View btnCancel = dialogView.findViewById(R.id.btnDialogCancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> dialog.dismiss());
        }

        View btnConfirm = dialogView.findViewById(R.id.btnDialogConfirm);
        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> {
                viewModel.selectAddress(address);
                checkoutViewModel.setSelectedAddress(address);
                dialog.dismiss();
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }

        View btnClose = dialogView.findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();
    }

    private void saveAddressToAccount(AddressDto address) {
        if (!com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để lưu địa chỉ", Toast.LENGTH_SHORT).show();
            com.example.frontend.core.auth.AuthNavigationHelper.showAuthPrompt(requireActivity(),
                    new com.example.frontend.core.auth.PendingAuthAction(
                            com.example.frontend.core.auth.PendingAuthAction.ActionType.OPEN_ADDRESS_BOOK,
                            "CheckoutAddress", 0, null));
            return;
        }

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("full_name", address.getRecipientName());
        data.put("phone", address.getPhone());
        data.put("address_line", address.getFullAddress());
        data.put("is_default", false);

        accountViewModel.addAccountAddress(data);
        Toast.makeText(getContext(), "Đang lưu vào sổ địa chỉ...", Toast.LENGTH_SHORT).show();
    }
}
