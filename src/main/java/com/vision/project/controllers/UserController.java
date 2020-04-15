package com.vision.project.controllers;


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
import com.vision.project.services.base.LongPollingService;
import com.vision.project.services.base.OrderService;
import com.vision.project.services.base.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final LongPollingService longPollingService;

    public UserController(UserService userService, OrderService orderService, LongPollingService longPollingService) {
        this.userService = userService;
        this.orderService = orderService;
        this.longPollingService = longPollingService;
    }

    @PostMapping("/login")
    @Transactional
    public UserDto login(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Restaurant restaurant = userDetails.getRestaurant();
        restaurant.setOrders(orderService.findAllNotReady(restaurant));

        UserRequest userRequest = new UserRequest(userDetails.getId(), userDetails.getRestaurantId());

        longPollingService.addRequest(userRequest);

        return new UserDto(userDetails);
    }

    @PostMapping(value = "/register")
    public UserDto register(@RequestBody UserSpec user, HttpServletResponse response) {
        // disabling the registration
        try{

            UserDetails loggedUser = (UserDetails)SecurityContextHolder
                    .getContext().getAuthentication().getDetails();
        }catch (Exception e){
            throw new RegistrationIsDisabled("Registration is disabled. Only the admin can register!");
        }
        //

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

    @GetMapping(value = "/auth/getLoggedUser")
    public UserDto getLoggedUser(){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        UserModel user = userService.findById(loggedUser.getId());
        Restaurant restaurant = user.getRestaurant();
        restaurant.setOrders(orderService.findAllNotReady(restaurant));

        UserRequest userRequest = new UserRequest(loggedUser.getId(), loggedUser.getRestaurantId());

        longPollingService.addRequest(userRequest);

        return new UserDto(user, restaurant);
    }

    @PostMapping(value = "auth/changeUserInfo")
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
