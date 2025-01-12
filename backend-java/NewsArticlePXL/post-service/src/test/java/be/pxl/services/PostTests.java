package be.pxl.services;
import be.pxl.services.domain.Post;
import be.pxl.services.enums.Status;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

public class PostTests {
    @Test
    void shouldCreatePostWithEmptyBuilder() {
        Post post = Post.builder().build();

        assertThat(post).isNotNull();
        assertThat(post.getId()).isNull();
        assertThat(post.getTitle()).isNull();
        assertThat(post.getContent()).isNull();
        assertThat(post.getAuthor()).isNull();
        assertThat(post.getStatus()).isNull();
        assertThat(post.getCreationDate()).isNull();
    }

    @Test
    void shouldCreatePostUsingDefaultConstructorOnly() {
        Post post = new Post(); // Alleen de default constructor aanroepen

        assertThat(post).isNotNull();
        assertThat(post.getId()).isNull();
        assertThat(post.getTitle()).isNull();
        assertThat(post.getContent()).isNull();
        assertThat(post.getAuthor()).isNull();
        assertThat(post.getStatus()).isNull();
        assertThat(post.getCreationDate()).isNull();
    }

    @Test
    void shouldCreatePostUsingAllArgsConstructor() {
        LocalDateTime creationDate = LocalDateTime.now();
        Post post = new Post(1L, "Test Title", "Test Content", "Test Author", Status.DRAFT, creationDate);

        assertThat(post.getId()).isEqualTo(1L);
        assertThat(post.getTitle()).isEqualTo("Test Title");
        assertThat(post.getContent()).isEqualTo("Test Content");
        assertThat(post.getAuthor()).isEqualTo("Test Author");
        assertThat(post.getStatus()).isEqualTo(Status.DRAFT);
        assertThat(post.getCreationDate()).isEqualTo(creationDate);
    }

    @Test
    void shouldCreatePostUsingDefaultConstructor() {
        Post post = new Post(); // Default constructor uitvoeren
        post.setId(1L);
        post.setTitle("Default Constructor Title");
        post.setContent("Default Constructor Content");
        post.setAuthor("Default Constructor Author");
        post.setStatus(Status.DRAFT);
        post.setCreationDate(LocalDateTime.now());

        assertThat(post.getId()).isEqualTo(1L);
        assertThat(post.getTitle()).isEqualTo("Default Constructor Title");
    }

    @Test
    void shouldBuildPostUsingBuilder() {
        LocalDateTime creationDate = LocalDateTime.now();
        Post post = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .author("Test Author")
                .status(Status.DRAFT)
                .creationDate(creationDate)
                .build();

        assertThat(post.getId()).isEqualTo(1L);
        assertThat(post.getTitle()).isEqualTo("Test Title");
        assertThat(post.getContent()).isEqualTo("Test Content");
        assertThat(post.getAuthor()).isEqualTo("Test Author");
        assertThat(post.getStatus()).isEqualTo(Status.DRAFT);
        assertThat(post.getCreationDate()).isEqualTo(creationDate);
    }

    @Test
    void shouldSetAndGetValues() {
        LocalDateTime creationDate = LocalDateTime.now();
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Set Title");
        post.setContent("Set Content");
        post.setAuthor("Set Author");
        post.setStatus(Status.SUBMITTED);
        post.setCreationDate(creationDate);

        assertThat(post.getId()).isEqualTo(1L);
        assertThat(post.getTitle()).isEqualTo("Set Title");
        assertThat(post.getContent()).isEqualTo("Set Content");
        assertThat(post.getAuthor()).isEqualTo("Set Author");
        assertThat(post.getStatus()).isEqualTo(Status.SUBMITTED);
        assertThat(post.getCreationDate()).isEqualTo(creationDate);
    }
}