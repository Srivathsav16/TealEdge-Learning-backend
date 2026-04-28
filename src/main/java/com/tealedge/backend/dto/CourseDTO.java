package com.tealedge.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private String referenceMaterial;
    private UserDTO faculty;
    private List<TaskDTO> tasks = new ArrayList<>();

    public CourseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReferenceMaterial() { return referenceMaterial; }
    public void setReferenceMaterial(String referenceMaterial) { this.referenceMaterial = referenceMaterial; }

    public UserDTO getFaculty() { return faculty; }
    public void setFaculty(UserDTO faculty) { this.faculty = faculty; }

    public List<TaskDTO> getTasks() { return tasks; }
    public void setTasks(List<TaskDTO> tasks) { this.tasks = tasks; }
}
