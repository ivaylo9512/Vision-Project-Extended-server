package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.project.config.AppConfig;
import com.vision.project.config.SecurityConfig;
import com.vision.project.config.TestWebConfig;
import com.vision.project.controllers.LongPollingController;
import com.vision.project.models.DTOs.RestaurantDto;
import com.vision.project.models.DTOs.UserDto;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.UserSpec;
import com.vision.project.security.Jwt;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.HashSet;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = { AppConfig.class, TestWebConfig.class, SecurityConfig.class })
@WebAppConfiguration(value = "src/main/java/com/vision/project")
@WebMvcTest(LongPollingController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@Transactional
public class Users {
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
        rdp.addScript(new ClassPathResource("integrationTestsSql/FilesData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/RestaurantsData.sql"));
        rdp.execute(dataSource);
    }

    @BeforeAll
    public void setup() {
        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScript(new ClassPathResource("integrationTestsSql/RestaurantsData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/FilesData.sql"));
        rdp.execute(dataSource);

        UserModel admin = new UserModel("adminUser", "password", "ROLE_ADMIN");
        admin.setId(1);

        UserModel user = new UserModel("testUser", "password", "ROLE_USER");
        user.setId(2);

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
    public void assertConfig_assertUserController() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean("longPollingController"));
    }

    private final Restaurant restaurant = new Restaurant(1, "testName", "testAddress", "fast food", new HashSet<>());
    private final UserModel user = new UserModel("username", "username@gmail.com", "password","ROLE_USER", "firstname",
            "lastname", 25, "Bulgaria", restaurant);
    private final UserDto userDto = new UserDto(user);

    private RequestBuilder createMediaRegisterRequest(String url, String role, String username, String email, String token){
        MockHttpServletRequestBuilder request = post(url)
                .param("username", username)
                .param("email", email)
                .param("password", user.getPassword())
                .param("firstName", user.getFirstName())
                .param("lastName", user.getLastName())
                .param("age", String.valueOf(user.getAge()))
                .param("country", user.getCountry())
                .param("restaurantToken", "zeT2sqA1");

        if(token != null){
            request.header("Authorization", token);
        }

        userDto.setRole(role);
        userDto.setId(10);
        userDto.setEmail(email);

        return request;
    }

    @WithMockUser(value = "spring")
    @Test
    public void register() throws Exception {
        mockMvc.perform(createMediaRegisterRequest("/api/users/polling/register", "ROLE_USER",
                        "username", "username@gmail.com", null))
                .andExpect(status().isOk());

        enableUser(userDto.getId());
        checkDbForUser(userDto);
    }

    @WithMockUser(value = "spring")
    @Test
    public void registerAdmin() throws Exception {
        mockMvc.perform(createMediaRegisterRequest("/api/users/polling/auth/registerAdmin", "ROLE_ADMIN",
                        "username", "username@gmail.com", adminToken))
                .andExpect(content().string(containsString(objectMapper.writeValueAsString(userDto))));

        checkDbForUser(userDto);
    }

    @WithMockUser(value = "spring")
    @Test
    public void registerAdmin_WithUserThatIsNotAdmin_Unauthorized() throws Exception {
        mockMvc.perform(createMediaRegisterRequest("/api/users/polling/auth/registerAdmin", "ROLE_ADMIN",
                        "username", "username@gmail.com", userToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Access is denied"));
    }

    @Test
    public void register_WhenUsernameIsTaken() throws Exception {
        mockMvc.perform(createMediaRegisterRequest("/api/users/polling/register", "ROLE_USER",
                        "testUser", "username@gmail.com", null))
                .andExpect(content().string(containsString("Username is already taken.")));
    }

    private void checkDbForUser(UserDto user) throws Exception{
        mockMvc.perform(get("/api/users/polling/findById/" + user.getId()))
                .andExpect(content().string(objectMapper.writeValueAsString(user)));
    }

    private void enableUser(long id) throws Exception{
        mockMvc.perform(patch("/api/users/polling/auth/setEnabled/true/" + id)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }

    @Test
    public void login() throws Exception {
        UserModel user = new UserModel("adminUser", "adminUser@gmail.com", "password","ROLE_ADMIN", "firstName",
                "lastName", 25, "Bulgaria", restaurant);

        UserDto userDto = new UserDto(user);
        userDto.setId(1);
        userDto.setProfileImage("profileImage1.png");

        String result = mockMvc.perform(post("/api/users/polling/login/5")
                .contentType("Application/json")
                .content("{\"username\": \"adminUser\", \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto foundUser = objectMapper.readValue(result, UserDto.class);
        foundUser.setLastCheck(null);

        assertEquals(objectMapper.writeValueAsString(foundUser), objectMapper.writeValueAsString(userDto));

    }

    @Test
    public void login_WithWrongPassword_ShouldThrow() throws Exception {
        mockMvc.perform(post("/api/users/polling/login")
                        .contentType("Application/json")
                        .content("{\"username\": \"username\", \"password\": \"incorrect\"}"))
                .andExpect(status().is(401))
                .andExpect(content().string(containsString("Bad credentials")));
    }

    @Test
    public void login_WithWrongUsername_ShouldThrow() throws Exception {
        mockMvc.perform(post("/api/users/polling/login")
                        .contentType("Application/json")
                        .content("{\"username\": \"incorrect\", \"password\": \"password\"}"))
                .andExpect(status().is(401))
                .andExpect(content().string(containsString("Bad credentials")));
    }

    @Test
    void findById() throws Exception {
        UserDto user = new UserDto(new UserModel("adminUser", "adminUser@gmail.com", "password", "ROLE_ADMIN",
                "firstName", "lastName", 25, "Bulgaria", restaurant));
        user.setId(1);
        user.setProfileImage("profileImage1.png");

        checkDbForUser(user);
    }

    @Test
    void findById_WithNonExistentId() throws Exception {
        mockMvc.perform(get("/api/users/polling/findById/222"))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeUserInfo() throws Exception {
        UserSpec userSpec = new UserSpec(1, "newUsername", "newUsername@gmail.com", "newFirstName",
                "newLastName", 26, "newCountry");
        UserDto userDto = new UserDto(userSpec, "ROLE_ADMIN");
        userDto.setRestaurant(new RestaurantDto(restaurant));
        userDto.setProfileImage("profileImage1.png");

        mockMvc.perform(post("/api/users/polling/auth/changeUserInfo")
                .header("Authorization", adminToken)
                .contentType("Application/json")
                .content(objectMapper.writeValueAsString(userSpec)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(userDto)));

        checkDbForUser(userDto);
    }

    @Test
    void registerAdmin_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(createMediaRegisterRequest("/api/users/polling/auth/registerAdmin", "ROLE_ADMIN",
                        "username", "username@gmail.com", null))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void registerAdmin_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(createMediaRegisterRequest("/api/users/polling/auth/registerAdmin", "ROLE_ADMIN",
                        "username", "username@gmail.com", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void changeUserInfo_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/users/polling/auth/changeUserInfo"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void changeUserInfo_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/users/polling/auth/changeUserInfo")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void searchForUsers_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(get("/api/users/polling/auth/searchForUsers/2"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void searchForUsers_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(get("/api/users/polling/auth/searchForUsers/2")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void changePassword_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/users/polling/auth/changePassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void changePassword_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(get("/api/users/polling/auth/changePassword")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }
}
