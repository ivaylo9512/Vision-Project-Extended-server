package com.vision.project.services.base;

import com.vision.project.models.Order;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface OrderService {

    Order findById(int id);

    List<Order> findAll();

    void updateUserRequests(Order order);

    Order save(Order order);

    BlockingQueue<UserRequest> getRequests();
}
