package com.classroom.repository;

import com.classroom.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByGroupId(Long groupId);
    List<Task> findByGroupIdAndCreatedBy(Long groupId, Long createdBy);
}

