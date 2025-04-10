package org.edu_app.controller.api;

import lombok.RequiredArgsConstructor;
import org.edu_app.model.dto.SubjectDTO;
import org.edu_app.model.entity.Role;
import org.edu_app.model.entity.Subject;
import org.edu_app.service.SubjectService;
import org.edu_app.utils.CurrentUserUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectRestController {

    private final SubjectService subjectService;
    private final CurrentUserUtils currentUserUtils;

    @GetMapping("/teacher")
    public ResponseEntity<List<SubjectDTO>> getTeacherSubjects() {
        var currentUser = currentUserUtils.get();
        
        // Verify that the user is a teacher
        if (currentUser == null || !currentUser.getRole().equals(Role.TEACHER)) {
            return ResponseEntity.status(403).build();
        }
        
        try {
            List<Subject> teacherSubjects = subjectService.getSubjectsByTeacherId(currentUser.getId());
            
            List<SubjectDTO> subjectDTOs = teacherSubjects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(subjectDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    private SubjectDTO convertToDTO(Subject subject) {
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