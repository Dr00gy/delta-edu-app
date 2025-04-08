package org.edu_app.controller;

import org.edu_app.model.dto.UserDTO;
import org.edu_app.utils.CurrentUserUtils;
import org.edu_app.utils.InitDBManager;
import org.edu_app.model.entity.Submission;
import org.edu_app.model.entity.Role;
import org.edu_app.service.SubmissionService;
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
    CurrentUserUtils currentUserUtils;

    @GetMapping("/")
    public String showHome(Model model) {
        // Get authenticated user email
        var user = currentUserUtils.get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        // Add attributes to model from userDetails
        if (user != null) {
            model.addAttribute("name", user.getFirstName());
            model.addAttribute("role", user.getRole());

            if (user.getRole() == Role.STUDENT) {
                List<Submission> latestSubmissions = submissionService.getLatestSubmissionsByStudent(user.getId());
                model.addAttribute("submissions", latestSubmissions);
            }
        } else {
            model.addAttribute("name", "Unknown");
            model.addAttribute("role", "Unknown");
            model.addAttribute("submissions", List.of());
        }
        model.addAttribute("date", formattedDate);

        return "index";
    }
}
