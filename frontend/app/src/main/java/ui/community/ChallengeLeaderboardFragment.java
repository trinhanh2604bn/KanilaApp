package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChallengeLeaderboardFragment extends Fragment {

    private RecyclerView rvLeaderboard;
    private LeaderboardAdapter adapter;
    private ChallengeViewModel viewModel;
    private MaterialButtonToggleGroup toggleGroup;
    private MaterialButton btnSeeAllLeaderboard;
    private View viewWeeklyUnderline, viewMonthlyUnderline;

    private List<LeaderboardUser> weeklyUsers = new ArrayList<>();
    private List<LeaderboardUser> monthlyUsers = new ArrayList<>();
    private boolean isWeeklySelected = true;
    private boolean isExpanded = false;

    private TextView tvMyRank, tvMyPoints, tvNeedPoints, tvProgressText;
    private ProgressBar progressMyRank;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_leaderboard, container, false);
        initViews(view);
        setupRecyclerView();
        setupViewModel();
        return view;
    }

    private void initViews(View view) {
        rvLeaderboard = view.findViewById(R.id.rvLeaderboard);
        toggleGroup = view.findViewById(R.id.toggleGroupLeaderboard);
        btnSeeAllLeaderboard = view.findViewById(R.id.btnSeeAllLeaderboard);
        viewWeeklyUnderline = view.findViewById(R.id.viewWeeklyUnderline);
        viewMonthlyUnderline = view.findViewById(R.id.viewMonthlyUnderline);
        
        tvMyRank = view.findViewById(R.id.tvMyRank);
        tvMyPoints = view.findViewById(R.id.tvMyPoints);
        tvNeedPoints = view.findViewById(R.id.tvNeedPoints);
        tvProgressText = view.findViewById(R.id.tvProgressText);
        progressMyRank = view.findViewById(R.id.progressMyRank);

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                updateLeaderboardPeriod(checkedId == R.id.btnWeekly);
            }
        });

        btnSeeAllLeaderboard.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            renderLeaderboard();
        });

        view.findViewById(R.id.layoutMyRank).setOnClickListener(v -> {
            RewardCenterFragment fragment = new RewardCenterFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void updateLeaderboardPeriod(boolean isWeekly) {
        this.isWeeklySelected = isWeekly;
        viewWeeklyUnderline.setVisibility(isWeekly ? View.VISIBLE : View.INVISIBLE);
        viewMonthlyUnderline.setVisibility(isWeekly ? View.INVISIBLE : View.VISIBLE);
        renderLeaderboard();
    }

    private void setupRecyclerView() {
        adapter = new LeaderboardAdapter();
        rvLeaderboard.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ChallengeViewModel.class);
        
        viewModel.getWeeklyLeaderboard().observe(getViewLifecycleOwner(), users -> {
            this.weeklyUsers = users;
            if (isWeeklySelected) renderLeaderboard();
        });

        viewModel.getMonthlyLeaderboard().observe(getViewLifecycleOwner(), users -> {
            this.monthlyUsers = users;
            if (!isWeeklySelected) renderLeaderboard();
        });
    }

    private void renderLeaderboard() {
        List<LeaderboardUser> source = isWeeklySelected ? weeklyUsers : monthlyUsers;
        if (source == null || source.isEmpty()) return;

        List<LeaderboardUser> displayList;
        if (isExpanded) {
            displayList = source;
        } else {
            displayList = source.subList(0, Math.min(3, source.size()));
        }

        adapter.setUsers(new ArrayList<>(displayList));
        btnSeeAllLeaderboard.setText(isExpanded ? "Thu gọn" : "Xem tất cả");

        // Update current user rank card based on selected period
        for (LeaderboardUser user : source) {
            if (user.isCurrentUser()) {
                tvMyRank.setText(getString(R.string.leaderboard_rank_suffix, user.getRank()).replace("Hạng", "Hạng #"));
                tvMyPoints.setText(getString(R.string.leaderboard_points_suffix, user.getPoints()));
                tvNeedPoints.setText(String.format(Locale.getDefault(), "Cần thêm %d điểm để vào Top 10", user.getPointsToNextTarget()));
                tvProgressText.setText(String.format(Locale.getDefault(), "%d / %d điểm", user.getPoints(), user.getTargetPoints()));
                progressMyRank.setMax(user.getTargetPoints());
                progressMyRank.setProgress(user.getPoints());
                break;
            }
        }
    }
}
