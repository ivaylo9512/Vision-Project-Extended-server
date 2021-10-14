package com.vision.project.models.specs;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MessageSpec {
    @NotNull(message = "You must provide chat id.")
    private Long chatId;

    private long senderId;

    @NotNull(message = "You must provide receiver id.")
    private Long receiverId;

    @NotBlank(message = "You must provide message.")
    private String message;

    public MessageSpec(){
    }

    public MessageSpec(long chatId, long senderId, long receiverId, String message) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
