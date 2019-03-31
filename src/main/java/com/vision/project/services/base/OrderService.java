package com.vision.project.services.base;

import com.vision.project.models.Order;
import com.vision.project.models.OrderRequest;
import com.vision.project.models.UserDetails;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    LocalDateTime getMostRecentDate(int restaurantId);

    Order findById(int id);

    List<Order> findAllNotReady();

    List<Order> findAll();

    Order create(Order order, int restaurantId, int userId);

    Order update(int orderId, int dishId);

    void findMoreRecent(OrderRequest request);

    void removeUserRequest(OrderRequest request);

    @EventListener
    void setDates(ApplicationReadyEvent event);
}
