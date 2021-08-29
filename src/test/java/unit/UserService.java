package unit;

import com.vision.project.exceptions.UsernameExistsException;
import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.NewPasswordSpec;
import com.vision.project.models.specs.RegisterSpec;
import com.vision.project.repositories.base.UserRepository;
import com.vision.project.services.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;
import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserService {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void findById_withNonExistingUser_EntityNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> userService.findById(1));

        assertEquals(thrown.getMessage(), "User not found.");
    }


    @Test
    public void RegisterUser_WithAlreadyTakenUsername_UsernameExists() {
        RegisterSpec newRegistration = new RegisterSpec();
        newRegistration.setUsername("Test");
        UserModel registeredUserModel = new UserModel(newRegistration, "ROLE_USER");

        when(userRepository.findByUsername("Test")).thenReturn(registeredUserModel);

        UsernameExistsException thrown = assertThrows(UsernameExistsException.class,
                () -> userService.create(registeredUserModel));

        assertEquals(thrown.getMessage(), "Username is already taken.");
    }

    @Test
    public void SuccessfulRegistration() {
        RegisterSpec newRegistration = new RegisterSpec("Test", "testPassword", "testPassword");
        UserModel userModel = new UserModel(newRegistration, "ROLE_USER");

        when(userRepository.findByUsername("Test")).thenReturn(null);
        when(userRepository.save(userModel)).thenReturn(userModel);

        UserModel user = userService.create(userModel);

        assertEquals(user, userModel);
    }

    @Test
    public void ChangePassword(){
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails loggedUser = new UserDetails("TEST", "TEST", authorities, 2, 1);

        NewPasswordSpec passwordSpec = new NewPasswordSpec("user",
                "currentPassword", "newTestPassword", "newTestPassword");

        UserModel userModel = new UserModel();
        userModel.setPassword("currentPassword");

        when(userRepository.findById(2)).thenReturn(Optional.of(userModel));
        when(userRepository.save(userModel)).thenReturn(userModel);

        UserModel user = userService.changePassword(passwordSpec, loggedUser);
        assertTrue(BCrypt.checkpw("newTestPassword", user.getPassword()));

    }

    @Test
    public void ChangePassword_WithWrongPassword_BadCredentials(){
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails loggedUser = new UserDetails("TEST", "TEST", authorities, 2, 1);

        NewPasswordSpec passwordSpec = new NewPasswordSpec("user",
                "incorrect", "newTestPassword", "newTestPassword");

        UserModel userModel = new UserModel();
        userModel.setPassword("currentPassword");

        when(userRepository.findById(2)).thenReturn(Optional.of(userModel));

        BadCredentialsException thrown = assertThrows(BadCredentialsException.class,
                () -> userService.changePassword(passwordSpec, loggedUser));

        assertEquals(thrown.getMessage(), "Invalid current password.");
    }

    @Test
    public void loadUserByUsername(){
        UserModel foundUser = new UserModel();
        foundUser.setRole("ROLE_USER");
        foundUser.setUsername("username");

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new UserDetails(foundUser, authorities);

        when(userRepository.findByUsername("username")).thenReturn(foundUser);

        UserDetails loggedUser = userService.loadUserByUsername("username");

        assertEquals(userDetails, loggedUser);
        assertEquals(foundUser.getUsername(), loggedUser.getUsername());
    }

    @Test
    public void loadByUsername_WithNonExistentUsername_BadCredentials(){
        when(userRepository.findByUsername("username")).thenReturn(null);

        BadCredentialsException thrown = assertThrows(
                BadCredentialsException.class,
                () -> userService.loadUserByUsername("username")
        );

        assertEquals(thrown.getMessage(), "Bad credentials");
    }
}
