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

import com.nguyenhuy.todo_app.dtos.TaskListDTO;
import com.nguyenhuy.todo_app.models.TaskList;
import com.nguyenhuy.todo_app.services.ITaskListService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/task-lists")
@RequiredArgsConstructor
public class TaskListController {
    
    private final ITaskListService taskListService;

    @GetMapping("/{idUser}")
    public ResponseEntity<List<TaskListDTO>> getTaskLists(@PathVariable("idUser") Long idUser) {
        try {
            List<TaskListDTO> taskLists = taskListService.getTaskListsByUserId(idUser);
            return ResponseEntity.ok(taskLists);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createTaskList(@Valid @RequestBody TaskListDTO taskListDTO, BindingResult result) {
        try {
            if(result.hasErrors()){
                List<String> errorMesseages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
                return ResponseEntity.badRequest().body(errorMesseages);
            }
            TaskList newTaskList = taskListService.createTaskList(taskListDTO);
            TaskListDTO newTaskListDTO = new TaskListDTO();
            newTaskListDTO.setTitle(newTaskList.getTitle());
            newTaskListDTO.setUserId(newTaskList.getUser().getId());
            return ResponseEntity.ok(newTaskListDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTaskList(@PathVariable("id") Long id, @Valid @RequestBody TaskListDTO taskListDTO, BindingResult result) {
        try {
            if(result.hasErrors()){
                List<String> errorMesseages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
                return ResponseEntity.badRequest().body(errorMesseages);
            }
            TaskList updatedTaskList = taskListService.updateTaskList(id, taskListDTO);
            return ResponseEntity.ok(updatedTaskList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskList(@PathVariable("id") Long id) {
        try {
            taskListService.deleteTaskList(id);
            return ResponseEntity.ok("Task list deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
