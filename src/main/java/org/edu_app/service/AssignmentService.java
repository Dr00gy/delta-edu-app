package org.edu_app.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.edu_app.model.entity.Assignment;
import org.edu_app.model.entity.Subject;
import org.edu_app.repository.AssignmentRepository;
import org.edu_app.repository.SubjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public void addAssignment(Assignment assignment, Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject with id " + subjectId + " not found"));
        assignment.setSubject(subject);
        assignmentRepository.save(assignment);
    }

    @Transactional
    public void updateAssignment(Long assignmentId, Assignment updatedAssignment) {
        Assignment existingAssignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment with id " + assignmentId + " not found"));
        existingAssignment.setName(updatedAssignment.getName());
        existingAssignment.setDescription(updatedAssignment.getDescription());
        existingAssignment.setMaxPoints(updatedAssignment.getMaxPoints());
        existingAssignment.setDeadline(updatedAssignment.getDeadline());
        assignmentRepository.save(existingAssignment);
    }

    @Transactional
    public void deleteAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment with id " + assignmentId + " not found"));
        assignmentRepository.delete(assignment);
    }

    public Assignment getAssignment(Long assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment with id " + assignmentId + " not found"));
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }
}

