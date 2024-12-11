package be.pxl.services.model.dto;

import be.pxl.services.enums.Category;
import be.pxl.services.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private Category category;
    private Status status;
    private LocalDateTime creationDate;
}
