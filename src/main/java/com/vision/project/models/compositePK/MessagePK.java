package com.vision.project.models.compositePK;


import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class MessagePK implements Serializable {

    @Column(name = "receiver_id")
    private int receiverId;

    @Column(name = "time")
    private LocalTime time;

    public MessagePK() {
    }

    public MessagePK(LocalTime time, int receiverId){
        this.time = time;
        this.receiverId = receiverId;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessagePK)) return false;
        MessagePK that = (MessagePK) o;
        return Objects.equals(getTime(), that.getTime()) &&
                Objects.equals(getReceiverId(), that.getReceiverId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTime(), getReceiverId());
    }
}
