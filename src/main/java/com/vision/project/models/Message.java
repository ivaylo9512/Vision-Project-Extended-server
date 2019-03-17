package com.vision.project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vision.project.models.DTOs.MessageDto;
import com.vision.project.models.compositePK.MessagePK;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@IdClass(MessagePK.class)
@Table(name = "messages")
public class Message{

    @Id
    private int receiverId;

    @Id
    @Column(name = "date")
    private LocalTime time;

    @Column(name = "message")
    private String message;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumns({@JoinColumn(name = "chat"),@JoinColumn(name = "session_date")})
    private Session session;

    public Message(int receiverId, LocalTime time, String message, Session session){
        this.receiverId = receiverId;
        this.time = time;
        this.message = message;
        this.session = session;
    }
    public Message() {
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
