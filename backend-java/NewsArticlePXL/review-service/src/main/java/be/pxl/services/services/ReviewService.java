package be.pxl.services.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Review;
import be.pxl.services.domain.dto.Request.ReviewRequest;
import be.pxl.services.domain.dto.Response.ReviewMessage;
import be.pxl.services.domain.dto.Response.ReviewResponse;
import be.pxl.services.enums.Status;
import be.pxl.services.domain.dto.Request.NotificationRequest;
import be.pxl.services.repository.ReviewRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final PostClient postClient;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewRepository reviewRepository;

    @Override
    public ReviewResponse getReviewForPost(Long postId, String role) {
        logger.info("Fetching review for post with ID: {}", postId);

        return reviewRepository.findByPostId(postId)
                .map(this::mapToReviewResponse)
                .orElseThrow(() -> {
                    logger.error("Review for post ID: {} not found", postId);
                    return new NotFoundException("Review for post ID " + postId + " not found");
                });
    }

    @Override
    public ReviewResponse makeReviewForPost(ReviewRequest reviewRequest, String role) {
        Long postId = reviewRequest.getPostId();
        logger.info("Attempting to make or update a review for post with ID: {}", postId);

        if (postClient.getPostById(reviewRequest.getPostId(), role) == null) {
            logger.error("Post with ID: {} not found", reviewRequest.getPostId());
            throw new NotFoundException("Post with ID " + reviewRequest.getPostId() + " not found");
        }

        Review review = reviewRepository.findByPostId(postId)
                .map(existingReview -> updateReview(existingReview, reviewRequest))
                .orElseGet(() -> createNewReview(reviewRequest));

        reviewRepository.save(review);
        logger.info("Review for post with ID: {} saved successfully", postId);

        sendMessageToQueue(review);
        sendNotificationToAuthor(review);

        return mapToReviewResponse(review);
    }

    private Review updateReview(Review existingReview, ReviewRequest reviewRequest) {
        logger.info("Review already exists for post ID: {}. Updating review.", reviewRequest.getPostId());
        existingReview.setReason(reviewRequest.getReason());
        existingReview.setPostAuthor(reviewRequest.getPostAuthor());
        existingReview.setAuthor(reviewRequest.getAuthor());
        existingReview.setStatus(reviewRequest.getStatus());
        return existingReview;
    }

    private Review createNewReview(ReviewRequest reviewRequest) {
        logger.info("Creating a new review for post ID: {}.", reviewRequest.getPostId());
        return Review.builder()
                .postId(reviewRequest.getPostId())
                .reason(reviewRequest.getReason())
                .postAuthor(reviewRequest.getPostAuthor())
                .author(reviewRequest.getAuthor())
                .status(reviewRequest.getStatus())
                .build();
    }

    private void sendMessageToQueue(Review review) {
        ReviewMessage reviewMessage = new ReviewMessage(review.getPostId(), review.getStatus()
        );
        rabbitTemplate.convertAndSend("reviewQueue", reviewMessage);
        logger.info("Review message sent to queue for post ID: {}", review.getPostId());
    }

    private void sendNotificationToAuthor(Review review) {
        NotificationRequest notificationRequest = new NotificationRequest(
                review.getStatus() == Status.ACCEPTED
                        ? "Je post (" + review.getPostId() + ") is geaccepteerd"
                        : "Je post (" + review.getPostId() + ") is geweigerd",
                review.getAuthor(),
                review.getPostAuthor()
        );
        postClient.sendNotification(notificationRequest);
        logger.info("Notification sent to author of post ID: {}", review.getPostId());
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        return new ReviewResponse(
                review.getPostId(),
                review.getReason(),
                review.getPostAuthor(),
                review.getAuthor(),
                review.getStatus()
        );
    }
}