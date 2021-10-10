package com.vision.project.models.specs;

import javax.validation.constraints.NotNull;

public class MessageSpec {
    @NotNull(message = "You must provide chat id.")
    private int chatId;

    private int senderId;

    @NotNull(message = "You must provide receiver id.")
    private int receiverId;

    @NotNull(message = "You must provide message.")
    private String message;

    public MessageSpec(){
    }

    public MessageSpec(int chatId, int senderId, int receiverId, String message) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
