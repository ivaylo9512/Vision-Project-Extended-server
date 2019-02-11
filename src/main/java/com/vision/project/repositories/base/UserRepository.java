package com.vision.project.repositories.base;

import com.vision.project.models.UserModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserModel, Integer> {

    UserModel findById(int id);

    List<UserModel> findAll();

    UserModel findByUsername(String username);
}
