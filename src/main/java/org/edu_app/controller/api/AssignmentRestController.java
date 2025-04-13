package org.edu_app.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.edu_app.model.dto.AssignmentCreateDTO;
import org.edu_app.model.dto.AssignmentUpdateDTO;
import org.edu_app.model.entity.Assignment;
import org.edu_app.service.AssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentRestController {

    private final AssignmentService assignmentService;

    @PostMapping("/update")
    public ResponseEntity<?> updateAssignment(@Valid @RequestBody AssignmentUpdateDTO updateDTO) {
        try {
            // Create a temporary Assignment with the new data.
            Assignment updatedAssignment = new Assignment();
            updatedAssignment.setName(updateDTO.getName());
            updatedAssignment.setDescription(updateDTO.getDescription());
            updatedAssignment.setMaxPoints(updateDTO.getMaxPoints());
            updatedAssignment.setDeadline(updateDTO.getDeadline());

            // Call the service to update the existing assignment.
            assignmentService.updateAssignment(updateDTO.getAssignmentId(), updatedAssignment);

            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error updating assignment: " + ex.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAssignment(@Valid @RequestBody AssignmentCreateDTO creationDTO) {
        try {
            Assignment assignment = new Assignment();
            assignment.setName(creationDTO.getName());
            assignment.setDescription(creationDTO.getDescription());
            assignment.setMaxPoints(creationDTO.getMaxPoints());
            assignment.setDeadline(creationDTO.getDeadline());
            // service method will associate the assignment with its subject (see AssignmentService.addAssignment)
            assignmentService.addAssignment(assignment, creationDTO.getSubjectId());
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error creating assignment: " + ex.getMessage());
        }
    }
}
