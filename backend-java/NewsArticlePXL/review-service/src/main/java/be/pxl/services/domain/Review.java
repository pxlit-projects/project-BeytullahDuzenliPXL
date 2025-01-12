package be.pxl.services.domain;

import be.pxl.services.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long postId;
    private String reason;
    private String postAuthor;
    private String author;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime creationDate;

    public Review() {
    }

    public Review(Long id, Long postId, String reason, String postAuthor, String author, Status status, LocalDateTime creationDate) {
        this.id = id;
        this.postId = postId;
        this.reason = reason;
        this.postAuthor = postAuthor;
        this.author = author;
        this.status = status;
        this.creationDate = creationDate;
    }
}
