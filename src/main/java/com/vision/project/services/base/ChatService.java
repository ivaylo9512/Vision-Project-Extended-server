package com.vision.project.services.base;

import com.vision.project.models.Chat;
import com.vision.project.models.DTOs.MessageDto;
import com.vision.project.models.Message;
import com.vision.project.models.Session;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatService {
    List<Chat> findUserChats(int id, int pageSize);

    List<Session> findNextChatSessions(int chatId, int page, int pageSize);

    void getNewMessages(int userId, LocalDateTime lastMessageCheck, DeferredResult<List<MessageDto>> userRequest);

    MessageDto addNewMessage(MessageDto message);

    @EventListener
    void setServerStartDate(ApplicationReadyEvent event);

    void removeUserRequest(int userId);

    void removeMessages(int userId);
}
