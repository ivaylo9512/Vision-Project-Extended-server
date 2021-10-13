package com.vision.project.models.DTOs;

import com.vision.project.models.Menu;
import com.vision.project.models.Order;
import com.vision.project.models.Restaurant;
import java.util.*;
import java.util.stream.Collectors;

public class RestaurantDto {
    private long id;
    private String name;
    private String address;
    private String type;
    private List<MenuDto> menu;
    private Map<Long, OrderDto> orders = new HashMap<>();

    public RestaurantDto() {
    }

    public RestaurantDto(long id, String name, String address, String type, List<Menu> menu) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.type = type;
        toMenuDto(menu);
    }

    public RestaurantDto(Restaurant restaurant, Map<Long, Order> orders){
        this(restaurant.getId(), restaurant.getName(), restaurant.getAddress(), restaurant.getType(), restaurant.getMenu());
        this.orders = orders.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, o -> new OrderDto(o.getValue()),
                        (existing, replacement) -> existing, LinkedHashMap::new));
    }

    public RestaurantDto(Restaurant restaurant) {
        this(restaurant.getId(), restaurant.getName(), restaurant.getAddress(), restaurant.getType(), restaurant.getMenu());
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

    public List<MenuDto> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuDto> menu) {
        this.menu = menu;
    }

    public Map<Long, OrderDto> getOrders() {
        return orders;
    }

    public void setOrders(Map<Long, OrderDto> orders) {
        this.orders = orders;
    }


    private void toMenuDto(List<Menu> menu) {
        if(menu != null){
            this.menu = menu.stream().map((MenuDto::new)).collect(Collectors.toList());
        }
    }
}
