package com.tealedge.backend.dto;

public class EnrollmentDTO {
    private Long id;
    private UserDTO student;
    private CourseDTO course;

    public EnrollmentDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserDTO getStudent() { return student; }
    public void setStudent(UserDTO student) { this.student = student; }

    public CourseDTO getCourse() { return course; }
    public void setCourse(CourseDTO course) { this.course = course; }
}
