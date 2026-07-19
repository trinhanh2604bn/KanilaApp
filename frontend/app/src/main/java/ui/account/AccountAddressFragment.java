package ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ui.account.AccountAddressAdapter;
import ui.account.AccountAddressAddFragment;

public class AccountAddressFragment extends Fragment {

    private AccountViewModel viewModel;
    private AccountAddressAdapter adapter;
    private RecyclerView rvAddressBook;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_address_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Đảm bảo container chính được hiển thị (phòng trường hợp MainActivity ẩn nó)
        View container = getActivity().findViewById(R.id.main_fragment_container);
        if (container != null) container.setVisibility(View.VISIBLE);
        View homeScroll = getActivity().findViewById(R.id.layoutHomeScroll);
        if (homeScroll != null) homeScroll.setVisibility(View.GONE);

        viewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);

        initViews(view);
        
        // Cập nhật tiêu đề trang
        android.widget.TextView tvTitle = view.findViewById(R.id.tvToolbarTitle);
        if (tvTitle != null) {
            tvTitle.setText("Địa chỉ của tôi");
        }

        observeViewModel();
        viewModel.loadAccountAddresses();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadAccountAddresses();
        }
    }

    private void initViews(View view) {
        rvAddressBook = view.findViewById(R.id.rvAddressBook);
        rvAddressBook.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new AccountAddressAdapter(new AccountAddressAdapter.OnAddressActionListener() {
            @Override
            public void onSetDefault(AddressDto address) {
                viewModel.setDefaultAccountAddress(address.getId());
            }

            @Override
            public void onEdit(AddressDto address) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main, AccountAddressAddFragment.newInstance(address))
                        .addToBackStack(null)
                        .commit();
            }
        });
        rvAddressBook.setAdapter(adapter);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        view.findViewById(R.id.btnAddAddress).setOnClickListener(v -> 
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main, new AccountAddressAddFragment())
                    .addToBackStack(null)
                    .commit()
        );
    }

    private void observeViewModel() {
        viewModel.getAccountAddressesResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.status == NetworkResult.Status.SUCCESS) {
                adapter.setAddresses(result.data);
            } else if (result.status == NetworkResult.Status.ERROR) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getAddAccountAddressResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                viewModel.loadAccountAddresses();
            }
        });

        viewModel.getDeleteAccountAddressResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                viewModel.loadAccountAddresses();
            }
        });

        viewModel.getSetDefaultAccountAddressResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                viewModel.loadAccountAddresses();
            }
        });
    }
}
