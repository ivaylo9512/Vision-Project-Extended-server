package unit;

import com.vision.project.exceptions.UnauthorizedException;
import com.vision.project.models.Menu;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.MenuUpdateSpec;
import com.vision.project.repositories.base.MenuRepository;
import com.vision.project.services.MenuServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {
    @InjectMocks
    private MenuServiceImpl menuService;

    @Mock
    private MenuRepository menuRepository;

    @Test
    public void findById(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel userModel = new UserModel();
        userModel.setId(1);
        userModel.setRole("ROLE_USER");
        userModel.setRestaurant(restaurant);

        Menu menu = new Menu();
        menu.setRestaurant(restaurant);

        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));

        Menu foundMenu = menuService.findById(1, userModel);

        assertEquals(menu, foundMenu);
    }

    @Test
    public void findById_WithNotFound(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel userModel = new UserModel();
        userModel.setId(1);
        userModel.setRole("ROLE_ADMIN");
        userModel.setRestaurant(restaurant);

        when(menuRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> menuService.findById(1, userModel));

        assertEquals(thrown.getMessage(), "Menu not found.");
    }

    @Test
    public void findById_WithDifferentUserRestaurant_NotAdmin(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(2);

        UserModel userModel = new UserModel();
        userModel.setId(1);
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_USER");

        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(3);

        Menu menu = new Menu();
        menu.setId(2L);
        menu.setRestaurant(restaurant1);

        when(menuRepository.findById(2L)).thenReturn(Optional.of(menu));

        UnauthorizedException thrown = assertThrows(UnauthorizedException.class,
                () -> menuService.findById(2, userModel));

        assertEquals(thrown.getMessage(), "Unauthorized.");
    }

    @Test
    public void findById_WithDifferentUserRestaurant_Admin(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(2);

        UserModel userModel = new UserModel();
        userModel.setId(1);
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_ADMIN");

        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(3);

        Menu menu = new Menu();
        menu.setId(2L);
        menu.setRestaurant(restaurant1);

        when(menuRepository.findById(2L)).thenReturn(Optional.of(menu));

        Menu foundMenu = menuService.findById(2, userModel);

        assertEquals(foundMenu, menu);
    }

    @Test
    public void delete(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel userModel = new UserModel();
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_USER");

        Menu menu = new Menu();
        menu.setRestaurant(restaurant);

        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));

        menuService.delete(1, userModel);

        verify(menuRepository, times(1)).delete(menu);
    }

    @Test
    public void delete_WithNotFound(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel userModel = new UserModel();
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_USER");

        when(menuRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> menuService.delete(1, userModel));


        assertEquals(thrown.getMessage(), "Menu not found.");
    }

    @Test
    public void delete_WithDifferentUserRestaurant_NotAdmin(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(2);

        UserModel userModel = new UserModel();
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_USER");

        Restaurant restaurant1 = new Restaurant();
        restaurant.setId(2);

        Menu menu = new Menu();
        menu.setRestaurant(restaurant1);

        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));

        UnauthorizedException thrown = assertThrows(UnauthorizedException.class,
                () -> menuService.delete(1, userModel));


        assertEquals(thrown.getMessage(), "Unauthorized.");
    }

    @Test
    public void delete_WithDifferentUserRestaurant_Admin(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel userModel = new UserModel();
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_ADMIN");

        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(2);

        Menu menu = new Menu();
        menu.setRestaurant(restaurant1);

        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));

        menuService.delete(1, userModel);

        verify(menuRepository, times(1)).delete(menu);
    }

    @Test
    public void update(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel user = new UserModel();
        user.setRestaurant(restaurant);

        MenuUpdateSpec menuUpdateSpec = new MenuUpdateSpec("name", 1);
        menuUpdateSpec.setId(2);

        Menu menu = new Menu();

        when(menuRepository.findById(menuUpdateSpec.getId())).thenReturn(Optional.of(menu));
        when(menuRepository.save(menu)).thenReturn(menu);

        Menu savedMenu = menuService.update(menuUpdateSpec, user);

        verify(menuRepository, times(1)).save(menu);
        assertEquals(savedMenu.getName(), menuUpdateSpec.getName());
    }

    @Test
    public void update_WithNotFound(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel userModel = new UserModel();
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_USER");

        MenuUpdateSpec menuUpdateSpec = new MenuUpdateSpec("name", 1);
        menuUpdateSpec.setId(2);

        when(menuRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> menuService.update(menuUpdateSpec, userModel));

        assertEquals(thrown.getMessage(), "Menu not found.");
    }

    @Test
    public void update_WithDifferentUserRestaurant_NotAdmin(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel userModel = new UserModel();
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_USER");

        MenuUpdateSpec menuUpdateSpec = new MenuUpdateSpec("name", 2);
        menuUpdateSpec.setId(2);

        UnauthorizedException thrown = assertThrows(UnauthorizedException.class,
                () -> menuService.update(menuUpdateSpec, userModel));

        assertEquals(thrown.getMessage(), "Unauthorized.");
    }

    @Test
    public void update_WithDifferentUserRestaurant_Admin(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel user = new UserModel();
        user.setRestaurant(restaurant);
        user.setRole("ROLE_ADMIN");

        MenuUpdateSpec menuUpdateSpec = new MenuUpdateSpec("name", 2);
        menuUpdateSpec.setId(2);

        Menu menu = new Menu();

        when(menuRepository.findById(menuUpdateSpec.getId())).thenReturn(Optional.of(menu));
        when(menuRepository.save(menu)).thenReturn(menu);

        Menu savedMenu = menuService.update(menuUpdateSpec, user);

        verify(menuRepository, times(1)).save(menu);
        assertEquals(savedMenu.getName(), menuUpdateSpec.getName());
    }

    @Test
    public void create() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel user = new UserModel();
        user.setRestaurant(restaurant);
        user.setRole("ROLE_USER");

        Menu menu = new Menu();
        menu.setRestaurant(restaurant);

        when(menuRepository.save(menu)).thenReturn(menu);

        Menu savedMenu = menuService.create(menu, user);

        assertEquals(savedMenu, menu);
    }

    @Test
    public void create_WithDifferentUserRestaurant_NotAdmin() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel user = new UserModel();
        user.setRestaurant(restaurant);
        user.setRole("ROLE_USER");

        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(2);

        Menu menu = new Menu();
        menu.setRestaurant(restaurant1);

        UnauthorizedException thrown = assertThrows(UnauthorizedException.class,
                () -> menuService.create(menu, user));

        assertEquals(thrown.getMessage(), "Unauthorized.");
    }

    @Test
    public void create_WithDifferentUserRestaurant_Admin() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel user = new UserModel();
        user.setRestaurant(restaurant);
        user.setRole("ROLE_ADMIN");

        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(2);

        Menu menu = new Menu();
        menu.setRestaurant(restaurant1);

        when(menuRepository.save(menu)).thenReturn(menu);

        Menu savedMenu = menuService.create(menu, user);

        assertEquals(savedMenu, menu);
    }
}
