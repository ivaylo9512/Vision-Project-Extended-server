package com.vision.project.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dishes")
public class Dish{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean ready;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME(6)")
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(columnDefinition = "DATETIME(6)")
    private LocalDateTime updated;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserModel updatedBy;

    private String name;

    public Dish() {
    }

    public Dish(String name, Boolean ready, Order order) {
        this.name = name;
        this.ready = ready;
        this.order = order;
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

    public Boolean getReady() {
        return ready;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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

    public UserModel getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UserModel updatedBy) {
        this.updatedBy = updatedBy;
    }
}
