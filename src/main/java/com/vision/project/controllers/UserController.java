package com.vision.project.controllers;


import com.vision.project.exceptions.PasswordsMissMatchException;
import com.vision.project.exceptions.RegistrationIsDisabled;
import com.vision.project.exceptions.UsernameExistsException;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.RestaurantDto;
import com.vision.project.models.DTOs.UserDto;
import com.vision.project.models.specs.UserSpec;
import com.vision.project.security.Jwt;
import com.vision.project.services.base.ChatService;
import com.vision.project.services.base.OrderService;
import com.vision.project.services.base.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final ChatService chatService;

    public UserController(UserService userService, OrderService orderService, ChatService chatService) {
        this.userService = userService;
        this.orderService = orderService;
        this.chatService = chatService;
    }

    @GetMapping(value = "/auth/getLoggedUser/{pageSize}")
    public UserDto getLoggedUser(@PathVariable("pageSize") int pageSize){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return initializeUser(userService.findById(loggedUser.getId()), pageSize);
    }

    @PostMapping("/login")
    @Transactional
    public UserDto login(@RequestParam("pageSize") int pageSize){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return initializeUser(userDetails.getUserModel(), pageSize);
    }

    private UserDto initializeUser(UserModel userModel, int pageSize){
        Restaurant restaurant = userModel.getRestaurant();
        Map<Integer, Order> orders = orderService.findNotReady(restaurant.getId(), 0, pageSize);
        RestaurantDto restaurantDto = new RestaurantDto(restaurant, orders);
        Map<Integer, Chat> chats = chatService.findUserChats(userModel.getId(), pageSize);

        return new UserDto(userModel, restaurantDto, chats);
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
        return new UserDto(userModel);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/auth/adminRegistration")
    public UserDto registerAdmin(@Valid UserSpec user){
        return new UserDto(userService.register(user,"ROLE_USER"));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/auth/findById/{id}")
    public UserDto findById(@PathVariable(name = "id") int id){
        return new UserDto(userService.findById(id));
    }

    @PostMapping(value = "/auth/changeUserInfo")
    public UserDto changeUserInfo(@RequestBody UserSpec userModel){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return new UserDto(userService.changeUserInfo(loggedUser.getId(), userModel));
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

    @ExceptionHandler
    ResponseEntity handlePasswordsMissMatchException(PasswordsMissMatchException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
