package com.vision.project.controllers;

import com.vision.project.exceptions.EmailExistsException;
import com.vision.project.exceptions.UnauthorizedException;
import com.vision.project.exceptions.UsernameExistsException;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.UserDto;
import com.vision.project.models.specs.NewPasswordSpec;
import com.vision.project.models.specs.RegisterSpec;
import com.vision.project.models.specs.UserSpec;
import com.vision.project.services.base.EmailTokenService;
import com.vision.project.services.base.FileService;
import com.vision.project.services.base.RestaurantService;
import com.vision.project.services.base.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {
    private final UserService userService;
    private final FileService fileService;
    private final RestaurantService restaurantService;
    private final EmailTokenService emailTokenService;

    public UserController(UserService userService, FileService fileService, RestaurantService restaurantService, EmailTokenService emailTokenService) {
        this.userService = userService;
        this.fileService = fileService;
        this.restaurantService = restaurantService;
        this.emailTokenService = emailTokenService;
    }

    @GetMapping(value = "/findById/{id}")
    public UserDto findById(@PathVariable(name = "id") int id){
        return new UserDto(userService.findById(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(value = "/auth/setEnabled/{state}/{id}")
    public void setEnable(@PathVariable(name = "state") boolean state,
                          @PathVariable(name = "id") int id){
        userService.setEnabled(state, id);
    }

    @PostMapping(value = "/register")
    public void register(@Valid @ModelAttribute RegisterSpec registerSpec) throws IOException, MessagingException {
        MultipartFile profileImage = registerSpec.getProfileImage();
        File file = null;

        if(profileImage != null){
            file = fileService.generate(profileImage,"profileImage", "image");
        }

        Restaurant restaurant = restaurantService.findByToken(registerSpec.getRestaurantToken());

        UserModel newUser = userService.create(new UserModel(registerSpec, file, restaurant, "ROLE_USER"));

        if(file != null){
            System.out.println(newUser.getId());
            fileService.save(file.getResourceType() + newUser.getId(), registerSpec.getProfileImage());
        }

        emailTokenService.sendVerificationEmail(newUser);
    }

    @GetMapping(value = "/activate/{token}")
    public void activate(@PathVariable("token") String token, HttpServletResponse httpServletResponse) throws IOException {
        EmailToken emailToken = emailTokenService.getToken(token);
        UserModel user = emailToken.getUser();

        if(emailToken.getExpiryDate().isBefore(LocalDateTime.now())){
            emailTokenService.delete(emailToken);
            userService.delete(emailToken.getUser());

            throw new UnauthorizedException("Token has expired. Repeat your registration.");
        }

        user.setEnabled(true);

        userService.save(user);
        emailTokenService.delete(emailToken);

        httpServletResponse.sendRedirect("https://localhost:4200");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/auth/registerAdmin")
    public UserDto registerAdmin(@Valid @ModelAttribute RegisterSpec registerSpec) throws IOException {
        MultipartFile profileImage = registerSpec.getProfileImage();
        File file = null;

        if(profileImage != null){
            file = fileService.generate(profileImage,"profileImage", "image");
        }

        Restaurant restaurant = restaurantService.findByToken(registerSpec.getRestaurantToken());
        UserModel newUser = userService.create(new UserModel(registerSpec, file, restaurant, "ROLE_ADMIN"));
        newUser.setEnabled(true);

        if(file != null){
            fileService.save(file.getResourceType() + newUser.getId(), registerSpec.getProfileImage());
        }

        return new UserDto(newUser);
    }

    @PatchMapping(value = "/auth/changePassword")
    public UserDto changePassword(@Valid @RequestBody NewPasswordSpec newPasswordSpec){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return new UserDto(userService.changePassword(newPasswordSpec, loggedUser));
    }

    @PatchMapping(value = "/auth/changeUserInfo")
    public UserDto changeUserInfo(@Valid @RequestBody UserSpec userModel){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return new UserDto(userService.changeUserInfo(userModel, userService.findById(loggedUser.getId())));
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
