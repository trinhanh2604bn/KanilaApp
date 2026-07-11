package ui.community;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;

public class CommunityViewModel extends AndroidViewModel {
    private final CommunityRepository repository;
    private final CommunityNotificationRepository notificationRepository;
    private final LiveData<List<Post>> feedPosts;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    public CommunityViewModel(android.app.Application application) {
        super(application);
        repository = CommunityRepository.getInstance(application);
        notificationRepository = CommunityNotificationRepository.getInstance();
        feedPosts = repository.getFeedPosts();
    }

    public LiveData<List<Post>> getFeedPosts() {
        return feedPosts;
    }

    public LiveData<List<CommunityNotification>> getNotifications() {
        return notificationRepository.getNotifications();
    }

    public LiveData<Integer> getUnreadCount() {
        return notificationRepository.getUnreadCount();
    }

    public void markNotificationAsRead(String id) {
        notificationRepository.markAsRead(id);
    }

    public void markAllNotificationsAsRead() {
        notificationRepository.markAllAsRead();
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public void addPost(Post post) {
        repository.addPost(post);
    }

    public void addComment(String postId, Comment comment) {
        repository.addComment(postId, comment);
    }

    public void updatePost(Post post) {
        repository.updatePost(post);
    }

    public void deletePost(String postId) {
        repository.deletePost(postId);
    }

    public void toggleSave(String postId, boolean isSaved) {
        repository.toggleSave(postId, isSaved);
    }

    public Post getPostById(String id) {
        List<Post> posts = feedPosts.getValue();
        if (posts != null) {
            for (Post p : posts) {
                if (p.getId().equals(id)) return p;
            }
        }
        return null;
    }
}
