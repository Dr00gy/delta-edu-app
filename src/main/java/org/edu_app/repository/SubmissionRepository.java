package org.edu_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.edu_app.model.entity.Submission;
import org.edu_app.model.entity.User;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    void deleteAllByStudent(User student);

    List<Submission> findByAssignmentId(Long assignmentId);

    List<Submission> findByStudentId(Long studentId);

    List<Submission> findTop3ByStudentIdOrderBySubmittedAtDesc(Long studentId);

    List<Submission> findByAssignment_Subject_TeacherId(Long teacherId);

    List<Submission> findTop3ByAssignment_Subject_TeacherIdOrderBySubmittedAtDesc(Long teacherId);
    
    List<Submission> findTop3ByOrderBySubmittedAtDesc();


    @Query(value = """
        SELECT s.*
        FROM submissions s
        LEFT JOIN grades g ON s.id = g.submission_id
        WHERE s.assignment_id = :assignmentId AND g.id IS NULL
    """, nativeQuery = true)
    List<Submission> findSubmissionsWithoutGrade(@Param("assignmentId") Long assignmentId);

    //NEW
    @Query("""
        SELECT s FROM Submission s
        JOIN FETCH s.student
        JOIN FETCH s.assignment
        WHERE s.assignment.id = :assignmentId AND s.grade IS NULL
    """)
    List<Submission> findUngradedSubmissionsByAssignmentId(@Param("assignmentId") Long assignmentId);

}
