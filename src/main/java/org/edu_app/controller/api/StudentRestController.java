package org.edu_app.controller.api;

import lombok.RequiredArgsConstructor;
import org.edu_app.model.dto.UserDTO;
import org.edu_app.model.entity.Role;
import org.edu_app.model.entity.User;
import org.edu_app.service.UserService;
import org.edu_app.utils.CurrentUserUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StudentRestController {

    private final UserService userService;
    private final CurrentUserUtils currentUserUtils;

    @GetMapping("/students")
    public ResponseEntity<List<UserDTO>> getAllStudents() {
        var currentUser = currentUserUtils.get();
        
        // Only admins and teachers can access this endpoint
        if (currentUser == null || (!currentUser.getRole().equals(Role.ADMIN) && !currentUser.getRole().equals(Role.TEACHER))) {
            return ResponseEntity.status(403).build();
        }
        
        List<User> students = userService.getAllUsers().stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .collect(Collectors.toList());
                
        List<UserDTO> studentDTOs = students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(studentDTOs);
    }
    
    @GetMapping("/available-students")
    public ResponseEntity<List<UserDTO>> getAvailableStudents() {
        var currentUser = currentUserUtils.get();
        
        // Only admins and teachers can access this endpoint
        if (currentUser == null || (!currentUser.getRole().equals(Role.ADMIN) && !currentUser.getRole().equals(Role.TEACHER))) {
            return ResponseEntity.status(403).build();
        }
        
        // Get all users and filter in memory instead of at database level (again, so we do not have to edit User stuff or DB)
        List<User> allUsers = userService.getAllUsers();
        List<User> students = allUsers.stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .collect(Collectors.toList());
                
        List<UserDTO> studentDTOs = students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(studentDTOs);
    }

    @GetMapping("/students-alternative")
    public ResponseEntity<List<UserDTO>> getAllStudentsAlternative() {
        var currentUser = currentUserUtils.get();
        
        // Only admins and teachers can access this endpoint
        if (currentUser == null || (!currentUser.getRole().equals(Role.ADMIN) && !currentUser.getRole().equals(Role.TEACHER))) {
            return ResponseEntity.status(403).build();
        }
        
        // Get all users instead of filtering at the database level (again, so we do not have to edit User stuff or DB)
        List<User> allUsers = userService.getAllUsers();
        
        // Filter students in Java code using enum comparison instead of string comparison
        List<User> students = allUsers.stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .collect(Collectors.toList());
        
        List<UserDTO> studentDTOs = students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(studentDTOs);
    }
        
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        return dto;
    }
}