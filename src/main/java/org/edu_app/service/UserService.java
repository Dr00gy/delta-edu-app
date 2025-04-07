package org.edu_app.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.edu_app.model.entity.Enrollment;
import org.edu_app.model.entity.Role;
import org.edu_app.model.entity.User;
import org.edu_app.repository.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;
    private final GradeRepository gradeRepository;

    public List<User> findStudentsBySubject(Long subjectId) {
        List<Enrollment> enrollments = enrollmentRepository.findBySubjectId(subjectId);
        return enrollments.stream()
                .map(Enrollment::getStudent)
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public void addUser(User user) throws  AccessDeniedException {
        if (userRepository.existsByEmail(user.getEmail())){
            throw new AccessDeniedException("User already Exists");
        }
        if (user.getRole() == null) {
            user.setRole(Role.STUDENT);
        }
        userRepository.save(user);

    }

    @Transactional

    public void updateUser(Long userId, User user){
        User user1 = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        user1.setEmail(user.getEmail());
        user1.setFirstName(user.getFirstName());
        user1.setLastName(user.getLastName());
        userRepository.save(user1);
    }


    @Transactional
    public void deleteUser(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        enrollmentRepository.deleteAllByStudent(user);
        submissionRepository.deleteAllByStudent(user);
        gradeRepository.deleteAllByTeacher(user);
    }

    public User getStudentBySubject(Long subjectId, Long studentId) {
        Enrollment enrollment = enrollmentRepository.findBySubjectIdAndStudentId(subjectId, studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student with id " + studentId + " not found in subject with id " + subjectId));
        return enrollment.getStudent();
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllStudents() {
        return userRepository.findByRole(Role.valueOf("STUDENT"));
    }

    public List<User> getAllTeachers() {
        return userRepository.findByRole(Role.valueOf("TEACHER"));
    }
}
