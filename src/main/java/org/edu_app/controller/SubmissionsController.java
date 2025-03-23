package org.edu_app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class SubmissionsController {

    @Value("${spring.upload.directory}") // From YAML
    private String uploadDirectory;

    @GetMapping("/upload")
    public String showUploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String singleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a file to upload.");
            return "upload";
        }

        try {
            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadDirectory + File.separator + file.getOriginalFilename());
            Files.write(path, bytes);

            model.addAttribute("success", "File uploaded successfully!");
            model.addAttribute("fileName", file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "An error occurred during file upload: " + e.getMessage());
        }

        return "upload";
    }
}
