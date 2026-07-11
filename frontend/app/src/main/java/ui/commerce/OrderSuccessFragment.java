package ui.commerce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;
import java.util.Locale;
import ui.order.OrderListFragment;
import ui.common.FragmentNavigationHelper;

public class OrderSuccessFragment extends Fragment {

    private String orderCode;
    private String paymentMethod;
    private String deliveryTime;
    private double totalAmount;
    private int earnedPoints;

    public static OrderSuccessFragment newInstance(String orderCode, String paymentMethod, String deliveryTime, double totalAmount, int earnedPoints) {
        OrderSuccessFragment fragment = new OrderSuccessFragment();
        Bundle args = new Bundle();
        args.putString("order_code", orderCode);
        args.putString("payment_method", paymentMethod);
        args.putString("delivery_time", deliveryTime);
        args.putDouble("total_amount", totalAmount);
        args.putInt("earned_points", earnedPoints);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderCode = getArguments().getString("order_code");
            paymentMethod = getArguments().getString("payment_method");
            deliveryTime = getArguments().getString("delivery_time");
            totalAmount = getArguments().getDouble("total_amount");
            earnedPoints = getArguments().getInt("earned_points");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvOrderCode = view.findViewById(R.id.tvOrderCode);
        TextView tvPaymentMethod = view.findViewById(R.id.tvPaymentMethod);
        TextView tvDeliveryTime = view.findViewById(R.id.tvDeliveryTime);
        TextView tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        TextView tvRewardPoints = view.findViewById(R.id.tvRewardPoints);

        tvOrderCode.setText(orderCode != null ? orderCode : "---");
        tvPaymentMethod.setText(paymentMethod != null ? paymentMethod : "---");
        tvDeliveryTime.setText(deliveryTime != null ? deliveryTime : "---");
        tvTotalAmount.setText(formatPrice(totalAmount));
        tvRewardPoints.setText(getString(R.string.order_success_reward, earnedPoints));

        view.findViewById(R.id.btnTrackOrder).setOnClickListener(v -> {
            if (getActivity() != null) {
                // Navigate to Order List - Pending tab
                OrderListFragment fragment = OrderListFragment.newInstance("pending");
                FragmentNavigationHelper.loadFragment(getActivity(), fragment);
            }
        });

        view.findViewById(R.id.btnContinueShopping).setOnClickListener(v -> {
            if (getActivity() != null) {
                // Pop back to home
                getActivity().getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
    }
}
