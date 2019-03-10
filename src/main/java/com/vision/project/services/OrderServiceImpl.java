package com.vision.project.services;

import com.vision.project.exceptions.NonExistingOrder;
import com.vision.project.models.*;
import com.vision.project.models.specs.DishSpec;
import com.vision.project.repositories.base.*;
import com.vision.project.services.base.OrderService;
import org.apache.tomcat.jni.Local;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private RestaurantRepository restaurantRepository;

    public BlockingQueue<UserRequest> requests = new ArrayBlockingQueue<>(100);
    private List<Order> orders = Collections.synchronizedList(new ArrayList<>());
    private volatile HashMap<Integer, LocalDateTime> mostRecentDates = new HashMap<>();

    public OrderServiceImpl(OrderRepository orderRepository, RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    @Override
    public Order create(Order order, int restaurantId){
        for (Dish dish:order.getDishes()) {
            dish.setOrder(order);
        }

        Restaurant restaurant = restaurantRepository.getOne(restaurantId);
        order.setRestaurant(restaurant);

        order = orderRepository.save(order);
        orders.add(order);
        updateUserRequests();

        return order;
    }

    @Transactional
    @Override
    public Order update(DishSpec dish) {
        Order order = orderRepository.findById(dish.getOrderId())
                .orElseThrow(() -> new NonExistingOrder("Order doesn't exist."));

        List<Dish> notReady = new ArrayList<>();
        boolean updated = false;
        for (Dish orderDish: order.getDishes()) {
            if(orderDish.getId() == dish.getId() && !orderDish.getReady()){
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
            orders.add(order);
            updateUserRequests();
        }
        return order;
    }

    @Override
    public void findMoreRecent(UserRequest userRequest) {
        List<Order> moreRecent = new ArrayList<>();

        if (userRequest.getLastPolledOrderDate().isBefore(mostRecentDates.get(userRequest.getRestaurantId()))) {
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
    private synchronized void updateUserRequests() {
        BlockingQueue<UserRequest> unmanagedRequests = new ArrayBlockingQueue<>(100);
        while (orders.size() > 0) {

            while (requests.size() > 0) {

                UserRequest userRequest = requests.poll();
                LocalDateTime userLastPolled = userRequest.getLastPolledOrderDate();

                if (userRequest.getLastPolledOrderDate().isBefore(mostRecentDates.get(userRequest.getRestaurantId()))) {
                    findMoreRecent(userRequest);
                    continue;
                }

                List<Order> moreRecentOrders = new ArrayList<>();
                for (Order order : orders) {
                    Optional<LocalDateTime> checkUpdated = Optional.ofNullable(order.getUpdated());
                    if (order.getRestaurant().getId() == userRequest.getRestaurantId() && (userLastPolled.isBefore(order.getCreated()) || (checkUpdated.isPresent() && userLastPolled.isBefore(checkUpdated.get())))) {
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
            updateMostRecentDates();
            orders = Collections.synchronizedList(new ArrayList<>());
        }
    }

    private void updateMostRecentDates(){
        orders.forEach(order -> {

            Optional<LocalDateTime> updated = Optional.ofNullable(order.getUpdated());
            LocalDateTime date = (updated.isPresent() && updated.get().isAfter(order.getCreated())
                    ? updated.get().withNano(0) : order.getCreated().withNano(0));
            mostRecentDates.replace(order.getRestaurant().getId(), date);

        });
    }

    @Override
    public void removeUserRequest(UserRequest request) {
        requests.remove(request);
    }

    @Override
    public void setDates(ApplicationReadyEvent event) throws RuntimeException {
        restaurantRepository.findAll().forEach(restaurant -> {
            restaurant.getMenu().forEach(menu -> System.out.println(menu.getName()));
            mostRecentDates.put(restaurant.getId(), getMostRecentDate(restaurant.getId()));
        });

        System.out.println(mostRecentDates.values());
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
