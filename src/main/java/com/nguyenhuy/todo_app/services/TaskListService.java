package com.nguyenhuy.todo_app.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nguyenhuy.todo_app.dtos.TaskListDTO;
import com.nguyenhuy.todo_app.models.TaskList;
import com.nguyenhuy.todo_app.models.User;
import com.nguyenhuy.todo_app.repositories.TaskListRepository;
import com.nguyenhuy.todo_app.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskListService implements ITaskListService {

    private final TaskListRepository taskListRepository;
    
    private final UserRepository userRepository;


    @Override
    public TaskList createTaskList(TaskListDTO taskListDTO) throws Exception {
        if(taskListDTO.getUserId() == null) {
            throw new Exception("User ID cannot be null");
        }

        if(taskListDTO.getTitle() == null || taskListDTO.getTitle().isEmpty()) {
            taskListDTO.setTitle("Default Task List");
        }
        
        User user = userRepository.findById(taskListDTO.getUserId())
                .orElseThrow(() -> new Exception("User not found with ID: " + taskListDTO.getUserId()));
        TaskList newTaskList = TaskList.builder()
                .title(taskListDTO.getTitle())
                .user(user)
                .build();
        return taskListRepository.save(newTaskList);
    }

    @Override
    public List<TaskList> getTaskListsByUserId(Long userId) throws Exception {
        return taskListRepository.findByUserId(userId);
    }

    @Override
    public TaskList getTaskListById(Long taskListId) throws Exception {
        return taskListRepository.findById(taskListId).orElse(null);
    }

    @Override
    public TaskList updateTaskList(Long taskListId, TaskListDTO taskListDTO) throws Exception {
        TaskList existingTaskList = taskListRepository.findById(taskListId)
            .orElseThrow(() -> new Exception("Task List not found with ID: " + taskListId));
        User user = userRepository.findById(taskListDTO.getUserId())
            .orElseThrow(() -> new Exception("User not found with ID: " + taskListDTO.getUserId()));

        if(taskListDTO.getTitle() != null && !taskListDTO.getTitle().isEmpty()) {
            existingTaskList.setTitle("Default Task List");
        }
        existingTaskList.setUser(user);
        return taskListRepository.save(existingTaskList);
    }

    @Override
    public void deleteTaskList(Long taskListId) throws Exception {
        TaskList existingTaskList = taskListRepository.findById(taskListId)
            .orElse(null);

        if (existingTaskList != null) {
            taskListRepository.delete(existingTaskList);
        }
    }
    
}
