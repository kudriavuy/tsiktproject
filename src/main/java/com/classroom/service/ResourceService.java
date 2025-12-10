package com.classroom.service;

import com.classroom.model.Resource;
import com.classroom.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public Resource createResource(Long groupId, Long uploadedBy, String title,
                                   Resource.ResourceType type, String pathOrUrl) {
        return createResource(groupId, null, uploadedBy, title, type, pathOrUrl);
    }

    public Resource createResource(Long groupId, Long taskId, Long uploadedBy, String title,
                                   Resource.ResourceType type, String pathOrUrl) {
        Resource resource = new Resource();
        resource.setGroupId(groupId);
        resource.setTaskId(taskId);
        resource.setUploadedBy(uploadedBy);
        resource.setTitle(title);
        resource.setType(type);
        resource.setPathOrUrl(pathOrUrl);
        return resourceRepository.save(resource);
    }

    public List<Resource> getResourcesByGroup(Long groupId) {
        return resourceRepository.findByGroupId(groupId);
    }

    public List<Resource> getResourcesByTask(Long taskId) {
        return resourceRepository.findByTaskId(taskId);
    }

    public void deleteResource(Long resourceId) {
        resourceRepository.deleteById(resourceId);
    }
}

