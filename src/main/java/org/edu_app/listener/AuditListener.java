package org.edu_app.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.edu_app.model.entity.Grade;
import org.edu_app.model.entity.GradeLog;
import org.edu_app.repository.GradeLogRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class AuditListener {

    private GradeLogRepository gradeLogRepository;


    private String getCurrentFormattedDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }


    private void createLog(Object object, String operation) {
        if (object instanceof Grade grade) {
            GradeLog log = new GradeLog();
            log.setGradeId(grade.getId());
            log.setOperation(operation);
            log.setTimestamp(getCurrentFormattedDate());
            gradeLogRepository.save(log);
        }
    }

    @PrePersist
    public void beforeInsert(Object object) {
        if (object instanceof Auditable auditable) {
            auditable.setOperation("INSERT");
            auditable.setLastModified(getCurrentFormattedDate());
        }
    }

    @PreRemove
    public void beforeDelete(Object object) {
        if (object instanceof Auditable auditable) {
            auditable.setOperation("DELETE");
            auditable.setLastModified(getCurrentFormattedDate());
        }
    }

    @PreUpdate
    public void beforeUpdate(Object object) {
        if (object instanceof Auditable auditable) {
            auditable.setOperation("UPDATE");
            auditable.setLastModified(getCurrentFormattedDate());
        }
    }
}