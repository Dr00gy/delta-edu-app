package org.edu_app.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.edu_app.model.dto.SubmissionGradeDTO;
import org.edu_app.model.dto.SubmissionGradeUpdateDTO;
import org.edu_app.model.dto.UserDTO;
import org.edu_app.model.entity.Submission;
import org.edu_app.model.entity.Grade;
import org.edu_app.model.entity.User;
import org.edu_app.service.SubmissionService;
import org.edu_app.service.UserService;
import org.edu_app.utils.CurrentUserUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeRestController {

    private final SubmissionService submissionService;
    private final CurrentUserUtils currentUserUtils;
    private final UserService userService;

    // Endpoint to load submission details for grading
    @GetMapping("/{submissionId}/details")
    public ResponseEntity<?> getSubmissionDetails(@PathVariable("submissionId") Long submissionId) {
        try {
            Submission submission = submissionService.getSubmission(submissionId);
            // Map submission to DTO – you can adjust as needed:
            SubmissionGradeDTO dto = new SubmissionGradeDTO();
            dto.setSubmissionId(submission.getId());
            dto.setStudentName(submission.getStudent().getFirstName() + " " +
                    submission.getStudent().getLastName());
            // Assuming you have file information – otherwise use dummy values:
            dto.setUploadedFileName("example.jpg");
            dto.setFileType("image");  // or "text"
            dto.setFileContent("/uploads/example.jpg"); // or text content
            dto.setStudentComment(submission.getStudentComment());
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body("Error loading submission details");
        }
    }

    // Endpoint to update grading information on a submission.
    @PostMapping("/updateGrade")
    public ResponseEntity<?> updateSubmissionGrade(@Valid @RequestBody SubmissionGradeUpdateDTO updateDTO) {
        try {
            Submission submission = submissionService.getSubmission(updateDTO.getSubmissionId());
            if (submission == null) {
                return ResponseEntity.notFound().build();
            }
            // Retrieve the current teacher as a DTO and then load the full User entity.
            UserDTO teacherDTO = currentUserUtils.get();
            User currentTeacher = userService.getUserById(teacherDTO.getId());

            if (submission.getGrade() == null) {
                Grade grade = new Grade();
                grade.setScore(updateDTO.getGrade());
                grade.setFeedback(updateDTO.getTeacherFeedback());
                grade.setTeacher(currentTeacher); // now a full User entity
                grade.setSubmission(submission);
                submission.setGrade(grade);
                submissionService.updateSubmission(updateDTO.getSubmissionId(), submission);
            } else {
                Grade existingGrade = submission.getGrade();
                existingGrade.setScore(updateDTO.getGrade());
                existingGrade.setFeedback(updateDTO.getTeacherFeedback());
                existingGrade.setTeacher(currentTeacher); // update teacher as needed
            }
            return ResponseEntity.ok().build();
        } catch(Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body("Error updating grade: " + ex.getMessage());
        }
    }
}
