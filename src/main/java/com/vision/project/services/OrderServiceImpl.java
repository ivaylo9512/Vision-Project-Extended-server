package com.vision.project.services;

import com.vision.project.models.*;
import com.vision.project.repositories.base.OrderRepository;
import com.vision.project.services.base.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        while (requests.size() > 0){
            UserRequest userRequest = requests.poll();
            date = userRequest.getLastPolledOrderDate();

            if (userRequest.getLastPolledOrderDate().after(date)) {
                continue;
            }else if(userRequest.getLastPolledOrderDate().before(date)){
                orderRepository.findMoreRecent(userRequest.getLastPolledOrderDate());
                continue;
            }
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sixMinutesBehind = LocalDateTime.now();

            Duration duration = Duration.between(now, sixMinutesBehind);
            long diff = Math.abs(duration.toMinutes());
            while (diff < 15){
                if(diff == 14){
                    now = LocalDateTime.now();
                    System.out.println(requests.size());
                    userRequest.getDeferredResult().setResult(order);
                    System.out.println(requests.size());
                }
                diff = Math.abs(duration.toMinutes());
                sixMinutesBehind = LocalDateTime.now();
                duration = Duration.between(now, sixMinutesBehind);

            }
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
