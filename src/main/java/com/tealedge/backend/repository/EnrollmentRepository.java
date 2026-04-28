package com.tealedge.backend.repository;

import com.tealedge.backend.model.Course;
import com.tealedge.backend.model.Enrollment;
import com.tealedge.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(User student);

    @Transactional
    void deleteByCourse(Course course);
}
