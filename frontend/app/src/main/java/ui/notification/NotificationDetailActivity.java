package ui.notification;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.feature.search.SearchSuggestedProductAdapter;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Hiển thị chi tiết thông báo. Đọc loại thông báo từ Intent extra và inflate
 * đúng view_notif_detail_* vào containerNotifDetail của page_notification_detail.
 */
public class NotificationDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NOTIF_TYPE = "extra_notif_type";
    public static final String EXTRA_NOTIF_TITLE = "extra_notif_title";
    public static final String EXTRA_NOTIF_CONTENT = "extra_notif_content";
    public static final String EXTRA_NOTIF_REF_ID = "extra_notif_ref_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_notification_detail);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        NotificationType type = parseType(getIntent().getStringExtra(EXTRA_NOTIF_TYPE));

        FrameLayout container = findViewById(R.id.containerNotifDetail);
        container.removeAllViews();
        LayoutInflater.from(this).inflate(layoutFor(type), container, true);

        bindHeader(type);
        setupSuggestedProducts();
    }

    /**
     * Đổ nội dung động của thông báo (tiêu đề + mô tả) vào phần header của layout
     * theo loại. Phần body chi tiết (điểm, voucher, đơn hàng...) hiện vẫn là mẫu
     * tĩnh, sẽ bind theo refId khi backend sẵn sàng. Mô tả rỗng thì ẩn đi.
     */
    private void bindHeader(NotificationType type) {
        @IdRes int titleId;
        @IdRes int subtitleId;
        switch (type) {
            case OFFER:
                titleId = R.id.tvOfferHeaderTitle;
                subtitleId = R.id.tvOfferHeaderSubtitle;
                break;
            case COMMUNITY:
                titleId = R.id.tvCommunityHeaderTitle;
                subtitleId = R.id.tvCommunityHeaderSubtitle;
                break;
            case PERSONAL:
                titleId = R.id.tvPersonalHeaderTitle;
                subtitleId = R.id.tvPersonalHeaderSubtitle;
                break;
            case ORDER:
            default:
                titleId = R.id.tvOrderStatusTitle;
                subtitleId = R.id.tvOrderStatusSubtitle;
                break;
        }

        String title = getIntent().getStringExtra(EXTRA_NOTIF_TITLE);
        String content = getIntent().getStringExtra(EXTRA_NOTIF_CONTENT);

        TextView tvTitle = findViewById(titleId);
        if (tvTitle != null && !TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }

        TextView tvSubtitle = findViewById(subtitleId);
        if (tvSubtitle != null) {
            if (TextUtils.isEmpty(content)) {
                tvSubtitle.setVisibility(View.GONE);
            } else {
                tvSubtitle.setText(content);
                tvSubtitle.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Cross-sell "Gợi ý dành cho bạn": tái dùng component view_suggested_products +
     * adapter/card sản phẩm sẵn có. RecyclerView đã set layoutManager ngang trong XML.
     */
    private void setupSuggestedProducts() {
        RecyclerView rvSuggested = findViewById(R.id.rvSuggestedProducts);
        SearchSuggestedProductAdapter adapter = new SearchSuggestedProductAdapter();
        rvSuggested.setAdapter(adapter);

        // TODO: thay bằng API gợi ý sản phẩm khi backend sẵn sàng
        List<Product> suggested = new ArrayList<>();
        suggested.add(new Product("s1", "Anua", "Heartleaf 77% Soothing Toner", "320000", "4.8", "1.2k", R.drawable.ic_product, "Best", ""));
        suggested.add(new Product("s2", "Skin1004", "Centella Ampoule", "365000", "4.7", "800", R.drawable.ic_product, "", ""));
        suggested.add(new Product("s3", "La Roche-Posay", "Mela B3 Serum", "790000", "4.9", "300", R.drawable.ic_product, "New", ""));
        suggested.add(new Product("s4", "The Ordinary", "Niacinamide 10% + Zinc 1%", "255000", "4.6", "2.1k", R.drawable.ic_product, "", ""));
        adapter.setItems(suggested);

        adapter.setOnProductClickListener(product ->
                Toast.makeText(this, product.getName(), Toast.LENGTH_SHORT).show());
    }

    @LayoutRes
    private int layoutFor(NotificationType type) {
        switch (type) {
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
