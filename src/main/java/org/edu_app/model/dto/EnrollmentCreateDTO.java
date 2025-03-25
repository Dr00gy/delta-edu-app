package org.edu_app.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentCreateDTO {
    @NotNull(message = "Student ID is mandatory")
    private Long studentId;

    @NotNull(message = "Subject ID is mandatory")
    private Long subjectId;
}
