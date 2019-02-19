package com.vision.project.services.base;

import com.vision.project.models.specs.UserSpec;
import com.vision.project.models.User;
import org.apache.http.auth.InvalidCredentialsException;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(int id, User loggedUser);

    User register(UserSpec userSpec, String role);

    User login(User user) throws InvalidCredentialsException;
}
