package com.vision.project.controllers;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.RestaurantDto;
import com.vision.project.models.DTOs.UserDto;
import com.vision.project.models.DTOs.UserRequestDto;
import com.vision.project.services.base.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/users/polling")
public class LongPollingController {
    private final UserService userService;
    private final OrderService orderService;
    private final ChatService chatService;
    private final LongPollingService longPollingService;
    private final RestaurantService restaurantService;

    public LongPollingController(UserService userService, OrderService orderService, ChatService chatService, LongPollingService longPollingService, RestaurantService restaurantService) {
        this.userService = userService;
        this.orderService = orderService;
        this.chatService = chatService;
        this.longPollingService = longPollingService;
        this.restaurantService = restaurantService;
    }

    @PostMapping("/login/{pageSize}")
    public UserDto login(@PathVariable("pageSize") int pageSize){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return initializeUser(userDetails.getUserModel(), pageSize);
    }

    @GetMapping(value = "/auth/getLoggedUser/{pageSize}")
    public UserDto getLoggedUser(@RequestParam("pageSize") int pageSize){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return initializeUser(userService.findById(loggedUser.getId()), pageSize);
    }

    @Transactional
    public UserDto initializeUser(UserModel user, int pageSize){
        Restaurant restaurant = user.getRestaurant();
        Map<Long, Order> orders = orderService.findNotReady(restaurant, 0, pageSize);
        RestaurantDto restaurantDto = new RestaurantDto(restaurant, orders);

        UserRequest userRequest = new UserRequest(user.getId(), restaurant, null);
        Map<Long, Chat> chats = chatService.findUserChats(user.getId(), pageSize);

        longPollingService.addRequest(userRequest);
        return new UserDto(user, restaurantDto, LocalDateTime.now(), chats);
    }

    @PostMapping(value = "/auth/waitData")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public DeferredResult<UserRequestDto> waitData(@RequestBody LocalDateTime lastCheck){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        DeferredResult<UserRequestDto> request = new DeferredResult<>(100_000L,"Time out.");

        UserRequest userRequest = new UserRequest(loggedUser.getId(), restaurantService.getById(loggedUser.getRestaurantId()), request, lastCheck);

        Runnable onTimeoutOrCompletion = ()-> userRequest.setRequest(null);
        request.onTimeout(onTimeoutOrCompletion);
        request.onCompletion(onTimeoutOrCompletion);

        longPollingService.setAndAddRequest(userRequest);

        return request;
    }
}
