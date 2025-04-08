package org.edu_app.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.edu_app.model.dto.EnrollmentCreateDTO;
import org.edu_app.model.entity.Enrollment;
import org.edu_app.model.entity.Subject;
import org.edu_app.model.entity.User;
import org.edu_app.repository.EnrollmentRepository;
import org.edu_app.repository.SubjectRepository;
import org.edu_app.repository.UserRepository;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public void addEnrollment(EnrollmentCreateDTO enrollmentCreateDTO) {
        User student = userRepository.findById(enrollmentCreateDTO.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student with id "
                        + enrollmentCreateDTO.getStudentId() + " not found"));
        Subject subject = subjectRepository.findById(enrollmentCreateDTO.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException("Subject with id "
                        + enrollmentCreateDTO.getSubjectId() + " not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSubject(subject);

        enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment with id " + id + " not found"));
        enrollmentRepository.delete(enrollment);
    }

    public Enrollment getEnrollment(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment with id " + id + " not found"));
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public Map<Long, Long> getEnrollmentCountsBySubject() {
        List<Object[]> results = enrollmentRepository.findEnrollmentCounts();

        Map<Long, Long> subjectEnrollmentMap = new HashMap<>();
        for (Object[] result : results) {
            Long subjectId = (Long) result[0];
            Long enrollmentCount = (Long) result[1];
            subjectEnrollmentMap.put(subjectId, enrollmentCount);
        }
        return subjectEnrollmentMap;
    }
}

