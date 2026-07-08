package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.frontend.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CommunityChallengeFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_challenge, container, false);
        tabLayout = view.findViewById(R.id.challengeTabLayout);
        viewPager = view.findViewById(R.id.challengeViewPager);
        setupTabs();
        return view;
    }

    private void setupTabs() {
        viewPager.setAdapter(new ChallengePagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText(R.string.challenge_tab_ongoing); break;
                case 1: tab.setText(R.string.challenge_tab_joined); break;
                case 2: tab.setText(R.string.challenge_tab_leaderboard); break;
            }
        }).attach();
    }

    private static class ChallengePagerAdapter extends FragmentStateAdapter {
        public ChallengePagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return ChallengeListFragment.newInstance(ChallengeListFragment.TYPE_ONGOING);
            if (position == 1) return ChallengeListFragment.newInstance(ChallengeListFragment.TYPE_JOINED);
            return new ChallengeLeaderboardFragment();
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
