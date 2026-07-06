package com.example.frontend.feature.product;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.product.ProductVariantDto;
import com.example.frontend.data.model.product.ProductMediaDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.model.Product;
import java.util.List;

public class ProductDetailViewModel extends AndroidViewModel {
    private final ProductRepository repository;
    private final MutableLiveData<NetworkResult<Product>> productResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<ProductMediaDto>>> mediaResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<ProductVariantDto>>> variantsResult = new MutableLiveData<>();

    public ProductDetailViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ProductRepository(application);
    }

    public LiveData<NetworkResult<Product>> getProductResult() {
        return productResult;
    }

    public LiveData<NetworkResult<List<ProductMediaDto>>> getMediaResult() {
        return mediaResult;
    }

    public LiveData<NetworkResult<List<ProductVariantDto>>> getVariantsResult() {
        return variantsResult;
    }

    public void loadProductDetails(String productId) {
        // We can't directly assign LiveData from repository to our private MutableLiveData
        // So we observe it or use transformations. For simplicity in this audit:
        repository.getProductById(productId).observeForever(productResult::setValue);
        repository.getProductMedia(productId, mediaResult);
        repository.getProductVariants(productId, variantsResult);
    }
}
