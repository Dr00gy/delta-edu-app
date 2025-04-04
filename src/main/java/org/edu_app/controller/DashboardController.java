package org.edu_app.controller;

import org.edu_app.model.dto.UserDTO;
import org.edu_app.utils.InitDBManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class DashboardController {

    @Autowired
    private InitDBManager dbManager;

    @GetMapping("/")
    public String showHome(Model model) {
        // Get authenticated user email
        String authenticatedEmail = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        // Fetch user details via initial DB manager
        UserDTO userDetails = dbManager.getUserDetails(authenticatedEmail);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        // Add attributes to model from userDetails
        if (userDetails != null) {
            model.addAttribute("name", userDetails.getFirstName());
            model.addAttribute("role", userDetails.getRole());
        } else {
            model.addAttribute("name", "Unknown");
            model.addAttribute("role", "Unknown");
        }
        model.addAttribute("date", formattedDate);

        return "index";
    }
}
