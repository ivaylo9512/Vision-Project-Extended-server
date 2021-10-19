package com.vision.project.repositories.base;

import com.vision.project.models.Chat;
import com.vision.project.models.Session;
import com.vision.project.models.compositePK.SessionPK;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, SessionPK> {
    @Query(value="from Session where chat = :chat")
    List<Session> findSessions(@Param("chat") Chat chat, Pageable pageable);

    @Query(value="FROM Session WHERE chat = :chat AND session_date < :session")
    List<Session> findNextSessions(@Param("chat") Chat chat, @Param("session") String session, Pageable pageable);
}
