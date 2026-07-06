package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ui.common.BottomNavigationHelper;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);
        
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        setupBottomNavigation();
        setupClickListeners();
    }

    private void setupBottomNavigation() {
        View root = findViewById(R.id.main);
        BottomNavigationHelper.setup(root, tabIndex -> {
            if (tabIndex == BottomNavigationHelper.TAB_HOME) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            } else if (tabIndex == BottomNavigationHelper.TAB_SUPPORT) {
                // Already here
            } else {
                Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });
        BottomNavigationHelper.setSelectedTab(root, BottomNavigationHelper.TAB_SUPPORT);
    }

    private void setupClickListeners() {
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View menuTrackOrder = findViewById(R.id.menuTrackOrder);
        if (menuTrackOrder != null) {
            menuTrackOrder.setOnClickListener(v -> {
                Toast.makeText(this, "Chuyển tới Tra cứu đơn hàng", Toast.LENGTH_SHORT).show();
            });
        }

        View menuClearChat = findViewById(R.id.menuClearChat);
        if (menuClearChat != null) {
            menuClearChat.setOnClickListener(v -> {
                Toast.makeText(this, "Đã xóa lịch sử trò chuyện", Toast.LENGTH_SHORT).show();
            });
        }
    }
}