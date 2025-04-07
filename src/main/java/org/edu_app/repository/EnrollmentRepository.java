package org.edu_app.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.edu_app.model.entity.Enrollment;
import org.edu_app.model.entity.Subject;
import org.edu_app.model.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findBySubjectId(Long subjectId);
    void deleteAllByStudent(User student);

    Optional<Enrollment> findBySubjectIdAndStudentId(Long subjectId, Long studentId);
}
