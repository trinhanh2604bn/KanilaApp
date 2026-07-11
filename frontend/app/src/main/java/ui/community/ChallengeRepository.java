package ui.community;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChallengeRepository {
    private static ChallengeRepository instance;
    private static final String PREF_NAME = "challenge_prefs";
    private static final String KEY_JOINED_CHALLENGES = "joined_challenges";
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    private final MutableLiveData<List<Challenge>> activeChallenges = new MutableLiveData<>();
    private final MutableLiveData<List<Challenge>> joinedChallenges = new MutableLiveData<>();
    private final MutableLiveData<List<LeaderboardUser>> weeklyLeaderboard = new MutableLiveData<>();
    private final MutableLiveData<List<LeaderboardUser>> monthlyLeaderboard = new MutableLiveData<>();

    private ChallengeRepository(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadMockData();
        loadJoinedChallenges();
    }

    private void saveJoinedChallenges() {
        List<Challenge> list = joinedChallenges.getValue();
        if (list == null) return;
        String json = gson.toJson(list);
        prefs.edit().putString(KEY_JOINED_CHALLENGES, json).apply();
    }

    private void loadJoinedChallenges() {
        String json = prefs.getString(KEY_JOINED_CHALLENGES, null);
        if (json != null) {
            try {
                Type type = new TypeToken<List<Challenge>>(){}.getType();
                List<Challenge> list = gson.fromJson(json, type);
                if (list != null) {
                    joinedChallenges.setValue(list);
                    syncActiveChallengesWithJoined(list);
                } else {
                    joinedChallenges.setValue(new ArrayList<>());
                }
            } catch (Exception e) {
                joinedChallenges.setValue(new ArrayList<>());
            }
        } else {
            joinedChallenges.setValue(new ArrayList<>());
        }
    }

    private void syncActiveChallengesWithJoined(List<Challenge> joinedList) {
        List<Challenge> active = activeChallenges.getValue();
        if (active == null || joinedList == null) return;

        boolean changed = false;
        for (Challenge jc : joinedList) {
            for (Challenge ac : active) {
                if (ac.getId().equals(jc.getId())) {
                    ac.setJoined(true);
                    ac.setCurrentProgress(jc.getCurrentProgress());
                    ac.setTasks(jc.getTasks());
                    changed = true;
                    break;
                }
            }
        }
        if (changed) {
            activeChallenges.setValue(new ArrayList<>(active));
        }
    }

    public static ChallengeRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ChallengeRepository(context);
        }
        return instance;
    }

    private void loadMockData() {
        List<Challenge> active = new ArrayList<>();
        
        // Product IDs from backend for consistency
        List<String> primerProducts = new ArrayList<>();
        primerProducts.add("000000000000000000000fad"); // Milk Makeup
        primerProducts.add("000000000000000000000fab"); // e.l.f
        primerProducts.add("000000000000000000000fac"); // Benefit

        Challenge c1 = new Challenge("1", "7 ngày môi xinh căng mọng", R.drawable.img_challenge1, 12600, 200, 7, true, false);
        c1.setDescription("Cùng Kanila thực hiện quy trình dưỡng da môi trong 7 ngày");
        c1.setProductIds(primerProducts); // Mocking with primers for now
        c1.setTasks(generateTasks(c1.getDurationDays()));
        c1.setParticipants(generateMockParticipants());
        active.add(c1);
        c1.setRemainingTime("Còn 05 ngày 16 giờ");
        
        Challenge c2 = new Challenge("2", "10 ngày nền mịn không mốc", R.drawable.img_challenge2, 8500, 200, 10, false, true);
        c2.setDescription("Cùng Kanila thực hiện quy trình dưỡng da môi trong 7 ngày");

        c2.setRemainingTime("Còn 08 ngày 04 giờ");
        c2.setProductIds(primerProducts);
        c2.setTasks(generateTasks(c2.getDurationDays()));
        c2.setParticipants(generateMockParticipants());
        active.add(c2);

        Challenge c3 = new Challenge("3", "5 ngày má hồng tươi tắn", R.drawable.img_challenge3, 2100, 150, 5, true, false);
        c3.setDescription("Cùng Kanila thực hiện quy trình dưỡng da môi trong 7 ngày");

        c3.setRemainingTime("Còn 02 ngày 12 giờ");
        c3.setProductIds(primerProducts);
        c3.setTasks(generateTasks(c3.getDurationDays()));
        c3.setParticipants(generateMockParticipants());
        active.add(c3);

        Challenge c4 = new Challenge("4", "12 ngày mắt long lanh", R.drawable.img_challenge4, 5400, 250, 12, false, false);
        c4.setDescription("Cùng Kanila thực hiện quy trình dưỡng da môi trong 7 ngày");

        c4.setRemainingTime("Còn 10 ngày 20 giờ");
        c4.setProductIds(primerProducts);
        c4.setTasks(generateTasks(c4.getDurationDays()));
        c4.setParticipants(generateMockParticipants());
        active.add(c4);

        Challenge c5 = new Challenge("5", "8 ngày chân mày gọn xinh", R.drawable.img_challenge5, 3200, 180, 8, true, false);
        c5.setDescription("Cùng Kanila thực hiện quy trình dưỡng da môi trong 7 ngày");

        c5.setRemainingTime("Còn 06 ngày 05 giờ");
        c5.setProductIds(primerProducts);
        c5.setTasks(generateTasks(c5.getDurationDays()));
        c5.setParticipants(generateMockParticipants());
        active.add(c5);

        Challenge c6 = new Challenge("6", "14 ngày makeup tối giản", R.drawable.img_challenge6, 7800, 300, 14, false, true);
        c6.setDescription("Cùng Kanila thực hiện quy trình dưỡng da môi trong 7 ngày");

        c6.setRemainingTime("Còn 12 ngày 01 giờ");
        c6.setProductIds(primerProducts);
        c6.setTasks(generateTasks(c6.getDurationDays()));
        c6.setParticipants(generateMockParticipants());
        active.add(c6);
        
        activeChallenges.setValue(active);

        // Leaderboard mock data
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

    private List<ChallengeParticipant> generateMockParticipants() {
        List<ChallengeParticipant> list = new ArrayList<>();
        
        List<Post> posts1 = new ArrayList<>();
        posts1.add(new Post("p1", "Thanh Mai", null, "1 ngày trước", "Challenge Ngày 1", "Môi mịn hơn hẳn sau khi tẩy tế bào chết", 
                Collections.singletonList("https://images.unsplash.com/photo-1596462502278-27bfad40038a"), 12, 2, 0, true, true));
        posts1.add(new Post("p2", "Thanh Mai", null, "Vừa xong", "Challenge Ngày 2", "Dưỡng môi ban đêm cực thích", 
                Collections.singletonList("https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9"), 8, 0, 0, true, true));
        list.add(new ChallengeParticipant("u1", "Thanh Mai", null, posts1));

        List<Post> posts2 = new ArrayList<>();
        posts2.add(new Post("p3", "Kim Trần", null, "2 ngày trước", "Day 1 #LipChallenge", "Bắt đầu hành trình thôi", 
                Collections.singletonList("https://images.unsplash.com/photo-1512496011931-d21d80327b0c"), 15, 5, 1, false, true));
        list.add(new ChallengeParticipant("u2", "Kim Trần", null, posts2));

        List<Post> posts3 = new ArrayList<>();
        posts3.add(new Post("p4", "Mộc Hà", null, "3 ngày trước", "Môi xinh", "Yêu routine này quá", 
                Collections.singletonList("https://images.unsplash.com/photo-1515688598190-829278833959"), 20, 3, 2, true, true));
        list.add(new ChallengeParticipant("u3", "Mộc Hà", null, posts3));

        return list;
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

    private List<ChallengeTask> generateTasks(int days) {
        List<ChallengeTask> tasks = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            tasks.add(new ChallengeTask("day_" + i, "Nhiệm vụ Ngày " + i, "Thực hiện đúng quy trình để đạt hiệu quả", R.drawable.ic_routine, false));
        }
        return tasks;
    }

    public void completeTask(String challengeId, String taskId) {
        completeTask(challengeId, taskId, null);
    }

    public void completeTask(String challengeId, String taskId, Post post) {
        List<Challenge> active = activeChallenges.getValue();
        if (active != null) {
            for (Challenge c : active) {
                if (c.getId().equals(challengeId)) {
                    for (ChallengeTask t : c.getTasks()) {
                        if (t.getId().equals(taskId)) {
                            t.setCompleted(true);
                            int completedCount = 0;
                            for (ChallengeTask task : c.getTasks()) {
                                if (task.isCompleted()) completedCount++;
                            }
                            c.setCurrentProgress(completedCount);

                            // Add post to current user's participant entry
                            if (post != null && c.getParticipants() != null) {
                                boolean userFound = false;
                                for (ChallengeParticipant cp : c.getParticipants()) {
                                    // Assuming "current_user" or matching userId for the current user
                                    if (cp.getUserName().equals("Bạn") || cp.getUserName().contains("(current user)")) {
                                        cp.getProgressPosts().add(0, post);
                                        userFound = true;
                                        break;
                                    }
                                }
                                // If user wasn't in participants list yet (shouldn't happen if joined properly)
                                if (!userFound) {
                                    List<Post> posts = new ArrayList<>();
                                    posts.add(post);
                                    c.addParticipant(new ChallengeParticipant("u_me", post.getUserName(), post.getUserAvatar(), posts));
                                }
                            }

                            activeChallenges.setValue(new ArrayList<>(active));
                            
                            // Also update joined list if it's there
                            List<Challenge> joined = joinedChallenges.getValue();
                            if (joined != null) {
                                boolean updated = false;
                                for (Challenge jc : joined) {
                                    if (jc.getId().equals(challengeId)) {
                                        jc.setCurrentProgress(completedCount);
                                        jc.setTasks(c.getTasks());
                                        jc.setParticipants(c.getParticipants());
                                        updated = true;
                                        break;
                                    }
                                }
                                if (updated) {
                                    joinedChallenges.setValue(new ArrayList<>(joined));
                                    saveJoinedChallenges();
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public void joinChallenge(String challengeId, ChallengeParticipant user) {
        List<Challenge> active = activeChallenges.getValue();
        Challenge target = null;
        if (active != null) {
            for (Challenge c : active) {
                if (c.getId().equals(challengeId)) {
                    c.setJoined(true);
                    c.setCurrentProgress(1); // Start at day 1
                    if (user != null) {
                        c.addParticipant(user);
                    }
                    target = c;
                    activeChallenges.setValue(new ArrayList<>(active));
                    break;
                }
            }
        }

        if (target != null) {
            List<Challenge> joined = joinedChallenges.getValue();
            if (joined == null) joined = new ArrayList<>();
            
            // Check if already in joined list
            boolean exists = false;
            for (Challenge c : joined) {
                if (c.getId().equals(challengeId)) {
                    exists = true;
                    break;
                }
            }
            
            if (!exists) {
                joined.add(0, target);
                joinedChallenges.setValue(new ArrayList<>(joined));
                saveJoinedChallenges();
            }
        }
    }
}
