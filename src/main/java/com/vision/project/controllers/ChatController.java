package com.vision.project.controllers;

import com.vision.project.models.DTOs.ChatDto;
import com.vision.project.models.DTOs.MessageDto;
import com.vision.project.models.DTOs.SessionDto;
import com.vision.project.models.Message;
import com.vision.project.models.UserDetails;
import com.vision.project.models.specs.MessageSpec;
import com.vision.project.services.base.ChatService;
import com.vision.project.services.base.LongPollingService;
import com.vision.project.services.base.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/chats/auth")
public class ChatController {
    private final ChatService chatService;
    private final LongPollingService longPollingService;
    private final UserService userService;

    public ChatController(ChatService chatService, LongPollingService longPollingService, UserService userService) {
        this.chatService = chatService;
        this.longPollingService = longPollingService;
        this.userService = userService;
    }

    @GetMapping(value = "/findNextSessions/{chatId}/{lastSession}")
    public List<SessionDto> findNextSessions(
            @PathVariable(name = "chatId") long chatId,
            @PathVariable(name = "lastSession") String lastSession){
        UserDetails loggedUser = (UserDetails)SecurityContextHolder
                .getContext().getAuthentication().getDetails();

        return chatService.findSessions(chatService.findById(chatId, loggedUser.getId()), lastSession).stream()
                .map(SessionDto::new).collect(Collectors.toList());
    }

    @DeleteMapping(value = "/delete/{id}")
    @Transactional
    public void delete(@PathVariable("id") long id){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        chatService.delete(id, userService.findById(loggedUser.getId()));
    }

    @PostMapping("/newMessage")
    public MessageDto addMessage(@Valid @RequestBody MessageSpec messageSpec) {
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        messageSpec.setSenderId(loggedUser.getId());

        Message message = chatService.addNewMessage(messageSpec);
        longPollingService.checkMessages(message);

        return new MessageDto(message);
    }
}
