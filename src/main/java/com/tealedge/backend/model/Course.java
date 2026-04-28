package com.tealedge.backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    
    @Column(length = 1000)
    private String referenceMaterial;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id")
    private User faculty;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    public Course() {}

    public Course(String title, String description, User faculty, String referenceMaterial) {
        this.title = title;
        this.description = description;
        this.faculty = faculty;
        this.referenceMaterial = referenceMaterial;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public User getFaculty() { return faculty; }
    public String getReferenceMaterial() { return referenceMaterial; }
    public List<Task> getTasks() { return tasks; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setFaculty(User faculty) { this.faculty = faculty; }
    public void setReferenceMaterial(String referenceMaterial) { this.referenceMaterial = referenceMaterial; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
}
