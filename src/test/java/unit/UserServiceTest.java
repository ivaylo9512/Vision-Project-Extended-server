package unit;

import com.vision.project.exceptions.DisabledUserException;
import com.vision.project.exceptions.EmailExistsException;
import com.vision.project.exceptions.UnauthorizedException;
import com.vision.project.exceptions.UsernameExistsException;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.NewPasswordSpec;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test()
    public void findById() {
        UserModel user = new UserModel("Test", "Test", "ROLE_ADMIN");
        user.setEnabled(true);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.findById(1);

        verify(userRepository, times(1)).findById(1);
    }
    @Test
    public void findById_withNonExistingUser() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> userService.findById(1));

        assertEquals(thrown.getMessage(), "UserModel not found.");
    }

    @Test
    public void register_WithAlreadyTakenUsername() {
        UserModel user = new UserModel("test", "test@gmail.com", "password", "ROLE_USER");

        UserModel existingUser = new UserModel();
        existingUser.setUsername("test");

        when(userRepository.findFirstByUsernameOrEmail(user.getUsername(), user.getEmail())).thenReturn(Optional.of(existingUser));

        UsernameExistsException thrown = assertThrows(UsernameExistsException.class,
                () -> userService.create(user));

        assertEquals(thrown.getMessage(), "{ \"username\": \"Username is already taken.\" }");
    }

    @Test
    public void register_WithAlreadyTakenEmail() {
        UserModel existingUser = new UserModel("test", "test@gmail.com", "test", "ROLE_ADMIN");
        UserModel user = new UserModel("nonexistent", "test@gmail.com", "test", "ROLE_ADMIN");

        when(userRepository.findFirstByUsernameOrEmail(user.getUsername(), user.getEmail())).thenReturn(Optional.of(existingUser));

        EmailExistsException thrown = assertThrows(EmailExistsException.class,
                () -> userService.create(user)
        );

        assertEquals(thrown.getMessage(), "{ \"email\": \"Email is already taken.\" }");
    }

    @Test
    public void register() {
        UserModel userModel = new UserModel("test", "testEmail@gmail.com","password", "ROLE_USER");

        when(userRepository.findFirstByUsernameOrEmail(userModel.getUsername(), userModel.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(userModel)).thenReturn(userModel);

        UserModel user = userService.create(userModel);

        assertEquals(user, userModel);
    }

    @Test
    public void registerAdmin() {
        UserModel user = new UserModel("test", "test@gmail.com", "test", "ROLE_ADMIN");

        when(userRepository.findFirstByUsernameOrEmail(user.getUsername(), user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        UserModel registeredUser = userService.create(user);

        assertEquals(registeredUser.getRole(),"ROLE_ADMIN");
    }

    @Test
    public void changePassword(){
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails loggedUser = new UserDetails("test", "test", authorities, 2, 1);

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
    public void changePassword_WithWrongPassword(){
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
    public void changePasswordState_WithNonExistentUser(){
        NewPasswordSpec passwordSpec = new NewPasswordSpec("username",
                "current", "newTestPassword");

        UserModel userModel = new UserModel(1, "username", "current", "ROLE_ADMIN", new Restaurant());

        UserDetails userDetails = new UserDetails(userModel, List.of(
                new SimpleGrantedAuthority(userModel.getRole())));

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userService.changePassword(passwordSpec, userDetails)
        );

        assertEquals(thrown.getMessage(), "UserModel not found.");
    }

    @Test
    public void loadByUsername(){
        UserModel userModel = new UserModel(1, "username", "password", "ROLE_ADMIN", new Restaurant());
        userModel.setEnabled(true);

        UserDetails userDetails = new UserDetails(userModel, List.of(
                new SimpleGrantedAuthority(userModel.getRole())));

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(userModel));

        UserDetails user = userService.loadUserByUsername("username");
        assertEquals(userDetails, user);
    }

    @Test
    public void loadUserByUsername_WithNotEnabledUser() {
        UserModel user = new UserModel();
        user.setEnabled(false);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        DisabledUserException thrown = assertThrows(DisabledUserException.class,
                () -> userService.loadUserByUsername("username"));

        assertEquals(thrown.getMessage(), "You must complete the registration. Check your email.");
    }

    @Test
    public void loadByUsername_WithNonExistentUsername(){
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        BadCredentialsException thrown = assertThrows(
                BadCredentialsException.class,
                () -> userService.loadUserByUsername("username")
        );

        assertEquals(thrown.getMessage(), "Bad credentials.");
    }

    @Test()
    public void changeUserInfo(){
        UserSpec newUser = new UserSpec(1, "newUsername", "newEmail@gmail.com", "firstName",
                "lastName", 25, "Country");

        UserModel oldUser = new UserModel("oldUsername", "email@gmail.com", "password", "ROLE_USER");
        oldUser.setId(1);

        when(userRepository.save(oldUser)).thenReturn(oldUser);
        when(userRepository.findFirstByUsernameOrEmail(newUser.getUsername(), newUser.getEmail())).thenReturn(Optional.empty());

        userService.changeUserInfo(newUser, oldUser);

        assertEquals(oldUser.getUsername(), newUser.getUsername());
        assertEquals(oldUser.getEmail(), newUser.getEmail());
        assertEquals(oldUser.getFirstName(), newUser.getFirstName());
        assertEquals(oldUser.getLastName(), newUser.getLastName());
        assertEquals(oldUser.getCountry(), newUser.getCountry());
        assertEquals(oldUser.getAge(), newUser.getAge());
    }

    @Test()
    public void changeUserInfo_WhenUsernameIsTaken(){
        UserSpec newUser = new UserSpec(1, "username", "email@gmail.com", "firstName",
                "lastName", 25, "Country");

        UserModel oldUser = new UserModel("oldUsername", "email@gmail.com", "password", "ROLE_USER");
        oldUser.setId(1);

        UserModel existingUser = new UserModel();
        existingUser.setId(2);
        existingUser.setUsername("username");

        when(userRepository.findFirstByUsernameOrEmail(newUser.getUsername(), null)).thenReturn(Optional.of(existingUser));

        UsernameExistsException thrown = assertThrows(
                UsernameExistsException.class,
                () -> userService.changeUserInfo(newUser, oldUser)
        );

        assertEquals(thrown.getMessage(), "{ \"username\": \"Username is already taken.\" }");
    }

    @Test()
    public void changeUserInfo_WhenUsernameAndEmailsAreTheSame(){
        UserSpec newUser = new UserSpec(1, "oldUsername", "email@gmail.com", "firstName",
                "lastName", 25, "Country");

        UserModel oldUser = new UserModel("oldUsername", "email@gmail.com", "password", "ROLE_USER");
        oldUser.setId(1);

        when(userRepository.save(oldUser)).thenReturn(oldUser);

        userService.changeUserInfo(newUser, oldUser);

        assertEquals(oldUser.getUsername(), newUser.getUsername());
        assertEquals(oldUser.getEmail(), newUser.getEmail());
        assertEquals(oldUser.getFirstName(), newUser.getFirstName());
        assertEquals(oldUser.getLastName(), newUser.getLastName());
        assertEquals(oldUser.getCountry(), newUser.getCountry());
        assertEquals(oldUser.getAge(), newUser.getAge());

        verify(userRepository, times(0)).findFirstByUsernameOrEmail("username", "email@gmail.com");
    }

    @Test()
    public void changeUserInfo_WhenEmailIsTaken(){
        UserSpec newUser = new UserSpec(1, "oldUsername", "taken@gmail.com", "firstName",
                "lastName", 25, "Country");

        UserModel oldUser = new UserModel("oldUsername", "email@gmail.com", "password", "ROLE_USER");
        oldUser.setId(1);

        UserModel existingUser = new UserModel();
        existingUser.setId(2);
        existingUser.setUsername("username");
        existingUser.setUsername("taken@gmail.com");

        when(userRepository.findFirstByUsernameOrEmail(null, newUser.getEmail())).thenReturn(Optional.of(existingUser));

        EmailExistsException thrown = assertThrows(EmailExistsException.class,
                () -> userService.changeUserInfo(newUser, oldUser)
        );

        assertEquals(thrown.getMessage(), "{ \"email\": \"Email is already taken.\" }");
    }

    @Test()
    public void setEnabled(){
        UserModel user = new UserModel();
        user.setEnabled(false);
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.setEnabled(true, 1);

        assertTrue(user.isEnabled());
        verify(userRepository, times(1)).save(user);
    }

    @Test()
    public void setEnabled_withNonExistent(){
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> userService.setEnabled(true, 1));

        assertEquals(thrown.getMessage(), "UserModel not found.");
    }

    @Test()
    public void delete_WithNonExistentUsername(){
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> userService.delete(1, any(UserDetails.class))
        );

        assertEquals(thrown.getMessage(), "UserModel not found.");
    }

    @Test()
    public void delete_WithDifferentLoggedUser_ThatIsNotAdmin(){
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new UserDetails("username", "password", authorities, 2, 1);

        when(userRepository.findById(1)).thenReturn(Optional.of(new UserModel()));

        UnauthorizedException thrown = assertThrows(UnauthorizedException.class,
                () -> userService.delete(1, userDetails)
        );

        assertEquals(thrown.getMessage(), "You are not allowed to modify the user.");
    }

    @Test
    public void delete_WithDifferentLoggedId_ThatIsAdmin(){
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"));
        UserDetails userDetails = new UserDetails("username", "password", authorities, 2, 1);

        when(userRepository.findById(1)).thenReturn(Optional.of(new UserModel()));

        userService.delete(1, userDetails);
    }

    @Test
    public void delete_WithSameLoggedId(){
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new UserDetails("username", "password", authorities, 1, 1);
        when(userRepository.findById(1)).thenReturn(Optional.of(new UserModel()));

        userService.delete(1, userDetails);
    }
}
