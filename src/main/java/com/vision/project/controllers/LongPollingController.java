package com.vision.project.controllers;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.vision.project.exceptions.PasswordsMissMatchException;
import com.vision.project.exceptions.RegistrationIsDisabled;
import com.vision.project.exceptions.UsernameExistsException;
import com.vision.project.models.DTOs.UserDto;
import com.vision.project.models.DTOs.UserRequestDto;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.models.UserRequest;
import com.vision.project.models.specs.UserSpec;
import com.vision.project.security.Jwt;
import com.vision.project.services.base.ChatService;
import com.vision.project.services.base.LongPollingService;
import com.vision.project.services.base.OrderService;
import com.vision.project.services.base.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

@RestController
@RequestMapping(value = "/api/users/polling")
public class LongPollingController {

    private final UserService userService;
    private final OrderService orderService;
    private final ChatService chatService;
    private final LongPollingService longPollingService;

    public LongPollingController(UserService userService, OrderService orderService, ChatService chatService, LongPollingService longPollingService) {
        this.userService = userService;
        this.orderService = orderService;
        this.chatService = chatService;
        this.longPollingService = longPollingService;
    }

    @PostMapping("/login")
    @Transactional
    public UserDto login(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UserModel userModel = userDetails.getUserModel();

        return initializeUser(userModel);
    }

    @GetMapping(value = "/auth/getLoggedUser")
    public UserDto getLoggedUser(){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        UserModel userModel = userService.findById(loggedUser.getId());

        return initializeUser(userModel);
    }

    @PostMapping(value = "/register")
    public UserDto register(@RequestBody UserSpec user, HttpServletResponse response) {
        if(SecurityContextHolder.getContext() != null){
            UserDetails loggedUser = (UserDetails)SecurityContextHolder
                    .getContext().getAuthentication().getDetails();
            if(!loggedUser.getUserModel().getRole().equals("admin")){
                throw new RegistrationIsDisabled("Registration is disabled. Only the admin can register!");
            }
        }else{
            throw new RegistrationIsDisabled("Registration is disabled. Only the admin can register!");
        }

        UserModel userModel = userService.register(user,"ROLE_USER");
        String token = Jwt.generate(new UserDetails(userModel, new ArrayList<>(
                Collections.singletonList(new SimpleGrantedAuthority(userModel.getRole())))));

        response.addHeader("Authorization", "Token " + token);

        Restaurant restaurant = userModel.getRestaurant();
        UserRequest userRequest = new UserRequest(userModel.getId(), restaurant.getId(), null);

        longPollingService.addRequest(userRequest);

        return new UserDto(userModel);
    }

    private UserDto initializeUser(UserModel user){
        Restaurant restaurant = user.getRestaurant();
        restaurant.setOrders(orderService.findAllNotReady(restaurant));

        UserRequest userRequest = new UserRequest(user.getId(), restaurant.getId(), null);
        user.setChats(chatService.findUserChats(user.getId(), 3));

        longPollingService.addRequest(userRequest);
        return new UserDto(user, restaurant, LocalDateTime.now());
    }


    @PostMapping(value = "/auth/waitData")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public DeferredResult waitData(@RequestBody LocalDateTime lastCheck){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        DeferredResult<UserRequestDto> request = new DeferredResult<>(15000L,"Time out.");

        UserRequest userRequest = new UserRequest(loggedUser.getId(), loggedUser.getRestaurantId(), request, lastCheck);

        Runnable onTimeoutOrCompletion = ()-> userRequest.setRequest(null);
        request.onTimeout(onTimeoutOrCompletion);
        request.onCompletion(onTimeoutOrCompletion);

        longPollingService.setAndAddRequest(userRequest);

        return request;
    }

    @ExceptionHandler
    ResponseEntity handlePasswordsMissMatchException(PasswordsMissMatchException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler
    ResponseEntity handleUsernameExistsException(UsernameExistsException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler
    ResponseEntity handleRegistrationIsDisabled(RegistrationIsDisabled e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }
}
