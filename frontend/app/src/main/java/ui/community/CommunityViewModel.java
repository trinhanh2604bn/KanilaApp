package ui.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class CommunityViewModel extends ViewModel {
    private final CommunityRepository repository;
    private final CommunityNotificationRepository notificationRepository;
    private final LiveData<List<Post>> feedPosts;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    public CommunityViewModel() {
        repository = CommunityRepository.getInstance();
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
}
