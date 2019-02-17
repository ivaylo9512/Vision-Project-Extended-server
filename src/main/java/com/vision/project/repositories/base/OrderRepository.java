package com.vision.project.repositories.base;

import com.vision.project.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    Order findById(int id);

    List<Order> findAll();

    List<Order> findByReadyFalse();

    @Query(value="from Order where created >= :date")
    List<Order> findMoreRecent(@Param("date") Date date);

    Order findTop1ByOrderByCreatedDescUpdatedDesc();
}
