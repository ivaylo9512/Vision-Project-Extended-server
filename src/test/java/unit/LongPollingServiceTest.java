package unit;

import com.vision.project.models.*;
import com.vision.project.models.DTOs.*;
import com.vision.project.services.LongPollingServiceImpl;
import com.vision.project.services.base.ChatService;
import com.vision.project.services.base.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.async.DeferredResult;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LongPollingServiceTest {
    @InjectMocks
    @Spy
    private LongPollingServiceImpl longPollingService;

    @Mock
    private OrderService orderService;

    @Mock
    private ChatService chatService;

    @Mock
    private DeferredResult<UserRequestDto> deferredResult;

    private final Restaurant restaurant = new Restaurant(1, "testName", "testAddress", "fast food", new ArrayList<>());
    private final UserModel userModel = new UserModel(1, "username", "email", "password", "ROLE_ADMIN", "firstName",
            "lastName", 25, "Bulgaria", restaurant);
    private List<Dish> dishes;
    private List<Message> messages;
    private List<Order> orders;

    @BeforeEach
    public void setup(){
        createOrders();
        createChats();
    }

    @Test
    public void setDataFromRequest() {
        DeferredResult<UserRequestDto> deferredResult = new DeferredResult<>();
        UserRequest userRequest = new UserRequest(userModel.getId(), restaurant, new ArrayList<>(orders), new ArrayList<>(messages), new ArrayList<>(dishes));
        userRequest.setRequest(deferredResult);

        longPollingService.setDataFromRequest(userRequest);

        UserRequestDto requestDto = (UserRequestDto) deferredResult.getResult();

        verify(longPollingService, times(1)).clearData(userRequest);
        assertNull(userRequest.getRequest());
        assertOrders(requestDto.getOrders().get(1L), orders.get(0));
        assertOrders(requestDto.getOrders().get(2L), orders.get(1));
        assertEquals(requestDto.getRestaurantId(), restaurant.getId());
        assertMessages(requestDto.getMessages().get(0), messages.get(0));
        assertMessages(requestDto.getMessages().get(1), messages.get(1));
        assertEquals(requestDto.getUserId(), userModel.getId());
    }

    @Test
    public void setAndAddRequest(){
        UserRequest userRequest = new UserRequest(userModel.getId(), restaurant, new ArrayList<>(orders), new ArrayList<>(messages), new ArrayList<>(dishes));
        longPollingService.setAndAddRequest(userRequest);

        DeferredResult<UserRequestDto> deferredResult = new DeferredResult<>();
        UserRequest userRequest1 = new UserRequest(userModel.getId(), restaurant, deferredResult);

        longPollingService.setAndAddRequest(userRequest1);

        verify(longPollingService, times(0)).addRequest(userRequest1);
        verify(longPollingService, times(0)).setMoreRecentData(userRequest1);
        verify(longPollingService, times(1)).setMoreRecentData(userRequest);
        verify(longPollingService, times(1)).setDataFromRequest(userRequest);
        verify(longPollingService, times(2)).addRequest(userRequest);

        UserRequestDto requestDto = (UserRequestDto) deferredResult.getResult();

        assertNull(userRequest.getRequest());
        assertOrders(requestDto.getOrders().get(1L), orders.get(0));
        assertOrders(requestDto.getOrders().get(2L), orders.get(1));
        assertEquals(requestDto.getRestaurantId(), restaurant.getId());
        assertMessages(requestDto.getMessages().get(0), messages.get(0));
        assertMessages(requestDto.getMessages().get(1), messages.get(1));
        assertEquals(requestDto.getUserId(), userModel.getId());
    }

    @Test
    public void clearData(){
        UserRequest userRequest = new UserRequest(userModel.getId(), restaurant, orders, messages, dishes);

        longPollingService.clearData(userRequest);

        assertEquals(orders.size(), 0);
        assertEquals(messages.size(), 0);
        assertEquals(dishes.size(), 0);
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

        dishes = new ArrayList<>(List.of(dish, dish1, dish2, dish3));
        orders = new ArrayList<>(List.of(order, order1));
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

        messages = new ArrayList<>(List.of(message, message1, message2, message3, message4, message5));

    }

    private void assertOrders(OrderDto orderDto, Order order){
        assertEquals(orderDto.getCreatedAt(), order.getCreated());
        assertEquals(orderDto.getUpdatedAt(), order.getUpdated());
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

    private void assertMessages(MessageDto messageDto, Message message){
        assertEquals(message.getMessage(), messageDto.getMessage());
        assertEquals(message.getReceiver().getId(), messageDto.getReceiverId());
        assertEquals(message.getSession().getDate(), messageDto.getSession());
        assertEquals(message.getTime(), messageDto.getTime());
    }
}
