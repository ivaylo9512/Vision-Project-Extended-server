package com.vision.project.models.DTOs;

import com.vision.project.models.Menu;
import com.vision.project.models.Order;
import com.vision.project.models.Restaurant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RestaurantDto {
    private int id;
    private String name;
    private String address;
    private String type;
    private Set<Menu> menu;
    private Map<Integer, OrderDto> orders;

    public RestaurantDto(Restaurant restaurant, Map<Integer, Order> orders){
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.type = restaurant.getType();
        this.menu = restaurant.getMenu();
        this.orders = orders.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, o -> new OrderDto(o.getValue()),
                        (existing, replacement) -> existing, LinkedHashMap::new));
    }

    public RestaurantDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.menu = restaurant.getMenu();
        this.address = restaurant.getAddress();
        this.type = restaurant.getType();
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

    public Map<Integer, OrderDto> getOrders() {
        return orders;
    }

    public void setOrders(Map<Integer, OrderDto> orders) {
        this.orders = orders;
    }
}
