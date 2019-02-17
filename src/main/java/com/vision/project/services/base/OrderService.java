package com.vision.project.services.base;

import com.vision.project.models.Order;
import com.vision.project.models.UserRequest;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

public interface OrderService {

    Order findById(int id);

    List<Order> findAll();

    List<Order> findByReadyFalse();

    Order create(Order order);

    Order update(Order order);

    void findMoreRecent(UserRequest userRequest);

    @EventListener
    void loadMostRecentDate(ApplicationReadyEvent event);
}
