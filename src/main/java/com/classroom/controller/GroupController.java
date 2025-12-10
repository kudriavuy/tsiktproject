package com.classroom.controller;

import com.classroom.model.Group;
import com.classroom.model.Membership;
import com.classroom.service.GroupService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GroupController {
    private final GroupService groupService;
    private final com.classroom.service.NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody CreateGroupRequest request) {
        Group group = groupService.createGroup(
                request.getName(),
                request.getDescription(),
                request.getCreatedBy()
        );
        return ResponseEntity.ok(group);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroup(@PathVariable Long groupId) {
        return groupService.getGroupById(groupId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Group>> getUserGroups(@PathVariable Long userId) {
        return ResponseEntity.ok(groupService.getGroupsByUser(userId));
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<?> addMember(
            @PathVariable Long groupId,
            @RequestBody AddMemberRequest request) {
        try {
            Membership membership = groupService.addMember(
                    groupId,
                    request.getAddedByUserId(),
                    request.getUserId(),
                    Membership.Role.valueOf(request.getRole())
            );
            // Send notification
            notificationService.sendNotification(
                    groupId,
                    "MEMBER_ADDED",
                    "New member added to group",
                    request.getAddedByUserId()
            );
            return ResponseEntity.ok(membership);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<Membership>> getGroupMembers(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupMembers(groupId));
    }

    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroup(
            @PathVariable Long groupId,
            @RequestBody UpdateGroupRequest request) {
        try {
            Group group = groupService.updateGroup(
                    groupId,
                    request.getUserId(),
                    request.getName(),
                    request.getDescription()
            );
            return ResponseEntity.ok(group);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @Data
    static class CreateGroupRequest {
        private String name;
        private String description;
        private Long createdBy;
    }

    @Data
    static class AddMemberRequest {
        private Long addedByUserId;
        private Long userId;
        private String role;
    }

    @Data
    static class UpdateGroupRequest {
        private Long userId;
        private String name;
        private String description;
    }
}

