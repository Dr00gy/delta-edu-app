package org.edu_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.edu_app.model.entity.Subject;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface SubjectRepository  extends JpaRepository<Subject, Long> {
    @Query("""
        SELECT e.subject FROM Enrollment e
        WHERE e.student.id = :studentId
        """)
    List<Subject> findSubjectsByStudentId(Long studentId);

    List<Subject> findSubjectsByTeacherId(Long teacherId);
}
