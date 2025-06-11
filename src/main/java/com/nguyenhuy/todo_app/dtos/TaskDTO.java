package com.nguyenhuy.todo_app.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    @Max(value = 200, message = "Title must not exceed 200 characters")
    @NotBlank(message = "Task title cannot be blank")
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("due_date")
    private LocalDateTime dueDate;

    @NotBlank(message = "Task status cannot be blank")
    @JsonProperty("completed")
    private boolean completed;

    @JsonProperty("task_list_id")
    private Long taskListId;

    @JsonProperty("user_id")
    private Long userId;
}
