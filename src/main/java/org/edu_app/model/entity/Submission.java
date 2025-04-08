package org.edu_app.model.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.edu_app.listener.AuditListener;
import org.edu_app.listener.Auditable;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditListener.class)

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "submissions")
public class Submission implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime submittedAt;


    @NotNull
    private String studentComment;


    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;


    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    private String operation;
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