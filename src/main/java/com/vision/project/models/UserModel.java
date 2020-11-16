package com.vision.project.models;

import com.vision.project.models.specs.RegisterSpec;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "users")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message="is required")
    @Size(min=1, message="is required")
    @Column(name = "username")
    private String username;

    @NotNull(message="is required")
    @Size(min=1, message="is required")
    private String password;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "age")
    private int age;

    @Column(name = "country")
    private String country;

    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(name = "chats",joinColumns ={@JoinColumn(name ="first_user")},
            inverseJoinColumns = @JoinColumn(name ="second_user" ))
    private List<Chat> chats;

    @Column(name = "profile_picture")
    private String profilePicture;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant")
    private Restaurant restaurant;

    private String role;

    public UserModel(){

    }

    public UserModel(String username, String password, String role, String firstName,
                     String lastName, int age, String country, String profilePicture, Restaurant restaurant) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.country = country;
        this.profilePicture = profilePicture;
        this.restaurant = restaurant;
    }

    public UserModel(RegisterSpec registerSpec, String role) {
        this.setUsername(registerSpec.getUsername());
        this.setPassword(registerSpec.getPassword());
        this.setRole(role);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
