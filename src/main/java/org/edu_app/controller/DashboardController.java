package org.edu_app.controller;

import org.edu_app.model.dto.UserDTO;
import org.edu_app.service.ExportLogService;
import org.edu_app.service.ExportLogService.ExportRecord;
import org.edu_app.utils.CurrentUserUtils;
import org.edu_app.utils.InitDBManager;
import org.edu_app.model.entity.Submission;
import org.edu_app.model.entity.Grade;
import org.edu_app.model.entity.Role;
import org.edu_app.service.SubmissionService;
import org.edu_app.service.GradeService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private InitDBManager dbManager;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private ExportLogService exportLogService;

    @Autowired
    CurrentUserUtils currentUserUtils;

    @GetMapping("/")
    public String showHome(Model model) {
        var user = currentUserUtils.get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        if (user != null) {
            model.addAttribute("name", user.getFirstName());
            model.addAttribute("role", user.getRole());

            if (user.getRole() == Role.STUDENT) {
                List<Submission> latestSubmissions = submissionService.getLatestSubmissionsByStudent(user.getId());
                model.addAttribute("submissions", latestSubmissions);

                List<Grade> latestGrades = gradeService.getLatestGradesByStudentId(user.getId());
                model.addAttribute("grades", latestGrades);
            } 
            else if (user.getRole() == Role.TEACHER) {
                List<Submission> latestSubmissions = submissionService.getLatestSubmissionsByTeacher(user.getId());
                model.addAttribute("submissions", latestSubmissions);
                model.addAttribute("grades", List.of()); // Empty list def
            } 
            else if (user.getRole() == Role.ADMIN) {
                List<Submission> latestSubmissions = submissionService.getLatestSubmissions();
                model.addAttribute("submissions", latestSubmissions);
                model.addAttribute("grades", List.of()); // Empty list def
            
            // Add latest exports for all user roles
            List<ExportRecord> latestExports = exportLogService.getLatestExportsByUser(user.getId(), 5);
            model.addAttribute("exports", latestExports);
        } else {
            model.addAttribute("name", "Unknown");
            model.addAttribute("role", "Unknown");
            model.addAttribute("submissions", List.of());
            model.addAttribute("grades", List.of());
            model.addAttribute("exports", List.of());
        }
        model.addAttribute("date", formattedDate);

        }
         return "index";
    }
}