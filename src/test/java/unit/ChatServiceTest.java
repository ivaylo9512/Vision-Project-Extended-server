package unit;

import com.vision.project.exceptions.UnauthorizedException;
import com.vision.project.models.Chat;
import com.vision.project.models.Message;
import com.vision.project.models.Session;
import com.vision.project.models.UserModel;
import com.vision.project.models.compositePK.SessionPK;
import com.vision.project.models.specs.MessageSpec;
import com.vision.project.repositories.base.ChatRepository;
import com.vision.project.repositories.base.MessageRepository;
import com.vision.project.repositories.base.SessionRepository;
import com.vision.project.repositories.base.UserRepository;
import com.vision.project.services.ChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {
    @InjectMocks
    private ChatServiceImpl chatService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private MessageRepository messageRepository;

    private UserModel user = new UserModel("Test", "Test", "ROLE_ADMIN");
    private Chat chat;
    private List<Chat> chats;

    @BeforeEach
    public void setup(){
        createChats();
    }

    @Test
    public void findById() {
        UserModel firsUser = new UserModel();
        UserModel secondUser = new UserModel();
        firsUser.setId(1);
        secondUser.setId(2);

        chat.setFirstUser(firsUser);
        chat.setSecondUser(secondUser);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        Chat foundChat = chatService.findById(1L, 2L);

        assertChats(chat, foundChat);
    }

    @Test
    public void findById_WithSecondUser() {
        UserModel firsUser = new UserModel();
        UserModel secondUser = new UserModel();
        firsUser.setId(1);
        secondUser.setId(2);

        chat.setFirstUser(firsUser);
        chat.setSecondUser(secondUser);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        Chat foundChat = chatService.findById(1L, 1L);

        assertChats(chat, foundChat);
    }

    @Test
    public void findById_WithChatThatDoesNotBelongToUser() {
        UserModel firsUser = new UserModel();
        UserModel secondUser = new UserModel();
        firsUser.setId(1);
        secondUser.setId(2);

        chat.setFirstUser(firsUser);
        chat.setSecondUser(secondUser);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> chatService.findById(1L, 3L)
        );

        assertEquals(thrown.getMessage(), "Unauthorized.");
    }

    @Test
    public void addNewMessage_withChatWithDifferentSender() {
        UserModel sender = new UserModel();
        sender.setId(1);
        UserModel receiver = new UserModel();
        receiver.setId(5);

        Chat chat = new Chat();
        chat.setFirstUser(sender);
        chat.setSecondUser(receiver);

        MessageSpec messageSpec = new MessageSpec(1, 2, 5, "message");

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> chatService.addNewMessage(messageSpec)
        );

        assertEquals(thrown.getMessage(), "Users don't match the given chat.");
    }

    @Test
    public void addNewMessage_withChatWithDifferentSender_WithChatSecondUser() {
        UserModel sender = new UserModel();
        sender.setId(1);
        UserModel receiver = new UserModel();
        receiver.setId(5);

        Chat chat = new Chat();
        chat.setFirstUser(receiver);
        chat.setSecondUser(sender);

        MessageSpec messageSpec = new MessageSpec(1, 2, 5, "message");

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> chatService.addNewMessage(messageSpec)
        );

        assertEquals(thrown.getMessage(), "Users don't match the given chat.");
    }

    @Test
    public void addNewMessage_withChatWithDifferentReceiver() {
        UserModel sender = new UserModel();
        sender.setId(5);
        UserModel receiver = new UserModel();
        receiver.setId(1);

        Chat chat = new Chat();
        chat.setFirstUser(sender);
        chat.setSecondUser(receiver);

        MessageSpec messageSpec = new MessageSpec(1, 5, 2, "message");

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> chatService.addNewMessage(messageSpec)
        );

        assertEquals(thrown.getMessage(), "Users don't match the given chat.");
    }

    @Test
    public void addNewMessage_withChatWithDifferentReceiver_WithChatSecondUser() {
        UserModel sender = new UserModel();
        sender.setId(5);
        UserModel receiver = new UserModel();
        receiver.setId(1);

        Chat chat = new Chat();
        chat.setFirstUser(receiver);
        chat.setSecondUser(sender);

        MessageSpec messageSpec = new MessageSpec(1, 2, 5, "message");

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> chatService.addNewMessage(messageSpec)
        );

        assertEquals(thrown.getMessage(), "Users don't match the given chat.");
    }

    @Test
    public void addNewMessage_withNewSession() {
        UserModel sender = new UserModel();
        sender.setId(2);
        UserModel receiver = new UserModel();
        receiver.setId(3);

        Session session = new Session();

        Chat chat = new Chat();
        chat.setFirstUser(sender);
        chat.setSecondUser(receiver);

        MessageSpec messageSpec = new MessageSpec(1, 2, 3, "message");
        Message message = new Message(sender, LocalTime.now(), messageSpec.getMessage(), session);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(sessionRepository.findById(new SessionPK(chat, LocalDate.now()))).thenReturn(Optional.of(session));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(userRepository.getById(3L)).thenReturn(new UserModel());

        Message savedMessage = chatService.addNewMessage(messageSpec);

        assertMessages(message, savedMessage);
    }

    @Test
    public void addNewMessage_WithReceiver() {
        UserModel sender = new UserModel();
        sender.setId(2);
        UserModel receiver = new UserModel();
        receiver.setId(3);

        Session session = new Session();

        Chat chat = new Chat();
        chat.setFirstUser(receiver);
        chat.setSecondUser(sender);

        MessageSpec messageSpec = new MessageSpec(1, 2, 3, "message");
        Message message = new Message(sender, LocalTime.now(), messageSpec.getMessage(), session);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(sessionRepository.findById(new SessionPK(chat, LocalDate.now()))).thenReturn(Optional.of(session));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(userRepository.getById(3L)).thenReturn(new UserModel());

        Message savedMessage = chatService.addNewMessage(messageSpec);

        assertMessages(message, savedMessage);
    }

    @Test
    public void delete(){
        UserModel user = new UserModel();
        user.setId(1);
        user.setRole("ROLE_USER");

        Chat chat = new Chat();
        chat.setFirstUser(user);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        chatService.delete(1L, user);

        verify(chatRepository, times(1)).delete(chat);
    }

    @Test
    public void delete_WithAdmin(){
        UserModel user = new UserModel();
        user.setId(1);
        user.setRole("ROLE_ADMIN");

        chatService.delete(1L, user);

        verify(chatRepository, times(1)).deleteById(1L);
    }

    @Test
    public void verifyMessage() {
        UserModel sender = new UserModel();
        UserModel receiver = new UserModel();

        sender.setId(2);
        receiver.setId(1);

        chat.setFirstUser(sender);
        chat.setSecondUser(receiver);

        MessageSpec messageSpec = new MessageSpec();
        messageSpec.setSenderId(sender.getId());
        messageSpec.setReceiverId(receiver.getId());

        chatService.verifyMessage(messageSpec, chat);
    }

    @Test
    public void verifyMessage_WithDifferentSender() {
        UserModel sender = new UserModel();
        UserModel receiver = new UserModel();

        sender.setId(2);
        receiver.setId(1);

        chat.setFirstUser(new UserModel());
        chat.setSecondUser(receiver);

        MessageSpec messageSpec = new MessageSpec();
        messageSpec.setSenderId(sender.getId());
        messageSpec.setReceiverId(receiver.getId());

        UnauthorizedException thrown = assertThrows(UnauthorizedException.class,
                () -> chatService.verifyMessage(messageSpec, chat));

        assertEquals(thrown.getMessage(), "Users don't match the given chat.");
    }

    @Test
    public void verifyMessage_WithDifferentReceiver() {
        UserModel sender = new UserModel();
        UserModel receiver = new UserModel();

        sender.setId(2);
        receiver.setId(1);

        chat.setFirstUser(sender);
        chat.setSecondUser(new UserModel());

        MessageSpec messageSpec = new MessageSpec();
        messageSpec.setSenderId(sender.getId());
        messageSpec.setReceiverId(receiver.getId());

        UnauthorizedException thrown = assertThrows(UnauthorizedException.class,
                () -> chatService.verifyMessage(messageSpec, chat));

        assertEquals(thrown.getMessage(), "Users don't match the given chat.");
    }

    private void createChats(){
        chat = new Chat();
        Chat chat1 = new Chat();

        chat.setId(1);
        chat1.setId(2);

        Session session = new Session(chat, LocalDate.now());
        Session session1 = new Session(chat1, LocalDate.now().plusDays(1));

        chat.setSessions(List.of(session));
        chat1.setSessions(List.of(session));

        LocalTime time = LocalTime.now();
        Message message = new Message(user, time, "message", session);

        LocalTime time1 = LocalTime.now().plusMinutes(1);
        Message message1 = new Message(user, time1, "message", session);

        LocalTime time2 = LocalTime.now().plusMinutes(2);
        Message message2 = new Message(user, time2, "message", session1);

        LocalTime time3 = LocalTime.now().plusMinutes(3);
        Message message3 = new Message(user, time3, "message", session1);

        session.setMessages(List.of(message, message1));
        session1.setMessages(List.of(message2, message3));

        chats = List.of(chat, chat1);
    }

    private void assertChats(Chat chat, Chat chat1){
        assertEquals(chat.getFirstUser(), chat1.getFirstUser());
        assertEquals(chat.getSecondUser(), chat1.getSecondUser());
        assertSessions(chat.getSessions().get(0), chat1.getSessions().get(0));
    }

    private void assertSessions(Session session, Session session1){
        assertEquals(session.getDate(), session1.getDate());
        assertEquals(session.getChat(), session1.getChat());
        assertMessages(session.getMessages().get(0), session1.getMessages().get(0));
        assertMessages(session.getMessages().get(1), session1.getMessages().get(1));
    }

    private void assertMessages(Message message, Message message1){
        assertEquals(message.getMessage(), message1.getMessage());
        assertEquals(message.getReceiver(), message1.getReceiver());
        assertEquals(message.getId(), message1.getId());
        assertEquals(message.getSession(), message1.getSession());
        assertEquals(message.getTime(), message1.getTime());
    }
}
