package com.vision.project.services.base;

import com.vision.project.models.Dish;
import com.vision.project.models.Order;
import com.vision.project.models.UserRequest;
import com.vision.project.models.specs.DishSpec;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    LocalDateTime getMostRecentDate(int restaurantId);

    Order findById(int id);

    List<Order> findAllNotReady();

    List<Order> findAll();

    Order create(Order order, int restaurantId);

    Order update(DishSpec dish);

    void findMoreRecent(UserRequest request);

    void removeUserRequest(UserRequest request);

    @EventListener
    void setDates(ApplicationReadyEvent event);
}
