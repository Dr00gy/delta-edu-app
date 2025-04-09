package org.edu_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class AssignmentGradingController { // TODO: In Figma, it is the individual Assignment grading on the very right bottom (not a popup anymore either)

    @GetMapping("/assignments/grades")
    public String showSubjectSubmissions(Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        model.addAttribute("name", "Duško");
        model.addAttribute("date", formattedDate);
        model.addAttribute("role", "Student");

        return "assignmentGrading";
    }
}