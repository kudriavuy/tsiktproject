package com.classroom.controller;

import com.classroom.model.Task;
import com.classroom.service.TaskService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {
    private final TaskService taskService;
    private final com.classroom.service.NotificationService notificationService;

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody CreateTaskRequest request) {
        try {
            Task task = taskService.createTask(
                    request.getGroupId(),
                    request.getCreatedBy(),
                    request.getTitle(),
                    request.getDescription(),
                    request.getDeadline() != null ? LocalDateTime.parse(request.getDeadline()) : null
            );
            // Send notification
            notificationService.sendNotification(
                    request.getGroupId(),
                    "TASK_CREATED",
                    "New task: " + task.getTitle(),
                    request.getCreatedBy()
            );
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable Long taskId) {
        return taskService.getTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Task>> getGroupTasks(@PathVariable Long groupId) {
        return ResponseEntity.ok(taskService.getTasksByGroup(groupId));
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody UpdateStatusRequest request) {
        Task task = taskService.updateTaskStatus(
                taskId,
                Task.TaskStatus.valueOf(request.getStatus())
        );
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{taskId}/solution")
    public ResponseEntity<?> addSolution(
            @PathVariable Long taskId,
            @RequestBody AddSolutionRequest request) {
        try {
            Task task = taskService.addSolution(
                    taskId,
                    request.getUserId(),
                    request.getSolutionText(),
                    request.getSolutionUrl()
            );
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{taskId}/solution")
    public ResponseEntity<?> updateSolution(
            @PathVariable Long taskId,
            @RequestBody AddSolutionRequest request) {
        try {
            Task task = taskService.updateSolution(
                    taskId,
                    request.getUserId(),
                    request.getSolutionText(),
                    request.getSolutionUrl()
            );
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/{taskId}/is-teacher")
    public ResponseEntity<Map<String, Boolean>> checkIfTeacher(
            @PathVariable Long taskId,
            @RequestParam Long userId) {
        try {
            Task task = taskService.getTaskById(taskId)
                    .orElseThrow(() -> new RuntimeException("Task not found"));
            boolean isTeacher = taskService.isUserTeacher(task.getGroupId(), userId);
            return ResponseEntity.ok(Map.of("isTeacher", isTeacher));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("isTeacher", false));
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request) {
        try {
            Task task = taskService.updateTask(
                    taskId,
                    request.getUserId(),
                    request.getTitle(),
                    request.getDescription(),
                    request.getDeadline() != null ? LocalDateTime.parse(request.getDeadline()) : null
            );
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/{taskId}/solution/approve")
    public ResponseEntity<?> approveSolution(
            @PathVariable Long taskId,
            @RequestBody ApproveSolutionRequest request) {
        try {
            Task task = taskService.approveSolution(taskId, request.getTeacherId());
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @Data
    static class CreateTaskRequest {
        private Long groupId;
        private Long createdBy;
        private String title;
        private String description;
        private String deadline;
    }

    @Data
    static class UpdateStatusRequest {
        private String status;
    }

    @Data
    static class AddSolutionRequest {
        private Long userId;
        private String solutionText;
        private String solutionUrl;
    }

    @Data
    static class UpdateTaskRequest {
        private Long userId;
        private String title;
        private String description;
        private String deadline;
    }

    @Data
    static class ApproveSolutionRequest {
        private Long teacherId;
    }
}

