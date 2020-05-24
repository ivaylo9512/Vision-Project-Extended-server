package com.vision.project.controllers;

import com.vision.project.models.Chat;
import com.vision.project.models.DTOs.ChatDto;
import com.vision.project.models.DTOs.MessageDto;
import com.vision.project.models.DTOs.SessionDto;
import com.vision.project.models.UserDetails;
import com.vision.project.models.specs.MessageSpec;
import com.vision.project.services.base.ChatService;
import com.vision.project.services.base.LongPollingService;
import com.vision.project.services.base.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/chat/auth")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;
    private final LongPollingService longPollingService;

    public ChatController(ChatService chatService, UserService userService, LongPollingService longPollingService) {
        this.chatService = chatService;
        this.userService = userService;
        this.longPollingService = longPollingService;
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
    public List<SessionDto> nextChatSessions(
            @RequestParam(name = "chatId") int chatId,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "pageSize") int pageSize){
        return chatService.findNextChatSessions(chatId, page, pageSize).stream().map(SessionDto::new).collect(Collectors.toList());
    }


    @PostMapping(value = "/newMessage")
    public MessageDto newMessage(@RequestBody MessageSpec message){
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
        message.setSenderId(userDetails.getId());
        return new MessageDto(longPollingService.addMessage(message));
    }
}
