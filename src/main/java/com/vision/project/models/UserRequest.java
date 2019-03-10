package com.vision.project.models;

import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class UserRequest {
    private DeferredResult<List<Order>> deferredResult;
    private LocalDateTime lastPolledOrderDate;
    private int restaurantId;

    public UserRequest(DeferredResult<List<Order>> deferredResult, LocalDateTime lastPolledOrderDate, int restaurantId) {
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

    public DeferredResult<List<Order>> getDeferredResult() {
        return deferredResult;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }
}
