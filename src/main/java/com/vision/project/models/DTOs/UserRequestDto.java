package com.vision.project.models.DTOs;

import com.vision.project.models.Dish;
import com.vision.project.models.Message;
import com.vision.project.models.Order;
import com.vision.project.models.UserRequest;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class UserRequestDto {
    private List<Order> orders = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    private List<Dish> dishes = new ArrayList<>();
    private int userId;
    private int restaurantId;
    private LocalDateTime lastCheck;

    public UserRequestDto(UserRequest currentRequest){
        this.orders = currentRequest.getOrders();
        this.messages = currentRequest.getMessages();
        this.dishes = currentRequest.getDishes();
        this.userId = currentRequest.getUserId();
        this.restaurantId = currentRequest.getRestaurantId();
        this.lastCheck = currentRequest.getLastCheck();
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public LocalDateTime getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(LocalDateTime lastCheck) {
        this.lastCheck = lastCheck;
    }
}
