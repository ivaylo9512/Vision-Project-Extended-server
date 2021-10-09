package com.vision.project.services;

import com.vision.project.models.*;
import com.vision.project.repositories.base.*;
import com.vision.project.services.base.OrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;


    public OrderServiceImpl(OrderRepository orderRepository, RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Order create(Order order, int restaurantId, int userId){
        if(order.getDishes() == null || order.getDishes().size() == 0){
            throw new IllegalArgumentException("Order must have at least one dish");
        }

        for (Dish dish : order.getDishes()) {
            dish.setOrder(order);
        }

        Restaurant restaurant = restaurantRepository.getById(restaurantId);
        order.setRestaurant(restaurant);

        order.setUser(userRepository.getById(userId));
        order = orderRepository.save(order);

        return order;
    }

    @Transactional
    @Override
    public Dish update(int orderId, int dishId, int userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order doesn't exist."));

        order.setReady(true);
        boolean updated = false;

        Dish updatedDish = null;
        for (Dish orderDish: order.getDishes()) {
            if(orderDish.getId() == dishId ){
                if(!orderDish.getReady()) {
                    orderDish.setUpdatedBy(userRepository.getById(userId));
                    orderDish.setReady(true);

                    updated = true;
                }
                updatedDish = orderDish;
            }
            if(!orderDish.getReady()){
                order.setReady(false);
            }
        }

        if(updated || order.isReady()) {
            orderRepository.save(order);
        }

        return updatedDish;
    }


    @Override
    public Order findById(int id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order doesn't exist."));
    }

    @Override
    public Map<Integer, Order> findNotReady(int restaurantId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "created");

        return orderRepository.findNotReady(restaurantRepository.getById(restaurantId), pageable).stream()
                .collect(Collectors.toMap(Order::getId, order -> order, (existing, replacement) -> existing, LinkedHashMap::new));
    }

    @Override
    public List<Order> findMoreRecent(LocalDateTime lastCheck, int restaurantId) {
        Restaurant restaurant = restaurantRepository.getById(restaurantId);
        return orderRepository.findMoreRecent(lastCheck, restaurant);
    }

}
