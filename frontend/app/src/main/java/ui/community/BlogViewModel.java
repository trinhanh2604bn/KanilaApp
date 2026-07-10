package ui.community;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class BlogViewModel extends AndroidViewModel {
    private final BlogRepository repository;

    public BlogViewModel(@NonNull Application application) {
        super(application);
        this.repository = BlogRepository.getInstance(application);
    }

    public LiveData<List<BlogPost>> getFeaturedBlogs() {
        return repository.getFeaturedBlogs();
    }

    public BlogPost getBlogById(String id) {
        return repository.getBlogById(id);
    }

    public void toggleSaveBlog(String blogId, boolean isSaved) {
        repository.toggleSaveBlog(blogId, isSaved);
    }

    public void toggleLikeBlog(String blogId, boolean isLiked) {
        repository.toggleLikeBlog(blogId, isLiked);
    }
}
