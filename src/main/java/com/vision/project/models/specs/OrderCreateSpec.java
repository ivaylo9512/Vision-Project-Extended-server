package com.vision.project.models.specs;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class OrderCreateSpec {
    @NotEmpty(message = "Dishes must have at least 1 item.")
    private List<String> dishes;

    @NotNull(message = "You must provide restaurant id.")
    private Long restaurantId;

    public OrderCreateSpec() {
    }

    public OrderCreateSpec(List<String> dishes, long restaurantId){
        this.dishes = dishes;
        this.restaurantId = restaurantId;
    }

    public List<String> getDishes() {
        return dishes;
    }

    public void setDishes(List<String> dishes) {
        this.dishes = dishes;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
