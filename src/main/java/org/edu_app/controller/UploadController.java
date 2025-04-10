package org.edu_app.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.edu_app.model.dto.SubmissionCreateDTO;
import org.edu_app.model.entity.Assignment;
import org.edu_app.model.entity.Role;
import org.edu_app.model.entity.Submission;
import org.edu_app.model.entity.User;
import org.edu_app.model.dto.UserDTO;
import org.edu_app.service.AssignmentService;
import org.edu_app.service.SubmissionService;
import org.edu_app.service.EnrollmentService;
import org.edu_app.service.UserService;
import org.edu_app.utils.CurrentUserUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UploadController {
    
    @Value("${spring.upload.directory}")
    private String uploadDirectory;
    
    @Autowired
    private CurrentUserUtils currentUserUtils;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private EnrollmentService enrollmentService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;
    
    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        try {
            UserDTO currentUser = currentUserUtils.get();
            
            // Only allow students and admin to access this page
            if (currentUser.getRole() != Role.STUDENT && currentUser.getRole() != Role.ADMIN) {
                return "redirect:/access-denied";
            }
            
            // Get assignments the user can submit to
            List<Assignment> assignments;
            if (currentUser.getRole() == Role.ADMIN || currentUser.getId() == 1) {
                // Admin can submit to any assignment
                assignments = assignmentService.getAllAssignments();
            } else {
                // Students can only submit to assignments in subjects they r enrolled in
                assignments = getAssignmentsForStudent(currentUser.getId());
            }
            
            model.addAttribute("assignments", assignments);
            return "upload";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading upload form: " + e.getMessage());
            return "upload";
        }
    }
    
    @PostMapping("/upload")
    public String handleFileUpload(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("assignmentId") Long assignmentId,
            @RequestParam("studentComment") String studentComment,
            Model model) {

        if (files.length == 0) {
            model.addAttribute("error", "Please select at least one file to upload.");
            return "upload";
        }

        try {
            UserDTO currentUser = currentUserUtils.get();

            if (currentUser.getRole() != Role.STUDENT && currentUser.getRole() != Role.ADMIN 
                    && currentUser.getId() != 1) {
                model.addAttribute("error", "Only students and administrators can upload submissions");
                return "upload";
            }

            Assignment assignment = assignmentService.getAssignment(assignmentId);

            if (currentUser.getRole() == Role.STUDENT && currentUser.getId() != 1) {
                boolean isEnrolled = checkStudentEnrollment(currentUser.getId(), assignment.getSubject().getId());
                if (!isEnrolled) {
                    model.addAttribute("error", "You are not enrolled in this subject");
                    return showUploadForm(model);
                }
            }

            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            User student = userService.getUserById(currentUser.getId());

            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue; // Skip empty files
                }
                
                Submission submission = new Submission();
                submission.setStudent(student);
                submission.setAssignment(assignment);
                submission.setStudentComment(studentComment);
                submission.setSubmittedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

                // Save to get submission ID
                submissionService.addSubmission(submission);

                Long submissionId = submission.getId();

                // Get original filename and extension
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                }

                // Create the new filename with the correct pattern and extension (pattern needed to view contents of the file later)
                String newFileName = String.format(
                    "%05dass_%05dstud_%05dsub%s", 
                    assignment.getId(), 
                    student.getId(), 
                    submissionId,
                    extension
                );

                // Create the full path
                Path targetPath = Paths.get(uploadDirectory, newFileName);
                
                // Copy the file to the target location (replacing existing file with the same name)
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            model.addAttribute("success", "Files uploaded successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "An error occurred during file upload: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error: " + e.getMessage());
        }

        return showUploadForm(model);
    }
    
    private List<Assignment> getAssignmentsForStudent(Long studentId) {
        List<Long> enrolledSubjectIds = enrollmentService.getEnrolledSubjectIds(studentId);
        return assignmentService.getAssignmentsBySubjectIds(enrolledSubjectIds);
    }
    
    private boolean checkStudentEnrollment(Long studentId, Long subjectId) {
        return enrollmentService.isStudentEnrolledInSubject(studentId, subjectId);
    }
}