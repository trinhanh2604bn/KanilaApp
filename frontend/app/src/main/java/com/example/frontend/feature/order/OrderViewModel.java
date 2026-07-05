package com.example.frontend.feature.order;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.order.OrderDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.OrderRepository;
import java.util.List;

public class OrderViewModel extends AndroidViewModel {
    private final OrderRepository repository;
    private final MutableLiveData<NetworkResult<List<OrderDto>>> ordersResult = new MutableLiveData<>();

    public OrderViewModel(@NonNull Application application) {
        super(application);
        this.repository = new OrderRepository(application);
    }

    public LiveData<NetworkResult<List<OrderDto>>> getOrdersResult() {
        return ordersResult;
    }

    public void loadMyOrders() {
        repository.getMyOrders(ordersResult);
    }
}
