package com.vision.project.models;

import com.vision.project.models.specs.RestaurantSpec;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "restaurant", fetch = FetchType.EAGER)
    @OrderBy("name")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Menu> menu;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<UserModel> users;

    private String name;
    private String address;
    private String type;
    private String token;

    public Restaurant() {
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

    public String getAddress() {
        return address;
    }

    public String getType() {
        return type;
    }

    public List<Menu> getMenu() {
        return menu;
    }

    public void setMenu(List<Menu> menu) {
        this.menu = menu;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
