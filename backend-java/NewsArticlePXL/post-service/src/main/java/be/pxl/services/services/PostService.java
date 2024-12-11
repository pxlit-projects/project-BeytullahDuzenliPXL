package be.pxl.services.services;

import be.pxl.services.client.NotificationClient;
import be.pxl.services.domain.Post;
import be.pxl.services.model.NotificationRequest;
import be.pxl.services.model.dto.PostRequest;
import be.pxl.services.model.dto.PostResponse;
import be.pxl.services.enums.Status;
import be.pxl.services.exceptions.NotFoundException;
import be.pxl.services.repository.PostRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService{
    private final PostRepository postRepository;
    private final NotificationClient notificationClient;


    @Override
    public PostResponse createPost(PostRequest postRequest) {
        if (postRequest.getStatus() != Status.DRAFT && postRequest.getStatus() != Status.SUBMITTED) {
            throw new IllegalArgumentException("Invalid initial status. Only DRAFT or SUBMITTED are allowed.");
        }

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .author(postRequest.getAuthor())
                .category(postRequest.getCategory())
                .status(postRequest.getStatus())
                .creationDate(LocalDateTime.now())
                .build();

        postRepository.save(post);

        NotificationRequest notificationRequest = NotificationRequest.builder().message("Post created").sender(post.getAuthor().toString()).build();
        notificationClient.sendNotification(notificationRequest);

        return mapToPostResponse(post);
    }

    @Override
    public PostResponse updatePost(Long postId, PostRequest postRequest) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found"));

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setCategory(postRequest.getCategory());

        postRepository.save(post);

        return mapToPostResponse(post);
    }

    @Override
    public List<PostResponse> getPostsByStatus(Status status) {
        List<Post> posts = postRepository.findByStatus(status);
        return posts.stream()
                .map(this::mapToPostResponse)
                .toList();
    }

    @Override
    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post with ID " + postId + " not found"));
        return mapToPostResponse(post);
    }

    @Override
    public PostResponse updateStatus(Long postId, Status status) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post with ID " + postId + " not found"));
        post.setStatus(status);
        postRepository.save(post);
        return mapToPostResponse(post);
    }

    private PostResponse mapToPostResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getCategory(),
                post.getStatus(),
                post.getCreationDate()
        );
    }
}