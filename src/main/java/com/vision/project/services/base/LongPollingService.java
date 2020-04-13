package com.vision.project.services.base;

import com.vision.project.models.UserRequest;

public interface LongPollingService {
    void setOrAddRequest(UserRequest newRequest);
}
