package com.vision.project.services.base;

import com.vision.project.models.UserModel;
import com.vision.project.models.specs.RegisterSpec;
import java.util.List;

public interface UserService {
    List<UserModel> findAll();

    UserModel findById(int id);

    UserModel create(UserModel user);

    UserModel save(UserModel userModel);

    UserModel changeUserInfo(int loggedUser, RegisterSpec registerSpec);
}
