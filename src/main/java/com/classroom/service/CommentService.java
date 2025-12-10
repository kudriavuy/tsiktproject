package com.classroom.service;

import com.classroom.model.Comment;
import com.classroom.model.Membership;
import com.classroom.model.Task;
import com.classroom.repository.CommentRepository;
import com.classroom.repository.MembershipRepository;
import com.classroom.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final MembershipRepository membershipRepository;

    @Transactional
    public Comment createComment(Long taskId, Long userId, String text) {
        // Check if task exists
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Check if user is a member of the group
        Optional<Membership> membership = membershipRepository.findByUserIdAndGroupId(userId, task.getGroupId());
        if (membership.isEmpty()) {
            throw new RuntimeException("User is not a member of the group");
        }

        Comment comment = new Comment();
        comment.setTaskId(taskId);
        comment.setUserId(userId);
        comment.setText(text);

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByTask(Long taskId) {
        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Check if user is comment author or teacher
        Task task = taskRepository.findById(comment.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Optional<Membership> membership = membershipRepository.findByUserIdAndGroupId(userId, task.getGroupId());
        boolean isTeacher = membership.isPresent() && membership.get().getRole() == Membership.Role.TEACHER;
        boolean isAuthor = comment.getUserId().equals(userId);

        if (!isTeacher && !isAuthor) {
            throw new RuntimeException("Can only delete own comments");
        }

        commentRepository.deleteById(commentId);
    }
}

