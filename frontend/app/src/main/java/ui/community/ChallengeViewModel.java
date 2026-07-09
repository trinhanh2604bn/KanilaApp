package ui.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class ChallengeViewModel extends ViewModel {
    private final ChallengeRepository repository;

    public ChallengeViewModel() {
        this.repository = ChallengeRepository.getInstance();
    }

    public LiveData<List<Challenge>> getActiveChallenges() {
        return repository.getActiveChallenges();
    }

    public LiveData<List<Challenge>> getJoinedChallenges() {
        return repository.getJoinedChallenges();
    }

    public LiveData<List<LeaderboardUser>> getWeeklyLeaderboard() {
        return repository.getWeeklyLeaderboard();
    }

    public LiveData<List<LeaderboardUser>> getMonthlyLeaderboard() {
        return repository.getMonthlyLeaderboard();
    }

    public Challenge getChallengeById(String id) {
        return repository.getChallengeById(id);
    }
}
