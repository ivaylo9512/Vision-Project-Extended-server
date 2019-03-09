package com.vision.project.services.base;

import com.vision.project.models.UserModel;
import com.vision.project.models.specs.UserSpec;
import org.apache.http.auth.InvalidCredentialsException;

import java.util.List;

public interface UserService {

    List<UserModel> findAll();

    UserModel findById(int id, UserModel loggedUserModel);

    UserModel register(UserSpec userSpec, String role);
}
