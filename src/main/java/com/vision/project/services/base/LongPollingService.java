package com.vision.project.services.base;

import com.vision.project.models.Dish;
import com.vision.project.models.Message;
import com.vision.project.models.Order;
import com.vision.project.models.UserRequest;
import com.vision.project.models.specs.MessageSpec;

public interface LongPollingService {
    void setAndAddRequest(UserRequest newRequest);

    void addRequest(UserRequest userRequest);

    void checkDishes(Dish dish, int restaurantId, int userId);

    void checkOrders(Order updatedOrder, int restaurantId, int userId);

    Message addMessage(MessageSpec message);
}
