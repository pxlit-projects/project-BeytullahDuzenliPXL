package be.pxl.services.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private String author;
    private String postAuthor;

    public Notification() {
    }

    public Notification(Long id, String message, String author, String postAuthor) {
        this.id = id;
        this.message = message;
        this.author = author;
        this.postAuthor = postAuthor;
    }
}