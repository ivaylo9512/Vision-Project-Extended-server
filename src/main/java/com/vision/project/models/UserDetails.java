package com.vision.project.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;
import java.util.List;

public class UserDetails extends User {
    private int id;
    private int restaurantId;
    private UserModel userModel;

    public UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, int id, int restaurantId){
        super(username,password,authorities);
        this.id = id;
        this.restaurantId = restaurantId;
    }

    public UserDetails(UserModel userModel, List<SimpleGrantedAuthority> authorities){
        super(userModel.getUsername(), userModel.getPassword(), authorities);
        this.id = userModel.getId();
        this.restaurantId = userModel.getRestaurant().getId();
        this.userModel = userModel;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
