package com.example.frontend;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.frontend.feature.chatbot.ChatbotQuickMenuBottomSheet;
import com.example.frontend.feature.home.HomeFragment;
import com.example.frontend.feature.community.reels.ReelsFeedFragment;
import ui.account.AccountFragment;
import ui.category.ProductCategoryFragment;
import ui.common.BottomNavigationHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View root = findViewById(R.id.main);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        setupBottomNavigation();
        setupChatbot();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), false);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    Fragment current = fm.findFragmentById(R.id.main_fragment_container);
                    if (!(current instanceof HomeFragment)) {
                        loadFragment(new HomeFragment(), false);
                        BottomNavigationHelper.setSelectedTab(findViewById(R.id.layoutBottomNavigation), BottomNavigationHelper.TAB_HOME);
                    } else {
                        setEnabled(false);
                        MainActivity.super.onBackPressed();
                        setEnabled(true);
                    }
                }
            }
        });
    }

    private void setupChatbot() {
        View ivChatbot = findViewById(R.id.ivChatbot);
        if (ivChatbot != null) {
            ivChatbot.setOnClickListener(v -> {
                ChatbotQuickMenuBottomSheet.newInstance()
                        .show(getSupportFragmentManager(), "ChatbotQuickMenu");
            });
        }
    }

    private void setupBottomNavigation() {
        View bottomNav = findViewById(R.id.layoutBottomNavigation);
        if (bottomNav != null) {
            BottomNavigationHelper.setup(bottomNav, tabIndex -> {
                if (tabIndex == BottomNavigationHelper.TAB_HOME) {
                    loadFragment(new HomeFragment(), false);
                } else if (tabIndex == BottomNavigationHelper.TAB_ACCOUNT) {
                    loadFragment(new AccountFragment(), false);
                } else if (tabIndex == BottomNavigationHelper.TAB_CATEGORY) {
                    loadFragment(new ProductCategoryFragment(), false);
                } else if (tabIndex == BottomNavigationHelper.TAB_REELS) {
                    loadFragment(new ReelsFeedFragment(), false);
                } else if (tabIndex == BottomNavigationHelper.TAB_COMMUNITY) {
                    Toast.makeText(this, "Community coming soon!", Toast.LENGTH_SHORT).show();
                }
            });
            BottomNavigationHelper.setSelectedTab(bottomNav, BottomNavigationHelper.TAB_HOME);
        }
    }

    public void loadFragment(Fragment fragment) {
        loadFragment(fragment, true);
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        var transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
