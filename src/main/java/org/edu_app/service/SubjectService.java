package org.edu_app.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.edu_app.model.entity.Role;
import org.edu_app.model.entity.Subject;
import org.edu_app.model.entity.User;
import org.edu_app.repository.SubjectRepository;
import org.edu_app.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addSubject(Subject subject, Long teacherId){
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher with id " + teacherId + " not found"));

        if (!teacher.getRole().equals(Role.TEACHER)) {
            throw new IllegalArgumentException("User with id " + teacherId + " is not a teacher");
        }

        subject.setTeacher(teacher);
        subjectRepository.save(subject);
    }
    @Transactional
    public void updateSubject(Long subjectId, Subject updatedSubject) {
        Subject existingSubject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject with id " + subjectId + " not found"));

        existingSubject.setName(updatedSubject.getName());
        existingSubject.setDescription(updatedSubject.getDescription());


        subjectRepository.save(existingSubject);
    }

    @Transactional
    public void deleteSubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject with id " + subjectId + " not found"));
        subjectRepository.delete(subject);
    }

    public Subject getSubject(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject with id " + subjectId + " not found"));
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public List<Subject> getSubjectsByStudentId(Long studentId) {
        return subjectRepository.findSubjectsByStudentId(studentId);
    }

    public List<Subject> getSubjectsByTeacherId(Long teacherId) {
        return subjectRepository.findSubjectsByTeacherId(teacherId);
    }

}
