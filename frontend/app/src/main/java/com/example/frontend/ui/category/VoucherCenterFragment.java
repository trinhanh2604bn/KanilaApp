package com.example.frontend.ui.category;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.model.coupon.CouponDto;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.feature.product.ProductDetailFragment;
import com.example.frontend.feature.voucher.CouponViewModel;
import com.example.frontend.feature.voucher.VoucherAdapter;
import com.example.frontend.model.Product;
import ui.common.FragmentNavigationHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import android.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;

public class VoucherCenterFragment extends Fragment {

    private ProductRepository productRepository;
    private CartViewModel cartViewModel;
    private CouponViewModel couponViewModel;

    private VoucherProductAdapter summerProductAdapter;
    private VoucherProductAdapter forYouProductAdapter;
    private VoucherAdapter voucherAdapter;

    private RecyclerView rvSummerProducts, rvExclusiveVouchers, rvForYouProducts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_voucher_center, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        productRepository = new ProductRepository(requireContext());
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        couponViewModel = new ViewModelProvider(this).get(CouponViewModel.class);

        initViews(view);
        loadVoucherProducts();
        loadVouchers();
    }

    private void initViews(View view) {
        view.findViewById(R.id.btnVoucherBack).setOnClickListener(v -> {
            if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        rvSummerProducts = view.findViewById(R.id.rvVoucherSummerProducts);
        rvExclusiveVouchers = view.findViewById(R.id.rvExclusiveVouchers);
        rvForYouProducts = view.findViewById(R.id.rvVoucherForYouProducts);

        rvSummerProducts.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        rvExclusiveVouchers.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        );

        rvForYouProducts.setLayoutManager(
                new GridLayoutManager(requireContext(), 2)
        );

        rvSummerProducts.setNestedScrollingEnabled(false);
        rvExclusiveVouchers.setNestedScrollingEnabled(false);
        rvForYouProducts.setNestedScrollingEnabled(false);

        // Summer Products Adapter
        summerProductAdapter = new VoucherProductAdapter(true);
        summerProductAdapter.setOnProductClickListener(new VoucherProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                FragmentNavigationHelper.replaceFragment(getActivity(), ProductDetailFragment.newInstance(product.getId()));
            }

            @Override
            public void onAddToCartClick(Product product) {
                cartViewModel.addToCart(product.getId(), null, 1);
                Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
        rvSummerProducts.setAdapter(summerProductAdapter);

        // Voucher Adapter
        voucherAdapter = new VoucherAdapter();
        voucherAdapter.setOnVoucherClickListener(new VoucherAdapter.OnVoucherClickListener() {
            @Override
            public void onVoucherClick(CouponDto voucher) {
                // Handle voucher click if needed
            }

            @Override
            public void onCopyClick(CouponDto voucher) {
                copyToClipboard(voucher.getCouponCode());
            }
        });
        rvExclusiveVouchers.setAdapter(voucherAdapter);

        // For You Products Adapter
        forYouProductAdapter = new VoucherProductAdapter(false);
        forYouProductAdapter.setOnProductClickListener(new VoucherProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                FragmentNavigationHelper.replaceFragment(getActivity(), ProductDetailFragment.newInstance(product.getId()));
            }

            @Override
            public void onAddToCartClick(Product product) {
                cartViewModel.addToCart(product.getId(), null, 1);
                Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
        rvForYouProducts.setAdapter(forYouProductAdapter);
    }

    private void loadVoucherProducts() {
        Map<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("limit", "50");
        query.put("fields", "card");
        query.put("sort", "popular");

        productRepository.getProductsByQuery(query).observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.status) {
                case SUCCESS:
                    List<Product> products = result.data != null
                            ? new ArrayList<>(result.data)
                            : new ArrayList<>();
                    bindRandomProductSections(products);
                    break;

                case EMPTY:
                    bindRandomProductSections(new ArrayList<>());
                    break;

                case ERROR:
                    Toast.makeText(getContext(), result.message != null ? result.message : "Không tải được sản phẩm", Toast.LENGTH_SHORT).show();
                    bindRandomProductSections(new ArrayList<>());
                    break;
            }
        });
    }

    private void bindRandomProductSections(List<Product> products) {
        if (products == null || products.isEmpty()) {
            summerProductAdapter.submitList(new ArrayList<>());
            forYouProductAdapter.submitList(new ArrayList<>());
            return;
        }

        List<Product> summer = new ArrayList<>(products);
        Collections.shuffle(summer, new Random(2026L));

        List<Product> forYou = new ArrayList<>(products);
        Collections.shuffle(forYou, new Random(9090L));

        summerProductAdapter.submitList(
            summer.subList(0, Math.min(10, summer.size()))
        );

        forYouProductAdapter.submitList(
            forYou.subList(0, Math.min(20, forYou.size()))
        );
    }

    private void loadVouchers() {
        couponViewModel.getAvailableCouponsResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) {
                Log.d("VoucherCenter", "voucher result = null");
                return;
            }

            Log.d(
                    "VoucherCenter",
                    "voucher status = " + result.status
                            + ", size = " + (result.data == null ? "null" : result.data.size())
                            + ", message = " + result.message
            );

            switch (result.status) {
                case SUCCESS:
                    if (result.data != null && !result.data.isEmpty()) {
                        voucherAdapter.setVouchers(result.data);
                        Log.d("VoucherCenter", "submit vouchers size = " + result.data.size());
                    } else {
                        voucherAdapter.setVouchers(new ArrayList<>());
                        Log.d("VoucherCenter", "voucher data empty");
                    }
                    break;

                case EMPTY:
                    voucherAdapter.setVouchers(new ArrayList<>());
                    Log.d("VoucherCenter", "voucher EMPTY");
                    break;

                case ERROR:
                    voucherAdapter.setVouchers(new ArrayList<>());
                    Toast.makeText(
                            getContext(),
                            result.message != null ? result.message : "Không tải được voucher",
                            Toast.LENGTH_SHORT
                    ).show();
                    break;
            }
        });

        couponViewModel.loadAvailableCoupons();
    }
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Voucher Code", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Đã lưu '" + text + "' thành công", Toast.LENGTH_SHORT).show();
    }
}
