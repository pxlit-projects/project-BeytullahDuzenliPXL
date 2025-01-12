package be.pxl.services.domain.dto.Response;

import be.pxl.services.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long postId;
    private String reason;
    private String postAuthor;
    private String author;
    private Status status;
}