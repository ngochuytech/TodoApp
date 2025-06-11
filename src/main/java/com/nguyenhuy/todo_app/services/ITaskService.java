package com.nguyenhuy.todo_app.services;

import java.util.List;

import com.nguyenhuy.todo_app.dtos.TaskDTO;
import com.nguyenhuy.todo_app.models.Task;

public interface ITaskService {
    Task createTask(TaskDTO taskDTO) throws Exception;

    Task getTaskById(Long taskId) throws Exception;

    Task updateTask(Long taskId, TaskDTO taskDTO) throws Exception;

    void deleteTask(Long taskId) throws Exception;

    List<Task> getAllTasksByUserId(Long userId) throws Exception;

    List<Task> getAllTasksByTaskListId(Long taskListId) throws Exception;
}
