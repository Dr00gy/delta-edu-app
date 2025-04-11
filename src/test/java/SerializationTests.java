import com.fasterxml.jackson.core.JsonProcessingException;
import org.edu_app.model.entity.*;
import org.edu_app.utils.SerialzationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

// Primarily testing both serialization and deserialization
class SerializationTests {

    Assignment assignment;
    User teacher;
    Subject subject;
    Enrollment enrollment;
    Submission submission;
    Grade grade;

    @BeforeEach
    void prepare() {
        // Existující kód pro Assignment a Teacher
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

        // Vytvoření studenta pro testovací účely
        var student = new User();
        student.setId(2L);
        student.setEmail("student@vsb.com");
        student.setFirstName("Jane");
        student.setLastName("Smith");
        student.setRole(Role.STUDENT);

        // Vytvoření předmětu
        var subject = new Subject();
        subject.setId(1L);
        subject.setName("Java Programming");
        subject.setDescription("Introduction to Java Programming");
        subject.setTeacher(teacher);
        this.subject = subject;

        // Vytvoření zápisu
        var enrollment = new Enrollment();
        enrollment.setId(1L);
        enrollment.setStudent(student);
        enrollment.setSubject(subject);
        this.enrollment = enrollment;

        // Vytvoření odevzdání
        var submission = new Submission();
        submission.setId(1L);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStudentComment("Here is my solution");
        submission.setStudent(student);
        submission.setAssignment(assignment);
        this.submission = submission;

        // Vytvoření hodnocení
        var grade = new Grade();
        grade.setId(1L);
        grade.setScore(8);
        grade.setFeedback("Good work, but could improve code quality");
        grade.setSubmission(submission);
        grade.setTeacher(teacher);
        this.grade = grade;
    }

    // Existující testy
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

    // Nové testy pro serializaci
    @Test
    void subjectSerialization() throws JsonProcessingException {
        var string = SerialzationManager.serialize(subject);

        SerialzationManager.deserialize(string, Subject.class);
    }

    @Test
    void enrollmentSerialization() throws JsonProcessingException {
        var string = SerialzationManager.serialize(enrollment);

        SerialzationManager.deserialize(string, Enrollment.class);
    }

    @Test
    void submissionSerialization() throws JsonProcessingException {
        var string = SerialzationManager.serialize(submission);

        SerialzationManager.deserialize(string, Submission.class);
    }

    @Test
    void gradeSerialization() throws JsonProcessingException {
        var string = SerialzationManager.serialize(grade);

        SerialzationManager.deserialize(string, Grade.class);
    }

    // Nové testy pro ukládání do souboru
    @Test
    void fileTestSubject() throws IOException {
        SerialzationManager.save(subject, "tests/subject.json");

        SerialzationManager.load("tests/subject.json", Subject.class);
    }

    @Test
    void fileTestEnrollment() throws IOException {
        SerialzationManager.save(enrollment, "tests/enrollment.json");

        SerialzationManager.load("tests/enrollment.json", Enrollment.class);
    }

    @Test
    void fileTestSubmission() throws IOException {
        SerialzationManager.save(submission, "tests/submission.json");

        SerialzationManager.load("tests/submission.json", Submission.class);
    }

    @Test
    void fileTestGrade() throws IOException {
        SerialzationManager.save(grade, "tests/grade.json");

        SerialzationManager.load("tests/grade.json", Grade.class);
    }
}