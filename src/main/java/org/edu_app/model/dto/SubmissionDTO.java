package vpsi.kelvin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDTO {
    private Long id;
    private LocalDateTime submittedAt;
    private String studentComment;
    private Long studentId;
    private String studentName;
    private Long assignmentId;
    private String assignmentName;
    private String lastModified;
    private String operation;
}
