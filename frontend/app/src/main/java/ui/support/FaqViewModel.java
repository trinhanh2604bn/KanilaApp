package ui.support;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class FaqViewModel extends ViewModel {
    
    public enum FaqStatus {
        PENDING,    // Đang chờ xử lý
        PROCESSING, // Đang được trả lời
        ANSWERED    // Đã trả lời
    }

    public static class FaqItemData {
        public String question;
        public String answer;
        public String category;
        public FaqStatus status;
        public long timestamp;

        public FaqItemData(String question, String answer, String category) {
            this(question, answer, category, FaqStatus.ANSWERED);
        }

        public FaqItemData(String question, String answer, String category, FaqStatus status) {
            this.question = question;
            this.answer = answer;
            this.category = category;
            this.status = status;
            this.timestamp = System.currentTimeMillis();
        }
    }

    // Static to persist across different fragment instances in the same session
    private static final List<FaqItemData> userQuestions = new ArrayList<>();
    private final MutableLiveData<List<FaqItemData>> _questions = new MutableLiveData<>(userQuestions);
    public final LiveData<List<FaqItemData>> questions = _questions;

    public void addQuestion(String question, String category) {
        userQuestions.add(0, new FaqItemData(question, 
            "Cảm ơn bạn đã đặt câu hỏi! Kanila đã ghi nhận và sẽ có chuyên viên phản hồi chi tiết cho bạn trong giây lát.",
            category,
            FaqStatus.PENDING));
        _questions.setValue(new ArrayList<>(userQuestions));
    }

    public List<FaqItemData> getUserQuestions() {
        return new ArrayList<>(userQuestions);
    }

    public List<FaqItemData> getQuestionsByCategory(String category) {
        List<FaqItemData> filtered = new ArrayList<>();
        for (FaqItemData item : userQuestions) {
            if (category.equals(item.category) || "Tất cả câu hỏi".equals(category)) {
                filtered.add(item);
            }
        }
        return filtered;
    }
}
