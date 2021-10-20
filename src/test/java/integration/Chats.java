package integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.project.config.AppConfig;
import com.vision.project.config.SecurityConfig;
import com.vision.project.config.TestWebConfig;
import com.vision.project.controllers.ChatController;
import com.vision.project.models.DTOs.ChatDto;
import com.vision.project.models.DTOs.SessionDto;
import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.security.Jwt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = { AppConfig.class, TestWebConfig.class, SecurityConfig.class })
@WebAppConfiguration(value = "src/main/java/com/vision/project")
@WebMvcTest(ChatController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class Chats {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private static String adminToken, userToken;

    @BeforeEach
    public void setupData() {
        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScript(new ClassPathResource("integrationTestsSql/UsersData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/ChatsData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/SessionsData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/MessagesData.sql"));
        rdp.execute(dataSource);
    }

    @BeforeAll
    public void setup() {
        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScript(new ClassPathResource("integrationTestsSql/FilesData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/RestaurantsData.sql"));
        rdp.execute(dataSource);

        UserModel admin = new UserModel("adminUser", "password", "ROLE_ADMIN");
        admin.setId(1);

        UserModel user = new UserModel("testUser", "password", "ROLE_USER");
        user.setId(3);

        adminToken = "Token " + Jwt.generate(new UserDetails(admin, Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        userToken = "Token " + Jwt.generate(new UserDetails(user, Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void findNextSessions() throws Exception {
        String response = mockMvc.perform(get("/api/chats/auth/findNextSessions/1/2021-09-18")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<SessionDto> sessions = objectMapper.readValue(response, new TypeReference<>() {});

        assertEquals(sessions.get(0).getDate().toString(), "2021-09-17");
        assertEquals(sessions.get(1).getDate().toString(), "2021-09-16");
        assertEquals(sessions.get(2).getDate().toString(), "2021-09-15");
    }

    @Test
    public void deleteChat() throws Exception {
        mockMvc.perform(delete("/api/chats/auth/delete/1")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/chats/auth/findByUser/2")
                        .header("Authorization", adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void assertConfig_assertRestaurantController() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean("chatController"));
    }

    @Test
    void getSessions_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(get("/api/chats/auth/getSessions"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void getSessions_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(get("/api/chats/auth/getSessions")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void newMessage_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/chats/auth/newMessage"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void newMessage_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/chats/auth/newMessage")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    public void delete_WithIncorrectToken() throws Exception {
        mockMvc.perform(get("/api/chats/auth/delete/1")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    public void delete_WithoutToken() throws Exception {
        mockMvc.perform(get("/api/chats/auth/delete/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }
}
