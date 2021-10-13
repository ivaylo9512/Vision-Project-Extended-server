package com.vision.project.repositories.base;

import com.vision.project.models.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByRestaurant(long restaurant);
}
