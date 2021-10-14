package com.vision.project.models;

import com.vision.project.models.specs.RestaurantSpec;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Where;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "restaurants")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "restaurant", fetch = FetchType.EAGER)
    @OrderBy("name")
    private List<Menu> menu;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "restaurant" , fetch = FetchType.LAZY)
    @OrderBy("created")
    private List<Order> orders;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
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

    public Restaurant(long id, String name, String address, String type, List<Menu> menu) {
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
        this.menu = restaurant.getMenu().stream().map(m -> new Menu(m, this)).collect(Collectors.toList());
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
