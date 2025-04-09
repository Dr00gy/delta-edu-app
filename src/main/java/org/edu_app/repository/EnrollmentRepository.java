package org.edu_app.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("SELECT e.subject.id, COUNT(DISTINCT e.student.id) FROM Enrollment e GROUP BY e.subject.id")
    List<Object[]> findEnrollmentCounts();

    boolean existsByStudentIdAndSubjectId(Long studentId, Long subjectId);
    
    @Query("SELECT e.subject.id FROM Enrollment e WHERE e.student.id = :studentId")
    List<Long> findSubjectIdsByStudentId(Long studentId);
}
