package ui.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.R;
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
        Challenge c1 = new Challenge("1", "7 ngày môi xinh căng mọng", R.drawable.img_challenge1, 12600, 200, 7, true, false);
        c1.setDescription("Cùng Kanila thực hiện quy trình dưỡng da sáng khỏe trong 14 ngày để nhận ngay 500 điểm thưởng.");
        c1.setRules("1. Chụp ảnh da mỗi ngày\n2. Sử dụng ít nhất 2 sản phẩm gợi ý\n3. Đăng bài công khai");
        
        List<ChallengeTask> tasks = new ArrayList<>();
        tasks.add(new ChallengeTask("t1", "Check-in hôm nay", "Hoàn thành để giữ chuỗi ngày", R.drawable.ic_calendar, false));
        tasks.add(new ChallengeTask("t2", "Đăng ảnh/clip cập nhật", "Chia sẻ tiến trình chăm sóc da", R.drawable.ic_camera, false));
        tasks.add(new ChallengeTask("t3", "Sử dụng sản phẩm Kanila", "Dùng sản phẩm theo gợi ý của chuyên gia", R.drawable.ic_checklist, false));
        c1.setTasks(tasks);
        
        active.add(c1);
        c1.setRemainingTime("Còn 05 ngày 16 giờ");
        
        Challenge c2 = new Challenge("2", "10 ngày nền mịn không mốc", R.drawable.img_challenge2, 8500, 200, 10, false, true);
        c2.setRemainingTime("Còn 08 ngày 04 giờ");
        c2.setTasks(tasks); // Reuse tasks for mock
        active.add(c2);

        Challenge c3 = new Challenge("3", "5 ngày má hồng tươi tắn", R.drawable.img_challenge3, 2100, 150, 5, true, false);
        c3.setRemainingTime("Còn 02 ngày 12 giờ");
        c3.setTasks(tasks);
        active.add(c3);

        Challenge c4 = new Challenge("4", "12 ngày mắt long lanh", R.drawable.img_challenge4, 5400, 250, 12, false, false);
        c4.setRemainingTime("Còn 10 ngày 20 giờ");
        c4.setTasks(tasks);
        active.add(c4);

        Challenge c5 = new Challenge("5", "8 ngày chân mày gọn xinh", R.drawable.img_challenge5, 3200, 180, 8, true, false);
        c5.setRemainingTime("Còn 06 ngày 05 giờ");
        c5.setTasks(tasks);
        active.add(c5);

        Challenge c6 = new Challenge("6", "14 ngày makeup tối giản", R.drawable.img_challenge6, 7800, 300, 14, false, true);
        c6.setRemainingTime("Còn 12 ngày 01 giờ");
        c6.setTasks(tasks);
        active.add(c6);
        
        activeChallenges.setValue(active);

        List<Challenge> joined = new ArrayList<>();
        Challenge c1Joined = new Challenge("1", "7 ngày môi xinh căng mọng", R.drawable.img_challenge1, 12600, 200, 7, true, false);
        c1Joined.setJoined(true);
        c1Joined.setCurrentProgress(3);
        c1Joined.setRemainingTime("Còn 05 ngày 16 giờ");
        joined.add(c1Joined);
        joinedChallenges.setValue(joined);

        List<LeaderboardUser> weekly = new ArrayList<>();
        weekly.add(new LeaderboardUser(1, "Thanh Mai", null, 4653));
        weekly.add(new LeaderboardUser(2, "Kim Trần", null, 2753));
        weekly.add(new LeaderboardUser(3, "Mộc Hà", null, 1653));
        weekly.add(new LeaderboardUser(4, "Hoàng Nam", null, 1550));
        weekly.add(new LeaderboardUser(5, "Tú Uyên", null, 1480));
        weekly.add(new LeaderboardUser(6, "Anh Thư", null, 1420));
        weekly.add(new LeaderboardUser(7, "Minh Quân", null, 1390));
        weekly.add(new LeaderboardUser(8, "Bảo Ngọc", null, 1350));
        weekly.add(new LeaderboardUser(9, "Khánh Linh", null, 1310));
        weekly.add(new LeaderboardUser(10, "Quang Huy", null, 1280));
        weekly.add(new LeaderboardUser(11, "Thùy Trang", null, 1250));
        weekly.add(new LeaderboardUser(12, "Gia Bảo", null, 1220));
        weekly.add(new LeaderboardUser(13, "Ngọc Ánh", null, 1200));
        weekly.add(new LeaderboardUser(14, "Thành Công", null, 1180));
        weekly.add(new LeaderboardUser(15, "Yến Nhi", null, 1160));
        weekly.add(new LeaderboardUser(16, "Việt Anh", null, 1150));
        weekly.add(new LeaderboardUser(17, "Diệu Linh", null, 1130));
        weekly.add(new LeaderboardUser(18, "Bạn (current user)", null, 1120, true, 1500, 380));
        weekly.add(new LeaderboardUser(19, "Minh Đức", null, 1100));
        weekly.add(new LeaderboardUser(20, "Phương Thảo", null, 1080));
        weeklyLeaderboard.setValue(weekly);
        
        List<LeaderboardUser> monthly = new ArrayList<>();
        monthly.add(new LeaderboardUser(1, "Thanh Thanh", null, 8950));
        monthly.add(new LeaderboardUser(2, "Minh Anh", null, 8220));
        monthly.add(new LeaderboardUser(3, "Ngọc Diệp", null, 7850));
        monthly.add(new LeaderboardUser(4, "Hoàng Nam", null, 7200));
        monthly.add(new LeaderboardUser(5, "Tú Uyên", null, 6980));
        monthly.add(new LeaderboardUser(6, "Anh Thư", null, 6500));
        monthly.add(new LeaderboardUser(7, "Minh Quân", null, 6200));
        monthly.add(new LeaderboardUser(8, "Bảo Ngọc", null, 5900));
        monthly.add(new LeaderboardUser(9, "Khánh Linh", null, 5600));
        monthly.add(new LeaderboardUser(10, "Quang Huy", null, 5300));
        monthly.add(new LeaderboardUser(11, "Thùy Trang", null, 5100));
        monthly.add(new LeaderboardUser(12, "Gia Bảo", null, 4900));
        monthly.add(new LeaderboardUser(13, "Ngọc Ánh", null, 4700));
        monthly.add(new LeaderboardUser(14, "Thành Công", null, 4550));
        monthly.add(new LeaderboardUser(15, "Yến Nhi", null, 4480));
        monthly.add(new LeaderboardUser(16, "Việt Anh", null, 4420));
        monthly.add(new LeaderboardUser(17, "Diệu Linh", null, 4390));
        monthly.add(new LeaderboardUser(18, "Bạn (current user)", null, 4320, true, 5000, 680));
        monthly.add(new LeaderboardUser(19, "Minh Đức", null, 4200));
        monthly.add(new LeaderboardUser(20, "Phương Thảo", null, 4100));
        monthlyLeaderboard.setValue(monthly);
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
