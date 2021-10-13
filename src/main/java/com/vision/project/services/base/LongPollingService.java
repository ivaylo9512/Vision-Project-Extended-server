package com.vision.project.services.base;

import com.vision.project.models.Dish;
import com.vision.project.models.Message;
import com.vision.project.models.Order;
import com.vision.project.models.UserRequest;

public interface LongPollingService {
    void setAndAddRequest(UserRequest newRequest);

    void addRequest(UserRequest userRequest);

    void checkDishes(Dish dish, long restaurantId, long userId);

    void checkOrders(Order updatedOrder, long restaurantId, long userId);

    void checkMessages(Message message);
}
