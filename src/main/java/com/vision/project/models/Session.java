package com.vision.project.models;

import com.vision.project.models.compositePK.SessionPK;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@IdClass(SessionPK.class)
@Table(name = "sessions")
public class Session{

    @Id
    private Chat chat;

    @Id
    @Column(name = "date")
    private LocalDate date;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "session", cascade = CascadeType.ALL)
    private List<Message> messages;
    public Session() {
    }

    public Session(Chat chat, LocalDate date) {
        this.date = date;
        this.chat = chat;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
