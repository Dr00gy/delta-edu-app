package org.edu_app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDTO {
    private Long id;
    private String name;
    private String description;
    private Double maxPoints;
    private LocalDateTime deadline;
    private Long subjectId;
    private String subjectName;

}
