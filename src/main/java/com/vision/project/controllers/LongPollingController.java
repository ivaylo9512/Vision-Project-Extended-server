package com.vision.project.controllers;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.vision.project.exceptions.EmailExistsException;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import javax.servlet.http.HttpServletResponse;
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
    private final FileService fileService;
    private final RestaurantService restaurantService;

    public LongPollingController(UserService userService, OrderService orderService, ChatService chatService, LongPollingService longPollingService, FileService fileService, RestaurantService restaurantService) {
        this.userService = userService;
        this.orderService = orderService;
        this.chatService = chatService;
        this.longPollingService = longPollingService;
        this.fileService = fileService;
        this.restaurantService = restaurantService;
    }

    @PostMapping("/login/{pageSize}")
    public UserDto login(@RequestParam("pageSize") int pageSize){
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

    @PostMapping(value = "/register")
    public UserDto register(@RequestBody RegisterSpec registerSpec, HttpServletResponse response) {
        MultipartFile profileImage = registerSpec.getProfileImage();
        File file = null;

        if(profileImage != null){
            file = fileService.generate(profileImage,"logo", "image/png");
        }

        Restaurant restaurant = restaurantService.findByToken(registerSpec.getRestaurantToken());
        UserModel newUser = new UserModel(registerSpec, restaurant, "ROLE_USER");

        userService.create(newUser);

        if(file != null){
            fileService.save(file.getResourceType() + newUser.getId(), registerSpec.getProfileImage());
        }

        String token = Jwt.generate(new UserDetails(newUser, List.of(
                new SimpleGrantedAuthority("ROLE_USER"))));
        response.addHeader("Authorization", "Token " + token);

        return new UserDto(userService.save(newUser));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/auth/registerAdmin")
    public UserDto registerAdmin(@RequestBody RegisterSpec registerSpec, HttpServletResponse response) {
        MultipartFile profileImage = registerSpec.getProfileImage();
        File file = null;

        if(profileImage != null){
            file = fileService.generate(profileImage,"logo", "image/png");
        }

        Restaurant restaurant = restaurantService.findByToken(registerSpec.getRestaurantToken());
        UserModel newUser = new UserModel(registerSpec, restaurant, "ROLE_ADMIN");
        userService.create(newUser);

        if(file != null){
            fileService.save(file.getResourceType() + newUser.getId(), registerSpec.getProfileImage());
        }

        return new UserDto(newUser);
    }

    @Transactional
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
    public DeferredResult<UserRequestDto> waitData(@RequestBody LocalDateTime lastCheck){
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
    ResponseEntity<String> handleUsernameExistsException(UsernameExistsException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<String> handleEmailExistsException(EmailExistsException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
