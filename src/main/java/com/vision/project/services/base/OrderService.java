package com.vision.project.services.base;

import com.vision.project.models.Dish;
import com.vision.project.models.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderService {
    Order findById(int id);

    Map<Integer, Order> findNotReady(int restaurantId, int page, int pageSize);

    List<Order> findAllNotReady(int restaurantId);

    List<Order> findAll();

    Order create(Order order, int restaurantId, int userId);

    Dish update(int orderId, int dishId, int userId);

    List<Order> findMoreRecent(LocalDateTime lastCheck, int restaurantId);
}
