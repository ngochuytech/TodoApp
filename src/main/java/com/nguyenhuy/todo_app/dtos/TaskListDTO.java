package com.nguyenhuy.todo_app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskListDTO {
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("title")    
    private String title;

    @JsonProperty("user_id")
    private Long userId;
}
