package com.tealedge.backend.controller;

import com.tealedge.backend.dto.CourseDTO;
import com.tealedge.backend.dto.EnrollmentDTO;
import com.tealedge.backend.exception.BadRequestException;
import com.tealedge.backend.exception.ResourceNotFoundException;
import com.tealedge.backend.model.Course;
import com.tealedge.backend.model.Enrollment;
import com.tealedge.backend.model.User;
import com.tealedge.backend.repository.CourseRepository;
import com.tealedge.backend.repository.EnrollmentRepository;
import com.tealedge.backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enrollments")
@Tag(name = "Enrollments", description = "Endpoints for managing student enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Operation(summary = "Enroll a student in a course")
    @PostMapping
    public ResponseEntity<EnrollmentDTO> enroll(@RequestBody java.util.Map<String, Long> payload, HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Long courseId = payload.get("courseId");
        if (courseId == null) {
            throw new BadRequestException("courseId must be provided");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        boolean alreadyEnrolled = enrollmentRepository.findByStudent(student)
                .stream()
                .anyMatch(e -> e.getCourse().getId().equals(courseId));
                
        if (alreadyEnrolled) {
            throw new BadRequestException("Already enrolled in this course");
        }
        
        Enrollment enrollment = new Enrollment(student, course);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return ResponseEntity.ok(modelMapper.map(savedEnrollment, EnrollmentDTO.class));
    }

    @Operation(summary = "Get enrollments for the current student")
    @GetMapping("/my")
    public ResponseEntity<List<CourseDTO>> getMyEnrollments(HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<CourseDTO> courses = enrollmentRepository.findByStudent(student)
                .stream()
                .map(enrollment -> modelMapper.map(enrollment.getCourse(), CourseDTO.class))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(courses);
    }
}
