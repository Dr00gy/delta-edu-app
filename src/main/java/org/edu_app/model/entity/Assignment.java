package org.edu_app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edu_app.listener.AuditListener;
import org.edu_app.listener.Auditable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@Entity
@EntityListeners(AuditListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assignments")
public class Assignment implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "name is mandatory")
    private String name;

    @NotBlank(message = "description is mandatory")
    private String description;

    @NotNull(message = "MaxPoints are mandatory")
    private Double maxPoints;

    @NotNull(message = "Deadline is mandatory")
    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Transient
    private String operation;

    @Transient
    private String lastModified;

    // Added relationships in last push
    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions = new ArrayList<>();


    @Override
    public void setOperation(String operation){
        this.operation = operation;
    }
    @Override
    public void setLastModified(String lastModified){
        this.lastModified = lastModified;
    }
}