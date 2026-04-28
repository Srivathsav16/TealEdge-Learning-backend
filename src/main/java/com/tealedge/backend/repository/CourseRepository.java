package com.tealedge.backend.repository;

import com.tealedge.backend.model.Course;
import com.tealedge.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByFaculty(User faculty);
}
