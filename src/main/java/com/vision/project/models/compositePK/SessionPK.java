package com.vision.project.models.compositePK;

import com.vision.project.models.Chat;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class SessionPK implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "chat", insertable = false, updatable = false)
    private Chat chat;

    @Column(name = "session_date")
    private LocalDate date;

    public SessionPK() {
    }

    public SessionPK(Chat chat, LocalDate date) {
        this.chat = chat;
        this.date = date;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionPK that)) return false;

        return Objects.equals(getChat(), that.getChat()) &&
                Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getChat());
    }


    public LocalDate getDate() {
        return date;
    }

    public Chat getChat() {
        return chat;
    }
}
