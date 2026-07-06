package ui.category;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.CatalogRepository;
import com.example.frontend.model.Brand;
import com.example.frontend.model.Category;
import java.util.List;

public class CatalogViewModel extends AndroidViewModel {
    private final CatalogRepository repository;
    private final LiveData<NetworkResult<List<Brand>>> brands;
    private final LiveData<NetworkResult<List<Category>>> categories;

    public CatalogViewModel(@NonNull Application application) {
        super(application);
        repository = new CatalogRepository(application);
        brands = repository.getBrands();
        categories = repository.getCategories();
    }

    public LiveData<NetworkResult<List<Brand>>> getBrands() {
        return brands;
    }

    public LiveData<NetworkResult<List<Category>>> getCategories() {
        return categories;
    }
    
    public void refreshBrands() {
        // In a more advanced setup, we'd have a trigger to re-fetch
    }
}
