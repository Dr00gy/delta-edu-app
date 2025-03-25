package vpsi.kelvin.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeCreateDTO {

    @Min(value = 0, message = "Minimum score is 0")
    @Max(value = 100, message = "Maximum score is 100")
    private int score;

    private String feedback;

    @NotNull(message = "Submission ID is mandatory")
    private Long submissionId;

    @NotNull(message = "Teacher ID is mandatory")
    private Long teacherId;
}
