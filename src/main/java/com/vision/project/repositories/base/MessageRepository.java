package com.vision.project.repositories.base;

import com.vision.project.models.Message;
import com.vision.project.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value="from Message where receiver = :user and session_date = :lastCheckDate and time > :lastCheckTime or receiver = :user and session_date > :lastCheckDate")
    List<Message> findMoreRecentMessages(@Param("user") UserModel user, @Param("lastCheckDate") LocalDate lastCheckDate, @Param("lastCheckTime") LocalTime lastCheckTime);
}
