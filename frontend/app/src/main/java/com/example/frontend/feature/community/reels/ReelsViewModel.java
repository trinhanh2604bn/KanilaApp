package com.example.frontend.feature.community.reels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.feature.community.reels.mock.MockReelsDataSource;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReelsViewModel extends AndroidViewModel {

    private final ProductRepository productRepository;
    private final MutableLiveData<ReelsUiState> uiState = new MutableLiveData<>(new ReelsUiState(true, null, null));

    public ReelsViewModel(@NonNull Application application) {
        super(application);
        this.productRepository = new ProductRepository(application);
    }

    public LiveData<ReelsUiState> getUiState() {
        return uiState;
    }

    public void loadReels() {
        // Step 1: SHOW MOCK IMMEDIATELY
        List<MockReelsDataSource.MockReel> initialReels = MockReelsDataSource.getDemoReels();
        uiState.setValue(new ReelsUiState(false, initialReels, null));
        
        // Step 2: LOAD DB PRODUCTS IN BACKGROUND
        productRepository.getProducts(null, null, null).observeForever(result -> {
            if (result == null || result.status != NetworkResult.Status.SUCCESS || result.data == null || result.data.isEmpty()) {
                return;
            }

            List<Product> dbProducts = result.data;
            int productIndex = 0;
            List<MockReelsDataSource.MockReel> updatedReels = new ArrayList<>();
            
            for (int j = 0; j < initialReels.size(); j++) {
                MockReelsDataSource.MockReel reel = initialReels.get(j);
                List<Product> reelProducts = new ArrayList<>();
                
                int count;
                switch (j) {
                    case 0: count = 2; break; 
                    case 1: count = 3; break; 
                    case 2: count = 3; break; 
                    case 3: count = 1; break; 
                    case 4: count = 2; break; 
                    default: count = 2; break;
                }

                for (int i = 0; i < count; i++) {
                    if (productIndex >= dbProducts.size()) productIndex = 0;
                    reelProducts.add(dbProducts.get(productIndex++));
                }
                
                updatedReels.add(new MockReelsDataSource.MockReel(
                        reel.getId(),
                        reel.getVideoUrl(),
                        reel.getThumbnailUrl(),
                        reel.getCreatorName(),
                        reel.getCreatorUsername(),
                        reel.getCreatorAvatarUrl(),
                        reel.getCaption(),
                        reel.getHashtags(),
                        reel.getAudioName(),
                        reel.getLikeCountText(),
                        reel.getCommentCountText(),
                        reel.getSaveCountText(),
                        reelProducts
                ));
            }
            
            // Step 3: Use a small delay before updating to ensure VideoView has stabilized
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                uiState.setValue(new ReelsUiState(false, updatedReels, null));
            }, 1000);
        });
    }

    public List<Product> getProductsByReelId(String reelId) {
        ReelsUiState state = uiState.getValue();
        if (state != null && state.reels != null) {
            for (MockReelsDataSource.MockReel reel : state.reels) {
                if (reel.getId().equals(reelId)) {
                    return reel.getProducts();
                }
            }
        }
        return Collections.emptyList();
    }

    public static class ReelsUiState {
        public final boolean loading;
        public final List<MockReelsDataSource.MockReel> reels;
        public final String error;

        public ReelsUiState(boolean loading, List<MockReelsDataSource.MockReel> reels, String error) {
            this.loading = loading;
            this.reels = reels;
            this.error = error;
        }
    }
}
