package com.vision.project.services;

import com.vision.project.exceptions.NonExistingOrder;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.OrderDto;
import com.vision.project.repositories.base.*;
import com.vision.project.services.base.OrderService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private RestaurantRepository restaurantRepository;
    private UserRepository userRepository;

    private BlockingQueue<OrderRequest> requests = new ArrayBlockingQueue<>(100);
    private List<OrderDto> orders = Collections.synchronizedList(new ArrayList<>());
    private volatile HashMap<Integer, LocalDateTime> mostRecentDates = new HashMap<>();

    public OrderServiceImpl(OrderRepository orderRepository, RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Order create(Order order, int restaurantId, int userId){
        for (Dish dish : order.getDishes()) {
            dish.setOrder(order);
        }

        Restaurant restaurant = restaurantRepository.getOne(restaurantId);
        order.setRestaurant(restaurant);

        order.setUser(userRepository.getOne(userId));
        order = orderRepository.save(order);
        orders.add(new OrderDto(order));

        updateUserRequests();

        return order;
    }

    @Transactional
    @Override
    public Order update(int orderId, int dishId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NonExistingOrder("Order doesn't exist."));

        List<Dish> notReady = new ArrayList<>();
        boolean updated = false;
        for (Dish orderDish: order.getDishes()) {
            if(orderDish.getId() == dishId && !orderDish.getReady()){
                orderDish.setReady(true);
                order.setUpdated(LocalDateTime.now());
                updated = true;
            }
            if(!orderDish.getReady()){
                notReady.add(orderDish);
            }
        }

        if(notReady.size() == 0){
            order.setReady(true);
        }
        if(updated) {
            order = orderRepository.save(order);
            orders.add(new OrderDto(order));
            updateUserRequests();
        }
        return order;
    }

    @Override
    public void findMoreRecent(OrderRequest orderRequest) {
        if (orderRequest.getLastPolledOrderDate().isBefore(mostRecentDates.get(orderRequest.getRestaurantId()))) {
            Restaurant restaurant = restaurantRepository.getOne(orderRequest.getRestaurantId());
            orderRequest.getDeferredResult().setResult(orderRepository
                    .findMoreRecent(orderRequest.getLastPolledOrderDate(), restaurant)
                    .stream()
                    .map(OrderDto::new)
                    .collect(Collectors.toList()));
            return;
        }


        CompletableFuture.runAsync(()->{
            try {
                requests.add(orderRequest);
            }catch (Exception ex){
                throw new RuntimeException(ex.getMessage());
            }
        });
    }
    private synchronized void updateUserRequests() {

        BlockingQueue<OrderRequest> unmanagedRequests = new ArrayBlockingQueue<>(100);
        while (orders.size() > 0) {

            while (requests.size() > 0) {

                OrderRequest orderRequest = requests.poll();
                LocalDateTime userLastPolled = orderRequest.getLastPolledOrderDate();

                if (orderRequest.getLastPolledOrderDate().isBefore(mostRecentDates.get(orderRequest.getRestaurantId()))) {
                    Restaurant restaurant = restaurantRepository.getOne(orderRequest.getRestaurantId());
                    orderRequest.getDeferredResult().setResult(orderRepository
                            .findMoreRecent(orderRequest.getLastPolledOrderDate(), restaurant)
                            .stream()
                            .map(OrderDto::new)
                            .collect(Collectors.toList()));
                    continue;
                }

                List<OrderDto> moreRecentOrders = new ArrayList<>();
                for (OrderDto order : orders) {
                    Optional<LocalDateTime> checkUpdated = Optional.ofNullable(order.getUpdated());
                    if (order.getRestaurantId() == orderRequest.getRestaurantId() && (userLastPolled.isBefore(order.getCreated()) || (checkUpdated.isPresent() && userLastPolled.isBefore(checkUpdated.get())))) {
                        moreRecentOrders.add(order);
                    }
                }

                if (moreRecentOrders.size() > 0) {
                    orderRequest.getDeferredResult().setResult(moreRecentOrders);
                } else {
                    unmanagedRequests.add(orderRequest);
                }

            }

            requests.addAll(unmanagedRequests);
            updateMostRecentDates();
            orders = Collections.synchronizedList(new ArrayList<>());
        }
    }

    private void updateMostRecentDates(){
        orders.forEach(order -> {

            Optional<LocalDateTime> updated = Optional.ofNullable(order.getUpdated());
            LocalDateTime date = (updated.isPresent() && updated.get().isAfter(order.getCreated())
                    ? updated.get().withNano(0) : order.getCreated().withNano(0));
            mostRecentDates.replace(order.getRestaurantId(), date);

        });
    }

    @Override
    public void removeUserRequest(OrderRequest request) {
        requests.remove(request);
    }

    @Override
    public void setDates() throws RuntimeException {
        restaurantRepository.findAll().forEach(restaurant ->
                mostRecentDates.put(restaurant.getId(), getMostRecentDate(restaurant.getId())));
    }

    @Override
    public LocalDateTime getMostRecentDate(int restaurantId){
        Restaurant restaurant = restaurantRepository.getOne(restaurantId);
        Order order = orderRepository.findMostRecentDate(restaurant, PageRequest.of(0,1)).get(0);

        LocalDateTime localDateTime;
        try {
            Optional<LocalDateTime> updated = Optional.ofNullable(order.getUpdated());

            localDateTime = (updated.isPresent() && updated.get().isAfter(order.getCreated())
                    ? updated.get() : order.getCreated());
        }catch (NullPointerException e){
            localDateTime = LocalDateTime.now();
            System.out.println("No orders in the database");
        }
        return localDateTime;
    }
    @Override
    public Order findById(int id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NonExistingOrder("Order doesn't exist."));
    }

    @Override
    public List<Order> findAllNotReady() {
        return orderRepository.findByReadyFalse();
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

}
