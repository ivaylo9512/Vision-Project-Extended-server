package com.vision.project.services.base;

import com.vision.project.models.Menu;

import java.util.List;

public interface MenuService {
    List<Menu> findAll();

    void delete(int id);

    Menu create(Menu menu);

    List<Menu> findByRestaurant(int restaurant);

}
