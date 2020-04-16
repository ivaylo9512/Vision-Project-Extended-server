package com.vision.project.models.DTOs;

import com.vision.project.models.Dish;
import com.vision.project.models.UserModel;

import java.time.LocalDateTime;

public class DishDto {
    private int id;
    private String name;
    private boolean ready;
    private LocalDateTime created;
    private LocalDateTime updated;
    private int updatedById;
    private int orderId;
    private boolean isOrderReady;

    public DishDto(Dish dish) {
        this.id = dish.getId();
        this.name = dish.getName();
        this.ready = dish.getReady();
        this.created = dish.getCreated();
        this.updated = dish.getUpdated();
        this.isOrderReady = dish.getOrder().isReady();
        this.orderId = dish.getOrder().getId();
        setUpdatedById(dish.getUpdatedBy());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
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

    public int getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(UserModel updatedBy) {
        if(updatedBy != null) {
            this.updatedById = updatedBy.getId();
        }
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public boolean isOrderReady() {
        return isOrderReady;
    }

    public void setOrderReady(boolean orderReady) {
        isOrderReady = orderReady;
    }
}
