package org.edu_app.controller.api;

import lombok.RequiredArgsConstructor;
import org.edu_app.model.dto.EnrollmentCreateDTO;
import org.edu_app.model.dto.SubjectDTO;
import org.edu_app.model.dto.UserDTO;
import org.edu_app.model.entity.Enrollment;
import org.edu_app.model.entity.Role;
import org.edu_app.model.entity.Subject;
import org.edu_app.model.entity.User;
import org.edu_app.service.EnrollmentService;
import org.edu_app.service.SubjectService;
import org.edu_app.service.UserService;
import org.edu_app.utils.CurrentUserUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentRestController {

    private final EnrollmentService enrollmentService;
    private final UserService userService;
    private final SubjectService subjectService;
    private final CurrentUserUtils currentUserUtils;

    @PostMapping
    public ResponseEntity<?> createEnrollment(@RequestBody EnrollmentCreateDTO enrollmentCreateDTO) {
        var currentUser = currentUserUtils.get();
        
        // Verify user is admin or teacher
        if (currentUser == null || (!currentUser.getRole().equals(Role.ADMIN) && !currentUser.getRole().equals(Role.TEACHER))) {
            return ResponseEntity.status(403).build();
        }
        
        // If user is a teacher, verify they teach this subject
        if (currentUser.getRole().equals(Role.TEACHER)) {
            Subject subject = subjectService.getSubject(enrollmentCreateDTO.getSubjectId());
            if (subject.getTeacher() == null || !subject.getTeacher().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body("You can only enroll students in subjects you teach");
            }
        }
        
        // Check if student is already enrolled
        if (enrollmentService.isStudentEnrolledInSubject(
                enrollmentCreateDTO.getStudentId(), 
                enrollmentCreateDTO.getSubjectId())) {
            return ResponseEntity.badRequest().body("Student is already enrolled in this subject");
        }
        
        try {
            enrollmentService.addEnrollment(enrollmentCreateDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
@DeleteMapping("/student/{studentId}/subject/{subjectId}")
public ResponseEntity<?> removeEnrollment(@PathVariable Long studentId, @PathVariable Long subjectId) {
    var currentUser = currentUserUtils.get();
    
    // Verify user is admin or teacher
    if (currentUser == null || (!currentUser.getRole().equals(Role.ADMIN) && !currentUser.getRole().equals(Role.TEACHER))) {
        return ResponseEntity.status(403).build();
    }
    
    // If user is a teacher, verify they teach this subject
    if (currentUser.getRole().equals(Role.TEACHER)) {
        Subject subject = subjectService.getSubject(subjectId);
        if (subject.getTeacher() == null || !subject.getTeacher().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body("You can only remove students from subjects you teach");
        }
    }
    
    try {
        // Find and delete the enrollment
        boolean enrollmentFound = false;
        for (Enrollment enrollment : enrollmentService.getAllEnrollments()) {
            if (enrollment.getStudent().getId().equals(studentId) && 
                enrollment.getSubject().getId().equals(subjectId)) {
                
                enrollmentService.deleteEnrollment(enrollment.getId());
                enrollmentFound = true;
                break;
            }
        }
        
        if (!enrollmentFound) {
            return ResponseEntity.status(404).body("Enrollment not found");
        }
        
        return ResponseEntity.ok().build();
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
    
@DeleteMapping("/student/{studentId}/all")
public ResponseEntity<?> removeAllEnrollments(@PathVariable Long studentId) {
    var currentUser = currentUserUtils.get();
    
    // Only admins can remove students from all subjects at once
    if (currentUser == null || !currentUser.getRole().equals(Role.ADMIN)) {
        return ResponseEntity.status(403).build();
    }
    
    try {
        // Find all enrollments for this student and delete them
        List<Enrollment> studentEnrollments = enrollmentService.getAllEnrollments().stream()
            .filter(e -> e.getStudent().getId().equals(studentId))
            .collect(Collectors.toList());
            
        if (studentEnrollments.isEmpty()) {
            return ResponseEntity.status(404).body("No enrollments found for this student");
        }
        
        for (Enrollment enrollment : studentEnrollments) {
            enrollmentService.deleteEnrollment(enrollment.getId());
        }
        
        return ResponseEntity.ok().build();
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
    
    @GetMapping("/student/{studentId}/subjects")
    public ResponseEntity<List<SubjectDTO>> getStudentSubjects(@PathVariable Long studentId) {
        var currentUser = currentUserUtils.get();
        
        // Verify user is admin or teacher
        if (currentUser == null || (!currentUser.getRole().equals(Role.ADMIN) && !currentUser.getRole().equals(Role.TEACHER))) {
            return ResponseEntity.status(403).build();
        }
        
        try {
            // Find all subjects this student is enrolled in
            List<Subject> studentSubjects = new ArrayList<>();
            
            enrollmentService.getAllEnrollments().stream()
                .filter(e -> e.getStudent().getId().equals(studentId))
                .forEach(e -> studentSubjects.add(e.getSubject()));
                
            // Convert to DTOs
            List<SubjectDTO> subjectDTOs = studentSubjects.stream()
                .map(this::convertToSubjectDTO)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(subjectDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/teacher-students")
    public ResponseEntity<List<UserDTO>> getTeacherStudents() {
        var currentUser = currentUserUtils.get();

        // Verify user is a teacher
        if (currentUser == null || !currentUser.getRole().equals(Role.TEACHER)) {
            return ResponseEntity.status(403).build();
        }

        try {
            // Get the subjects taught by the current teacher
            List<Subject> teacherSubjects = subjectService.getSubjectsByTeacherId(currentUser.getId());

            // Get all users and filter in Java
            List<User> allUsers = userService.getAllUsers();

            List<User> students = new ArrayList<>();

            for (Subject subject : teacherSubjects) {
                for (User user : allUsers) {
                    // Check if user is a student and enrolled in this subject
                    if (user.getRole().equals(Role.STUDENT) &&
                        user.getEnrollments().stream().anyMatch(enr -> 
                            enr.getSubject() != null && 
                            enr.getSubject().getId().equals(subject.getId()))) {

                        // Avoid duplicates
                        if (!students.contains(user)) {
                            students.add(user);
                        }
                    }
                }
            }

            // Map to DTOs
            List<UserDTO> studentDTOs = students.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(studentDTOs);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        return dto;
    }
    
    private SubjectDTO convertToSubjectDTO(Subject subject) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setName(subject.getName());
        dto.setDescription(subject.getDescription());
        if (subject.getTeacher() != null) {
            dto.setTeacherId(subject.getTeacher().getId());
        }
        return dto;
    }
}