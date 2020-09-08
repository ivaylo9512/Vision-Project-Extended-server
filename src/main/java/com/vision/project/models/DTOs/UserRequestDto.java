package com.vision.project.models.DTOs;

import com.vision.project.models.Order;
import com.vision.project.models.UserRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserRequestDto {
    private Map<Integer, OrderDto> orders;
    private List<MessageDto> messages;
    private List<DishDto> dishes;
    private int userId;
    private int restaurantId;
    private LocalDateTime lastCheck;

    public UserRequestDto(UserRequest currentRequest){
        this.orders = currentRequest.getOrders().stream()
                .collect(Collectors.toMap(Order::getId, OrderDto::new, (existing, replacement) -> existing, LinkedHashMap::new));
        this.messages = currentRequest.getMessages().stream().map(MessageDto::new).collect(Collectors.toList());
        this.dishes = currentRequest.getDishes().stream().map(DishDto::new).collect(Collectors.toList());
        this.userId = currentRequest.getUserId();
        this.restaurantId = currentRequest.getRestaurantId();
        this.lastCheck = currentRequest.getLastCheck();
    }

    public Map<Integer, OrderDto> getOrders() {
        return orders;
    }

    public void setOrders(Map<Integer, OrderDto> orders) {
        this.orders = orders;
    }

    public List<MessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }

    public List<DishDto> getDishes() {
        return dishes;
    }

    public void setDishes(List<DishDto> dishes) {
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
