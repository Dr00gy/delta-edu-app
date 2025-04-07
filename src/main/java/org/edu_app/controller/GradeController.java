package vpsi.kelvin.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vpsi.kelvin.model.dto.GradeDTO;
import vpsi.kelvin.model.entity.Grade;
import vpsi.kelvin.service.GradeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/grades")
@Tag(name = "grade", description = "Grade API")
public class GradeController {

    private final GradeService gradeService;
    private final ModelMapper modelMapper;

    @PostMapping("/assign")
    public void assignGrade(@RequestBody GradeDTO gradeDTO) {
        gradeService.assignGradeToSubmission(gradeDTO);
    }

    @PutMapping("/update/{id}")
    public void updateGrade(@PathVariable Long id, @RequestBody GradeDTO gradeDTO) {
        Grade grade = modelMapper.map(gradeDTO, Grade.class);
        gradeService.updateGrade(id, grade);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
    }

    @GetMapping("/{id}")
    public GradeDTO getGrade(@PathVariable Long id) {
        Grade grade = gradeService.getGrade(id);
        return modelMapper.map(grade, GradeDTO.class);
    }


    @GetMapping
    public List<GradeDTO> getAllGrades() {
        List<Grade> grades = gradeService.getAllGrades();
        return grades.stream()
                .map(g -> modelMapper.map(g, GradeDTO.class))
                .toList();
    }
}
