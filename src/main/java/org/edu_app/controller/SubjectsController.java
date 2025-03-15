package org.edu_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SubjectsController {

    @GetMapping("/subjects")
    public String subjects(Model model) {

        return "subjects";
    }
}
