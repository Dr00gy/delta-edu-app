package org.edu_app.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.edu_app.model.dto.GradeDTO;
import org.edu_app.model.entity.Grade;
import org.edu_app.model.entity.Submission;
import org.edu_app.model.entity.User;
import org.edu_app.repository.GradeRepository;
import org.edu_app.repository.SubmissionRepository;
import org.edu_app.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public void assignGradeToSubmission(GradeDTO gradeDTO) {
        Submission submission = submissionRepository.findById(gradeDTO.getSubmissionId())
                .orElseThrow(() -> new EntityNotFoundException("Submission with id " + gradeDTO.getSubmissionId() + " not found"));

        User teacher = userRepository.findById(gradeDTO.getTeacherId())
                .orElseThrow(() -> new EntityNotFoundException("Teacher with id " + gradeDTO.getTeacherId() + " not found"));

        Grade grade = new Grade();
        grade.setScore(gradeDTO.getScore());
        grade.setFeedback(gradeDTO.getFeedback());
        grade.setSubmission(submission);
        grade.setTeacher(teacher);
        gradeRepository.save(grade);
    }

    public void updateGrade(Long id, Grade grade) {
        Grade existingGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grade with id " + id + " not found"));

        existingGrade.setScore(grade.getScore());
        existingGrade.setFeedback(grade.getFeedback());
        gradeRepository.save(existingGrade);
    }

    public void deleteGrade(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grade with id " + id + " not found"));
        gradeRepository.delete(grade);
    }

    public Grade getGrade(Long id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grade with id " + id + " not found"));
    }

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }
}
