package unit;

import com.vision.project.controllers.FileController;
import com.vision.project.models.DTOs.FileDto;
import com.vision.project.models.File;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.services.FileServiceImpl;
import com.vision.project.services.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileControllerTest {
    @InjectMocks
    FileController fileController;

    @Mock
    FileServiceImpl fileService;

    @Mock
    UserServiceImpl userService;

    @Mock
    MockHttpServletRequest request;
    @Mock
    ServletContext servletContext;

    private final Restaurant restaurant = new Restaurant(1, "testName", "testAddress", "fast food", new ArrayList<>());
    private final UserModel userModel = new UserModel(1, "username", "email", "password", "ROLE_ADMIN", "firstName",
            "lastName", 25, "Bulgaria", restaurant);
    private final UserDetails user = new UserDetails(userModel, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    private final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getId());
    private final File file = new File("profileImage", 32_000, "image/png", "png", userModel);

    @Test
    public void delete(){
        UserModel owner = new UserModel();
        owner.setId(2);

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);


        when(userService.getById(2)).thenReturn(owner);
        when(userService.findById(1)).thenReturn(userModel);
        when(fileService.delete("profileImage", owner, userModel)).thenReturn(true);

        fileController.delete("profileImage", 2);

        verify(fileService, times(1)).delete("profileImage", owner, userModel);
    }

    @Test
    public void findByType(){
        when(userService.getById(1)).thenReturn(userModel);
        when(fileService.findByType("profileImage", userModel)).thenReturn(file);

        FileDto fileDto = fileController.findByType("profileImage", 1);

        assertEquals(fileDto.getOwnerId(), userModel.getId());
        assertEquals(fileDto.getResourceType(), file.getResourceType());
        assertEquals(fileDto.getSize(), file.getSize());
        assertEquals(fileDto.getType(), file.getType());
        assertEquals(fileDto.getExtension(), file.getExtension());
    }

    @Test
    public void getAsResource() throws IOException {
        Resource resource = new UrlResource("file", "/file1.txt");

        when(fileService.getAsResource("file1.txt")).thenReturn(resource);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getMimeType(resource.getFile().getAbsolutePath())).thenReturn("plain/text");

        ResponseEntity<Resource> response = fileController.getAsResource("file1.txt", request);

        assertEquals(response.getBody(), resource);
        assertEquals(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION), "attachment; filename=\"file1.txt\"");
    }
}