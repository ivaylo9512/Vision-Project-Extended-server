package com.vision.project.services.base;

import com.vision.project.models.Chat;
import com.vision.project.models.DTOs.MessageDto;
import com.vision.project.models.Message;
import com.vision.project.models.Session;

import java.util.List;

public interface ChatService {
    List<Chat> findUserChats(int id);

    List<Session> findNextChatSessions(int chatId, int page, int pageSize);

    List<Message> getNewMessages(int userId);

    Message addNewMessage(MessageDto message);
}
