package ui.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class CommunityProfileViewModel extends ViewModel {
    private CommunityProfileRepository repository;

    public CommunityProfileViewModel() {
        this.repository = CommunityProfileRepository.getInstance();
    }

    public LiveData<CommunityProfile> getMyProfile() {
        return repository.getMyProfile();
    }

    public LiveData<List<Post>> getMyPosts() {
        return repository.getMyPosts();
    }

    public LiveData<List<SavedContent>> getSavedContent() {
        return repository.getSavedContent();
    }
}
