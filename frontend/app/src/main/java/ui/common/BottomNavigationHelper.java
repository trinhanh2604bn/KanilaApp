package ui.common;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;

import com.example.frontend.MainActivity;
import com.example.frontend.R;
import com.example.frontend.feature.community.reels.ReelsFeedFragment;
import ui.account.AccountFragment;
import ui.category.ProductCategoryFragment;

public class BottomNavigationHelper {

    public static final int TAB_HOME = 0;
    public static final int TAB_CATEGORY = 1;
    public static final int TAB_REELS = 2;
    public static final int TAB_COMMUNITY = 3;
    public static final int TAB_ACCOUNT = 4;

    public interface OnTabSelectedListener {
        void onTabSelected(int tabIndex);
    }

    public static void setup(View root, OnTabSelectedListener listener) {
        View navHome = root.findViewById(R.id.navHome);
        View navCategory = root.findViewById(R.id.navCategory);
        View navReels = root.findViewById(R.id.navReels);
        View navCommunity = root.findViewById(R.id.navCommunity);
        View navAccount = root.findViewById(R.id.navAccount);

        if (navHome != null) navHome.setOnClickListener(v -> {
            setSelectedTab(root, TAB_HOME);
            if (listener != null) listener.onTabSelected(TAB_HOME);
        });
        if (navCategory != null) navCategory.setOnClickListener(v -> {
            setSelectedTab(root, TAB_CATEGORY);
            if (listener != null) listener.onTabSelected(TAB_CATEGORY);
        });
        if (navReels != null) navReels.setOnClickListener(v -> {
            setSelectedTab(root, TAB_REELS);
            if (listener != null) listener.onTabSelected(TAB_REELS);
        });
        if (navCommunity != null) navCommunity.setOnClickListener(v -> {
            setSelectedTab(root, TAB_COMMUNITY);
            if (listener != null) listener.onTabSelected(TAB_COMMUNITY);
        });
        if (navAccount != null) navAccount.setOnClickListener(v -> {
            setSelectedTab(root, TAB_ACCOUNT);
            if (listener != null) listener.onTabSelected(TAB_ACCOUNT);
        });
    }

    public static void setupStandardNavigation(Fragment fragment, View root) {
        setup(root, tabIndex -> {
            if (tabIndex == TAB_HOME) {
                Intent intent = new Intent(fragment.requireContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                fragment.startActivity(intent);
            } else if (tabIndex == TAB_CATEGORY) {
                if (!(fragment instanceof ProductCategoryFragment)) {
                    fragment.getParentFragmentManager().beginTransaction()
                            .replace(R.id.main, new ProductCategoryFragment())
                            .commit();
                }
            } else if (tabIndex == TAB_ACCOUNT) {
                if (!(fragment instanceof AccountFragment)) {
                    fragment.getParentFragmentManager().beginTransaction()
                            .replace(R.id.main, new AccountFragment())
                            .commit();
                }
            } else if (tabIndex == TAB_REELS) {
                if (!(fragment instanceof ReelsFeedFragment)) {
                    fragment.getParentFragmentManager().beginTransaction()
                            .replace(R.id.main, new ReelsFeedFragment())
                            .commit();
                }
            } else if (tabIndex == TAB_COMMUNITY) {
                if (!(fragment instanceof ui.community.CommunityHomeFragment)) {
                    fragment.getParentFragmentManager().beginTransaction()
                            .replace(R.id.main, new ui.community.CommunityHomeFragment())
                            .commit();
                }
            }
        });
    }

    public static void setupStandardNavigation(AppCompatActivity activity, View root) {
        setup(root, tabIndex -> {
            if (tabIndex == TAB_HOME) {
                if (!(activity instanceof MainActivity)) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                }
            } else if (tabIndex == TAB_CATEGORY) {
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new ProductCategoryFragment())
                        .commit();
            } else if (tabIndex == TAB_ACCOUNT) {
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new AccountFragment())
                        .commit();
            } else if (tabIndex == TAB_REELS) {
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new ReelsFeedFragment())
                        .commit();
            } else if (tabIndex == TAB_COMMUNITY) {
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new ui.community.CommunityHomeFragment())
                        .commit();
            }
        });
    }

    public static void setSelectedTab(View root, int tabIndex) {
        int[] itemIds = {R.id.navHome, R.id.navCategory, R.id.navReels, R.id.navCommunity, R.id.navAccount};
        int[] iconIds = {R.id.ivHome, R.id.ivCategory, R.id.ivReels, R.id.ivCommunity, R.id.ivAccount};
        int[] textIds = {R.id.tvHome, R.id.tvCategory, R.id.tvReels, R.id.tvCommunity, R.id.tvAccount};
        int[] bgIds = {R.id.bgHome, R.id.bgCategory, R.id.bgReels, R.id.bgCommunity, R.id.bgAccount};

        Context context = root.getContext();
        int activeColor = ContextCompat.getColor(context, R.color.button);
        int inactiveColor = ContextCompat.getColor(context, R.color.text_main);

        for (int i = 0; i < itemIds.length; i++) {
            boolean isSelected = (i == tabIndex);
            View item = root.findViewById(itemIds[i]);
            ImageView icon = root.findViewById(iconIds[i]);
            TextView text = root.findViewById(textIds[i]);
            View bg = root.findViewById(bgIds[i]);

            if (item != null) {
                if (isSelected) {
                    animateSelectedItem(item, i == TAB_REELS);
                } else {
                    item.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start();
                }
            }

            if (icon != null) {
                ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(isSelected ? activeColor : inactiveColor));
            }

            if (text != null) {
                text.setTextColor(isSelected ? activeColor : inactiveColor);
            }

            if (bg != null) {
                bg.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            }
        }
    }

    public static void showBadge(View root, int tabIndex, boolean show) {
        int[] badgeIds = {R.id.badgeHome, R.id.badgeCategory, R.id.badgeReels, R.id.badgeCommunity, R.id.badgeAccount};
        if (tabIndex >= 0 && tabIndex < badgeIds.length) {
            View badge = root.findViewById(badgeIds[tabIndex]);
            if (badge != null) {
                badge.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }
    }

    public static void setBadgeText(View root, int tabIndex, String text) {
        int[] badgeIds = {R.id.badgeHome, R.id.badgeCategory, R.id.badgeReels, R.id.badgeCommunity, R.id.badgeAccount};
        if (tabIndex >= 0 && tabIndex < badgeIds.length) {
            TextView badge = root.findViewById(badgeIds[tabIndex]);
            if (badge != null) {
                badge.setText(text);
                badge.setVisibility(text != null && !text.isEmpty() ? View.VISIBLE : View.GONE);
            }
        }
    }

    public static void clearAllBadges(View root) {
        int[] badgeIds = {R.id.badgeHome, R.id.badgeCategory, R.id.badgeReels, R.id.badgeCommunity, R.id.badgeAccount};
        for (int id : badgeIds) {
            View badge = root.findViewById(id);
            if (badge != null) {
                badge.setVisibility(View.GONE);
            }
        }
    }

    public static void animateSelectedItem(View item, boolean isReels) {
        float scale = isReels ? 1.08f : 1.06f;
        item.animate()
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(150)
                .start();
    }
}
