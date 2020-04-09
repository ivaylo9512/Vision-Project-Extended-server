package com.vision.project.models.DTOs;

import com.vision.project.models.Menu;
import com.vision.project.models.Restaurant;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RestaurantDto {
    private int id;
    private String name;
    private String address;
    private String type;
    private Set<Menu> menu;
    private List<OrderDto> orders;


    RestaurantDto(Restaurant restaurant){
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.type = restaurant.getType();
        this.menu = restaurant.getMenu();
        this.orders = restaurant.getOrders().stream().map(OrderDto::new).collect(Collectors.toList());
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<Menu> getMenu() {
        return menu;
    }

    public void setMenu(Set<Menu> menu) {
        this.menu = menu;
    }

    public List<OrderDto> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDto> orders) {
        this.orders = orders;
    }
}
