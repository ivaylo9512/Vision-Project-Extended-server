package com.vision.project.repositories.base;

import com.vision.project.models.Dish;
import com.vision.project.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface DishRepository extends CrudRepository<Dish, Integer> {
}
