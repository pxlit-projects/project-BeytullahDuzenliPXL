package be.pxl.services;

import be.pxl.services.domain.Review;
import be.pxl.services.enums.Status;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ReviewTests {

    @Test
    void shouldCreateReviewUsingAllArgsConstructor() {
        LocalDateTime creationDate = LocalDateTime.now();
        Review review = new Review(
                1L,
                10L,
                "Reason for Review",
                "Post Author",
                "Reviewer Author",
                Status.SUBMITTED,
                creationDate
        );

        assertThat(review.getId()).isEqualTo(1L);
        assertThat(review.getPostId()).isEqualTo(10L);
        assertThat(review.getReason()).isEqualTo("Reason for Review");
        assertThat(review.getPostAuthor()).isEqualTo("Post Author");
        assertThat(review.getAuthor()).isEqualTo("Reviewer Author");
        assertThat(review.getStatus()).isEqualTo(Status.SUBMITTED);
        assertThat(review.getCreationDate()).isEqualTo(creationDate);
    }

    @Test
    void shouldCreateReviewUsingDefaultConstructor() {
        Review review = new Review();
        LocalDateTime creationDate = LocalDateTime.now();

        review.setId(1L);
        review.setPostId(10L);
        review.setReason("Default Reason");
        review.setPostAuthor("Default Post Author");
        review.setAuthor("Default Reviewer");
        review.setStatus(Status.DRAFT);
        review.setCreationDate(creationDate);

        assertThat(review.getId()).isEqualTo(1L);
        assertThat(review.getPostId()).isEqualTo(10L);
        assertThat(review.getReason()).isEqualTo("Default Reason");
        assertThat(review.getPostAuthor()).isEqualTo("Default Post Author");
        assertThat(review.getAuthor()).isEqualTo("Default Reviewer");
        assertThat(review.getStatus()).isEqualTo(Status.DRAFT);
        assertThat(review.getCreationDate()).isEqualTo(creationDate);
    }

    @Test
    void shouldBuildReviewUsingBuilder() {
        LocalDateTime creationDate = LocalDateTime.now();
        Review review = Review.builder()
                .id(1L)
                .postId(10L)
                .reason("Built Reason")
                .postAuthor("Built Post Author")
                .author("Built Reviewer")
                .status(Status.ACCEPTED)
                .creationDate(creationDate)
                .build();

        assertThat(review.getId()).isEqualTo(1L);
        assertThat(review.getPostId()).isEqualTo(10L);
        assertThat(review.getReason()).isEqualTo("Built Reason");
        assertThat(review.getPostAuthor()).isEqualTo("Built Post Author");
        assertThat(review.getAuthor()).isEqualTo("Built Reviewer");
        assertThat(review.getStatus()).isEqualTo(Status.ACCEPTED);
        assertThat(review.getCreationDate()).isEqualTo(creationDate);
    }

    @Test
    void shouldSetAndGetValues() {
        LocalDateTime creationDate = LocalDateTime.now();
        Review review = new Review();

        review.setId(1L);
        review.setPostId(20L);
        review.setReason("Set Reason");
        review.setPostAuthor("Set Post Author");
        review.setAuthor("Set Reviewer");
        review.setStatus(Status.REJECTED);
        review.setCreationDate(creationDate);

        assertThat(review.getId()).isEqualTo(1L);
        assertThat(review.getPostId()).isEqualTo(20L);
        assertThat(review.getReason()).isEqualTo("Set Reason");
        assertThat(review.getPostAuthor()).isEqualTo("Set Post Author");
        assertThat(review.getAuthor()).isEqualTo("Set Reviewer");
        assertThat(review.getStatus()).isEqualTo(Status.REJECTED);
        assertThat(review.getCreationDate()).isEqualTo(creationDate);
    }

    @Test
    void shouldCreateReviewWithEmptyBuilder() {
        Review review = Review.builder().build();

        assertThat(review).isNotNull();
        assertThat(review.getId()).isNull();
        assertThat(review.getPostId()).isNull();
        assertThat(review.getReason()).isNull();
        assertThat(review.getPostAuthor()).isNull();
        assertThat(review.getAuthor()).isNull();
        assertThat(review.getStatus()).isNull();
        assertThat(review.getCreationDate()).isNull();
    }
}