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
import javax.transaction.Transactional;
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
        File file = generateFile(registerSpec.getProfileImage(), null);

        Restaurant restaurant = restaurantService.findByToken(registerSpec.getRestaurantToken());

        UserModel newUser = userService.create(new UserModel(registerSpec, file, restaurant, "ROLE_USER"));

        if(file != null){
            fileService.save(file.getResourceType() + newUser.getId(), registerSpec.getProfileImage());
        }

        emailTokenService.sendVerificationEmail(newUser);
    }

    @GetMapping(value = "/activate/{token}")
    public void activate(@PathVariable("token") String token, HttpServletResponse httpServletResponse) throws IOException {
        EmailToken emailToken = emailTokenService.findByToken(token);
        UserModel user = emailToken.getUser();

        if(emailToken.getExpiryDate().isBefore(LocalDateTime.now())){
            emailTokenService.delete(emailToken);
            userService.delete(emailToken.getUser());

            throw new UnauthorizedException("Token has expired. Repeat your registration.");
        }

        user.setEnabled(true);

        userService.save(user);
        emailTokenService.delete(emailToken);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/auth/registerAdmin")
    @Transactional
    public UserDto registerAdmin(@Valid @ModelAttribute RegisterSpec registerSpec) throws IOException {
        File file = generateFile(registerSpec.getProfileImage(), null);

        Restaurant restaurant = restaurantService.findByToken(registerSpec.getRestaurantToken());
        UserModel user = new UserModel(registerSpec, file, restaurant, "ROLE_ADMIN");
        user.setEnabled(true);

        UserModel newUser = userService.create(user);

        if(file != null){
            fileService.save(file.getResourceType() + newUser.getId(), registerSpec.getProfileImage());
        }

        return new UserDto(newUser);
    }

    @DeleteMapping(value = "/auth/delete/{id}")
    public void delete(@PathVariable("id") int id){
        UserDetails loggedUser = (UserDetails)SecurityContextHolder
                .getContext().getAuthentication().getDetails();

        UserModel user = userService.findById(id);
        String fileName = "profileImage" + id + "." + user.getProfileImage().getExtension();

        userService.delete(user);
        fileService.deleteFromSystem(fileName);
    }

    @PatchMapping(value = "/auth/changePassword")
    @Transactional
    public UserDto changePassword(@Valid @RequestBody NewPasswordSpec newPasswordSpec){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return new UserDto(userService.changePassword(newPasswordSpec, loggedUser));
    }

    @PostMapping(value = "/auth/changeUserInfo")
    public UserDto changeUserInfo(@Valid @ModelAttribute UserSpec userSpec) throws IOException {
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        UserModel user = userService.findById(loggedUser.getId());

        String imageName = user.getProfileImage() != null
                ? "profileImage" + loggedUser.getId() + "." + user.getProfileImage().getExtension() : null;

        File file = generateFile(userSpec.getProfileImage(), user.getProfileImage());
        user.setProfileImage(file == null ? user.getProfileImage() : file);

        UserModel newUser = userService.changeUserInfo(userSpec, user);

        if(file != null){
            fileService.save(file.getResourceType() + newUser.getId(), userSpec.getProfileImage());

            if(imageName != null && !imageName.equals("profileImage" + newUser.getId() + "." + file.getExtension())){
                fileService.deleteFromSystem(imageName);
            }
        }

        return new UserDto(newUser);
    }

    public File generateFile(MultipartFile profileImage, File oldImage){
        if(profileImage == null){
            return null;
        }

        File file = fileService.generate(profileImage,"profileImage", "image");
        file.setId(oldImage != null ? oldImage.getId() : 0);

        return file;
    }

    @ExceptionHandler
    ResponseEntity<String> handleUsernameExistsException(UsernameExistsException e) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(e.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<String> handleEmailExistsException(EmailExistsException e) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(e.getMessage());
    }
}
