package com.vision.project.services;

import com.vision.project.exceptions.NonExistingChat;
import com.vision.project.models.Chat;
import com.vision.project.models.DTOs.MessageDto;
import com.vision.project.models.Message;
import com.vision.project.models.Session;
import com.vision.project.models.compositePK.SessionPK;
import com.vision.project.repositories.base.ChatRepository;
import com.vision.project.repositories.base.MessageRepository;
import com.vision.project.repositories.base.SessionRepository;
import com.vision.project.repositories.base.UserRepository;
import com.vision.project.services.base.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    private ChatRepository chatRepository;
    private SessionRepository sessionRepository;
    private MessageRepository messageRepository;
    private UserRepository userRepository;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository, SessionRepository sessionRepository, MessageRepository messageRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Chat> findUserChats(int id) {
        return chatRepository.findUserChats(1);
    }


    @Override
    public List<Session> findNextChatSessions(int chatId, int page, int pageSize){
        return sessionRepository.getSessions(chatRepository.getOne(1), PageRequest.of(1,2));
    }

    @Override
    public List<Message> getNewMessages(int userId){
        return new ArrayList<>();
    }

    @Override
    public Message addNewMessage(MessageDto messageDto) {
        int sender = messageDto.getSenderId();
        int receiver = messageDto.getReceiverId();


        Chat chat = chatRepository.findById(messageDto.getChatId())
                .orElseThrow(()-> new NonExistingChat("Chat with id: " + messageDto.getChatId() + "is not found."));

        int chatFirstUser = chat.getFirstUser().getId();
        int chatSecondUser = chat.getSecondUser().getId();

        if ((sender != chatFirstUser && sender != chatSecondUser) || (receiver != chatFirstUser && receiver != chatSecondUser)) {
            throw new NonExistingChat("Users don't match the given chat.");
        }

        Session session = new Session(chat, LocalDate.now());
        Message message = new Message(messageDto.getReceiverId(),LocalDateTime.now(),messageDto.getMessage(),session);
        message = messageRepository.save(message);

        return message;
    }
}
