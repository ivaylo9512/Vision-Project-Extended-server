package integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.project.config.AppConfig;
import com.vision.project.config.SecurityConfig;
import com.vision.project.config.TestWebConfig;
import com.vision.project.controllers.MenuController;
import com.vision.project.models.DTOs.MenuDto;
import com.vision.project.models.DTOs.RestaurantDto;
import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.MenuCreateSpec;
import com.vision.project.models.specs.MenuUpdateSpec;
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
import org.springframework.http.MediaType;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = { AppConfig.class, TestWebConfig.class, SecurityConfig.class })
@WebAppConfiguration(value = "src/main/java/com/vision/project")
@WebMvcTest(MenuController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@Transactional
public class Menu {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;
    private static String adminToken, userToken;
    private ObjectMapper objectMapper;
    private RestaurantDto restaurant;

    @BeforeEach
    public void setupEach(){
        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScript(new ClassPathResource("integrationTestsSql/RestaurantsData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/FilesData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/UsersData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/EmailTokenData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/MenuData.sql"));
        rdp.execute(dataSource);
    }

    @BeforeAll
    public void setup() {
        UserModel admin = new UserModel("adminUser", "password", "ROLE_ADMIN");
        admin.setId(1);

        UserModel user = new UserModel("testUser", "password", "ROLE_USER");
        user.setId(3);

        adminToken = "Token " + Jwt.generate(new UserDetails(admin, Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        userToken = "Token " + Jwt.generate(new UserDetails(user, Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void assertConfig_assertRestaurantController() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean("menuController"));
    }

    @Test
    public void findById() throws Exception {
        String response = mockMvc.perform(get("/api/menu/auth/findById/1")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MenuDto menu = objectMapper.readValue(response, MenuDto.class);

        assertEquals(menu.getName(), "Pizza");
        assertEquals(menu.getId(), 1);
        assertEquals(menu.getRestaurantId(), 1);
    }

    @Test
    public void create() throws Exception {
        MenuCreateSpec menuCreateSpec = new MenuCreateSpec("name", 1);

        String response = mockMvc.perform(post("/api/menu/auth/create")
                .content(objectMapper.writeValueAsString(menuCreateSpec))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MenuDto menu = objectMapper.readValue(response, MenuDto.class);

        String findByIdResponse = mockMvc.perform(get("/api/menu/auth/findById/" + menu.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MenuDto found = objectMapper.readValue(findByIdResponse, MenuDto.class);

        assertEquals(found.getName(), menuCreateSpec.getName());
        assertEquals(found.getRestaurantId(), menuCreateSpec.getRestaurantId());
        assertEquals(found.getId(), menu.getId());
    }

    @Test
    public void create_WithWrongFields() throws Exception {
        String response = mockMvc.perform(post("/api/menu/auth/create")
                .header("Authorization", adminToken)
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, String> errors = objectMapper.readValue(response, new TypeReference<>() {});

        assertEquals(errors.get("name"), "You must provide name.");
        assertEquals(errors.get("restaurantId"), "You must provide restaurant id.");
    }

    @Test
    public void update() throws Exception {
        MenuUpdateSpec menuUpdateSpec = new MenuUpdateSpec(1, "newName", 1);

        mockMvc.perform(patch("/api/menu/auth/update")
                .content(objectMapper.writeValueAsString(menuUpdateSpec))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", adminToken))
                .andExpect(status().isOk());


        String findByIdResponse = mockMvc.perform(get("/api/menu/auth/findById/1")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MenuDto found = objectMapper.readValue(findByIdResponse, MenuDto.class);

        assertEquals(found.getName(), menuUpdateSpec.getName());
        assertEquals(found.getRestaurantId(), menuUpdateSpec.getRestaurantId());
        assertEquals(found.getId(), 1);
    }

    @Test
    public void update_WithWrongFields() throws Exception {
        String response = mockMvc.perform(patch("/api/menu/auth/update")
                        .header("Authorization", adminToken)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, String> errors = objectMapper.readValue(response, new TypeReference<>() {});

        assertEquals(errors.get("id"), "You must provide id.");
        assertEquals(errors.get("name"), "You must provide name.");
        assertEquals(errors.get("restaurantId"), "You must provide restaurant id.");
    }

    @Test
    public void deleteMenu() throws Exception {
        mockMvc.perform(delete("/api/menu/auth/delete/1")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/menu/findById/1")
                        .header("Authorization", adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/menu/auth/create"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void create_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/menu/auth/create")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void update_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(patch("/api/menu/auth/update"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void update_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(patch("/api/menu/auth/update")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void delete_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(delete("/api/menu/auth/delete/3"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void delete_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(delete("/api/menu/auth/delete/3")
                .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }
}