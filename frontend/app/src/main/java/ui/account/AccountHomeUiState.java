package ui.account;

import com.example.frontend.data.model.account.ProfileHubDto;

public class AccountHomeUiState {
    public final boolean isLoading;
    public final ProfileHubDto data;
    public final String errorMessage;

    public AccountHomeUiState(boolean isLoading, ProfileHubDto data, String errorMessage) {
        this.isLoading = isLoading;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static AccountHomeUiState loading() {
        return new AccountHomeUiState(true, null, null);
    }

    public static AccountHomeUiState success(ProfileHubDto data) {
        return new AccountHomeUiState(false, data, null);
    }

    public static AccountHomeUiState error(String message) {
        return new AccountHomeUiState(false, null, message);
    }
}
