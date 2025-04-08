package org.edu_app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.edu_app.model.dto.SubmissionCreateDTO;
import org.edu_app.model.dto.SubmissionDTO;
import org.edu_app.model.entity.Submission;
import org.edu_app.service.SubmissionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/submission")
@Tag(name = "submission", description = "Submission API")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final ModelMapper modelMapper;

    @PostMapping("/add")
    public void addSubmission(@RequestBody SubmissionCreateDTO submissionCreateDTO) {
        Submission submission = modelMapper.map(submissionCreateDTO, Submission.class);
        submissionService.addSubmission(submission);
    }

    // EDIT Komentáře od studenta
    @PutMapping("/update/{id}")
    public void updateSubmission(@PathVariable Long id, @RequestBody SubmissionDTO submissionDTO) {
        Submission submission = modelMapper.map(submissionDTO, Submission.class);
        submissionService.updateSubmission(id, submission);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteSubmission(@PathVariable Long id) {
        submissionService.deleteSubmission(id);
    }

    @GetMapping("/{id}")
    public SubmissionDTO getSubmission(@PathVariable Long id) {
        Submission submission = submissionService.getSubmission(id);
        return modelMapper.map(submission, SubmissionDTO.class);
    }

    @GetMapping("/assignment/{assignmentId}")
    public List<SubmissionDTO> getSubmissionsByAssignment(@PathVariable Long assignmentId) {
        List<Submission> submissions = submissionService.getSubmissionsByAssignment(assignmentId);
        return submissions.stream()
                .map(s -> modelMapper.map(s, SubmissionDTO.class))
                .toList();
    }

    @GetMapping("/student/{studentId}")
    public List<SubmissionDTO> getSubmissionsByStudent(@PathVariable Long studentId) {
        List<Submission> submissions = submissionService.getSubmissionsByStudent(studentId);
        return submissions.stream()
                .map(s -> modelMapper.map(s, SubmissionDTO.class))
                .toList();
    }
    @GetMapping
    public List<SubmissionDTO> getAllSubmissions() {
        List<Submission> submissions = submissionService.getAllSubmissions();
        return submissions.stream()
                .map(s -> modelMapper.map(s, SubmissionDTO.class))
                .toList();
    }

    @GetMapping("/student/{studentId}/latest")
    public List<SubmissionDTO> getLatestSubmissionsByStudent(@PathVariable Long studentId) {
        List<Submission> submissions = submissionService.getLatestSubmissionsByStudent(studentId);
        return submissions.stream()
                .map(s -> modelMapper.map(s, SubmissionDTO.class))
                .toList();
    }


}
