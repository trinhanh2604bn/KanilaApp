package ui.support;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import com.example.frontend.feature.home.HomeFragment;
import ui.account.AccountFragment;
import com.example.frontend.ui.category.ProductCategoryFragment;
import ui.common.BottomNavigationHelper;
import com.example.frontend.feature.chatbot.ChatbotQuickMenuBottomSheet;

public class SupportActivity extends AppCompatActivity {

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
        setupChatbot();

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
                    Toast.makeText(SupportActivity.this, "Community coming soon", Toast.LENGTH_SHORT).show();
                }
            });
            BottomNavigationHelper.setSelectedTab(bottomNav, BottomNavigationHelper.TAB_REELS);
        }
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

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commit();
    }
}
