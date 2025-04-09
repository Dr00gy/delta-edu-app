package org.edu_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class SubjectsStudentsController { // TODO: In Figma, it is the student management on the right (not a popup anymore)

    @GetMapping("/subjects/students")
    public String showSubjectStudents(Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        model.addAttribute("name", "Duško");
        model.addAttribute("date", formattedDate);
        model.addAttribute("role", "Student");

        return "subjectsStudents";
    }
}