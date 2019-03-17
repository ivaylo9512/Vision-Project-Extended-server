package com.vision.project.repositories.base;

import com.vision.project.models.Message;
import com.vision.project.models.compositePK.MessagePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, MessagePK> {
    @Query(value="from Message where receiverId = :userId and session_date = :lastCheckDate and time > :lastCheckTime or receiverId = :userId and session_date > :lastCheckDate")
    List<Message> findMostRecentMessages(@Param("userId")int userId, @Param("lastCheckDate")LocalDate lastCheckDate, @Param("lastCheckTime") LocalTime lastCheckTime);
}
