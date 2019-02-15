package com.vision.project.services.base;

import com.vision.project.models.Order;
import com.vision.project.models.UserRequest;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface OrderService {

    Order findById(int id);

    List<Order> findAll();

    Order save(Order order);

    void findMoreRecent(UserRequest userRequest);

    BlockingQueue<UserRequest> getRequests();

    @EventListener
    void loadMostRecentDate(ApplicationReadyEvent event);
}
