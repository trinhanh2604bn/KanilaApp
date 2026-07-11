package com.example.frontend.feature.beauty;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.beauty.BeautyReferenceDto;
import com.example.frontend.data.model.beauty.CustomerBeautyProfileDto;
import com.example.frontend.data.model.beauty.SavedRoutineDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.BeautyProfileRepository;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.model.Product;
import java.util.ArrayList;
import java.util.List;

public class BeautyProfileViewModel extends AndroidViewModel {
    private final BeautyProfileRepository repository;
    private final ProductRepository productRepository;
    private final MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> profileResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<BeautyReferenceDto>>> referencesResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<Product>>> recommendedProductsResult = new MutableLiveData<>();
    
    // In-memory storage for saved routines
    private final MutableLiveData<List<SavedRoutineDto>> savedRoutines = new MutableLiveData<>(new ArrayList<>());

    public BeautyProfileViewModel(@NonNull Application application) {
        super(application);
        this.repository = new BeautyProfileRepository(application);
        this.productRepository = new ProductRepository(application);
        // Initialize with some default items if empty
        initDefaultRoutines();
    }

    private void initDefaultRoutines() {
        List<SavedRoutineDto> current = new ArrayList<>();
        current.add(new SavedRoutineDto("default_1", "Clean Girl Look", System.currentTimeMillis() - (2 * 60 * 60 * 1000), com.example.frontend.R.drawable.hinh_nen));
        current.add(new SavedRoutineDto("default_2", "Glass Skin Routine", System.currentTimeMillis() - (4L * 24 * 60 * 60 * 1000), com.example.frontend.R.drawable.bg_slide_1));
        savedRoutines.setValue(current);
    }

    public LiveData<NetworkResult<CustomerBeautyProfileDto>> getProfileResult() {
        return profileResult;
    }

    public LiveData<NetworkResult<List<BeautyReferenceDto>>> getReferencesResult() {
        return referencesResult;
    }

    public LiveData<NetworkResult<List<Product>>> getRecommendedProductsResult() {
        return recommendedProductsResult;
    }

    public LiveData<List<SavedRoutineDto>> getSavedRoutines() {
        return savedRoutines;
    }

    public void saveRoutine(SavedRoutineDto routine) {
        List<SavedRoutineDto> current = savedRoutines.getValue();
        if (current == null) current = new ArrayList<>();
        
        // Remove existing if same ID or Name to avoid duplicates in this simple mock
        current.removeIf(item -> item.getName().equals(routine.getName()));
        
        current.add(0, routine); // Add to top
        savedRoutines.setValue(current);
    }

    public void loadProfile(String customerId) {
        repository.getBeautyProfile(customerId, profileResult);
    }

    public void updateProfile(String customerId, CustomerBeautyProfileDto profile) {
        // Optimistic update
        profileResult.setValue(NetworkResult.success(profile));
        
        // Persist to backend
        repository.updateBeautyProfile(customerId, profile, new MutableLiveData<NetworkResult<CustomerBeautyProfileDto>>() {
            @Override
            protected void onActive() {
                super.onActive();
                observeForever(result -> {
                    if (result != null) {
                        if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                            // Merge logic: ensure we don't lose data if server returns partial object
                            CustomerBeautyProfileDto merged = mergeProfiles(profile, result.data);
                            profileResult.postValue(NetworkResult.success(merged));
                        } else if (result.status == NetworkResult.Status.ERROR) {
                            // On error, we could potentially rollback, but keeping optimistic data is usually better for UX
                            // unless the error is critical.
                        }
                    }
                });
            }
        });
    }

    private CustomerBeautyProfileDto mergeProfiles(CustomerBeautyProfileDto local, CustomerBeautyProfileDto remote) {
        if (remote == null) return local;
        
        // If remote has substantive data, prefer it. Otherwise keep local.
        if (remote.getSkinType() == null && local.getSkinType() != null) {
            remote.setSkinType(local.getSkinType());
        }
        if ((remote.getSkinConcerns() == null || remote.getSkinConcerns().isEmpty()) && !local.getSkinConcerns().isEmpty()) {
            remote.setSkinConcerns(local.getSkinConcerns());
        }
        if (remote.getSensitivityLevel() == null && local.getSensitivityLevel() != null) {
            remote.setSensitivityLevel(local.getSensitivityLevel());
        }
        if (remote.getSkinColor() == null && local.getSkinColor() != null) {
            remote.setSkinColor(local.getSkinColor());
        }
        if (remote.getSkinUndertone() == null && local.getSkinUndertone() != null) {
            remote.setSkinUndertone(local.getSkinUndertone());
        }
        if (remote.getFoundationFinish() == null && local.getFoundationFinish() != null) {
            remote.setFoundationFinish(local.getFoundationFinish());
        }
        if ((remote.getLipstickColors() == null || remote.getLipstickColors().isEmpty()) && !local.getLipstickColors().isEmpty()) {
            remote.setLipstickColors(local.getLipstickColors());
        }
        if ((remote.getMakeupStyles() == null || remote.getMakeupStyles().isEmpty()) && !local.getMakeupStyles().isEmpty()) {
            remote.setMakeupStyles(local.getMakeupStyles());
        }
        if (remote.getBudget() == null && local.getBudget() != null) {
            remote.setBudget(local.getBudget());
        }
        if ((remote.getAvoidIngredients() == null || remote.getAvoidIngredients().isEmpty()) && !local.getAvoidIngredients().isEmpty()) {
            remote.setAvoidIngredients(local.getAvoidIngredients());
        }
        if (remote.getProfileCompletion() == 0 && local.getProfileCompletion() > 0) {
            remote.setProfileCompletion(local.getProfileCompletion());
        }
        
        return remote;
    }

    public void updateProfileLocally(CustomerBeautyProfileDto profile) {
        profileResult.setValue(NetworkResult.success(profile));
    }

    public void loadReferences() {
        repository.getBeautyReferences(referencesResult);
    }

    public void loadRecommendedProducts(String skinType, String budget) {
        String query = skinType != null ? skinType : "";
        
        productRepository.getProducts(query, null, null).observeForever(result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                List<Product> filteredProducts = filterByBudget(result.data, budget);
                recommendedProductsResult.postValue(NetworkResult.success(filteredProducts));
            } else {
                recommendedProductsResult.postValue(result);
            }
        });
    }

    private List<Product> filterByBudget(List<Product> products, String budget) {
        if (budget == null || budget.isEmpty()) return products;
        
        List<Product> filtered = new ArrayList<>();
        for (Product p : products) {
            double price = p.getPriceValue();
            boolean match = false;
            
            if ("Dưới 300K".equalsIgnoreCase(budget)) {
                match = price < 300000;
            } else if ("300K - 500K".equalsIgnoreCase(budget)) {
                match = price >= 300000 && price <= 500000;
            } else if ("500K +".equalsIgnoreCase(budget)) {
                match = price > 500000;
            } else {
                match = true; // Default if budget format is unknown
            }
            
            if (match) {
                filtered.add(p);
            }
        }
        
        // If no products match the exact budget, we could optionally relax the filter 
        // but to fix the user's specific complaint, we must stick to the filter.
        return filtered;
    }
}
