package com.vision.project.repositories.base;

import com.vision.project.models.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query(value="from Chat where first_user = :user or second_user = :user order by id")
    List<Chat> findAllUserChats(@Param("user") long id);

    @Query(value="FROM Chat as c where (first_user = :user or second_user = :user) AND (updated_at = :lastUpdatedAt AND id > :lastId OR updated_at < :lastUpdatedAt) order by updatedAt desc, id asc")
    Page<Chat> findNextUserChats(
            @Param("user") long id,
            @Param("lastId") long lastId,
            @Param("lastUpdatedAt") String lastUpdatedAt,
            Pageable pageable);

    @Query(value="FROM Chat as c where first_user = :user OR second_user = :user order by updatedAt desc, id asc")
    Page<Chat> findUserChats(
            @Param("user") long id,
            Pageable pageable);

}
