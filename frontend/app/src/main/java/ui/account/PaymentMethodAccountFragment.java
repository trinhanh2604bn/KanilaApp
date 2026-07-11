package ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;
import com.google.android.material.card.MaterialCardView;

public class PaymentMethodAccountFragment extends Fragment {

    private LinearLayout layoutExpanded, containerActiveCards, containerActiveBanks;
    private TextView tvExpandedTitle;
    private LinearLayout containerMethodItems;
    private TextView tvShopeePayStatus, tvMoMoStatus, tvZaloPayStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_payment_method_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initHeader(view);
        initViews(view);
    }

    private void initHeader(View view) {
        View header = view.findViewById(R.id.layoutHeader);
        TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText(R.string.payment_method_account_title);

        View btnBack = header.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
    }

    private void initViews(View view) {
        layoutExpanded = view.findViewById(R.id.layoutExpandedMethods);
        tvExpandedTitle = view.findViewById(R.id.tvExpandedTitle);
        containerMethodItems = view.findViewById(R.id.containerMethodItems);
        containerActiveCards = view.findViewById(R.id.containerActiveCards);
        containerActiveBanks = view.findViewById(R.id.containerActiveBanks);
        
        tvShopeePayStatus = view.findViewById(R.id.tvShopeePayStatus);
        tvMoMoStatus = view.findViewById(R.id.tvMoMoStatus);
        tvZaloPayStatus = view.findViewById(R.id.tvZaloPayStatus);

        view.findViewById(R.id.btnAddNewCard).setOnClickListener(v -> showExpandedList("card"));
        view.findViewById(R.id.btnAddNewBank).setOnClickListener(v -> showExpandedList("bank"));
        view.findViewById(R.id.btnShopeePay).setOnClickListener(v -> showExpandedList("shopeepay"));
        view.findViewById(R.id.btnMoMo).setOnClickListener(v -> showExpandedList("momo"));
        view.findViewById(R.id.btnZaloPay).setOnClickListener(v -> showExpandedList("zalopay"));
    }

    private void showExpandedList(String type) {
        layoutExpanded.setVisibility(View.VISIBLE);
        containerMethodItems.removeAllViews();

        String[] items;
        int iconRes = R.drawable.ic_paymeny_card;
        String title = "";

        switch (type) {
            case "card":
                title = "Chọn loại thẻ";
                items = new String[]{"Visa **** 1234", "MasterCard **** 5678", "JCB **** 9012"};
                iconRes = R.drawable.ic_paymeny_card;
                break;
            case "bank":
                title = "Chọn ngân hàng liên kết";
                items = new String[]{"Vietcombank", "Techcombank", "BIDV", "Agribank"};
                iconRes = R.drawable.ic_routine;
                break;
            case "shopeepay":
                title = "Dịch vụ liên kết ShopeePay";
                items = new String[]{"Tài khoản: bảo_kanila@gmail.com"};
                iconRes = R.drawable.ic_gift;
                break;
            case "momo":
                title = "Liên kết ví MoMo";
                items = new String[]{"Số điện thoại: 07xx xxx 108"};
                iconRes = R.drawable.bg_circle_pink;
                break;
            case "zalopay":
                title = "Liên kết ví ZaloPay";
                items = new String[]{"Tài khoản Zalo: Nguyễn Bảo"};
                iconRes = R.drawable.ic_zalo;
                break;
            default:
                items = new String[]{};
        }

        tvExpandedTitle.setText(title);

        for (String itemText : items) {
            View itemView = createMethodItemView(itemText, iconRes, type);
            containerMethodItems.addView(itemView);
        }

        layoutExpanded.requestFocus();
    }

    private View createMethodItemView(String text, int iconRes, String type) {
        MaterialCardView card = new MaterialCardView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(px(16), px(8), px(16), 0);
        card.setLayoutParams(params);
        card.setRadius(px(12));
        card.setCardElevation(0);
        card.setStrokeColor(getResources().getColorStateList(R.color.border_divider, null));
        card.setStrokeWidth(px(1));
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        layout.setPadding(px(16), px(16), px(16), px(16));

        ImageView ivIcon = new ImageView(requireContext());
        ivIcon.setLayoutParams(new LinearLayout.LayoutParams(px(32), px(24)));
        ivIcon.setImageResource(iconRes);
        if (iconRes == R.drawable.ic_paymeny_card || iconRes == R.drawable.ic_routine) {
             ivIcon.setImageTintList(getResources().getColorStateList(R.color.button, null));
        }

        TextView tv = new TextView(requireContext());
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        tvParams.setMarginStart(px(16));
        tv.setLayoutParams(tvParams);
        tv.setText(text);
        tv.setTextColor(getResources().getColor(R.color.text_main, null));
        tv.setTextSize(14);

        com.google.android.material.button.MaterialButton btn = new com.google.android.material.button.MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonStyle);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, px(36));
        btn.setLayoutParams(btnParams);
        btn.setText(R.string.payment_method_select);
        btn.setTextSize(12);
        btn.setPadding(px(12), 0, px(12), 0);
        btn.setCornerRadius(px(18));
        btn.setBackgroundTintList(getResources().getColorStateList(R.color.button, null));
        
        btn.setOnClickListener(v -> {
            handleMethodSelection(text, iconRes, type);
            layoutExpanded.setVisibility(View.GONE);
        });

        layout.addView(ivIcon);
        layout.addView(tv);
        layout.addView(btn);
        card.addView(layout);

        return card;
    }

    private void handleMethodSelection(String text, int iconRes, String type) {
        if ("card".equals(type)) {
            addActiveMethodToContainer(containerActiveCards, text, iconRes);
        } else if ("bank".equals(type)) {
            addActiveMethodToContainer(containerActiveBanks, text, iconRes);
        } else if ("shopeepay".equals(type)) {
            tvShopeePayStatus.setText("Đã liên kết");
            tvShopeePayStatus.setTextColor(getResources().getColor(R.color.button, null));
        } else if ("momo".equals(type)) {
            tvMoMoStatus.setText("Đã liên kết");
            tvMoMoStatus.setTextColor(getResources().getColor(R.color.button, null));
        } else if ("zalopay".equals(type)) {
            tvZaloPayStatus.setText("Đã liên kết");
            tvZaloPayStatus.setTextColor(getResources().getColor(R.color.button, null));
        }
        
        Toast.makeText(getContext(), "Đã thêm phương thức: " + text, Toast.LENGTH_SHORT).show();
    }

    private void addActiveMethodToContainer(LinearLayout container, String text, int iconRes) {
        MaterialCardView activeCard = new MaterialCardView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(px(16), px(8), px(16), 0);
        activeCard.setLayoutParams(params);
        activeCard.setRadius(px(12));
        activeCard.setCardElevation(0);
        activeCard.setStrokeColor(getResources().getColorStateList(R.color.border_divider, null));
        activeCard.setStrokeWidth(px(1));
        activeCard.setCardBackgroundColor(getResources().getColorStateList(R.color.background_main, null));

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        layout.setPadding(px(16), px(16), px(16), px(16));

        ImageView ivIcon = new ImageView(requireContext());
        ivIcon.setLayoutParams(new LinearLayout.LayoutParams(px(32), px(24)));
        ivIcon.setImageResource(iconRes);
        ivIcon.setImageTintList(getResources().getColorStateList(R.color.button, null));

        TextView tv = new TextView(requireContext());
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        tvParams.setMarginStart(px(16));
        tv.setLayoutParams(tvParams);
        tv.setText(text);
        tv.setTextColor(getResources().getColor(R.color.text_main, null));
        tv.setTextSize(14);
        
        ImageView ivCheck = new ImageView(requireContext());
        ivCheck.setLayoutParams(new LinearLayout.LayoutParams(px(20), px(20)));
        ivCheck.setImageResource(R.drawable.ic_routine); // Using routine as a checkmark for demo
        ivCheck.setImageTintList(getResources().getColorStateList(R.color.button, null));

        layout.addView(ivIcon);
        layout.addView(tv);
        layout.addView(ivCheck);
        activeCard.addView(layout);

        container.addView(activeCard);
    }

    private int px(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
