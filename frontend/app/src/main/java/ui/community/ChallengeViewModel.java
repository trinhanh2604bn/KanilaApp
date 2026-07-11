package ui.community;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class ChallengeViewModel extends AndroidViewModel {
    private final ChallengeRepository repository;

    public ChallengeViewModel(@NonNull Application application) {
        super(application);
        this.repository = ChallengeRepository.getInstance(application);
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

    public void completeTask(String challengeId, String taskId) {
        repository.completeTask(challengeId, taskId);
    }

    public void completeTask(String challengeId, String taskId, Post post) {
        repository.completeTask(challengeId, taskId, post);
    }

    public void joinChallenge(String challengeId, ChallengeParticipant user) {
        repository.joinChallenge(challengeId, user);
    }
}
