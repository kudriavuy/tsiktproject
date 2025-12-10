package com.classroom.service;

import com.classroom.model.Membership;
import com.classroom.model.Task;
import com.classroom.repository.MembershipRepository;
import com.classroom.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final MembershipRepository membershipRepository;

    public Task createTask(Long groupId, Long createdBy, String title, String description, LocalDateTime deadline) {
        // Check if user is TEACHER in the group
        Optional<Membership> membership = membershipRepository.findByUserIdAndGroupId(createdBy, groupId);
        if (membership.isEmpty() || membership.get().getRole() != Membership.Role.TEACHER) {
            throw new RuntimeException("Only teacher can create tasks");
        }

        Task task = new Task();
        task.setGroupId(groupId);
        task.setCreatedBy(createdBy);
        task.setTitle(title);
        task.setDescription(description);
        task.setDeadline(deadline);
        task.setStatus(Task.TaskStatus.OPEN);
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public List<Task> getTasksByGroup(Long groupId) {
        return taskRepository.findByGroupId(groupId);
    }

    public Task updateTaskStatus(Long taskId, Task.TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    @Transactional
    public Task addSolution(Long taskId, Long userId, String solutionText, String solutionUrl) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Check if user is a member of the group
        Optional<Membership> membership = membershipRepository.findByUserIdAndGroupId(userId, task.getGroupId());
        if (membership.isEmpty()) {
            throw new RuntimeException("User is not a member of the group");
        }

        task.setSolutionText(solutionText);
        task.setSolutionUrl(solutionUrl);
        task.setSolutionUploadedBy(userId);
        task.setSolutionUploadedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateSolution(Long taskId, Long userId, String solutionText, String solutionUrl) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Check if user is the solution author or teacher
        Optional<Membership> membership = membershipRepository.findByUserIdAndGroupId(userId, task.getGroupId());
        if (membership.isEmpty()) {
            throw new RuntimeException("User is not a member of the group");
        }

        // Can only update own solution (if not teacher)
        if (membership.get().getRole() != Membership.Role.TEACHER) {
            if (task.getSolutionUploadedBy() == null || !task.getSolutionUploadedBy().equals(userId)) {
                throw new RuntimeException("Can only update own solution");
            }
        }

        task.setSolutionText(solutionText);
        task.setSolutionUrl(solutionUrl);
        task.setSolutionUploadedBy(userId);
        task.setSolutionUploadedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }

    public boolean isUserTeacher(Long groupId, Long userId) {
        Optional<Membership> membership = membershipRepository.findByUserIdAndGroupId(userId, groupId);
        return membership.isPresent() && membership.get().getRole() == Membership.Role.TEACHER;
    }

    @Transactional
    public Task approveSolution(Long taskId, Long teacherId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Check if user is TEACHER
        Optional<Membership> membership = membershipRepository.findByUserIdAndGroupId(teacherId, task.getGroupId());
        if (membership.isEmpty() || membership.get().getRole() != Membership.Role.TEACHER) {
            throw new RuntimeException("Only administrator can approve solutions");
        }

        // Check if solution exists
        if (task.getSolutionText() == null && task.getSolutionUrl() == null) {
            throw new RuntimeException("Solution not found");
        }

        // Change status to COMPLETED
        task.setStatus(Task.TaskStatus.COMPLETED);

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long taskId, Long userId, String title, String description, LocalDateTime deadline) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Check if user is teacher of the group or task creator
        Optional<Membership> membership = membershipRepository.findByUserIdAndGroupId(userId, task.getGroupId());
        boolean isTeacher = membership.isPresent() && membership.get().getRole() == Membership.Role.TEACHER;
        boolean isCreator = task.getCreatedBy().equals(userId);

        if (!isTeacher && !isCreator) {
            throw new RuntimeException("Only teacher or task creator can edit task");
        }

        if (title != null && !title.trim().isEmpty()) {
            task.setTitle(title.trim());
        }
        if (description != null) {
            task.setDescription(description.trim());
        }
        if (deadline != null) {
            task.setDeadline(deadline);
        }

        return taskRepository.save(task);
    }
}

