package unit;

import com.vision.project.controllers.UserController;
import com.vision.project.exceptions.UnauthorizedException;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.UserDto;
import com.vision.project.models.specs.NewPasswordSpec;
import com.vision.project.models.specs.RegisterSpec;
import com.vision.project.models.specs.UserSpec;
import com.vision.project.services.FileServiceImpl;
import com.vision.project.services.UserServiceImpl;
import com.vision.project.services.base.EmailTokenService;
import com.vision.project.services.base.RestaurantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserServiceImpl userService;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private EmailTokenService emailTokenService;

    @Mock
    private FileServiceImpl fileService;

    @InjectMocks
    @Spy
    private UserController userController;

    private final MockMultipartFile multipartFile = new MockMultipartFile("imageTest", "imageTest.png", "image/png", "imageTest".getBytes());
    private final Restaurant restaurant = new Restaurant(1, "testName", "testAddress", "fast food", new ArrayList<>());
    private final UserModel userModel = new UserModel(1, "username", "email", "password", "ROLE_ADMIN", "firstName", "lastName", 25, "Bulgaria", restaurant);
    private final File profileImage = new File("profileImage", 32_000, "image/png", "png", userModel);
    private final UserDetails user = new UserDetails(userModel, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    private final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getId());

    private void assertUser(UserDto userDto, UserModel userModel) {
        assertEquals(userDto.getId(), userModel.getId());
        assertEquals(userDto.getUsername(), userModel.getUsername());
        assertEquals(userDto.getEmail(), userModel.getEmail());
        assertEquals(userDto.getRole(), userModel.getRole());
        assertEquals(userDto.getCountry(), userModel.getCountry());
        assertEquals(userDto.getFirstName(), userModel.getFirstName());
        assertEquals(userDto.getLastName(), userModel.getLastName());
        assertEquals(userDto.getAge(), userModel.getAge());
    }

    @Test
    public void register() throws IOException, MessagingException {
        RegisterSpec register = new RegisterSpec("username", "email@gmail.com", "password", multipartFile, "firstName", "lastName", "Bulgaria", 25, "token");

        userModel.setProfileImage(profileImage);
        userModel.setRole("ROLE_USER");

        ArgumentCaptor<UserModel> captor = ArgumentCaptor.forClass(UserModel.class);
        when(userService.create(any(UserModel.class))).thenReturn(userModel);
        when(fileService.generate(multipartFile, "profileImage", "image")).thenReturn(profileImage);
        doNothing().when(emailTokenService).sendVerificationEmail(userModel);

        userController.register(register);

        verify(fileService, times(1)).save("profileImage1", multipartFile);
        verify(emailTokenService, times(1)).sendVerificationEmail(userModel);
        verify(userService).create(captor.capture());
        verify(restaurantService, times(1)).findByToken(register.getRestaurantToken());
        UserModel passedToCreate = captor.getValue();

        assertEquals(passedToCreate.getUsername(), userModel.getUsername());
        assertEquals(passedToCreate.getProfileImage(), profileImage);
        assertEquals(passedToCreate.getProfileImage().getOwner().getUsername(), userModel.getUsername());
        assertEquals(passedToCreate.getRole(), userModel.getRole());
    }

    @Test
    public void registerAdmin() throws IOException {
        RegisterSpec register = new RegisterSpec("username", "password", "email@gmail.com", multipartFile, "firstName", "lastName", "Bulgaria", 25, "token");

        userModel.setProfileImage(profileImage);
        userModel.setRole("ROLE_ADMIN");

        ArgumentCaptor<UserModel> captor = ArgumentCaptor.forClass(UserModel.class);
        when(userService.create(any(UserModel.class))).thenReturn(userModel);
        when(fileService.generate(multipartFile, "profileImage", "image")).thenReturn(profileImage);

        UserDto registeredUser = userController.registerAdmin(register);

        assertUser(registeredUser, userModel);

        verify(fileService, times(1)).save("profileImage1", multipartFile);
        verify(userService).create(captor.capture());
        verify(restaurantService, times(1)).findByToken(register.getRestaurantToken());
        UserModel passedToCreate = captor.getValue();

        assertEquals(passedToCreate.getUsername(), userModel.getUsername());
        assertEquals(passedToCreate.getProfileImage(), profileImage);
        assertEquals(passedToCreate.getProfileImage().getOwner().getUsername(), userModel.getUsername());
        assertEquals(passedToCreate.getRole(), userModel.getRole());
    }

    @Test
    public void activate() throws IOException {
        MockHttpServletResponse response = Mockito.spy(new MockHttpServletResponse());
        EmailToken token = new EmailToken();
        UserModel user = new UserModel();

        user.setEnabled(false);
        token.setExpiryDate(LocalDateTime.now().plusDays(1));
        token.setUser(user);

        when(emailTokenService.findByToken("token")).thenReturn(token);

        userController.activate("token", response);

        assertTrue(user.isEnabled());
        verify(userService, times(1)).save(user);
        verify(emailTokenService, times(1)).delete(token);
    }

    @Test
    public void activate_WithExpiredToken() {
        MockHttpServletResponse response = Mockito.spy(new MockHttpServletResponse());
        EmailToken token = new EmailToken();
        UserModel user = new UserModel();

        user.setEnabled(false);
        token.setExpiryDate(LocalDateTime.now().minusDays(1));
        token.setUser(user);

        when(emailTokenService.findByToken("token")).thenReturn(token);

        UnauthorizedException thrown = assertThrows(UnauthorizedException.class,
                () -> userController.activate("token", response));

        assertEquals(thrown.getMessage(), "Token has expired. Repeat your registration.");
        assertFalse(user.isEnabled());
        verify(userService, times(0)).save(user);
        verify(userService, times(1)).delete(user);
        verify(emailTokenService, times(1)).delete(token);
    }

    @Test
    public void findById(){
        when(userService.findById(1)).thenReturn(userModel);

        UserDto user = userController.findById(1);

        assertUser(user, userModel);
        verify(userService, times(1)).findById(1);
    }

    @Test
    public void delete(){
        userModel.setProfileImage(profileImage);
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userService.findById(user.getId())).thenReturn(userModel);

        userController.delete(user.getId());

        verify(userService, times(1)).delete(userModel);
        verify(fileService, times(1)).deleteFromSystem("profileImage" + userModel.getId() + "." + userModel.getProfileImage().getExtension());
    }

    @Test
    public void changeUserInfo() throws IOException {
        userModel.setProfileImage(profileImage);
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        MockMultipartFile newFile = new MockMultipartFile("imageTest", "imageTest.svg", "image/svg", "imageTest".getBytes());
        File file = new File();
        file.setExtension("svg");
        file.setResourceType("profileImage");

        UserSpec userSpec = new UserSpec();
        userSpec.setProfileImage(newFile);

        when(userService.findById(user.getId())).thenReturn(userModel);
        when(userService.changeUserInfo(userSpec, userModel)).thenReturn(userModel);
        when(fileService.generate(newFile, "profileImage", "image")).thenReturn(file);

        UserDto userDto = userController.changeUserInfo(userSpec);

        assertUser(userDto, userModel);

        verify(fileService, times(1)).save(file.getResourceType() + user.getId(), newFile);
        verify(fileService, times(1)).deleteFromSystem("profileImage" + user.getId() + "." + profileImage.getExtension());
    }

    @Test
    public void changeUserInfo_WithoutNewFile() throws IOException {
        userModel.setProfileImage(profileImage);
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserSpec userSpec = new UserSpec();

        when(userService.findById(user.getId())).thenReturn(userModel);
        when(userService.changeUserInfo(userSpec, userModel)).thenReturn(userModel);
        when(userController.generateFile(userSpec.getProfileImage(), userModel.getProfileImage())).thenReturn(null);

        UserDto userDto = userController.changeUserInfo(userSpec);

        assertUser(userDto, userModel);

        verify(fileService, times(0)).save(any(String.class), any(MultipartFile.class));
        verify(fileService, times(0)).deleteFromSystem(any(String.class));
    }

    @Test
    public void changeUserInfo_withNullImageName() throws IOException {
        userModel.setProfileImage(null);
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        MockMultipartFile newFile = new MockMultipartFile("imageTest", "imageTest.svg", "image/svg", "imageTest".getBytes());
        File file = new File();
        file.setExtension("svg");
        file.setResourceType("profileImage");

        UserSpec userSpec = new UserSpec();
        userSpec.setProfileImage(newFile);

        when(userService.findById(user.getId())).thenReturn(userModel);
        when(userService.changeUserInfo(userSpec, userModel)).thenReturn(userModel);
        when(fileService.generate(newFile, "profileImage", "image")).thenReturn(file);

        UserDto userDto = userController.changeUserInfo(userSpec);

        assertUser(userDto, userModel);

        verify(fileService, times(1)).save(file.getResourceType() + user.getId(), newFile);
        verify(fileService, times(0)).deleteFromSystem(any(String.class));
    }

    @Test
    public void changeUserInfo_withSameImageName() throws IOException {
        userModel.setProfileImage(profileImage);
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        MockMultipartFile newFile = new MockMultipartFile("imageTest", "imageTest.png", "image/png", "imageTest".getBytes());
        File file = new File();
        file.setExtension("png");
        file.setResourceType("profileImage");

        UserSpec userSpec = new UserSpec();
        userSpec.setProfileImage(newFile);

        when(userService.findById(user.getId())).thenReturn(userModel);
        when(userService.changeUserInfo(userSpec, userModel)).thenReturn(userModel);
        when(fileService.generate(newFile, "profileImage", "image")).thenReturn(file);

        UserDto userDto = userController.changeUserInfo(userSpec);

        assertUser(userDto, userModel);

        verify(fileService, times(1)).save(file.getResourceType() + user.getId(), newFile);
        verify(fileService, times(0)).deleteFromSystem(any(String.class));
    }

    @Test
    public void generateFile(){
        File file = new File();
        file.setId(5);

        File newFile = new File();

        when(fileService.generate(multipartFile, "profileImage", "image")).thenReturn(newFile);

        userController.generateFile(multipartFile, file);

        assertEquals(newFile.getId(), 5);
    }

    @Test
    public void generateFile_WithNullFile(){
        File newFile = new File();

        when(fileService.generate(multipartFile, "profileImage", "image")).thenReturn(newFile);

        userController.generateFile(multipartFile, null);

        assertEquals(newFile.getId(), 0);
    }

    @Test
    public void generateFile_WithNullMultipartFile(){
        File file = userController.generateFile(null, new File());

        assertNull(file);
    }

    @Test
    public void setEnabled(){
        userController.setEnable(true, 1);
        verify(userService, times(1)).setEnabled(true, 1);
    }

    @Test
    public void changePassword(){
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);
        NewPasswordSpec passwordSpec = new NewPasswordSpec("username", "password", "newPassword");

        when(userService.changePassword(passwordSpec, user)).thenReturn(userModel);

        UserDto userDto = userController.changePassword(passwordSpec);

        assertUser(userDto, userModel);
    }
}
