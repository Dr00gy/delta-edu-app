package org.edu_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class DashboardController {

    @GetMapping("/")
<<<<<<< Updated upstream
    public String home(Model model) {
=======
    public String showHome(Model model) {
        // Get authenticated user email
        String authenticatedEmail = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        // Fetch user details via initial DB manager
        UserDTO userDetails = dbManager.getUserDetails(authenticatedEmail);

>>>>>>> Stashed changes
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        model.addAttribute("name", "Duško");
        model.addAttribute("date", formattedDate);
        model.addAttribute("role", "Student");
        return "index";
    }
}
