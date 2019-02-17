package com.vision.project.services;

import com.vision.project.models.*;
import com.vision.project.repositories.base.DishRepository;
import com.vision.project.repositories.base.OrderRepository;
import com.vision.project.services.base.OrderService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private DishRepository dishRepository;
    private BlockingQueue<UserRequest> requests = new ArrayBlockingQueue<>(100);
    private List<Order> orders = Collections.synchronizedList(new ArrayList<>());
    private volatile Date date;
    Order order1 = new Order();
    public OrderServiceImpl(OrderRepository orderRepository, DishRepository dishRepository) {
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional
    @Override
    public Order create(Order order){
        for (Dish dish:order.getDishes()) {
            dish.setOrder(order);
        }
        order = orderRepository.save(order);
        orders.add(order);
        updateUserRequests();
        return order;
    }

    @Transactional
    @Override
    public Order update(Order order) {
        for (Dish dish:order.getDishes()) {
            dish.setOrder(order);
        }
        order = orderRepository.save(order);
        orders.add(order);
        updateUserRequests();
        return order;
    }

    @Override
    public void findMoreRecent(UserRequest userRequest) {
        List<Order> moreRecent = new ArrayList<>();
        if (userRequest.getLastPolledOrderDate().before(date)) {
            moreRecent = orderRepository.findMoreRecent(userRequest.getLastPolledOrderDate());
        }
        if(moreRecent.size() > 0){
            userRequest.getDeferredResult().setResult(moreRecent);
            return;
        }
        CompletableFuture.runAsync(()->{
            try {
                requests.add(userRequest);
            }catch (Exception ex){
                throw new RuntimeException(ex.getMessage());
            }
        });
    }

    public synchronized void updateUserRequests() {
        BlockingQueue<UserRequest> unmanagedRequests = new ArrayBlockingQueue<>(100);
        while (orders.size() > 0) {

            while (requests.size() > 0) {

                UserRequest userRequest = requests.poll();
                Date userLastPolled = userRequest.getLastPolledOrderDate();

                if (userRequest.getLastPolledOrderDate().before(date)) {
                    findMoreRecent(userRequest);
                    continue;
                }

                List<Order> moreRecentOrders = new ArrayList<>();
                for (Order order : orders) {
                    Optional<Date> checkUpdated = Optional.ofNullable(order.getUpdated());
                    if (userLastPolled.before(order.getCreated()) || (checkUpdated.isPresent() && userLastPolled.before(checkUpdated.get()))) {
                        moreRecentOrders.add(order);
                    }
                }

                if (moreRecentOrders.size() > 0) {
                    userRequest.getDeferredResult().setResult(moreRecentOrders);
                } else {
                    unmanagedRequests.add(userRequest);
                }

            }

            requests.addAll(unmanagedRequests);
            Order mostRecent = orders.get(orders.size() - 1);

            Optional<Date> updated = Optional.ofNullable(mostRecent.getUpdated());
            date = (updated.isPresent() && updated.get().after(mostRecent.getCreated())
                    ? updated.get() : mostRecent.getCreated());

            orders = Collections.synchronizedList(new ArrayList<>());
        }
    }
    @Override
    public void loadMostRecentDate(ApplicationReadyEvent event) throws RuntimeException {
        Order order = orderRepository.findTop1ByOrderByCreatedDescUpdatedDesc();

        try {
            Optional<Date> updated = Optional.ofNullable(order.getUpdated());

            date = (updated.isPresent() && updated.get().after(order.getCreated())
                    ? updated.get() : order.getCreated());
        }catch (NullPointerException e){
            date = new Date();
            System.out.println("No orders in the database at application start.");
        }
    }

    @Override
    public Order findById(int id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findByReadyFalse() {
        return orderRepository.findByReadyFalse();
    }
}
