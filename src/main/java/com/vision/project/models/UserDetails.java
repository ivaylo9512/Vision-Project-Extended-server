package com.vision.project.models;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserDetails extends User {
    private int id;

    public UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, int id){
        super(username,password,authorities);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
