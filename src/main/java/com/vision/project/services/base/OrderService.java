package com.vision.project.services.base;

import com.vision.project.models.Dish;
import com.vision.project.models.Order;
import com.vision.project.models.Restaurant;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    Order findById(int id);

    List<Order> findAllNotReady(Restaurant restaurant, Pageable pageable);

    List<Order> findAll();

    Order create(Order order, int restaurantId, int userId);

    Dish update(int orderId, int dishId, int userId);

    List<Order> findMoreRecent(LocalDateTime lastCheck, int restaurantId);
}
