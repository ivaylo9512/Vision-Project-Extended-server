package com.vision.project.services.base;

import com.vision.project.models.Dish;
import com.vision.project.models.Order;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.OrderCreateSpec;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderService {
    Order findById(int id, UserModel loggedUser);

    Map<Integer, Order> findNotReady(Restaurant restaurant, int page, int pageSize);

    Order create(OrderCreateSpec order, Restaurant restaurant, UserModel loggedUser);

    Dish update(int orderId, int dishId, Restaurant restaurant, UserModel loggedUser);

    List<Order> findMoreRecent(LocalDateTime lastCheck, Restaurant restaurant);
}
