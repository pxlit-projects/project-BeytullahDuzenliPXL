package be.pxl.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.request.PostRequest;
import be.pxl.services.domain.dto.response.PostResponse;
import be.pxl.services.domain.dto.response.ReviewMessage;
import be.pxl.services.enums.Status;
import be.pxl.services.repository.PostRepository;
import be.pxl.services.services.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.when;
import be.pxl.services.exceptions.InvalidStatusException;
import be.pxl.services.exceptions.NotFoundException;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PostServiceTests {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreatePostSuccessfully() {
        PostRequest postRequest = new PostRequest("Title", "Content", "Author", Status.DRAFT);

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setId(1L); // Stel een mock ID in
            return post;
        });

        PostResponse response = postService.createPost(postRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Title");
        assertThat(response.getId()).isEqualTo(1L);
        verify(postRepository).save(any(Post.class));
        verify(rabbitTemplate).convertAndSend("postQueue", 1L); // Controleer dat het ID juist is
    }

    @Test
    void shouldThrowInvalidStatusExceptionWhenCreatingPostWithInvalidStatus() {
        PostRequest postRequest = new PostRequest("Title", "Content", "Author", Status.PUBLISHED);

        assertThatThrownBy(() -> postService.createPost(postRequest))
                .isInstanceOf(InvalidStatusException.class)
                .hasMessage("Invalid initial status. Only DRAFT or SUBMITTED are allowed.");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void shouldUpdatePostSuccessfully() {
        Long postId = 1L;
        PostRequest postRequest = new PostRequest("Updated Title", "Updated Content", "Author", Status.SUBMITTED);
        Post existingPost = Post.builder()
                .id(postId)
                .title("Title")
                .content("Content")
                .author("Author")
                .status(Status.DRAFT)
                .creationDate(LocalDateTime.now())
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenReturn(existingPost);

        PostResponse response = postService.updatePost(postId, postRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Updated Title");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistentPost() {
        Long postId = 1L;
        PostRequest postRequest = new PostRequest("Updated Title", "Updated Content", "Author", Status.SUBMITTED);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.updatePost(postId, postRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Post not found");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void shouldGetPostsByStatus() {
        when(postRepository.findByStatus(Status.DRAFT)).thenReturn(List.of(
                Post.builder().id(1L).title("Title").status(Status.DRAFT).build()
        ));

        List<PostResponse> posts = postService.getPostsByStatus(Status.DRAFT);

        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getStatus()).isEqualTo(Status.DRAFT);
    }

    @Test
    void shouldGetPostByIdSuccessfully() {
        Long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .title("Title")
                .content("Content")
                .author("Author")
                .status(Status.DRAFT)
                .creationDate(LocalDateTime.now())
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        PostResponse response = postService.getPostById(postId);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Title");
    }

    @Test
    void shouldThrowNotFoundExceptionWhenGettingNonExistentPost() {
        Long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPostById(postId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Post with ID " + postId + " not found");
    }

    @Test
    void shouldUpdateStatusSuccessfully() {
        Long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .title("Title")
                .status(Status.DRAFT)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponse response = postService.updateStatus(postId, Status.PUBLISHED);

        assertThat(response.getStatus()).isEqualTo(Status.PUBLISHED);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingPostWithNullFields() {
        PostRequest postRequest = new PostRequest(null, null, null, Status.DRAFT);

        assertThatThrownBy(() -> postService.createPost(postRequest))
                .isInstanceOf(IllegalArgumentException.class) // Pas hier de exception aan
                .hasMessage("Post fields cannot be null"); // Controleer de foutmelding

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void shouldThrowInvalidStatusExceptionWhenUpdatingPostWithInvalidStatus() {
        Long postId = 1L;
        PostRequest postRequest = new PostRequest("Updated Title", "Updated Content", "Author", Status.PUBLISHED);

        Post existingPost = Post.builder()
                .id(postId)
                .title("Title")
                .content("Content")
                .author("Author")
                .status(Status.DRAFT)
                .creationDate(LocalDateTime.now())
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        assertThatThrownBy(() -> postService.updatePost(postId, postRequest))
                .isInstanceOf(InvalidStatusException.class)
                .hasMessage("Invalid initial status. Only DRAFT or SUBMITTED are allowed.");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoPostsFoundForStatus() {
        when(postRepository.findByStatus(Status.DRAFT)).thenReturn(List.of());

        List<PostResponse> posts = postService.getPostsByStatus(Status.DRAFT);

        assertThat(posts).isEmpty();
        verify(postRepository).findByStatus(Status.DRAFT);
    }

    @Test
    void shouldGetAllPostsWithFilters() {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(1);
        LocalDateTime toDate = LocalDateTime.now();

        when(postRepository.findFilteredPosts("Content", "Author", fromDate, toDate, Status.PUBLISHED))
                .thenReturn(List.of(
                        Post.builder()
                                .id(1L)
                                .title("Title")
                                .content("Content")
                                .author("Author")
                                .status(Status.PUBLISHED)
                                .creationDate(LocalDateTime.now())
                                .build()
                ));

        List<PostResponse> posts = postService.getAllPosts(
                "Content",
                "Author",
                fromDate,
                toDate,
                Status.PUBLISHED
        );

        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getStatus()).isEqualTo(Status.PUBLISHED);
        verify(postRepository).findFilteredPosts("Content", "Author", fromDate, toDate, Status.PUBLISHED);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPostWithNullStatus() {
        Long postId = 1L;

        // Maak een PostRequest met een null status
        PostRequest postRequest = new PostRequest("Updated Title", "Updated Content", "Author", null);

        // Mock een bestaande post in de repository
        Post existingPost = Post.builder()
                .id(postId)
                .title("Title")
                .content("Content")
                .author("Author")
                .status(Status.DRAFT)
                .creationDate(LocalDateTime.now())
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        // Controleer dat de juiste exception wordt gegooid
        assertThatThrownBy(() -> postService.updatePost(postId, postRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Status cannot be null");

        // Controleer dat de post niet wordt opgeslagen
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void shouldUpdatePostBasedOnReviewMessage() {
        ReviewMessage reviewMessage = new ReviewMessage(1L, Status.ACCEPTED);
        Post post = Post.builder()
                .id(reviewMessage.getId())
                .title("Title")
                .content("Content")
                .author("Author")
                .status(Status.SUBMITTED)
                .creationDate(LocalDateTime.now())
                .build();

        when(postRepository.findById(reviewMessage.getId())).thenReturn(Optional.of(post));

        postService.getReviews(reviewMessage);

        verify(postRepository).save(post);
        assertThat(post.getStatus()).isEqualTo(Status.PUBLISHED);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenReviewPostDoesNotExist() {
        ReviewMessage reviewMessage = new ReviewMessage(1L, Status.REJECTED);

        when(postRepository.findById(reviewMessage.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getReviews(reviewMessage))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Post with ID " + reviewMessage.getId() + " not found");

        verify(postRepository, never()).save(any(Post.class));
    }
}