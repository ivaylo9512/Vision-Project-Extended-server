package com.vision.project.models;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.Date;

public class UserRequest {
    private DeferredResult<Order> deferredResult;
    private Date lastPolledOrderDate;

    public UserRequest(DeferredResult<Order> deferredResult, Date lastPolledOrderDate) {
        this.deferredResult = deferredResult;
        this.lastPolledOrderDate = lastPolledOrderDate;
    }

    public Date getLastPolledOrderDate() {
        return lastPolledOrderDate;
    }

    public void setLastPolledOrderDate(Date lastPolledOrderDate) {
        this.lastPolledOrderDate = lastPolledOrderDate;
    }

    public DeferredResult<Order> getDeferredResult() {
        return deferredResult;
    }
}
