package com.vision.project.controllers;

import com.vision.project.models.Chat;
import com.vision.project.models.DTOs.ChatDto;
import com.vision.project.models.DTOs.MessageDto;
import com.vision.project.models.Session;
import com.vision.project.models.UserDetails;
import com.vision.project.services.base.ChatService;
import com.vision.project.services.base.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/chat/auth")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @GetMapping(value = "/getChats")
    @Transactional
    public List<ChatDto> getChats(@RequestParam(name = "pageSize") int pageSize){

        UserDetails userDetails = (UserDetails)SecurityContextHolder
                .getContext().getAuthentication().getDetails();
        int userId = userDetails.getId();

        List<Chat> chats = chatService.findUserChats(userId, pageSize);

        return chats.stream()
                .map(ChatDto::new)
                .collect(Collectors.toList());

    }

    @GetMapping(value = "/nextSessions")
    public List<Session> nextChatSessions(
            @RequestParam(name = "chatId") int chatId,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "pageSize") int pageSize){
        return chatService.findNextChatSessions(chatId, page, pageSize);
    }

    @PatchMapping(value = "/getChatUpdates")
    public DeferredResult<List<MessageDto>> chatUpdates(@RequestBody LocalDateTime lastMessageCheck){
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
        int userId = userDetails.getId();

        DeferredResult<List<MessageDto>> deferredResult = new DeferredResult<>(100000L,"Time out.");
        chatService.getNewMessages(userId, lastMessageCheck, deferredResult);

        deferredResult.onCompletion(() -> chatService.removeUserRequest(userId, deferredResult));
        deferredResult.onTimeout(() -> chatService.removeUserRequest(userId, deferredResult));

        return deferredResult;
    }


    @PostMapping(value = "/newMessage")
    public MessageDto newMessage(@RequestBody MessageDto message){
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
        message.setSenderId(userDetails.getId());
        return chatService.addNewMessage(message);
    }
}
