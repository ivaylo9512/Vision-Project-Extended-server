package com.vision.project.models.DTOs;

import com.vision.project.models.Menu;
import com.vision.project.models.Order;
import com.vision.project.models.Restaurant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RestaurantDto {
    private int id;
    private String name;
    private String address;
    private String type;
    private Set<MenuDto> menu;
    private Map<Integer, OrderDto> orders = new HashMap<>();

    public RestaurantDto() {
    }

    public RestaurantDto(Restaurant restaurant, Map<Integer, Order> orders){
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.type = restaurant.getType();
        this.orders = orders.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, o -> new OrderDto(o.getValue()),
                        (existing, replacement) -> existing, LinkedHashMap::new));
        toMenuDto(restaurant.getMenu(), restaurant.getId());
    }

    public RestaurantDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.type = restaurant.getType();
        toMenuDto(restaurant.getMenu(), restaurant.getId());
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

    public Set<MenuDto> getMenu() {
        return menu;
    }

    public void setMenu(Set<MenuDto> menu) {
        this.menu = menu;
    }

    public Map<Integer, OrderDto> getOrders() {
        return orders;
    }

    public void setOrders(Map<Integer, OrderDto> orders) {
        this.orders = orders;
    }


    private void toMenuDto(Set<Menu> menu, int restaurantId) {
        if(menu != null){
            this.menu = menu.stream().map(m -> new MenuDto(m.getName(), restaurantId)).collect(Collectors.toSet());
        }
    }
}
