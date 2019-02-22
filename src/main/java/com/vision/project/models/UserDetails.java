package com.vision.project.models;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.persistence.Column;
import java.util.Collection;

public class UserDetails extends User {
    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private String country;

    public UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, int id){
        super(username,password,authorities);
        this.id = id;
    }
    public UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
                       int id, String firstName, String lastName, int age, String country){

        super(username,password,authorities);
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.country = country;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setlastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
