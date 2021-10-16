package integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vision.project.config.AppConfig;
import com.vision.project.config.SecurityConfig;
import com.vision.project.config.TestWebConfig;
import com.vision.project.controllers.OrderController;
import com.vision.project.models.DTOs.DishDto;
import com.vision.project.models.DTOs.OrderDto;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.OrderCreateSpec;
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
import java.time.LocalDateTime;
import java.time.Month;
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
@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class Orders {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;
    private static String adminToken, userToken;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setupData() {
        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScript(new ClassPathResource("integrationTestsSql/UsersData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/RestaurantsData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/OrdersData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/DishesData.sql"));
        rdp.execute(dataSource);
    }

    @BeforeAll
    public void setup() {
        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScript(new ClassPathResource("integrationTestsSql/RestaurantsData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/FilesData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/MenuData.sql"));
        rdp.execute(dataSource);

        Restaurant restaurant = new Restaurant(1, "testName", "testAddress", "fast food", new ArrayList<>());

        UserModel admin = new UserModel("adminUser", "password", "ROLE_ADMIN");
        admin.setId(1);
        admin.setRestaurant(restaurant);

        UserModel user = new UserModel("testUser", "password", "ROLE_USER");
        user.setId(3);
        user.setRestaurant(restaurant);

        adminToken = "Token " + Jwt.generate(new UserDetails(admin, Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        userToken = "Token " + Jwt.generate(new UserDetails(user, Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
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
        assertNotNull(webApplicationContext.getBean("orderController"));
    }

    @Test
    public void findById() throws Exception {
        String response = mockMvc.perform(get("/api/orders/auth/findById/1")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderDto order = objectMapper.readValue(response, OrderDto.class);
        List<DishDto> dishes = order.getDishes();
        DishDto dish = dishes.get(0);

        assertEquals(order.getId(), 1);
        assertEquals(order.getUpdated(), LocalDateTime.of(2021, Month.MAY, 14, 15, 53, 41));
        assertEquals(order.getCreated(), LocalDateTime.of(2021, Month.APRIL, 14, 15, 53, 37));
        assertEquals(order.getRestaurantId(), 1);

        assertEquals(dishes.size(), 4);
        assertEquals(dish.getId(), 1);
        assertTrue(dish.isReady());
        assertEquals(dish.getName(), "Burger");
        assertEquals(dish.getCreatedAt(), LocalDateTime.of(2021, Month.OCTOBER, 14, 16, 0, 56));
        assertEquals(dish.getUpdatedAt(), LocalDateTime.of(2021, Month.OCTOBER, 14, 16, 1, 12));
        assertEquals(dish.getUpdatedById(), 1);

        assertEquals(dishes.get(1).getId(), 2);
        assertEquals(dishes.get(1).getName(), "Pizza");
        assertFalse(dishes.get(1).isReady());

        assertEquals(dishes.get(2).getId(), 3);
        assertEquals(dishes.get(2).getName(), "Salad");
        assertFalse(dishes.get(2).isReady());

        assertEquals(dishes.get(3).getId(), 4);
        assertEquals(dishes.get(3).getName(), "Sushi");
        assertTrue(dishes.get(3).isReady());
    }

    @Test
    public void findById_WithNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/auth/findById/222")
                .header("Authorization", userToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Order not found."));
    }

    @Test
    public void create() throws Exception {
        OrderCreateSpec orderCreateSpec = new OrderCreateSpec(List.of("menu", "menu1"), 1);
        String response = mockMvc.perform(post("/api/orders/auth/create")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderCreateSpec)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderDto order = objectMapper.readValue(response, OrderDto.class);

        String findByIdResponse = mockMvc.perform(get("/api/orders/auth/findById/" + order.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderDto foundOrder = objectMapper.readValue(findByIdResponse, OrderDto.class);
        List<DishDto> dishes = foundOrder.getDishes();

        assertEquals(foundOrder.getId(), order.getId());
        assertEquals(foundOrder.getRestaurantId(), 1);
        assertEquals(foundOrder.getDishes().size(), 2);

        assertEquals(dishes.get(0).getId(), 13);
        assertFalse(dishes.get(0).isReady());
        assertEquals(dishes.get(0).getName(), "menu");

        assertEquals(dishes.get(1).getId(), 14);
        assertFalse(dishes.get(1).isReady());
        assertEquals(dishes.get(1).getName(), "menu1");
    }

    @Test
    public void create_WithWrongFields() throws Exception {
        String response = mockMvc.perform(post("/api/orders/auth/create")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, String> errors = objectMapper.readValue(response, new TypeReference<>() {});

        assertEquals(errors.get("dishes"), "Dishes must have at least 1 item.");
        assertEquals(errors.get("restaurantId"), "You must provide restaurant id.");
    }

    @Test
    public void update() throws Exception {
        mockMvc.perform(patch("/api/orders/auth/update/1/2")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String response = mockMvc.perform(get("/api/orders/auth/findById/1")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderDto order = objectMapper.readValue(response, OrderDto.class);
        List<DishDto> dishes = order.getDishes();
        DishDto dish = dishes.get(0);

        assertEquals(order.getId(), 1);
        assertEquals(order.getUpdated(), LocalDateTime.of(2021, Month.MAY, 14, 15, 53, 41));
        assertEquals(order.getCreated(), LocalDateTime.of(2021, Month.APRIL, 14, 15, 53, 37));
        assertEquals(order.getRestaurantId(), 1);

        assertEquals(dishes.size(), 4);
        assertEquals(dish.getId(), 1);
        assertTrue(dish.isReady());
        assertEquals(dish.getName(), "Burger");
        assertEquals(dish.getCreatedAt(), LocalDateTime.of(2021, Month.OCTOBER, 14, 16, 0, 56));
        assertEquals(dish.getUpdatedAt(), LocalDateTime.of(2021, Month.OCTOBER, 14, 16, 1, 12));
        assertEquals(dish.getUpdatedById(), 1);

        assertEquals(dishes.get(1).getId(), 2);
        assertEquals(dishes.get(1).getName(), "Pizza");
        assertTrue(dishes.get(1).isReady());

        assertEquals(dishes.get(2).getId(), 3);
        assertEquals(dishes.get(2).getName(), "Salad");
        assertFalse(dishes.get(2).isReady());

        assertEquals(dishes.get(3).getId(), 4);
        assertEquals(dishes.get(3).getName(), "Sushi");
        assertTrue(dishes.get(3).isReady());
    }

    @Test
    public void update_WithNotOrderDish() throws Exception {
        mockMvc.perform(patch("/api/orders/auth/update/222/1")
                .header("Authorization", userToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Order not found."));
    }

    @Test
    public void update_WithNotFoundDish() throws Exception {
        mockMvc.perform(patch("/api/orders/auth/update/5/222")
                        .header("Authorization", userToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Dish not found."));
    }

    @Test
    void findNotReady_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(get("/api/restaurant/auth/findNotReady/2/3/2"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void findNotReady_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(get("/api/orders/auth/findNotReady/2/3/2")
                .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void findById_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(get("/api/restaurant/auth/findById/2"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void findById_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(get("/api/orders/auth/findById/2")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void create_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/restaurant/auth/create"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void create_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/orders/auth/create")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void update_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/restaurant/auth/update/2/3"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void update_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/orders/auth/update/2/3")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }
}
