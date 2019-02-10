package com.vision.project.services.base;

import com.vision.project.models.User;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(int id, User loggedUser);

    User login(User user) throws InvalidCredentialsException;
}
