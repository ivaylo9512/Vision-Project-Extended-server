package unit;

import com.vision.project.exceptions.DisabledUserException;
import com.vision.project.exceptions.UsernameExistsException;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.NewPasswordSpec;
import com.vision.project.models.specs.RegisterSpec;
import com.vision.project.models.specs.UserSpec;
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
    public void findById_WithNotEnabledUser() {
        UserModel user = new UserModel();
        user.setEnabled(false);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        DisabledUserException thrown = assertThrows(DisabledUserException.class,
                () -> userService.findById(1));

        assertEquals(thrown.getMessage(), "You must complete the registration. Check your email.");
    }

    @Test
    public void RegisterUser_WithAlreadyTakenUsername_UsernameExists() {
        UserModel user = new UserModel("test", "test@gmail.com", "password", "ROLE_USER");

        UserModel existingUser = new UserModel();
        existingUser.setUsername("test");

        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail())).thenReturn(existingUser);

        UsernameExistsException thrown = assertThrows(UsernameExistsException.class,
                () -> userService.create(user));

        assertEquals(thrown.getMessage(), "Username is already taken.");
    }

    @Test
    public void SuccessfulRegistration() {
        UserModel userModel = new UserModel("test", "testEmail@gmail.com","password", "ROLE_USER");

        when(userRepository.findByUsernameOrEmail(userModel.getUsername(), userModel.getEmail())).thenReturn(null);
        when(userRepository.save(userModel)).thenReturn(userModel);

        UserModel user = userService.create(userModel);

        assertEquals(user, userModel);
    }

    @Test
    public void ChangePassword(){
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails loggedUser = new UserDetails("TEST", "TEST", authorities, 2, 1);

        NewPasswordSpec passwordSpec = new NewPasswordSpec("user", "currentPassword", "newTestPassword");

        UserModel userModel = new UserModel();
        userModel.setPassword(BCrypt.hashpw(passwordSpec.getCurrentPassword(),BCrypt.gensalt(4)));
        userModel.setEnabled(true);

        when(userRepository.findById(2)).thenReturn(Optional.of(userModel));
        when(userRepository.save(userModel)).thenReturn(userModel);

        UserModel user = userService.changePassword(passwordSpec, loggedUser);
        assertTrue(BCrypt.checkpw("newTestPassword", user.getPassword()));

    }

    @Test
    public void ChangePassword_WithWrongPassword_BadCredentials(){
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails loggedUser = new UserDetails("TEST", "TEST", authorities, 2, 1);

        NewPasswordSpec passwordSpec = new NewPasswordSpec("user", "incorrect", "newTestPassword");

        UserModel userModel = new UserModel();
        userModel.setPassword(BCrypt.hashpw("currentPassword",BCrypt.gensalt(4)));
        userModel.setEnabled(true);

        when(userRepository.findById(2)).thenReturn(Optional.of(userModel));

        BadCredentialsException thrown = assertThrows(BadCredentialsException.class,
                () -> userService.changePassword(passwordSpec, loggedUser));

        assertEquals(thrown.getMessage(), "Invalid current password.");
    }

    @Test
    public void loadUserByUsername(){
        UserModel userModel = new UserModel(1, "username", "password", "ROLE_ADMIN", new Restaurant());

        UserDetails userDetails = new UserDetails(userModel, List.of(
                new SimpleGrantedAuthority(userModel.getRole())));

        when(userRepository.findByUsername("username")).thenReturn(userModel);

        UserDetails user = userService.loadUserByUsername("username");
        assertEquals(userDetails, user);
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

    @Test()
    public void changeUserInfo_WithNonExistentUser_ShouldThrow(){
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        UserSpec userSpec = new UserSpec();
        userSpec.setId(1);

        UserModel loggedUser = new UserModel(1, "username",
                "password", "ROLE_ADMIN", new Restaurant());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userService.changeUserInfo(userSpec, loggedUser)
        );

        assertEquals(thrown.getMessage(), "User not found.");
    }

    @Test()
    public void changeUserInfo_WithSameId(){
        UserSpec newUser = new UserSpec(1, "newUsername", "newUser@gmail.com", "firstName",
                "lastName", 25, "Country");

        UserModel oldUser = new UserModel(1, "username",
                "password", "ROLE_USER", new Restaurant());

        when(userRepository.findById(1)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(oldUser)).thenReturn(oldUser);
        when(userRepository.findByUsername("newUsername")).thenReturn(null);

        userService.changeUserInfo(newUser, oldUser);

        assertEquals(newUser.getUsername(), oldUser.getUsername());
        assertEquals(newUser.getFirstName(), oldUser.getFirstName());
        assertEquals(newUser.getLastName(), oldUser.getLastName());
        assertEquals(newUser.getCountry(), oldUser.getCountry());
        assertEquals(newUser.getAge(), oldUser.getAge());
    }

    @Test()
    public void changeUserInfo_WithAdmin(){
        UserSpec newUser = new UserSpec(1, "newUsername", "newUser@gmail.com", "firstName",
                "lastName", 25, "Country");

        UserModel oldUser = new UserModel();
        oldUser.setUsername("username");

        UserModel loggedUser = new UserModel(2, "username",
                "password", "ROLE_ADMIN", new Restaurant());

        when(userRepository.findById(1)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(oldUser)).thenReturn(oldUser);
        when(userRepository.findByUsername("newUsername")).thenReturn(null);

        userService.changeUserInfo(newUser, loggedUser);

        assertEquals(oldUser.getUsername(), newUser.getUsername());
        assertEquals(oldUser.getFirstName(), newUser.getFirstName());
        assertEquals(oldUser.getLastName(), newUser.getLastName());
        assertEquals(oldUser.getCountry(), newUser.getCountry());
        assertEquals(oldUser.getAge(), newUser.getAge());
    }

    @Test()
    public void changeUserInfo_WhenSameOldNewUsername(){
        UserSpec newUser = new UserSpec(1, "username", "newUser@gmail.com", "firstName", "lastName", 25, "Country");

        UserModel oldUser = new UserModel(1, "username",
                "password", "ROLE_USER", new Restaurant());

        when(userRepository.findById(1)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(oldUser)).thenReturn(oldUser);

        userService.changeUserInfo(newUser, oldUser);

        assertEquals(oldUser.getFirstName(), newUser.getFirstName());
        assertEquals(oldUser.getLastName(), newUser.getLastName());
        assertEquals(oldUser.getCountry(), newUser.getCountry());
        assertEquals(oldUser.getAge(), newUser.getAge());
    }

    @Test()
    public void changeUserInfo_WhenUsernameIsTaken(){
        UserSpec newUser = new UserSpec(1, "newUsername", "newUser@gmail.com", "firstName",
                "lastName", 25, "Country");

        UserModel oldUser = new UserModel(1, "username", "password", "ROLE_USER", new Restaurant());

        when(userRepository.findById(1)).thenReturn(Optional.of(oldUser));
        when(userRepository.findByUsername("newUsername")).thenReturn(new UserModel());

        UsernameExistsException thrown = assertThrows(
                UsernameExistsException.class,
                () -> userService.changeUserInfo(newUser, oldUser)
        );

        assertEquals(thrown.getMessage(), "Username is already taken.");
    }
}
