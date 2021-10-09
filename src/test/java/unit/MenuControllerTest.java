package unit;

import com.vision.project.controllers.MenuController;
import com.vision.project.models.DTOs.MenuDto;
import com.vision.project.models.Menu;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.MenuCreateSpec;
import com.vision.project.models.specs.MenuUpdateSpec;
import com.vision.project.services.base.MenuService;
import com.vision.project.services.base.RestaurantService;
import com.vision.project.services.base.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuControllerTest {
    @InjectMocks
    private MenuController menuController;

    @Mock
    private MenuService menuService;

    @Mock
    private UserService userService;

    @Mock
    private RestaurantService restaurantService;

    private final Restaurant restaurant = new Restaurant(1, "testName", "testAddress", "fast food", new HashSet<>());
    private final UserModel userModel = new UserModel(1, "username", "email", "password", "ROLE_ADMIN", "firstName", "lastName", 25, "Bulgaria", restaurant);
    private final UserDetails user = new UserDetails(userModel, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    private final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getId());

    @Test
    public void create(){
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        MenuCreateSpec menuCreateSpec = new MenuCreateSpec("name", restaurant.getId());

        Menu menu = new Menu(menuCreateSpec.getName(), restaurant);

        when(userService.findById(user.getId())).thenReturn(userModel);
        when(restaurantService.findById(restaurant.getId(), userModel)).thenReturn(restaurant);
        when(menuService.create(eq(menu), eq(userModel))).thenReturn(menu);

        MenuDto menuDto = menuController.create(menuCreateSpec);

        assertEquals(menuDto.getName(), menu.getName());
        assertEquals(menuDto.getRestaurantId(), menu.getRestaurant().getId());
    }

    @Test
    public void update(){
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        MenuUpdateSpec menuUpdateSpec = new MenuUpdateSpec("name", restaurant.getId());
        menuUpdateSpec.setId(1);
        Menu menu = new Menu(menuUpdateSpec.getName(), restaurant);
        menu.setId(1);

        when(userService.findById(user.getId())).thenReturn(userModel);
        when(menuService.update(menuUpdateSpec, userModel)).thenReturn(menu);

        MenuDto menuDto = menuController.update(menuUpdateSpec);

        assertEquals(menuDto.getName(), menu.getName());
        assertEquals(menuDto.getRestaurantId(), menu.getRestaurant().getId());
        assertEquals(menuDto.getId(), menu.getId());
    }

    @Test
    public void delete(){
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userService.findById(user.getId())).thenReturn(userModel);

        menuController.delete(1);

        verify(menuService, times(1)).delete(1, userModel);
    }
}
