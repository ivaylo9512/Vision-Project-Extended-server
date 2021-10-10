package com.vision.project.services;

import com.vision.project.exceptions.UnauthorizedException;
import com.vision.project.models.*;
import com.vision.project.models.specs.OrderCreateSpec;
import com.vision.project.repositories.base.*;
import com.vision.project.services.base.OrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order create(OrderCreateSpec orderSpec, Restaurant restaurant, UserModel loggedUser){
        return orderRepository.save(new Order(orderSpec, restaurant, loggedUser));
    }

    @Override
    public Dish update(int orderId, int dishId, Restaurant restaurant, UserModel loggedUser) {
        Order order = orderRepository.findByIdAndRestaurant(orderId, restaurant)
                .orElseThrow(() -> new EntityNotFoundException("Order doesn't exist."));

        order.setReady(true);
        boolean updated = false;

        Dish updatedDish = null;
        for (Dish orderDish: order.getDishes()) {
            if(orderDish.getId() == dishId ){
                if(!orderDish.getReady()) {
                    orderDish.setUpdatedBy(loggedUser);
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
    public Order findById(int id, UserModel loggedUser) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order doesn't exist."));

        if(order.getRestaurant().getId() != loggedUser.getRestaurant().getId() && !loggedUser.getRole().equals("ROLE_ADMIN")){
            throw new UnauthorizedException("Unauthorized.");
        }

        return order;
    }

    @Override
    public Map<Integer, Order> findNotReady(Restaurant restaurant, int page, int pageSize) {
        return orderRepository.findNotReady(restaurant, PageRequest.of(page, pageSize, Sort.Direction.DESC, "created")).stream()
                .collect(Collectors.toMap(Order::getId, order -> order, (existing, replacement) -> existing, LinkedHashMap::new));
    }

    @Override
    public List<Order> findMoreRecent(LocalDateTime lastCheck, Restaurant restaurant) {
        return orderRepository.findMoreRecent(lastCheck, restaurant);
    }

}
