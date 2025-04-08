package org.edu_app.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.edu_app.model.dto.SubjectCreateDTO;
import org.edu_app.model.dto.SubjectDTO;
import org.edu_app.model.entity.Subject;
import org.edu_app.service.SubjectService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subject")
@Tag(name = "subject", description = "Subject API")
public class SubjectController {

    private final SubjectService subjectService;
    private final ModelMapper modelMapper;

    @PostMapping("/add/{userId}")
    public void addSubject(@PathVariable Long userId, @RequestBody SubjectCreateDTO subjectCreateDTO) {
        Subject subject = modelMapper.map(subjectCreateDTO, Subject.class);
        subjectService.addSubject(subject, userId);
    }

    @PutMapping("/update/{id}")
    public void updateSubject(@PathVariable Long id, @RequestBody SubjectDTO subjectDTO) {
        Subject subject = modelMapper.map(subjectDTO, Subject.class);
        subjectService.updateSubject(id, subject);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
    }

    @GetMapping("/{id}")
    public SubjectDTO getSubject(@PathVariable Long id) {
        Subject subject = subjectService.getSubject(id);
        return modelMapper.map(subject, SubjectDTO.class);
    }

    @GetMapping
    public List<SubjectDTO> getAllSubjects() {
        List<Subject> subjects = subjectService.getAllSubjects();
        return subjects.stream()
                .map(subject -> modelMapper.map(subject, SubjectDTO.class))
                .toList();
    }

    @GetMapping("/student/{studentId}/all")
    public List<SubjectDTO> findSubjectsByStudentId(@PathVariable Long studentId) {
        List<Subject> subjects = subjectService.getSubjectsByStudentId(studentId);
        return subjects.stream()
                .map(s -> modelMapper.map(s, SubjectDTO.class))
                .toList();
    }

    @GetMapping("/teacher/{teacherId}/all")
    public List<SubjectDTO> findSubjectsByTeacherId(@PathVariable Long teacherId) {
        List<Subject> subjects = subjectService.getSubjectsByTeacherId(teacherId);
        return subjects.stream()
                .map(s -> modelMapper.map(s, SubjectDTO.class))
                .toList();
    }

}
