package com.vision.project.services.base;

import com.vision.project.models.Dish;
import com.vision.project.models.Order;
import com.vision.project.models.UserRequest;

public interface LongPollingService {
    void setAndAddRequest(UserRequest newRequest);

    void addRequest(UserRequest userRequest);

    Dish addDish(int orderId, int dishId, int userId, int restaurantId);

    Order addOrder(Order order, int restaurantId, int userId);
}
