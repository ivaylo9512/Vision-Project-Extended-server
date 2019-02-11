package com.vision.project.repositories.base;

import com.vision.project.models.Order;
import com.vision.project.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findById(int id);

    List<User> findAll();

    User findByUsername(String username);
}
