package com.vision.project.models.DTOs;

import com.vision.project.models.*;
import com.vision.project.models.specs.UserSpec;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserDto {
    private long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private int age;
    private String country;
    private String role;
    private String profileImage;
    private RestaurantDto restaurant;
    private Map<Long, ChatDto> chats = new HashMap<>();
    private LocalDateTime lastCheck;

    public UserDto() {
    }

    public UserDto(UserModel userModel, RestaurantDto restaurant, LocalDateTime lastCheck, Map<Long, Chat> chats){
        this(userModel);
        this.restaurant = restaurant;
        this.chats = chats.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, o -> new ChatDto(o.getValue()), (existing, replacement) -> existing, LinkedHashMap::new));
        this.lastCheck = lastCheck;
    }

    public UserDto(UserSpec user, String role) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.age = user.getAge();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.country = user.getCountry();
        this.role = role;
    }

    public UserDto(UserModel user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.age = user.getAge();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.country = user.getCountry();
        this.role = user.getRole();
        this.restaurant = new RestaurantDto(user.getRestaurant());
        setProfileImage(user.getProfileImage());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setProfileImage(File profileImage) {
        if(profileImage != null){
            this.profileImage = profileImage.getResourceType() + profileImage.getOwner().getId() +
                    "." + profileImage.getExtension();
        }
    }

    public RestaurantDto getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantDto restaurant) {
        this.restaurant = restaurant;
    }

    public Map<Long, ChatDto> getChats() {
        return chats;
    }

    public void setChats(Map<Long, ChatDto> chats) {
        this.chats = chats;
    }

    public LocalDateTime getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(LocalDateTime lastCheck) {
        this.lastCheck = lastCheck;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
