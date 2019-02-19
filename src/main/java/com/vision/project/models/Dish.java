package com.vision.project.models;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "dishes")
public class Dish{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "ready")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean ready = false;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;

    public Dish() {
    }

    public Dish(int id, String name, Boolean ready, Order order) {
        this.id = id;
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

    public void setOrder(Order order) {
        this.order = order;
    }
}
