package ui.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;

public class ChallengeRepository {
    private static ChallengeRepository instance;
    private final MutableLiveData<List<Challenge>> activeChallenges = new MutableLiveData<>();
    private final MutableLiveData<List<Challenge>> joinedChallenges = new MutableLiveData<>();
    private final MutableLiveData<List<LeaderboardUser>> weeklyLeaderboard = new MutableLiveData<>();
    private final MutableLiveData<List<LeaderboardUser>> monthlyLeaderboard = new MutableLiveData<>();

    private ChallengeRepository() {
        loadMockData();
    }

    public static ChallengeRepository getInstance() {
        if (instance == null) {
            instance = new ChallengeRepository();
        }
        return instance;
    }

    private void loadMockData() {
        List<Challenge> active = new ArrayList<>();
        Challenge c1 = new Challenge("1", "14 ngày da sáng khỏe", null, 1250, 500, 14, true, false);
        c1.setDescription("Cùng Kanila thực hiện quy trình dưỡng da sáng khỏe trong 14 ngày để nhận ngay 500 điểm thưởng.");
        c1.setRules("1. Chụp ảnh da mỗi ngày\n2. Sử dụng ít nhất 2 sản phẩm gợi ý\n3. Đăng bài công khai");
        
        List<ChallengeTask> tasks = new ArrayList<>();
        tasks.add(new ChallengeTask("t1", "Làm sạch da buổi sáng", false));
        tasks.add(new ChallengeTask("t2", "Sử dụng Serum Vitamin C", false));
        tasks.add(new ChallengeTask("t3", "Thoa kem chống nắng", false));
        c1.setTasks(tasks);
        
        active.add(c1);
        active.add(new Challenge("2", "Thử thách Makeup Clean Girl", null, 850, 300, 7, false, true));
        active.add(new Challenge("3", "7 ngày kiềm dầu cùng Eucerin", null, 2100, 1000, 7, true, false));
        activeChallenges.setValue(active);

        List<Challenge> joined = new ArrayList<>();
        Challenge c1Joined = new Challenge("1", "14 ngày da sáng khỏe", null, 1250, 500, 14, true, false);
        c1Joined.setJoined(true);
        c1Joined.setCurrentProgress(8);
        joined.add(c1Joined);
        joinedChallenges.setValue(joined);

        List<LeaderboardUser> weekly = new ArrayList<>();
        weekly.add(new LeaderboardUser(1, "Thanh Thanh", null, 2500));
        weekly.add(new LeaderboardUser(2, "Minh Anh", null, 2350));
        weekly.add(new LeaderboardUser(3, "Ngọc Diệp", null, 2200));
        weekly.add(new LeaderboardUser(4, "Hoàng Nam", null, 1950));
        weekly.add(new LeaderboardUser(5, "Tú Uyên", null, 1800));
        weeklyLeaderboard.setValue(weekly);
        
        monthlyLeaderboard.setValue(weekly); // reuse for mock
    }

    public LiveData<List<Challenge>> getActiveChallenges() { return activeChallenges; }
    public LiveData<List<Challenge>> getJoinedChallenges() { return joinedChallenges; }
    public LiveData<List<LeaderboardUser>> getWeeklyLeaderboard() { return weeklyLeaderboard; }
    public LiveData<List<LeaderboardUser>> getMonthlyLeaderboard() { return monthlyLeaderboard; }

    public Challenge getChallengeById(String id) {
        if (activeChallenges.getValue() != null) {
            for (Challenge c : activeChallenges.getValue()) {
                if (c.getId().equals(id)) return c;
            }
        }
        return null;
    }
}
