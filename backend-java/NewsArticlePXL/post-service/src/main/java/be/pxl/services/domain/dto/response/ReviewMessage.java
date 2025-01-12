package be.pxl.services.domain.dto.response;

import be.pxl.services.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewMessage {
    private Long id;
    private Status status;
}
