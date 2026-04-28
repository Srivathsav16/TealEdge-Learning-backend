package com.tealedge.backend.repository;

import com.tealedge.backend.model.Submission;
import com.tealedge.backend.model.Task;
import com.tealedge.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudent(User student);
    List<Submission> findByTask(Task task);
    boolean existsByStudentAndTask(User student, Task task);
}
