package vpsi.kelvin.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vpsi.kelvin.model.dto.EnrollmentCreateDTO;
import vpsi.kelvin.model.dto.EnrollmentDTO;
import vpsi.kelvin.model.entity.Enrollment;
import vpsi.kelvin.service.EnrollmentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/enrollments")
@Tag(name = "enrollment", description = "Enrollment API")
public class EnrollmentController {
    private EnrollmentService enrollmentService;
    private ModelMapper modelMapper;

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

