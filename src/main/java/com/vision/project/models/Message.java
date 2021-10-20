package com.vision.project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "messages")
public class Message{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver")
    private UserModel receiver;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({@JoinColumn(name = "chat"),@JoinColumn(name = "session_date")})
    private Session session;

    private LocalTime time;
    private String message;

    public Message(UserModel receiver, LocalTime time, String message, Session session){
        this.receiver = receiver;
        this.time = time;
        this.message = message;
        this.session = session;
    }

    public Message() {
    }

    public UserModel getReceiver() {
        return receiver;
    }

    public LocalTime getTime() {
        return time;
    }

    public Session getSession() {
        return session;
    }

    public String getMessage() {
        return message;
    }

    public long getId() {
        return id;
    }
}
