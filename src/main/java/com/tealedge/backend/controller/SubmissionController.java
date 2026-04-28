package com.tealedge.backend.controller;

import com.tealedge.backend.dto.SubmissionDTO;
import com.tealedge.backend.model.Course;
import com.tealedge.backend.model.Submission;
import com.tealedge.backend.model.Task;
import com.tealedge.backend.model.User;
import com.tealedge.backend.repository.CourseRepository;
import com.tealedge.backend.repository.SubmissionRepository;
import com.tealedge.backend.repository.TaskRepository;
import com.tealedge.backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/submissions")
@Tag(name = "Submissions", description = "Endpoints for managing submissions")
public class SubmissionController {

    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Operation(summary = "Submit a task (student only)")
    @PostMapping("/task/{taskId}")
    public ResponseEntity<?> submitTask(@PathVariable Long taskId, @RequestBody Map<String, String> body, HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        User student = userRepository.findByEmail(email).orElseThrow();
        Task task = taskRepository.findById(taskId).orElseThrow();
        
        if (submissionRepository.existsByStudentAndTask(student, task)) {
            return ResponseEntity.badRequest().body("Already submitted");
        }
        
        Submission submission = new Submission(student, task);
        if (body != null) {
            submission.setFileName(body.get("fileName"));
            submission.setFileType(body.get("fileType"));
            submission.setFileData(body.get("fileData"));
        }
        
        submissionRepository.save(submission);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get my submissions (student only)")
    @GetMapping("/my")
    public ResponseEntity<List<SubmissionDTO>> getMySubmissions(HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        User student = userRepository.findByEmail(email).orElseThrow();
        List<SubmissionDTO> dtos = submissionRepository.findByStudent(student).stream()
                .map(s -> {
                    SubmissionDTO dto = modelMapper.map(s, SubmissionDTO.class);
                    dto.setTaskId(s.getTask().getId());
                    return dto;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @Operation(summary = "Get submissions for faculty courses (faculty only)")
    @GetMapping("/faculty")
    public ResponseEntity<List<SubmissionDTO>> getFacultySubmissions(HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        User faculty = userRepository.findByEmail(email).orElseThrow();
        
        List<Course> courses = courseRepository.findAll().stream()
                .filter(c -> c.getFaculty().getId().equals(faculty.getId()))
                .collect(Collectors.toList());
        List<SubmissionDTO> dtos = new ArrayList<>();
        
        for (Course c : courses) {
            for (Task t : c.getTasks()) {
                List<Submission> subs = submissionRepository.findByTask(t);
                for (Submission s : subs) {
                    SubmissionDTO dto = modelMapper.map(s, SubmissionDTO.class);
                    dto.setTaskId(t.getId());
                    dtos.add(dto);
                }
            }
        }
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Grade a submission (faculty only)")
    @PutMapping("/{id}/grade")
    public ResponseEntity<?> gradeSubmission(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        Submission submission = submissionRepository.findById(id).orElseThrow();
        submission.setGrade(body.get("grade"));
        submissionRepository.save(submission);
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "Delete a submission")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubmission(@PathVariable Long id) {
        submissionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
