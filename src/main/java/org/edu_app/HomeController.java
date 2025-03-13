package org.edu_app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("name", "Bunny");
        model.addAttribute("date", java.time.LocalDate.now().toString());
        return "index";
    }
}
