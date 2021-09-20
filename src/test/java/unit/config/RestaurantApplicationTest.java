package unit.config;

import com.vision.project.RestaurantApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class RestaurantApplicationTest {
    @Test
    public void start(){
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {

            mocked.when(() -> SpringApplication.run(RestaurantApplication.class,
                            "arg1", "arg2"))
                    .thenReturn(Mockito.mock(ConfigurableApplicationContext.class));

            RestaurantApplication.main(new String[] { "arg1", "arg2" });

            mocked.verify(() -> { SpringApplication.run(RestaurantApplication.class,
                    "arg1", "arg2"); });

        }
    }
}
