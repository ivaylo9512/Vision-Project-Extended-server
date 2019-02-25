package com.vision.project.models.DTOs;

import com.vision.project.models.Chat;
import com.vision.project.models.Session;
import com.vision.project.models.User;

import javax.persistence.*;
import java.util.List;

public class ChatDto {
    private int id;
    private UserDto firstUser;
    private UserDto secondUser;

    public ChatDto() {
    }

    public ChatDto(Chat chat) {
        this.id = chat.getId();
        this.firstUser = new UserDto(chat.getFirstUser());
        this.secondUser = new UserDto(chat.getSecondUser());
    }

    public UserDto getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(UserDto firstUser) {
        this.firstUser = firstUser;
    }

    public UserDto getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(UserDto secondUser) {
        this.secondUser = secondUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}