package com.vision.project.models.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vision.project.models.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDto {
    private long id;
    private long userId;
    private List<DishDto> dishes;
    private LocalDateTime created;
    private LocalDateTime updated;
    private boolean ready;
    private long restaurantId;

    public OrderDto() {
    }

    public OrderDto(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.dishes = order.getDishes().stream().map(DishDto::new).collect(Collectors.toList());
        this.created = order.getCreated();
        this.updated = order.getUpdated();
        this.restaurantId = order.getRestaurant().getId();
        this.ready = order.isReady();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<DishDto> getDishes() {
        return dishes;
    }

    public void setDishes(List<DishDto> dishes) {
        this.dishes = dishes;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
