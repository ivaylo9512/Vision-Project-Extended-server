package com.vision.project.services;

import com.vision.project.exceptions.UnauthorizedException;
import com.vision.project.models.Menu;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.MenuUpdateSpec;
import com.vision.project.repositories.base.MenuRepository;
import com.vision.project.services.base.MenuService;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;

@Service
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;

    public MenuServiceImpl(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    public void delete(long id, UserModel loggedUser) {
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Menu not found."));

        if(loggedUser.getRestaurant().getId() != menu.getRestaurant().getId() && !loggedUser.getRole().equals("ROLE_ADMIN")){
            throw new UnauthorizedException("Unauthorized.");
        }

        menuRepository.delete(menu);
    }

    @Override
    public Menu update(MenuUpdateSpec menuUpdateSpec, UserModel loggedUser) {
        if(loggedUser.getRestaurant().getId() != menuUpdateSpec.getRestaurantId() && !loggedUser.getRole().equals("ROLE_ADMIN")){
            throw new UnauthorizedException("Unauthorized.");
        }

        Menu menu = menuRepository.findById(menuUpdateSpec.getId()).orElseThrow(() ->
                new EntityNotFoundException("Menu not found."));
        menu.setName(menuUpdateSpec.getName());

        return menuRepository.save(menu);
    }

    @Override
    public Menu create(Menu menu, UserModel loggedUser) {
        if(loggedUser.getRestaurant().getId() != menu.getRestaurant().getId() && !loggedUser.getRole().equals("ROLE_ADMIN")){
            throw new UnauthorizedException("Unauthorized.");
        }

        return menuRepository.save(menu);
    }
}
