package org.edu_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.edu_app.model.entity.Grade;
import org.edu_app.model.entity.User;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    void deleteAllByTeacher(User teacher);
}
