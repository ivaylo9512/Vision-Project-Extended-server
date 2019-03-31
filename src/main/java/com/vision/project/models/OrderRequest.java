package com.vision.project.models;

import com.vision.project.models.DTOs.OrderDto;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class OrderRequest {
    private DeferredResult<List<OrderDto>> deferredResult;
    private LocalDateTime lastPolledOrderDate;
    private int restaurantId;

    public OrderRequest(DeferredResult<List<OrderDto>> deferredResult, LocalDateTime lastPolledOrderDate, int restaurantId) {
        this.deferredResult = deferredResult;
        this.lastPolledOrderDate = lastPolledOrderDate;
        this.restaurantId = restaurantId;
    }

    public LocalDateTime getLastPolledOrderDate() {
        return lastPolledOrderDate;
    }

    public void setLastPolledOrderDate(LocalDateTime lastPolledOrderDate) {
        this.lastPolledOrderDate = lastPolledOrderDate;
    }

    public DeferredResult<List<OrderDto>> getDeferredResult() {
        return deferredResult;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }
}
