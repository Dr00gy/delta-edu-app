package org.edu_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GradingController {

    @GetMapping("/grading")
    public String grading(Model model) {

        return "grading";
    }
}
