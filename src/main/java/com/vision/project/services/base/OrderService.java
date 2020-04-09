package com.vision.project.services.base;

import com.vision.project.models.Dish;
import com.vision.project.models.Order;
import com.vision.project.models.OrderRequest;
import com.vision.project.models.Restaurant;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface OrderService {

    LocalDateTime getMostRecentDate(int restaurantId);

    Order findById(int id);

    List<Order> findAllNotReady(Restaurant restaurant);

    List<Order> findAll();

    Order create(Order order, int restaurantId, int userId);

    Dish update(int orderId, int dishId);

    void findMoreRecent(OrderRequest request);

    void removeUserRequest(OrderRequest request);

    void setDates();
}
