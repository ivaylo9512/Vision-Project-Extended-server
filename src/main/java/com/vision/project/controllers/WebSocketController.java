package com.vision.project.controllers;

import com.vision.project.models.DTOs.OrderDto;
import com.vision.project.models.Order;
import com.vision.project.models.UserDetails;
import com.vision.project.models.specs.MessageSpec;
import com.vision.project.security.Jwt;
import com.vision.project.services.base.ChatService;
import com.vision.project.services.base.OrderService;
import com.vision.project.services.base.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;

import javax.transaction.Transactional;
import java.security.Principal;

@Controller
public class WebSocketController {
    private SimpMessagingTemplate messagingTemplate;
    private ChatService chatService;
    private UserService userService;
    private OrderService orderService;

    public WebSocketController(SimpMessagingTemplate messagingTemplate, ChatService chatService, UserService userService, OrderService orderService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.userService = userService;
        this.orderService = orderService;
    }


    @MessageMapping("/message")
    public void message(Principal principal, MessageSpec message, SimpMessageHeaderAccessor headers) throws  Exception {
        UserDetails loggedUser;
        try{
            String auth = headers.getNativeHeader("Authorization").get(0);
            String token = auth.substring(6);
            loggedUser = Jwt.validate(token);
        }catch (Exception e){
            throw new BadCredentialsException("Jwt token is missing or is incorrect.");
        }
        message.setSenderId(loggedUser.getId());
        chatService.addNewMessage(message);

        messagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiverId()), "/message", message);
    }

    @MessageMapping("/createOrder")
    @Transactional
    public void createChat(Principal principal, Order order, SimpMessageHeaderAccessor headers) throws  Exception {
        UserDetails loggedUser;
        try{
            String auth = headers.getNativeHeader("Authorization").get(0);
            String token = auth.substring(6);
            loggedUser = Jwt.validate(token);
        }catch (Exception e){
            throw new BadCredentialsException("Jwt token is missing or is incorrect.");
        }

        int restaurantId = loggedUser.getRestaurantId();
        int userId = loggedUser.getId();

        OrderDto orderDto = new OrderDto(orderService.create(order, restaurantId, userId);
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/createOrder", orderDto);
    }
}
