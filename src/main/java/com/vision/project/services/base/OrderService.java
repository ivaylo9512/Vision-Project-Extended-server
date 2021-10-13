package com.vision.project.services.base;

import com.vision.project.models.Dish;
import com.vision.project.models.Order;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderService {
    Order findById(long id, UserModel loggedUser);

    Map<Long, Order> findNotReady(Restaurant restaurant, int page, int pageSize);

    Order create(Order order);

    Dish update(long orderId, long dishId, Restaurant restaurant, UserModel loggedUser);

    List<Order> findMoreRecent(LocalDateTime lastCheck, Restaurant restaurant);
}
