package com.example.frontend.ui.category;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.feature.product.ProductDetailFragment;
import com.example.frontend.feature.product.QuickAddHelper;
import com.example.frontend.model.Product;

import androidx.core.content.ContextCompat;

import android.os.CountDownTimer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class FlashSaleFragment extends Fragment {

    private static final String TAG = "FlashSaleFragment";
    private RecyclerView rvFlashSalePageProducts;
    private FlashSalePageProductAdapter adapter;
    private ProductRepository productRepository;
    private CartViewModel cartViewModel;
    private View layoutFlashSaleLoading;
    private View layoutFlashSaleEmpty;

    private List<Product> allFlashSaleProducts = new ArrayList<>();
    private String selectedSessionKey = "NOW";

    private View layoutSessionNow;
    private View layoutSessionMidnight;
    private View layoutSessionTwo;
    private View layoutSessionNine;

    private TextView tvSessionNowTime;
    private TextView tvSessionNowStatus;
    private View viewSessionNowIndicator;

    private TextView tvSessionMidnightTime;
    private TextView tvSessionMidnightStatus;
    private View viewSessionMidnightIndicator;

    private TextView tvSessionTwoTime;
    private TextView tvSessionTwoStatus;
    private View viewSessionTwoIndicator;

    private TextView tvSessionNineTime;
    private TextView tvSessionNineStatus;
    private View viewSessionNineIndicator;

    private TextView tvFlashSalePageHour;
    private TextView tvFlashSalePageMinute;
    private TextView tvFlashSalePageSecond;
    private TextView tvFlashSalePageCountdownLabel;

    private CountDownTimer flashSaleCountDownTimer;
    private static final long ONE_SECOND = 1000L;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_flash_sale, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productRepository = new ProductRepository(requireContext());
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupSessionClicks();
        loadFlashSaleProducts();
        startCountdownForSession(selectedSessionKey);
    }

    private void initViews(View root) {
        rvFlashSalePageProducts = root.findViewById(R.id.rvFlashSalePageProducts);
        layoutFlashSaleLoading = root.findViewById(R.id.layoutFlashSaleLoading);
        layoutFlashSaleEmpty = root.findViewById(R.id.layoutFlashSaleEmpty);

        ImageButton btnBack = root.findViewById(R.id.btnFlashSaleBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }

        ImageButton btnSearch = root.findViewById(R.id.btnFlashSaleSearch);
        if (btnSearch != null) {
            btnSearch.setOnClickListener(v -> {
                // Future search implementation
            });
        }

        layoutSessionNow = root.findViewById(R.id.layoutSessionNow);
        layoutSessionMidnight = root.findViewById(R.id.layoutSessionMidnight);
        layoutSessionTwo = root.findViewById(R.id.layoutSessionTwo);
        layoutSessionNine = root.findViewById(R.id.layoutSessionNine);

        tvSessionNowTime = root.findViewById(R.id.tvSessionNowTime);
        tvSessionNowStatus = root.findViewById(R.id.tvSessionNowStatus);
        viewSessionNowIndicator = root.findViewById(R.id.viewSessionNowIndicator);

        tvSessionMidnightTime = root.findViewById(R.id.tvSessionMidnightTime);
        tvSessionMidnightStatus = root.findViewById(R.id.tvSessionMidnightStatus);
        viewSessionMidnightIndicator = root.findViewById(R.id.viewSessionMidnightIndicator);

        tvSessionTwoTime = root.findViewById(R.id.tvSessionTwoTime);
        tvSessionTwoStatus = root.findViewById(R.id.tvSessionTwoStatus);
        viewSessionTwoIndicator = root.findViewById(R.id.viewSessionTwoIndicator);

        tvSessionNineTime = root.findViewById(R.id.tvSessionNineTime);
        tvSessionNineStatus = root.findViewById(R.id.tvSessionNineStatus);
        viewSessionNineIndicator = root.findViewById(R.id.viewSessionNineIndicator);

        tvFlashSalePageHour = root.findViewById(R.id.tvFlashSalePageHour);
        tvFlashSalePageMinute = root.findViewById(R.id.tvFlashSalePageMinute);
        tvFlashSalePageSecond = root.findViewById(R.id.tvFlashSalePageSecond);
        tvFlashSalePageCountdownLabel = root.findViewById(R.id.tvFlashSalePageCountdownLabel);
    }

    private void setupRecyclerView() {
        adapter = new FlashSalePageProductAdapter();
        adapter.setOnFlashSaleActionListener(new FlashSalePageProductAdapter.OnFlashSaleActionListener() {
            @Override
            public void onProductClick(Product product) {
                if (product == null || product.getId() == null) return;
                ProductDetailFragment fragment = ProductDetailFragment.newInstance(product.getId());
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onBuyNowClick(Product product) {
                handleBuyNow(product);
            }
        });

        rvFlashSalePageProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFlashSalePageProducts.setAdapter(adapter);
    }

    private void loadFlashSaleProducts() {
        Map<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("limit", "50");
        query.put("fields", "card");
        query.put("saleOnly", "true");
        query.put("sort", "hot_deal");

        Log.d(TAG, "Flash sale query = " + query.toString());

        if (layoutFlashSaleLoading != null) layoutFlashSaleLoading.setVisibility(View.VISIBLE);
        if (layoutFlashSaleEmpty != null) layoutFlashSaleEmpty.setVisibility(View.GONE);

        productRepository.getProductsByQuery(query)
                .observe(getViewLifecycleOwner(), result -> {
                    if (layoutFlashSaleLoading != null) layoutFlashSaleLoading.setVisibility(View.GONE);
                    if (result == null) return;

                    Log.d(TAG, "status = " + result.status + ", size = " + (result.data == null ? "null" : result.data.size()));

                    switch (result.status) {
                        case SUCCESS:
                            if (result.data != null && !result.data.isEmpty()) {
                                allFlashSaleProducts = new ArrayList<>(result.data);
                                showProductsForSession(selectedSessionKey);
                                if (layoutFlashSaleEmpty != null) layoutFlashSaleEmpty.setVisibility(View.GONE);
                            } else {
                                allFlashSaleProducts = new ArrayList<>();
                                adapter.submitList(new ArrayList<>());
                                if (layoutFlashSaleEmpty != null) layoutFlashSaleEmpty.setVisibility(View.VISIBLE);
                            }
                            break;

                        case EMPTY:
                            allFlashSaleProducts = new ArrayList<>();
                            adapter.submitList(new ArrayList<>());
                            if (layoutFlashSaleEmpty != null) layoutFlashSaleEmpty.setVisibility(View.VISIBLE);
                            break;

                        case ERROR:
                        case NO_INTERNET:
                            Toast.makeText(
                                    getContext(),
                                    result.message != null ? result.message : "Không tải được Flash Sale",
                                    Toast.LENGTH_SHORT
                            ).show();
                            adapter.submitList(new ArrayList<>());
                            if (layoutFlashSaleEmpty != null) layoutFlashSaleEmpty.setVisibility(View.VISIBLE);
                            break;
                    }
                });
    }

    private void handleBuyNow(Product product) {
        if (product == null || product.getId() == null) return;

        QuickAddHelper.quickBuyNow(
                requireContext(), getChildFragmentManager(), getViewLifecycleOwner(), product, cartViewModel);
    }

    private void setupSessionClicks() {
        if (layoutSessionNow != null) {
            layoutSessionNow.setOnClickListener(v -> selectSession("NOW"));
        }

        if (layoutSessionMidnight != null) {
            layoutSessionMidnight.setOnClickListener(v -> selectSession("MIDNIGHT"));
        }

        if (layoutSessionTwo != null) {
            layoutSessionTwo.setOnClickListener(v -> selectSession("TWO"));
        }

        if (layoutSessionNine != null) {
            layoutSessionNine.setOnClickListener(v -> selectSession("NINE"));
        }

        updateSessionUi("NOW");
    }

    private void selectSession(String sessionKey) {
        selectedSessionKey = sessionKey;
        updateSessionUi(sessionKey);
        showProductsForSession(sessionKey);
        startCountdownForSession(sessionKey);
        Log.d(TAG, "selected session = " + sessionKey);
    }

    private void updateSessionUi(String selectedKey) {
        boolean isNow = "NOW".equals(selectedKey);
        boolean isMidnight = "MIDNIGHT".equals(selectedKey);
        boolean isTwo = "TWO".equals(selectedKey);
        boolean isNine = "NINE".equals(selectedKey);

        updateSingleSessionUi(tvSessionNowTime, tvSessionNowStatus, viewSessionNowIndicator, isNow);
        updateSingleSessionUi(tvSessionMidnightTime, tvSessionMidnightStatus, viewSessionMidnightIndicator, isMidnight);
        updateSingleSessionUi(tvSessionTwoTime, tvSessionTwoStatus, viewSessionTwoIndicator, isTwo);
        updateSingleSessionUi(tvSessionNineTime, tvSessionNineStatus, viewSessionNineIndicator, isNine);
    }

    private void updateSingleSessionUi(TextView timeView, TextView statusView, View indicator, boolean selected) {
        if (getContext() == null) return;
        
        int timeColor = ContextCompat.getColor(
                requireContext(),
                selected ? R.color.button : R.color.text_secondary
        );

        int statusColor = ContextCompat.getColor(
                requireContext(),
                selected ? R.color.button : R.color.text_tertiary
        );

        if (timeView != null) {
            timeView.setTextColor(timeColor);
        }

        if (statusView != null) {
            statusView.setTextColor(statusColor);
        }

        if (indicator != null) {
            indicator.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void showProductsForSession(String sessionKey) {
        if (adapter == null) return;

        if (allFlashSaleProducts == null || allFlashSaleProducts.isEmpty()) {
            adapter.submitList(new ArrayList<>());
            if (layoutFlashSaleEmpty != null) layoutFlashSaleEmpty.setVisibility(View.VISIBLE);
            return;
        }

        List<Product> randomized = new ArrayList<>(allFlashSaleProducts);

        long seed;
        switch (sessionKey) {
            case "MIDNIGHT":
                seed = 1001L;
                break;
            case "TWO":
                seed = 2002L;
                break;
            case "NINE":
                seed = 9009L;
                break;
            case "NOW":
            default:
                seed = 2100L;
                break;
        }

        Collections.shuffle(randomized, new Random(seed));
        adapter.submitList(randomized);
        if (layoutFlashSaleEmpty != null) layoutFlashSaleEmpty.setVisibility(randomized.isEmpty() ? View.VISIBLE : View.GONE);

        Log.d(TAG, "session = " + sessionKey + ", product size = " + randomized.size());
    }

    private void startCountdownForSession(String sessionKey) {
        cancelFlashSaleCountdown();
        updateCountdownLabel(sessionKey);

        long durationMillis = getCountdownDurationForSession(sessionKey);

        if (durationMillis <= 0) {
            updateCountdownText(0);
            return;
        }

        flashSaleCountDownTimer = new CountDownTimer(durationMillis, ONE_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateCountdownText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                updateCountdownText(0);
                if (getContext() != null) {
                    Toast.makeText(
                            requireContext(),
                            "Phiên Flash Sale đã kết thúc",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        };

        flashSaleCountDownTimer.start();
    }

    private void updateCountdownLabel(String sessionKey) {
        if (tvFlashSalePageCountdownLabel == null) return;
        if ("NOW".equals(sessionKey)) {
            tvFlashSalePageCountdownLabel.setText(R.string.flash_sale_ends_in);
        } else {
            tvFlashSalePageCountdownLabel.setText(R.string.flash_sale_starts_in);
        }
    }

    private long getCountdownDurationForSession(String sessionKey) {
        switch (sessionKey) {
            case "MIDNIGHT":
                return 2 * 60 * 60 * 1000L + 37 * 60 * 1000L + 52 * 1000L; // 02:37:52
            case "TWO":
                return 5 * 60 * 60 * 1000L + 50 * 60 * 1000L + 42 * 1000L; // 05:50:42
            case "NINE":
                return 14 * 60 * 60 * 1000L + 47 * 60 * 1000L + 13 * 1000L; // 14:47:13
            case "NOW":
            default:
                return 45 * 60 * 1000L + 52 * 1000L; // 00:45:52
        }
    }

    private void updateCountdownText(long millisUntilFinished) {
        long totalSeconds = Math.max(0, millisUntilFinished / 1000L);
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;

        if (tvFlashSalePageHour != null) {
            tvFlashSalePageHour.setText(String.format(Locale.getDefault(), "%02d", hours));
        }
        if (tvFlashSalePageMinute != null) {
            tvFlashSalePageMinute.setText(String.format(Locale.getDefault(), "%02d", minutes));
        }
        if (tvFlashSalePageSecond != null) {
            tvFlashSalePageSecond.setText(String.format(Locale.getDefault(), "%02d", seconds));
        }
    }

    private void cancelFlashSaleCountdown() {
        if (flashSaleCountDownTimer != null) {
            flashSaleCountDownTimer.cancel();
            flashSaleCountDownTimer = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelFlashSaleCountdown();
    }
}
