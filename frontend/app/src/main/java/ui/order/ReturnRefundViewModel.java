package ui.order;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.returnrefund.ReturnRefundRequestDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.OrderRepository;
import java.util.List;

public class ReturnRefundViewModel extends AndroidViewModel {
    private final OrderRepository repository;
    private final MutableLiveData<NetworkResult<Object>> submitResult = new MutableLiveData<>();

    public ReturnRefundViewModel(@NonNull Application application) {
        super(application);
        this.repository = new OrderRepository(application);
    }

    public LiveData<NetworkResult<Object>> getSubmitResult() {
        return submitResult;
    }

    public void submitReturnRefund(String orderId, String orderItemId, String reason, String description,
                                  List<ReturnRefundRequestDto.EvidenceMedia> media, String shippingMethod,
                                  String refundMethod, String refundAccountId) {
        
        ReturnRefundRequestDto request = new ReturnRefundRequestDto();
        request.setOrderId(orderId);
        request.setOrderItemId(orderItemId);
        request.setReason(reason);
        request.setDescription(description);
        request.setEvidenceMedia(media);
        request.setReturnShippingMethod(shippingMethod);
        request.setRefundMethod(refundMethod);
        request.setRefundAccountId(refundAccountId);

        repository.submitReturnRefund(orderId, request, submitResult);
    }
}
