package com.classroom.controller;

import com.classroom.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/group/{groupId}/tasks")
    public ResponseEntity<Map<String, Object>> getGroupTaskStatistics(@PathVariable Long groupId) {
        return ResponseEntity.ok(statisticsService.getGroupTaskStatistics(groupId));
    }

    @GetMapping("/group/{groupId}/engagement")
    public ResponseEntity<Map<String, Object>> getGroupEngagementStatistics(@PathVariable Long groupId) {
        return ResponseEntity.ok(statisticsService.getGroupEngagementStatistics(groupId));
    }

    @GetMapping("/group/{groupId}/overall")
    public ResponseEntity<Map<String, Object>> getGroupOverallStatistics(@PathVariable Long groupId) {
        return ResponseEntity.ok(statisticsService.getGroupOverallStatistics(groupId));
    }

    @GetMapping("/user/{userId}/activity")
    public ResponseEntity<Map<String, Object>> getUserActivityStatistics(@PathVariable Long userId) {
        return ResponseEntity.ok(statisticsService.getUserActivityStatistics(userId));
    }
}

