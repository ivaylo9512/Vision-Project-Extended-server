package com.vision.project.models.specs;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class RestaurantSpec {
    @NotBlank(message = "You must provide name.")
    private String name;

    @NotBlank(message = "You must provide type.")
    private String type;

    @NotBlank(message = "You must provide address.")
    private String address;

    private List<String> menu;

    public RestaurantSpec(){
    }

    public RestaurantSpec(String name, String type, String address, List<String> menu) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.menu = menu;
    }


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

    public List<String> getMenu() {
        return menu;
    }

    public void setMenu(List<String> menu) {
        this.menu = menu;
    }
}
