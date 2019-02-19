package com.vision.project.models;

import com.vision.project.models.compositePK.MessagePK;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@IdClass(MessagePK.class)
@Table(name = "messages")
public class Message{

    @Id
    private int receiver;

    @Id
    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "message")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumns({@JoinColumn(name = "chat"),@JoinColumn(name = "session_date")})
    private Session session;

    public Message() {
    }

    public Message(int receiver, LocalDateTime date) {
        this.receiver = receiver;
        this.date = date;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
