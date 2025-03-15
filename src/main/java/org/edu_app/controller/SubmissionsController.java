package org.edu_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SubmissionsController {

    @GetMapping("/submissions")
    public String submissions(Model model) {

        return "submissions";
    }
}
