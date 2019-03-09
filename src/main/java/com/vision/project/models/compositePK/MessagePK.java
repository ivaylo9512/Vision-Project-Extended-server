package com.vision.project.models.compositePK;


import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class MessagePK implements Serializable {

    @Column(name = "receiver")
    private int receiver;

    @Column(name = "date")
    private LocalTime date;

    public MessagePK() {
    }

    public MessagePK(LocalTime date, int receiver){
        this.date = date;
        this.receiver = receiver;
    }

    public LocalTime getDate() {
        return date;
    }

    public void setDate(LocalTime date) {
        this.date = date;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessagePK)) return false;
        MessagePK that = (MessagePK) o;
        return Objects.equals(getDate(), that.getDate()) &&
                Objects.equals(getReceiver(), that.getReceiver());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getReceiver());
    }
}
