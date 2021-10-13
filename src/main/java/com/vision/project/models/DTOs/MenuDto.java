package com.vision.project.models.DTOs;

import com.vision.project.models.Menu;

import java.util.Objects;

public class MenuDto {
    private long id;
    private String name;
    private long restaurantId;

    private MenuDto(){

    }

    public MenuDto(String name, long restaurantId) {
        this.name = name;
        this.restaurantId = restaurantId;
    }

    public MenuDto(Menu menu){
        this.id = menu.getId();
        this.name = menu.getName();
        this.restaurantId = menu.getRestaurant().getId();
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

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
