package com.vision.project.services;

import com.vision.project.models.*;
import com.vision.project.repositories.base.OrderRepository;
import com.vision.project.services.base.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private BlockingQueue<UserRequest> requests = new ArrayBlockingQueue<>(100);
    private volatile Date date = new Date();

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {

        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Order save(Order order){
        order = orderRepository.save(order);
        updateUserRequests(order);
        return order;
    }
    @Override
    public synchronized void updateUserRequests(Order order){
        for (UserRequest request: requests) {
            UserRequest userRequest = requests.poll();
            date = userRequest.getLastPolledOrderDate();

            if (userRequest.getLastPolledOrderDate().after(date)) {
                continue;
            }else if(userRequest.getLastPolledOrderDate().before(date)){
                orderRepository.findMoreRecent(userRequest.getLastPolledOrderDate());
                continue;
            }
            request.getDeferredResult().setResult(order);
        }
        date = order.getCreated();
    }

    @Override
    public BlockingQueue<UserRequest> getRequests() {
        return requests;
    }
    @Override
    public Order findById(int id) {
        return orderRepository.findById(id);
    }

}
