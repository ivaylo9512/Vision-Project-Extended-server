package com.vision.project.services.base;

import com.vision.project.models.Order;
import com.vision.project.models.UserRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface OrderService {

    Order findById(int id);

    List<Order> findAll();

    void updateUserRequests(Order order);

    Order save(Order order);

    BlockingQueue<UserRequest> getRequests();
}
