package be.pxl.services.domain.dto.Request;

import be.pxl.services.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class ReviewRequest {
    private String reason;
    private String author;
    private String postAuthor;
    private Long postId;
    private Status status;

    public ReviewRequest() {
    }

    public ReviewRequest(String reason, String author, String postAuthor, Long postId, Status status) {
        this.reason = reason;
        this.author = author;
        this.postAuthor = postAuthor;
        this.postId = postId;
        this.status = status;
    }
}