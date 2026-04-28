package com.tealedge.backend.dto;

import java.time.LocalDateTime;

public class SubmissionDTO {
    private Long id;
    private UserDTO student;
    private Long taskId;
    private LocalDateTime submittedAt;
    private String grade;
    private String fileName;
    private String fileType;
    private String fileData;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserDTO getStudent() { return student; }
    public void setStudent(UserDTO student) { this.student = student; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getFileData() { return fileData; }
    public void setFileData(String fileData) { this.fileData = fileData; }
}
