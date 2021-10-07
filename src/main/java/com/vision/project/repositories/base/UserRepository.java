package com.vision.project.repositories.base;

import com.vision.project.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Integer> {
    List<UserModel> findAll();

    Optional<UserModel> findByUsername(String username);

    UserModel findByUsernameOrEmail(String username, String email);
}
