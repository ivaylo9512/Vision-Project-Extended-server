package com.vision.project.models.specs;

import com.vision.project.models.Menu;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class RestaurantSpec {
    @NotNull(message = "You must provide name.")
    private String name;

    @NotNull(message = "You must provide type.")
    private String type;

    @NotNull(message = "You must provide address.")
    private String address;

    public RestaurantSpec(String name, String type, String address, Set<Menu> menu) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.menu = menu;
    }

    private Set<Menu> menu;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<Menu> getMenu() {
        return menu;
    }

    public void setMenu(Set<Menu> menu) {
        this.menu = menu;
    }
}
