package vpsi.kelvin.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionCreateDTO {

    @NotNull(message = "SubmittedAt is mandatory")
    private LocalDateTime submittedAt;

    private String studentComment;

    @NotNull(message = "Student ID is mandatory")
    private Long studentId;

    @NotNull(message = "Assignment ID is mandatory")
    private Long assignmentId;
}
