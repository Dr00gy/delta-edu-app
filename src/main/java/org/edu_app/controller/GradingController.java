package org.edu_app.controller;

import org.edu_app.model.entity.Grade;
import org.edu_app.repository.GradeRepository;
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

        // Display the specific assignment grading page
    @GetMapping("/assignments/{assignmentId}")
    public String gradeAssignment(@PathVariable("assignmentId") Long assignmentId, Model model) {
        var user = currentUserUtils.get();

        // Check if the user is a teacher
        /*if (user == null || user.getRole() != Role.TEACHER) {
            return "redirect:/";  // Redirect to home page if not a teacher
        }*/

        // Get the assignment by ID
        /*var assignment = assignmentService.getAssignmentById(assignmentId);
        if (assignment == null) {
            model.addAttribute("error", "Assignment not found");
            return "redirect:/grading";  // Redirect back if the assignment doesn't exist
        }*/

        // Get all ungraded submissions for this assignment
        //var ungradedSubmissions = submissionService.getUngradedSubmissionsByAssignmentId(assignmentId);
        //model.addAttribute("assignment", assignment);
        //model.addAttribute("ungradedSubmissions", ungradedSubmissions);
        model.addAttribute("name", user.getFirstName());
        model.addAttribute("role", user.getRole());

        return "gradeAssignment";  // This page will show the ungraded submissions
    }

    // Handle the grading submission (when a teacher submits grades for a student's submission)
    //@PostMapping("/assignments/{assignmentId}/grade")
    /*public String gradeSubmission(@PathVariable("assignmentId") Long assignmentId,
                                   @RequestParam("submissionId") Long submissionId,
                                   @RequestParam("score") Double score,
                                   @RequestParam("grade") String grade) {

        // Fetch the ungraded submission
        Submission submission = submissionService.getSubmissionById(submissionId);
        if (submission == null) {
            return "redirect:/grading";  // Redirect if the submission is not found
        }

        // Create a new grade for the submission
        Grade gradeEntity = new Grade();
        gradeEntity.setSubmission(submission);
        gradeEntity.setScore(score);
        gradeEntity.setGrade(grade);

        // Save the grade to the database
        gradeService.saveGrade(gradeEntity);

        // Redirect back to the assignment grading page
        return "redirect:/assignments/" + assignmentId;
    }*/
}
