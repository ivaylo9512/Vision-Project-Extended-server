package com.vision.project.services.base;

import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.NewPasswordSpec;
import com.vision.project.models.specs.UserSpec;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserModel findById(long id);

    UserModel getById(long id);

    UserModel create(UserModel user);

    UserModel save(UserModel userModel);


    UserModel changePassword(NewPasswordSpec passwordSpec, UserDetails loggedUser);

    UserModel changeUserInfo(UserSpec userSpec, UserModel loggedUser);

    void setEnabled(boolean state, long id);

    void delete(long id, UserDetails loggedUser);

    void delete(UserModel user);
}
