package org.edu_app.controller;

import lombok.RequiredArgsConstructor;
import org.edu_app.model.dto.UserDTO;
import org.edu_app.model.entity.Grade;
import org.edu_app.model.entity.Role;
import org.edu_app.model.entity.Subject;
import org.edu_app.service.GradeService;
import org.edu_app.service.SubjectService;
import org.edu_app.utils.CurrentUserUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequiredArgsConstructor
public class ExportController {

    private final GradeService gradeService;
    private final CurrentUserUtils currentUserUtils;
    private final SubjectService subjectService;

    @Value("${spring.upload.directory}")
    private String uploadDirectory;

    @GetMapping("/export")
    public String showExportable() {
        return "export";
    }

    @GetMapping("/export/grades")
    public ResponseEntity<byte[]> exportGrades() {
        try {
            UserDTO user = currentUserUtils.get();
            List<Grade> grades;

            if (user.getRole() == Role.STUDENT) {
                grades = gradeService.getAllGradesByStudentId(user.getId());
            } else if (user.getRole() == Role.TEACHER) {
                grades = gradeService.getAllGradesByTeacherId(user.getId());
            } else {
                grades = gradeService.getAllGrades(); // Admin
            }

            // Convert grades to exportable format
            List<Map<String, Object>> exportData = new ArrayList<>();
            for (Grade grade : grades) {
                Map<String, Object> record = new LinkedHashMap<>();
                record.put("subject", grade.getSubmission().getAssignment().getSubject().getName());
                record.put("assignment", grade.getSubmission().getAssignment().getName());
                record.put("score", grade.getScore());
                record.put("feedback", grade.getFeedback());
                record.put("student", grade.getSubmission().getStudent().getFirstName() + " " + grade.getSubmission().getStudent().getLastName());
                exportData.add(record);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            byte[] jsonBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(exportData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"grades_export.json\"")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(("Error exporting grades: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/export/files")
    public ResponseEntity<InputStreamResource> exportFiles() {
        try {
            UserDTO user = currentUserUtils.get();
            File uploadDir = new File(uploadDirectory);
            
            if (!uploadDir.exists() || !uploadDir.isDirectory()) {
                throw new IOException("Upload directory does not exist or is not a directory");
            }
            
            // Create a temp file for the zip
            Path tempFile = Files.createTempFile("submissions", ".zip");
            
            // Create zip file
            try (FileOutputStream fos = new FileOutputStream(tempFile.toFile());
                 ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                
                File[] files = uploadDir.listFiles();
                if (files != null) {
                    List<File> filesToInclude = filterFilesByUserRole(files, user);
                    
                    for (File file : filesToInclude) {
                        addFileToZip(file, file.getName(), zipOut);
                    }
                }
            }
            
            // Create resource from the temp file
            InputStream inputStream = new FileInputStream(tempFile.toFile());
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            // Set up the response
            String filename = "submission_files.zip";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private List<File> filterFilesByUserRole(File[] files, UserDTO user) {
        List<File> filteredFiles = new ArrayList<>();
        
        if (user.getRole() == Role.ADMIN) {
            // Admin gets all files
            filteredFiles.addAll(Arrays.asList(files));
        } else if (user.getRole() == Role.TEACHER) {
            // Teacher gets files for assignments in their subjects
            List<Subject> teacherSubjects = subjectService.getSubjectsByTeacherId(user.getId());
            List<Long> assignmentIds = teacherSubjects.stream()
                    .flatMap(subject -> subject.getAssignments().stream())
                    .map(assignment -> assignment.getId())
                    .collect(Collectors.toList());
            
            for (File file : files) {
                String fileName = file.getName();
                if (matchesAssignmentIds(fileName, assignmentIds)) {
                    filteredFiles.add(file);
                }
            }
        } else if (user.getRole() == Role.STUDENT) {
            // Student gets their own files
            Long studentId = user.getId();
            
            for (File file : files) {
                String fileName = file.getName();
                if (matchesStudentId(fileName, studentId)) {
                    filteredFiles.add(file);
                }
            }
        }
        
        return filteredFiles;
    }
    
    private boolean matchesAssignmentIds(String fileName, List<Long> assignmentIds) {
        // Format is xxxxxass_xxxxxstud_xxxxxsub.extension
        String[] parts = fileName.split("_");
        if (parts.length >= 1) {
            String assignmentPart = parts[0];
            if (assignmentPart.endsWith("ass") && assignmentPart.length() > 3) {
                String idPart = assignmentPart.substring(0, assignmentPart.length() - 3);
                // Remove leading zeroes and parse as long
                Long assignmentId = Long.parseLong(idPart.replaceFirst("^0+(?!$)", ""));
                return assignmentIds.contains(assignmentId);
            }
        }
        return false;
    }
    
    private boolean matchesStudentId(String fileName, Long studentId) {
        // Format is xxxxxass_xxxxxstud_xxxxxsub.extension
        String[] parts = fileName.split("_");
        if (parts.length >= 2) {
            String studentPart = parts[1];
            if (studentPart.endsWith("stud") && studentPart.length() > 4) {
                String idPart = studentPart.substring(0, studentPart.length() - 4);
                // Remove leading zeroes and parse as long
                Long fileStudentId = Long.parseLong(idPart.replaceFirst("^0+(?!$)", ""));
                return fileStudentId.equals(studentId);
            }
        }
        return false;
    }
    
    private void addFileToZip(File file, String fileName, ZipOutputStream zipOut) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        
        fis.close();
    }
}