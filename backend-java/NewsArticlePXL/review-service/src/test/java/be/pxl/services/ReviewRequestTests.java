package be.pxl.services;

import be.pxl.services.domain.dto.Request.ReviewRequest;
import be.pxl.services.enums.Status;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ReviewRequestTests {

    @Test
    void shouldCreateReviewRequestUsingAllArgsConstructor() {
        ReviewRequest reviewRequest = new ReviewRequest(
                "Reason for Review",
                "Reviewer Author",
                "Post Author",
                10L,
                Status.SUBMITTED
        );

        assertThat(reviewRequest.getReason()).isEqualTo("Reason for Review");
        assertThat(reviewRequest.getAuthor()).isEqualTo("Reviewer Author");
        assertThat(reviewRequest.getPostAuthor()).isEqualTo("Post Author");
        assertThat(reviewRequest.getPostId()).isEqualTo(10L);
        assertThat(reviewRequest.getStatus()).isEqualTo(Status.SUBMITTED);
    }

    @Test
    void shouldCreateReviewRequestUsingDefaultConstructor() {
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setReason("Default Reason");
        reviewRequest.setAuthor("Default Reviewer");
        reviewRequest.setPostAuthor("Default Post Author");
        reviewRequest.setPostId(20L);
        reviewRequest.setStatus(Status.DRAFT);

        assertThat(reviewRequest.getReason()).isEqualTo("Default Reason");
        assertThat(reviewRequest.getAuthor()).isEqualTo("Default Reviewer");
        assertThat(reviewRequest.getPostAuthor()).isEqualTo("Default Post Author");
        assertThat(reviewRequest.getPostId()).isEqualTo(20L);
        assertThat(reviewRequest.getStatus()).isEqualTo(Status.DRAFT);
    }

    @Test
    void shouldBuildReviewRequestUsingBuilder() {
        ReviewRequest reviewRequest = ReviewRequest.builder()
                .reason("Builder Reason")
                .author("Builder Reviewer")
                .postAuthor("Builder Post Author")
                .postId(30L)
                .status(Status.ACCEPTED)
                .build();

        assertThat(reviewRequest.getReason()).isEqualTo("Builder Reason");
        assertThat(reviewRequest.getAuthor()).isEqualTo("Builder Reviewer");
        assertThat(reviewRequest.getPostAuthor()).isEqualTo("Builder Post Author");
        assertThat(reviewRequest.getPostId()).isEqualTo(30L);
        assertThat(reviewRequest.getStatus()).isEqualTo(Status.ACCEPTED);
    }

    @Test
    void shouldSetAndGetValues() {
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setReason("Set Reason");
        reviewRequest.setAuthor("Set Reviewer");
        reviewRequest.setPostAuthor("Set Post Author");
        reviewRequest.setPostId(40L);
        reviewRequest.setStatus(Status.REJECTED);

        assertThat(reviewRequest.getReason()).isEqualTo("Set Reason");
        assertThat(reviewRequest.getAuthor()).isEqualTo("Set Reviewer");
        assertThat(reviewRequest.getPostAuthor()).isEqualTo("Set Post Author");
        assertThat(reviewRequest.getPostId()).isEqualTo(40L);
        assertThat(reviewRequest.getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void shouldCreateReviewRequestWithEmptyBuilder() {
        ReviewRequest reviewRequest = ReviewRequest.builder().build();

        assertThat(reviewRequest).isNotNull();
        assertThat(reviewRequest.getReason()).isNull();
        assertThat(reviewRequest.getAuthor()).isNull();
        assertThat(reviewRequest.getPostAuthor()).isNull();
        assertThat(reviewRequest.getPostId()).isNull();
        assertThat(reviewRequest.getStatus()).isNull();
    }
}