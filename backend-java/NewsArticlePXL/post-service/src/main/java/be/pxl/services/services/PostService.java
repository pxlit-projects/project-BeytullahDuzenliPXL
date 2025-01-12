package be.pxl.services.services;

import be.pxl.services.domain.Notification;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.request.NotificationRequest;
import be.pxl.services.domain.dto.response.NotificationResponse;
import be.pxl.services.domain.dto.response.ReviewMessage;
import be.pxl.services.exceptions.InvalidStatusException;
import be.pxl.services.domain.dto.request.PostRequest;
import be.pxl.services.domain.dto.response.PostResponse;
import be.pxl.services.enums.Status;
import be.pxl.services.exceptions.NotFoundException;
import be.pxl.services.repository.NotificationRepository;
import be.pxl.services.repository.PostRepository;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService{
    private final PostRepository postRepository;
    private final NotificationRepository notificationRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Override
    public PostResponse createPost(PostRequest postRequest) {
        logger.info("Attempting to create a new post with title: {}", postRequest.getTitle());

        if (postRequest.getTitle() == null || postRequest.getContent() == null || postRequest.getAuthor() == null) {
            logger.error("Post request contains null fields");
            throw new IllegalArgumentException("Post fields cannot be null");
        }

        if (postRequest.getStatus() != Status.DRAFT && postRequest.getStatus() != Status.SUBMITTED) {
            logger.error("Invalid initial status: {}", postRequest.getStatus());
            throw new InvalidStatusException("Invalid initial status. Only DRAFT or SUBMITTED are allowed.");
        }

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .author(postRequest.getAuthor())
                .status(postRequest.getStatus())
                .creationDate(LocalDateTime.now())
                .build();

        postRepository.save(post);
        logger.info("Post with ID: {} created successfully", post.getId());
        rabbitTemplate.convertAndSend("postQueue", post.getId());
        return mapToPostResponse(post);
    }

    @Override
    public PostResponse updatePost(Long postId, PostRequest postRequest) {
        logger.info("Attempting to update post with ID: {}", postId);

        if (postRequest.getStatus() == null) {
            logger.error("Status cannot be null for post ID: {}", postId);
            throw new IllegalArgumentException("Status cannot be null");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post with ID: {} not found", postId);
                    return new NotFoundException("Post not found");
                });

        if (postRequest.getStatus() != Status.DRAFT && postRequest.getStatus() != Status.SUBMITTED) {
            logger.error("Invalid status: {} for updating post", postRequest.getStatus());
            throw new InvalidStatusException("Invalid initial status. Only DRAFT or SUBMITTED are allowed.");
        }

        post.setTitle(postRequest.getTitle());
        post.setAuthor(postRequest.getAuthor());
        post.setContent(postRequest.getContent());
        post.setStatus(postRequest.getStatus());
        postRepository.save(post);

        logger.info("Post with ID: {} updated successfully", postId);
        return mapToPostResponse(post);
    }

    @Override
    public List<PostResponse> getPostsByStatus(Status status) {
        logger.info("Fetching posts with status: {}", status);
        List<Post> posts = postRepository.findByStatus(status);
        logger.debug("Found {} posts with status: {}", posts.size(), status);
        return posts.stream()
                .map(this::mapToPostResponse)
                .toList();
    }

    @Override
    public PostResponse getPostById(Long postId) {
        logger.info("Fetching post with ID: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post with ID: {} not found", postId);
                    return new NotFoundException("Post with ID " + postId + " not found");
                });

        logger.info("Post with ID: {} fetched successfully", postId);
        return mapToPostResponse(post);
    }

    @Override
    public PostResponse updateStatus(Long postId, Status status) {
        logger.info("Updating status of post with ID: {} to {}", postId, status);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post with ID: {} not found", postId);
                    return new NotFoundException("Post with ID " + postId + " not found");
                });

        post.setStatus(status);
        postRepository.save(post);

        logger.info("Status of post with ID: {} updated to {}", postId, status);
        return mapToPostResponse(post);
    }

    @Override
    public List<PostResponse> getAllPosts(String content, String author, LocalDateTime fromDate, LocalDateTime toDate, Status status) {
        logger.info("Fetching posts with filters - Content: {}, Author: {}, FromDate: {}, ToDate: {}, Status: {}",
                content, author, fromDate, toDate, status);
        List<Post> posts = postRepository.findFilteredPosts(
                content == null || content.isBlank() ? null : content,
                author == null || author.isBlank() ? null : author,
                fromDate,
                toDate, status
        );
        logger.debug("Found {} posts with given filters", posts.size());
        return posts.stream()
                .map(this::mapToPostResponse)
                .toList();
    }

    private PostResponse mapToPostResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getStatus(),
                post.getCreationDate()
        );
    }

    @RabbitListener(queues = "reviewQueue")
    public void getReviews(ReviewMessage reviewMessage) {
        logger.info("Received review message for post ID: {}", reviewMessage.getId());

        // Find the post by ID
        Post post = postRepository.findById(reviewMessage.getId())
                .orElseThrow(() -> {
                    logger.error("Post with ID: {} not found", reviewMessage.getId());
                    return new NotFoundException("Post with ID " + reviewMessage.getId() + " not found");
                });

        // Update the status of the post based on the review status
        if (reviewMessage.getStatus() == Status.ACCEPTED) {
            post.setStatus(Status.PUBLISHED);
        } else if (reviewMessage.getStatus() == Status.REJECTED) {
            post.setStatus(Status.REJECTED);
        }

        // Save the updated post
        postRepository.save(post);
        logger.info("Updated post ID: {} with status: {}", post.getId(), post.getStatus());
    }

    @Override
    public void getNotification(NotificationRequest notificationRequest) {
        Notification notification = Notification.builder()
                .message(notificationRequest.getMessage())
                .author(notificationRequest.getAuthor())
                .postAuthor(notificationRequest.getPostAuthor())
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getNotificationsForAuthor(String author) {
        List<Notification> notifications = notificationRepository.findNotificationsByPostAuthor(author);
        return notifications.stream()
                .map(notification -> new NotificationResponse(notification.getMessage(), notification.getAuthor(), notification.getPostAuthor()))
                .toList();
    }
}