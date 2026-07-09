package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class RewardCenterFragment extends Fragment {

    private TextView tvTotalPoints, tvEquivalentValue;
    private RecyclerView rvRewards;
    private RewardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward_center, container, false);
        initViews(view);
        setupRewards();
        return view;
    }

    private void initViews(View view) {
        tvTotalPoints = view.findViewById(R.id.tvTotalPoints);
        tvEquivalentValue = view.findViewById(R.id.tvEquivalentValue);
        rvRewards = view.findViewById(R.id.rvRewards);
        
        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Mock data
        tvTotalPoints.setText("2.500");
        tvEquivalentValue.setText(getString(R.string.reward_equivalent_value, "250.000"));
    }

    private void setupRewards() {
        adapter = new RewardAdapter();
        rvRewards.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRewards.setAdapter(adapter);

        List<RewardItem> rewards = new ArrayList<>();
        rewards.add(new RewardItem("1", "Voucher 50K", "Giảm 50k cho đơn từ 500k", 500, null, "VOUCHER"));
        rewards.add(new RewardItem("2", "Voucher 100K", "Giảm 100k cho đơn từ 1tr", 1000, null, "VOUCHER"));
        rewards.add(new RewardItem("3", "Mặt nạ giấy Innisfree", "Dành cho da nhạy cảm", 300, null, "PRODUCT"));
        adapter.setRewards(rewards);
    }
}
