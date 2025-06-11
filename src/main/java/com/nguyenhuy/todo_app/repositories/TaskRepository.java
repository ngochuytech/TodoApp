package com.nguyenhuy.todo_app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyenhuy.todo_app.models.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{
    List<Task> findTasksByUserId(Long userId);

    List<Task> findTasksByTaskListId(Long taskListId);
}
