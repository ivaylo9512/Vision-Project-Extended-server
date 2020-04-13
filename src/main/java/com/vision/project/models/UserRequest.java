package com.vision.project.models;

import com.vision.project.models.DTOs.UserRequestDto;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class UserRequest {
    private List<Order> orders = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    private List<Dish> dishes = new ArrayList<>();
    private DeferredResult<UserRequestDto> request = new DeferredResult<>();
    private ReentrantLock lock = new ReentrantLock();
    private int userId;
    private int restaurantId;
    private LocalDateTime lastCheck;

    public UserRequest(){

    }

    public UserRequest(DeferredResult request){
        this.request = request;
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

    public DeferredResult<UserRequestDto> getRequest() {
        return request;
    }

    public void setRequest(DeferredResult<UserRequestDto> request) {
        this.request = request;
    }

    public ReentrantLock getLock() {
        return lock;
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
