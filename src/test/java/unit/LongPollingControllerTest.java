package unit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.vision.project.controllers.LongPollingController;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.*;
import com.vision.project.services.base.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LongPollingControllerTest {
    @InjectMocks
    @Spy
    private LongPollingController longPollingController;

    @Mock
    private UserService userService;

    @Mock
    private OrderService orderService;

    @Mock
    private ChatService chatService;

    @Mock
    private LongPollingService longPollingService;

    @Mock
    private RestaurantService restaurantService;

    private final Restaurant restaurant = new Restaurant(1, "testName", "testAddress", "fast food", new ArrayList<>());
    private final UserModel userModel = new UserModel(1, "username", "email", "password", "ROLE_ADMIN", "firstName",
            "lastName", 25, "Bulgaria", restaurant);
    private final UserDetails user = new UserDetails(userModel, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    private final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getId());
    private final Map<Long, Order> orders = new LinkedHashMap<>();
    private final Map<Long, Chat> chats = new LinkedHashMap<>();

    @BeforeEach
    public void setup(){
        createOrders();
        createChats();
    }

    @Test
    public void login(){
        int pageSize = 5;

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(orderService.findNotReady(restaurant, 0, pageSize)).thenReturn(orders);
        when(chatService.findUserChats(user.getId(), pageSize)).thenReturn(chats);

        UserDto userDto = longPollingController.login(pageSize);

        verify(longPollingController, times(1)).initializeUser(userModel, pageSize);
        assertOrders(userDto.getRestaurant().getOrders().get(1L), orders.get(1L));
        assertOrders(userDto.getRestaurant().getOrders().get(2L), orders.get(2L));
        assertRestaurants(restaurant, userDto.getRestaurant());
        assertChats(chats.get(1L), userDto.getChats().get(1L));
        assertChats(chats.get(2L), userDto.getChats().get(2L));
    }

    @Test
    public void getLoggedUser(){
        int pageSize = 5;

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userService.findById(user.getId())).thenReturn(userModel);
        when(orderService.findNotReady(restaurant, 0, pageSize)).thenReturn(orders);
        when(chatService.findUserChats(user.getId(), pageSize)).thenReturn(chats);

        UserDto userDto = longPollingController.getLoggedUser(pageSize);

        verify(longPollingController, times(1)).initializeUser(userModel, pageSize);
        assertOrders(userDto.getRestaurant().getOrders().get(1L), orders.get(1L));
        assertOrders(userDto.getRestaurant().getOrders().get(2L), orders.get(2L));
        assertRestaurants(restaurant, userDto.getRestaurant());
        assertChats(chats.get(1L), userDto.getChats().get(1L));
        assertChats(chats.get(2L), userDto.getChats().get(2L));
    }

    @Test
    public void initializeUser(){
        int pageSize = 5;

        when(orderService.findNotReady(restaurant, 0, pageSize)).thenReturn(orders);
        when(chatService.findUserChats(user.getId(), pageSize)).thenReturn(chats);

        ArgumentCaptor<UserRequest> captor = ArgumentCaptor.forClass(UserRequest.class);

        UserDto userDto = longPollingController.initializeUser(userModel, pageSize);

        verify(longPollingService).addRequest(captor.capture());
        UserRequest request = captor.getValue();

        assertEquals(request.getRestaurant(), userModel.getRestaurant());
        assertEquals(request.getUserId(), userModel.getId());
        assertOrders(userDto.getRestaurant().getOrders().get(1L), orders.get(1L));
        assertOrders(userDto.getRestaurant().getOrders().get(2L), orders.get(2L));
        assertRestaurants(restaurant, userDto.getRestaurant());
        assertChats(chats.get(1L), userDto.getChats().get(1L));
        assertChats(chats.get(2L), userDto.getChats().get(2L));
    }

    @Test
    public void waitData(){
        ArgumentCaptor<UserRequest> captor = ArgumentCaptor.forClass(UserRequest.class);
        LocalDateTime lastCheck = LocalDateTime.now();

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(restaurantService.getById(restaurant.getId())).thenReturn(restaurant);

        longPollingController.waitData(lastCheck);

        verify(longPollingService).setAndAddRequest(captor.capture());

        UserRequest userRequest = captor.getValue();

        assertEquals(userRequest.getRestaurant(), restaurant);
        assertEquals(userRequest.getLastCheck(), lastCheck);
        assertEquals(userRequest.getUserId(), user.getId());
        assertNotNull(userRequest.getRequest());
    }

    private void createOrders(){
        Dish dish = new Dish("name", true, userModel, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Dish dish1 = new Dish("name1", false, userModel, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3));
        Order order = new Order(1, List.of(dish, dish1), LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(5), restaurant);
        order.setUser(userModel);
        dish.setOrder(order);
        dish1.setOrder(order);

        Dish dish2 = new Dish("name2", false, userModel, LocalDateTime.now().plusDays(6), LocalDateTime.now().plusDays(7));
        Dish dish3 = new Dish("name3", true, userModel, LocalDateTime.now().plusDays(8), LocalDateTime.now().plusDays(9));
        Order order1 = new Order(2, List.of(dish2, dish3), LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(11), restaurant);
        order1.setUser(userModel);
        dish2.setOrder(order1);
        dish3.setOrder(order1);

        orders.put(order.getId(), order);
        orders.put(order1.getId(), order1);
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

    private void createChats(){
        Chat chat = new Chat();
        Chat chat1 = new Chat();

        chat.setId(1);
        chat1.setId(2);

        Session session = new Session(chat, LocalDate.now());
        Session session1 = new Session(chat1, LocalDate.now().plusDays(1));
        Session session2 = new Session(chat, LocalDate.now());

        UserModel userModel1 = new UserModel(2, "username2", "email2", "password2", "ROLE_USER", "firstName2",
                "lastName2", 26, "Bulgaria", restaurant);

        chat.setSessions(List.of(session, session2));
        chat.setFirstUser(userModel);
        chat.setSecondUser(userModel1);

        chat1.setSessions(List.of(session));
        chat1.setFirstUser(userModel1);
        chat1.setSecondUser(new UserModel(2, "username3", "email3", "password3", "ROLE_USER", "firstName3",
                "lastName3", 24, "Bulgaria", restaurant));

        LocalTime time = LocalTime.now();
        Message message = new Message(userModel, time, "message", session);

        LocalTime time1 = LocalTime.now().plusMinutes(1);
        Message message1 = new Message(userModel, time1, "message", session);

        LocalTime time2 = LocalTime.now().plusMinutes(2);
        Message message2 = new Message(userModel, time2, "message", session1);

        LocalTime time3 = LocalTime.now().plusMinutes(3);
        Message message3 = new Message(userModel, time3, "message", session1);

        LocalTime time4 = LocalTime.now();
        Message message4 = new Message(userModel, time4, "message3", session);

        LocalTime time5 = LocalTime.now().plusMinutes(1);
        Message message5 = new Message(userModel, time5, "message4", session);

        session.setMessages(List.of(message, message1));
        session1.setMessages(List.of(message2, message3));
        session2.setMessages(List.of(message4, message5));

        chats.put(chat.getId(), chat);
        chats.put(chat1.getId(), chat1);
    }

    private void assertChats(Chat chat, ChatDto chatDto){
        assertUsers(chat.getFirstUser(), chatDto.getFirstUser());
        assertUsers(chat.getSecondUser(), chatDto.getSecondUser());
        assertSessions(chat.getSessions().get(0), chatDto.getSessions().get(0));
    }

    private void assertSessions(Session session, SessionDto sessionDto){
        assertEquals(session.getDate(), sessionDto.getDate());
        assertMessages(session.getMessages().get(0), sessionDto.getMessages().get(0));
        assertMessages(session.getMessages().get(1), sessionDto.getMessages().get(1));
    }

    private void assertMessages(Message message, MessageDto messageDto){
        assertEquals(message.getMessage(), messageDto.getMessage());
        assertEquals(message.getReceiver().getId(), messageDto.getReceiverId());
        assertEquals(message.getSession().getDate(), messageDto.getSession());
        assertEquals(message.getTime(), messageDto.getTime());
    }

    private void assertUsers(UserModel user, UserDto userDto){
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getAge(), userDto.getAge());
        assertEquals(user.getCountry(), userDto.getCountry());
        assertEquals(user.getRestaurant().getId(), userDto.getRestaurant().getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getRole(), userDto.getRole());
    }

    private void assertRestaurants(Restaurant restaurant, RestaurantDto restaurantDto){
        assertEquals(restaurantDto.getId(), restaurant.getId());
        assertEquals(restaurantDto.getAddress(), restaurant.getAddress());
        assertEquals(restaurantDto.getName(), restaurant.getName());
        assertEquals(restaurantDto.getType(), restaurant.getType());
    }
}