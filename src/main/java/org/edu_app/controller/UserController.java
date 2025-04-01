package vpsi.kelvin.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vpsi.kelvin.model.dto.UserCreateDTO;
import vpsi.kelvin.model.dto.UserDTO;
import vpsi.kelvin.model.entity.User;
import vpsi.kelvin.service.UserService;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "user", description = "User API")
public class UserController {
    private UserService userService;
    private ModelMapper modelMapper;


    @GetMapping("/subjects/{subjectId}/students")
    public List<UserDTO> searchStudents(@PathVariable Long subjectId) {
        //List<User> users = userService.findStudentsBySubject(subjectId);
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();
    }
    @GetMapping("/{subjectId}/students/{studentId}")
    public UserDTO getStudent(@PathVariable Long subjectId, @PathVariable Long studentId) {
        // Získat studenta z daného předmětu, např.:
        // User user = userService.getStudentBySubject(subjectId, studentId);
        return modelMapper.map(user, UserDTO.class);
    }

    @PostMapping("/addUser")
    public void addUser(@RequestBody UserCreateDTO userCreateDTO){
        User user = modelMapper.map(userCreateDTO, User.class);
        userService.addUser(user);

    }
    @PutMapping("/update/{id}")
    public void updateUser(@PathVariable Long id,@RequestBody UserDTO userDTO){
        User user = modelMapper.map(userDTO, User.class);
        userService.updateUser(id, user);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
}
