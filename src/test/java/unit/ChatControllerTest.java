package unit;

import com.vision.project.controllers.ChatController;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.ChatDto;
import com.vision.project.models.DTOs.MessageDto;
import com.vision.project.models.DTOs.SessionDto;
import com.vision.project.models.DTOs.UserDto;
import com.vision.project.models.specs.MessageSpec;
import com.vision.project.services.base.ChatService;
import com.vision.project.services.base.LongPollingService;
import com.vision.project.services.base.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {
    @InjectMocks
    private ChatController chatController;

    @Mock
    private ChatService chatService;

    @Mock
    private UserService userService;

    @Mock
    private LongPollingService longPollingService;

    private final Restaurant restaurant = new Restaurant(1, "testName", "testAddress", "fast food", new ArrayList<>());
    private final UserModel userModel = new UserModel(1, "username", "email", "password", "ROLE_ADMIN", "firstName",
            "lastName", 25, "Bulgaria", restaurant);
    private final UserDetails user = new UserDetails(userModel, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    private final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getId());
    private final Map<Long, Chat> chats = new LinkedHashMap<>();

    private Chat chat;

    @BeforeEach
    public void setup(){
        createChats();
    }

    @Test
    public void findChats(){
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(chatService.findUserChats(user.getId(), 5)).thenReturn(chats);

        Map<Long, ChatDto> chatDtos = chatController.findChats(5);

        chatDtos.forEach((k, v) -> assertChats(chats.get(k), v));
    }

    @Test
    public void getSessions(){
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        List<Session> sessions = chat.getSessions();

        when(chatService.findById(1, user.getId())).thenReturn(chat);
        when(chatService.findSessions(chat, 3, 5)).thenReturn(List.of(sessions.get(0), sessions.get(1)));

        List<SessionDto> sessionDtos = chatController.getSessions(1, 3, 5);

        assertSessions(sessions.get(0), sessionDtos.get(0));
        assertSessions(sessions.get(1), sessionDtos.get(1));
    }

    @Test
    public void addMessage() {
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Message message = chat.getSessions().get(0).getMessages().get(0);
        MessageSpec messageSpec = new MessageSpec(chat.getId(), chat.getFirstUser().getId(), chat.getSecondUser().getId(), message.getMessage());

        when(chatService.addNewMessage(messageSpec)).thenReturn(message);

        MessageDto messageDto = chatController.addMessage(messageSpec);

        assertMessages(message, messageDto);
        assertEquals(messageDto.getChatId(), messageSpec.getChatId());
        verify(longPollingService, times(1)).checkMessages(message);
    }

    @Test
    public void delete(){
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userService.findById(1L)).thenReturn(userModel);

        chatController.delete(1L);

        verify(chatService, times(1)).delete(1L, userModel);
    }

    private void createChats(){
        chat = new Chat();
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
}
