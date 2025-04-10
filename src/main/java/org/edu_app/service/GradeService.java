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
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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

    public List<Grade> getAllGradesByTeacherId(Long id) {
        return gradeRepository.findAll()
                .stream()
                .filter(grade -> grade.getTeacher().getId().equals(id))
                .toList();
    }

    public List<Grade> getLatestGradesByStudentId(Long studentId) {
        return gradeRepository.findTop3ByStudentIdOrderByIdDesc(studentId);
    }

    public List<Grade> getAllGradesByStudentId(Long studentId) {
        return gradeRepository.findBySubmission_StudentId(studentId);
    }
    
    public List<Submission> getUngradedSubmissionsByTeacherId(Long teacherId) {
        return gradeRepository.findUngradedSubmissionsByTeacherId(teacherId);
    }

    public static class SubjectAverage {
        private String subjectName;
        private Double averagePercentage;
        
        public SubjectAverage(String subjectName, Double averagePercentage) {
            this.subjectName = subjectName;
            this.averagePercentage = averagePercentage;
        }
        
        public String getSubjectName() { return subjectName; }
        public Double getAveragePercentage() { return averagePercentage; }
    }

    public List<SubjectAverage> calculateStudentAveragesBySubject(Long studentId) {
        List<Object[]> rawData = gradeRepository.findStudentGradeDataWithMaxPoints(studentId);
        
        Map<String, List<Double>> percentagesBySubject = new HashMap<>();

        for (Object[] row : rawData) {
            String subjectName = (String) row[0];
            Double score = ((Number) row[1]).doubleValue();
            Double maxPoints = ((Number) row[2]).doubleValue();
            
            double percentage = (score / maxPoints) * 100.0;

            percentagesBySubject
                .computeIfAbsent(subjectName, k -> new ArrayList<>())
                .add(percentage);
        }

        return percentagesBySubject.entrySet().stream()
            .map(entry -> {
                String subject = entry.getKey();
                List<Double> percentages = entry.getValue();
                double avg = percentages.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                return new SubjectAverage(subject, avg);
            })
            .toList();
    }
    
}