import com.fasterxml.jackson.core.JsonProcessingException;
import org.edu_app.model.entity.Assignment;
import org.edu_app.model.entity.Role;
import org.edu_app.model.entity.User;
import org.edu_app.utils.SerialzationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

// Primarily testing both serialization and deserialization
class SerializationTests {

    Assignment assignment;
    User teacher;

    @BeforeEach
    void prepare() {
        var assignment = new Assignment();
        assignment.setId(1L);
        assignment.setName("Assignment 1");
        assignment.setDescription("Description 1");
        assignment.setMaxPoints(10.0);
        assignment.setDeadline(LocalDateTime.now());
        this.assignment = assignment;

        var teacher = new User();
        teacher.setId(1L);
        teacher.setEmail("teacher@vsb.com");
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setRole(Role.TEACHER);
        this.teacher = teacher;
    }

    @Test
    void assignmentSerialization() throws JsonProcessingException {
        var string = SerialzationManager.serialize(assignment);

        SerialzationManager.deserialize(string, Assignment.class);
    }

    @Test
    void teacherSerialization() throws JsonProcessingException {
        var string = SerialzationManager.serialize(teacher);

        SerialzationManager.deserialize(string, User.class);
    }

    @Test
    void fileTest() throws IOException {
        SerialzationManager.save(teacher, "tests/teacher.json");

        SerialzationManager.load("tests/teacher.json", User.class);
    }
}