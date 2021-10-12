package unit;

import com.vision.project.exceptions.InvalidRestaurantTokenException;
import com.vision.project.exceptions.UnauthorizedException;
import com.vision.project.models.Menu;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.RestaurantSpec;
import com.vision.project.repositories.base.RestaurantRepository;
import com.vision.project.services.RestaurantServiceImpl;
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

    @Test
    public void findById(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel userModel = new UserModel();
        userModel.setId(1);
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_USER");

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));

        Restaurant foundRestaurant = restaurantService.findById(1, userModel);

        assertEquals(restaurant, foundRestaurant);
    }

    @Test
    public void findById_WithNotFound(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel userModel = new UserModel();
        userModel.setId(1);
        userModel.setRole("ROLE_ADMIN");
        userModel.setRestaurant(restaurant);

        when(restaurantRepository.findById(1)).thenReturn(Optional.empty());

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
        Restaurant restaurant = new Restaurant();
        restaurant.setId(2);

        UserModel userModel = new UserModel();
        userModel.setId(1);
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_ADMIN");

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));

        Restaurant foundRestaurant = restaurantService.findById(1, userModel);

        assertEquals(restaurant, foundRestaurant);
    }

    @Test
    public void findByToken(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        when(restaurantRepository.findByToken("token")).thenReturn(Optional.of(restaurant));

        Restaurant foundRestaurant = restaurantService.findByToken("token");

        assertEquals(restaurant, foundRestaurant);
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
        List<Menu> menuList = new ArrayList<>();
        Restaurant restaurant = new Restaurant(1, "restaurant", "address", "type", menuList);
        restaurant.setId(1);

        Menu menu = new Menu("menu", restaurant);
        Menu menu1 = new Menu("menu1", restaurant);

        menuList.add(menu);
        menuList.add(menu1);

        RestaurantSpec restaurantSpec = new RestaurantSpec("restaurant", "type", "address", menuList);
        UUID uuid = UUID.randomUUID();

        try(MockedStatic<UUID> mocked = mockStatic(UUID.class)) {
            mocked.when(UUID::randomUUID).thenReturn(uuid);

            restaurantService.create(restaurant);

            verify(restaurantRepository, times(2)).save(restaurant);
            assertEquals(restaurant.getToken(), uuid.toString() + restaurant.getId());
            assertEquals(menu.getRestaurant(), restaurant);
            assertEquals(menu.getRestaurant(), restaurant);
            assertEquals(restaurant.getType(), restaurantSpec.getType());
            assertEquals(restaurant.getAddress(), restaurantSpec.getAddress());
            assertEquals(restaurant.getMenu(), menuList);
        }
    }

    @Test
    public void delete(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);

        UserModel userModel = new UserModel();
        userModel.setRestaurant(restaurant);

        restaurantService.delete(1, userModel);

        verify(restaurantRepository, times(1)).deleteById(1);
    }

    @Test
    public void delete_WithDifferentRestaurantAndRoleAdmin(){
        Restaurant restaurant = new Restaurant();
        restaurant.setId(2);

        UserModel userModel = new UserModel();
        userModel.setRestaurant(restaurant);
        userModel.setRole("ROLE_ADMIN");

        restaurantService.delete(1, userModel);

        verify(restaurantRepository, times(1)).deleteById(1);
    }
}
