package com.vision.project.controllers;


import com.vision.project.exceptions.PasswordsMissMatchException;
import com.vision.project.exceptions.RegistrationIsDisabled;
import com.vision.project.exceptions.UsernameExistsException;
import com.vision.project.models.DTOs.UserDto;
import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.UserSpec;
import com.vision.project.security.Jwt;
import com.vision.project.services.base.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping(value = "users/register")
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
    @PostMapping(value = "auth/users/adminRegistration")
    public UserDto registerAdmin(@Valid UserSpec user){
        return new UserDto(userService.register(user,"ROLE_USER"));
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
