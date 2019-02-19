package com.vision.project.models.DTOs;

public class MessageDto {
    private String message;

    private int chatId;

    private int senderId;

    private int receiverId;

    public MessageDto(String message, int chatId, int senderId, int receiverId) {
        this.message = message;
        this.chatId = chatId;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
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
}
