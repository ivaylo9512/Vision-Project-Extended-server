package com.vision.project.services.base;

import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.NewPasswordSpec;
import com.vision.project.models.specs.UserSpec;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<UserModel> findAll();

    UserModel findById(int id);

    UserModel getById(int id);

    UserModel create(UserModel user);

    UserModel save(UserModel userModel);


    UserModel changePassword(NewPasswordSpec passwordSpec, UserDetails loggedUser);

    UserModel changeUserInfo(UserSpec userSpec, UserModel loggedUser);
}
