package org.edu_app.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentUpdateDTO {
    @NotNull(message = "Assignment ID is mandatory")
    private Long assignmentId;

    private String name;
    private String description;
    private Double maxPoints;

    // Ensure your Jackson configuration can parse ISO date/time strings.
    private LocalDateTime deadline;
}
