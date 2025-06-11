package com.nguyenhuy.todo_app.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nguyenhuy.todo_app.dtos.TaskDTO;
import com.nguyenhuy.todo_app.models.Task;
import com.nguyenhuy.todo_app.models.TaskList;
import com.nguyenhuy.todo_app.models.User;
import com.nguyenhuy.todo_app.repositories.TaskListRepository;
import com.nguyenhuy.todo_app.repositories.TaskRepository;
import com.nguyenhuy.todo_app.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {

    private final TaskRepository taskRepository;

    private final TaskListRepository taskListRepository;

    private final UserRepository userRepository;
    
    @Override
    public Task createTask(TaskDTO taskDTO) throws Exception {
        User existingUser = userRepository.findById(taskDTO.getUserId())
            .orElseThrow(() -> new Exception("User not found with ID: " + taskDTO.getUserId()));
        TaskList existingTaskList = taskListRepository.findById(taskDTO.getTaskListId())
            .orElseThrow(() -> new Exception("Task List not found with ID: " + taskDTO.getTaskListId()));
        
        Task task = Task.builder()
            .title(taskDTO.getTitle())
            .description(taskDTO.getDescription())
            .dueDate(taskDTO.getDueDate())
            .completed(taskDTO.isCompleted())
            .taskList(existingTaskList)
            .user(existingUser)
            .build();

        return taskRepository.save(task);
    }

    @Override
    public Task getTaskById(Long taskId) throws Exception {
        return taskRepository.findById(taskId).orElse(null);
    }

    @Override
    public Task updateTask(Long taskId, TaskDTO taskDTO) throws Exception {
        User existingUser = userRepository.findById(taskDTO.getUserId())
            .orElseThrow(() -> new Exception("User not found with ID: " + taskDTO.getUserId()));
        TaskList existingTaskList = taskListRepository.findById(taskDTO.getTaskListId())
            .orElseThrow(() -> new Exception("Task List not found with ID: " + taskDTO.getTaskListId()));
        
        Task existingTask = taskRepository.findById(taskId)
            .orElseThrow(() -> new Exception("Task not found with ID: " + taskId));
        
        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setDueDate(taskDTO.getDueDate());
        existingTask.setCompleted(taskDTO.isCompleted());
        existingTask.setTaskList(existingTaskList);
        existingTask.setUser(existingUser);

        return taskRepository.save(existingTask);
    }

    @Override
    public void deleteTask(Long taskId) throws Exception {
        taskRepository.deleteById(taskId);
    }

    @Override
    public List<Task> getAllTasksByUserId(Long userId) throws Exception {
        List<Task> tasks = taskRepository.findTasksByUserId(userId);
        if (tasks == null || tasks.isEmpty()) {
            throw new Exception("No tasks found for user with ID: " + userId);
        }
        return tasks;
    }

    @Override
    public List<Task> getAllTasksByTaskListId(Long taskListId) throws Exception {
        List<Task> tasks = taskRepository.findTasksByTaskListId(taskListId);
        if (tasks == null || tasks.isEmpty()) {
            throw new Exception("No tasks found for task list with ID: " + taskListId);
        }
        return tasks;
    }
    
}
