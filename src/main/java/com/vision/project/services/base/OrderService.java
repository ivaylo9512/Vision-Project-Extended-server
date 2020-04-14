package com.vision.project.services.base;

import com.vision.project.models.Dish;
import com.vision.project.models.Order;
import com.vision.project.models.Restaurant;

import java.util.List;

public interface OrderService {
    Order findById(int id);

    List<Order> findAllNotReady(Restaurant restaurant);

    List<Order> findAll();

    Order create(Order order, int restaurantId, int userId);

    Dish update(int orderId, int dishId, int userId);
}
