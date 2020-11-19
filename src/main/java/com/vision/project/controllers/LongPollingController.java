package com.vision.project.controllers;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.vision.project.exceptions.PasswordsMissMatchException;
import com.vision.project.exceptions.RegistrationIsDisabled;
import com.vision.project.exceptions.UsernameExistsException;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.RestaurantDto;
import com.vision.project.models.DTOs.UserDto;
import com.vision.project.models.DTOs.UserRequestDto;
import com.vision.project.models.specs.RegisterSpec;
import com.vision.project.security.Jwt;
import com.vision.project.services.base.*;
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
import java.util.Map;

@RestController
@RequestMapping(value = "/api/users/polling")
public class LongPollingController {

    private final UserService userService;
    private final OrderService orderService;
    private final ChatService chatService;
    private final LongPollingService longPollingService;
    private final FileService fileService;

    public LongPollingController(UserService userService, OrderService orderService, ChatService chatService, LongPollingService longPollingService, FileService fileService) {
        this.userService = userService;
        this.orderService = orderService;
        this.chatService = chatService;
        this.longPollingService = longPollingService;
        this.fileService = fileService;
    }

    @PostMapping("/login")
    @Transactional
    public UserDto login(@RequestParam("pageSize") int pageSize){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return initializeUser(userDetails.getUserModel(), pageSize);
    }

    @GetMapping(value = "/auth/getLoggedUser")
    public UserDto getLoggedUser(@RequestParam("pageSize") int pageSize){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return initializeUser(userService.findById(loggedUser.getId()), pageSize);
    }

    @PostMapping(value = "/register")
    public UserDto register(@ModelAttribute RegisterSpec registerSpec, HttpServletResponse response) {
        UserModel newUser = new UserModel(registerSpec, "ROLE_USER");

        if(registerSpec.getProfileImage() != null){
            File profileImage = fileService.create(registerSpec.getProfileImage(), newUser.getId() + "logo");
            newUser.setProfileImage(profileImage);
        }

        String token = Jwt.generate(new UserDetails(newUser, new ArrayList<>(
                Collections.singletonList(new SimpleGrantedAuthority(newUser.getRole())))));
        response.addHeader("Authorization", "Token " + token);

        return new UserDto(userService.create(newUser));
    }

    private UserDto initializeUser(UserModel user, int pageSize){
        Restaurant restaurant = user.getRestaurant();
        Map<Integer, Order> orders = orderService.findNotReady(restaurant.getId(), 0, pageSize);
        RestaurantDto restaurantDto = new RestaurantDto(restaurant, orders);

        UserRequest userRequest = new UserRequest(user.getId(), restaurant.getId(), null);
        Map<Integer, Chat> chats = chatService.findUserChats(user.getId(), pageSize);

        longPollingService.addRequest(userRequest);
        return new UserDto(user, restaurantDto, LocalDateTime.now(), chats);
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
