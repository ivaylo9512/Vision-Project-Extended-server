package integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.project.config.AppConfig;
import com.vision.project.config.SecurityConfig;
import com.vision.project.config.TestWebConfig;
import com.vision.project.controllers.RestaurantController;
import com.vision.project.models.DTOs.MenuDto;
import com.vision.project.models.DTOs.RestaurantDto;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.RestaurantSpec;
import com.vision.project.security.Jwt;
import org.junit.jupiter.api.*;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
@WebMvcTest(RestaurantController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class Restaurants {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;
    private static String adminToken, userToken;
    private ObjectMapper objectMapper;
    private Restaurant restaurant;

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
        restaurant = new Restaurant(1, "testName", "testAddress", "fast food", new ArrayList<>());

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
        assertNotNull(webApplicationContext.getBean("restaurantController"));
    }

    @Test
    public void findById() throws Exception {
        String response = mockMvc.perform(get("/api/restaurants/auth/findById/1")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RestaurantDto restaurantDto = objectMapper.readValue(response, RestaurantDto.class);
        assertRestaurants(restaurant, restaurantDto);
    }

    @Test
    public void create() throws Exception {
        RestaurantSpec restaurantSpec = new RestaurantSpec("name", "type", "address", List.of("menu", "menu1"));
        Restaurant restaurant = new Restaurant(restaurantSpec);

        String response = mockMvc.perform(post("/api/restaurants/auth/create")
                .header("Authorization", adminToken)
                .content(objectMapper.writeValueAsString(restaurantSpec))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RestaurantDto restaurantDto = objectMapper.readValue(response, RestaurantDto.class);
        restaurant.setId(restaurantDto.getId());

        String findByIdResponse = mockMvc.perform(get("/api/restaurants/auth/findById/" + restaurantDto.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RestaurantDto foundRestaurant = objectMapper.readValue(findByIdResponse, RestaurantDto.class);

        assertTrue(restaurantDto.getMenu().contains(new MenuDto("menu", restaurantDto.getId())));
        assertTrue(restaurantDto.getMenu().contains(new MenuDto("menu1", restaurantDto.getId())));
        assertEquals(restaurantDto.getMenu().size(), 2);
        assertRestaurants(restaurant, foundRestaurant);
    }

    @Test
    public void create_WithWrongFields() throws Exception {
        String response = mockMvc.perform(post("/api/restaurants/auth/create")
                        .header("Authorization", adminToken)
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, String> errors = objectMapper.readValue(response, new TypeReference<>() {});

        assertEquals(errors.get("name"), "You must provide name.");
        assertEquals(errors.get("type"), "You must provide type.");
        assertEquals(errors.get("address"), "You must provide address.");
    }

    @Test
    public void deleteRestaurant() throws Exception {
        mockMvc.perform(delete("/api/restaurants/auth/delete/1")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/restaurants/findById/1")
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound());
    }


    @Test
    void create_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/restaurants/auth/create"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void create_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/restaurants/auth/create")
                .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void delete_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(delete("/api/restaurants/auth/delete/3"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void delete_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(delete("/api/restaurants/auth/delete/3")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void findById_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(get("/api/restaurants/auth/findById/3"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void findById_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(get("/api/restaurants/auth/findById/3")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    private void assertRestaurants(Restaurant restaurant, RestaurantDto restaurantDto){
        assertEquals(restaurantDto.getId(), restaurant.getId());
        assertEquals(restaurantDto.getAddress(), restaurant.getAddress());
        assertEquals(restaurantDto.getName(), restaurant.getName());
        assertEquals(restaurantDto.getType(), restaurant.getType());
    }
}
