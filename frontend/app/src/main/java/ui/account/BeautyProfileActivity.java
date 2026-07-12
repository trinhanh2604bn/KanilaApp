package ui.account;

import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.frontend.R;
import com.example.frontend.feature.beauty.BeautyProfileViewModel;

public class BeautyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_beauty_profile);

        View container = findViewById(R.id.beautyProfileFragmentHost);
        if (container != null) {
            ViewCompat.setOnApplyWindowInsetsListener(container, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Khởi tạo ViewModel ở cấp Activity để dùng chung giữa các Fragments
        new ViewModelProvider(this).get(BeautyProfileViewModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.beautyProfileFragmentHost, new BeautyProfileOverviewFragment())
                    .commit();
        }
    }
}
