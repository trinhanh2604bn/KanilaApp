package ui.category;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.model.Product;
import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    private final ProductRepository repository;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ProductRepository(application);
    }

    public LiveData<NetworkResult<List<Product>>> getProducts(String query, String categoryId, String brandId) {
        return repository.getProducts(query, categoryId, brandId);
    }

    public LiveData<NetworkResult<Product>> getProductById(String id) {
        return repository.getProductById(id);
    }
}
