package com.vision.project.controllers;

import com.vision.project.models.DTOs.ChatDto;
import com.vision.project.models.DTOs.SessionDto;
import com.vision.project.models.UserDetails;
import com.vision.project.services.base.ChatService;
import com.vision.project.services.base.LongPollingService;
import com.vision.project.services.base.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    public Map<Integer, ChatDto> getChats(@RequestParam(name = "pageSize") int pageSize){
        UserDetails userDetails = (UserDetails)SecurityContextHolder
                .getContext().getAuthentication().getDetails();
        int userId = userDetails.getId();

        return chatService.findUserChats(userId, pageSize).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, o -> new ChatDto(o.getValue()), (existing, duplicate) -> existing, LinkedHashMap::new));
    }

    @GetMapping(value = "/getSessions")
    public List<SessionDto> getSessions(
            @RequestParam(name = "chatId") int chatId,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "pageSize") int pageSize){
        return chatService.findSessions(chatId, page, pageSize).stream().map(SessionDto::new).collect(Collectors.toList());
    }
}
