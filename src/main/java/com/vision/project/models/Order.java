package com.vision.project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = CascadeType.ALL ,mappedBy = "order", fetch = FetchType.EAGER)
    private Set<Dish> dishes = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created", columnDefinition = "DATETIME(6)")
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "updated", columnDefinition = "DATETIME(6)")
    private LocalDateTime updated;

    @Column(name = "ready")
    private boolean ready = false;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="restaurant")
    private Restaurant restaurant;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="created_by")
    private UserModel user;

    public Order() {
    }
    public Order(Order order) {
        this.restaurant = order.getRestaurant();
        this.user = order.getUser();
        this.created = order.getCreated();
    }
    public Order(int id, Set<Dish> dishes, LocalDateTime created, LocalDateTime updated, Restaurant restaurant) {
        this.id = id;
        this.dishes = dishes;
        this.created = created;
        this.updated = updated;
        this.restaurant = restaurant;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Set<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(Set<Dish> dishes) {
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
