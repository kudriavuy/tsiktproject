package com.classroom.repository;

import com.classroom.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByGroupId(Long groupId);
    List<Resource> findByTaskId(Long taskId);
    List<Resource> findByGroupIdAndTaskIdIsNull(Long groupId);
}

