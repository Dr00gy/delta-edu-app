package org.edu_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.edu_app.model.entity.Grade;
import org.edu_app.model.entity.User;
import org.edu_app.model.entity.Submission;

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

    List<Grade> findBySubmission_StudentId(Long studentId);

    // Find ungraded submissions to populate Teacher "to be graded" table
    @Query("""
        SELECT s FROM Submission s
        WHERE s.assignment.subject.teacher.id = :teacherId
        AND NOT EXISTS (SELECT g FROM Grade g WHERE g.submission = s)
        """)
    List<Submission> findUngradedSubmissionsByTeacherId(Long teacherId);

    // For calculating student averages per subject
    @Query("""
        SELECT s.assignment.subject.name,
            g.score,
            s.assignment.maxPoints
        FROM Grade g
        JOIN g.submission s
        WHERE s.student.id = :studentId
        """)
    List<Object[]> findStudentGradeDataWithMaxPoints(Long studentId);

    @Query("""
        SELECT s.assignment.subject.name as subjectName,
            AVG(g.score * 100.0 / s.assignment.maxPoints) as averagePercentage
        FROM Grade g
        JOIN g.submission s
        WHERE s.student.id = :studentId
        GROUP BY s.assignment.subject.id, s.assignment.subject.name
        """)
    List<Object[]> calculateAverageGradePercentageBySubjectForStudent(Long studentId);
}
