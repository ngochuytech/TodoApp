package com.nguyenhuy.todo_app.services;

import java.util.List;

import com.nguyenhuy.todo_app.dtos.TaskDTO;
import com.nguyenhuy.todo_app.models.Task;

public interface ITaskService {
    Task createTask(TaskDTO taskDTO) throws Exception;

    TaskDTO getTaskById(Long taskId) throws Exception;

    Task updateTask(Long taskId, TaskDTO taskDTO) throws Exception;

    void deleteTask(Long taskId) throws Exception;

    List<TaskDTO> getAllTasksByUserId(Long userId) throws Exception;

    List<TaskDTO> getAllTasksByTaskListId(Long taskListId) throws Exception;

    List<TaskDTO> getAllTasksUncompledtedByUserId(Long userId) throws Exception;

    List<TaskDTO> getAllTasksUncompletedByTaskListId(Long taskListId) throws Exception;
}
