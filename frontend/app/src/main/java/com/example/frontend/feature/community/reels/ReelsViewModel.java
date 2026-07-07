package com.example.frontend.feature.community.reels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.frontend.feature.community.reels.mock.MockReelsDataSource;

import java.util.List;

public class ReelsViewModel extends ViewModel {

    private final MutableLiveData<ReelsUiState> uiState = new MutableLiveData<>(new ReelsUiState(true, null, null));

    public LiveData<ReelsUiState> getUiState() {
        return uiState;
    }

    public void loadReels() {
        uiState.setValue(new ReelsUiState(true, null, null));
        
        // Simulate loading delay
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            List<MockReelsDataSource.MockReel> reels = MockReelsDataSource.getDemoReels();
            if (reels.isEmpty()) {
                uiState.setValue(new ReelsUiState(false, null, null));
            } else {
                uiState.setValue(new ReelsUiState(false, reels, null));
            }
        }, 500);
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
