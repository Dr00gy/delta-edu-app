package org.edu_app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {
    private Long id;
    private String name;
    private String description;
    private Long teacherId;
    private String teacherName;
    private String lastModified;
    private String operation;
}
