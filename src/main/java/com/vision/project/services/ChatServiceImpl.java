package com.vision.project.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vision.project.exceptions.NonExistingChat;
import com.vision.project.models.Chat;
import com.vision.project.models.DTOs.MessageDto;
import com.vision.project.models.Message;
import com.vision.project.models.Session;
import com.vision.project.models.compositePK.SessionPK;
import com.vision.project.repositories.base.ChatRepository;
import com.vision.project.repositories.base.MessageRepository;
import com.vision.project.repositories.base.SessionRepository;
import com.vision.project.services.base.ChatService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {
    private ChatRepository chatRepository;
    private SessionRepository sessionRepository;
    private MessageRepository messageRepository;

    private Map<Integer, List<MessageDto>> newMessages = Collections.synchronizedMap(new HashMap<>());
    private ConcurrentMap<Integer, DeferredResult<List<MessageDto>>> userRequests = new ConcurrentHashMap<>();

    private LocalDateTime serverStartDate;

    public ChatServiceImpl(ChatRepository chatRepository, SessionRepository sessionRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public List<Chat> findUserChats(int id, int pageSize) {
        List<Chat> chats = chatRepository.findUserChats(id);
        chats.forEach(chat -> chat
                .setSessions(sessionRepository
                        .getSessions(chat,
                                PageRequest.of(0, pageSize, Sort.Direction.DESC, "session_date"))));
        return chats;
    }

    @Override
    public List<Session> findNextChatSessions(int chatId, int page, int pageSize){
        return sessionRepository.getSessions(chatRepository.getOne(chatId), PageRequest.of(page, pageSize, Sort.Direction.DESC, "session_date"));
    }

    @Override
    public void getNewMessages(int userId, LocalDateTime lastMessageCheck, DeferredResult<List<MessageDto>> deferredResult){
        if(lastMessageCheck.isBefore(serverStartDate)){
            List<Message> messages = messageRepository.findMostRecentMessages(userId, lastMessageCheck.toLocalDate(), lastMessageCheck.toLocalTime());
            List<MessageDto> messageDTOs = messages.stream().map(MessageDto::new).collect(Collectors.toList());
            deferredResult.setResult(messageDTOs);
            return;
        }
        if(newMessages.containsKey(userId)){
            deferredResult.setResult(newMessages.get(userId));
            newMessages.remove(userId);
        }

        if(!deferredResult.hasResult()){
            userRequests.put(userId, deferredResult);
        }

    }

    @Override
    public MessageDto addNewMessage(MessageDto messageDto) {
        int sender = messageDto.getSenderId();
        int receiver = messageDto.getReceiverId();

        Chat chat = chatRepository.findById(messageDto.getChatId())
                .orElseThrow(()-> new NonExistingChat("Chat with id: " + messageDto.getChatId() + "is not found."));

        int chatFirstUser = chat.getFirstUserModel().getId();
        int chatSecondUser = chat.getSecondUserModel().getId();

        if ((sender != chatFirstUser && sender != chatSecondUser) || (receiver != chatFirstUser && receiver != chatSecondUser)) {
            throw new NonExistingChat("Users don't match the given chat.");
        }

        Session session = sessionRepository.findById(new SessionPK(chat,LocalDate.now()))
                .orElse(new Session(chat, LocalDate.now()));
        Message message = new Message(messageDto.getReceiverId(),LocalTime.now(),messageDto.getMessage(),session);
        message = messageRepository.save(message);

        messageDto.setTime(message.getTime());
        messageDto.setSession(session.getDate());

        List<MessageDto> messages = new ArrayList<>(Collections.singletonList(messageDto));
        if(userRequests.containsKey(message.getReceiverId())){
            userRequests.get(message.getReceiverId()).setResult(messages);
        }else {
            if (newMessages.containsKey(message.getReceiverId())) {
                newMessages.get(message.getReceiverId()).add(messageDto);
            } else {
                newMessages.put(message.getReceiverId(), messages);
            }
        }

        return messageDto;
    }

    @Override
    public void setServerStartDate() {
        serverStartDate = LocalDateTime.now();
    }

    @Override
    public void removeUserRequest(int userId, DeferredResult deferredResult) {
        userRequests.remove(userId, deferredResult);
    }

    @Override
    public void removeMessages(int userId) {
        newMessages.remove(userId);
    }
}
