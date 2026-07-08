package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class ChallengeLeaderboardFragment extends Fragment {

    private RecyclerView rvLeaderboard;
    private LeaderboardAdapter adapter;
    private ChallengeViewModel viewModel;
    private MaterialButtonToggleGroup toggleGroup;

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
        
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnWeekly) {
                    viewModel.getWeeklyLeaderboard().observe(getViewLifecycleOwner(), adapter::setUsers);
                } else {
                    viewModel.getMonthlyLeaderboard().observe(getViewLifecycleOwner(), adapter::setUsers);
                }
            }
        });

        view.findViewById(R.id.layoutMyRank).setOnClickListener(v -> {
            RewardCenterFragment fragment = new RewardCenterFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupRecyclerView() {
        adapter = new LeaderboardAdapter();
        rvLeaderboard.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ChallengeViewModel.class);
        viewModel.getWeeklyLeaderboard().observe(getViewLifecycleOwner(), adapter::setUsers);
    }
}
