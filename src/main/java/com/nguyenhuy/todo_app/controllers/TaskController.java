package com.nguyenhuy.todo_app.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyenhuy.todo_app.dtos.TaskDTO;
import com.nguyenhuy.todo_app.models.Task;
import com.nguyenhuy.todo_app.services.ITaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final ITaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id){
        try {
            TaskDTO taskDTO = taskService.getTaskById(id);
            return ResponseEntity.ok(taskDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllTasksByUserId(@PathVariable Long userId) {
        try {
            List<TaskDTO> tasks = taskService.getAllTasksByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }   
    }

    @GetMapping("/task-list/{taskListId}")
    public ResponseEntity<?> getAllTasksByTaskListId(@PathVariable Long taskListId) {
        try {
            List<TaskDTO> tasks = taskService.getAllTasksByTaskListId(taskListId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/uncompleted")
    public ResponseEntity<?> getAllUncompletedTasksByUserId(@PathVariable Long userId) {
        try {
            List<TaskDTO> tasks = taskService.getAllTasksUncompledtedByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/task-list/{taskListId}/uncompleted")
    public ResponseEntity<?> getAllUncompletedTasksByTaskListId(@PathVariable Long taskListId) {
        try {
            List<TaskDTO> tasks = taskService.getAllTasksUncompletedByTaskListId(taskListId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskDTO taskDTO, BindingResult resutls) {
        try {
            if(resutls.hasErrors()){
                List<String> errorMesseages = resutls.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
                return ResponseEntity.badRequest().body(errorMesseages);
            }   
            Task createdTask = taskService.createTask(taskDTO);
            TaskDTO createdTaskDTO = new TaskDTO();
            createdTaskDTO.setId(createdTask.getId());
            createdTaskDTO.setTitle(createdTask.getTitle());
            createdTaskDTO.setDescription(createdTask.getDescription());
            createdTaskDTO.setCreatedAt(createdTask.getCreatedAt());
            createdTaskDTO.setDueDate(createdTask.getDueDate());
            createdTaskDTO.setCompleted(createdTask.isCompleted());
            createdTaskDTO.setTaskListId(createdTask.getTaskList().getId());
            createdTaskDTO.setUserId(createdTask.getUser().getId());
            return ResponseEntity.ok(createdTaskDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable("id") Long id, @Valid @RequestBody TaskDTO taskDTO, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Task updatedTask = taskService.updateTask(id, taskDTO);
            TaskDTO updatedTaskDTO = new TaskDTO();
            updatedTaskDTO.setId(updatedTask.getId());
            updatedTaskDTO.setTitle(updatedTask.getTitle());
            updatedTaskDTO.setDescription(updatedTask.getDescription());
            updatedTaskDTO.setDueDate(updatedTask.getDueDate());
            updatedTaskDTO.setCompleted(updatedTask.isCompleted());
            updatedTaskDTO.setUserId(updatedTask.getUser().getId());
            updatedTaskDTO.setTaskListId(updatedTask.getTaskList().getId());
            return ResponseEntity.ok(updatedTaskDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable("id") Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
