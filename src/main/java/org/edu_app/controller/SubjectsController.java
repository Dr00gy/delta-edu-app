package org.edu_app.controller;

import lombok.RequiredArgsConstructor;
import org.edu_app.model.entity.Assignment;
import org.edu_app.model.entity.Subject;
import org.edu_app.model.entity.Submission;
import org.edu_app.service.AssignmentService;
import org.edu_app.service.EnrollmentService;
import org.edu_app.service.SubjectService;
import org.edu_app.service.SubmissionService;
import org.edu_app.utils.CurrentUserUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SubjectsController {

    private final SubjectService subjectService;
    private final EnrollmentService enrollmentService;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;
    private final CurrentUserUtils currentUserUtils;

    @GetMapping("/subjects")
    public String showSubjects(Model model) {
        var user = currentUserUtils.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);
        
        if (user != null) {
            model.addAttribute("name", user.getFirstName());
            model.addAttribute("role", user.getRole());
            List<Subject> subjects;
            
            switch (user.getRole()) {
                case STUDENT:
                    subjects = subjectService.getSubjectsByStudentId(user.getId());
                    // Calculate missing assignments for each subject
                    Map<Long, SubjectAssignmentStatus> subjectStatusMap = new HashMap<>();
                    List<Submission> studentSubmissions = submissionService.getSubmissionsByStudent(user.getId());
                    
                    for (Subject subject : subjects) {
                        List<Assignment> subjectAssignments = subject.getAssignments();
                        
                        // Get all assignment IDs for this subject
                        List<Long> assignmentIds = subjectAssignments.stream()
                            .map(Assignment::getId)
                            .collect(Collectors.toList());
                        
                        // Get all submissions by this student for these assignments
                        List<Long> submittedAssignmentIds = studentSubmissions.stream()
                            .filter(s -> assignmentIds.contains(s.getAssignment().getId()))
                            .map(s -> s.getAssignment().getId())
                            .collect(Collectors.toList());
                        
                        int missingCount = 0;
                        boolean hasMissedDeadlines = false;
                        boolean hasUpcomingDeadlines = false;
                        
                        // Check each assignment
                        for (Assignment assignment : subjectAssignments) {
                            if (!submittedAssignmentIds.contains(assignment.getId())) {
                                missingCount++;
                                
                                // Check deadline against current time
                                LocalDateTime now = LocalDateTime.now();
                                if (assignment.getDeadline().isBefore(now)) {
                                    hasMissedDeadlines = true;
                                } else {
                                    hasUpcomingDeadlines = true;
                                }
                            }
                        }
                        
                        // Determine status
                        String status = "excellent"; // Default if no missing assignments
                        if (missingCount > 0) {
                            if (hasMissedDeadlines) {
                                status = "poor";
                            } else if (hasUpcomingDeadlines) {
                                status = "average";
                            }
                        }
                        
                        subjectStatusMap.put(subject.getId(), new SubjectAssignmentStatus(missingCount, status));
                    }
                    
                    model.addAttribute("subjectStatusMap", subjectStatusMap);
                    break;
                    
                case TEACHER:
                    subjects = subjectService.getSubjectsByTeacherId(user.getId());
                    model.addAttribute("subjectEnrollmentMap", enrollmentService.getEnrollmentCountsBySubject());
                    break;
                    
                case ADMIN:
                    subjects = subjectService.getAllSubjects();
                    model.addAttribute("subjectEnrollmentMap", enrollmentService.getEnrollmentCountsBySubject());
                    break;
                    
                default:
                    subjects = List.of();
                    break;
            }
            
            model.addAttribute("subjects", subjects);
        } else {
            model.addAttribute("name", "Unknown");
            model.addAttribute("role", "Unknown");
            model.addAttribute("subjects", List.of());
        }
        
        model.addAttribute("date", formattedDate);
        return "subjects";
    }
    
    // Helper class to store missing assignments count and status
    private static class SubjectAssignmentStatus {
        private final int missingCount;
        private final String status;
        
        public SubjectAssignmentStatus(int missingCount, String status) {
            this.missingCount = missingCount;
            this.status = status;
        }
        
        public int getMissingCount() {
            return missingCount;
        }
        
        public String getStatus() {
            return status;
        }
    }
}