package com.vision.project.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;
import java.util.List;

public class UserDetails extends User {
    private long id;
    private long restaurantId;
    private UserModel userModel;

    public UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, long id, long restaurantId){
        super(username, password, authorities);
        this.id = id;
        this.restaurantId = restaurantId;
    }

    public UserDetails(UserModel userModel, List<SimpleGrantedAuthority> authorities){
        super(userModel.getUsername(), userModel.getPassword(), authorities);
        this.id = userModel.getId();
        setRestaurantId(userModel.getRestaurant());
        this.userModel = userModel;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setRestaurantId(Restaurant restaurant) {
        if(restaurant != null){
            this.restaurantId = restaurant.getId();
        }
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
