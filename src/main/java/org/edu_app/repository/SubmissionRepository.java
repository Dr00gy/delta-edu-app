package org.edu_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
