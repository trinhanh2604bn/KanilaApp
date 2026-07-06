package ui.category;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.CatalogRepository;
import com.example.frontend.model.Brand;
import com.example.frontend.model.Category;
import java.util.List;

public class CatalogViewModel extends AndroidViewModel {
    private final CatalogRepository repository;
    private final MutableLiveData<Boolean> brandTrigger = new MutableLiveData<>();
    private final LiveData<NetworkResult<List<Brand>>> brands;
    
    private final MutableLiveData<Boolean> categoryTrigger = new MutableLiveData<>();
    private final LiveData<NetworkResult<List<Category>>> categories;

    public CatalogViewModel(@NonNull Application application) {
        super(application);
        repository = new CatalogRepository(application);
        
        brands = Transformations.switchMap(brandTrigger, input -> repository.getBrands());
        categories = Transformations.switchMap(categoryTrigger, input -> repository.getCategories());
    }

    public LiveData<NetworkResult<List<Brand>>> getBrands() {
        return brands;
    }

    public LiveData<NetworkResult<List<Category>>> getCategories() {
        return categories;
    }
    
    public void loadBrands() {
        brandTrigger.setValue(true);
    }
    
    public void loadCategories() {
        categoryTrigger.setValue(true);
    }
}
