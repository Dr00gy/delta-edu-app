package org.edu_app.controller;

import org.edu_app.model.entity.Assignment;
import org.edu_app.model.entity.Grade;
import org.edu_app.model.entity.Submission;
import org.edu_app.repository.GradeRepository;
import org.edu_app.repository.SubmissionRepository;
import org.edu_app.service.AssignmentService;
import org.edu_app.service.GradeService;
import org.edu_app.service.SubmissionService;
import org.edu_app.utils.CurrentUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class GradingController {
    @Autowired
    SubmissionService submissionService;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    GradeService gradeService;

    @Autowired
    CurrentUserUtils currentUserUtils;

    @Autowired
    AssignmentService assignmentService;
    @Autowired
    private SubmissionRepository submissionRepository;

    @GetMapping("/grading")
public String grading(Model model) {
    var user = currentUserUtils.get();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    String formattedDate = LocalDate.now().format(formatter);

    model.addAttribute("name", user.getFirstName());
    model.addAttribute("date", formattedDate);
    model.addAttribute("role", user.getRole());

    var id = user.getId();
    
    // Set up grades table (main table)
    var grades = switch (user.getRole()) {
        case STUDENT -> gradeService.getAllGradesByStudentId(id);
        case TEACHER -> gradeService.getAllGradesByTeacherId(id);
        case ADMIN -> gradeService.getAllGrades();
    };
    model.addAttribute("grades", grades);
    
    // Set up secondary table (averages for students, ungraded submissions for teachers)
    switch (user.getRole()) {
        case STUDENT:
            var studentAverages = gradeService.calculateStudentAveragesBySubject(id);
            model.addAttribute("averages", studentAverages);
            break;
        case TEACHER:
            var ungradedSubmissions = gradeService.getUngradedSubmissionsByTeacherId(id);
            model.addAttribute("ungradedSubmissions", ungradedSubmissions);
            break;
        case ADMIN:
            // No special handling needed for admin
            break;
    }

    return "grading";
    }
        /*
        // Display the specific assignment grading page
        @GetMapping("/assignments/{assignmentId}")
        public String gradeAssignment(@PathVariable("assignmentId") Long assignmentId, Model model) {
            var user = currentUserUtils.get();



            System.out.println("Received assignment ID: " + assignmentId);

            try {
                System.out.println("Current user: " + user.getFirstName() + " " + user.getLastName());
                Assignment assignment = assignmentService.getAssignment(assignmentId);
                System.out.println("Assignment found: " + (assignment != null));
                // rest of your code
            } catch (Exception e) {
                System.err.println("Exception details: " + e.getMessage());
                e.printStackTrace();
                return "error";
            }


            try {
                Assignment assignment = assignmentService.getAssignment(assignmentId);
                if (assignment == null) {
                    return "error"; // Create a generic error page
                }

                model.addAttribute("assignment", assignment);
                Submission submission = submissionService.getSubmissionsByAssignment(assignmentId).get(0);

                List<Submission> unGradedSubs = submissionService.getSubmissionsWithoutGrade(assignmentId);
                model.addAttribute("submission", submission);
                System.out.println("Controller: ungradedSubmissions size = " + unGradedSubs.size());
                model.addAttribute("ungradedSubmissions", unGradedSubs);
                model.addAttribute("name", user.getFirstName());
                model.addAttribute("role", user.getRole());

                // Don't forget to add date like in other controllers
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                String formattedDate = LocalDate.now().format(formatter);
                model.addAttribute("date", formattedDate);

                return "gradeAssignment";
            } catch (Exception e) {
                System.err.println("Error loading assignment: " + e.getMessage());
                return "error";
            }
        }*/
        @GetMapping("/assignments/{assignmentId}")
        public String gradeAssignment(@PathVariable("assignmentId") Long assignmentId, Model model) {
            var user = currentUserUtils.get();
            Assignment assignment = assignmentService.getAssignment(assignmentId);
            if (assignment == null) {
                return "error";  // or another error page
            }

            // Fetch only submissions for this assignment that are ungraded.
            List<Submission> ungradedSubs = submissionService.getSubmissionsWithoutGrade(assignmentId);

            model.addAttribute("assignment", assignment);
            model.addAttribute("ungradedSubmissions", ungradedSubs);
            model.addAttribute("name", user.getFirstName());
            model.addAttribute("role", user.getRole());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            model.addAttribute("date", LocalDate.now().format(formatter));

            return "gradeAssignment";
        }

}
