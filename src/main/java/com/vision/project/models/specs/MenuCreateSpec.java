package com.vision.project.models.specs;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MenuCreateSpec {
    @NotNull(message = "You must provide restaurant id.")
    private Long restaurantId;

    @NotBlank(message = "You must provide name.")
    private String name;

    public MenuCreateSpec() {
    }

    public MenuCreateSpec(String name, long restaurantId) {
        this.name = name;
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
