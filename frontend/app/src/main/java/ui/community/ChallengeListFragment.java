package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import java.util.List;

public class ChallengeListFragment extends Fragment implements ChallengeAdapter.OnChallengeClickListener {

    private static final String ARG_TYPE = "type";
    public static final int TYPE_ONGOING = 0;
    public static final int TYPE_JOINED = 1;

    private int type;
    private ChallengeAdapter adapter;
    private ChallengeViewModel viewModel;
    private CommunityViewModel communityViewModel;
    private List<Challenge> allChallenges;

    public static ChallengeListFragment newInstance(int type) {
        ChallengeListFragment fragment = new ChallengeListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_list, container, false);
        RecyclerView rvChallenges = view.findViewById(R.id.rvChallenges);
        
        adapter = new ChallengeAdapter(this);
        rvChallenges.setAdapter(adapter);
        
        viewModel = new ViewModelProvider(requireActivity()).get(ChallengeViewModel.class);
        communityViewModel = new ViewModelProvider(requireActivity()).get(CommunityViewModel.class);
        
        if (type == TYPE_ONGOING) {
            viewModel.getActiveChallenges().observe(getViewLifecycleOwner(), challenges -> {
                allChallenges = challenges;
                filterAndSetChallenges(challenges, communityViewModel.getSearchQuery().getValue());
            });
        } else {
            viewModel.getJoinedChallenges().observe(getViewLifecycleOwner(), challenges -> {
                allChallenges = challenges;
                filterAndSetChallenges(challenges, communityViewModel.getSearchQuery().getValue());
            });
        }

        communityViewModel.getSearchQuery().observe(getViewLifecycleOwner(), query -> {
            filterAndSetChallenges(allChallenges, query);
        });
        
        return view;
    }

    private void filterAndSetChallenges(List<Challenge> challenges, String query) {
        if (challenges == null) return;
        if (query == null || query.isEmpty()) {
            adapter.setChallenges(challenges);
            return;
        }

        java.util.List<Challenge> filteredList = new java.util.ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Challenge challenge : challenges) {
            if (challenge.getTitle().toLowerCase().contains(lowerQuery) ||
                (challenge.getDescription() != null && challenge.getDescription().toLowerCase().contains(lowerQuery))) {
                filteredList.add(challenge);
            }
        }
        adapter.setChallenges(filteredList);
    }

    @Override
    public void onChallengeClick(Challenge challenge) {
        Fragment fragment;
        if (type == TYPE_JOINED) {
            // In Joined tab, clicking goes to the Active view to continue progress
            fragment = ChallengeActiveFragment.newInstance(challenge.getId());
        } else {
            // In Ongoing tab, always show detail view (discovery/rules) even if already joined
            fragment = ChallengeDetailFragment.newInstance(challenge.getId());
        }
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onActionClick(Challenge challenge) {
        // Primary action follows the same logic as clicking the item
        onChallengeClick(challenge);
    }
}
