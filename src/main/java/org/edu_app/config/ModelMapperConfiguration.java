package org.edu_app.configuration;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.edu_app.model.dto.AssignmentDTO;
import org.edu_app.model.dto.EnrollmentDTO;
import org.edu_app.model.dto.GradeDTO;
import org.edu_app.model.dto.SubmissionDTO;
import org.edu_app.model.dto.UserDTO;
import org.edu_app.model.dto.SubjectDTO;
import org.edu_app.model.entity.Assignment;
import org.edu_app.model.entity.Enrollment;
import org.edu_app.model.entity.Grade;
import org.edu_app.model.entity.Submission;
import org.edu_app.model.entity.User;
import org.edu_app.model.entity.Subject;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        TypeMap<Subject, SubjectDTO> subjectTypeMap = modelMapper.typeMap(Subject.class, SubjectDTO.class);
        subjectTypeMap.addMappings(mapper -> {
            mapper.skip(SubjectDTO::setTeacherName);
            mapper.map(src -> src.getTeacher().getId(), SubjectDTO::setTeacherId);
        });

        subjectTypeMap.setPostConverter(context -> {
            Subject source = context.getSource();
            SubjectDTO destination = context.getDestination();
            if (source.getTeacher() != null) {
                String teacherName = source.getTeacher().getFirstName() + " " + source.getTeacher().getLastName();
                destination.setTeacherName(teacherName);
            }
            return destination;
        });

        TypeMap<UserDTO, User> userMap = modelMapper.typeMap(UserDTO.class, User.class);
        userMap.addMappings(mapper -> {
            mapper.skip(User::setId);
            mapper.skip(User::setLastModified);
            mapper.skip(User::setOperation);
        });

        TypeMap<SubmissionDTO, Submission> submissionMap = modelMapper.typeMap(SubmissionDTO.class, Submission.class);
        submissionMap.addMappings(mapper -> {
            mapper.skip(Submission::setId);
        });

        TypeMap<AssignmentDTO, Assignment> assignmentMap = modelMapper.typeMap(AssignmentDTO.class, Assignment.class);
        assignmentMap.addMappings(mapper -> {
            mapper.skip(Assignment::setId);
        });

        TypeMap<EnrollmentDTO, Enrollment> enrollmentMap = modelMapper.typeMap(EnrollmentDTO.class, Enrollment.class);
        enrollmentMap.addMappings(mapper -> {
            mapper.skip(Enrollment::setId);
        });

        TypeMap<GradeDTO, Grade> gradeMap = modelMapper.typeMap(GradeDTO.class, Grade.class);
        gradeMap.addMappings(mapper -> {
            mapper.skip(Grade::setId);
        });

        return modelMapper;
    }
}
