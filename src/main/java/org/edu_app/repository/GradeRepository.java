package org.edu_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.edu_app.model.entity.Grade;
import org.edu_app.model.entity.User;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    void deleteAllByTeacher(User teacher);

    @Query("""
        SELECT g FROM Grade g
        WHERE g.submission.student.id = :studentId
        ORDER BY g.id DESC
        """)    
    List<Grade> findTop3ByStudentIdOrderByIdDesc(Long studentId);
}
