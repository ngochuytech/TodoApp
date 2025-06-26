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
    public TaskDTO getTaskById(Long taskId) throws Exception {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new Exception("Task not found with ID: " + taskId));
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setCreatedAt(task.getCreatedAt());
        taskDTO.setDueDate(task.getDueDate());
        taskDTO.setCompleted(task.isCompleted());
        taskDTO.setUserId(task.getUser().getId());
        taskDTO.setTaskListId(task.getTaskList().getId());
        return taskDTO;
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
        System.out.println("Updating task: " + existingTask);
        return taskRepository.save(existingTask);
    }

    @Override
    public void deleteTask(Long taskId) throws Exception {
        taskRepository.deleteById(taskId);
    }

    @Override
    public List<TaskDTO> getAllTasksByUserId(Long userId) throws Exception {
        List<Task> tasks = taskRepository.findTasksByUserId(userId);
        if (tasks == null || tasks.isEmpty()) {
            throw new Exception("No tasks found for user with ID: " + userId);
        }
        List<TaskDTO> taskDTOs = tasks.stream().map(task -> new TaskDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getCreatedAt(),
            task.getDueDate(),
            task.isCompleted(),
            task.getUser().getId(),
            task.getTaskList().getId()
        )).toList();
        return taskDTOs;
    }

    @Override
    public List<TaskDTO> getAllTasksByTaskListId(Long taskListId) throws Exception {
        List<Task> tasks = taskRepository.findTasksByTaskListId(taskListId);
        if (tasks == null) {
            throw new Exception("No tasks found for task list with ID: " + taskListId);
        } else if(tasks.isEmpty()) {
            return null;
        }
        List<TaskDTO> taskDTOs = tasks.stream().map(task -> new TaskDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getCreatedAt(),
            task.getDueDate(),
            task.isCompleted(),
            task.getUser().getId(),
            task.getTaskList().getId()
        )).toList();
        return taskDTOs;
    }

    @Override
    public List<TaskDTO> getAllTasksUncompledtedByUserId(Long userId) throws Exception {
        if(userId == null) {
            throw new Exception("User ID cannot be null");
        } else if( userRepository.findById(userId).isEmpty()) {
            throw new Exception("User not found with ID: " + userId);
        }
        List<Task> tasks = taskRepository.findTasksByUserIdAndCompletedFalse(userId);
        return tasks.stream().map(task -> new TaskDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getCreatedAt(),
            task.getDueDate(),
            task.isCompleted(),
            task.getUser().getId(),
            task.getTaskList().getId()
        )).toList();
    }

    @Override
    public List<TaskDTO> getAllTasksUncompletedByTaskListId(Long taskListId) throws Exception {
        if(taskListId == null) {
            throw new Exception("Task List ID cannot be null");
        } else if(taskListRepository.findById(taskListId).isEmpty()){
            throw new Exception("Task List not found with ID: " + taskListId);
        }
        List<Task> tasks = taskRepository.findTaskByTaskListIdAndCompletedFalse(taskListId);
        return tasks.stream().map(task -> new TaskDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getCreatedAt(),
            task.getDueDate(),
            task.isCompleted(),
            task.getUser().getId(),
            task.getTaskList().getId()
        )).toList();
    }
    
}
