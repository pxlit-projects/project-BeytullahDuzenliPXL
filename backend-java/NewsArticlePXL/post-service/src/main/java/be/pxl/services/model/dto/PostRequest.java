package be.pxl.services.model.dto;

import be.pxl.services.enums.Category;
import be.pxl.services.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    private String title;
    private String content;
    private Long author;
    private Category category;
    private Status status;
}