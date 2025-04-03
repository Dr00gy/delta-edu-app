package org.edu_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class GradingController {

    @GetMapping("/grading")
<<<<<<< Updated upstream
    public String grading(Model model) { // NOTE: See SubjectsController for TODOs that also apply to Gradings
=======
    public String showGrading(Model model) {
>>>>>>> Stashed changes
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        model.addAttribute("name", "Duško");
        model.addAttribute("date", formattedDate);
        model.addAttribute("role", "Student");

        return "grading";
    }
}
