package com.vision.project.services;

import com.vision.project.exceptions.PasswordsMissMatchException;
import com.vision.project.exceptions.UserNotFoundException;
import com.vision.project.exceptions.UsernameExistsException;
import com.vision.project.models.Specs.UserSpec;
import com.vision.project.models.UserModel;
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
    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserModel findById(int id, UserModel loggedUserModel) {
        UserModel userModel = userRepository.findById(id);
        if (userModel == null) {
            throw new UserNotFoundException("UserModel doesn't exist.");
        }
        return userModel;
    }
    @Override
    public UserModel register(UserSpec userSpec, String role) {
        UserModel userModel = userRepository.findByUsername(userSpec.getUsername());

        if (userModel != null) {
            throw new UsernameExistsException("Username is already taken.");
        }

        if (!userSpec.getPassword().equals(userSpec.getRepeatPassword())) {
            throw new PasswordsMissMatchException("Passwords must match.");
        }

        userModel = new UserModel(userSpec, role);
        userModel.setPassword(BCrypt.hashpw(userModel.getPassword(),BCrypt.gensalt(4)));
        return userRepository.save(userModel);
    }
    @Override
    public UserModel login(UserModel userModel) throws InvalidCredentialsException {
        String username = userModel.getUsername();
        String password = userModel.getPassword();

        UserModel foundUserModel = userRepository.findByUsername(username);

        if (foundUserModel == null) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }

        if (!BCrypt.checkpw(password, foundUserModel.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }

        return foundUserModel;
    }

}
