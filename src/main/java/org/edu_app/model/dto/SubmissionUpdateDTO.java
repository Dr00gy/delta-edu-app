package org.edu_app.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionUpdateDTO {
    @NotNull(message = "Submission ID is mandatory")
    private Long submissionId;

    // The updated student comment
    private String studentComment;
}
