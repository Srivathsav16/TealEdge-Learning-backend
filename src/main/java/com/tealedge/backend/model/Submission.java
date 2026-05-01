package com.tealedge.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id")
    private Task task;

    private LocalDateTime submittedAt;
    private String grade;

    private String fileName;
    private String fileType;
    
    @Column(columnDefinition = "TEXT")
    private String fileData;

    public Submission() {}

    public Submission(User student, Task task) {
        this.student = student;
        this.task = task;
        this.submittedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getStudent() { return student; }
    public Task getTask() { return task; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public String getGrade() { return grade; }
    public String getFileName() { return fileName; }
    public String getFileType() { return fileType; }
    public String getFileData() { return fileData; }

    public void setStudent(User student) { this.student = student; }
    public void setTask(Task task) { this.task = task; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public void setFileData(String fileData) { this.fileData = fileData; }
}
