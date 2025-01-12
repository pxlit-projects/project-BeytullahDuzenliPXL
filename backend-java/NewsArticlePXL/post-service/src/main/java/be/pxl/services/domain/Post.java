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
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String author;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime creationDate;

    public Post() {
    }

    public Post(Long id, String title, String content, String author, Status status, LocalDateTime creationDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.status = status;
        this.creationDate = creationDate;
    }
}