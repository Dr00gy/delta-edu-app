package org.edu_app.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edu_app.listener.AuditListener;
import org.edu_app.listener.Auditable;

@Entity
@EntityListeners(AuditListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "enrollments")
public class Enrollment implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Transient
    private String operation;
    
    @Transient
    private String lastModified;

    @Override
    public void setOperation(String operation){
        this.operation = operation;
    }
    @Override
    public void setLastModified(String lastModified){
        this.lastModified = lastModified;
    }
}
