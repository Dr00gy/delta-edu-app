package vpsi.kelvin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private Long id;
    private Long studentId;
    private String studentName;     // firstName + lastName
    private Long subjectId;
    private String subjectName;
    private String lastModified;
    private String operation;
}
