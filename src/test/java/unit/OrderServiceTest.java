package unit;

import com.vision.project.models.Dish;
import com.vision.project.models.Order;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserModel;
import com.vision.project.repositories.base.OrderRepository;
import com.vision.project.services.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    private Order order;

    private final Restaurant restaurant = new Restaurant(1, "testName", "testAddress", "fast food", new ArrayList<>());
    private final UserModel userModel = new UserModel(1, "username", "email", "password", "ROLE_ADMIN", "firstName", "lastName", 25, "Bulgaria", restaurant);

    @BeforeEach
    public void setup(){
        Dish dish = new Dish("name", true, userModel, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Dish dish1 = new Dish("name1", false, userModel, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3));

        order = new Order(1, List.of(dish, dish1), LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(5), restaurant);
        order.setUser(userModel);

        dish.setOrder(order);
        dish1.setOrder(order);
    }

    @Test
    public void create(){
        when(orderRepository.save(order)).thenReturn(order);

        Order savedOrder = orderService.create(order);

        assertOrders(savedOrder, order);
    }

    @Test
    public void update_WithNotFound(){
        Order order = new Order();
        order.setId(2);

        when(orderRepository.findByIdAndRestaurant(order.getId(), restaurant)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> orderService.update(order.getId(), 3, restaurant, userModel));

        assertEquals(thrown.getMessage(), "Order not found.");
    }

    @Test
    public void update(){
        order.getDishes().get(1).setReady(true);
        Dish dish = order.getDishes().get(0);
        dish.setId(4);
        dish.setReady(false);

        order.setReady(false);

        when(orderRepository.findByIdAndRestaurant(order.getId(), restaurant)).thenReturn(Optional.of(order));

        Dish savedDish = orderService.update(order.getId(), dish.getId(), restaurant, userModel);

        verify(orderRepository, times(1)).save(order);

        assertEquals(savedDish.getId(), 4);
        assertEquals(savedDish.getName(), dish.getName());
        assertTrue(savedDish.isReady());
        assertTrue(order.isReady());
        assertEquals(dish.getUpdatedBy(), userModel);
    }

    @Test
    public void update_WithAlreadyUpdated(){
        UserModel updatedBy = new UserModel();
        Dish dish = order.getDishes().get(0);
        dish.setId(4);
        dish.setReady(true);
        dish.setUpdatedBy(updatedBy);

        order.setReady(false);

        when(orderRepository.findByIdAndRestaurant(order.getId(), restaurant)).thenReturn(Optional.of(order));

        Dish savedDish = orderService.update(order.getId(), dish.getId(), restaurant, userModel);

        verify(orderRepository, times(0)).save(order);

        assertEquals(savedDish.getId(), 4);
        assertEquals(savedDish.getName(), dish.getName());
        assertTrue(savedDish.isReady());
        assertFalse(order.isReady());
        assertEquals(dish.getUpdatedBy(), updatedBy);
    }

    @Test
    public void update_WithAnotherNotReadyDish(){
        order.getDishes().get(1).setReady(false);
        Dish dish = order.getDishes().get(0);
        dish.setId(4);
        dish.setReady(false);

        order.setReady(false);

        when(orderRepository.findByIdAndRestaurant(order.getId(), restaurant)).thenReturn(Optional.of(order));

        Dish savedDish = orderService.update(order.getId(), dish.getId(), restaurant, userModel);

        verify(orderRepository, times(1)).save(order);

        assertEquals(savedDish.getId(), 4);
        assertEquals(savedDish.getName(), dish.getName());
        assertTrue(savedDish.isReady());
        assertFalse(order.isReady());
        assertEquals(dish.getUpdatedBy(), userModel);
    }

    private void assertOrders(Order order, Order order1){
        assertEquals(order.getCreated(), order1.getCreated());
        assertEquals(order.getUpdated(), order1.getUpdated());
        assertEquals(order.getId(), order1.getId());
        assertEquals(order.getRestaurant(), order1.getRestaurant());
        assertEquals(order.getUser(), order1.getUser());

        assertDishes(order.getDishes().get(0), order1.getDishes().get(0));
    }

    private void assertDishes(Dish dish, Dish dish1){
        assertEquals(dish.getCreatedAt(), dish1.getCreatedAt());
        assertEquals(dish.getCreatedAt(), dish1.getCreatedAt());
        assertEquals(dish.getName(), dish1.getName());
        assertEquals(dish.getOrder(), dish1.getOrder());
        assertEquals(dish.getId(), dish1.getId());

        if(dish.getUpdatedBy() != null){
            assertEquals(dish.getUpdatedBy(), dish1.getUpdatedBy());
        }
    }
}
