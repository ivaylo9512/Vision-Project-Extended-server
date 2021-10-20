package com.vision.project.models.DTOs;

import com.vision.project.models.Order;
import com.vision.project.models.UserRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserRequestDto {
    private Map<Long, OrderDto> orders;
    private List<MessageDto> messages;
    private List<DishDto> dishes;
    private Long userId;
    private long restaurantId;
    private LocalDateTime lastCheck;

    public UserRequestDto(UserRequest currentRequest){
        this.orders = currentRequest.getOrders().stream()
                .collect(Collectors.toMap(Order::getId, OrderDto::new, (existing, replacement) -> existing, LinkedHashMap::new));
        this.messages = currentRequest.getMessages().stream().map(MessageDto::new).collect(Collectors.toList());
        this.dishes = currentRequest.getDishes().stream().map(DishDto::new).collect(Collectors.toList());
        this.userId = currentRequest.getUserId();
        this.restaurantId = currentRequest.getRestaurant().getId();
        this.lastCheck = currentRequest.getLastCheck();
    }

    public Map<Long, OrderDto> getOrders() {
        return orders;
    }

    public List<MessageDto> getMessages() {
        return messages;
    }

    public List<DishDto> getDishes() {
        return dishes;
    }

    public Long getUserId() {
        return userId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public LocalDateTime getLastCheck() {
        return lastCheck;
    }
}
