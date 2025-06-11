package com.nguyenhuy.todo_app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyenhuy.todo_app.models.TaskList;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    List<TaskList> findByUserId(Long userId);
}
