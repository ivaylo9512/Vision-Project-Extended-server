package com.vision.project.models;

import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class UserRequest {
    private DeferredResult<List<Order>> deferredResult;
    private LocalDateTime lastPolledOrderDate;

    public UserRequest(DeferredResult<List<Order>> deferredResult, LocalDateTime lastPolledOrderDate) {
        this.deferredResult = deferredResult;
        this.lastPolledOrderDate = lastPolledOrderDate;
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
}
