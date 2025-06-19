package com.nguyenhuy.todo_app.services;

import java.util.List;

import com.nguyenhuy.todo_app.dtos.TaskListDTO;
import com.nguyenhuy.todo_app.models.TaskList;

public interface ITaskListService {
    TaskList createTaskList(TaskListDTO taskListDTO) throws Exception;

    List<TaskListDTO> getTaskListsByUserId(Long userId) throws Exception;

    TaskList getTaskListById(Long taskListId) throws Exception;

    TaskList updateTaskList(Long taskListId, TaskListDTO taskListDTO) throws Exception;

    void deleteTaskList(Long taskListId) throws Exception;

}
