package vpsi.kelvin.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vpsi.kelvin.model.dto.SubjectDTO;
import vpsi.kelvin.model.entity.Subject;
import vpsi.kelvin.service.SubjectService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subjects")
@Tag(name = "subject", description = "Subject API")
public class SubjectController {

    private SubjectService subjectService;
    private ModelMapper modelMapper;

    @PostMapping("/add")
    public void addSubject(@RequestBody SubjectDTO subjectDTO) {
        Subject subject = modelMapper.map(subjectDTO, Subject.class);
        subjectService.addSubject(subject, subjectDTO.getTeacherId());
    }

    @PutMapping("/update/{id}")
    public void updateSubject(@PathVariable Long id, @RequestBody SubjectDTO subjectDTO) {
        Subject subject = modelMapper.map(subjectDTO, Subject.class);
        subjectService.updateSubject(id, subject);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
    }

    @GetMapping("/{id}")
    public SubjectDTO getSubject(@PathVariable Long id) {
        Subject subject = subjectService.getSubject(id);
        return modelMapper.map(subject, SubjectDTO.class);
    }

    @GetMapping
    public List<SubjectDTO> getAllSubjects() {
        List<Subject> subjects = subjectService.getAllSubjects();
        return subjects.stream()
                .map(subject -> modelMapper.map(subject, SubjectDTO.class))
                .toList();
    }
}
}
