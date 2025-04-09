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
}
