package ui.community;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.HomeRepository;
import com.example.frontend.model.Product;
import java.util.List;

public class BlogViewModel extends AndroidViewModel {
    private final BlogRepository repository;
    private final HomeRepository homeRepository;
    private final MutableLiveData<NetworkResult<List<Product>>> suggestedProductsResult = new MutableLiveData<>();

    public BlogViewModel(@NonNull Application application) {
        super(application);
        this.repository = BlogRepository.getInstance(application);
        this.homeRepository = new HomeRepository(application);
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

    public void addComment(String blogId, Comment comment) {
        repository.addComment(blogId, comment);
    }

    public LiveData<NetworkResult<List<Product>>> getSuggestedProductsResult() {
        return suggestedProductsResult;
    }

    public void loadSuggestedProducts(List<String> productIds) {
        // Fetch products from backend - prioritization logic can be added later
        homeRepository.getProducts(null, suggestedProductsResult);
    }
}
