package ui.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend.R;

/**
 * Hiển thị chi tiết thông báo. Đọc loại thông báo từ Intent extra và inflate
 * đúng view_notif_detail_* vào containerNotifDetail của page_notification_detail.
 */
public class NotificationDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NOTIF_TYPE = "extra_notif_type";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_notification_detail);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        FrameLayout container = findViewById(R.id.containerNotifDetail);
        container.removeAllViews();
        LayoutInflater.from(this).inflate(resolveDetailLayout(), container, true);
    }

    @LayoutRes
    private int resolveDetailLayout() {
        switch (parseType(getIntent().getStringExtra(EXTRA_NOTIF_TYPE))) {
            case OFFER:
                return R.layout.view_notif_detail_offer;
            case COMMUNITY:
                return R.layout.view_notif_detail_community;
            case PERSONAL:
                return R.layout.view_notif_detail_personal;
            case ORDER:
            default:
                return R.layout.view_notif_detail_order;
        }
    }

    private NotificationType parseType(String raw) {
        if (raw != null) {
            try {
                return NotificationType.valueOf(raw);
            } catch (IllegalArgumentException ignored) {
                // extra không hợp lệ -> mặc định ORDER
            }
        }
        return NotificationType.ORDER;
    }
}
