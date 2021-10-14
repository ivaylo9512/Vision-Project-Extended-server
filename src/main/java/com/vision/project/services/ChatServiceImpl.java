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
    private final ChatRepository chatRepository;
    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ChatServiceImpl(ChatRepository chatRepository, SessionRepository sessionRepository, MessageRepository messageRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Chat findById(long id, long loggedUser) {
        Chat chat = chatRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Chat not found."));

        if(!chat.hasUser(loggedUser)){
            throw new UnauthorizedException("Unauthorized.");
        }

        return chat;
    }


    @Override
    public Map<Long, Chat> findUserChats(long id, int pageSize) {
        Map<Long, Chat> chatsMap = new LinkedHashMap<>();
        chatRepository.findUserChats(id, PageRequest.of(0, pageSize)).forEach(chat -> {
            chat.setSessions(sessionRepository.findSessions(chat, PageRequest.of(0, pageSize,
                    Sort.Direction.DESC, "session_date")));

            UserModel loggedUser = chat.getFirstUser();
            if(loggedUser.getId() != id){
                chat.setFirstUser(chat.getSecondUser());
                chat.setSecondUser(loggedUser);
            }

            chatsMap.put(chat.getId(), chat);
        });
        return chatsMap;
    }

    @Override
    public List<Session> findSessions(Chat chat, int page, int pageSize){
        return sessionRepository.findSessions(chat, PageRequest.of(page, pageSize, Sort.Direction.DESC, "session_date"));
    }

    @Transactional
    @Override
    public Message addNewMessage(MessageSpec messageSpec) {
        Chat chat = chatRepository.findById(messageSpec.getChatId())
                .orElseThrow(()-> new EntityNotFoundException("Chat not found."));

        verifyMessage(messageSpec, chat);

        Session session = sessionRepository.findById(new SessionPK(chat,LocalDate.now()))
                .orElse(new Session(chat, LocalDate.now()));

        UserModel user = userRepository.getById(messageSpec.getReceiverId());
        Message message = new Message(user, LocalTime.now(), messageSpec.getMessage(), session);

        return messageRepository.save(message);
    }

    public void verifyMessage(MessageSpec message, Chat chat) {
        long sender = message.getSenderId();
        long receiver = message.getReceiverId();

        if (!chat.hasUser(sender) || !chat.hasUser(receiver)) {
            throw new UnauthorizedException("Users don't match the given chat.");
        }
    }

    @Override
    public List<Message> findMoreRecentMessages(Long userId, LocalDateTime lastCheck) {
        return messageRepository.findMoreRecentMessages(userRepository.getById(userId), lastCheck.toLocalDate(), lastCheck.toLocalTime());
    }

    @Override
    public void delete(long id, UserModel user) {
        if(user.getRole().equals("ROLE_ADMIN")){
            chatRepository.deleteById(id);
            return;
        }

        chatRepository.delete(findById(id, user.getId()));
    }
}
