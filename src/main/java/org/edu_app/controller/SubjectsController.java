package org.edu_app.controller;

import org.edu_app.model.entity.Subject;
import org.edu_app.repository.SubjectRepository;
import org.edu_app.service.SubjectService;
import org.edu_app.model.entity.Enrollment;
import org.edu_app.repository.EnrollmentRepository;
import org.edu_app.service.EnrollmentService;
import org.edu_app.utils.CurrentUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
public class SubjectsController {
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    CurrentUserUtils currentUserUtils;

    @GetMapping("/subjects")
    public String showSubjects(Model model) {
        var user = currentUserUtils.get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        model.addAttribute("name", user.getFirstName());
        model.addAttribute("date", formattedDate);
        model.addAttribute("role", user.getRole().name()); // Neccesary for Thymeleaf switch to work on the .html

        Map<Long, Long> subjectEnrollmentMap = enrollmentService.getEnrollmentCountsBySubject();

        var subjects = switch (user.getRole()) {
            case STUDENT -> subjectService.getSubjectsByStudentId(user.getId());
            case TEACHER -> subjectService.getSubjectsByTeacherId(user.getId());
            case ADMIN -> subjectService.getAllSubjects();
        };
        //System.out.println("Subjects for user " + user.getId() + " (" + user.getRole() + "): " + subjects.size());
        model.addAttribute("subjects", subjects);
        model.addAttribute("subjectEnrollmentMap", subjectEnrollmentMap);



        return "subjects";
    }
}
