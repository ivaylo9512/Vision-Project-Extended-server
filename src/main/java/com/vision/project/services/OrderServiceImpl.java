package com.vision.project.services;

import com.vision.project.models.*;
import com.vision.project.repositories.base.OrderRepository;
import com.vision.project.services.base.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
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
    private List<Order> orders = Collections.synchronizedList(new ArrayList<>());
    private volatile Date date;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order save(Order order){
        order = orderRepository.save(order);
        orders.add(order);
        updateUserRequests();
        return order;
    }

    @Override
    public void findMoreRecent(UserRequest userRequest){
        List<Order> moreRecent = orderRepository.findMoreRecent(userRequest.getLastPolledOrderDate());
        if(moreRecent.size() > 0){
            userRequest.getDeferredResult().setResult(moreRecent);
        }
    }

    public synchronized void updateUserRequests(){
        BlockingQueue<UserRequest> unmanagedRequests = new ArrayBlockingQueue<>(100);
        while (requests.size() > 0){
            UserRequest userRequest = requests.poll();
            Date userLastPolled = userRequest.getLastPolledOrderDate();

            if(userRequest.getLastPolledOrderDate().before(date)){
                findMoreRecent(userRequest);
                continue;
            }
            List<Order> moreRecentOrders = new ArrayList<>();
            for (Order order: orders) {
                Optional<Date> checkNull = Optional.ofNullable(order.getUpdated());
                if (userLastPolled.before(order.getCreated()) || (checkNull.isPresent() && userLastPolled.before(checkNull.get()))) {
                    moreRecentOrders.add(order);
                }
            }
            if(moreRecentOrders.size() > 0){
                userRequest.getDeferredResult().setResult(moreRecentOrders);
            }else {
                unmanagedRequests.add(userRequest);
            }
        }
        requests.addAll(unmanagedRequests);
        date = orders.get(orders.size() - 1).getCreated();
        orders = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void loadMostRecentDate(ApplicationReadyEvent event) {
        date = orderRepository.findTop1ByOrderByCreatedDescUpdatedDesc().get(0).getCreated();
        System.out.println(date);
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
