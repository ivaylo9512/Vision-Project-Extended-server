package unit;

import com.vision.project.exceptions.InvalidRestaurantTokenException;
import com.vision.project.exceptions.UnauthorizedException;
import com.vision.project.models.Menu;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserModel;
import com.vision.project.repositories.base.RestaurantRepository;
import com.vision.project.services.RestaurantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.persistence.EntityNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {
    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    @Mock
    private RestaurantRepository restaurantRepository;

    private Restaurant restaurant;

    @BeforeEach
    public void setup(){
        restaurant = new Restaurant(1, "testName", "testAddress", "fast food", new ArrayList<>());

        List<Menu> menu = List.of(new Menu(2, "Burger", restaurant), new Menu(5, "Juice", restaurant), new Menu(1, "Pizza", restaurant),
                new Menu(4, "Sushi", restaurant), new Menu(3, "Water", restaurant));
        restaurant.setMenu(menu);
    }

    @Test
    public void findById(){
        UserModel userModel = new UserModel();
        userModel.setId(1);
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_USER");

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        Restaurant foundRestaurant = restaurantService.findById(1, userModel);

        assertRestaurants(restaurant, foundRestaurant);
    }

    @Test
    public void findById_WithNotFound(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel userModel = new UserModel();
        userModel.setId(1);
        userModel.setRole("ROLE_ADMIN");
        userModel.setRestaurant(restaurant);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> restaurantService.findById(1, userModel));

        assertEquals(thrown.getMessage(), "Restaurant not found.");
    }


    @Test
    public void findById_WithDifferentUserRestaurant_NotAdmin(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(2);

        UserModel userModel = new UserModel();
        userModel.setId(1);
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_USER");

        UnauthorizedException thrown = assertThrows(UnauthorizedException.class,
                () -> restaurantService.findById(1, userModel));

        assertEquals(thrown.getMessage(), "Unauthorized.");
    }

    @Test
    public void findById_WithDifferentUserRestaurant_Admin(){
        UserModel userModel = new UserModel();
        userModel.setId(1);
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_ADMIN");

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        Restaurant foundRestaurant = restaurantService.findById(1, userModel);

        assertRestaurants(restaurant, foundRestaurant);
    }

    @Test
    public void findByToken(){
        when(restaurantRepository.findByToken("token")).thenReturn(Optional.of(restaurant));

        Restaurant foundRestaurant = restaurantService.findByToken("token");

        assertRestaurants(restaurant, foundRestaurant);
    }

    @Test
    public void findByToken_WithNotFound(){
        when(restaurantRepository.findByToken("token")).thenReturn(Optional.empty());

        InvalidRestaurantTokenException thrown = assertThrows(InvalidRestaurantTokenException.class,
                () -> restaurantService.findByToken("token"));

        assertEquals(thrown.getMessage(), "Restaurant token is invalid.");
    }

    @Test
    public void create() {
        UUID uuid = UUID.randomUUID();

        try(MockedStatic<UUID> mocked = mockStatic(UUID.class)) {
            mocked.when(UUID::randomUUID).thenReturn(uuid);

            Restaurant savedRestaurant = restaurantService.create(restaurant);

            verify(restaurantRepository, times(2)).save(restaurant);
            assertRestaurants(restaurant, savedRestaurant);
        }
    }

    @Test
    public void delete(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel userModel = new UserModel();
        userModel.setRestaurant(restaurant);

        restaurantService.delete(1, userModel);

        verify(restaurantRepository, times(1)).deleteById(1L);
    }

    @Test
    public void delete_DifferentUserRestaurant_Admin(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(2);

        UserModel userModel = new UserModel();
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_ADMIN");

        restaurantService.delete(1, userModel);

        verify(restaurantRepository, times(1)).deleteById(1L);
    }

    @Test
    public void delete_DifferentUserRestaurant_NotAdmin(){
        UserModel userModel = new UserModel();
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_USER");

        UnauthorizedException thrown = assertThrows(UnauthorizedException.class,
                () -> restaurantService.delete(3, userModel));
    }

    @Test
    public void getById(){
        when(restaurantRepository.getById(restaurant.getId())).thenReturn(restaurant);

        Restaurant foundRestaurant =  restaurantService.getById(restaurant.getId());

        assertRestaurants(foundRestaurant, restaurant);
    }

    private void assertRestaurants(Restaurant restaurant, Restaurant restaurant1){
        assertEquals(restaurant.getId(), restaurant1.getId());
        assertEquals(restaurant.getAddress(), restaurant1.getAddress());
        assertEquals(restaurant.getToken(), restaurant1.getToken());
        assertEquals(restaurant.getType(), restaurant1.getType());
        assertEquals(restaurant.getName(), restaurant1.getName());
        assertMenu(restaurant.getMenu(), restaurant1.getMenu());
    }

    private void assertMenu(List<Menu> menuList, List<Menu> menuList1){
        for (int i = 0; i < menuList.size(); i++) {
            Menu menu = menuList.get(i);
            Menu menu1 = menuList1.get(i);

            assertEquals(menu.getName(), menu1.getName());
            assertEquals(menu.getId(), menu1.getId());
            assertEquals(menu.getRestaurant(), menu1.getRestaurant());
        }
    }
}
