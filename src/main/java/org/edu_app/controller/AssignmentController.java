package org.edu_app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.edu_app.model.dto.AssignmentCreateDTO;
import org.edu_app.model.dto.AssignmentDTO;
import org.edu_app.model.entity.Assignment;
import org.edu_app.service.AssignmentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assignment")
@Tag(name = "assignment", description = "Assignment API")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final ModelMapper modelMapper;

    @PostMapping("/add")
    public void addAssignment(@RequestBody AssignmentCreateDTO assignmentCreateDTO) {
        Assignment assignment = modelMapper.map(assignmentCreateDTO, Assignment.class);
        assignmentService.addAssignment(assignment, assignmentCreateDTO.getSubjectId());
    }

    @PutMapping("/update/{id}")
    public void updateAssignment(@PathVariable Long id, @RequestBody AssignmentDTO assignmentDTO) {
        Assignment assignment = modelMapper.map(assignmentDTO, Assignment.class);
        assignmentService.updateAssignment(id, assignment);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
    }

    @GetMapping("/{id}")
    public AssignmentDTO getAssignment(@PathVariable Long id) {
        Assignment assignment = assignmentService.getAssignment(id);
        return modelMapper.map(assignment, AssignmentDTO.class);
    }

    @GetMapping
    public List<AssignmentDTO> getAllAssignments() {
        List<Assignment> assignments = assignmentService.getAllAssignments();
        return assignments.stream()
                .map(a -> modelMapper.map(a, AssignmentDTO.class))
                .toList();
    }
}

