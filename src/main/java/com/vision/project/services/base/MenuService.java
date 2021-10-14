package com.vision.project.services.base;

import com.vision.project.models.Menu;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.MenuUpdateSpec;

public interface MenuService {
    Menu findById(long id, UserModel loggedUser);

    void delete(long id, UserModel loggedUser);

    Menu create(Menu menu, UserModel loggedUser);

    Menu update(MenuUpdateSpec menuUpdateSpec, UserModel loggedUser);
}
