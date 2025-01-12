package be.pxl.services;

import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.request.CommentRequest;
import be.pxl.services.domain.dto.response.CommentResponse;
import be.pxl.services.repository.CommentRepository;
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
public class CommentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Container
    private static MySQLContainer<?> sqlContainer = new MySQLContainer<>("mysql:5.7.37");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        Comment comment1 = new Comment(1L, 101L, "Author1", "Content1");
        Comment comment2 = new Comment(2L, 101L, "Author2", "Content2");
        Comment comment3 = new Comment(3L, 102L, "Author3", "Content3");
        commentRepository.saveAll(List.of(comment1, comment2, comment3));
    }

    @Test
    void shouldCreateComment_whenRoleIsValid() throws Exception {
        CommentRequest commentRequest = new CommentRequest("New Comment Content", 101L, "Author");

        mockMvc.perform(MockMvcRequestBuilders.post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Role", "gebruiker")
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnForbidden_whenRoleIsInvalidForCreate() throws Exception {
        CommentRequest commentRequest = new CommentRequest("New Comment Content", 101L, "Author");

        mockMvc.perform(MockMvcRequestBuilders.post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Role", "invalidRole")
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetCommentsForPost_whenRoleIsValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/comments/101")
                        .header("Role", "gebruiker"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbidden_whenRoleIsInvalidForGetComments() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/comments/101")
                        .header("Role", "invalidRole"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateComment_whenRoleIsValid() throws Exception {
        Comment comment = commentRepository.findAll().get(0);
        CommentRequest updatedRequest = new CommentRequest("Updated Content", comment.getPostId(), "Updated Author");

        mockMvc.perform(MockMvcRequestBuilders.patch("/comments/" + comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Role", "redacteur")
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbidden_whenRoleIsInvalidForUpdate() throws Exception {
        Comment comment = commentRepository.findAll().get(0);
        CommentRequest updatedRequest = new CommentRequest("Updated Content", comment.getPostId(), "Updated Author");

        mockMvc.perform(MockMvcRequestBuilders.patch("/comments/" + comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Role", "invalidRole")
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDeleteComment_whenRoleIsValid() throws Exception {
        Comment comment = commentRepository.findAll().get(0);

        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/" + comment.getId())
                        .header("Role", "gebruiker"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnForbidden_whenRoleIsInvalidForDelete() throws Exception {
        Comment comment = commentRepository.findAll().get(0);

        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/" + comment.getId())
                        .header("Role", "invalidRole"))
                .andExpect(status().isForbidden());
    }
}
