package org.edu_app.controller;

import org.edu_app.model.entity.Subject;
import org.edu_app.repository.SubjectRepository;
import org.edu_app.service.SubjectService;
import org.edu_app.utils.CurrentUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class SubjectsController {
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    CurrentUserUtils currentUserUtils;

    @GetMapping("/subjects")
    public String showSubjects(Model model) {
        var user = currentUserUtils.get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        model.addAttribute("name", user.getFirstName());
        model.addAttribute("date", formattedDate);
        model.addAttribute("role", user.getRole());


        // Add subjects to the model
        var subjects = switch (user.getRole()) {
            case STUDENT -> subjectService.getAllSubjects();
            // todo - perhaps add cases for students and teachers??
            case TEACHER -> subjectService.getAllSubjects();
            case ADMIN -> subjectService.getAllSubjects();
        };
        model.addAttribute("subjects", subjects);



        return "subjects";
    }
}
