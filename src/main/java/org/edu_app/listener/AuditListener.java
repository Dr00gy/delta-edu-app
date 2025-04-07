package vpsi.kelvin.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class AuditListener {

    private String getCurrentFormattedDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
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