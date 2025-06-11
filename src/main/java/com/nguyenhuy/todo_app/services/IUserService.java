package com.nguyenhuy.todo_app.services;

import com.nguyenhuy.todo_app.dtos.UserDTO;
import com.nguyenhuy.todo_app.models.User;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    
    String login(String email, String password) throws Exception;
}
