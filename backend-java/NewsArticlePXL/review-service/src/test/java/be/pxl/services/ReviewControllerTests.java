package be.pxl.services;

import be.pxl.services.domain.Review;
import be.pxl.services.domain.dto.Request.ReviewRequest;
import be.pxl.services.domain.dto.Response.ReviewResponse;
import be.pxl.services.enums.Status;
import be.pxl.services.repository.ReviewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ReviewControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    @Container
    private static final MySQLContainer<?> sqlContainer = new MySQLContainer<>("mysql:5.7.37");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
    }

    @Test
    void shouldMakeReview_whenRoleIsRedacteur() throws Exception {
        // Arrange
        ReviewRequest reviewRequest = ReviewRequest.builder()
                .reason("Great post!")
                .author("Redacteur")
                .postAuthor("Author")
                .postId(1L)
                .status(Status.ACCEPTED)
                .build();

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Role", "redacteur")
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbidden_whenRoleIsNotRedacteurMakingReview() throws Exception {
        // Arrange
        ReviewRequest reviewRequest = ReviewRequest.builder()
                .reason("Great post!")
                .author("User")
                .postAuthor("Author")
                .postId(1L)
                .status(Status.ACCEPTED)
                .build();

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Role", "gebruiker")
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetReview_whenRoleIsRedacteur() throws Exception {
        // Arrange
        Long postId = 1L;
        Review review = Review.builder()
                .postId(postId)
                .reason("Good post!")
                .postAuthor("Post Author")
                .author("Redacteur")
                .status(Status.ACCEPTED)
                .build();

        // Sla de Review entity op in de repository
        reviewRepository.save(review);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/reviews/" + postId)
                        .header("Role", "redacteur"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbidden_whenRoleIsNotRedacteurGettingReview() throws Exception {
        // Arrange
        Long postId = 1L;

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/reviews/" + postId)
                        .header("Role", "gebruiker"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnBadRequest_whenGettingReviewWithInvalidPostId() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/reviews/invalidId")
                        .header("Role", "redacteur"))
                .andExpect(status().isBadRequest());
    }
}