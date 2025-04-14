package org.edu_app.controller;


import lombok.RequiredArgsConstructor;
import org.edu_app.model.entity.Assignment;
import org.edu_app.service.AssignmentService;
import org.edu_app.utils.CurrentUserUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RequiredArgsConstructor
@Controller
public class AssignmentDetailsController {
/*
    @GetMapping("/assignments")
    public String showSubjectSubmissions(Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        model.addAttribute("name", "Duško");
        model.addAttribute("date", formattedDate);
        model.addAttribute("role", "Student");

        return "assignmentDetails";
    }*/
    private final AssignmentService assignmentService;
    private final CurrentUserUtils currentUserUtils;

    @GetMapping("/assignmentsDetailed/{id}")
    public String showAssignmentDetails(@PathVariable Long id, Model model) {
        var user = currentUserUtils.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        Assignment assignment = assignmentService.getAssignment(id);
        model.addAttribute("assignment", assignment);
        model.addAttribute("user",currentUserUtils);
        model.addAttribute("date", formattedDate);
        model.addAttribute("now", new Date());
        model.addAttribute("name", user.getFirstName());
        model.addAttribute("role", user.getRole());
        return "assignmentDetails";
    }

}