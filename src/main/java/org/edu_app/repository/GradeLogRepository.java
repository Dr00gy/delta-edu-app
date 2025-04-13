package org.edu_app.repository;

import org.edu_app.model.entity.GradeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeLogRepository extends JpaRepository<GradeLog, Long> {
    List<GradeLog> findTop5ByOrderByTimestampDesc();

}
