package com.vision.project.models;

import com.vision.project.models.specs.RegisterSpec;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(name = "chats",joinColumns ={@JoinColumn(name ="first_user")},
            inverseJoinColumns = @JoinColumn(name ="second_user" ))
    private List<Chat> chats;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant")
    private Restaurant restaurant;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_image")
    private File profileImage;

    @Column(name = "is_enabled")
    private boolean isEnabled = false;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String role;
    private String password;
    private String firstName;
    private String lastName;
    private int age;
    private String country;

    public UserModel(){

    }

    public UserModel(String username, String password, String role, String firstName,
                     String lastName, int age, String country, File profileImage, Restaurant restaurant) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.country = country;
        this.profileImage = profileImage;
        this.restaurant = restaurant;
    }

    public UserModel(RegisterSpec registerSpec, String role) {
        this.setUsername(registerSpec.getUsername());
        this.setPassword(registerSpec.getPassword());
        this.setRole(role);
    }

    public UserModel(String username, String password, String role){
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public UserModel(String username, String password, String role, String email){
        this(username, password, role);
        this.email = email;
    }

    public UserModel(int id, String username, String password, String role, Restaurant restaurant){
        this(username, password, role);
        this.id = id;
        this.restaurant = restaurant;
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

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public File getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(File profileImage) {
        this.profileImage = profileImage;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
