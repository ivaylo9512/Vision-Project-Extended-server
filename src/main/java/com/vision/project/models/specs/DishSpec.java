package com.vision.project.models.specs;

public class DishSpec {
    private int id;

    private int orderId;

    public DishSpec() {
    }

    public DishSpec(int id, int orderId) {
        this.id = id;
        this.orderId = orderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
