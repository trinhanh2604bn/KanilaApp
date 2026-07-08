package com.example.frontend;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import ui.account.AccountFragment;
import ui.category.ProductCategoryFragment;
import ui.common.BottomNavigationHelper;
import ui.support.HelpCenterFragment;

import com.example.frontend.feature.home.HomeFragment;

public class MainActivity7 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main7);

        View root = findViewById(R.id.main);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        setupBottomNavigation();

        if (savedInstanceState == null) {
            loadFragment(new HelpCenterFragment());
            BottomNavigationHelper.setSelectedTab(findViewById(R.id.layoutBottomNavigation), BottomNavigationHelper.TAB_REELS);
        }
    }

    private void setupBottomNavigation() {
        View bottomNav = findViewById(R.id.layoutBottomNavigation);
        if (bottomNav != null) {
            BottomNavigationHelper.setup(bottomNav, tabIndex -> {
                if (tabIndex == BottomNavigationHelper.TAB_HOME) {
                    loadFragment(new HomeFragment());
                } else if (tabIndex == BottomNavigationHelper.TAB_ACCOUNT) {
                    loadFragment(new AccountFragment());
                } else if (tabIndex == BottomNavigationHelper.TAB_CATEGORY) {
                    loadFragment(new ProductCategoryFragment());
                } else if (tabIndex == BottomNavigationHelper.TAB_REELS) {
                    loadFragment(new HelpCenterFragment());
                } else if (tabIndex == BottomNavigationHelper.TAB_COMMUNITY) {
                    Toast.makeText(MainActivity7.this, "Community coming soon", Toast.LENGTH_SHORT).show();
                }
            });
            BottomNavigationHelper.setSelectedTab(bottomNav, BottomNavigationHelper.TAB_REELS);
        }
    }

    private void loadFragment(Fragment fragment) {
        // The ID container7 is defined in activity_main7.xml
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commit();
    }
}
