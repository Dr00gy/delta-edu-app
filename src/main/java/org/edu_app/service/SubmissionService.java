package org.edu_app.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.edu_app.model.entity.Submission;
import org.edu_app.repository.SubmissionRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Component
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;

    @Transactional
    public void addSubmission(Submission submission) {
        submissionRepository.save(submission);
    }

    @Transactional
    public void updateSubmission(Long submissionId, Submission submission) {
        Submission existingSubmission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submission with id " + submissionId + " not found"));

        existingSubmission.setStudentComment(submission.getStudentComment());
        existingSubmission.setSubmittedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        submissionRepository.save(existingSubmission);
    }


    @Transactional
    public void deleteSubmission(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submission with id " + submissionId + " not found"));
        submissionRepository.delete(submission);
    }

    public Submission getSubmission(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Submission with id " + id + " not found"));
    }

    public List<Submission> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    public List<Submission> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }

    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public List<Submission> getLatestSubmissionsByStudent(Long studentId) {
        return submissionRepository.findTop3ByStudentIdOrderBySubmittedAtDesc(studentId);
    }

    public List<Submission> getLatestSubmissionsByTeacher(Long teacherId) {
        return submissionRepository.findTop3ByAssignment_Subject_TeacherIdOrderBySubmittedAtDesc(teacherId);
    }

    public List<Submission> getLatestSubmissions() {
        return submissionRepository.findTop3ByOrderBySubmittedAtDesc();
    }

    public List<Submission> getSubmissionsByTeacher(Long teacherId) {
    return submissionRepository.findByAssignment_Subject_TeacherId(teacherId);
    }
}
