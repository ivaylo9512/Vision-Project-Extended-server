package com.vision.project.controllers;

import com.vision.project.models.Chat;
import com.vision.project.models.DTOs.ChatDto;
import com.vision.project.models.DTOs.MessageDto;
import com.vision.project.models.Message;
import com.vision.project.models.Session;
import com.vision.project.models.UserDetails;
import com.vision.project.services.base.ChatService;
import com.vision.project.services.base.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/auth/chat")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @GetMapping(value = "/getChats")
    public List<ChatDto> getChats(){

        UserDetails userDetails = (UserDetails)SecurityContextHolder
                .getContext().getAuthentication().getDetails();
        int userId = userDetails.getId();

        List<Chat> chats = chatService.findUserChats(userId);
        List<ChatDto> chatsDto = chats.stream()
                .map(ChatDto::new)
                .collect(Collectors.toList());

        return chatsDto;

    }

    @GetMapping(value = "/nextSessions")
    public List<Session> nextChatSessions(
            @RequestParam(name = "chatId") int chatId,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "pageSize") int pageSize){
        System.out.println(page);
        System.out.println(pageSize);
        return chatService.findNextChatSessions(chatId, page, pageSize);
    }

    @GetMapping(value = "/getChatUpdates")
    public List<Message> chatUpdates(){
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
        int userId = userDetails.getId();
        return chatService.getNewMessages(userId);
    }

    @PostMapping(value = "/newMessage")
    public Message newMessage(@RequestParam MessageDto message){
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
        message.setSenderId(userDetails.getId());
        return chatService.addNewMessage(message);
    }
}
