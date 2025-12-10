package com.classroom.controller;

import com.classroom.model.Resource;
import com.classroom.service.ResourceService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<Resource> createResource(@RequestBody CreateResourceRequest request) {
        Resource resource;
        if (request.getTaskId() != null) {
            resource = resourceService.createResource(
                    request.getGroupId(),
                    request.getTaskId(),
                    request.getUploadedBy(),
                    request.getTitle(),
                    Resource.ResourceType.valueOf(request.getType()),
                    request.getPathOrUrl()
            );
        } else {
            resource = resourceService.createResource(
                    request.getGroupId(),
                    request.getUploadedBy(),
                    request.getTitle(),
                    Resource.ResourceType.valueOf(request.getType()),
                    request.getPathOrUrl()
            );
        }
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Resource>> getGroupResources(@PathVariable Long groupId) {
        return ResponseEntity.ok(resourceService.getResourcesByGroup(groupId));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Resource>> getTaskResources(@PathVariable Long taskId) {
        return ResponseEntity.ok(resourceService.getResourcesByTask(taskId));
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long resourceId) {
        resourceService.deleteResource(resourceId);
        return ResponseEntity.ok().build();
    }

    @Data
    static class CreateResourceRequest {
        private Long groupId;
        private Long taskId;
        private Long uploadedBy;
        private String title;
        private String type;
        private String pathOrUrl;
    }
}

