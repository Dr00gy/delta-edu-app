package org.edu_app.controller;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ViewController {
    @GetMapping("/view")
    public String showUploadedFile(Model model, @RequestParam(name = "filename", required = false) String filename) {
        if (filename == null || filename.isEmpty()) {
            model.addAttribute("errorMessage", "Nebyl vložen soubor");
            return "view";
        }

        Path path = Paths.get(filename);


        try {
            String mimetype = Files.probeContentType(path);
            if (mimetype == null || mimetype.isEmpty()) {
                mimetype = "application/octet-stream";
            }
            model.addAttribute("mimetype", mimetype);
            
            if (mimetype.startsWith("text")) {
                String fileContent = Files.readString(path, StandardCharsets.UTF_8);
                model.addAttribute("fileContent", fileContent);
            } else if (mimetype.startsWith("image")) {
                model.addAttribute("imageFilename", filename);
            } else {
              model.addAttribute("downloadFilename", filename);
            }


        }catch (IOException e) {
            model.addAttribute("errorMessage", "Chyba čtení souboru: " + e.getMessage());
        }

        return "view";
    }


    @GetMapping("/file")
    public ResponseEntity<Resource> serveFile(@RequestParam("filename") String filename) {

        Path path = Paths.get(filename);

        try {
            byte[] data = Files.readAllBytes(path);
            ByteArrayResource resource = new ByteArrayResource(data);

            String mimeType = Files.probeContentType(path);
            if (mimeType == null || mimeType.isEmpty()) {
                mimeType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName().toString() + "\"")
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body((Resource) resource);
        }catch (IOException e){
            return ResponseEntity.notFound().build();
        }
    }
}
