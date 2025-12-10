package com.classroom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDateTime deadline;

    @Column(name = "solution_url")
    private String solutionUrl;

    @Column(name = "solution_text", columnDefinition = "TEXT")
    private String solutionText;

    @Column(name = "solution_uploaded_by")
    private Long solutionUploadedBy;

    @Column(name = "solution_uploaded_at")
    private LocalDateTime solutionUploadedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = TaskStatus.OPEN;
        }
    }

    public enum TaskStatus {
        OPEN,
        IN_PROGRESS,
        COMPLETED
    }
}

