package org.edu_app.controller;

import org.edu_app.Main;
import org.edu_app.repository.SubmissionRepository;
import org.edu_app.service.SubmissionService;
import org.edu_app.utils.CurrentUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class SubmissionsController {

    @Autowired
    SubmissionService submissionService;

    @Autowired
    CurrentUserUtils currentUserUtils;

    @GetMapping("/submissions")
    public String showSubmissions(Model model) {
        var user = currentUserUtils.get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        model.addAttribute("name", user.getFirstName());
        model.addAttribute("date", formattedDate);
        model.addAttribute("role", user.getRole());


        var id = user.getId();
        var submissions = switch (user.getRole()) {
            case STUDENT -> submissionService.getSubmissionsByStudent(id);
            case TEACHER -> submissionService.getSubmissionsByTeacher(id);
            case ADMIN -> submissionService.getAllSubmissions();
        };


        model.addAttribute("submissions", submissions);



        return "submissions";
    }
}