package com.vision.project.models.DTOs;

import com.vision.project.models.User;
import com.vision.project.models.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.*;
import java.util.stream.Collectors;

public class UserDto {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private int age;
    private String country;
    private String role;

    public UserDto(UserDetails userDetails){
        this.id = userDetails.getId();
        this.username = userDetails.getUsername();
        this.age = userDetails.getAge();
        this.firstName = userDetails.getFirstName();
        this.lastName = userDetails.getLastName();
        this.age = userDetails.getAge();
        this.country = userDetails.getCountry();
        this.role = new ArrayList<>(userDetails.getAuthorities()).get(0).getAuthority();
    }

    public UserDto(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.age = user.getAge();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.age = user.getAge();
        this.country = user.getCountry();
        this.role = user.getRole();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
