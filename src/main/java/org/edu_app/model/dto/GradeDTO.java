package org.edu_app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeDTO {
    private Long id;
    private int score;
    private String feedback;
    private Long submissionId;
    private Long teacherId;
    private String teacherName;

}
