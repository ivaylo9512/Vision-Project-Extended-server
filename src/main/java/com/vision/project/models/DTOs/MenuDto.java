package com.vision.project.models.DTOs;

import java.util.Objects;

public class MenuDto {
    private String name;
    private int restaurantId;

    public MenuDto(String name, int restaurantId) {
        this.name = name;
        this.restaurantId = restaurantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuDto menuDto = (MenuDto) o;
        return restaurantId == menuDto.restaurantId && name.equals(menuDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, restaurantId);
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
