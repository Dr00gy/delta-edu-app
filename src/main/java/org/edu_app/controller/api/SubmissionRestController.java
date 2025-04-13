package org.edu_app.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.edu_app.model.dto.SubmissionUpdateDTO;
import org.edu_app.model.entity.Submission;
import org.edu_app.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionRestController {

    private final SubmissionService submissionService;

    @PostMapping("/updateComment")
    public ResponseEntity<?> updateStudentComment(@Valid @RequestBody SubmissionUpdateDTO updateDTO) {
        try {
            // Retrieve the existing submission
            Submission submission = submissionService.getSubmission(updateDTO.getSubmissionId());
            if (submission == null) {
                return ResponseEntity.notFound().build();
            }
            // Update the student comment field
            submission.setStudentComment(updateDTO.getStudentComment());

            // Use the service to update the record in the database.
            submissionService.updateSubmission(updateDTO.getSubmissionId(), submission);

            // Return OK status (empty body is acceptable)
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .body("Error updating submission comment: " + ex.getMessage());
        }
    }
}
