package com.nguyenhuy.todo_app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("fullname")
    @NotBlank(message = "Full name is required")
    private String fullName;

    @JsonProperty("password")
    @NotBlank(message = "Password is required")
    private String password;

    @JsonProperty("retype_password")   
    private String retypePassword;
    
    @JsonProperty("email")
    @NotBlank(message = "Email is required")
    private String email;
}
