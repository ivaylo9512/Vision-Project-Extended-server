package com.vision.project.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "first_user", insertable = false, updatable = false)
    private User firstUser;

//    @Id
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "second_user", insertable = false, updatable = false)
    private User secondUser;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Session> sessions;

//    @OneToMany(cascade = CascadeType.ALL ,mappedBy = "chat", fetch = FetchType.EAGER, orphanRemoval = true)
//    private List<Session> session;
    public Chat() {
    }

    public Chat(User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(User secondUser) {
        this.secondUser = secondUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }
}
