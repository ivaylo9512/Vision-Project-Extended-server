package com.vision.project.models;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.Date;
import java.util.List;

public class UserRequest {
    private DeferredResult<List<Order>> deferredResult;
    private Date lastPolledOrderDate;

    public UserRequest(DeferredResult<List<Order>> deferredResult, Date lastPolledOrderDate) {
        this.deferredResult = deferredResult;
        this.lastPolledOrderDate = lastPolledOrderDate;
    }

    public Date getLastPolledOrderDate() {
        return lastPolledOrderDate;
    }

    public DeferredResult<List<Order>> getDeferredResult() {
        return deferredResult;
    }
}
