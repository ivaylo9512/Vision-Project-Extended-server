package unit;

import com.vision.project.controllers.OrderController;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.DishDto;
import com.vision.project.models.DTOs.OrderDto;
import com.vision.project.models.specs.OrderCreateSpec;
import com.vision.project.services.base.LongPollingService;
import com.vision.project.services.base.OrderService;
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
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {
    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private LongPollingService longPollingService;

    private final Restaurant restaurant = new Restaurant(1, "testName", "testAddress", "fast food", new HashSet<>());
    private final UserModel userModel = new UserModel(1, "username", "email", "password", "ROLE_ADMIN", "firstName", "lastName", 25, "Bulgaria", restaurant);
    private final UserDetails user = new UserDetails(userModel, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    private final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getId());

    @Test
    public void findNotReady(){
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Order order = createOrder();

        Dish dish2 = new Dish("name2", false, userModel, LocalDateTime.now().plusDays(6), LocalDateTime.now().plusDays(7));
        Dish dish3 = new Dish("name3", true, userModel, LocalDateTime.now().plusDays(8), LocalDateTime.now().plusDays(9));
        Order order1 = new Order(2, List.of(dish2, dish3), LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(11), restaurant);
        order1.setUser(userModel);
        dish2.setOrder(order1);
        dish3.setOrder(order1);

        when(orderService.findNotReady(restaurant, 0, 5)).thenReturn(Map.of(order.getId(), order, order1.getId(), order1));
        when(restaurantService.getById(restaurant.getId())).thenReturn(restaurant);

        Map<Integer, OrderDto> orders = orderController.findNotReady(0, 5);

        assertOrders(orders.get(1), order);
        assertOrders(orders.get(2), order1);
        assertEquals(orders.size(), 2);
    }


    @Test
    public void findById() throws IOException {
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Order order = createOrder();

        when(userService.findById(user.getId())).thenReturn(userModel);
        when(orderService.findById(order.getId(), userModel)).thenReturn(order);

        OrderDto orderDto = orderController.findById(order.getId());

        assertOrders(orderDto, order);
    }

    @Test
    public void create() {
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        OrderCreateSpec orderSpec = new OrderCreateSpec();
        orderSpec.setDishes(List.of("dish1", "dish2"));
        Order order = new Order(orderSpec, restaurant, userModel);

        when(orderService.create(orderSpec, restaurant, userModel)).thenReturn(order);
        when(restaurantService.getById(restaurant.getId())).thenReturn(restaurant);
        when(userService.getById(user.getId())).thenReturn(userModel);

        OrderDto orderDto = orderController.create(orderSpec);

        verify(longPollingService, times(1)).checkOrders(order, restaurant.getId(), userModel.getId());

        assertOrders(orderDto, order);
    }

    @Test
    public void update(){
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Order order = createOrder();
        Dish dish = new Dish("name", true, userModel, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        dish.setOrder(order);

        when(restaurantService.getById(restaurant.getId())).thenReturn(restaurant);
        when(userService.getById(user.getId())).thenReturn(userModel);
        when(orderService.update(order.getId(), dish.getId(), restaurant, userModel)).thenReturn(dish);

        DishDto dishDto = orderController.update(order.getId(), dish.getId());

        verify(longPollingService, times(1)).checkDishes(dish, restaurant.getId(), userModel.getId());
        assertDishes(dishDto, dish);
    }

    private Order createOrder(){
        Dish dish = new Dish("name", true, userModel, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Dish dish1 = new Dish("name1", false, userModel, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3));
        Order order = new Order(1, List.of(dish, dish1), LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(5), restaurant);
        order.setUser(userModel);
        dish.setOrder(order);
        dish1.setOrder(order);

        return order;
    }

    private void assertOrders(OrderDto orderDto, Order order){
        assertEquals(orderDto.getCreated(), order.getCreated());
        assertEquals(orderDto.getUpdated(), order.getUpdated());
        assertEquals(orderDto.getId(), order.getId());
        assertEquals(orderDto.getRestaurantId(), order.getRestaurant().getId());
        assertEquals(orderDto.getUserId(), order.getUser().getId());

        assertDishes(orderDto.getDishes().get(0), order.getDishes().get(0));
    }

    private void assertDishes(DishDto dishDto, Dish dish){
        assertEquals(dishDto.getCreatedAt(), dish.getCreatedAt());
        assertEquals(dishDto.getCreatedAt(), dish.getCreatedAt());
        assertEquals(dishDto.getName(), dish.getName());
        assertEquals(dishDto.getOrderId(), dish.getOrder().getId());
        assertEquals(dishDto.getId(), dish.getId());

        if(dish.getUpdatedBy() != null){
            assertEquals(dishDto.getUpdatedById(), dish.getUpdatedBy().getId());
        }
    }
}
