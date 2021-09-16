package com.vision.project.services.base;

import com.vision.project.models.Menu;

public interface MenuService {
    void delete(int id);

    Menu create(Menu menu);
}
