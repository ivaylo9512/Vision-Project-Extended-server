package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.project.config.AppConfig;
import com.vision.project.config.SecurityConfig;
import com.vision.project.config.TestWebConfig;
import com.vision.project.controllers.RestaurantController;
import com.vision.project.models.DTOs.RestaurantDto;
import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.security.Jwt;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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
@Transactional
public class Restaurants {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;
    private static String adminToken, userToken;
    private ObjectMapper objectMapper;
    private RestaurantDto restaurant;

    @BeforeAll
    public void setup() {
//        restaurant = new RestaurantDto(1, 'testAddress', 'testName', 'zeT2sqA1', 'fast food');

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

//    @Test
//    public void findById() throws Exception {
//        String response = mockMvc.perform(get("/api/restaurants/findById/1")
//                .header("Authorization", adminToken))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        RestaurantDto restaurant = objectMapper.readValue(response, RestaurantDto.class);
//    }
//
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @PostMapping(value = "/create")
//    public RestaurantDto create(@Valid @RequestBody RestaurantSpec restaurant){
//        return new RestaurantDto(restaurantService.create(new Restaurant(restaurant)));
//    }
//
//    @PostMapping(value = "/delete/{id}")
//    public void delete(@PathVariable("id") long id){
//        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
//                .getAuthentication().getDetails();
//
//        restaurantService.delete(id, userService.findById(loggedUser.getId()));
//    }



    @Test
    void create_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/restaurant/auth/create"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void create_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/restaurant/auth/create")
                .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void delete_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(delete("/api/restaurant/auth/delete/3"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void delete_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(delete("/api/restaurant/auth/delete/3")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void findById_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(get("/api/restaurant/auth/findById/3"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void findById_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(get("/api/restaurant/auth/findById/3")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }
}
