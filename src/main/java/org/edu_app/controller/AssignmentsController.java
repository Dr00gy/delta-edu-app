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
public class AssignmentsController {

    private final CurrentUserUtils currentUserUtils;

    @GetMapping("/assignments")
    public String showAssignments(Model model) {

        var user = currentUserUtils.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        model.addAttribute("user",currentUserUtils);
        model.addAttribute("date", formattedDate);
        model.addAttribute("now", new Date());
        model.addAttribute("name", user.getFirstName());
        model.addAttribute("role", user.getRole());



        return "assignments";
    }

}
