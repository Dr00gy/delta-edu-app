package vpsi.kelvin.model.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import vpsi.kelvin.listener.AuditListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditListener.class)

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "submissions")
public class Submission {

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
}
