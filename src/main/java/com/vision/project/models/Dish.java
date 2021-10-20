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
    private long id;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean ready;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;

    @CreationTimestamp
    @Column(name = "created_at" ,columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at" ,columnDefinition = "DATETIME(6)")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "updated_by")
    private UserModel updatedBy;

    private String name;

    public Dish() {
    }

    public Dish(String name, Order order){
        this.name = name;
        this.order = order;
    }

    public Dish(String name, Boolean ready, UserModel updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.ready = ready;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Boolean isReady() {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public UserModel getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UserModel updatedBy) {
        this.updatedBy = updatedBy;
    }
}
