package com.tealedge.backend.controller;

import com.tealedge.backend.dto.CourseDTO;
import com.tealedge.backend.exception.ResourceNotFoundException;
import com.tealedge.backend.exception.UnauthorizedException;
import com.tealedge.backend.model.Course;
import com.tealedge.backend.model.User;
import com.tealedge.backend.repository.CourseRepository;
import com.tealedge.backend.repository.EnrollmentRepository;
import com.tealedge.backend.repository.TaskRepository;
import com.tealedge.backend.repository.UserRepository;
import com.tealedge.backend.dto.TaskDTO;
import com.tealedge.backend.model.Task;
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
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Endpoints for managing courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Operation(summary = "Get all courses")
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseRepository.findAll().stream()
                .map(course -> modelMapper.map(course, CourseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Create a new course (faculty only)")
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO, HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!"faculty".equalsIgnoreCase(user.getRole())) {
            throw new UnauthorizedException("Only faculty can create courses");
        }

        Course course = modelMapper.map(courseDTO, Course.class);
        course.setFaculty(user);
        
        Course savedCourse = courseRepository.save(course);
        return ResponseEntity.ok(modelMapper.map(savedCourse, CourseDTO.class));
    }

    @Operation(summary = "Delete a course (faculty only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id, HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!"faculty".equalsIgnoreCase(user.getRole())) {
            throw new UnauthorizedException("Only faculty can delete courses");
        }

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getFaculty().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only delete your own courses");
        }

        enrollmentRepository.deleteByCourse(course);
        courseRepository.delete(course);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a single course by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return ResponseEntity.ok(modelMapper.map(course, CourseDTO.class));
    }

    @Operation(summary = "Edit a course (faculty only)")
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO, HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!"faculty".equalsIgnoreCase(user.getRole())) {
            throw new UnauthorizedException("Only faculty can edit courses");
        }

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getFaculty().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only edit your own courses");
        }

        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setReferenceMaterial(courseDTO.getReferenceMaterial());

        Course updatedCourse = courseRepository.save(course);
        return ResponseEntity.ok(modelMapper.map(updatedCourse, CourseDTO.class));
    }

    @Operation(summary = "Add a task to a course (faculty only)")
    @PostMapping("/{id}/tasks")
    public ResponseEntity<TaskDTO> addTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO, HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!"faculty".equalsIgnoreCase(user.getRole())) {
            throw new UnauthorizedException("Only faculty can add tasks");
        }

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getFaculty().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only add tasks to your own courses");
        }

        Task task = modelMapper.map(taskDTO, Task.class);
        task.setCourse(course);
        Task savedTask = taskRepository.save(task);

        return ResponseEntity.ok(modelMapper.map(savedTask, TaskDTO.class));
    }
}
