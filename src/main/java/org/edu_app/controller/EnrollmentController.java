package org.edu_app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.edu_app.model.dto.EnrollmentCreateDTO;
import org.edu_app.model.dto.EnrollmentDTO;
import org.edu_app.model.entity.Enrollment;
import org.edu_app.service.EnrollmentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/enrollments")
@Tag(name = "enrollment", description = "Enrollment API")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final ModelMapper modelMapper;

    @PostMapping("/add")
    public void addEnrollment(@RequestBody EnrollmentCreateDTO enrollmentCreateDTO) {
        enrollmentService.addEnrollment(enrollmentCreateDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
    }

    @GetMapping("/{id}")
    public EnrollmentDTO getEnrollment(@PathVariable Long id) {
        Enrollment enrollment = enrollmentService.getEnrollment(id);
        return modelMapper.map(enrollment, EnrollmentDTO.class);
    }

    @GetMapping
    public List<EnrollmentDTO> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        return enrollments.stream()
                .map(e -> modelMapper.map(e, EnrollmentDTO.class))
                .toList();
    }
}

