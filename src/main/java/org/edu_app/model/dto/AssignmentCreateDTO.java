package org.edu_app.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentCreateDTO {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "MaxPoints is mandatory")
    private Double maxPoints;

    @NotNull(message = "Deadline is mandatory")
    private LocalDateTime deadline;

    @NotNull(message = "Subject ID is mandatory")
    private Long subjectId;
}
