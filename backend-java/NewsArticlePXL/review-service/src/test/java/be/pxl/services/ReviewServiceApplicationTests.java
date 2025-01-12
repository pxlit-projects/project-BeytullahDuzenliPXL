package be.pxl.services;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
public class ReviewServiceApplicationTests {
    @Test
    void mainMethodShouldRun() {
        // Mock SpringApplication.run zodat de applicatie niet echt wordt gestart
        try (var mockedSpringApplication = mockStatic(SpringApplication.class)) {
            mockedSpringApplication.when(() ->
                    SpringApplication.run(ReviewServiceApplication.class, new String[] {})
            ).thenReturn(null);

            // Roep de main methode aan
            ReviewServiceApplication.main(new String[] {});

            // Controleer dat SpringApplication.run werd aangeroepen
            mockedSpringApplication.verify(() ->
                    SpringApplication.run(ReviewServiceApplication.class, new String[] {})
            );
        }
    }
}
