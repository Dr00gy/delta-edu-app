package org.edu_app.controller;

import lombok.RequiredArgsConstructor;
import org.edu_app.model.entity.Assignment;
import org.edu_app.model.entity.Subject;
import org.edu_app.model.entity.Submission;
import org.edu_app.model.entity.Role;
import org.edu_app.service.AssignmentService;
import org.edu_app.service.EnrollmentService;
import org.edu_app.service.SubjectService;
import org.edu_app.service.SubmissionService;
import org.edu_app.utils.CurrentUserUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Controller
@RequiredArgsConstructor
public class SubjectsController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private CurrentUserUtils currentUserUtils;
    
    private static final Logger logger = Logger.getLogger(SubjectsController.class.getName());

    @GetMapping("/subjects")
    public String showSubjects(Model model) {
        var user = currentUserUtils.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);
        
        if (user != null) {
            logger.info("User found: " + user.getFirstName() + " with role " + user.getRole());
            model.addAttribute("name", user.getFirstName());
            model.addAttribute("role", user.getRole().toString()); // Convert enum to string for comparison in Thymeleaf
            List<Subject> subjects;
            
            switch (user.getRole()) {
                case STUDENT:
                    subjects = subjectService.getSubjectsByStudentId(user.getId());
                    logger.info("Student subjects found: " + subjects.size());
                    
                    // Calculate missing assignments for each subject by comparing submissions not being in the grades table
                    Map<Long, SubjectAssignmentStatus> subjectStatusMap = new HashMap<>();
                    List<Submission> studentSubmissions = submissionService.getSubmissionsByStudent(user.getId());
                    logger.info("Student submissions found: " + studentSubmissions.size());
                    
                    for (Subject subject : subjects) {
                        List<Assignment> subjectAssignments = assignmentService.getAssignmentsBySubjectIds(List.of(subject.getId()));
                        logger.info("Subject " + subject.getName() + " has " + subjectAssignments.size() + " assignments");
                        
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
                        
                        logger.info("Subject " + subject.getName() + " status: " + status + " with " + missingCount + " missing assignments");
                        subjectStatusMap.put(subject.getId(), new SubjectAssignmentStatus(missingCount, status));
                    }
                    
                    model.addAttribute("subjectStatusMap", subjectStatusMap);
                    break;
                    
                case TEACHER:
                    subjects = subjectService.getSubjectsByTeacherId(user.getId());
                    logger.info("Teacher subjects found: " + subjects.size());
                    Map<Long, Long> enrollmentCounts = enrollmentService.getEnrollmentCountsBySubject();
                    model.addAttribute("subjectEnrollmentMap", enrollmentCounts);
                    
                    // If teacher has subjects, set current subject ID to the first one
                    if (!subjects.isEmpty()) {
                        model.addAttribute("currentSubjectId", subjects.get(0).getId());
                    }
                    break;
                    
                case ADMIN:
                    subjects = subjectService.getAllSubjects();
                    logger.info("Admin subjects found: " + subjects.size());
                    model.addAttribute("subjectEnrollmentMap", enrollmentService.getEnrollmentCountsBySubject());
                    break;
                    
                default:
                    subjects = List.of();
                    logger.warning("Unknown role: " + user.getRole());
                    break;
            }
            
            model.addAttribute("subjects", subjects);
        } else {
            logger.warning("No user found in current context");
            model.addAttribute("name", "Unknown");
            model.addAttribute("role", "Unknown");
            model.addAttribute("subjects", List.of());
        }
        
        model.addAttribute("date", formattedDate);
        return "subjects";
    }
    
    // Helper class to store missing assignments count and status, public static for Thymeleaf access
    public static class SubjectAssignmentStatus {
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

    @GetMapping("/subjects/{id}")
    public String showSubjectDetails(@PathVariable("id") Long subjectId, Model model) {
        var user = currentUserUtils.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        if (user != null) {
            // Fetch subject by id (make it a name later)
            Subject subject = subjectService.getSubjectById(subjectId);
            if (subject == null) {
                logger.warning("Subject not found with id: " + subjectId);
                return "redirect:/subjects"; // Redirect if subject not found
            }
            
            model.addAttribute("name", user.getFirstName());
            model.addAttribute("role", user.getRole().toString()); // Convert enum to string for Thymeleaf
            model.addAttribute("subject", subject); // Add the subject object to the model
            model.addAttribute("date", formattedDate);

            if (user.getRole() == Role.STUDENT) {
                List<Assignment> assignments = assignmentService.getAssignmentsBySubjectIds(List.of(subjectId));
                model.addAttribute("assignments", assignments);
                List<Submission> submissions = submissionService.getSubmissionsByStudent(user.getId());
                model.addAttribute("submissions", submissions);
            } else if (user.getRole() == Role.TEACHER) {
                Map<Long, Long> enrollmentCounts = enrollmentService.getEnrollmentCountsBySubject();
                model.addAttribute("subjectEnrollmentMap", enrollmentCounts);
            }

        } else {
            logger.warning("No user found in current context");
            model.addAttribute("name", "Unknown");
            model.addAttribute("role", "Unknown");
        }
        return "subjectDetails";
    }
}