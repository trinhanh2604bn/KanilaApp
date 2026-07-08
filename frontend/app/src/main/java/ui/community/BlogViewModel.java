package ui.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class BlogViewModel extends ViewModel {
    private BlogRepository repository;

    public BlogViewModel() {
        this.repository = BlogRepository.getInstance();
    }

    public LiveData<List<BlogPost>> getFeaturedBlogs() {
        return repository.getFeaturedBlogs();
    }

    public BlogPost getBlogById(String id) {
        return repository.getBlogById(id);
    }
}
