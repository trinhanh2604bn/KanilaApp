package ui.order;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.returnrefund.ReturnDetailDto;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.remote.ApiClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReturnDetailViewModel extends AndroidViewModel {
    private final ApiService apiService;
    private final MutableLiveData<NetworkResult<ReturnDetailDto>> returnDetailResult = new MutableLiveData<>();

    public ReturnDetailViewModel(@NonNull Application application) {
        super(application);
        this.apiService = ApiClient.getClient(application).create(ApiService.class);
    }

    public LiveData<NetworkResult<ReturnDetailDto>> getReturnDetailResult() {
        return returnDetailResult;
    }

    public void loadReturnDetail(String orderId) {
        returnDetailResult.setValue(NetworkResult.loading());
        apiService.getReturnsByOrderId(orderId).enqueue(new Callback<ApiResponse<List<ReturnDetailDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ReturnDetailDto>>> call, Response<ApiResponse<List<ReturnDetailDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ReturnDetailDto> returns = response.body().getData();
                    if (returns != null && !returns.isEmpty()) {
                        // Normally we show the latest return request
                        returnDetailResult.setValue(NetworkResult.success(returns.get(0)));
                    } else {
                        returnDetailResult.setValue(NetworkResult.error("Không tìm thấy thông tin hoàn trả"));
                    }
                } else {
                    returnDetailResult.setValue(NetworkResult.error("Lỗi tải thông tin hoàn trả"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReturnDetailDto>>> call, Throwable t) {
                returnDetailResult.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
