package ui.support;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class LinkedWalletsFragment extends Fragment {

    private LinearLayout linkedContainer;
    private LinearLayout suggestedContainer;
    private TextView tvLinkedTitle;
    
    private final List<WalletItem> allWallets = new ArrayList<>();

    private static class WalletItem {
        String name;
        int iconRes;
        int iconBg;
        int iconTint;
        boolean isLinked;
        String phone;

        WalletItem(String name, int iconRes, int iconBg, int iconTint, boolean isLinked, String phone) {
            this.name = name;
            this.iconRes = iconRes;
            this.iconBg = iconBg;
            this.iconTint = iconTint;
            this.isLinked = isLinked;
            this.phone = phone;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_linked_wallets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        linkedContainer = view.findViewById(R.id.linkedContainer);
        suggestedContainer = view.findViewById(R.id.suggestedContainer);
        tvLinkedTitle = view.findViewById(R.id.tvLinkedTitle);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        initData();
        renderWallets();
    }

    private void initData() {
        allWallets.clear();
        allWallets.add(new WalletItem("Ví MoMo", R.drawable.ic_wallet, 0xFFFDF0F5, 0xFFD82D8B, true, "09****108"));
        allWallets.add(new WalletItem("ZaloPay", R.drawable.ic_zalo, 0xFFEAF3FF, 0xFF0084FF, false, ""));
        allWallets.add(new WalletItem("VNPay", R.drawable.ic_paymeny_card, 0xFFFFF0F0, 0xFFED1C24, false, ""));
    }

    private void renderWallets() {
        linkedContainer.removeAllViews();
        suggestedContainer.removeAllViews();

        int linkedCount = 0;
        for (WalletItem wallet : allWallets) {
            View itemView = getLayoutInflater().inflate(R.layout.item_wallet_card, (wallet.isLinked ? linkedContainer : suggestedContainer), false);
            
            TextView tvName = itemView.findViewById(R.id.tvWalletName);
            TextView tvStatus = itemView.findViewById(R.id.tvWalletStatus);
            TextView btnAction = itemView.findViewById(R.id.btnAction);
            ImageView ivIcon = itemView.findViewById(R.id.ivWalletIcon);
            MaterialCardView iconContainer = itemView.findViewById(R.id.cardIconContainer);

            tvName.setText(wallet.name);
            ivIcon.setImageResource(wallet.iconRes);
            ivIcon.setColorFilter(wallet.iconTint);
            iconContainer.setCardBackgroundColor(wallet.iconBg);

            if (wallet.isLinked) {
                linkedCount++;
                tvStatus.setText(String.format("%s • Mặc định", wallet.phone));
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.success));
                btnAction.setText("Hủy");
                btnAction.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_tertiary));
                btnAction.setOnClickListener(v -> showUnlinkDialog(wallet));
            } else {
                tvStatus.setText("Chưa liên kết");
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_tertiary));
                btnAction.setText("Liên kết");
                btnAction.setTextColor(ContextCompat.getColor(requireContext(), R.color.button));
                btnAction.setOnClickListener(v -> showLinkInputSheet(wallet));
            }

            if (wallet.isLinked) {
                linkedContainer.addView(itemView);
            } else {
                suggestedContainer.addView(itemView);
            }
        }

        tvLinkedTitle.setVisibility(linkedCount > 0 ? View.VISIBLE : View.GONE);
    }

    private void showLinkInputSheet(WalletItem wallet) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_link_wallet_input, null);
        dialog.setContentView(sheetView);

        TextView tvTitle = sheetView.findViewById(R.id.tvSheetTitle);
        EditText edtPhone = sheetView.findViewById(R.id.edtWalletPhone);
        MaterialButton btnSubmit = sheetView.findViewById(R.id.btnSubmitLink);

        tvTitle.setText(String.format("Liên kết %s", wallet.name));
        
        btnSubmit.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            if (phone.isEmpty()) {
                edtPhone.setError("Vui lòng nhập số điện thoại");
                return;
            }
            if (phone.length() < 10 || !phone.startsWith("0")) {
                edtPhone.setError("Số điện thoại không hợp lệ");
                return;
            }

            // Simulate linking process
            Toast.makeText(getContext(), "Đang xác thực...", Toast.LENGTH_SHORT).show();
            
            v.postDelayed(() -> {
                if (isAdded()) {
                    dialog.dismiss();
                    wallet.isLinked = true;
                    wallet.phone = phone.substring(0, 2) + "****" + phone.substring(phone.length() - 3);
                    renderWallets();
                    
                    SuccessDialog success = new SuccessDialog(requireContext(), 
                            "Liên kết thành công!", 
                            String.format("Ví %s của bạn đã được liên kết với hệ thống Kanila.", wallet.name), 
                            "Tuyệt vời");
                    success.show();
                }
            }, 1500);
        });

        dialog.show();
    }

    private void showUnlinkDialog(WalletItem wallet) {
        new AlertDialog.Builder(getContext())
                .setTitle("Hủy liên kết ví")
                .setMessage(String.format("Bạn có chắc chắn muốn hủy liên kết với %s?", wallet.name))
                .setPositiveButton("Hủy liên kết", (dialog, which) -> {
                    wallet.isLinked = false;
                    wallet.phone = "";
                    renderWallets();
                    Toast.makeText(getContext(), "Đã hủy liên kết thành công", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Đóng", null)
                .show();
    }
}
