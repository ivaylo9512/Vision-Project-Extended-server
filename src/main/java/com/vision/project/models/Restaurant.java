package com.vision.project.models;

import com.vision.project.models.specs.RestaurantSpec;
import org.hibernate.annotations.Where;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "restaurants")
public class Restaurant {
    @Id
    private int id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "restaurant", fetch = FetchType.EAGER)
    @OrderBy("name")
    private List<Menu> menu;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "restaurant" , fetch = FetchType.LAZY)
    @OrderBy("created")
    @Where(clause = "ready = false")
    private List<Order> orders;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "restaurant", fetch = FetchType.LAZY)
    private List<UserModel> users;

    private String name;
    private String address;
    private String type;
    private String token;

    public Restaurant() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Restaurant(int id, String name, String address, String type, List<Menu> menu) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.type = type;
        this.menu = menu;
    }

    public Restaurant(RestaurantSpec restaurant) {
        this.address = restaurant.getAddress();
        this.name = restaurant.getName();
        this.type = restaurant.getType();
        this.menu = restaurant.getMenu();
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

    public List<Menu> getMenu() {
        return menu;
    }

    public void setMenu(List<Menu> menu) {
        this.menu = menu;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }
}
