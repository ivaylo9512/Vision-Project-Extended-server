package com.vision.project.models;

import com.vision.project.models.specs.OrderCreateSpec;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
public class Order{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(cascade = CascadeType.ALL , mappedBy = "order", fetch = FetchType.EAGER)
    private List<Dish> dishes;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime updated;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean ready;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="restaurant")
    private Restaurant restaurant;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="created_by")
    private UserModel user;

    public Order() {
    }

    public Order(long id){
        this.id = id;
    }

    public Order(OrderCreateSpec orderSpec, Restaurant restaurant, UserModel user){
        this.restaurant = restaurant;
        this.user = user;
        this.dishes = orderSpec.getDishes().stream().map(s -> new Dish(s, this)).collect(Collectors.toList());
    }
    public Order(Order order) {
        this.restaurant = order.getRestaurant();
        this.user = order.getUser();
        this.created = order.getCreated();
    }
    public Order(long id, List<Dish> dishes, LocalDateTime created, LocalDateTime updated, Restaurant restaurant) {
        this.id = id;
        this.dishes = dishes;
        this.created = created;
        this.updated = updated;
        this.restaurant = restaurant;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
