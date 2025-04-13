package org.edu_app.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class SubmissionGradeUpdateDTO {
    @NotNull
    private Long submissionId;

    @NotNull
    private Integer grade;

    private String teacherFeedback;
}
