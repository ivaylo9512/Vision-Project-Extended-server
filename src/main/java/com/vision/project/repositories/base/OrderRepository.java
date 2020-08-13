package com.vision.project.repositories.base;

import com.vision.project.models.Order;
import com.vision.project.models.Restaurant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query(value = "from Order order by CASE WHEN created > updated THEN created ELSE updated END desc")
    List<Order> findAll();

    @Query(value = "from Order where ready = false and restaurant = :restaurant order by CASE WHEN created > updated THEN created ELSE updated END desc")
    List<Order> findByReadyFalse(@Param("restaurant")Restaurant restaurant, Pageable pageable);

    @Query(value="from Order where CASE WHEN created > updated THEN created ELSE updated END > :date and restaurant = :restaurant order by CASE WHEN created > updated THEN created ELSE updated END desc")
    List<Order> findMoreRecent(@Param("date") LocalDateTime date, @Param("restaurant") Restaurant restaurant);

    @Query(value = "from Order where restaurant = :restaurant order by CASE WHEN created > updated THEN created ELSE updated END desc")
    List<Order> findMostRecentDate(@Param("restaurant") Restaurant restaurant, Pageable pageRequest);
}
