package ui.loyalty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class TierPerksFragment extends Fragment {

    private LinearLayout containerPerks;
    private TextView[] tabs;
    private View[] indicators;
    private int currentTier = 0; // 0: Bronze, 1: Silver, 2: Gold, 3: Diamond

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_tier_perks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupHeader(view);
        initViews(view);
        switchTier(0);
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutHeader);
        TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText(R.string.loyalty_perks_title);
        View btnBack = header.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void initViews(View view) {
        containerPerks = view.findViewById(R.id.containerPerks);
        tabs = new TextView[]{
                view.findViewById(R.id.tabBronze),
                view.findViewById(R.id.tabSilver),
                view.findViewById(R.id.tabGold),
                view.findViewById(R.id.tabDiamond)
        };
        indicators = new View[]{
                view.findViewById(R.id.indicatorBronze),
                view.findViewById(R.id.indicatorSilver),
                view.findViewById(R.id.indicatorGold),
                view.findViewById(R.id.indicatorDiamond)
        };

        for (int i = 0; i < tabs.length; i++) {
            final int index = i;
            tabs[i].setOnClickListener(v -> switchTier(index));
        }
    }

    private void switchTier(int index) {
        currentTier = index;
        for (int i = 0; i < tabs.length; i++) {
            tabs[i].setTextColor(ContextCompat.getColor(requireContext(), 
                i == index ? R.color.button : R.color.text_main));
            tabs[i].setTypeface(null, i == index ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
            indicators[i].setBackgroundColor(ContextCompat.getColor(requireContext(), 
                i == index ? R.color.button : android.R.color.transparent));
        }

        renderPerks(index);
    }

    private void renderPerks(int tierIndex) {
        containerPerks.removeAllViews();
        List<Perk> perks = getPerksForTier(tierIndex);

        for (Perk perk : perks) {
            View itemView = getLayoutInflater().inflate(R.layout.view_tier_perk_item, containerPerks, false);
            ((ImageView) itemView.findViewById(R.id.ivPerkIcon)).setImageResource(perk.icon);
            ((TextView) itemView.findViewById(R.id.tvPerkTitle)).setText(perk.title);
            ((TextView) itemView.findViewById(R.id.tvPerkDesc)).setText(perk.desc);
            
            containerPerks.addView(itemView);
            
            // Add Divider
            View divider = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            params.setMarginStart(px(64));
            divider.setLayoutParams(params);
            divider.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.border_divider));
            containerPerks.addView(divider);
        }
    }

    private List<Perk> getPerksForTier(int tierIndex) {
        List<Perk> list = new ArrayList<>();
        String tierName = getTierName(tierIndex);

        // Common perks
        list.add(new Perk(R.drawable.ic_delivery_truck, getString(R.string.perk_freeship_title), getString(R.string.perk_freeship_desc)));
        list.add(new Perk(R.drawable.ic_routine, getString(R.string.perk_member_day_title), getString(R.string.perk_member_day_desc, tierName)));

        if (tierIndex >= 1) { // Silver+
            list.add(new Perk(R.drawable.ic_coupon, getString(R.string.perk_exclusive_price_title, tierName), getString(R.string.perk_exclusive_price_desc, tierName)));
            list.add(new Perk(R.drawable.ic_hot, getString(R.string.perk_hot_voucher_title), getString(R.string.perk_hot_voucher_desc)));
        }

        if (tierIndex >= 2) { // Gold+
            list.add(new Perk(R.drawable.ic_winner, getString(R.string.perk_upgrade_title, tierName), "Mã giảm giá và ưu đãi thăng hạng"));
            list.add(new Perk(R.drawable.ic_gift, getString(R.string.perk_birthday_title), "Món quà đặc biệt trong tháng sinh nhật"));
        }

        if (tierIndex >= 3) { // Diamond
            list.add(new Perk(R.drawable.ic_crown, getString(R.string.perk_maintain_title), "Voucher đặc quyền duy trì thứ hạng"));
            list.add(new Perk(R.drawable.ic_verified, "Ưu tiên hỗ trợ", "Đội ngũ CSKH hỗ trợ ưu tiên 24/7"));
        }

        return list;
    }

    private String getTierName(int index) {
        switch (index) {
            case 0: return getString(R.string.loyalty_tier_bronze);
            case 1: return getString(R.string.loyalty_tier_silver);
            case 2: return getString(R.string.loyalty_tier_gold);
            case 3: return getString(R.string.loyalty_tier_diamond);
            default: return "";
        }
    }

    private int px(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private static class Perk {
        int icon;
        String title;
        String desc;

        Perk(int icon, String title, String desc) {
            this.icon = icon;
            this.title = title;
            this.desc = desc;
        }
    }
}
