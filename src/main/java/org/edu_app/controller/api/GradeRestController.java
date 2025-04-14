package org.edu_app.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.edu_app.model.dto.SubmissionGradeDTO;
import org.edu_app.model.dto.SubmissionGradeUpdateDTO;
import org.edu_app.model.dto.UserDTO;
import org.edu_app.model.entity.Grade;
import org.edu_app.model.entity.Submission;
import org.edu_app.model.entity.User;
import org.edu_app.service.SubmissionService;
import org.edu_app.service.UserService;
import org.edu_app.utils.CurrentUserUtils;
import org.edu_app.utils.FileRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeRestController {
    private final SubmissionService submissionService;
    private final CurrentUserUtils currentUserUtils;
    private final UserService userService;

    // Endpoint to load submission details for grading
    @GetMapping("/{submissionId}/details")
    public ResponseEntity<?> getSubmissionDetails(@PathVariable("submissionId") Long submissionId) {
        try {
            Submission submission = submissionService.getSubmission(submissionId);
            if (submission == null) {
                return ResponseEntity.notFound().build();
            }

            // Map submission to DTO
            SubmissionGradeDTO dto = new SubmissionGradeDTO();
            dto.setSubmissionId(submission.getId());
            dto.setStudentName(submission.getStudent().getFirstName() + " " +
                    submission.getStudent().getLastName());
            
            // Use the original file name from your file registry or a default name
            // Since Submission doesn't have fileName directly, we need to get it elsewhere
            
            // Use submission ID as key for FileRegistry lookup
            Map<String, List<String>> allFiles = FileRegistry.getAllFilePaths();
            List<String> filePaths = new ArrayList<>();

            if (filePaths.isEmpty()) {
                // Format submissionId with leading zeros (assuming up to 5 digits)
                String paddedSubmissionId = String.format("%05d", submissionId);
                
                // Search through all registry entries for matching file pattern
                for (Map.Entry<String, List<String>> entry : allFiles.entrySet()) {
                    for (String path : entry.getValue()) {
                        // Extract just the filename from the path
                        String fileName = path.substring(path.lastIndexOf('/') + 1);
                        
                        // Check if filename contains the submission ID in the expected pattern
                        // Pattern: xxxxxass_xxxxxstud_xxxxxsub.extension
                String regex = String.format("\\d{5}ass_\\d{5}stud_%ssub\\.[a-zA-Z0-9]+", paddedSubmissionId);
                            if (fileName.matches(regex)) {
                            filePaths.add(path);
                            break;
                        }
                    }
                    if (!filePaths.isEmpty()) break;
                }
            }

            if (filePaths.isEmpty()) {
                dto.setUploadedFileName("No file found");
                dto.setFileType("text");
                dto.setFileContent("No file found for this submission ID: " + submissionId);
            } else {
                // Process the found file
                String filePath = filePaths.get(0);
                // Extract the filename from the path
                String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
                dto.setUploadedFileName(fileName);
                
                byte[] fileContent = FileRegistry.getFileContent(filePath);
                
                if (fileContent != null) {
                    handleFileContent(dto, fileName, fileContent);
                } else {
                    dto.setFileType("text");
                    dto.setFileContent("Could not read file: " + fileName);
                }
            }
            
            dto.setStudentComment(submission.getStudentComment());
            
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body("Error loading submission details: " + ex.getMessage());
        }
    }
    
    // Helper method to handle different file types
    private void handleFileContent(SubmissionGradeDTO dto, String fileName, byte[] fileContent) {
        fileName = fileName.toLowerCase();
        
        // Determine file type based on extension
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
            fileName.endsWith(".png") || fileName.endsWith(".gif")) {
            
            // Handle image files
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
            String mimeType = "image/" + fileExtension;
            dto.setFileType("image");
            // Convert image to base64 for display in browser
            dto.setFileContent("data:" + mimeType + ";base64," + 
                              Base64.getEncoder().encodeToString(fileContent));
        } 
        else if (fileName.endsWith(".txt") || fileName.endsWith(".md") || 
                 fileName.endsWith(".html") || fileName.endsWith(".css") || 
                 fileName.endsWith(".js") || fileName.endsWith(".json") || 
                 fileName.endsWith(".xml") || fileName.endsWith(".csv")) {
            
            // Handle text files
            dto.setFileType("text");
            dto.setFileContent(new String(fileContent, StandardCharsets.UTF_8));
        }
        else {
            // Handle other file types
            dto.setFileType("other");
            dto.setFileContent("File type not supported for preview: " + fileName);
        }
    }

    // Endpoint to update grading information on a submission.
    @PostMapping("/updateGrade")
    public ResponseEntity<?> updateSubmissionGrade(@Valid @RequestBody SubmissionGradeUpdateDTO updateDTO) {
        try {
            Submission submission = submissionService.getSubmission(updateDTO.getSubmissionId());
            if (submission == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Retrieve the current teacher as a DTO and then load the full User entity.
            UserDTO teacherDTO = currentUserUtils.get();
            User currentTeacher = userService.getUserById(teacherDTO.getId());
            
            if (submission.getGrade() == null) {
                Grade grade = new Grade();
                grade.setScore(updateDTO.getGrade());
                grade.setFeedback(updateDTO.getTeacherFeedback());
                grade.setTeacher(currentTeacher); 
                grade.setSubmission(submission);
                submission.setGrade(grade);
                submissionService.updateSubmission(updateDTO.getSubmissionId(), submission);
            } else {
                Grade existingGrade = submission.getGrade();
                existingGrade.setScore(updateDTO.getGrade());
                existingGrade.setFeedback(updateDTO.getTeacherFeedback());
                existingGrade.setTeacher(currentTeacher);
            }
            
            return ResponseEntity.ok().build();
        } catch(Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body("Error updating grade: " + ex.getMessage());
        }
    }
}