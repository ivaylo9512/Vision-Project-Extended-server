package com.vision.project.services;

import com.vision.project.models.Specs.UserSpec;
import com.vision.project.models.User;
import com.vision.project.repositories.base.UserRepository;
import com.vision.project.services.base.UserService;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public User login(User user) throws InvalidCredentialsException {
        String username = user.getUsername();
        String password = user.getPassword();

        User foundUser = userRepository.findByUserName(username);

        if (foundUser == null) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }

        if (!BCrypt.checkpw(password,foundUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }

        return foundUser;
    }

}
