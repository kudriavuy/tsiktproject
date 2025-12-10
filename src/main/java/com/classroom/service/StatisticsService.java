package com.classroom.service;

import com.classroom.model.Membership;
import com.classroom.model.Task;
import com.classroom.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final TaskRepository taskRepository;
    private final MembershipRepository membershipRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    // Task statistics for group
    public Map<String, Object> getGroupTaskStatistics(Long groupId) {
        List<Task> tasks = taskRepository.findByGroupId(groupId);

        long totalTasks = tasks.size();
        long openTasks = tasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.OPEN).count();
        long inProgressTasks = tasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.IN_PROGRESS).count();
        long completedTasks = tasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED).count();

        // Statistics by dates (last 30 days)
        Map<String, Long> tasksByDate = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = tasks.stream()
                    .filter(t -> t.getCreatedAt() != null &&
                            t.getCreatedAt().toLocalDate().equals(date))
                    .count();
            tasksByDate.put(date.toString(), count);
        }

        // Task completion statistics by dates
        Map<String, Long> completedByDate = new LinkedHashMap<>();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = tasks.stream()
                    .filter(t -> t.getSolutionUploadedAt() != null &&
                            t.getSolutionUploadedAt().toLocalDate().equals(date))
                    .count();
            completedByDate.put(date.toString(), count);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalTasks", totalTasks);
        result.put("openTasks", openTasks);
        result.put("inProgressTasks", inProgressTasks);
        result.put("completedTasks", completedTasks);
        result.put("tasksByDate", tasksByDate);
        result.put("completedByDate", completedByDate);

        return result;
    }

    // Participant engagement statistics
    public Map<String, Object> getGroupEngagementStatistics(Long groupId) {
        List<Membership> members = membershipRepository.findByGroupId(groupId);
        List<Task> tasks = taskRepository.findByGroupId(groupId);

        Map<String, Object> engagement = new HashMap<>();

        // Total number of members
        long totalMembers = members.size();
        long teachers = members.stream().filter(m -> m.getRole() == Membership.Role.TEACHER).count();
        long students = members.stream().filter(m -> m.getRole() == Membership.Role.STUDENT).count();

        // Statistics by participants (who completed how many tasks)
        Map<String, Long> memberTaskCount = new HashMap<>();
        for (Membership member : members) {
            long taskCount = tasks.stream()
                    .filter(t -> t.getSolutionUploadedBy() != null &&
                            t.getSolutionUploadedBy().equals(member.getUserId()))
                    .count();
            userRepository.findById(member.getUserId()).ifPresent(user -> {
                memberTaskCount.put(user.getName(), taskCount);
            });
        }

        // Percentage of active members (those who completed at least one task)
        long activeMembers = tasks.stream()
                .filter(t -> t.getSolutionUploadedBy() != null)
                .map(Task::getSolutionUploadedBy)
                .distinct()
                .count();

        double engagementRate = totalMembers > 0 ? (double) activeMembers / totalMembers * 100 : 0;

        engagement.put("totalMembers", totalMembers);
        engagement.put("teachers", teachers);
        engagement.put("students", students);
        engagement.put("activeMembers", activeMembers);
        engagement.put("engagementRate", Math.round(engagementRate * 100.0) / 100.0);
        engagement.put("memberTaskCount", memberTaskCount);

        return engagement;
    }

    // User activity statistics
    public Map<String, Object> getUserActivityStatistics(Long userId) {
        List<com.classroom.model.ActivityLog> activities = activityLogRepository.findByUserIdOrderByTimestampDesc(userId);

        // Activity by days (last 30 days)
        Map<String, Long> activityByDate = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = activities.stream()
                    .filter(a -> a.getTimestamp() != null &&
                            a.getTimestamp().toLocalDate().equals(date))
                    .count();
            activityByDate.put(date.toString(), count);
        }

        // Activity by action types
        Map<String, Long> activityByType = activities.stream()
                .collect(Collectors.groupingBy(
                        com.classroom.model.ActivityLog::getAction,
                        Collectors.counting()
                ));

        Map<String, Object> result = new HashMap<>();
        result.put("totalActivities", activities.size());
        result.put("activityByDate", activityByDate);
        result.put("activityByType", activityByType);
        result.put("recentActivities", activities.stream()
                .limit(10)
                .map(a -> Map.of(
                        "action", a.getAction(),
                        "timestamp", a.getTimestamp().toString(),
                        "details", a.getDetails() != null ? a.getDetails() : ""
                ))
                .collect(Collectors.toList()));

        return result;
    }

    // Overall statistics for group
    public Map<String, Object> getGroupOverallStatistics(Long groupId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("taskStatistics", getGroupTaskStatistics(groupId));
        stats.put("engagementStatistics", getGroupEngagementStatistics(groupId));
        return stats;
    }
}

