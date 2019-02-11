package com.vision.project.services;

import com.vision.project.exceptions.PasswordsMissMatchException;
import com.vision.project.exceptions.UserNotFoundException;
import com.vision.project.exceptions.UsernameExistsException;
import com.vision.project.models.Specs.UserSpec;
import com.vision.project.models.User;
import com.vision.project.repositories.base.UserRepository;
import com.vision.project.services.base.UserService;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(int id, User loggedUser) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException("User doesn't exist.");
        }
        return user;
    }
    @Override
    public User register(UserSpec userSpec, String role) {
        User user = userRepository.findByUsername(userSpec.getUsername());

        if (user != null) {
            throw new UsernameExistsException("Username is already taken.");
        }

        if (!userSpec.getPassword().equals(userSpec.getRepeatPassword())) {
            throw new PasswordsMissMatchException("Passwords must match.");
        }

        user = new User(userSpec, role);
        user.setPassword(BCrypt.hashpw(user.getPassword(),BCrypt.gensalt(4)));
        return userRepository.save(user);
    }
    @Override
    public User login(User user) throws InvalidCredentialsException {
        String username = user.getUsername();
        String password = user.getPassword();

        User foundUser = userRepository.findByUsername(username);

        if (foundUser == null) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }

        if (!BCrypt.checkpw(password,foundUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }

        return foundUser;
    }

}
