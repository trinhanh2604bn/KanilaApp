package com.example.frontend;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ui.account.AccountFragment;
import ui.category.ProductCategoryFragment;
import ui.commerce.CartFragment;
import ui.common.BottomNavigationHelper;
import ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private View bottomNav;

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

        bottomNav = findViewById(R.id.layoutBottomNavigation);
        setupBottomNavigation();

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), false);
        }
    }

    public void navigateToCart() {
        loadFragment(new CartFragment(), true);
    }

    private void setupBottomNavigation() {
        if (bottomNav != null) {
            BottomNavigationHelper.setup(bottomNav, tabIndex -> {
                if (tabIndex == BottomNavigationHelper.TAB_HOME) {
                    // Return to Home: clear stack to avoid "exiting" behavior
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    loadFragment(new HomeFragment(), false);
                } else if (tabIndex == BottomNavigationHelper.TAB_CATEGORY) {
                    loadFragment(new ProductCategoryFragment(), false);
                } else if (tabIndex == BottomNavigationHelper.TAB_ACCOUNT) {
                    loadFragment(new AccountFragment(), false);
                }
            });
            BottomNavigationHelper.setSelectedTab(bottomNav, BottomNavigationHelper.TAB_HOME);
        }
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        // Prevent reloading the same fragment type if it's already active
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.containerMain);
        if (current != null && current.getClass().equals(fragment.getClass())) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containerMain, fragment);
        
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        
        transaction.commit();
        
        // Update Bottom Nav selection if needed
        updateBottomNavSelection(fragment);
    }

    private void updateBottomNavSelection(Fragment fragment) {
        if (bottomNav == null) return;
        
        if (fragment instanceof HomeFragment) {
            BottomNavigationHelper.setSelectedTab(bottomNav, BottomNavigationHelper.TAB_HOME);
        } else if (fragment instanceof ProductCategoryFragment) {
            BottomNavigationHelper.setSelectedTab(bottomNav, BottomNavigationHelper.TAB_CATEGORY);
        } else if (fragment instanceof AccountFragment) {
            BottomNavigationHelper.setSelectedTab(bottomNav, BottomNavigationHelper.TAB_ACCOUNT);
        }
    }
}
