package com.vision.project.services.base;

import com.vision.project.models.Chat;
import com.vision.project.models.Message;
import com.vision.project.models.Session;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.MessageSpec;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ChatService {
    Chat findById(long id, long loggedUser);

    Map<Long, Chat> findUserChats(long id, int pageSize);

    List<Session> findSessions(Chat chat, int page, int pageSize);

    Message addNewMessage(MessageSpec message);

    List<Message> findMoreRecentMessages(Long userId, LocalDateTime lastCheck);

    void delete(long id, UserModel user);
}
