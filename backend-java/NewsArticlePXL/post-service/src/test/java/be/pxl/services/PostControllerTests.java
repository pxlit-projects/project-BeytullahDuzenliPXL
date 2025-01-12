package be.pxl.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.request.PostRequest;
import be.pxl.services.enums.Status;
import be.pxl.services.repository.PostRepository;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class PostControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Container
    private static MySQLContainer sqlContainer = new MySQLContainer("mysql:5.7.37");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @BeforeEach
    public void setUp() {
        postRepository.deleteAll();

        Post post = new Post(1L, "Title", "Content", "Author", Status.DRAFT, LocalDateTime.now());
        Post post2 = new Post(2L, "Title2", "Content2", "Author2", Status.SUBMITTED, LocalDateTime.now());
        Post post3 = new Post(3L, "Title3", "Content3", "Author3", Status.PUBLISHED, LocalDateTime.now());
        postRepository.saveAll(List.of(post, post2, post3));
    }

    @Test
    void shouldCreatePost_whenRoleIsRedacteur() throws Exception {
        PostRequest postRequest = new PostRequest("Title", "Content", "Author", Status.DRAFT);

        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Role", "redacteur")
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnForbidden_whenRoleIsNotRedacteur() throws Exception {
        PostRequest postRequest = new PostRequest("Title", "Content", "Author", Status.DRAFT);

        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Role", "gebruiker")
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetPostById_whenRoleIsValid() throws Exception {
        Post post = postRepository.findAll().get(0);

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + post.getId())
                        .header("Role", "redacteur"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbiddenOnGetPostById_whenRoleIsInvalid() throws Exception {
        Post post = postRepository.findAll().get(0);

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + post.getId())
                        .header("Role", "invalidRole"))
                .andExpect(status().isForbidden());
    }
    @Test
    void shouldGetPostsByStatus_whenRoleIsRedacteur() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/status/PUBLISHED")
                        .header("Role", "redacteur"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbiddenOnGetPostsByStatus_whenRoleIsNotRedacteur() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/status/PUBLISHED")
                        .header("Role", "gebruiker"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdatePost_whenRoleIsRedacteur() throws Exception {
        Post post = postRepository.findAll().get(0);
        PostRequest updatedRequest = new PostRequest("Updated Title", "Updated Content", post.getAuthor(), Status.SUBMITTED);

        mockMvc.perform(MockMvcRequestBuilders.put("/posts/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Role", "redacteur")
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbiddenOnUpdatePost_whenRoleIsNotRedacteur() throws Exception {
        Post post = postRepository.findAll().get(0);
        PostRequest updatedRequest = new PostRequest("Updated Title", "Updated Content", post.getAuthor(), Status.SUBMITTED);

        mockMvc.perform(MockMvcRequestBuilders.put("/posts/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Role", "gebruiker")
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFilterPosts_whenRoleIsValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .param("status", "PUBLISHED")
                        .header("Role", "redacteur"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbiddenOnGetFilteredPosts_whenRoleHeaderIsMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .param("status", "PUBLISHED")
                        .header("Role", "fakeRole"))
                .andExpect(status().isForbidden());
    }

//    @Test
//    void shouldReturnBadRequest_whenCreatingPostWithMissingFields() throws Exception {
//        PostRequest postRequest = new PostRequest("", "", "Author", Status.DRAFT);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Role", "redacteur")
//                        .content(objectMapper.writeValueAsString(postRequest)))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void shouldReturnBadRequestOnGetPostsByInvalidStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/status/INVALID_STATUS")
                        .header("Role", "redacteur"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundOnUpdateNonExistentPost() throws Exception {
        PostRequest updatedRequest = new PostRequest("Updated Title", "Updated Content", "Author", Status.DRAFT);

        mockMvc.perform(MockMvcRequestBuilders.put("/posts/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Role", "redacteur")
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdatePostStatus_whenRoleIsRedacteur() throws Exception {
        Post post = postRepository.findAll().get(0);

        mockMvc.perform(MockMvcRequestBuilders.patch("/posts/" + post.getId() + "/status")
                        .param("status", "PUBLISHED")
                        .header("Role", "redacteur"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbiddenOnUpdatePostStatus_whenRoleIsNotRedacteur() throws Exception {
        Post post = postRepository.findAll().get(0);

        mockMvc.perform(MockMvcRequestBuilders.patch("/posts/" + post.getId() + "/status")
                        .param("status", "PUBLISHED")
                        .header("Role", "gebruiker"))
                .andExpect(status().isForbidden());
    }
}