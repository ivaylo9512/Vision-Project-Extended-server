package com.vision.project.services;

import com.vision.project.exceptions.UnauthorizedException;
import com.vision.project.models.Chat;
import com.vision.project.models.Message;
import com.vision.project.models.Session;
import com.vision.project.models.UserModel;
import com.vision.project.models.compositePK.SessionPK;
import com.vision.project.models.specs.MessageSpec;
import com.vision.project.repositories.base.ChatRepository;
import com.vision.project.repositories.base.MessageRepository;
import com.vision.project.repositories.base.SessionRepository;
import com.vision.project.repositories.base.UserRepository;
import com.vision.project.services.base.ChatService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class ChatServiceImpl implements ChatService {
    private ChatRepository chatRepository;
    private SessionRepository sessionRepository;
    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private int sessionPageSize = 3;

    public ChatServiceImpl(ChatRepository chatRepository, SessionRepository sessionRepository, MessageRepository messageRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Map<Integer, Chat> findUserChats(int id, int pageSize) {
        Map<Integer, Chat> chatsMap = new LinkedHashMap<>();
        chatRepository.findUserChats(id, PageRequest.of(0, pageSize)).forEach(chat -> {
            chat.setSessions(sessionRepository.findSessions(chat, PageRequest.of(0, pageSize,
                    Sort.Direction.DESC, "session_date")));

            UserModel loggedUser = chat.getFirstUserModel();
            if(loggedUser.getId() != id){
                chat.setFirstUserModel(chat.getSecondUserModel());
                chat.setSecondUserModel(loggedUser);
            }

            chatsMap.put(chat.getId(), chat);
        });
        return chatsMap;
    }

    @Override
    public List<Session> findSessions(int chatId, int page, int pageSize){
        return sessionRepository.findSessions(chatRepository.getById(chatId), PageRequest.of(page, pageSize, Sort.Direction.DESC, "session_date"));
    }

    @Transactional
    @Override
    public Message addNewMessage(MessageSpec messageSpec) {
        int sender = messageSpec.getSenderId();
        int receiver = messageSpec.getReceiverId();

        Chat chat = chatRepository.findById(messageSpec.getChatId())
                .orElseThrow(()-> new EntityNotFoundException("Chat with id: " + messageSpec.getChatId() + " is not found."));

        int chatFirstUser = chat.getFirstUserModel().getId();
        int chatSecondUser = chat.getSecondUserModel().getId();

        if ((sender != chatFirstUser && sender != chatSecondUser) || (receiver != chatFirstUser && receiver != chatSecondUser)) {
            throw new UnauthorizedException("Users don't match the given chat.");
        }

        Session session = sessionRepository.findById(new SessionPK(chat,LocalDate.now()))
                .orElse(new Session(chat, LocalDate.now()));

        UserModel user = userRepository.getById(receiver);
        Message message = new Message(user,LocalTime.now(),messageSpec.getMessage(),session);

        return messageRepository.save(message);
    }

    @Override
    public List<Message> findMoreRecentMessages(int userId, LocalDateTime lastCheck) {
        return messageRepository.findMoreRecentMessages(userRepository.getById(userId), lastCheck.toLocalDate(), lastCheck.toLocalTime());
    }
}
