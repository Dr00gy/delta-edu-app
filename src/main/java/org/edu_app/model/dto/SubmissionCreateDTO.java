package org.edu_app.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
public class SubmissionCreateDTO {

    @Getter
    @NotNull(message = "SubmittedAt is mandatory")
    private LocalDateTime submittedAt;

    @Getter
    private String studentComment;

    @NotNull(message = "Student ID is mandatory")
    @Getter
    private Long studentId;

    @NotNull(message = "Assignment ID is mandatory")
    @Getter
    private Long assignmentId;
}
