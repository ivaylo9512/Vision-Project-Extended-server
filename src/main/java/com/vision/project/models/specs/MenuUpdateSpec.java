package com.vision.project.models.specs;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MenuUpdateSpec {
    @NotNull(message = "You must provide id")
    private int id;

    @NotNull(message = "You must provide restaurant id")
    private int restaurantId;

    @NotBlank(message = "You must provide name")
    private String name;

    public MenuUpdateSpec() {
    }

    public MenuUpdateSpec(String name, int restaurantId) {
        this.name = name;
        this.restaurantId = restaurantId;
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

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }
}
