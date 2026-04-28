package com.tealedge.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    public Task() {}

    public Task(String title, String deadline, Course course) {
        this.title = title;
        this.deadline = deadline;
        this.course = course;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDeadline() { return deadline; }
    public Course getCourse() { return course; }

    public void setTitle(String title) { this.title = title; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setCourse(Course course) { this.course = course; }
}
