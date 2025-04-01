package vpsi.kelvin.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vpsi.kelvin.model.dto.SubmissionCreateDTO;
import vpsi.kelvin.model.dto.SubmissionDTO;
import vpsi.kelvin.model.entity.Submission;
import vpsi.kelvin.service.SubmissionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions")
@Tag(name = "submission", description = "Submission API")
public class SubmissionController {

    private SubmissionService submissionService;
    private ModelMapper modelMapper;

    @PostMapping("/add")
    public void addSubmission(@RequestBody SubmissionCreateDTO submissionCreateDTO) {
        Submission submission = modelMapper.map(submissionCreateDTO, Submission.class);
        submissionService.addSubmission(submission);
    }

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

}
