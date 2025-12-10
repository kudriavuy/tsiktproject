package com.classroom.controller;

import com.classroom.model.Comment;
import com.classroom.repository.TaskRepository;
import com.classroom.service.CommentService;
import com.classroom.service.NotificationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommentController {
    private final CommentService commentService;
    private final NotificationService notificationService;
    private final TaskRepository taskRepository;

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CreateCommentRequest request) {
        try {
            Comment comment = commentService.createComment(
                    request.getTaskId(),
                    request.getUserId(),
                    request.getText()
            );
            // Send notification
            var task = taskRepository.findById(request.getTaskId());
            if (task.isPresent()) {
                notificationService.sendNotification(
                        task.get().getGroupId(),
                        "COMMENT_ADDED",
                        "New comment on task",
                        request.getUserId()
                );
            }
            return ResponseEntity.ok(comment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Comment>> getCommentsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long userId) {
        try {
            commentService.deleteComment(commentId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @Data
    static class CreateCommentRequest {
        private Long taskId;
        private Long userId;
        private String text;
    }
}

