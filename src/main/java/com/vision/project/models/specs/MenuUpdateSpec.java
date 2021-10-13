package com.vision.project.models.specs;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MenuUpdateSpec {
    @NotNull(message = "You must provide id")
    private long id;

    @NotNull(message = "You must provide restaurant id")
    private long restaurantId;

    @NotBlank(message = "You must provide name")
    private String name;

    public MenuUpdateSpec() {
    }

    public MenuUpdateSpec(String name, long restaurantId) {
        this.name = name;
        this.restaurantId = restaurantId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
